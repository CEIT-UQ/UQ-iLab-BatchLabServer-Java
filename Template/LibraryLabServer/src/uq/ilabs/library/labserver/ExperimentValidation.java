/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.labserver.engine.LabExperimentValidation;

/**
 *
 * @author uqlpayne
 */
public class ExperimentValidation extends LabExperimentValidation {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentValidation.class.getName();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private int someParameterMin;
    private int someParameterMax;

    public int getSomeParameterMin() {
        return someParameterMin;
    }

    public int getSomeParameterMax() {
        return someParameterMax;
    }
    //</editor-fold>

    /**
     *
     * @param xmlValidation
     * @throws Exception
     */
    public ExperimentValidation(String xmlValidation) throws Exception {
        super(xmlValidation);

        final String methodName = "ExperimentValidation";
        Logfile.WriteCalled(STR_ClassName, methodName);

        try {
            /*
             * Get the minimum and maximum values allowed for 'SomeParameter'
             */
            Node xmlNode = XmlUtilities.GetChildNode(this.nodeValidation, Consts.STRXML_SomeParameter);
            this.someParameterMin = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Min);
            this.someParameterMax = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Max);
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }
}
