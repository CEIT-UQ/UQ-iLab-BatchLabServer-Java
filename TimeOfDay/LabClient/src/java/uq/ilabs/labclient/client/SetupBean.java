/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labclient.client;

import java.io.Serializable;
import java.util.logging.Level;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.types.ClientSubmissionReport;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.labclient.Consts;
import uq.ilabs.library.labclient.ExperimentSpecification;
import uq.ilabs.library.labclient.engine.LabClientSession;
import uq.ilabs.library.labclient.engine.types.SetupInfo;

/**
 *
 * @author uqlpayne
 */
@Named(value = "setupBean")
@SessionScoped
public class SetupBean implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = SetupBean.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants
     */
    private static final String STR_SpecificationValid_arg = "Specification is valid. Execution time will be %s.";
    private static final String STR_SubmissionSuccessful_arg2 = "Submission was successful. Experiment # is %d and execution time is %s.";
    private static final String STR_ExperimentNumberHasBeenSubmitted_arg = "Experiment %d has been submitted.";
    private static final String STR_ExperimentNumbersHaveBeenSubmitted_arg = "Experiments %s have been submitted.";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ValidationFailed = "Validation Failed!";
    private static final String STRERR_SubmissionFailed = "Submission Failed!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabClientSession labClientSession;
    private String setupId;
    private String oldSetupName;
    private String timeFormatDefault;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String[] setupNames;
    private String hsomSetupName;
    private String hotSetupDescription;
    private boolean hcbSubmitDisabled;
    private String holMessage;
    private String holMessageClass;
    private String hsomServerUrl;
    private String[] serverUrlList;
    private String hsomTimeFormat;
    private String[] timeFormatList;
    private boolean serverUrlListRendered;

    public String[] getSetupNames() {
        return setupNames;
    }

    public String getHsomSetupName() {
        return hsomSetupName;
    }

    public void setHsomSetupName(String hsomSetupName) {
        this.hsomSetupName = hsomSetupName;
    }

    public String getHotSetupDescription() {
        return hotSetupDescription;
    }

    public boolean isHcbSubmitDisabled() {
        return hcbSubmitDisabled;
    }

    public String getHolMessage() {
        return holMessage;
    }

    public String getHolMessageClass() {
        return holMessageClass;
    }

    public String getHsomServerUrl() {
        return hsomServerUrl;
    }

    public void setHsomServerUrl(String hsomServerUrl) {
        this.hsomServerUrl = hsomServerUrl;
    }

    public String[] getServerUrlList() {
        return serverUrlList;
    }

    public String getHsomTimeFormat() {
        return hsomTimeFormat;
    }

    public void setHsomTimeFormat(String hsomTimeFormat) {
        this.hsomTimeFormat = hsomTimeFormat;
    }

    public String[] getTimeFormatList() {
        return timeFormatList;
    }

    public boolean isServerUrlListRendered() {
        return serverUrlListRendered;
    }
    //</editor-fold>

    /**
     * Creates a new instance of SetupBean
     */
    public SetupBean() {
        final String methodName = "SetupBean";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        this.labClientSession = (LabClientSession) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Consts.STRSSN_LabClient);
        this.setupNames = this.labClientSession.getSetupNames();

        try {
            /*
             * Populate the time server dropdown list
             */
            Document document = XmlUtilities.GetDocumentFromString(this.labClientSession.getXmlConfiguration());
            Node nodeConfiguration = XmlUtilities.GetRootNode(document, Consts.STRXML_Configuration);
            Node node = XmlUtilities.GetChildNode(nodeConfiguration, Consts.STRXML_TimeServers);
            this.serverUrlList = XmlUtilities.GetChildValues(node, Consts.STRXML_Url);

            /*
             * Populate the time server dropdown list
             */
            node = XmlUtilities.GetChildNode(nodeConfiguration, Consts.STRXML_TimeFormats);
            this.timeFormatList = XmlUtilities.GetChildValues(node, Consts.STRXML_TimeFormat);
            this.timeFormatDefault = XmlUtilities.GetAttributeValue(node, Consts.STRXML_ATTR_Default);

        } catch (Exception ex) {
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     */
    public void pageLoad() {
        final String methodName = "pageLoad";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        if (FacesContext.getCurrentInstance().isPostback() == false) {
            /*
             * Not a postback, initialise page controls
             */
            this.ShowMessageInfo(null);
            this.hsomSetupName = this.setupNames[0];

            /*
             * Check if an experiment has been submitted
             */
            int[] submittedIds = this.labClientSession.getSubmittedIds();
            if (submittedIds != null) {
                if (submittedIds.length == 1) {
                    /*
                     * Show the one that has been submitted
                     */
                    this.ShowMessageInfo(String.format(STR_ExperimentNumberHasBeenSubmitted_arg, submittedIds[0]));
                } else if (submittedIds.length > 1) {
                    /*
                     * More than one has been submitted, show them all
                     */
                    String arg = "";
                    for (int i = 0; i < submittedIds.length; i++) {
                        if (i > 0) {
                            arg += ", ";
                        }
                        arg += Integer.toString(submittedIds[i]);
                    }
                    this.ShowMessageInfo(String.format(STR_ExperimentNumbersHaveBeenSubmitted_arg, arg));
                }
            } else {
                this.hcbSubmitDisabled = false;
            }
        }

        /*
         * Check if the setup has changed
         */
        if (this.hsomSetupName.equals(this.oldSetupName) == false) {
            this.PopulatePage();
            this.oldSetupName = this.hsomSetupName;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    public String actionValidate() {
        final String methodName = "actionValidate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Create the XML experiment specification string
             */
            String xmlSpecification = this.CreateXmlSpecification();

            /*
             * Validate the experiment specification
             */
            ValidationReport validationReport = this.labClientSession.getServiceBrokerAPI().Validate(xmlSpecification);
            if (validationReport == null) {
                throw new RuntimeException(STRERR_ValidationFailed);
            }

            if (validationReport.isAccepted() == false) {
                throw new RuntimeException(validationReport.getErrorMessage());
            }

            /*
             * Validation was accepted
             */
            String message = String.format(STR_SpecificationValid_arg, this.labClientSession.FormatTimeMessage((int) validationReport.getEstRuntime()));
            ShowMessageInfo(message);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            ShowMessageError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @return
     */
    public String actionSubmit() {
        final String methodName = "actionSubmit";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Create the XML experiment specification string
             */
            String xmlSpecification = this.CreateXmlSpecification();

            /*
             * Submit the experiment specification
             */
            ClientSubmissionReport clientSubmissionReport = this.labClientSession.getServiceBrokerAPI().Submit(xmlSpecification);
            if (clientSubmissionReport == null) {
                throw new RuntimeException(STRERR_SubmissionFailed);
            }

            /*
             * Check if submission was successful
             */
            ValidationReport validationReport = clientSubmissionReport.getValidationReport();
            if (validationReport.isAccepted() == false) {
                throw new RuntimeException(validationReport.getErrorMessage());
            }

            /*
             * Submission was accepted
             */
            String message = String.format(STR_SubmissionSuccessful_arg2,
                    clientSubmissionReport.getExperimentId(), this.labClientSession.FormatTimeMessage((int) validationReport.getEstRuntime()));
            ShowMessageInfo(message);

            /*
             * Update session with submitted id
             */
            this.labClientSession.AddSubmittedId(clientSubmissionReport.getExperimentId());
            if (this.labClientSession.isMultiSubmit() == false) {
                /*
                 * Disable futher submission
                 */
                this.hcbSubmitDisabled = true;
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            ShowMessageError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     */
    private void PopulatePage() {
        /*
         * Get the setup Id and update the description for the selected setup
         */
        SetupInfo setupInfo = this.labClientSession.getSetupInfoMap().get(this.hsomSetupName);
        this.setupId = setupInfo.getId();
        this.hotSetupDescription = setupInfo.getDescription();

        /*
         * Show/hide the page controls for the specified setup
         */
        switch (this.setupId) {
            case Consts.STRXML_SetupId_LocalClock:
                this.serverUrlListRendered = false;
                break;

            case Consts.STRXML_SetupId_NTPServer:
                this.serverUrlListRendered = true;
                break;
        }

        /*
         * Update defaults
         */
        this.hsomTimeFormat = this.timeFormatDefault;

        /*
         * Hide the information message
         */
        this.ShowMessageInfo(null);
    }

    /**
     *
     * @return String
     * @throws Exception
     */
    private String CreateXmlSpecification() throws Exception {
        /*
         * Create an instance of the experiment specification
         */
        ExperimentSpecification experimentSpecification = new ExperimentSpecification(this.labClientSession.getXmlSpecification());

        /*
         * Add specification information
         */
        experimentSpecification.setSetupName(this.hsomSetupName);
        experimentSpecification.setSetupId(this.setupId);
        experimentSpecification.setServerUrl(this.hsomServerUrl);
        experimentSpecification.setFormatName(this.hsomTimeFormat);

        /*
         * Convert specification information to an XML string
         */
        String xmlSpecification = experimentSpecification.ToXmlString();

        return xmlSpecification;
    }

    /**
     *
     * @param message
     */
    private void ShowMessageInfo(String message) {
        this.holMessage = message;
        this.holMessageClass = Consts.STRSTL_InfoMessage;
    }

    /**
     *
     * @param message
     */
    private void ShowMessageError(String message) {
        this.holMessage = message;
        this.holMessageClass = Consts.STRSTL_ErrorMessage;
    }
}
