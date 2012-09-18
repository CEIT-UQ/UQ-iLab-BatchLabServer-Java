/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.drivers;

import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.Configuration;
import uq.ilabs.library.labserver.ExperimentResult;
import uq.ilabs.library.labserver.engine.drivers.DriverEquipmentGeneric;
import uq.ilabs.library.labserver.engine.types.LabEquipmentServiceInfo;

/**
 *
 * @author uqlpayne
 */
public class DriverEquipment extends DriverEquipmentGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DriverEquipment.class.getName();
    private static final Level logLevel = Level.FINE;
    //</editor-fold>

    /**
     *
     * @param labEquipmentServiceInfo
     * @throws Exception
     */
    public DriverEquipment(Configuration configuration, LabEquipmentServiceInfo labEquipmentServiceInfo) throws Exception {
        super(configuration, labEquipmentServiceInfo);

        final String methodName = "DriverEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Initialise local variables and properties
             */
            this.labExperimentResult = new ExperimentResult(configuration);
            if (this.labExperimentResult == null) {
                throw new NullPointerException(ExperimentResult.class.getSimpleName());
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
