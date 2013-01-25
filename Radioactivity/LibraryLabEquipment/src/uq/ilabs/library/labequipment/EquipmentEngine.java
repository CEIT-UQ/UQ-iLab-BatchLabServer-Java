/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment;

import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.labequipment.devices.DeviceFlexMotion;
import uq.ilabs.library.labequipment.devices.DeviceFlexMotionSimulation;
import uq.ilabs.library.labequipment.devices.DeviceST360Counter;
import uq.ilabs.library.labequipment.devices.DeviceST360CounterSimulation;
import uq.ilabs.library.labequipment.devices.DeviceSerialLcd;
import uq.ilabs.library.labequipment.devices.DeviceSerialLcdSimulation;
import uq.ilabs.library.labequipment.drivers.DriverRadioactivity;
import uq.ilabs.library.labequipment.drivers.DriverSimActivity;
import uq.ilabs.library.labequipment.drivers.DriverSimActivityNoDelay;
import uq.ilabs.library.labequipment.engine.LabConsts;
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
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for exception messages
     */
    private static final String STRERR_InvalidDeviceType_arg2 = "Invalid Device Type: {%s} - {%s}";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private DeviceFlexMotion deviceFlexMotion;
    private DeviceST360Counter deviceST360Counter;
    private DeviceSerialLcd deviceSerialLcd;
    private DeviceFlexMotionSimulation deviceFlexMotionSimulation;
    private DeviceST360CounterSimulation deviceST360CounterSimulation;
    private DeviceSerialLcdSimulation deviceSerialLcdSimulation;
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
             * Create instances of the simulation devices to be used by the simulation drivers
             */
            this.deviceFlexMotionSimulation = new DeviceFlexMotionSimulation(this.labEquipmentConfiguration);
            this.deviceST360CounterSimulation = new DeviceST360CounterSimulation(this.labEquipmentConfiguration);
            this.deviceSerialLcdSimulation = new DeviceSerialLcdSimulation(this.labEquipmentConfiguration);

            /*
             * Determine FlexMotion device to use
             */
            Document document = XmlUtilities.GetDocumentFromString(this.labEquipmentConfiguration.GetXmlDeviceConfiguration(DeviceFlexMotion.ClassName()));
            Node nodeDevice = XmlUtilities.GetRootNode(document, LabConsts.STRXML_Device);
            String deviceType = XmlUtilities.GetChildValue(nodeDevice, Consts.STRXML_Type);

            /*
             * Create instance of the FlexMotion device to use
             */
            switch (deviceType) {
                case Consts.STRXML_TypeSimulation:
                    this.deviceFlexMotion = this.deviceFlexMotionSimulation;
                    break;

                default:
                    throw new RuntimeException(String.format(STRERR_InvalidDeviceType_arg2, DeviceFlexMotion.ClassName(), deviceType));
            }

            /*
             * Determine ST360Counter device to use
             */
            document = XmlUtilities.GetDocumentFromString(this.labEquipmentConfiguration.GetXmlDeviceConfiguration(DeviceST360Counter.ClassName()));
            nodeDevice = XmlUtilities.GetRootNode(document, LabConsts.STRXML_Device);
            deviceType = XmlUtilities.GetChildValue(nodeDevice, Consts.STRXML_Type);

            /*
             * Create instance of the ST360Counter device to use
             */
            switch (deviceType) {
                case Consts.STRXML_TypeSimulation:
                    this.deviceST360Counter = this.deviceST360CounterSimulation;
                    break;

                default:
                    throw new RuntimeException(String.format(STRERR_InvalidDeviceType_arg2, DeviceST360Counter.ClassName(), deviceType));
            }

            /*
             * Determine DeviceSerialLcd device to use
             */
            document = XmlUtilities.GetDocumentFromString(this.labEquipmentConfiguration.GetXmlDeviceConfiguration(DeviceSerialLcd.ClassName()));
            nodeDevice = XmlUtilities.GetRootNode(document, LabConsts.STRXML_Device);
            deviceType = XmlUtilities.GetChildValue(nodeDevice, Consts.STRXML_Type);

            /*
             * Create instance of the DeviceSerialLcd device to use
             */
            switch (deviceType) {
                case Consts.STRXML_TypeNone:
                    this.deviceSerialLcd = new DeviceSerialLcd(this.labEquipmentConfiguration);
                    break;

                case Consts.STRXML_TypeSimulation:
                    this.deviceSerialLcd = this.deviceSerialLcdSimulation;
                    break;

                default:
                    throw new RuntimeException(String.format(STRERR_InvalidDeviceType_arg2, DeviceSerialLcd.ClassName(), deviceType));
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
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

        DriverGeneric driverGeneric = null;

        /*
         * Create an instance of the driver for the specified setup Id
         */
        switch (setupId) {
            case Consts.STRXML_SetupId_RadioactivityVsTime:
            case Consts.STRXML_SetupId_RadioactivityVsDistance:
                driverGeneric = new DriverRadioactivity(this.labEquipmentConfiguration);
                break;

            case Consts.STRXML_SetupId_SimActivityVsTime:
            case Consts.STRXML_SetupId_SimActivityVsDistance:
                driverGeneric = new DriverSimActivity(this.labEquipmentConfiguration);
                break;

            case Consts.STRXML_SetupId_SimActivityVsTimeNoDelay:
            case Consts.STRXML_SetupId_SimActivityVsDistanceNoDelay:
                driverGeneric = new DriverSimActivityNoDelay(this.labEquipmentConfiguration);
                break;
        }

        /*
         * If a driver instance was created, set the devices in the driver instance
         */
        if (driverGeneric != null) {
            switch (setupId) {
                case Consts.STRXML_SetupId_RadioactivityVsTime:
                case Consts.STRXML_SetupId_RadioactivityVsDistance:
                    ((DriverRadioactivity) driverGeneric).setDeviceFlexMotion(this.deviceFlexMotion);
                    ((DriverRadioactivity) driverGeneric).setDeviceST360Counter(this.deviceST360Counter);
                    ((DriverRadioactivity) driverGeneric).setDeviceSerialLcd(this.deviceSerialLcd);
                    break;

                case Consts.STRXML_SetupId_SimActivityVsTime:
                case Consts.STRXML_SetupId_SimActivityVsDistance:
                case Consts.STRXML_SetupId_SimActivityVsTimeNoDelay:
                case Consts.STRXML_SetupId_SimActivityVsDistanceNoDelay:
                    ((DriverRadioactivity) driverGeneric).setDeviceFlexMotion(this.deviceFlexMotionSimulation);
                    ((DriverRadioactivity) driverGeneric).setDeviceST360Counter(this.deviceST360CounterSimulation);
                    ((DriverRadioactivity) driverGeneric).setDeviceSerialLcd(this.deviceSerialLcdSimulation);
                    break;
            }
        } else {
            driverGeneric = super.GetDriver(setupId);
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

        boolean success = false;

        try {
            /*
             * Nothing to do here
             */

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
    @Override
    protected boolean InitialiseEquipment() {
        final String methodName = "InitialiseEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Initialise the equipment devices
             */
            if (this.deviceFlexMotion.Initialise() == false) {
                throw new RuntimeException(this.deviceFlexMotion.getLastError());
            }
            if (this.deviceST360Counter.Initialise() == false) {
                throw new RuntimeException(this.deviceST360Counter.getLastError());
            }
            if (this.deviceSerialLcd.Initialise() == false) {
                throw new RuntimeException(this.deviceSerialLcd.getLastError());
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
    @Override
    protected boolean PowerdownEquipment() {
        final String methodName = "PowerdownEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Nothing to do here
             */

            success = true;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }
}
