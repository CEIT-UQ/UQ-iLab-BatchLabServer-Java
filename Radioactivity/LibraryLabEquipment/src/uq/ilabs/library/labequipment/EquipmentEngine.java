/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment;

import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.devices.DeviceFlexMotion;
import uq.ilabs.library.labequipment.devices.DeviceST360Counter;
import uq.ilabs.library.labequipment.drivers.DriverEquipment;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;
import uq.ilabs.library.labequipment.engine.LabEquipmentEngine;
import uq.ilabs.library.labequipment.engine.drivers.DriverGeneric;

/**
 *
 * @author uqlpayne
 */
public class EquipmentEngine extends LabEquipmentEngine {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = EquipmentEngine.class.getName();
    private static final Level logLevel = Level.INFO;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private DeviceFlexMotion deviceFlexMotion;
    private DeviceST360Counter deviceST360Counter;
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public EquipmentEngine(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration);

        final String methodName = "EquipmentEngine";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Create instances of the equipment devices
             */
            this.deviceFlexMotion = new DeviceFlexMotion(this.labEquipmentConfiguration);
            this.deviceST360Counter = new DeviceST360Counter(this.labEquipmentConfiguration);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param setupId
     * @return
     * @throws Exception
     */
    @Override
    protected DriverGeneric GetDriver(String setupId) throws Exception {
        final String methodName = "GetDriver";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_SetupId_arg, setupId));

        DriverGeneric driverGeneric;

        /*
         * Create an instance of the driver for the specified setup Id
         */
        switch (setupId) {
            case Consts.STRXML_SetupId_RadioactivityVsTime:
            case Consts.STRXML_SetupId_RadioactivityVsDistance:
                driverGeneric = new DriverEquipment(this.labEquipmentConfiguration);
                ((DriverEquipment) driverGeneric).setDeviceFlexMotion(this.deviceFlexMotion);
                ((DriverEquipment) driverGeneric).setDeviceST360Counter(this.deviceST360Counter);
                break;
            default:
                driverGeneric = super.GetDriver(setupId);
                break;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                driverGeneric.getDriverName());

        return driverGeneric;
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean PowerupEquipment() {
        final String methodName = "PowerupEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = true;

        /*
         * YOUR CODE HERE
         */

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean InitialiseEquipment() {
        final String methodName = "InitialiseEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Initialise the equipment devices
             */
            if ((success = deviceFlexMotion.Initialise()) == true) {
                success = deviceST360Counter.Initialise();
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
     * @return
     */
    @Override
    protected boolean PowerdownEquipment() {
        final String methodName = "PowerdownEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = true;

        /*
         * YOUR CODE HERE
         */

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }
}
