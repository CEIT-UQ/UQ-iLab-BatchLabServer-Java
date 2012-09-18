/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.engine.LabExperimentManager;
import uq.ilabs.library.labserver.engine.LabManagement;

/**
 *
 * @author uqlpayne
 */
public class ExperimentManager extends LabExperimentManager {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentManager.class.getName();
    //</editor-fold>

    /**
     *
     * @param configurationFilename
     * @throws Exception
     */
    public ExperimentManager(LabManagement labManagement) throws Exception {
        super(labManagement);

        final String methodName = "ExperimentManager";
        Logfile.WriteCalled(STR_ClassName, methodName);

        /*
         * Nothing to do here
         */

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean Create() {
        final String methodName = "Create";
        Logfile.WriteCalled(STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Create local class instances just to check that all is in order
             */
            ExperimentResult experimentResult = new ExperimentResult((Configuration) this.labManagement.getLabConfiguration());
            if (experimentResult == null) {
                throw new NullPointerException(ExperimentResult.class.getSimpleName());
            }

            /*
             * Create instances of the experiment engines
             */
            this.labExperimentEngines = new ExperimentEngine[this.farmSize];
            if (this.labExperimentEngines == null) {
                throw new NullPointerException(ExperimentEngine.class.getSimpleName() + "[]");
            }
            for (int unitId = 0; unitId < this.farmSize; unitId++) {
                this.labExperimentEngines[unitId] = new ExperimentEngine(unitId, labManagement);
                if (this.labExperimentEngines[unitId] == null) {
                    throw new NullPointerException(ExperimentEngine.class.getSimpleName());
                }
            }

            success = true;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }
}
