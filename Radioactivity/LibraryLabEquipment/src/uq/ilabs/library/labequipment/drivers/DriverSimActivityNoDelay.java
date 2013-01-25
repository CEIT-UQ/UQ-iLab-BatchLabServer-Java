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
import uq.ilabs.library.labequipment.devices.DeviceSerialLcd;
import uq.ilabs.library.labequipment.devices.DeviceSerialLcdSimulation;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;

/**
 *
 * @author uqlpayne
 */
public class DriverSimActivityNoDelay extends DriverRadioactivity {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DriverSimActivityNoDelay.class.getName();
    private static final Level logLevel = Level.FINER;
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DriverSimActivityNoDelay(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
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
            if (experimentSpecification == null) {
                throw new NullPointerException(ExperimentSpecification.ClassName());
            }

            /*
             * Check the setup Id
             */
            String setupId = experimentSpecification.getSetupId();
            switch (setupId) {
                case Consts.STRXML_SetupId_SimActivityVsTimeNoDelay:
                case Consts.STRXML_SetupId_SimActivityVsDistanceNoDelay:
                    /*
                     * Check that the correct devices have been set
                     */
                    if (DeviceFlexMotionSimulation.class.isInstance(this.deviceFlexMotion) == false) {
                        throw new NullPointerException(String.format(STRERR_InvalidDeviceInstance_arg, DeviceFlexMotionSimulation.ClassName()));
                    }
                    if (DeviceST360CounterSimulation.class.isInstance(this.deviceST360Counter) == false) {
                        throw new NullPointerException(String.format(STRERR_DeviceNotSet_arg, DeviceST360CounterSimulation.ClassName()));
                    }
                    if (DeviceSerialLcd.class.isInstance(this.deviceSerialLcd) == false) {
                        throw new NullPointerException(String.format(STRERR_DeviceNotSet_arg, DeviceSerialLcdSimulation.ClassName()));
                    }

                    /*
                     * Specification is valid - execution time, one is the smallest number
                     */
                    validation = new Validation(true, 1);
                    this.labExperimentSpecification = experimentSpecification;
                    break;

                default:
                    /*
                     * Don't throw an exception, a derived class will want to check the setup Id
                     */
                    validation = new Validation(String.format(STRERR_InvalidSetupId_arg, setupId));
                    break;
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

    /**
     *
     * @return
     */
    @Override
    protected boolean ExecuteInitialising() {
        /*
         * Turn off simulated delays
         */
        ((DeviceFlexMotionSimulation) this.deviceFlexMotion).setDelaysSimulated(false);
        ((DeviceST360CounterSimulation) this.deviceST360Counter).setDelaysSimulated(false);
        ((DeviceSerialLcdSimulation) this.deviceSerialLcd).setDelaysSimulated(false);

        return true;
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean ExecuteFinalising() {
        /*
         * Turn simulated delays back on
         */
        ((DeviceFlexMotionSimulation) this.deviceFlexMotion).setDelaysSimulated(true);
        ((DeviceST360CounterSimulation) this.deviceST360Counter).setDelaysSimulated(true);
        ((DeviceSerialLcdSimulation) this.deviceSerialLcd).setDelaysSimulated(true);

        return true;
    }
}
