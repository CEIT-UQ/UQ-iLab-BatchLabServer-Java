/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.client;

import java.util.logging.Level;
import javax.faces.application.ViewExpiredException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import uq.ilabs.labserver.service.LabServerService;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.client.Consts;
import uq.ilabs.library.labserver.client.LabServerSession;
import uq.ilabs.library.labserver.database.ServiceBrokersDB;
import uq.ilabs.library.labserver.engine.types.ServiceBrokerInfo;

/**
 *
 * @author uqlpayne
 */
@ManagedBean
@SessionScoped
public class ServiceBrokersBean {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokersBean.class.getName();
    private static final Level logLevel = Level.CONFIG;
    /*
     * String constants
     */
    private static final String STR_ServiceBroker_arg = "ServiceBroker '%s' ";
    private static final String STR_SaveSuccessful_arg = STR_ServiceBroker_arg + "saved successfully.";
    private static final String STR_UpdateSuccessful_arg = STR_ServiceBroker_arg + "updated successfully.";
    private static final String STR_DeleteSuccessful_arg = STR_ServiceBroker_arg + "deleted successfully.";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ServiceBrokerName = "ServiceBroker Name";
    private static final String STRERR_ServiceBrokerGuid = "ServiceBroker Guid";
    private static final String STRERR_OutgoingPasskey = "Outgoing Passkey";
    private static final String STRERR_ServiceUrl = "Service Url";
    private static final String STRERR_NotSpecified_arg = "%s: Not specified!";
    private static final String STRERR_AlreadyExists_arg = STR_ServiceBroker_arg + "already exists!";
    private static final String STRERR_RetrieveFailed_arg = STR_ServiceBroker_arg + "could not be retrieved.";
    private static final String STRERR_SaveFailed_arg = STR_ServiceBroker_arg + "could not be saved.";
    private static final String STRERR_UpdateFailed_arg = STR_ServiceBroker_arg + "could not be updated.";
    private static final String STRERR_DeleteFailed_arg = STR_ServiceBroker_arg + "could not be deleted.";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabServerSession labServerSession;
    private ServiceBrokersDB serviceBrokersDB;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String hsomServiceBroker;
    private String hitServiceBrokerName;
    private String hitServiceBrokerGuid;
    private String hitOutgoingPasskey;
    private String hitIncomingPasskey;
    private String hitServiceUrl;
    private boolean hcbPermitted;
    private String[] serviceBrokers;
    private boolean registered;
//    private boolean deleteDisabled;
    private String holMessage;
    private String holMessageClass;

    public String getHsomServiceBroker() {
        return hsomServiceBroker;
    }

    public void setHsomServiceBroker(String hsomServiceBroker) {
        this.hsomServiceBroker = hsomServiceBroker;
    }

    public String getHitServiceBrokerName() {
        return hitServiceBrokerName;
    }

    public void setHitServiceBrokerName(String hitServiceBrokerName) {
        this.hitServiceBrokerName = hitServiceBrokerName;
    }

    public String getHitServiceBrokerGuid() {
        return hitServiceBrokerGuid;
    }

    public void setHitServiceBrokerGuid(String hitServiceBrokerGuid) {
        this.hitServiceBrokerGuid = hitServiceBrokerGuid;
    }

    public String getHitOutgoingPasskey() {
        return hitOutgoingPasskey;
    }

    public void setHitOutgoingPasskey(String hitOutgoingPasskey) {
        this.hitOutgoingPasskey = hitOutgoingPasskey;
    }

    public String getHitIncomingPasskey() {
        return hitIncomingPasskey;
    }

    public void setHitIncomingPasskey(String hitIncomingPasskey) {
        this.hitIncomingPasskey = hitIncomingPasskey;
    }

    public String getHitServiceUrl() {
        return hitServiceUrl;
    }

    public void setHitServiceUrl(String hitServiceUrl) {
        this.hitServiceUrl = hitServiceUrl;
    }

    public boolean isHcbPermitted() {
        return hcbPermitted;
    }

    public void setHcbPermitted(boolean hcbPermitted) {
        this.hcbPermitted = hcbPermitted;
    }

    public String[] getServiceBrokers() {
        return serviceBrokers;
    }

    public boolean isRegistered() {
        return registered;
    }

//    public boolean isDeleteDisabled() {
//        return deleteDisabled;
//    }
    public String getHolMessage() {
        return holMessage;
    }

    public String getHolMessageClass() {
        return holMessageClass;
    }
    //</editor-fold>

    /**
     * Creates a new instance of ServiceBrokersBean
     */
    public ServiceBrokersBean() {
        final String methodName = "ServiceBrokersBean";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        this.labServerSession = (LabServerSession) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Consts.STRSSN_LabServer);

        try {
            this.serviceBrokersDB = new ServiceBrokersDB(this.labServerSession.getDbConnection());
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
            this.serviceBrokers = this.CreateServiceBrokerList();

            /*
             * Clear the page
             */
            actionNew();
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    public String actionSelect() {

        if (this.hsomServiceBroker != null && this.hsomServiceBroker.equals(this.serviceBrokers[0]) == false) {
            PopulateServiceBrokerInfo();
            this.hsomServiceBroker = this.serviceBrokers[0];
            this.ShowMessageInfo(null);
        }

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @return
     */
    public String actionSave() {
        final String methodName = "actionSave";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Parse the web page information
         */
        ServiceBrokerInfo serviceBrokerInfo = this.Parse(null);
        if (serviceBrokerInfo != null) {
            try {
                /*
                 * Add information for a new ServiceBroker
                 */
                if (this.serviceBrokersDB.Add(serviceBrokerInfo) < 0) {
                    throw new Exception(String.format(STRERR_SaveFailed_arg, serviceBrokerInfo.getName()));
                }

                /*
                 * Refresh the ServiceBroker list
                 */
                this.serviceBrokers = this.CreateServiceBrokerList();

                /*
                 * Recache ServiceBrokers
                 */
                LabServerService.setMapServiceBrokerInfo(null);

                /*
                 * Information saved successfully
                 */
                this.registered = true;
                ShowMessageInfo(String.format(STR_SaveSuccessful_arg, serviceBrokerInfo.getName()));

            } catch (Exception ex) {
                ShowMessageError(ex.getMessage());
                Logfile.WriteError(ex.toString());
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
             * Get the ServiceBroker info for the selected ServiceBroker
             */
            ServiceBrokerInfo serviceBrokerInfo = this.serviceBrokersDB.RetrieveByName(this.hitServiceBrokerName);
            if (serviceBrokerInfo == null) {
                throw new Exception(String.format(STRERR_RetrieveFailed_arg, this.hitServiceBrokerName));
            }

            /*
             * Parse the web page information
             */
            serviceBrokerInfo = this.Parse(serviceBrokerInfo);
            if (serviceBrokerInfo != null) {
                /*
                 * Update information for an existing ServiceBroker
                 */
                if (this.serviceBrokersDB.Update(serviceBrokerInfo) == false) {
                    throw new Exception(String.format(STRERR_SaveFailed_arg, serviceBrokerInfo.getName()));
                }

                /*
                 * Recache ServiceBrokers
                 */
                LabServerService.setMapServiceBrokerInfo(null);

                /*
                 * Information updated successfully
                 */
                ShowMessageInfo(String.format(STR_UpdateSuccessful_arg, serviceBrokerInfo.getName()));
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
     * @return
     */
    public String actionDelete() {
        final String methodName = "actionDelete";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Get the ServiceBroker info for the selected ServiceBroker
             */
            ServiceBrokerInfo serviceBrokerInfo = this.serviceBrokersDB.RetrieveByName(this.hitServiceBrokerName);
            if (serviceBrokerInfo == null) {
                throw new Exception(String.format(STRERR_RetrieveFailed_arg, this.hitServiceBrokerName));
            }

            /*
             * Delete the ServiceBroker
             */
            if (this.serviceBrokersDB.Delete(serviceBrokerInfo.getId()) == false) {
                throw new Exception(String.format(STRERR_DeleteFailed_arg, serviceBrokerInfo.getName()));
            }

            /*
             * Refresh the ServiceBroker list and clear the page
             */
            this.serviceBrokers = this.CreateServiceBrokerList();
            actionNew();

            /*
             * Recache ServiceBrokers
             */
            LabServerService.setMapServiceBrokerInfo(null);

            /*
             * Information deleted successfully
             */
            ShowMessageInfo(String.format(STR_DeleteSuccessful_arg, serviceBrokerInfo.getName()));

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
     * @return
     */
    public String actionNew() {
        final String methodName = "actionNew";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Clear information
         */
        this.hsomServiceBroker = null;
        this.hitServiceBrokerName = null;
        this.hitServiceBrokerGuid = null;
        this.hitServiceUrl = null;
        this.hitOutgoingPasskey = null;
        this.hitIncomingPasskey = null;
        this.hcbPermitted = false;

        /*
         * Update controls
         */
        this.registered = false;
//        this.deleteDisabled = true;

        this.ShowMessageInfo(null);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @return
     */
    private String[] CreateServiceBrokerList() {

        String[] serviceBrokerList = null;

        try {
            /*
             * Get the list of LabClient names
             */
            String[] stringArray = this.serviceBrokersDB.GetListOfNames();
            if (stringArray != null) {
                serviceBrokerList = new String[stringArray.length + 1];
                System.arraycopy(stringArray, 0, serviceBrokerList, 1, stringArray.length);
            } else {
                serviceBrokerList = new String[1];
            }
            serviceBrokerList[0] = "";
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        return serviceBrokerList;
    }

    /**
     *
     */
    private void PopulateServiceBrokerInfo() {
        final String methodName = "PopulateServiceBrokerInfo";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            ServiceBrokerInfo serviceBrokerInfo = this.serviceBrokersDB.RetrieveByName(this.hsomServiceBroker);
            if (serviceBrokerInfo == null) {
                throw new Exception(String.format(STRERR_RetrieveFailed_arg, this.hsomServiceBroker));
            }

            /*
             * Update information
             */
            this.hitServiceBrokerName = serviceBrokerInfo.getName();
            this.hitServiceBrokerGuid = serviceBrokerInfo.getGuid();
            this.hitServiceUrl = serviceBrokerInfo.getServiceUrl();
            this.hitOutgoingPasskey = serviceBrokerInfo.getOutPasskey();
            this.hitIncomingPasskey = serviceBrokerInfo.getInPasskey();
            this.hcbPermitted = serviceBrokerInfo.isPermitted();

            /*
             * Update controls
             */
            this.registered = true;
//            this.deleteDisabled = false;

        } catch (Exception ex) {
            ShowMessageError(ex.getMessage());
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param serviceBrokerInfo
     * @return ServiceBrokerInfo
     */
    private ServiceBrokerInfo Parse(ServiceBrokerInfo serviceBrokerInfo) {
        final String methodName = "Parse";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check if ServiceBrokerInfo has been provided
             */
            if (serviceBrokerInfo == null) {
                /*
                 * Create instance of ServiceBrokerInfo ready to fill in
                 */
                serviceBrokerInfo = new ServiceBrokerInfo();

                /*
                 * Check that ServiceBroker Name has been entered
                 */
                this.hitServiceBrokerName = this.hitServiceBrokerName.trim();
                if (this.hitServiceBrokerName.isEmpty() == true) {
                    throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_ServiceBrokerName));
                }
                serviceBrokerInfo.setName(this.hitServiceBrokerName);

                /*
                 * Check if ServiceBroker Name already exists
                 */
                if (this.serviceBrokersDB.RetrieveByName(this.hitServiceBrokerName) != null) {
                    throw new Exception(String.format(STRERR_AlreadyExists_arg, this.hitServiceBrokerName));
                }
            }

            /*
             * Check that ServiceBroker Guid has been entered
             */
            this.hitServiceBrokerGuid = this.hitServiceBrokerGuid.trim();
            if (this.hitServiceBrokerGuid.isEmpty() == true) {
                throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_ServiceBrokerGuid));
            }
            serviceBrokerInfo.setGuid(this.hitServiceBrokerGuid);

            /*
             * Check that Outgoing Passkey has been entered
             */
            this.hitOutgoingPasskey = this.hitOutgoingPasskey.trim();
            if (this.hitOutgoingPasskey.isEmpty() == true) {
                throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_OutgoingPasskey));
            }
            serviceBrokerInfo.setOutPasskey(this.hitOutgoingPasskey);

            /*
             * Get allowed status
             */
            serviceBrokerInfo.setPermitted(this.hcbPermitted);

            /*
             * Optional Information
             */
            this.hitServiceUrl = this.hitServiceUrl.trim();
            serviceBrokerInfo.setServiceUrl(this.hitServiceUrl.isEmpty() ? null : this.hitServiceUrl);
            this.hitIncomingPasskey = this.hitIncomingPasskey.trim();
            serviceBrokerInfo.setInPasskey(this.hitIncomingPasskey.isEmpty() ? null : this.hitIncomingPasskey);

        } catch (Exception ex) {
            this.ShowMessageError(ex.getMessage());
            serviceBrokerInfo = null;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return serviceBrokerInfo;
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
