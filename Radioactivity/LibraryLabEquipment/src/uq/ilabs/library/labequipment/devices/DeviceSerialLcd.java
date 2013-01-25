/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.devices;

import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.labequipment.Consts;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;
import uq.ilabs.library.labequipment.engine.devices.DeviceGeneric;

/**
 *
 * @author uqlpayne
 */
public class DeviceSerialLcd extends DeviceGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DeviceSerialLcd.class.getName();
    private static final Level logLevel = Level.FINER;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    protected double writeLineTime;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">

    public static String ClassName() {
        return DeviceSerialLcd.class.getSimpleName();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Types">

    public enum LineNumber {

        One(1),
        Two(2);
        //
        private final int value;

        public int getValue() {
            return value;
        }

        private LineNumber(int value) {
            this.value = value;
        }
    }
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DeviceSerialLcd(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration, DeviceSerialLcd.ClassName());

        final String methodName = "DeviceSerialLcd";
        Logfile.WriteCalled(Level.CONFIG, STR_ClassName, methodName);

        try {
            /*
             * WriteLine time
             */
            this.writeLineTime = XmlUtilities.GetChildValueAsDouble(this.xmlNodeDevice, Consts.STRXML_WriteLineTime);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(Level.CONFIG, STR_ClassName, methodName);
    }

    /**
     *
     * @return String
     */
    public String GetHardwareFirmwareVersion() {
        return null;
    }

    /**
     *
     * @return double
     */
    public double GetWriteLineTime() {
        return 0.0;
    }

    /**
     *
     * @param lineno
     * @param message
     * @return boolean
     */
    public boolean WriteLine(LineNumber lineno, String message) {
        return true;
    }
}
