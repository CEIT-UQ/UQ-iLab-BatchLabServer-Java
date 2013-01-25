/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.drivers;

import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Delay;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.labequipment.Consts;
import uq.ilabs.library.labequipment.ExperimentSpecification;
import uq.ilabs.library.labequipment.ExperimentValidation;
import uq.ilabs.library.labequipment.devices.DeviceEquipment;
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
    private static final boolean debugTrace = false;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private int someResult;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private DeviceEquipment deviceEquipment;

    public void setDeviceEquipment(DeviceEquipment deviceEquipment) {
        this.deviceEquipment = deviceEquipment;
    }
    //</editor-fold>

    private enum States {

        Done, RunningDelay, TakeMeasurements
    }

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DriverEquipment(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration);

        final String methodName = "DriverEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Create an instance of ExperimentValidation
             */
            this.labExperimentValidation = new ExperimentValidation(labEquipmentConfiguration.getXmlValidation());
            if (this.labExperimentValidation == null) {
                throw new NullPointerException(ExperimentValidation.class.getSimpleName());
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
             * Check that the devices have been set
             */
            if (this.deviceEquipment == null) {
                throw new NullPointerException(DeviceEquipment.class.getSimpleName());
            }

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
            switch (setupId) {
                case Consts.STRXML_SetupId_Equipment:
                    break;
                default:
                    throw new RuntimeException(String.format(STRERR_InvalidSetupId_arg, setupId));
            }

            /*
             * Validate the experiment specification parameters
             */
            ExperimentValidation experimentValidation = (ExperimentValidation) this.labExperimentValidation;
            experimentValidation.ValidateSomeParameter(experimentSpecification.getSomeParameter());

            /*
             * Calculate the execution time
             */
            int executionTime = this.executionTimes.getTotalExecutionTime();

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
        final String methodName = "GetExperimentResults";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String xmlExperimentResults = null;

        try {
            /*
             * Get the experiment specification
             */
            ExperimentSpecification experimentSpecification = (ExperimentSpecification) this.labExperimentSpecification;

            /*
             * Load the experiment results template XML document from the string
             */
            Document document = XmlUtilities.GetDocumentFromString(this.xmlExperimentResultsTemplate);
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
            xmlExperimentResults = XmlUtilities.ToXmlString(document);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        return xmlExperimentResults;
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
             * Get the experiment specification
             */
            ExperimentSpecification experimentSpecification = (ExperimentSpecification) this.labExperimentSpecification;

            /*
             * Initialise state machine
             */
            int runningTime = this.executionTimes.getRun();
            States thisState = States.RunningDelay;

            /*
             * State machine loop
             */
            while (thisState != States.Done) {
                switch (thisState) {
                    case RunningDelay:
                        /*
                         * Delay added for realism
                         */
                        if (debugTrace == true) {
                            System.out.println("[R]");
                        }
                        Delay.MilliSeconds(1000);

                        /*
                         * Check if running delay has completed
                         */
                        if (--runningTime == 0) {
                            thisState = States.TakeMeasurements;
                        }
                        break;

                    case TakeMeasurements:
                        /*
                         * Get a random number in the range 0 to 'someParameter' from the device
                         */
                        int[] intValue = new int[1];
                        if (this.deviceEquipment.GetRandom(experimentSpecification.getSomeParameter(), intValue) == false) {
                            throw new RuntimeException(this.deviceEquipment.getLastError());
                        }
                        this.someResult = intValue[0];

                        thisState = States.Done;
                        break;
                }

                /*
                 * Check if the experiment has been cancelled
                 */
                if (this.cancelled == true) {
                    thisState = States.Done;
                }
            }

            success = (this.cancelled == false);
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
