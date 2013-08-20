/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient.engine;

import uq.ilabs.library.labclient.ServiceBrokerAPI;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labclient.engine.types.SetupInfo;

/**
 *
 * @author uqlpayne
 */
public class LabClientSession implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabClientSession.class.getName();
    private static final Level logLevel = Level.INFO;
    /*
     * String constants
     */
    private static final String STR_MinutesAnd_arg2 = "%d minute%s and ";
    private static final String STR_Seconds_arg2 = "%d second%s";
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_TitleVersion_arg2 = "Title: %s  Version: %s";
    private static final String STRLOG_PhotoUrl_arg = "Photo Url: %s";
    private static final String STRLOG_LabCameraUrl_arg = "LabCamera Url: %s";
    private static final String STRLOG_LabInfoUrl_arg = "LabInfo Url: %s";
    private static final String STRLOG_SetupIDName_arg = "Setup Id: %s Name: %s";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String labCameraUrl;
    private String labInfoText;
    private String labInfoUrl;
    private String navmenuPhotoUrl;
    private String title;
    private String version;
    private String xmlConfiguration;
    private String xmlSpecification;
    private String xmlValidation;
    private HashMap<String, SetupInfo> setupInfoMap;
    private String[] setupNames;
    private int[] submittedIds;
    private int[] completedIds;
    private String feedbackEmailUrl;
    private boolean multiSubmit;
    private ServiceBrokerAPI serviceBrokerAPI;

    public String getLabCameraUrl() {
        return labCameraUrl;
    }

    public String getLabInfoText() {
        return labInfoText;
    }

    public String getLabInfoUrl() {
        return labInfoUrl;
    }

    public String getNavmenuPhotoUrl() {
        return navmenuPhotoUrl;
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

    public String getXmlSpecification() {
        return xmlSpecification;
    }

    public String getXmlValidation() {
        return xmlValidation;
    }

    public HashMap<String, SetupInfo> getSetupInfoMap() {
        return setupInfoMap;
    }

    public String[] getSetupNames() {
        return setupNames;
    }

    public int[] getSubmittedIds() {
        return submittedIds;
    }

    public int[] getCompletedIds() {
        return completedIds;
    }

    public String getFeedbackEmailUrl() {
        return feedbackEmailUrl;
    }

    public void setFeedbackEmailUrl(String feedbackEmailUrl) {
        this.feedbackEmailUrl = feedbackEmailUrl;
    }

    public boolean isMultiSubmit() {
        return multiSubmit;
    }

    public void setMultiSubmit(boolean multiSubmit) {
        this.multiSubmit = multiSubmit;
    }

    public ServiceBrokerAPI getServiceBrokerAPI() {
        return serviceBrokerAPI;
    }

    public void setServiceBrokerAPI(ServiceBrokerAPI serviceBrokerAPI) {
        this.serviceBrokerAPI = serviceBrokerAPI;
    }
    //</editor-fold>

    /**
     *
     * @param labClientSession
     * @param xmlLabConfiguration
     */
    public void ParseLabConfiguration(String xmlLabConfiguration) {
        final String methodName = "ParseLabConfiguration";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            String logMessage = Logfile.STRLOG_Newline;

            /*
             * Load the lab configuration XML document from the string
             */
            Document document = XmlUtilities.GetDocumentFromString(xmlLabConfiguration);
            Node nodeLabConfiguration = XmlUtilities.GetRootNode(document, LabConsts.STRXML_LabConfiguration);

            /*
             * Get information from the lab configuration node
             */
            this.title = XmlUtilities.GetAttributeValue(nodeLabConfiguration, LabConsts.STRXML_ATTR_Title);
            this.version = XmlUtilities.GetAttributeValue(nodeLabConfiguration, LabConsts.STRXML_ATTR_Version);
            logMessage += String.format(STRLOG_TitleVersion_arg2, this.title, this.version) + Logfile.STRLOG_Newline;

            Node nodeNavmenuPhoto = XmlUtilities.GetChildNode(nodeLabConfiguration, LabConsts.STRXML_NavmenuPhoto);
            this.navmenuPhotoUrl = XmlUtilities.GetChildValue(nodeNavmenuPhoto, LabConsts.STRXML_Image);
            logMessage += String.format(STRLOG_PhotoUrl_arg, this.navmenuPhotoUrl) + Logfile.STRLOG_Newline;

            Node nodeLabCamera = XmlUtilities.GetChildNode(nodeLabConfiguration, LabConsts.STRXML_LabCamera, false);
            if (nodeLabCamera != null) {
                String url = XmlUtilities.GetChildValue(nodeLabCamera, LabConsts.STRXML_Url, false);
                if (url != null && url.isEmpty() == false) {
                    this.labCameraUrl = url;
                }
            }
            logMessage += String.format(STRLOG_LabCameraUrl_arg, this.labCameraUrl) + Logfile.STRLOG_Newline;

            Node nodeLabInfo = XmlUtilities.GetChildNode(nodeLabConfiguration, LabConsts.STRXML_LabInfo, false);
            if (nodeLabInfo != null) {
                String url = XmlUtilities.GetChildValue(nodeLabInfo, LabConsts.STRXML_Url, false);
                if (url != null && url.isEmpty() == false) {
                    this.labInfoUrl = url;
                }
            }
            logMessage += String.format(STRLOG_LabInfoUrl_arg, this.labInfoUrl) + Logfile.STRLOG_Newline;

            /*
             * Get the configuration node and save as an XML string
             */
            Node nodeConfiguration = XmlUtilities.GetChildNode(nodeLabConfiguration, LabConsts.STRXML_Configuration);
            DocumentFragment documentFragment = document.createDocumentFragment();
            documentFragment.appendChild(nodeConfiguration.cloneNode(true));
            this.xmlConfiguration = XmlUtilities.ToXmlString(documentFragment);

            /*
             * Get a list of all setups, must have at least one. Also, keep a seperate list of setup names so that they
             * appear in the same order as in the configuration
             */
            ArrayList nodeList = XmlUtilities.GetChildNodeList(nodeConfiguration, LabConsts.STRXML_Setup);
            this.setupInfoMap = new HashMap<>();
            this.setupNames = new String[nodeList.size()];
            for (int i = 0; i < nodeList.size(); i++) {
                Node nodeSetup = (Node) nodeList.get(i);

                /*
                 * Get the setup Id and name
                 */
                String setupId = XmlUtilities.GetAttributeValue(nodeSetup, LabConsts.STRXML_ATTR_Id);
                String setupName = XmlUtilities.GetChildValue(nodeSetup, LabConsts.STRXML_Name);

                /*
                 * Get the setup description
                 */
                Node nodeDescription = XmlUtilities.GetChildNode(nodeSetup, LabConsts.STRXML_Description);
                documentFragment = document.createDocumentFragment();
                documentFragment.appendChild(nodeDescription.cloneNode(true));
                String setupDescription = XmlUtilities.ToXmlString(documentFragment);

                /*
                 * Add setup information to the list
                 */
                SetupInfo setupInfo = new SetupInfo(setupId);
                setupInfo.setName(setupName);
                setupInfo.setDescription(setupDescription);
                setupInfo.setXmlSetup(XmlUtilities.ToXmlString(nodeSetup));
                this.setupInfoMap.put(setupName, setupInfo);
                this.setupNames[i] = setupName;
                logMessage += String.format(STRLOG_SetupIDName_arg, setupId, setupName) + Logfile.STRLOG_Newline;
            }

            Logfile.Write(logLevel, logMessage);

            /*
             * Get the experiment specification node and save as an XML string
             */
            Node nodeSpecification = XmlUtilities.GetChildNode(nodeLabConfiguration, LabConsts.STRXML_ExperimentSpecification);
            documentFragment = document.createDocumentFragment();
            documentFragment.appendChild(nodeSpecification.cloneNode(true));
            this.xmlSpecification = XmlUtilities.ToXmlString(documentFragment);

            /*
             * Get the validation node, if it exists, and save as an XML string
             */
            Node nodeValidation = XmlUtilities.GetChildNode(nodeLabConfiguration, LabConsts.STRXML_Validation, false);
            if (nodeValidation != null) {
                documentFragment = document.createDocumentFragment();
                documentFragment.appendChild(nodeValidation.cloneNode(true));
                this.xmlValidation = XmlUtilities.ToXmlString(documentFragment);
            }

        } catch (XmlUtilitiesException | DOMException ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param id
     */
    public void AddSubmittedId(int id) {
        /*
         * Check if multisubmit is enabled or submitted ids exists
         */
        if (multiSubmit == false || submittedIds == null) {
            /*
             * Create a new array of submitted ids and add the id
             */
            submittedIds = new int[]{id};
        } else {
            /*
             * Create a bigger array of submitted ids and add the id
             */
            int[] newSubmittedIds = Arrays.copyOf(submittedIds, submittedIds.length + 1);
            newSubmittedIds[submittedIds.length] = id;
            submittedIds = newSubmittedIds;
        }
    }

    /**
     *
     * @param id
     */
    public void DeleteSubmittedId(int id) {
        /*
         * Check if submitted id exists
         */
        if (submittedIds != null) {
            /*
             * Check if multisubmit is enabled or one submitted id exists
             */
            if (multiSubmit == false || (submittedIds.length == 1 && submittedIds[0] == id)) {
                submittedIds = null;
            } else {
                /*
                 * Find submitted id
                 */
                for (int i = 0; i < submittedIds.length; i++) {
                    if (submittedIds[i] == id) {
                        /*
                         * Create a smaller array of completed iIds
                         */
                        int[] newSubmittedIds = new int[submittedIds.length - 1];

                        /*
                         * Copy the ids to the new array excluding the id
                         */
                        System.arraycopy(submittedIds, 0, newSubmittedIds, 0, i);
                        System.arraycopy(submittedIds, i + 1, newSubmittedIds, i, newSubmittedIds.length - i);
                        submittedIds = newSubmittedIds;
                        break;
                    }
                }
            }
        }
    }

    /**
     *
     * @param id
     */
    public void AddCompletedId(int id) {
        /*
         * Check if multisubmit is enabled or completed ids exists
         */
        if (multiSubmit == false || completedIds == null) {
            /*
             * Create a new array of completed ids and add the id
             */
            completedIds = new int[]{id};
        } else {
            /*
             * Create a bigger array of completed ids and add the id
             */
            int[] newCompletedIds = Arrays.copyOf(completedIds, completedIds.length + 1);
            newCompletedIds[completedIds.length] = id;
            completedIds = newCompletedIds;
        }
    }

    /**
     *
     * @return
     */
    public String[] GetSubmittedIds() {
        String[] ids = null;

        if (submittedIds != null) {
            ids = new String[submittedIds.length];
            for (int i = 0; i < submittedIds.length; i++) {
                ids[i] = Integer.toString(submittedIds[i]);
            }
        }

        return ids;
    }

    /**
     *
     * @param seconds
     * @return
     */
    public String FormatTimeMessage(int seconds) {
        /*
         * Convert to minutes and seconds
         */
        int minutes = seconds / 60;
        seconds -= minutes * 60;

        String message = "";
        try {
            if (minutes > 0) {
                /* Display minutes */
                message += String.format(STR_MinutesAnd_arg2, minutes, FormatPlural(minutes));
            }
            /* Display seconds */
            message += String.format(STR_Seconds_arg2, seconds, FormatPlural(seconds));
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            message = ex.getMessage();
        }

        return message;
    }

    /**
     *
     * @param value
     * @return
     */
    private String FormatPlural(int value) {
        return (value == 1) ? "" : "s";
    }
}
