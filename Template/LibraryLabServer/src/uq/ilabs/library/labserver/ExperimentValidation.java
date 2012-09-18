/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.engine.LabExperimentValidation;

/**
 *
 * @author uqlpayne
 */
public class ExperimentValidation extends LabExperimentValidation {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentValidation.class.getName();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    //</editor-fold>

    public ExperimentValidation(String xmlValidation) throws Exception {
        super(xmlValidation);

        final String methodName = "ExperimentValidation";
        Logfile.WriteCalled(STR_ClassName, methodName);

        try {
            /*
             * YOUR CODE HERE
             */
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }
}
