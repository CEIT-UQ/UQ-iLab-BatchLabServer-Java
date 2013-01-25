/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment;

import java.util.logging.Level;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.labequipment.engine.LabExperimentValidation;

/**
 *
 * @author uqlpayne
 */
public class ExperimentValidation extends LabExperimentValidation {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentValidation.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for exception messages
     */
    private static final String STRERR_SomeParameter = "SomeParameter";
    private static final String STRERR_ValueLessThanMinimum_arg2 = "%s: Less than minimum (%d)!";
    private static final String STRERR_ValueGreaterThanMaximum_arg2 = "%s: Greater than maximum (%d)!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private int someParameterMinimum;
    private int someParameterMaximum;
    //</editor-fold>

    /**
     *
     * @param xmlValidation
     * @throws Exception
     */
    public ExperimentValidation(String xmlValidation) throws Exception {
        super(xmlValidation);

        final String methodName = "ExperimentValidation";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Get the minimum and maximum values allowed for 'SomeParameter'
             */
            Node xmlNode = XmlUtilities.GetChildNode(this.nodeValidation, Consts.STRXML_SomeParameter);
            this.someParameterMinimum = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Minimum);
            this.someParameterMaximum = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Maximum);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param someParameter
     * @throws Exception
     */
    public void ValidateSomeParameter(int someParameter) throws Exception {
        if (someParameter < this.someParameterMinimum) {
            throw new IllegalArgumentException(String.format(STRERR_ValueLessThanMinimum_arg2, STRERR_SomeParameter, this.someParameterMinimum));
        }
        if (someParameter > this.someParameterMaximum) {
            throw new IllegalArgumentException(String.format(STRERR_ValueGreaterThanMaximum_arg2, STRERR_SomeParameter, this.someParameterMaximum));
        }
    }
}
