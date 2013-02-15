package uq.ilabs.library.labserver.engine.drivers;

import java.util.Calendar;
import java.util.logging.Level;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.utilities.Delay;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.engine.LabConfiguration;
import uq.ilabs.library.labserver.engine.LabConsts;
import uq.ilabs.library.labserver.engine.LabExperimentResult;
import uq.ilabs.library.labserver.engine.LabExperimentSpecification;
import uq.ilabs.library.labserver.engine.LabExperimentValidation;

/**
 *
 * @author uqlpayne
 */
public class DriverGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DriverGeneric.class.getName();
    private static final Level logLevel = Level.FINEST;
    private static final boolean debugTrace = true;
    /*
     * String constants for logfile messages
     */
    protected static final String STRLOG_Validation_arg3 = "Accepted: %s  EstRuntime: %.1f  ErrorMessage: %s";
    protected static final String STRLOG_ExecutionTime_arg = "ExecutionTime: %d";
    protected static final String STRLOG_StatusCode_arg = "StatusCode: %s";
    protected static final String STRLOG_TimeRemaining_arg = "TimeRemaining: %d";
    /*
     * String constants for exception messages
     */
    protected static final String STRERR_XmlSpecification = "xmlSpecification";
    protected static final String STRERR_InvalidSetupId_arg = "Invalid SetupId: %s";
    /*
     * Constants
     */
    private static final int INT_ExecutionTimeSecs = 7;
    /*
     * String constants
     */
    private static final String STR_XmlExperimentResults =
            "<" + LabConsts.STRXML_ExperimentResults + ">"
            + "</" + LabConsts.STRXML_ExperimentResults + ">";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    protected LabConfiguration labConfiguration;
    protected LabExperimentSpecification labExperimentSpecification;
    protected LabExperimentValidation labExperimentValidation;
    protected LabExperimentResult labExperimentResult;
    protected Calendar timeStarted;
    protected Calendar timeCompleted;
    protected StatusCodes statusCode;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    protected String driverName;
    protected boolean cancelled;

    public String getDriverName() {
        return driverName;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    //</editor-fold>

    /**
     *
     * @throws Exception
     */
    public DriverGeneric(LabConfiguration labConfiguration) throws Exception {
        final String methodName = "DriverGeneric";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Create an instance of the experiment result ready to fill in
             */
            this.labExperimentResult = new LabExperimentResult(labConfiguration);
            if (this.labExperimentResult == null) {
                throw new NullPointerException(LabExperimentResult.class.getSimpleName());
            }

            /*
             * Initialise local variables and properties
             */
            this.labConfiguration = labConfiguration;
            this.driverName = this.getClass().getSimpleName();
            this.cancelled = false;

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
    public ValidationReport Validate(String xmlSpecification) throws Exception {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ValidationReport validationReport;

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
            String setupId = labExperimentSpecification.getSetupId();
            if (setupId.equals(LabConsts.STRXML_SetupId_Generic) == false) {
                /*
                 * Don't throw an exception, a derived class will want to check the setup Id
                 */
                validationReport = new ValidationReport(String.format(STRERR_InvalidSetupId_arg, setupId));
            } else {
                /*
                 * Set the execution time
                 */
                validationReport = new ValidationReport();
                validationReport.setEstRuntime(INT_ExecutionTimeSecs);
                validationReport.setAccepted(true);
            }
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
    public LabExperimentResult Execute(String xmlSpecification) throws Exception {
        final String methodName = "Execute";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Validate the specification and get the estimated execution time
             */
            ValidationReport validationReport = this.Validate(xmlSpecification);
            if (validationReport.isAccepted() == false) {
                throw new RuntimeException(validationReport.getErrorMessage());
            }

            try {
                /*
                 * Set the start and completion times
                 */
                this.timeStarted = Calendar.getInstance();
                this.timeCompleted = Calendar.getInstance();
                this.timeCompleted.setTimeInMillis(this.timeStarted.getTimeInMillis() + (int) validationReport.getEstRuntime() * 1000);

                /*
                 * Start execution of the lab experiment specification
                 */
                this.statusCode = StatusCodes.Running;

                /*
                 * Delay for the full execution time, unless cancelled
                 */
                for (int i = 0; i < INT_ExecutionTimeSecs; i++) {
                    if (debugTrace == true) {
                        System.out.println("[*]");
                    }
                    Delay.MilliSeconds(1000);

                    /*
                     * Check if the experiment has been cancelled
                     */
                    if (this.isCancelled() == true) {
                        statusCode = StatusCodes.Cancelled;
                        break;
                    }
                }

                /*
                 * Check if the experiment was cancelled
                 */
                if (statusCode == StatusCodes.Cancelled) {
                    /*
                     * Yes, it was
                     */
                    this.labExperimentResult.getResultReport().setStatusCode(StatusCodes.Cancelled);
                } else {
                    /*
                     * Get the actual execution time
                     */
                    int executionTime = (int) ((Calendar.getInstance().getTimeInMillis() - this.timeStarted.getTimeInMillis()) / 1000);

                    /*
                     * Process the execution result
                     */
                    ResultReport resultReport = this.labExperimentResult.getResultReport();
                    resultReport.setStatusCode(StatusCodes.Completed);
                    resultReport.setXmlExperimentResults(STR_XmlExperimentResults);
                    this.labExperimentResult.setExecutionTime(executionTime);
                }
            } catch (Exception ex) {
                ResultReport resultReport = this.labExperimentResult.getResultReport();
                resultReport.setStatusCode(StatusCodes.Failed);
                resultReport.setErrorMessage(ex.getMessage());
            } finally {
                this.labExperimentResult.setTimeCompleted(this.timeCompleted);
                this.timeCompleted = null;
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_StatusCode_arg, this.labExperimentResult.getResultReport().getStatusCode().toString()));

        return this.labExperimentResult;
    }

    /**
     *
     * @return
     */
    public int GetTimeRemaining() {
        final String methodName = "GetTimeRemaining";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int timeRemaining = -1;

        if (this.timeCompleted != null) {
            /*
             * Get the time in seconds from now until the expected completion time
             */
            long timeNowInMillis = Calendar.getInstance().getTimeInMillis();
            timeRemaining = (int) ((this.timeCompleted.getTimeInMillis() - timeNowInMillis) / 1000);
            if (timeRemaining < 0) {
                timeRemaining = 0;
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_TimeRemaining_arg, timeRemaining));

        return timeRemaining;
    }
}
