/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;

/**
 *
 * @author payne
 */
public class LabConfiguration {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabConfiguration.class.getName();
    private static final Level logLevel = Level.CONFIG;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Filename_arg = "Filename: '%s'";
    private static final String STRLOG_ParsingLabConfiguration = "Parsing LabConfiguration...";
    private static final String STRLOG_Title_arg = "Title: '%s'";
    private static final String STRLOG_Version_arg = "Version: '%s'";
    private static final String STRLOG_PhotoUrl_arg = "Photo Url: '%s'";
    private static final String STRLOG_CameraUrl_arg = "Camera Url: '%s'";
    private static final String STRLOG_LabInfoUrl_arg = "LabInfo Url: '%s'";
    private static final String STRLOG_SetupIdName_arg = "Setup Id: '%s' Name: '%s'";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_XmlLabConfiguration = "xmlLabConfiguration";
    private static final String STRERR_Filename = "filename";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String filename;
    private String title;
    private String version;
    private String photoUrl;
    private String cameraUrl;
    private String labInfoUrl;
    private String xmlConfiguration;
    private String xmlExperimentSpecification;
    private String xmlExperimentResult;
    private String xmlValidation;
    private Properties setupNames;

    public String getCameraUrl() {
        return cameraUrl;
    }

    public String getFilename() {
        return filename;
    }

    public String getLabInfoUrl() {
        return labInfoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Properties getSetupNames() {
        return setupNames;
    }

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public String getXmlConfiguration() {
        return xmlConfiguration;
    }

    public String getXmlExperimentResult() {
        return xmlExperimentResult;
    }

    public String getXmlExperimentSpecification() {
        return xmlExperimentSpecification;
    }

    public String getXmlValidation() {
        return xmlValidation;
    }
    //</editor-fold>

    /**
     * Constructor - Parse the lab configuration XML string for information specific to the LabServer.
     *
     * @param xmlLabConfiguration The string containing the XML to parse.
     */
    public LabConfiguration(String xmlLabConfiguration) throws Exception {
        this(null, null, xmlLabConfiguration);
    }

    /**
     * Constructor - Parse the lab configuration XML file for information specific to the LabServer.
     *
     * @param filepath Path to the lab configuration XML file.
     * @param filename Name of the lab configuration XML file.
     */
    public LabConfiguration(String filepath, String filename) throws Exception {
        this(filepath, filename, null);
    }

    /**
     * Constructor - Parse the lab configuration for information specific to the LabServer.
     *
     * @param filepath Path to the lab configuration XML file.
     * @param filename Name of the lab configuration XML file.
     * @param xmlLabConfiguration The string containing the XML to parse.
     */
    public LabConfiguration(String filepath, String filename, String xmlLabConfiguration) throws Exception {
        final String methodName = "LabConfiguration";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String logMessage = Logfile.STRLOG_Newline;
        Document document;

        try {
            /*
             * Check if an XML lab configuration string is specified
             */
            if (xmlLabConfiguration != null) {
                if (xmlLabConfiguration.trim().isEmpty()) {
                    throw new IllegalArgumentException(STRERR_XmlLabConfiguration);
                }
                /*
                 * Load the lab configuration XML document from the string
                 */
                document = XmlUtilities.GetDocumentFromString(xmlLabConfiguration);
            } else {
                /*
                 * Check that a filename is specified
                 */
                if (filename == null) {
                    throw new NullPointerException(STRERR_Filename);
                }
                if (filename.trim().isEmpty()) {
                    throw new IllegalArgumentException(STRERR_Filename);
                }

                /*
                 * Combine the file path and name and check if the file exists
                 */
                File file = new File(filepath, filename);
                if (file.exists() == false) {
                    throw new FileNotFoundException(file.getAbsolutePath());
                }

                this.filename = file.getAbsolutePath();
                logMessage += String.format(STRLOG_Filename_arg, this.filename) + Logfile.STRLOG_Newline;

                /*
                 * Load the lab configuration XML document from the file
                 */
                document = XmlUtilities.GetDocumentFromFile(filepath, filename);
            }

            /*
             * Get the document's root node
             */
            Node nodeLabConfiguration = XmlUtilities.GetRootNode(document, LabConsts.STRXML_LabConfiguration);

            logMessage += STRLOG_ParsingLabConfiguration + Logfile.STRLOG_Newline;

            /*
             * Get information from the lab configuration node
             */
            this.title = XmlUtilities.GetAttribute(nodeLabConfiguration, LabConsts.STRXML_ATTR_Title, false);
            logMessage += String.format(STRLOG_Title_arg, this.title) + Logfile.STRLOG_Newline;

            this.version = XmlUtilities.GetAttribute(nodeLabConfiguration, LabConsts.STRXML_ATTR_Version, false);
            logMessage += String.format(STRLOG_Version_arg, this.version) + Logfile.STRLOG_Newline;

            Node nodeNavmenuPhoto = XmlUtilities.GetChildNode(nodeLabConfiguration, LabConsts.STRXML_NavmenuPhoto, false);
            this.photoUrl = XmlUtilities.GetChildValue(nodeNavmenuPhoto, LabConsts.STRXML_Image, true);
            logMessage += String.format(STRLOG_PhotoUrl_arg, this.photoUrl) + Logfile.STRLOG_Newline;

            Node nodeLabCamera = XmlUtilities.GetChildNode(nodeLabConfiguration, LabConsts.STRXML_LabCamera, false);
            this.cameraUrl = XmlUtilities.GetChildValue(nodeLabCamera, LabConsts.STRXML_Url, true);
            logMessage += String.format(STRLOG_CameraUrl_arg, this.cameraUrl) + Logfile.STRLOG_Newline;

            Node nodeLabInfo = XmlUtilities.GetChildNode(nodeLabConfiguration, LabConsts.STRXML_LabInfo, false);
            this.labInfoUrl = XmlUtilities.GetChildValue(nodeLabInfo, LabConsts.STRXML_Url, true);
            logMessage += String.format(STRLOG_LabInfoUrl_arg, this.labInfoUrl) + Logfile.STRLOG_Newline;

            /*
             * Get the configuration node and save as an XML string
             */
            Node nodeConfiguration = XmlUtilities.GetChildNode(nodeLabConfiguration, LabConsts.STRXML_Configuration, false);
            DocumentFragment documentFragment = document.createDocumentFragment();
            documentFragment.appendChild(nodeConfiguration.cloneNode(true));
            this.xmlConfiguration = XmlUtilities.ToXmlString(documentFragment);

            /*
             * Get a list of all setups, must have at least one
             */
            this.setupNames = new Properties();
            ArrayList nodeList = XmlUtilities.GetChildNodeList(nodeConfiguration, LabConsts.STRXML_Setup);
            for (int i = 0; i < nodeList.size(); i++) {
                Node nodeSetup = (Node) nodeList.get(i);

                /*
                 * Get the setup information
                 */
                String setupId = XmlUtilities.GetAttribute(nodeSetup, LabConsts.STRXML_ATTR_Id, false);
                String setupName = XmlUtilities.GetChildValue(nodeSetup, LabConsts.STRXML_Name, false);
                this.setupNames.put(setupId, setupName);
                logMessage += String.format(STRLOG_SetupIdName_arg, setupId, setupName) + Logfile.STRLOG_Newline;
            }

            /*
             * Get the experiment specification node and save as an XML string
             */
            Node nodeSpecification = XmlUtilities.GetChildNode(nodeLabConfiguration, LabConsts.STRXML_ExperimentSpecification);
            documentFragment = document.createDocumentFragment();
            documentFragment.appendChild(nodeSpecification.cloneNode(true));
            this.xmlExperimentSpecification = XmlUtilities.ToXmlString(documentFragment);

            /*
             * Get the experiment result node and save as an XML string
             */
            Node nodeResult = XmlUtilities.GetChildNode(nodeLabConfiguration, LabConsts.STRXML_ExperimentResult);
            documentFragment = document.createDocumentFragment();
            documentFragment.appendChild(nodeResult.cloneNode(true));
            this.xmlExperimentResult = XmlUtilities.ToXmlString(documentFragment);

            /*
             * Get the validation node, if it exists, and save as an XML string
             */
            Node nodeValidation = XmlUtilities.GetChildNode(nodeLabConfiguration, LabConsts.STRXML_Validation, false);
            if (nodeValidation != null) {
                documentFragment = document.createDocumentFragment();
                documentFragment.appendChild(nodeValidation.cloneNode(true));
                this.xmlValidation = XmlUtilities.ToXmlString(documentFragment);
            }

            Logfile.Write(logLevel, logMessage);
        } catch (XmlUtilitiesException | NullPointerException | IllegalArgumentException | FileNotFoundException | DOMException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
