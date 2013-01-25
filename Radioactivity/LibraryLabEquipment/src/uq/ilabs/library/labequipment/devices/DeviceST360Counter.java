/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.devices;

import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;
import uq.ilabs.library.labequipment.engine.devices.DeviceGeneric;

/**
 *
 * @author uqlpayne
 */
public class DeviceST360Counter extends DeviceGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DeviceST360Counter.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_DistanceDuration_arg2 = "Distance: %d  Duration: %d";
    private static final String STRLOG_Data_arg = "Data: %d";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">

    public static String ClassName() {
        return DeviceST360Counter.class.getSimpleName();
    }
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DeviceST360Counter(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration, DeviceST360Counter.ClassName());

        final String methodName = "DeviceST360Counter";
        Logfile.WriteCalled(Level.CONFIG, STR_ClassName, methodName);

        try {
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(Level.CONFIG, STR_ClassName, methodName);
    }

    /**
     *
     * @param duration
     * @return
     */
    public double GetCaptureDataTime(int duration) {
        return 0.0;
    }

    /**
     *
     * @param distance
     * @param duration
     * @return
     */
    public int CaptureData(int distance, int duration) {
        return 0;
    }
}
