/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.drivers.DriverEquipment;
import uq.ilabs.library.labserver.drivers.DriverSimulation;
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
        Logfile.WriteCalled(STR_ClassName, methodName);

        /*
         * Nothing to do here
         */

        Logfile.WriteCompleted(STR_ClassName, methodName);
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
            case Consts.STRXML_SetupId_Simulation:
                driverGeneric = new DriverSimulation((Configuration) this.labManagement.getLabConfiguration());
                break;
            case Consts.STRXML_SetupId_Equipment:
                driverGeneric = new DriverEquipment((Configuration) this.labManagement.getLabConfiguration(),
                        this.labManagement.getLabEquipmentServiceInfo()[this.unitId]);
                break;
            default:
                driverGeneric = super.GetDriver(setupId);
                break;
        }

        return driverGeneric;
    }
}
