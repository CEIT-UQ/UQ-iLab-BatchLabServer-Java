/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.drivers;

import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.utilities.Delay;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labserver.Configuration;
import uq.ilabs.library.labserver.Consts;
import uq.ilabs.library.labserver.ExperimentResult;
import uq.ilabs.library.labserver.ExperimentSpecification;
import uq.ilabs.library.labserver.ExperimentValidation;
import uq.ilabs.library.labserver.engine.drivers.DriverGeneric;

/**
 *
 * @author uqlpayne
 */
public class DriverSimulation extends DriverGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DriverSimulation.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants for exception messages
     */
    private static final String STRERR_SomeParameter = "SomeParameter";
    private static final String STRERR_ValueLessThanMinimum_arg2 = "%s: Less than minimum (%d)!";
    private static final String STRERR_ValueGreaterThanMaximum_arg2 = "%s: Greater than maximum (%d)!";
    /*
     * Constants
     */
    private static final int INT_ExecutionTimeSecs = 9;
    /*
     * String constants
     */
    private static final String STR_XmlExperimentResults =
            "<" + Consts.STRXML_ExperimentResults + ">"
            + "<" + Consts.STRXML_SomeParameter + " />"
            + "<" + Consts.STRXML_SomeResult + " />"
            + "</" + Consts.STRXML_ExperimentResults + ">";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private ExperimentValidation experimentValidation;
    //</editor-fold>

    /**
     *
     * @throws Exception
     */
    public DriverSimulation(Configuration configuration) throws Exception {
        super(configuration);

        final String methodName = "DriverSimulation";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Create an instance of the experiment result ready to fill in
             */
            this.labExperimentResult = new ExperimentResult(configuration);
            if (this.labExperimentResult == null) {
                throw new NullPointerException(ExperimentResult.class.getSimpleName());
            }
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
             * Create an instance of ExperimentSpecification
             */
            ExperimentSpecification experimentSpecification = new ExperimentSpecification(xmlSpecification);
            if (experimentSpecification == null) {
                throw new NullPointerException(ExperimentSpecification.class.getSimpleName());
            }

            /*
             * Check the setup Id
             */
            String setupId = experimentSpecification.getSetupId();
            if (setupId.equals(Consts.STRXML_SetupId_Simulation) == false) {
                throw new RuntimeException(String.format(STRERR_InvalidSetupId_arg, setupId));
            }

            /*
             * Check 'someParameter' is valid
             */
            int someParameter = experimentSpecification.getSomeParameter();
            if (someParameter < this.experimentValidation.getSomeParameterMin()) {
                throw new RuntimeException(String.format(STRERR_ValueLessThanMinimum_arg2, STRERR_SomeParameter, this.experimentValidation.getSomeParameterMin()));
            }
            if (someParameter > this.experimentValidation.getSomeParameterMax()) {
                throw new RuntimeException(String.format(STRERR_ValueGreaterThanMaximum_arg2, STRERR_SomeParameter, this.experimentValidation.getSomeParameterMax()));
            }

            /*
             * Specification is valid, set the execution time
             */
            validationReport = new ValidationReport(true, INT_ExecutionTimeSecs);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            validationReport = new ValidationReport(ex.getMessage());
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
    public ExperimentResult Execute(String xmlSpecification) throws Exception {
        final String methodName = "Execute";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ExperimentResult experimentResult = (ExperimentResult) this.labExperimentResult;

        try {
            /*
             * Validate the specification and get the estimated execution time
             */
            ValidationReport validationReport = this.Validate(xmlSpecification);
            if (validationReport.isAccepted() == false) {
                throw new RuntimeException(validationReport.getErrorMessage());
            }

            /*
             * Create an instance of ExperimentSpecification to get specification information
             */
            ExperimentSpecification experimentSpecification = new ExperimentSpecification(xmlSpecification);
            if (experimentSpecification == null) {
                throw new NullPointerException(ExperimentSpecification.class.getSimpleName());
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
                 * Generate a random number in the range 0 to 'someParameter'
                 */
                Random random = new Random();
                int someResult = random.nextInt(experimentSpecification.getSomeParameter());

                /*
                 * Delay for the full execution time, unless cancelled
                 */
                for (int i = 0; i < INT_ExecutionTimeSecs; i++) {
                    System.out.println('*');
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
                     * Load the experiment result XML document from the string
                     */
                    Document document = XmlUtilities.GetDocumentFromString(STR_XmlExperimentResults);
                    Node nodeRoot = XmlUtilities.GetRootNode(document, Consts.STRXML_ExperimentResults);

                    /*
                     * Add the experiment specification information to the XML document
                     */
                    XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_SomeParameter, experimentSpecification.getSomeParameter());

                    /*
                     * Add the experiment result information to the XML document
                     */
                    XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_SomeResult, someResult);

                    /*
                     * Process the execution result
                     */
                    ResultReport resultReport = experimentResult.getResultReport();
                    resultReport.setStatusCode(StatusCodes.Completed);
                    resultReport.setXmlExperimentResults(XmlUtilities.ToXmlString(document));
                    experimentResult.setExecutionTime(executionTime);
                }
            } catch (XmlUtilitiesException | NullPointerException ex) {
                ResultReport resultReport = experimentResult.getResultReport();
                resultReport.setStatusCode(StatusCodes.Failed);
                resultReport.setErrorMessage(ex.getMessage());
            } finally {
                experimentResult.setTimeCompleted(this.timeCompleted);
                this.timeCompleted = null;
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_StatusCode_arg, experimentResult.getResultReport().getStatusCode().toString()));

        return experimentResult;
    }
}
