/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.drivers;

import java.util.logging.Level;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.Consts;
import uq.ilabs.library.labequipment.ExperimentSpecification;
import uq.ilabs.library.labequipment.devices.DeviceFlexMotionSimulation;
import uq.ilabs.library.labequipment.devices.DeviceST360CounterSimulation;
import uq.ilabs.library.labequipment.devices.DeviceSerialLcdSimulation;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;

/**
 *
 * @author uqlpayne
 */
public class DriverSimActivity extends DriverRadioactivity {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DriverSimActivity.class.getName();
    private static final Level logLevel = Level.FINER;
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DriverSimActivity(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration);
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

        try {
            /*
             * Check that base parameters are valid
             */
            super.Validate(xmlSpecification);

            /*
             * Create an instance of ExperimentSpecification
             */
            ExperimentSpecification experimentSpecification = new ExperimentSpecification(xmlSpecification);

            /*
             * Check the setup Id
             */
            String setupId = experimentSpecification.getSetupId();
            switch (setupId) {
                case Consts.STRXML_SetupId_SimActivityVsTime:
                case Consts.STRXML_SetupId_SimActivityVsDistance:
                    /*
                     * Check that the correct devices have been set
                     */
                    if (DeviceFlexMotionSimulation.class.isInstance(this.deviceFlexMotion) == false) {
                        throw new NullPointerException(String.format(STRERR_InvalidDeviceInstance_arg, DeviceFlexMotionSimulation.ClassName()));
                    }
                    if (DeviceST360CounterSimulation.class.isInstance(this.deviceST360Counter) == false) {
                        throw new NullPointerException(String.format(STRERR_DeviceNotSet_arg, DeviceST360CounterSimulation.ClassName()));
                    }
                    if (DeviceSerialLcdSimulation.class.isInstance(this.deviceSerialLcd) == false) {
                        throw new NullPointerException(String.format(STRERR_DeviceNotSet_arg, DeviceSerialLcdSimulation.ClassName()));
                    }

                    /*
                     * Specification is valid
                     */
                    validation = new Validation(true, this.GetExecutionTime(experimentSpecification));
                    this.labExperimentSpecification = experimentSpecification;
                    break;

                default:
                    /*
                     * Don't throw an exception, a derived class will want to check the setup Id
                     */
                    validation = new Validation(String.format(STRERR_InvalidSetupId_arg, setupId));
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            validation = new Validation(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Validation_arg3,
                validation.isAccepted(), validation.getExecutionTime(), validation.getErrorMessage()));

        return validation;
    }
}
