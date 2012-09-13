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
public class DeviceEquipment extends DeviceGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DeviceEquipment.class.getName();
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DeviceEquipment(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration);

        final String methodName = "DeviceEquipment";
        Logfile.WriteCalled(Level.CONFIG, STR_ClassName, methodName);

        try {
            /*
             * YOUR CODE HERE
             */
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
        Logfile.WriteCalled(STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * YOUR CODE HERE
             */
            success = super.Initialise();

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }
}
