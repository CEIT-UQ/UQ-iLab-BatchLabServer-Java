/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient.engine;

import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;

/**
 *
 * @author uqlpayne
 */
public class LabExperimentSpecification {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabExperimentSpecification.class.getName();
    private static final Level logLevel = Level.FINEST;
    /*
     * String constants for exception messages
     */
    private static final String STRERR_XmlSpecification = "xmlSpecification";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    protected Node nodeSpecification;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    protected String setupName;
    protected String setupId;

    public String getSetupName() {
        return setupName;
    }

    public void setSetupName(String setupName) {
        this.setupName = setupName;
    }

    public String getSetupId() {
        return setupId;
    }

    public void setSetupId(String setupId) {
        this.setupId = setupId;
    }
    //</editor-fold>

    /**
     *
     * @param xmlSpecification
     * @throws Exception
     */
    public LabExperimentSpecification(String xmlSpecification) throws Exception {
        final String methodName = "LabExperimentSpecification";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that parameters are valid
             */
            if (xmlSpecification == null) {
                throw new NullPointerException(STRERR_XmlSpecification);
            }
            if (xmlSpecification.trim().isEmpty()) {
                throw new IllegalArgumentException(STRERR_XmlSpecification);
            }

            /*
             * Load the experiment specification XML document from the string
             */
            Document document = XmlUtilities.GetDocumentFromString(xmlSpecification);
            Node nodeRoot = XmlUtilities.GetRootNode(document, LabConsts.STRXML_ExperimentSpecification);

            /*
             * Check that all required XML nodes exist
             */
            XmlUtilities.GetChildNode(nodeRoot, LabConsts.STRXML_SetupName);
            XmlUtilities.GetChildNode(nodeRoot, LabConsts.STRXML_SetupId);

            /*
             * Save a copy of the experiment specification for the derived class
             */
            this.nodeSpecification = nodeRoot.cloneNode(true);

        } catch (NullPointerException | IllegalArgumentException | XmlUtilitiesException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return String
     */
    protected String ToXmlString() {
        final String methodName = "ToXmlString";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String xmlString;

        try {
            /*
             * Add the lab experiment specification information to the XML document
             */
            XmlUtilities.SetChildValue(this.nodeSpecification, LabConsts.STRXML_SetupName, this.setupName);
            XmlUtilities.SetChildValue(this.nodeSpecification, LabConsts.STRXML_SetupId, this.setupId);

            /*
             * Convert the XML document to an XML string
             */
            xmlString = XmlUtilities.ToXmlString(this.nodeSpecification);
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            xmlString = null;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return xmlString;
    }
}
