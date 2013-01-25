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
public class LabExperimentValidation {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabExperimentValidation.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants for exception messages
     */
    private static final String STRERR_XmlValidation = "xmlValidation";
    /*
     * XML elements in the LabConfiguration.xml file
     */
    private static final String STRXML_validation = "validation";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    protected Node nodeValidation;
    //</editor-fold>

    /**
     *
     * @param xmlValidation
     * @throws Exception
     */
    public LabExperimentValidation(String xmlValidation) throws Exception {
        final String methodName = "LabExperimentValidation";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that parameters are valid
             */
            if (xmlValidation == null) {
                throw new NullPointerException(STRERR_XmlValidation);
            }
            if (xmlValidation.trim().isEmpty()) {
                throw new IllegalArgumentException(STRERR_XmlValidation);
            }

            /*
             * Load the experiment validation XML document from the string in the lab configuration
             */
            Document document = XmlUtilities.GetDocumentFromString(xmlValidation);
            Node nodeRoot = XmlUtilities.GetRootNode(document, STRXML_validation);

            /*
             * Save a copy of the node
             */
            this.nodeValidation = nodeRoot.cloneNode(true);

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
            xmlString = XmlUtilities.ToXmlString(this.nodeValidation);
        } catch (XmlUtilitiesException ex) {
        }

        return xmlString;
    }
}
