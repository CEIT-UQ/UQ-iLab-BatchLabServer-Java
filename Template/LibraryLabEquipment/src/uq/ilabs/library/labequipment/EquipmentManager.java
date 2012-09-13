/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment;

import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.engine.LabEquipmentManager;

/**
 *
 * @author uqlpayne
 */
public class EquipmentManager extends LabEquipmentManager {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = EquipmentManager.class.getName();
    private static final Level logLevel = Level.INFO;
    //</editor-fold>

    /**
     *
     * @param configurationFilename
     * @throws Exception
     */
    public EquipmentManager(String configurationFilename) throws Exception {
        super(configurationFilename);

        final String methodName = "EquipmentManager";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Nothing to do here
         */

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean Create() {
        final String methodName = "Create";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        /*
         * Create an instance of the equipment engine
         */
        try {
            this.labEquipmentEngine = new EquipmentEngine(this.labEquipmentConfiguration);
            if (this.labEquipmentEngine == null) {
                throw new NullPointerException(EquipmentEngine.class.getSimpleName());
            }

            success = true;
        } catch (Exception ex) {
            Logfile.WriteException(STR_ClassName, methodName, ex);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return success;
    }
}
