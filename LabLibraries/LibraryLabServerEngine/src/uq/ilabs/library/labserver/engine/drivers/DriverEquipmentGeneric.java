package uq.ilabs.library.labserver.engine.drivers;

import java.util.Calendar;
import java.util.logging.Level;
import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.LabEquipmentStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.utilities.Delay;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.LabEquipmentAPI;
import uq.ilabs.library.labserver.engine.LabConfiguration;
import uq.ilabs.library.labserver.engine.LabExperimentResult;
import uq.ilabs.library.labserver.engine.types.LabEquipmentServiceInfo;

/**
 *
 * @author uqlpayne
 */
public class DriverEquipmentGeneric extends DriverGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DriverEquipmentGeneric.class.getName();
    private static final Level logLevel = Level.FINER;
    private static final boolean debugTrace = false;
    /*
     * String constants for logfile messages
     */
    protected static final String STRLOG_ExecutionStatus_arg2 = "ExecuteStatus: %s  TimeRemaining: %d";
    protected static final String STRLOG_StatusCodeExecutionTime_arg2 = "StatusCode: %s  ExecutionTime: %d";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_LabEquipmentOffline = "LabEquipment is offline!";
    private static final String STRERR_LabEquipmentAlreadyExecuting = "LabEquipment is already executing!";
    private static final String STRERR_LabEquipmentFailedReady = "LabEquipment failed to become ready!";
    private static final String STRERR_LabEquipmentFailedToStartExecution = "LabEquipment failed to start execution!";
    private static final String STRERR_ExecutionTimeout = "Timeout waiting for LabEquipment execution to complete!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    protected LabEquipmentAPI labEquipmentAPI;
    //</editor-fold>

    /**
     *
     * @param labConfiguration
     * @param labEquipmentServiceInfo
     * @throws Exception
     */
    public DriverEquipmentGeneric(LabConfiguration labConfiguration, LabEquipmentServiceInfo labEquipmentServiceInfo) throws Exception {
        super(labConfiguration);

        final String methodName = "DriverEquipmentGeneric";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that parameters are valid
             */
            if (labEquipmentServiceInfo == null) {
                throw new NullPointerException(LabEquipmentServiceInfo.class.getSimpleName());
            }

            /*
             * Create an instance of the lab equipment API for the specified service url
             */
            this.labEquipmentAPI = labEquipmentServiceInfo.getLabEquipmentAPI();
            if (this.labEquipmentAPI == null) {
                throw new NullPointerException(LabEquipmentAPI.class.getSimpleName());
            }

            /*
             * Initialise locals
             */
            this.driverName = this.getClass().getSimpleName();

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param xmlSpecification
     * @return
     */
    @Override
    public ValidationReport Validate(String xmlSpecification) throws Exception {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ValidationReport validationReport;

        /*
         * Check that parameters are valid
         */
        super.Validate(xmlSpecification);

        try {
            /*
             * Validate the specification
             */
            validationReport = this.labEquipmentAPI.Validate(xmlSpecification);
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Validation_arg3,
                validationReport.isAccepted(), validationReport.getEstRuntime(), validationReport.getErrorMessage()));

        return validationReport;
    }

    /**
     *
     * @param xmlSpecification
     * @return
     * @throws Exception
     */
    @Override
    public LabExperimentResult Execute(String xmlSpecification) throws Exception {
        final String methodName = "Execute";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Check that parameters are valid
         */
        super.Validate(xmlSpecification);

        try {
            /*
             * Check that the LabEquipment is ready
             */
            LabEquipmentStatus labEquipmentStatus = this.labEquipmentAPI.GetLabEquipmentStatus();
            if (labEquipmentStatus.isOnline() == false) {
                throw new RuntimeException(STRERR_LabEquipmentOffline);
            }

            /*
             * Check if the LabEquipment is already running an experiment
             */
            ExecutionStatus executionStatus = labEquipmentAPI.GetLabExecutionStatus(0);
            if (executionStatus.getExecuteStatus() != ExecutionStatus.Status.None) {
                if (executionStatus.getExecuteStatus() != ExecutionStatus.Status.Completed) {
                    throw new RuntimeException(STRERR_LabEquipmentAlreadyExecuting);
                }

                /*
                 * Check if a previous experiment has completed but the results not retrieved
                 */
                if (executionStatus.getResultStatus() == ExecutionStatus.Status.Completed) {
                    this.labEquipmentAPI.GetLabExecutionResults(executionStatus.getExecutionId());
                }

                /*
                 * Check the status again
                 */
                executionStatus = labEquipmentAPI.GetLabExecutionStatus(0);
                if (executionStatus.getExecuteStatus() != ExecutionStatus.Status.None
                        || executionStatus.getResultStatus() != ExecutionStatus.Status.None) {
                    throw new RuntimeException(STRERR_LabEquipmentFailedReady);
                }
            }

            /*
             * Set the start time
             */
            this.timeStarted = Calendar.getInstance();

            /*
             * Start execution of the lab experiment specification
             */
            this.statusCode = StatusCodes.Running;

            /*
             * Start execution of the lab experiment specification and get execution Id
             */
            executionStatus = this.labEquipmentAPI.StartLabExecution(xmlSpecification);
            if (executionStatus.getResultStatus() == ExecutionStatus.Status.Failed) {
                throw new RuntimeException(STRERR_LabEquipmentFailedToStartExecution);
            }

            /*
             * Set the expected completion time
             */
            this.timeCompleted = Calendar.getInstance();
            this.timeCompleted.setTimeInMillis(this.timeStarted.getTimeInMillis() + executionStatus.getTimeRemaining() * 1000);

            /*
             * Get the execution Id for further communication with the LabEquipment
             */
            int executionId = executionStatus.getExecutionId();

            /*
             * Set a timeout so that we don't wait forever for the experiment to complete
             */
            int timeout = executionStatus.getTimeRemaining() + 60;
            while (timeout > 0) {
                /*
                 * Check if execution has completed
                 */
                executionStatus = this.labEquipmentAPI.GetLabExecutionStatus(executionId);
                if (executionStatus.getExecuteStatus() == ExecutionStatus.Status.Completed) {
                    break;
                }

                /*
                 * Not yet - get time remaining
                 */
                int executionTimeRemaining = executionStatus.getTimeRemaining();
                Logfile.Write(String.format(STRLOG_ExecutionStatus_arg2,
                        executionStatus.getExecuteStatus().toString(), executionStatus.getTimeRemaining()));

                /*
                 * Update the expected completion time
                 */
                this.timeCompleted.setTimeInMillis(this.timeStarted.getTimeInMillis() + executionTimeRemaining * 1000);

                /*
                 * Wait a bit and then check again
                 */
                int secondsToWait;
                if (executionTimeRemaining > 40) {
                    secondsToWait = 20;
                } else if (executionTimeRemaining > 5) {
                    secondsToWait = executionTimeRemaining / 2;
                } else {
                    secondsToWait = 2;
                }

                for (int i = 0; i < secondsToWait; i++) {
                    if (debugTrace == true) {
                        System.out.println("[E]");
                    }
                    Delay.MilliSeconds(1000);

                    /*
                     * Check if the experiment has been cancelled
                     */
                    if (this.isCancelled() == true && this.statusCode != StatusCodes.Cancelled) {
                        this.labEquipmentAPI.CancelLabExecution(executionId);
                        this.statusCode = StatusCodes.Cancelled;
                    }
                }

                /*
                 * Update timeout
                 */
                timeout -= secondsToWait;
            }

            /*
             * Check timeout
             */
            if (timeout < 0) {
                throw new RuntimeException(STRERR_ExecutionTimeout);
            }

            /*
             * Check result status and process
             */
            switch (executionStatus.getResultStatus()) {
                case Completed:
                    /*
                     * Get the experiment results
                     */
                    String experimentResults = this.labEquipmentAPI.GetLabExecutionResults(executionId);

                    /*
                     * Process the execution result
                     */
                    ResultReport resultReport = this.labExperimentResult.getResultReport();
                    resultReport.setStatusCode(StatusCodes.Completed);
                    resultReport.setXmlExperimentResults(experimentResults);

                    /*
                     * Set the actual execution time
                     */
                    int executionTime = (int) ((Calendar.getInstance().getTimeInMillis() - this.timeStarted.getTimeInMillis()) / 1000);
                    this.labExperimentResult.setExecutionTime(executionTime);
                    break;

                case Cancelled:
                    this.labExperimentResult.getResultReport().setStatusCode(StatusCodes.Cancelled);
                    break;

                case Failed:
                    throw new RuntimeException(executionStatus.getErrorMessage());

                default:
                    this.labExperimentResult.getResultReport().setStatusCode(StatusCodes.Unknown);
                    break;
            }
        } catch (Exception ex) {
            ResultReport resultReport = this.labExperimentResult.getResultReport();
            resultReport.setStatusCode(StatusCodes.Failed);
            resultReport.setErrorMessage(ex.getMessage());
        }


        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_StatusCodeExecutionTime_arg2,
                this.labExperimentResult.getResultReport().getStatusCode().toString(), this.labExperimentResult.getExecutionTime()));

        return this.labExperimentResult;
    }
}
