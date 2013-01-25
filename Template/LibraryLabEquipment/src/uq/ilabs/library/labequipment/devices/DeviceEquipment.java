/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.devices;

import java.util.Random;
import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;
import uq.ilabs.library.labequipment.engine.devices.DeviceGeneric;

/**
 *
 * @author uqlpayne
 */
public class DeviceEquipment extends DeviceGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DeviceEquipment.class.getName();
    private static final Level logLevel = Level.FINER;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Random random;
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DeviceEquipment(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration, DeviceEquipment.class.getSimpleName());

        final String methodName = "DeviceEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Nothiing to do here
             */
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
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
             * Create an instance of the random number generator
             */
            this.random = new Random();

            success = true;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            this.lastError = ex.getMessage();
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @param maxValue
     * @param value
     * @return boolean
     */
    public boolean GetRandom(int maxValue, int[] value) {
        final String methodName = "GetRandom";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Generate a random number in the range 0 to maxValue
             */
            value[0] = this.random.nextInt(maxValue);

            success = true;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            this.lastError = ex.getMessage();
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }
}
