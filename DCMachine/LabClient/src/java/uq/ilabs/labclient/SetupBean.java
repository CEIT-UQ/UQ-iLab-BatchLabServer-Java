/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labclient;

import java.io.Serializable;
import java.util.logging.Level;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
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
@ManagedBean
@SessionScoped
public class SetupBean implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = SetupBean.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants
     */
    private static final String STR_HintRange_arg2 = "Range: %d to %d";
    private static final String STR_SpecificationValid_arg = "Specification is valid. Execution time will be %s.";
    private static final String STR_SubmissionSuccessful_arg2 = "Submission was successful. Experiment # is %d and execution time is %s.";
    private static final String STR_MinutesAnd_arg2 = "%d minute%s and ";
    private static final String STR_Seconds_arg2 = "%d second%s";
    private static final String STR_ExperimentNumberHasBeenSubmitted_arg = "Experiment %d has been submitted.";
    private static final String STR_ExperimentNumbersHaveBeenSubmitted_arg = "Experiments %s have been submitted.";
    /*
     * String constants for XML elements
     */
    public static final String STRXML_SomeParameter = "someParameter";
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
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String[] setupNames;
    private String hsomSetupName;
    private String hotSetupDescription;
    private boolean hcbSubmitDisabled;
    private String holMessage;
    private String holMessageClass;
    private String holParamsTitle;
    private String hitMinimum;
    private String hitMaximum;
    private String hitStepSize;
    private String attrMinimum;
    private String attrMaximum;
    private String attrStepSize;

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

    public String getHolParamsTitle() {
        return holParamsTitle;
    }

    public void setHolParamsTitle(String holParamsTitle) {
        this.holParamsTitle = holParamsTitle;
    }

    public String getHitMinimum() {
        return hitMinimum;
    }

    public void setHitMinimum(String hitMinimum) {
        this.hitMinimum = hitMinimum;
    }

    public String getHitMaximum() {
        return hitMaximum;
    }

    public void setHitMaximum(String hitMaximum) {
        this.hitMaximum = hitMaximum;
    }

    public String getHitStepSize() {
        return hitStepSize;
    }

    public void setHitStepSize(String hitStepSize) {
        this.hitStepSize = hitStepSize;
    }

    public String getAttrMinimum() {
        return attrMinimum;
    }

    public String getAttrMaximum() {
        return attrMaximum;
    }

    public String getAttrStepSize() {
        return attrStepSize;
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
            String message = String.format(STR_SpecificationValid_arg, FormatTimeMessage((int) validationReport.getEstRuntime()));
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
                    clientSubmissionReport.getExperimentId(), FormatTimeMessage((int) validationReport.getEstRuntime()));
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

        try {
            /*
             * Populate the web page controls with default values for the specified setup
             */
            Document document = XmlUtilities.GetDocumentFromString(setupInfo.getXmlSetup());
            Node nodeRoot = XmlUtilities.GetRootNode(document, Consts.STRXML_Setup);
            this.holParamsTitle = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_ParamsTitle);

            /*
             * Minimum, maximum and step size inputs
             */
            switch (this.setupId) {
                case Consts.STRXML_SetupId_VoltageVsSpeed:
                case Consts.STRXML_SetupId_SpeedVsVoltage:
                    this.hitMinimum = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_SpeedMin);
                    this.hitMaximum = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_SpeedMax);
                    this.hitStepSize = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_SpeedStep);
                    break;
                case Consts.STRXML_SetupId_VoltageVsField:
                case Consts.STRXML_SetupId_SpeedVsField:
                    this.hitMinimum = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_FieldMin);
                    this.hitMaximum = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_FieldMax);
                    this.hitStepSize = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_FieldStep);
                    break;
                case Consts.STRXML_SetupId_VoltageVsLoad:
                    this.hitMinimum = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_LoadMin);
                    this.hitMaximum = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_LoadMax);
                    this.hitStepSize = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_LoadStep);
                    break;
            }

            /*
             * Validation boundary values
             */
            document = XmlUtilities.GetDocumentFromString(this.labClientSession.getXmlValidation());
            nodeRoot = XmlUtilities.GetRootNode(document, Consts.STRXML_Validation);
            Node nodeValidation = null;
            switch (this.setupId) {
                case Consts.STRXML_SetupId_VoltageVsSpeed:
                case Consts.STRXML_SetupId_SpeedVsVoltage:
                    nodeValidation = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_ValidationSpeed);
                    break;
                case Consts.STRXML_SetupId_VoltageVsField:
                case Consts.STRXML_SetupId_SpeedVsField:
                    nodeValidation = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_ValidationField);
                    break;
                case Consts.STRXML_SetupId_VoltageVsLoad:
                    nodeValidation = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_ValidationLoad);
                    break;
            }
            int minimum = XmlUtilities.GetChildValueAsInt(nodeValidation, Consts.STRXML_ValidationMinimum);
            int maximum = XmlUtilities.GetChildValueAsInt(nodeValidation, Consts.STRXML_ValidationMaximum);
            int stepMin = XmlUtilities.GetChildValueAsInt(nodeValidation, Consts.STRXML_ValidationStepMin);
            int stepMax = XmlUtilities.GetChildValueAsInt(nodeValidation, Consts.STRXML_ValidationStepMax);
            this.attrMinimum = String.format(STR_HintRange_arg2, minimum, maximum);
            this.attrMaximum = String.format(STR_HintRange_arg2, minimum, maximum);
            this.attrStepSize = String.format(STR_HintRange_arg2, stepMin, stepMax);
        } catch (Exception ex) {
        }

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
        if (experimentSpecification == null) {
            throw new NullPointerException(ExperimentSpecification.class.getSimpleName());
        }

        /*
         * Add specification information
         */
        experimentSpecification.setSetupId(this.setupId);
        experimentSpecification.setMinimum(this.hitMinimum);
        experimentSpecification.setMaximum(this.hitMaximum);
        experimentSpecification.setStepSize(this.hitStepSize);

        /*
         * Convert specification information to an XML string
         */
        String xmlSpecification = experimentSpecification.ToXmlString();

        return xmlSpecification;
    }

    /**
     *
     * @param seconds
     * @return
     */
    private String FormatTimeMessage(int seconds) {
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
