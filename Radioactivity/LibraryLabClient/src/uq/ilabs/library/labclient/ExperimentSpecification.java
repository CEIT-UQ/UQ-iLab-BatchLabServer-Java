/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient;

import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.labclient.engine.LabExperimentSpecification;

/**
 *
 * @author uqlpayne
 */
public class ExperimentSpecification extends LabExperimentSpecification {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabExperimentSpecification.class.getName();
    private static final Level logLevel = Level.FINER;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String source;
    private String absorbers;
    private String distances;
    private String duration;
    private String trials;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAbsorbers() {
        return absorbers;
    }

    public void setAbsorbers(String absorbers) {
        this.absorbers = absorbers;
    }

    public String getDistances() {
        return distances;
    }

    public void setDistances(String distances) {
        this.distances = distances;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTrials() {
        return trials;
    }

    public void setTrials(String trials) {
        this.trials = trials;
    }
    //</editor-fold>

    /**
     *
     * @param xmlSpecification
     * @throws Exception
     */
    public ExperimentSpecification(String xmlSpecification) throws Exception {
        super(xmlSpecification);

        final String methodName = "ExperimentSpecification";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that all required XML nodes exist
             */
            XmlUtilities.GetChildNode(this.nodeSpecification, Consts.STRXML_SourceName);
            XmlUtilities.GetChildNode(this.nodeSpecification, Consts.STRXML_AbsorberName);
            XmlUtilities.GetChildNode(this.nodeSpecification, Consts.STRXML_Distance);
            XmlUtilities.GetChildNode(this.nodeSpecification, Consts.STRXML_Duration);
            XmlUtilities.GetChildNode(this.nodeSpecification, Consts.STRXML_Repeat);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return String
     */
    @Override
    public String ToXmlString() {
        final String methodName = "ToXmlString";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String xmlString = null;

        try {
            /*
             * Call super to create the XML document and add its part
             */
            if (super.ToXmlString() != null) {
                /*
                 * Add the experiment specification information to the XML document
                 */
                XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_SourceName, this.source);
                XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_AbsorberName, this.absorbers);
                XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_Distance, this.distances);
                XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_Duration, this.duration);
                XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_Repeat, this.trials);

                /*
                 * Convert the XML document to an XML string
                 */
                xmlString = XmlUtilities.ToXmlString(this.nodeSpecification);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            xmlString = null;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return xmlString;
    }
}
