/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.client;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import uq.ilabs.labserver.LabServerAppBean;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.LabServerSoapAPI;
import uq.ilabs.library.labserver.client.Consts;
import uq.ilabs.library.labserver.client.LabServerSession;
import uq.ilabs.library.labserver.database.types.LabServerInfo;
import uq.ilabs.library.labserver.database.types.ServiceBrokerInfo;
import uq.ilabs.library.labserver.engine.LabManagement;

/**
 *
 * @author uqlpayne
 */
@Named(value = "selfRegistrationBean")
@SessionScoped
public class SelfRegistrationBean implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = SelfRegistrationBean.class.getName();
    private static final Level logLevel = Level.CONFIG;
    /*
     * String constants
     */
    private static final String STR_LabServer_arg = "LabServer '%s' ";
    private static final String STR_RestartRequired = " LabServer restart is required for settings to take effect.";
    private static final String STR_SaveSuccessful_arg = STR_LabServer_arg + "saved successfully.";
    private static final String STR_UpdateSuccessful_arg = STR_LabServer_arg + "updated successfully.";
    private static final String STR_Online = "Online";
    private static final String STR_Offline = "Offline";
    private static final String STR_LabServerStatus_arg2 = "LabServer Status: %s - %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_LabServerNotRegistered = "LabServer is not registered yet!";
    private static final String STRERR_Name = "Name";
    private static final String STRERR_Guid = "Guid";
    private static final String STRERR_ServiceUrl = "Service Url";
    private static final String STRERR_ContactEmail = "Contact Email";
    private static final String STRERR_RetrieveFailed_arg = STR_LabServer_arg + "could not be retrieved.";
    private static final String STRERR_NotSpecified_arg = "%s: Not specified!";
    private static final String STRERR_SaveFailed_arg = "%s: Information could not be saved.";
    private static final String STRERR_UpdateFailed_arg = "%s: Information could not be updated.";
    private static final String STRERR_LabServerUnaccessible = "LabServer is unaccessible!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @EJB
    private LabServerAppBean labServerAppBean;
    private LabServerSession labServerSession;
    private LabManagement labManagement;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String hitLabServerName;
    private String hitLabServerGuid;
    private String hitServiceUrl;
    private String hitContactEmail;
    private boolean hcbAuthenticate;
    private String hitCompletedEmail;
    private String hitFailedEmail;
    private boolean registered;
    private String holMessage;
    private String holMessageClass;

    public String getHitLabServerName() {
        return hitLabServerName;
    }

    public void setHitLabServerName(String hitLabServerName) {
        this.hitLabServerName = hitLabServerName;
    }

    public String getHitLabServerGuid() {
        return hitLabServerGuid;
    }

    public void setHitLabServerGuid(String hitLabServerGuid) {
        this.hitLabServerGuid = hitLabServerGuid;
    }

    public String getHitServiceUrl() {
        return hitServiceUrl;
    }

    public void setHitServiceUrl(String hitServiceUrl) {
        this.hitServiceUrl = hitServiceUrl;
    }

    public String getHitContactEmail() {
        return hitContactEmail;
    }

    public void setHitContactEmail(String hitContactEmail) {
        this.hitContactEmail = hitContactEmail;
    }

    public boolean isHcbAuthenticate() {
        return hcbAuthenticate;
    }

    public void setHcbAuthenticate(boolean hcbAuthenticate) {
        this.hcbAuthenticate = hcbAuthenticate;
    }

    public String getHitCompletedEmail() {
        return hitCompletedEmail;
    }

    public void setHitCompletedEmail(String hitCompletedEmail) {
        this.hitCompletedEmail = hitCompletedEmail;
    }

    public String getHitFailedEmail() {
        return hitFailedEmail;
    }

    public void setHitFailedEmail(String hitFailedEmail) {
        this.hitFailedEmail = hitFailedEmail;
    }

    public boolean isRegistered() {
        return registered;
    }

    public String getHolMessage() {
        return holMessage;
    }

    public String getHolMessageClass() {
        return holMessageClass;
    }
    //</editor-fold>

    /**
     * Creates a new instance of SelfRegistrationBean
     */
    public SelfRegistrationBean() {
        final String methodName = "SelfRegistrationBean";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        this.labServerSession = (LabServerSession) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Consts.STRSSN_LabServer);
        this.labManagement = this.labServerSession.getLabManagement();

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     */
    public void pageLoad() {
        final String methodName = "pageLoad";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Check if user is logged in
         */
        if (this.labServerSession.getUserSession() == null) {
            throw new ViewExpiredException();
        }

        if (FacesContext.getCurrentInstance().isPostback() == false) {
            /*
             * Not a postback, initialise page controls
             */
            this.PopulateLabServerInfo();
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    public String actionCreateGuid() {

        this.hitLabServerGuid = UUID.randomUUID().toString();

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @return String
     */
    public String actionSave() {
        final String methodName = "actionSave";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Parse the web page information
         */
        LabServerInfo labServerInfo = this.Parse(null);
        if (labServerInfo != null) {
            try {
                /*
                 * Save the information
                 */
                if (this.labManagement.getLabServerDB().Add(labServerInfo) < 0) {
                    throw new Exception(String.format(STRERR_SaveFailed_arg, labServerInfo.getName()));
                }

                /*
                 * Recache LabServerInfo
                 */
                this.labManagement.setLabServerInfo(labServerInfo);

                /*
                 * Information saved successfully
                 */
                this.registered = true;
                this.ShowMessageInfo(String.format(STR_SaveSuccessful_arg, labServerInfo.getName()));

            } catch (Exception ex) {
                this.ShowMessageError(ex.getMessage());
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @return String
     */
    public String actionUpdate() {
        final String methodName = "actionUpdate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Get LabServerInfo
             */
            LabServerInfo labServerInfo = this.labManagement.getLabServerDB().Retrieve();
            if (labServerInfo == null) {
                throw new Exception(String.format(STRERR_RetrieveFailed_arg, ""));
            }

            /*
             * Save these before parsing to check if they have changed and a LabServer restart is required
             */
            String name = labServerInfo.getName();
            String guid = labServerInfo.getGuid();
            String serviceUrl = labServerInfo.getServiceUrl();

            /*
             * Parse the web page information
             */
            labServerInfo = this.Parse(labServerInfo);
            if (labServerInfo != null) {
                /*
                 * Update the information
                 */
                if (this.labManagement.getLabServerDB().Update(labServerInfo) == false) {
                    throw new Exception(String.format(STRERR_UpdateFailed_arg, labServerInfo.getName()));
                }

                /*
                 * Recache LabServerInfo in the session
                 */
                this.labManagement.setLabServerInfo(labServerInfo);

                /*
                 * Check to see if the LabServer service is running
                 */
                boolean restartRequired = false;
                if (labManagement != null) {
                    /*
                     * Check what has changed that requires a LabServer restart
                     */
                    if ((name.equalsIgnoreCase(labServerInfo.getName()) == false)
                            || (guid.equalsIgnoreCase(labServerInfo.getGuid()) == false)
                            || (serviceUrl.equalsIgnoreCase(labServerInfo.getServiceUrl()) == false)) {
                        restartRequired = true;
                    }

                    /*
                     * If a restart is not required then update the LabServerInfo
                     */
                    if (restartRequired == false) {
                        labManagement.getLabServerInfo().setAuthenticate(labServerInfo.isAuthenticate());
                        labManagement.getLabServerInfo().setContactEmail(labServerInfo.getContactEmail());
                        labManagement.getLabServerInfo().setCompletedEmail(labServerInfo.getCompletedEmail());
                        labManagement.getLabServerInfo().setFailedEmail(labServerInfo.getFailedEmail());
                    }
                }

                /*
                 * Information saved successfully
                 */
                String message = String.format(STR_UpdateSuccessful_arg, labServerInfo.getName());
                if (restartRequired == true) {
                    message += STR_RestartRequired;
                }
                ShowMessageInfo(message);
            }
        } catch (Exception ex) {
            this.ShowMessageError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @return String
     */
    public String actionTest() {
        final String methodName = "actionTest";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Get the LabServer Guid
             */
            if (this.labManagement.getLabServerInfo() == null) {
                throw new NullPointerException(STRERR_LabServerNotRegistered);
            }
            String labServerGuid = this.labManagement.getLabServerInfo().getGuid();
            if (labServerGuid == null) {
                throw new NullPointerException(STRERR_LabServerNotRegistered);
            }

            /*
             * Get ServiceBroker information for localhost
             */
            ServiceBrokerInfo serviceBrokerInfo = this.labManagement.getServiceBrokersDB().RetrieveByName(Consts.STR_SbNameLocalHost);
            if (serviceBrokerInfo == null) {
                throw new Exception(String.format(STRERR_RetrieveFailed_arg, Consts.STR_SbNameLocalHost));
            }

            /*
             * Get a proxy to the LabServer service
             */
            LabServerSoapAPI labServerAPI = new LabServerSoapAPI(this.hitServiceUrl);
            labServerAPI.setIdentifier(serviceBrokerInfo.getGuid());
            labServerAPI.setPasskey(serviceBrokerInfo.getOutPasskey());

            /*
             * Get the status of the LabServer through the web service in case the service hasn't started yet.
             */
            LabStatus labStatus = labServerAPI.GetLabStatus();
            if (labStatus == null) {
                throw new NullPointerException(STRERR_LabServerUnaccessible);
            }

            /*
             * Now get the verbose status through the experiment manager
             */
            labStatus = this.labServerAppBean.getExperimentManager().GetLabStatus(true);

            ShowMessageInfo(String.format(STR_LabServerStatus_arg2,
                    labStatus.isOnline() ? STR_Online : STR_Offline, labStatus.getLabStatusMessage()));

        } catch (Exception ex) {
            ShowMessageError(ex.getMessage());
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @return String
     */
    public String actionNew() {
        final String methodName = "actionNew";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Clear information
         */
        this.hitLabServerName = null;
        this.hitLabServerGuid = null;
        this.hitServiceUrl = null;
        this.hitContactEmail = null;
        this.hcbAuthenticate = true;
        this.hitCompletedEmail = null;
        this.hitFailedEmail = null;

        /*
         * Update controls
         */
        this.registered = false;

        this.ShowMessageInfo(null);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @param labServerInfo
     * @return LabServerInfo
     */
    private LabServerInfo Parse(LabServerInfo labServerInfo) {
        final String methodName = "Parse";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check if LabServerInfo has been provided
             */
            if (labServerInfo == null) {
                /*
                 * Create instance of LabServerInfo ready to fill in
                 */
                labServerInfo = new LabServerInfo();
            }

            /*
             * Check that Name has been entered
             */
            this.hitLabServerName = this.hitLabServerName.trim();
            if (this.hitLabServerName.isEmpty() == true) {
                throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_Name));
            }
            labServerInfo.setName(this.hitLabServerName);

            /*
             * Check that Guid has been entered
             */
            this.hitLabServerGuid = this.hitLabServerGuid.trim();
            if (this.hitLabServerGuid.isEmpty() == true) {
                throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_Guid));
            }
            labServerInfo.setGuid(this.hitLabServerGuid);

            /*
             * Check that Service Url has been entered
             */
            this.hitServiceUrl = this.hitServiceUrl.trim();
            if (this.hitServiceUrl.isEmpty() == true) {
                throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_ServiceUrl));
            }
            labServerInfo.setServiceUrl(this.hitServiceUrl);

            /*
             * Check that Contact Email has been entered
             */
            this.hitContactEmail = this.hitContactEmail.trim();
            if (this.hitContactEmail.isEmpty() == true) {
                throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_ContactEmail));
            }
            labServerInfo.setContactEmail(this.hitContactEmail);

            /*
             * Authenticate
             */
            labServerInfo.setAuthenticate(this.hcbAuthenticate);

            /*
             * Optional Information
             */
            this.hitCompletedEmail = this.hitCompletedEmail.trim();
            labServerInfo.setCompletedEmail(this.hitCompletedEmail.isEmpty() ? null : this.hitCompletedEmail);
            this.hitFailedEmail = this.hitFailedEmail.trim();
            labServerInfo.setFailedEmail(this.hitFailedEmail.isEmpty() ? null : this.hitFailedEmail);

        } catch (Exception ex) {
            labServerInfo = null;
            this.ShowMessageError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labServerInfo;
    }

    /**
     *
     */
    private void PopulateLabServerInfo() {
        final String methodName = "PopulateLabServerInfo";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            LabServerInfo labServerInfo = this.labManagement.getLabServerDB().Retrieve();
            if (labServerInfo != null) {
                this.hitLabServerName = labServerInfo.getName();
                this.hitLabServerGuid = labServerInfo.getGuid();
                this.hitServiceUrl = labServerInfo.getServiceUrl();
                this.hitContactEmail = labServerInfo.getContactEmail();
                this.hitCompletedEmail = labServerInfo.getCompletedEmail();
                this.hitFailedEmail = labServerInfo.getFailedEmail();
                this.hcbAuthenticate = labServerInfo.isAuthenticate();

                this.registered = true;

                this.ShowMessageInfo(null);
            } else {
                this.hcbAuthenticate = true;
                this.ShowMessageError(STRERR_LabServerNotRegistered);
            }
        } catch (Exception ex) {
            this.ShowMessageError(ex.getMessage());
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
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
