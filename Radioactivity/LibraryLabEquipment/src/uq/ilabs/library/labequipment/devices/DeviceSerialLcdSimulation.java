/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.devices;

import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Delay;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;

/**
 *
 * @author uqlpayne
 */
public class DeviceSerialLcdSimulation extends DeviceSerialLcd {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DeviceSerialLcd.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants
     */
    private static final String STR_HardwareFirmwareVersion = "Simulation";
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_WriteLine_arg3 = "%s - %d:[%s]";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private boolean delaysSimulated;

    public boolean isDelaysSimulated() {
        return delaysSimulated;
    }

    public void setDelaysSimulated(boolean delaysSimulated) {
        this.delaysSimulated = delaysSimulated;
    }
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DeviceSerialLcdSimulation(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration);

        final String methodName = "DeviceSerialLcdSimulation";
        Logfile.WriteCalled(Level.CONFIG, STR_ClassName, methodName);

        try {
            /*
             * Initialise properties
             */
            this.delaysSimulated = true;

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(Level.CONFIG, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean Initialise() {
        final String methodName = "Initialise";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Get the firmware version and display
             */
            this.WriteLine(LineNumber.One, DeviceSerialLcd.ClassName());
            this.WriteLine(LineNumber.Two, this.GetHardwareFirmwareVersion());

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
     * @return String
     */
    @Override
    public String GetHardwareFirmwareVersion() {

        return STR_HardwareFirmwareVersion;
    }

    /**
     *
     * @return double
     */
    @Override
    public double GetWriteLineTime() {

        return this.writeLineTime;
    }

    /**
     *
     * @param lineno
     * @param message
     * @return boolean
     */
    @Override
    public boolean WriteLine(LineNumber lineno, String message) {
        /*
         * Write the message to the logfile
         */
        String line = String.format(STRLOG_WriteLine_arg3, DeviceSerialLcd.ClassName(), lineno.getValue(), (message != null) ? message : "");
        Logfile.Write(line);

        /*
         * Check if simulating delays
         */
        if (this.delaysSimulated == true) {
            Delay.MilliSeconds((int) (this.writeLineTime * 1000));
        }

        return true;
    }
}
