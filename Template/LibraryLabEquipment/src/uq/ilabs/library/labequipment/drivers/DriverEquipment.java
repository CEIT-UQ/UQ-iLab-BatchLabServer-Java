/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.drivers;

import java.util.Random;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Delay;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.labequipment.Consts;
import uq.ilabs.library.labequipment.ExperimentSpecification;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;
import uq.ilabs.library.labequipment.engine.drivers.DriverGeneric;

/**
 *
 * @author uqlpayne
 */
public class DriverEquipment extends DriverGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DriverEquipment.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants for exception messages
     */
    private static final String STRERR_SomeParameter = "SomeParameter";
    private static final String STRERR_ValueLessThanMinimum_arg2 = "%s: Less than minimum (%d)!";
    private static final String STRERR_ValueGreaterThanMaximum_arg2 = "%s: Greater than maximum (%d)!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private int someParameterMin;
    private int someParameterMax;
    private int someResult;
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DriverEquipment(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration);

        final String methodName = "DriverEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Get the minimum and maximum values allowed for 'SomeParameter'
         */
        Node xmlNodeValidation = XmlUtilities.GetChildNode(this.nodeDriver, Consts.STRXML_Validation);
        Node xmlNode = XmlUtilities.GetChildNode(xmlNodeValidation, Consts.STRXML_SomeParameter);
        this.someParameterMin = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Min);
        this.someParameterMax = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Max);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param xmlSpecification
     * @return
     * @throws Exception
     */
    @Override
    public Validation Validate(String xmlSpecification) throws Exception {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        Validation validation;

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
            if (setupId.equals(Consts.STRXML_SetupId_Equipment) == false) {
                throw new RuntimeException(String.format(STRERR_InvalidSetupId_arg, setupId));
            }

            /*
             * Check 'someParameter' is valid
             */
            int someParameter = experimentSpecification.getSomeParameter();
            if (someParameter < this.someParameterMin) {
                throw new RuntimeException(String.format(STRERR_ValueLessThanMinimum_arg2, STRERR_SomeParameter, this.someParameterMin));
            }
            if (someParameter > this.someParameterMax) {
                throw new RuntimeException(String.format(STRERR_ValueGreaterThanMaximum_arg2, STRERR_SomeParameter, this.someParameterMax));
            }

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
            this.labExperimentSpecification = experimentSpecification;

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            validation = new Validation(ex.getMessage());
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
    @Override
    public String GetExperimentResults() {
        return super.GetExperimentResults();
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean ExecuteInitialising() {
        return super.ExecuteInitialising();
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean ExecuteStarting() {
        return super.ExecuteStarting();
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean ExecuteRunning() {
        final String methodName = "ExecuteRunning";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Run for the specified execution time and check if cancelled
             */
            for (int i = 0; i < this.executionTimes.getRun(); i++) {
                System.out.println("[R]");
                Delay.MilliSeconds(1000);

                if (this.cancelled == true) {
                    break;
                }
            }

            if (this.cancelled == false) {
                /*
                 * Get the experiment specification
                 */
                ExperimentSpecification experimentSpecification = (ExperimentSpecification) this.labExperimentSpecification;

                /*
                 * Generate a random number in the range 0 to 'someParameter'
                 */
                Random random = new Random();
                this.someResult = random.nextInt(experimentSpecification.getSomeParameter());

                /*
                 * Load the experiment results XML document from the string
                 */
                Document document = XmlUtilities.GetDocumentFromString(this.xmlExperimentResults);
                Node nodeRoot = XmlUtilities.GetRootNode(document, Consts.STRXML_ExperimentResults);

                /*
                 * Add the experiment specification information to the XML document
                 */
                XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_SomeParameter, experimentSpecification.getSomeParameter());

                /*
                 * Add the experiment result information to the XML document
                 */
                XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_SomeResult, this.someResult);

                /*
                 * Save the experiment results information to an XML string
                 */
                this.xmlExperimentResults = XmlUtilities.ToXmlString(document);

                success = true;
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            this.executionStatus.setErrorMessage(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean ExecuteStopping() {
        return super.ExecuteStopping();
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean ExecuteFinalising() {
        return super.ExecuteFinalising();
    }
}
