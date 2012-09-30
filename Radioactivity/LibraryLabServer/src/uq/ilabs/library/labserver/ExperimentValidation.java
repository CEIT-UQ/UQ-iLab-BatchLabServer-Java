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
    private int distanceMinimum;
    private int distanceMaximum;
    private int durationMinimum;
    private int durationMaximum;
    private int trialsMinimum;
    private int trialsMaximum;
    private int totaltimeMinimum;
    private int totaltimeMaximum;

    public int getDistanceMinimum() {
        return distanceMinimum;
    }

    public int getDistanceMaximum() {
        return distanceMaximum;
    }

    public int getDurationMinimum() {
        return durationMinimum;
    }

    public int getDurationMaximum() {
        return durationMaximum;
    }

    public int getTrialsMinimum() {
        return trialsMinimum;
    }

    public int getTrialsMaximum() {
        return trialsMaximum;
    }

    public int getTotaltimeMinimum() {
        return totaltimeMinimum;
    }

    public int getTotaltimeMaximum() {
        return totaltimeMaximum;
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
             * Get the minimum and maximum values allowed for distance
             */
            Node xmlNode = XmlUtilities.GetChildNode(this.nodeValidation, Consts.STRXML_VdnDistance);
            this.distanceMinimum = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Minimum);
            this.distanceMaximum = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Maximum);
            /*
             * Get the minimum and maximum values allowed for duration
             */
            xmlNode = XmlUtilities.GetChildNode(this.nodeValidation, Consts.STRXML_VdnDuration);
            this.durationMinimum = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Minimum);
            this.durationMaximum = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Maximum);
            /*
             * Get the minimum and maximum values allowed for trials
             */
            xmlNode = XmlUtilities.GetChildNode(this.nodeValidation, Consts.STRXML_VdnRepeat);
            this.trialsMinimum = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Minimum);
            this.trialsMaximum = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Maximum);
            /*
             * Get the minimum and maximum values allowed for total estimated exectuion time
             */
            xmlNode = XmlUtilities.GetChildNode(this.nodeValidation, Consts.STRXML_VdnTotaltime);
            this.totaltimeMinimum = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Minimum);
            this.totaltimeMaximum = XmlUtilities.GetChildValueAsInt(xmlNode, Consts.STRXML_Maximum);
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }
}
