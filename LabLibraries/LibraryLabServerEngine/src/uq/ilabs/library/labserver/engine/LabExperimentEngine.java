/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine;

import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Level;
import uq.ilabs.library.lab.types.*;
import uq.ilabs.library.lab.utilities.Delay;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.SmtpClient;
import uq.ilabs.library.labserver.engine.drivers.DriverEquipmentGeneric;
import uq.ilabs.library.labserver.engine.drivers.DriverGeneric;
import uq.ilabs.library.labserver.engine.types.ExperimentResultInfo;
import uq.ilabs.library.labserver.engine.types.LabExecutionInfo;
import uq.ilabs.library.labserver.engine.types.LabExperimentInfo;
import uq.ilabs.library.labserver.labequipment.LabEquipmentAPI;
import uq.ilabs.library.labserver.servicebroker.ServiceBrokerAPI;

/**
 *
 * @author uqlpayne
 */
public class LabExperimentEngine implements Runnable {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabExperimentEngine.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants
     */
    private static final String STRLOG_MailMessageSubject_arg2 = "[%s LabServer] Experiment %s";
    private static final String STRLOG_MailMessageBody_arg7 = "An experiment has completed with the following details:\r\n\r\n"
            + "ServiceBroker: %s\r\n"
            + "Experiment Id: %d\r\n"
            + "Setup:         %s\r\n"
            + "Usergroup:     %s\r\n"
            + "Unit Id:       %d\r\n"
            + "StatusCode:    %s\r\n"
            + "%s";
    private static final String STRLOG_MailMessageError_arg = "Error Message: {0}\r\n";
    private static final String STRLOG_SendingEmail_arg3 = "Sending email - To: '{0}  From: '{1}'  Subject: '{2}'";
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_UnitId_arg = "UnitId: %d";
    private static final String STRLOG_LabEquipmentStatus_arg2 = "LabEquipment - Online: %s  Message: %s";
    private static final String STRLOG_LabStatus_arg2 = "Online: %s  Message: %s";
    private static final String STRLOG_ExperimentIdSbName_arg = "ExperimentId: %d SbName: %s";
    private static final String STRLOG_TimeUntilReady_arg = "TimeUntilReady: %d seconds";
    private static final String STRLOG_QueuedExperimentCancelled = "Experiment was cancelled while queued";
    private static final String STRLOG_StateChange_arg2 = "[LEE: %s->%s]";
    private static final String STRLOG_RemainingRuntime_arg = "Remaining runtime: %d seconds";
    private static final String STRLOG_ExperimentStatus_arg3 = "StatusCode: %s  EstRuntime: %.0f  RemainingRuntime: %.0f";
    //
    protected static final String STRLOG_Accepted_arg = "Accepted: %s";
    protected static final String STRLOG_Success_arg = "Success: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_LabEquipmentStatus_arg2 = "LabEquipmentStatus: %s";
    private static final String STRERR_InvalidUnitId_arg = "Invalid UnitId: %d";
    private static final String STRERR_InvalidSetupId_arg = "Invalid SetupId: %s";
    private static final String STRERR_LabExecutionInfoLock = "labExecutionInfoLock";
    private static final String STRERR_ThreadFailedToStart = "Thread failed to start!";
//
    private static final String STRERR_LabEquipmentAPI = "labEquipmentAPI";
    private static final String STRERR_LabExperimentInfo = "labExperimentInfo";
    private static final String STRERR_RetryingExperiment_arg = "Retrying Experiment... Retry: %d";
    private static final String STRERR_FailedToUpdateQueueStatus = "Failed to update queue statistics!";
    private static final String STRERR_FailedToSaveExperimentResults = "Failed to save experiment results!";
    private static final String STRERR_FailedToUpdateStatisticsStarted = "Failed to update statistics started!";
    private static final String STRERR_FailedToUpdateStatisticsCancelled = "Failed to update statistics cancelled!";
    private static final String STRERR_FailedToUpdateStatisticsCompleted = "Failed to update statistics completed!";
    /*
     * Constants
     */
    private static final int MAX_RETRY_RunExperiment = 3;
    private static final int MAX_RETRY_ServiceBrokerNotify = 3;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Thread thread;
    private LabEquipmentAPI labEquipmentAPI;
    private final Object labExecutionInfoLock;
    //
    protected int unitId;
    protected LabManagement labManagement;
    protected LabExecutionInfo labExecutionInfo;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private boolean running;

    public int getUnitId() {
        return unitId;
    }

    public boolean isRunning() {
        return running;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Types">

    private enum States {

        GetExperiment, PrepareExperiment, ExecuteExperiment, ConcludeExperiment, NotifyServiceBroker, NotifyByEmail, Completed, Done
    }
    //</editor-fold>

    /**
     *
     * @param unitId
     * @param labManagement
     * @throws Exception
     */
    public LabExperimentEngine(int unitId, LabManagement labManagement) throws Exception {
        final String methodName = "LabExperimentEngine";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_UnitId_arg, unitId));

        try {
            /*
             * Check that all parameters are valid
             */
            if (unitId < 0) {
                throw new IllegalArgumentException(String.format(STRERR_InvalidUnitId_arg, unitId));
            }
            if (labManagement == null) {
                throw new NullPointerException(LabManagement.class.getSimpleName());
            }

            /*
             * Save to local variables
             */
            this.unitId = unitId;
            this.labManagement = labManagement;

            /*
             * Create thread objects
             */
            this.labExecutionInfoLock = new Object();
            if (this.labExecutionInfoLock == null) {
                throw new NullPointerException(STRERR_LabExecutionInfoLock);
            }

            /*
             * Check if a lab equipment service exists for this lab experiment engine - may not exist
             */
            if (this.labManagement.getLabEquipmentServiceInfo()[unitId] != null) {
                /*
                 * Try to get a proxy to the LabEquipment service
                 */
                this.labEquipmentAPI = new LabEquipmentAPI(this.labManagement.getLabEquipmentServiceInfo()[unitId].getServiceUrl());
                if (this.labEquipmentAPI == null) {
                    throw new NullPointerException(STRERR_LabEquipmentAPI);
                }
                this.labEquipmentAPI.setIdentifier(this.labManagement.getConfigProperties().getLabServerGuid());
                this.labEquipmentAPI.setPasskey(this.labManagement.getLabEquipmentServiceInfo()[unitId].getPasskey());

                /*
                 * Get the status of the LabEquipment
                 */
                try {
                    LabEquipmentStatus labEquipmentStatus = this.labEquipmentAPI.GetLabEquipmentStatus();
                    Logfile.Write(logLevel, String.format(STRLOG_LabEquipmentStatus_arg2,
                            labEquipmentStatus.isOnline(), labEquipmentStatus.getStatusMessage()));
                } catch (Exception ex) {
                    Logfile.Write(logLevel, String.format(STRERR_LabEquipmentStatus_arg2, ex.getMessage()));
                }

                /*
                 * Get the time until the equipment is ready
                 */
                try {
                    int timeUntilReady = this.labEquipmentAPI.GetTimeUntilReady();
                    Logfile.Write(logLevel, String.format(STRLOG_TimeUntilReady_arg, timeUntilReady));
                } catch (Exception ex) {
                    Logfile.Write(logLevel, String.format(STRERR_LabEquipmentStatus_arg2, ex.getMessage()));
                }
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    public boolean Start() {
        final String methodName = "Start";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Create a new thread and start it
             */
            this.thread = new Thread(this);
            if (this.thread == null) {
                throw new NullPointerException(Thread.class.getSimpleName());
            }
            this.thread.start();

            /*
             * Give it a chance to start running and then check that it has started
             */
            for (int i = 0; i < 5; i++) {
                if ((success = this.running) == true) {
                    break;
                }

                Delay.MilliSeconds(500);
                System.out.println('?');
            }

            if (success == false) {
                throw new RuntimeException(STRERR_ThreadFailedToStart);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @param experimentId
     * @param sbName
     * @return
     */
    public boolean Cancel(int experimentId, String sbName) {
        final String methodName = "Cancel";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentIdSbName_arg, experimentId, sbName));

        boolean success = false;

        /*
         * Check if an experiment is currently running
         */
        synchronized (this.labExecutionInfoLock) {
            if (this.labExecutionInfo != null) {
                /*
                 * Get the lab experiment information
                 */
                LabExperimentInfo labExperimentInfo = this.labExecutionInfo.getLabExperimentInfo();
                if (labExperimentInfo != null) {
                    /*
                     * Check the experiment Id and ServiceBroker name
                     */
                    if (labExperimentInfo.getExperimentId() == experimentId
                            && sbName != null && sbName.equalsIgnoreCase(labExperimentInfo.getSbName())) {
                        /*
                         * The specified experiment is currently running, cancel the experiment
                         */
                        this.labExecutionInfo.getDriver().setCancelled(true);
                        success = true;
                    }
                }
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    public LabStatus GetLabStatus() {
        final String methodName = "GetLabStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        LabStatus labStatus;

        try {
            /*
             * Check if a LabEquipment service exists for this engine - may not
             */
            if (this.labEquipmentAPI != null) {
                /*
                 * Get the status of the LabEquipment
                 */
                LabEquipmentStatus labEquipmentStatus = this.labEquipmentAPI.GetLabEquipmentStatus();
                labStatus = new LabStatus(labEquipmentStatus.isOnline(), labEquipmentStatus.getStatusMessage());
            } else {
                /*
                 * No equipment service, just get the status of this engine
                 */
                labStatus = new LabStatus();
                synchronized (this.labExecutionInfoLock) {
                    labStatus.setOnline(true);
                    if (this.labExecutionInfo != null) {
                        StatusCodes statusCode = this.labExecutionInfo.getLabExperimentInfo().getStatusCode();
                        labStatus.setLabStatusMessage(statusCode.toString());
                    } else {
                        labStatus.setLabStatusMessage(StatusCodes.Ready.toString());
                    }
                }
            }
        } catch (Exception ex) {
            labStatus = new LabStatus(false, ex.getMessage());
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_LabStatus_arg2, labStatus.isOnline(), labStatus.getLabStatusMessage()));

        return labStatus;
    }

    /**
     *
     * @param experimentId
     * @param sbName
     * @return
     */
    public ExperimentStatus GetExperimentStatus(int experimentId, String sbName) {
        final String methodName = "GetExperimentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentIdSbName_arg, experimentId, sbName));

        ExperimentStatus experimentStatus = new ExperimentStatus(StatusCodes.Unknown);

        /*
         * Check if the specified experiment is currently running
         */
        synchronized (this.labExecutionInfoLock) {
            if (this.labExecutionInfo != null) {
                /*
                 * Get the lab experiment information
                 */
                LabExperimentInfo labExperimentInfo = this.labExecutionInfo.getLabExperimentInfo();
                if (labExperimentInfo != null) {
                    /*
                     * Check the experiment Id and ServiceBroker name
                     */
                    if (experimentId == labExperimentInfo.getExperimentId()
                            && sbName != null && sbName.equalsIgnoreCase(labExperimentInfo.getSbName())) {
                        /*
                         * The specified experiment is currently running, create the experiment status information
                         */
                        experimentStatus = new ExperimentStatus(labExperimentInfo.getStatusCode());
                        experimentStatus.setEstRuntime(labExperimentInfo.getEstExecutionTime());

                        /*
                         * Calculate the time remaining for the experiment
                         */
                        long elapsedTime = (Calendar.getInstance().getTimeInMillis() - this.labExecutionInfo.getStartTime().getTimeInMillis()) / 1000;
                        int remainingRuntime = labExperimentInfo.getEstExecutionTime() - (int) elapsedTime;

                        /*
                         * Remaining runtime cannot be negative and may have been underestimated. Don't say remaining
                         * runtime is zero while the experiment is still running.
                         */
                        experimentStatus.setEstRemainingRuntime((remainingRuntime < 1) ? 1 : remainingRuntime);
                    }
                }
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentStatus_arg3,
                experimentStatus.getStatusCode(), experimentStatus.getEstRuntime(), experimentStatus.getEstRemainingRuntime()));

        return experimentStatus;
    }

    /**
     * Get the time remaining in seconds for the currently running experiment. If there is no experiment currently
     * running on this engine, -1 is returned.
     *
     * @return Time in seconds remaining for the currently running experiment.
     */
    public int GetRemainingRuntime() {
        final String methodName = "GetRemainingRuntime";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int remainingRuntime = -1;

        /*
         * Check if an experiment is currently running
         */
        synchronized (this.labExecutionInfoLock) {
            if (this.labExecutionInfo != null) {
//                /*
//                 * Get the lab experiment information
//                 */
//                LabExperimentInfo labExperimentInfo = this.labExecutionInfo.getLabExperimentInfo();
//                if (labExperimentInfo != null) {
//                    /*
//                     * Calculate the time remaining for the experiment
//                     */
//                    long elapsedTime = (Calendar.getInstance().getTimeInMillis() - this.labExecutionInfo.getStartTime().getTimeInMillis()) / 1000;
//                    remainingRuntime = labExperimentInfo.getEstExecutionTime() - (int) elapsedTime;
                DriverGeneric driver = this.labExecutionInfo.getDriver();
                if (driver != null) {
                    remainingRuntime = driver.GetTimeRemaining();

                    /*
                     * Remaining runtime cannot be negative and may have been underestimated. Don't say remaining
                     * runtime is zero while the experiment is still running.
                     */
                    if (remainingRuntime < 1) {
                        remainingRuntime = 1;
                    }
                }
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_RemainingRuntime_arg, remainingRuntime));

        return remainingRuntime;
    }

    /**
     *
     * @param xmlSpecification
     * @return
     */
    public ValidationReport Validate(String xmlSpecification) {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Create an instance of a validation report ready to fill in
         */
        ValidationReport validationReport = new ValidationReport();

        try {
            /*
             * Get the setup Id from the experiment specification
             */
            LabExperimentSpecification labExperimentSpecification = new LabExperimentSpecification(xmlSpecification);
            String setupId = labExperimentSpecification.getSetupId();

            /*
             *  Create an instance of the driver for the setup Id and validate the experiment specification
             */
            DriverGeneric driver = this.GetDriver(setupId);
            validationReport = driver.Validate(xmlSpecification);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            validationReport.setErrorMessage(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Accepted_arg, validationReport.isAccepted()));

        return validationReport;
    }

    /**
     *
     */
    public void Close() {
        final String methodName = "Close";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Stop the LabExperimentEngine thread
         */
        if (this.running == true) {
            this.running = false;

            /*
             * Cancel the experiment
             */
            this.Cancel(unitId, methodName);

            try {
                this.thread.join();
            } catch (InterruptedException ex) {
                Logfile.WriteError(ex.toString());
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     */
    @Override
    public void run() {
        final String methodName = "run";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Initialise state machine
         */
        States lastState = States.Done;
        States thisState = States.GetExperiment;
        LabExperimentInfo labExperimentInfo = null;
        int retryCount = 0;
        this.running = true;

        /*
         * Allow other threads to check the state of this thread
         */
        Delay.MilliSeconds(500);

        /*
         * State machine loop
         */
        try {
            while (thisState != States.Done) {
                /*
                 * Display message on each state change
                 */
                if (thisState != lastState) {
                    String logMessage = String.format(STRLOG_StateChange_arg2, lastState.toString(), thisState.toString());
                    System.out.println(logMessage);
//                    Logfile.Write(logLevel, logMessage);

                    lastState = thisState;
                }

                switch (thisState) {
                    case GetExperiment:
                        /*
                         * Check if there is an experiment to run
                         */
                        if ((labExperimentInfo = this.labManagement.getExperimentQueue().Dequeue(this.unitId)) == null) {
                            /*
                             * No experiment to run
                             */
                            thisState = States.Completed;
                            break;
                        }

                        thisState = States.PrepareExperiment;
                        break;

                    case PrepareExperiment:
                        /*
                         * Prepare the experiment for running
                         */
                        if (PrepareExperiment(labExperimentInfo) == false) {
                            /*
                             * Preparation failed
                             */
                            thisState = States.ConcludeExperiment;
                            break;
                        }

                        thisState = States.ExecuteExperiment;
                        break;

                    case ExecuteExperiment:
                        /*
                         * Execute the experiment
                         */
                        if (ExecuteExperiment() == false) {
                            /*
                             * Check if experiment should be retried
                             */
                            if (++retryCount < MAX_RETRY_RunExperiment) {
                                /*
                                 * Run the experiment again
                                 */
                                Logfile.Write(String.format(STRERR_RetryingExperiment_arg, retryCount));
                                break;
                            }
                        }

                        thisState = States.ConcludeExperiment;
                        break;

                    case ConcludeExperiment:
                        /*
                         * Conclude experiment after running
                         */
                        if (ConcludeExperiment() == false) {
                            /*
                             * Conclude failed, don't rerun experiment
                             */
                            thisState = States.Completed;
                            break;
                        }

                        thisState = States.NotifyServiceBroker;
                        break;

                    case NotifyServiceBroker:
                        /*
                         * Notify ServiceBroker of experiment completion
                         */
                        LabExperimentResult labExperimentResult = this.labExecutionInfo.getLabExperimentResult();
                        NotifyServiceBroker(labExperimentResult.getExperimentId(), labExperimentResult.getSbName());

                        thisState = States.NotifyByEmail;
                        break;

                    case NotifyByEmail:
                        /*
                         * Notify by email of experiment completion
                         */
                        NotifyByEmail(this.labExecutionInfo.getLabExperimentResult());

                        /*
                         * Experiment is finished
                         */
                        synchronized (this.labExecutionInfoLock) {
                            this.labExecutionInfo = null;
                        }

                        /*
                         * Check if there is another experiment to run
                         */
                        thisState = States.GetExperiment;
                        break;

                    case Completed:
                        /*
                         * No more experiments to run
                         */
                        thisState = States.Done;
                        break;
                }
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        /*
         * Thread is no longer running
         */
        this.running = false;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param setupId
     * @return
     * @throws Exception
     */
    protected DriverGeneric GetDriver(String setupId) throws Exception {

        DriverGeneric driverGeneric = null;

        /*
         * Create an instance of the driver for the specified setup Id
         */
        switch (setupId) {
            case LabConsts.STRXML_SetupId_Generic:
                driverGeneric = new DriverGeneric(this.labManagement.getLabConfiguration());
                break;
            case LabConsts.STRXML_SetupId_EquipmentGeneric:
                driverGeneric = new DriverEquipmentGeneric(this.labManagement.getLabConfiguration(),
                        this.labManagement.getLabEquipmentServiceInfo()[this.unitId]);
                break;
            default:
                throw new RuntimeException(String.format(STRERR_InvalidSetupId_arg, setupId));
        }

        return driverGeneric;
    }

    /**
     *
     * @param labExperimentInfo
     * @return
     */
    private boolean PrepareExperiment(LabExperimentInfo labExperimentInfo) {
        final String methodName = "PrepareExperiment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Check if the experiment was cancelled while waiting on the queue
             */
            if (labExperimentInfo.isCancelled() == true) {
                LabExperimentResult labExperimentResult = new LabExperimentResult(this.labManagement.getLabConfiguration());
                ResultReport resultReport = labExperimentResult.getResultReport();
                resultReport.setStatusCode(StatusCodes.Cancelled);

                Logfile.Write(logLevel, STRLOG_QueuedExperimentCancelled);
            } else {
                /*
                 * Update the statistics for starting the experiment
                 */
                if (this.labManagement.getExperimentStatistics().Started(
                        labExperimentInfo.getExperimentId(), labExperimentInfo.getSbName(), this.unitId) == false) {
                    throw new RuntimeException(STRERR_FailedToUpdateStatisticsStarted);
                }

                /*
                 * Create an instance of LabExecutionInfo for information about experiment execution
                 */
                synchronized (this.labExecutionInfoLock) {
                    this.labExecutionInfo = new LabExecutionInfo();
                    if (this.labExecutionInfo == null) {
                        throw new NullPointerException(STRERR_LabExperimentInfo);
                    }

                    /*
                     * Create an instance of the driver for the specification
                     */
                    LabExperimentSpecification labExperimentSpecification = new LabExperimentSpecification(labExperimentInfo.getXmlSpecification());
                    this.labExecutionInfo.setDriver(this.GetDriver(labExperimentSpecification.getSetupId()));

                    /*
                     * Experiment is now running
                     */
                    labExperimentInfo.setSetupId(labExperimentSpecification.getSetupId());
                    labExperimentInfo.setUnitId(this.unitId);
                    labExperimentInfo.setStatusCode(StatusCodes.Running);
                    this.labExecutionInfo.setLabExperimentInfo(labExperimentInfo);
                    this.labExecutionInfo.setStartTime(Calendar.getInstance());
                }
            }


            success = true;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    private boolean ExecuteExperiment() {
        final String methodName = "ExecuteExperiment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Get the driver and experiment specification and then execute the experiment
             */
            DriverGeneric driver = this.labExecutionInfo.getDriver();
            String xmlSpecification = this.labExecutionInfo.getLabExperimentInfo().getXmlSpecification();
            LabExperimentResult labExperimentResult = driver.Execute(xmlSpecification);

            /*
             * Update the execution result status
             */
            synchronized (this.labExecutionInfoLock) {
                this.labExecutionInfo.setLabExperimentResult(labExperimentResult);
            }

            success = true;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    private boolean ConcludeExperiment() {
        final String methodName = "ConcludeExperiment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Update time completed
             */
            LabExperimentResult labExperimentResult = this.labExecutionInfo.getLabExperimentResult();
            labExperimentResult.setTimeCompleted(Calendar.getInstance());

            /*
             * Save the experiment results
             */
            LabExperimentInfo labExperimentInfo = this.labExecutionInfo.getLabExperimentInfo();
            labExperimentResult.SetLabExperimentInfo(labExperimentInfo);
            ExperimentResultInfo experimentResultInfo = labExperimentResult.GetExperimentResultInfo();
            if (this.labManagement.getExperimentResults().Add(experimentResultInfo) < 0) {
                throw new RuntimeException(STRERR_FailedToSaveExperimentResults);
            }

            /*
             * Update experiment status in the queue table
             */
            if (this.labManagement.getExperimentQueue().UpdateStatus(labExperimentInfo.getQueueId(), experimentResultInfo.getStatusCode()) == false) {
                throw new RuntimeException(STRERR_FailedToUpdateQueueStatus);
            }

            /*
             * Check experiment completion status for updating the statistics
             */
            if (labExperimentInfo.getStatusCode() == StatusCodes.Cancelled) {
                /*
                 * Update statistics for cancelled experiment
                 */
                if (this.labManagement.getExperimentStatistics().Cancelled(experimentResultInfo.getExperimentId(), experimentResultInfo.getSbName()) == false) {
                    throw new RuntimeException(STRERR_FailedToUpdateStatisticsCancelled);
                }
            } else {
                /*
                 * Update statistics for completed experiment
                 */
                if (this.labManagement.getExperimentStatistics().Completed(experimentResultInfo.getExperimentId(), experimentResultInfo.getSbName()) == false) {
                    throw new RuntimeException(STRERR_FailedToUpdateStatisticsCompleted);
                }
            }

            success = true;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @param experimentId
     * @param sbName
     * @return
     */
    public boolean NotifyServiceBroker(int experimentId, String sbName) {
        final String methodName = "NotifyServiceBroker";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Notify the ServiceBroker so that the results can be retrieved
             */
            String serviceUrl = this.labManagement.getServiceBrokers().GetServiceUrlByName(sbName);
            ServiceBrokerAPI serviceBrokerAPI = new ServiceBrokerAPI(serviceUrl);
            if (serviceBrokerAPI != null) {
                if ((success = serviceBrokerAPI.Notify(experimentId)) == true) {
                    success = this.labManagement.getExperimentResults().UpdateNotified(experimentId, sbName);
                }
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @param labExperimentResult
     * @return boolean
     */
    public boolean NotifyByEmail(LabExperimentResult labExperimentResult) {
        final String methodName = "NotifyByEmail";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Get experiment result information
             */
            ResultReport resultReport = labExperimentResult.getResultReport();

            /*
             * Check the experiment status code to determine email destination
             */
            String[] toArray;
            String errorMessage;
            if (resultReport.getStatusCode() == StatusCodes.Failed) {
                /*
                 * Send email to all those listed for when the experiment fails
                 */
                toArray = this.labManagement.getConfigProperties().getFailedEmailList();
                errorMessage = String.format(STRLOG_MailMessageError_arg, resultReport.getErrorMessage());
            } else {
                /*
                 * Send email to all those listed for when the experiment completes successfully or is cancelled
                 */
                toArray = this.labManagement.getConfigProperties().getCompletedEmailList();
                errorMessage = "";
            }

            /*
             * Check if any destination email addresses have been specified
             */
            if (toArray != null && toArray.length > 0) {
                /*
                 * Send the email
                 */
                SmtpClient smtpClient = new SmtpClient();
                smtpClient.getTo().addAll(Arrays.asList(toArray));
                smtpClient.setFrom(this.labManagement.getConfigProperties().getContactEmail());
                smtpClient.setSubject(String.format(STRLOG_MailMessageSubject_arg2,
                        this.labManagement.getLabConfiguration().getTitle(), resultReport.getStatusCode().toString()));
                smtpClient.setBody(String.format(STRLOG_MailMessageBody_arg7,
                        labExperimentResult.getSbName(), labExperimentResult.getExperimentId(),
                        labExperimentResult.getSetupId(), labExperimentResult.getUserGroup(),
                        this.unitId, resultReport.getStatusCode(), errorMessage));
                success = smtpClient.Send();
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }
}
