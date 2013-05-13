/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine;

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
    private static final Level logLevel = Level.FINER;
    /*
     * String constants for exception messages
     */
    private static final String STRERR_XmlSpecification = "xmlSpecification";
    //
    protected static final String STRERR_InvalidSetupId_arg = "Invalid SetupId: %s";
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
             * Get the setup name, it may not exist
             */
            this.setupName = XmlUtilities.GetChildValue(nodeRoot, LabConsts.STRXML_SetupName, false);

            /*
             * Get the setup Id and check that it exists - search is case-sensitive
             */
            this.setupId = XmlUtilities.GetChildValue(nodeRoot, LabConsts.STRXML_SetupId);

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
     * @return
     */
    @Override
    public String toString() {
        String xmlString = null;

        try {
            xmlString = XmlUtilities.ToXmlString(this.nodeSpecification);
        } catch (XmlUtilitiesException ex) {
        }

        return xmlString;
    }
}
