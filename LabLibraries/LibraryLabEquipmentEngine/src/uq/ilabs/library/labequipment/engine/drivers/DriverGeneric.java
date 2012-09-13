/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.engine.drivers;

import java.util.Calendar;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Delay;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labequipment.engine.LabConsts;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;
import uq.ilabs.library.labequipment.engine.LabExperimentSpecification;
import uq.ilabs.library.labequipment.engine.types.ExecutionTimes;

/**
 *
 * @author uqlpayne
 */
public class DriverGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DriverGeneric.class.getName();
    private static final Level logLevel = Level.FINEST;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_StateChange_arg = "[DG: %s->%s]";
    private static final String STRLOG_UpdateExecutionStatus_arg3 = "Success: %s  SuccessStatus: %s  FailedStatus: %s";
    //
    protected static final String STRLOG_Validation_arg3 = "Accepted: %s  ExecutionTime: %d  ErrorMessage: %s";
    protected static final String STRLOG_ExecutionTime_arg = "ExecutionTime: %d";
    protected static final String STRLOG_ExecutionStatus_arg5 = "ExecutionId: %d  ExecuteStatus: %s  ResultStatus: %s  TimeRemaining: %d  ErrorMessage: %s";
    protected static final String STRLOG_Success_arg = "Success: %s";
    /*
     * String constants for exception messages
     */
    protected static final String STRERR_XmlSpecification = "xmlSpecification";
    protected static final String STRERR_InvalidSetupId_arg = "Invalid SetupId: %s";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private final Object executionStatusLock;
    private Calendar completionTime;
    //
    protected Node nodeDriver;
    protected LabExperimentSpecification labExperimentSpecification;
    protected String xmlExperimentResults;
    protected ExecutionStatus executionStatus;
    protected boolean cancelled;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private int executionId;
    protected String driverName;
    protected ExecutionTimes executionTimes;

    public int getExecutionId() {
        return executionId;
    }

    public void setExecutionId(int executionId) {
        this.executionId = executionId;
    }

    public String getDriverName() {
        return driverName;
    }

    public ExecutionTimes getExecutionTimes() {
        return executionTimes;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Types">

    private enum States {

        None, Initialise, Start, Run, Stop, Finalise, Completed
    }
    //</editor-fold>

    /**
     *
     * @param xmlDriverConfiguration
     * @throws Exception
     */
    public DriverGeneric(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        final String methodName = "DriverGeneric";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that parameters are valid
             */
            if (labEquipmentConfiguration == null) {
                throw new NullPointerException(LabEquipmentConfiguration.class.getSimpleName());
            }

            /*
             * Get the driver configuration from the XML string
             */
            Document xmlDocument = XmlUtilities.GetDocumentFromString(labEquipmentConfiguration.GetXmlDriverConfiguration(this.getClass().getSimpleName()));
            Node nodeRoot = XmlUtilities.GetRootNode(xmlDocument, LabConsts.STRXML_Driver);

            /*
             * Get the execution times for each of the driver states
             */
            Node xmlNodeExecutionTimes = XmlUtilities.GetChildNode(nodeRoot, LabConsts.STRXML_ExecutionTimes);
            this.executionTimes = new ExecutionTimes();
            this.executionTimes.setInitialise(XmlUtilities.GetChildValueAsInt(xmlNodeExecutionTimes, LabConsts.STRXML_Initialise));
            this.executionTimes.setStart(XmlUtilities.GetChildValueAsInt(xmlNodeExecutionTimes, LabConsts.STRXML_Start));
            this.executionTimes.setRun(XmlUtilities.GetChildValueAsInt(xmlNodeExecutionTimes, LabConsts.STRXML_Run));
            this.executionTimes.setStop(XmlUtilities.GetChildValueAsInt(xmlNodeExecutionTimes, LabConsts.STRXML_Stop));
            this.executionTimes.setFinalise(XmlUtilities.GetChildValueAsInt(xmlNodeExecutionTimes, LabConsts.STRXML_Finalise));

            /*
             * Get the experiment results XML template
             */
            Node xmlNodeExperimentResults = XmlUtilities.GetChildNode(nodeRoot, LabConsts.STRXML_ExperimentResults);
            this.xmlExperimentResults = XmlUtilities.ToXmlString(xmlNodeExperimentResults);

            /*
             * Initialise local variables
             */
            this.driverName = this.getClass().getSimpleName();
            this.completionTime = Calendar.getInstance();
            this.executionId = 0;
            this.executionStatusLock = new Object();
            this.executionStatus = new ExecutionStatus();
            this.executionStatus.setExecuteStatus(ExecutionStatus.Status.Created);
            this.executionStatus.setTimeRemaining(this.executionTimes.getTotalExecutionTime());

            /*
             * Save a copy of the driver XML node for the derived class
             */
            this.nodeDriver = nodeRoot.cloneNode(true);

        } catch (NullPointerException | IllegalArgumentException | XmlUtilitiesException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        String logMessage = String.format(STRLOG_ExecutionStatus_arg5, executionStatus.getExecutionId(), executionStatus.getExecuteStatus(),
                executionStatus.getResultStatus(), executionStatus.getTimeRemaining(), executionStatus.getErrorMessage());

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName, logMessage);
    }

    /**
     *
     * @param xmlSpecification
     * @return
     */
    public Validation Validate(String xmlSpecification) throws Exception {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        Validation validation;

        try {
            /*
             * Create an instance of LabExperimentSpecification and get the setup Id
             */
            this.labExperimentSpecification = new LabExperimentSpecification(xmlSpecification);
            if (this.labExperimentSpecification == null) {
                throw new NullPointerException(LabExperimentSpecification.class.getSimpleName());
            }

            /*
             * Check the setup Id
             */
            String setupId = this.labExperimentSpecification.getSetupId();
            if (setupId.equals(LabConsts.STRXML_SetupId_Generic) == false) {
                /*
                 * Don't throw an exception, a derived class will want to check the setup Id
                 */
                validation = new Validation(String.format(STRERR_InvalidSetupId_arg, setupId));
            } else {
                /*
                 * Calculate the execution time
                 */
                int executionTime = this.executionTimes.getInitialise()
                        + this.executionTimes.getStart()
                        + this.executionTimes.getRun()
                        + this.executionTimes.getStop()
                        + this.executionTimes.getFinalise();

                /*
                 * Specification is valid
                 */
                validation = new Validation(true, executionTime);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Validation_arg3,
                validation.isAccepted(), validation.getExecutionTime(), validation.getErrorMessage()));

        return validation;
    }

    /**
     *
     * @return
     */
    public ExecutionStatus GetExecutionStatus() {
        final String methodName = "GetExecutionStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ExecutionStatus execStatus = new ExecutionStatus();

        /*
         * Copy over the execution status
         */
        synchronized (this.executionStatusLock) {
            execStatus.setExecutionId(this.executionId);
            execStatus.setExecuteStatus(this.executionStatus.getExecuteStatus());
            execStatus.setResultStatus(this.executionStatus.getResultStatus());
            execStatus.setErrorMessage(this.executionStatus.getErrorMessage());
        }

        /*
         * Update the time remaining
         */
        int timeRemaining;
        switch (execStatus.getExecuteStatus()) {
            case Created:
                timeRemaining = this.executionTimes.getTotalExecutionTime();
                break;

            case Initialising:
            case Starting:
            case Running:
            case Stopping:
            case Finalising:
                /*
                 * Get the time in seconds from now until the expected completion time
                 */
                timeRemaining = (int) ((this.completionTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 1000);

                /*
                 * Ensure time remaining is greater than zero
                 */
                if (timeRemaining < 1) {
                    timeRemaining = 1;
                }
                break;

            case Done:
            case Completed:
            case Failed:
            case Cancelled:
                timeRemaining = 0;
                break;

            default:
                timeRemaining = -1;
        }
        execStatus.setTimeRemaining(timeRemaining);

        String logMessage = String.format(STRLOG_ExecutionStatus_arg5, execStatus.getExecutionId(), execStatus.getExecuteStatus(),
                execStatus.getResultStatus(), execStatus.getTimeRemaining(), execStatus.getErrorMessage());

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName, logMessage);

        return execStatus;
    }

    /**
     *
     * @return
     */
    public String GetExperimentResults() {
        final String methodName = "GetExperimentResults";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String experimentResults = this.xmlExperimentResults;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return experimentResults;
    }

    /**
     *
     */
    public void Cancel() {
        final String methodName = "Cancel";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        this.cancelled = true;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     */
    public void Execute() {
        final String methodName = "Execute";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Initialise state machine
         */
        this.cancelled = false;
        States lastState = States.None;
        States thisState = States.Initialise;
        this.UpdateExecutionStatus(true, ExecutionStatus.Status.Initialising, ExecutionStatus.Status.None);

        /*
         * Allow other threads to check the state of this thread
         */
        Delay.MilliSeconds(500);

        /*
         * State machine loop
         */
        try {
            while (thisState != States.Completed) {
                boolean success;
//                int seconds;

                /*
                 * Display message on each state change
                 */
                if (thisState != lastState) {
                    String logMessage = String.format(STRLOG_StateChange_arg, lastState.toString(), thisState.toString());
//                    System.out.println(logMessage);
                    Logfile.Write(logMessage);

                    lastState = thisState;
                }

                switch (thisState) {
                    case Initialise:
                        /*
                         * Execute this part of the driver
                         */
                        success = this.ExecuteInitialising();
                        this.UpdateExecutionStatus(success, ExecutionStatus.Status.Starting, ExecutionStatus.Status.Completed);
                        thisState = (success == true) ? States.Start : States.Completed;
                        break;

                    case Start:
                        /*
                         * Execute this part of the driver
                         */
                        success = this.ExecuteStarting();
                        this.UpdateExecutionStatus(success, ExecutionStatus.Status.Running, ExecutionStatus.Status.Stopping);
                        thisState = (success == true) ? States.Run : States.Stop;
                        break;

                    case Run:
                        /*
                         * Execute this part of the driver
                         */
                        success = this.ExecuteRunning();
                        this.UpdateExecutionStatus(success, ExecutionStatus.Status.Stopping, ExecutionStatus.Status.Stopping);
                        thisState = States.Stop;
                        break;

                    case Stop:
                        /*
                         * Execute this part of the driver
                         */
                        success = this.ExecuteStopping();
                        this.UpdateExecutionStatus(success, ExecutionStatus.Status.Finalising, ExecutionStatus.Status.Finalising);
                        thisState = States.Finalise;
                        break;

                    case Finalise:
                        /*
                         * Execute this part of the driver
                         */
                        success = this.ExecuteFinalising();
                        this.UpdateExecutionStatus(success, ExecutionStatus.Status.Completed, ExecutionStatus.Status.Completed);
                        thisState = States.Completed;
                        break;
                }
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    protected boolean ExecuteInitialising() {
        final String methodName = "ExecuteInitialising";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = true;

        for (int i = 0; i < this.executionTimes.getInitialise(); i++) {
            System.out.println("[i]");
            Delay.MilliSeconds(1000);

            if (this.cancelled == true) {
                success = false;
                break;
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
    protected boolean ExecuteStarting() {
        final String methodName = "ExecuteStarting";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = true;

        for (int i = 0; i < this.executionTimes.getStart(); i++) {
            System.out.println("[s]");
            Delay.MilliSeconds(1000);

            if (this.cancelled == true) {
                success = false;
                break;
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
    protected boolean ExecuteRunning() {
        final String methodName = "ExecuteRunning";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = true;

        for (int i = 0; i < this.executionTimes.getRun(); i++) {
            System.out.println("[r]");
            Delay.MilliSeconds(1000);

            if (this.cancelled == true) {
                success = false;
                break;
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
    protected boolean ExecuteStopping() {
        final String methodName = "ExecuteStopping";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = true;

        for (int i = 0; i < this.executionTimes.getStop(); i++) {
            System.out.println("[p]");
            Delay.MilliSeconds(1000);

//            if (this.cancelled == true) {
//                success = false;
//                break;
//            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    protected boolean ExecuteFinalising() {
        final String methodName = "ExecuteFinalising";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = true;

        for (int i = 0; i < this.executionTimes.getFinalise(); i++) {
            System.out.println("[f]");
            Delay.MilliSeconds(1000);

//            if (this.cancelled == true) {
//                success = false;
//                break;
//            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @param success
     * @param successStatus
     * @param failedStatus
     */
    private void UpdateExecutionStatus(boolean success, ExecutionStatus.Status successStatus, ExecutionStatus.Status failedStatus) {
        final String methodName = "UpdateExecutionStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_UpdateExecutionStatus_arg3, success, successStatus, failedStatus));

        synchronized (this.executionStatusLock) {
            /*
             * Update result status if execution failed
             */
            if (success == false) {
                if (this.executionStatus.getResultStatus() == ExecutionStatus.Status.None) {
                    this.executionStatus.setResultStatus(
                            (this.cancelled == true) ? ExecutionStatus.Status.Cancelled : ExecutionStatus.Status.Failed);
                }
            }

            /*
             * Update result status if execution has completed
             */
            if (successStatus == ExecutionStatus.Status.Completed) {
                if (this.executionStatus.getResultStatus() == ExecutionStatus.Status.None) {
                    this.executionStatus.setResultStatus(ExecutionStatus.Status.Completed);
                }
            }

            /*
             * Update execute status only after updating result status
             */
            this.executionStatus.setExecuteStatus((success == true) ? successStatus : failedStatus);

            /*
             * Get the time remaining
             */
            int timeRemaining = 0;
            switch (this.executionStatus.getExecuteStatus()) {
                case Initialising:
                    timeRemaining = this.executionTimes.getInitialise()
                            + this.executionTimes.getStart()
                            + this.executionTimes.getRun()
                            + this.executionTimes.getStop()
                            + this.executionTimes.getFinalise();
                    break;
                case Starting:
                    timeRemaining = this.executionTimes.getStart()
                            + this.executionTimes.getRun()
                            + this.executionTimes.getStop()
                            + this.executionTimes.getFinalise();
                    break;
                case Running:
                    timeRemaining = this.executionTimes.getRun()
                            + this.executionTimes.getStop()
                            + this.executionTimes.getFinalise();
                    break;
                case Stopping:
                    timeRemaining = this.executionTimes.getStop()
                            + this.executionTimes.getFinalise();
                    break;
                case Finalising:
                    timeRemaining =
                            this.executionTimes.getFinalise();
                    break;
                case Completed:
                    break;
            }

            /*
             * Set the time remaining in the execution status and renew the expected completion time
             */
            this.executionStatus.setTimeRemaining(timeRemaining);
            this.completionTime.setTimeInMillis(Calendar.getInstance().getTimeInMillis() + timeRemaining * 1000);

            Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
        }
    }
}
