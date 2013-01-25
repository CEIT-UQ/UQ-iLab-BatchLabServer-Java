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
    private static final String STRERR_Distance = "Distance";
    private static final String STRERR_Duration = "Duration";
    private static final String STRERR_Repeat = "Repeat";
    private static final String STRERR_ValueLessThanMinimum_arg2 = "%s: Less than minimum (%d)!";
    private static final String STRERR_ValueGreaterThanMaximum_arg2 = "%s: Greater than maximum (%d)!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private int distanceMin;
    private int distanceMax;
    private int durationMin;
    private int durationMax;
    private int repeatMin;
    private int repeatMax;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">

    public static String ClassName() {
        return ExperimentValidation.class.getSimpleName();
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
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Get the minimum and maximum values allowed for 'Distance'
             */
            Node xmlNode = XmlUtilities.GetChildNode(this.nodeValidation, Consts.STRXML_VdnDistance);
            this.distanceMin = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_VdnMinimum);
            this.distanceMax = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_VdnMaximum);

            /*
             * Get the minimum and maximum values allowed for 'Duration'
             */
            xmlNode = XmlUtilities.GetChildNode(this.nodeValidation, Consts.STRXML_VdnDuration);
            this.durationMin = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_VdnMinimum);
            this.durationMax = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_VdnMaximum);

            /*
             * Get the minimum and maximum values allowed for 'Repeat'
             */
            xmlNode = XmlUtilities.GetChildNode(this.nodeValidation, Consts.STRXML_VdnRepeat);
            this.repeatMin = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_VdnMinimum);
            this.repeatMax = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_VdnMaximum);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param distance
     * @throws Exception
     */
    public void ValidateDistance(int distance) throws Exception {
        if (distance < this.distanceMin) {
            throw new IllegalArgumentException(String.format(STRERR_ValueLessThanMinimum_arg2, STRERR_Distance, this.distanceMin));
        }
        if (distance > this.distanceMax) {
            throw new IllegalArgumentException(String.format(STRERR_ValueGreaterThanMaximum_arg2, STRERR_Distance, this.distanceMax));
        }
    }

    /**
     *
     * @param distance
     * @throws Exception
     */
    public void ValidateDuration(int distance) throws Exception {
        if (distance < this.durationMin) {
            throw new IllegalArgumentException(String.format(STRERR_ValueLessThanMinimum_arg2, STRERR_Duration, this.durationMin));
        }
        if (distance > this.durationMax) {
            throw new IllegalArgumentException(String.format(STRERR_ValueGreaterThanMaximum_arg2, STRERR_Duration, this.durationMax));
        }
    }

    /**
     *
     * @param repeat
     * @throws Exception
     */
    public void ValidateRepeat(int repeat) throws Exception {
        if (repeat < this.repeatMin) {
            throw new IllegalArgumentException(String.format(STRERR_ValueLessThanMinimum_arg2, STRERR_Repeat, this.repeatMin));
        }
        if (repeat > this.repeatMax) {
            throw new IllegalArgumentException(String.format(STRERR_ValueGreaterThanMaximum_arg2, STRERR_Repeat, this.repeatMax));
        }
    }
}
