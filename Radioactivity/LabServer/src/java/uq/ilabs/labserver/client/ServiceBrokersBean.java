/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.client;

import java.io.Serializable;
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
public class ServiceBrokersBean implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokersBean.class.getName();
    private static final Level logLevel = Level.CONFIG;
    /*
     * String constants
     */
    private static final String STR_SaveSuccessful_arg = "Information for ServiceBroker '%s' was saved successfully.";
    private static final String STR_DeleteSuccessful_arg = "Information for ServiceBroker '%s' was deleted successfully.";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_Name = "Name";
    private static final String STRERR_Guid = "Guid";
    private static final String STRERR_OutPasskey = "OutPasskey";
    private static final String STRERR_WebServiceUrl = "Web Service Url";
    private static final String STRERR_NotSpecified_arg = "%s: Not specified!";
    private static final String STRERR_AlreadyExists_arg = "%s: Already exists!";
    private static final String STRERR_RetrieveFailed_arg = "%s: Information could not be retrieved.";
    private static final String STRERR_SaveFailed_arg = "%s: Information could not be saved.";
    private static final String STRERR_DeleteFailed_arg = "%s: Information could not be deleted.";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabServerSession labServerSession;
    private ServiceBrokersDB serviceBrokersDB;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String hsomName;
    private String hitName;
    private String hitGuid;
    private String hitOutPasskey;
    private String hitInPasskey;
    private String hitWebServiceUrl;
    private boolean hcbPermitted;
    private String[] names;
    private boolean registered;
    private boolean deleteDisabled;
    private String holMessage;
    private String holMessageClass;

    public String getHsomName() {
        return hsomName;
    }

    public void setHsomName(String hsomName) {
        this.hsomName = hsomName;
    }

    public String getHitName() {
        return hitName;
    }

    public void setHitName(String hitName) {
        this.hitName = hitName;
    }

    public String getHitGuid() {
        return hitGuid;
    }

    public void setHitGuid(String hitGuid) {
        this.hitGuid = hitGuid;
    }

    public String getHitOutPasskey() {
        return hitOutPasskey;
    }

    public void setHitOutPasskey(String hitOutPasskey) {
        this.hitOutPasskey = hitOutPasskey;
    }

    public String getHitInPasskey() {
        return hitInPasskey;
    }

    public void setHitInPasskey(String hitInPasskey) {
        this.hitInPasskey = hitInPasskey;
    }

    public String getHitWebServiceUrl() {
        return hitWebServiceUrl;
    }

    public void setHitWebServiceUrl(String hitWebServiceUrl) {
        this.hitWebServiceUrl = hitWebServiceUrl;
    }

    public boolean isHcbPermitted() {
        return hcbPermitted;
    }

    public void setHcbPermitted(boolean hcbPermitted) {
        this.hcbPermitted = hcbPermitted;
    }

    public String[] getNames() {
        return names;
    }

    public boolean isRegistered() {
        return registered;
    }

    public boolean isDeleteDisabled() {
        return deleteDisabled;
    }

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
            try {
                /*
                 * Get the LabServer's instance of the ServiceBroker class if it exists. It won't exist if the service
                 * has not been initialised yet
                 */
                if ((this.serviceBrokersDB = LabServerService.getServiceBrokers()) == null) {
                    this.serviceBrokersDB = new ServiceBrokersDB(this.labServerSession.getDbConnection());
                }

                /*
                 * Get the names of the ServiceBrokers
                 */
                this.names = this.CreateServiceBrokerList();

                /*
                 * Clear the page
                 */
                actionNew();
            } catch (Exception ex) {
                ShowMessageError(ex.getMessage());
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    public String actionSelect() {

        if (this.hsomName != null && this.hsomName.equals(this.names[0]) == false) {
            PopulateServiceBrokerInfo();
            this.hsomName = this.names[0];
            this.ShowMessageInfo(null);
        }

        /*
         * Navigate to the current page
         */
        return null;
    }

    /**
     *
     * @return
     */
    public String actionSave() {
        final String methodName = "actionSave";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Create instance of ServiceBroker info ready to fill in
             */
            ServiceBrokerInfo serviceBrokerInfo = new ServiceBrokerInfo();

            /*
             * Check if adding a new Servicebroker or updating an existing one
             */
            if (this.registered == false) {
                /*
                 * Adding a new ServiceBroker, check that a name has been entered
                 */
                this.hitName = this.hitName.trim();
                if (this.hitName.isEmpty() == true) {
                    throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_Name));
                }
                serviceBrokerInfo.setName(this.hitName);

                /*
                 * Check if name already exists
                 */
                if (this.serviceBrokersDB.RetrieveByName(this.hitName) != null) {
                    throw new Exception(String.format(STRERR_AlreadyExists_arg, this.hitName));
                }
            } else {
                /*
                 * Updating an existing ServiceBroker
                 */
                serviceBrokerInfo = this.serviceBrokersDB.RetrieveByName(hitName);
            }

            /*
             * Check that a guid has been entered
             */
            this.hitGuid = this.hitGuid.trim();
            if (this.hitGuid.isEmpty() == true) {
                throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_Guid));
            }
            serviceBrokerInfo.setGuid(this.hitGuid);

            /*
             * Check that an outgoing passkey has been entered
             */
            this.hitOutPasskey = this.hitOutPasskey.trim();
            if (serviceBrokerInfo.getOutPasskey().isEmpty() == true) {
                throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_OutPasskey));
            }
            serviceBrokerInfo.setOutPasskey(this.hitOutPasskey);

            /*
             * An incoming passkey is optional
             */
            this.hitInPasskey = this.hitInPasskey.trim();
            serviceBrokerInfo.setInPasskey(this.hitInPasskey);

            /*
             * Check that a web service url has been entered
             */
            this.hitWebServiceUrl = this.hitWebServiceUrl.trim();
            if (serviceBrokerInfo.getServiceUrl().isEmpty() == true) {
                throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_WebServiceUrl));
            }
            serviceBrokerInfo.setServiceUrl(this.hitWebServiceUrl);

            /*
             * Get allowed status
             */
            serviceBrokerInfo.setPermitted(this.hcbPermitted);

            /*
             * Check if adding a new Servicebroker or updating an existing one
             */
            if (this.registered == false) {
                /*
                 * Add information for a new ServiceBroker
                 */
                if (this.serviceBrokersDB.Add(serviceBrokerInfo) < 0) {
                    throw new Exception(String.format(STRERR_SaveFailed_arg, serviceBrokerInfo.getName()));
                }

                /*
                 * Update ServiceBroker list
                 */
                this.names = this.serviceBrokersDB.GetListOfNames();
                this.hsomName = serviceBrokerInfo.getName();
            } else {
                /*
                 * Update information for an existing ServiceBroker
                 */
                if (this.serviceBrokersDB.Update(serviceBrokerInfo) == false) {
                    throw new Exception(String.format(STRERR_SaveFailed_arg, serviceBrokerInfo.getName()));
                }
            }

            /*
             * Recache ServiceBrokers
             */
            this.serviceBrokersDB.CreateCache();

            /*
             * Information saved successfully
             */
            ShowMessageInfo(String.format(STR_SaveSuccessful_arg, serviceBrokerInfo.getName()));
        } catch (Exception ex) {
            ShowMessageError(ex.getMessage());
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /*
         * Navigate to the current page
         */
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
             * Delete the ServiceBroker information
             */
            ServiceBrokerInfo serviceBrokerInfo = this.serviceBrokersDB.RetrieveByName(this.hsomName);
            if (this.serviceBrokersDB.Delete(serviceBrokerInfo.getId()) == false) {
                throw new Exception(String.format(STRERR_DeleteFailed_arg, serviceBrokerInfo.getName()));
            }

            /*
             * Update ServiceBroker list and information
             */
            this.names = this.serviceBrokersDB.GetListOfNames();
            actionNew();

            /*
             * Recache ServiceBrokers
             */
            this.serviceBrokersDB.CreateCache();

            /*
             * Information deleted successfully
             */
            ShowMessageInfo(String.format(STR_DeleteSuccessful_arg, serviceBrokerInfo.getName()));
        } catch (Exception ex) {
            ShowMessageError(ex.getMessage());
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /*
         * Navigate to the current page
         */
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
        this.hsomName = null;
        this.hitName = null;
        this.hitGuid = null;
        this.hitOutPasskey = null;
        this.hitInPasskey = null;
        this.hitWebServiceUrl = null;
        this.hcbPermitted = false;

        /*
         * Update controls
         */
        this.registered = false;
        this.deleteDisabled = true;

        this.ShowMessageInfo(null);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /*
         * Navigate to the current page
         */
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
            ServiceBrokerInfo serviceBrokerInfo = this.serviceBrokersDB.RetrieveByName(this.hsomName);
            if (serviceBrokerInfo == null) {
                throw new Exception(String.format(STRERR_RetrieveFailed_arg, this.hsomName));
            }

            /*
             * Update information
             */
            this.hitName = serviceBrokerInfo.getName();
            this.hitGuid = serviceBrokerInfo.getGuid();
            this.hitOutPasskey = serviceBrokerInfo.getOutPasskey();
            this.hitInPasskey = serviceBrokerInfo.getInPasskey();
            this.hitWebServiceUrl = serviceBrokerInfo.getServiceUrl();
            this.hcbPermitted = serviceBrokerInfo.isPermitted();

            /*
             * Update controls
             */
            this.registered = true;
            this.deleteDisabled = false;
        } catch (Exception ex) {
            ShowMessageError(ex.getMessage());
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
