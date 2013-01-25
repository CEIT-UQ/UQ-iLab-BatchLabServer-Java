/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.drivers.DriverEquipment;
import uq.ilabs.library.labserver.engine.LabExperimentEngine;
import uq.ilabs.library.labserver.engine.LabManagement;
import uq.ilabs.library.labserver.engine.drivers.DriverGeneric;

/**
 *
 * @author uqlpayne
 */
public class ExperimentEngine extends LabExperimentEngine {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentEngine.class.getName();
    private static final Level logLevel = Level.FINE;
    //</editor-fold>

    /**
     *
     * @param unitId
     * @param labManagement
     * @throws Exception
     */
    public ExperimentEngine(int unitId, LabManagement labManagement) throws Exception {
        super(unitId, labManagement);

        final String methodName = "ExperimentEngine";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Nothing to do here
             */
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

        DriverGeneric driverGeneric;

        /*
         * Create an instance of the driver for the specified setup Id
         */
        switch (setupId) {
            case Consts.STRXML_SetupId_OpenCircuitVaryField:
            case Consts.STRXML_SetupId_OpenCircuitVarySpeed:
            case Consts.STRXML_SetupId_ShortCircuitVaryField:
            case Consts.STRXML_SetupId_PreSynchronisation:
            case Consts.STRXML_SetupId_Synchronisation:
                driverGeneric = new DriverEquipment((Configuration) this.labManagement.getLabConfiguration(),
                        this.labManagement.getLabEquipmentServiceInfoList().get(this.unitId));
                break;
            default:
                driverGeneric = super.GetDriver(setupId);
                break;
        }

        return driverGeneric;
    }
}
