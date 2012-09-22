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
    private String minimum;
    private String maximum;
    private String stepSize;

    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

    public String getMaximum() {
        return maximum;
    }

    public void setMaximum(String maximum) {
        this.maximum = maximum;
    }

    public String getStepSize() {
        return stepSize;
    }

    public void setStepSize(String stepSize) {
        this.stepSize = stepSize;
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

        /*
         * Check that all required XML nodes exist
         */
        XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_SpeedMin);
        XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_SpeedMax);
        XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_SpeedStep);
        XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_FieldMin);
        XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_FieldMax);
        XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_FieldStep);
        XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_LoadMin);
        XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_LoadMax);
        XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_LoadStep);

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
                switch (this.setupId) {
                    case Consts.STRXML_SetupId_VoltageVsSpeed:
                    case Consts.STRXML_SetupId_SpeedVsVoltage:
                        XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_SpeedMin, this.minimum);
                        XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_SpeedMax, this.maximum);
                        XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_SpeedStep, this.stepSize);
                        break;
                    case Consts.STRXML_SetupId_VoltageVsField:
                    case Consts.STRXML_SetupId_SpeedVsField:
                        XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_FieldMin, this.minimum);
                        XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_FieldMax, this.maximum);
                        XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_FieldStep, this.stepSize);
                        break;
                    case Consts.STRXML_SetupId_VoltageVsLoad:
                        XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_LoadMin, this.minimum);
                        XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_LoadMax, this.maximum);
                        XmlUtilities.SetChildValue(this.nodeSpecification, Consts.STRXML_LoadStep, this.stepSize);
                        break;
                }

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
