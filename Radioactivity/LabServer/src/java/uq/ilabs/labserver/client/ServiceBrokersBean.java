/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.client;

import java.io.Serializable;
import java.util.logging.Level;
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
    private static final String STR_NameRequired = "Field required: Name";
    private static final String STR_NameAlreadyExists = "Name already exists";
    private static final String STR_GuidRequired = "Field required: Guid";
    private static final String STR_OutPasskeyRequired = "Field required: OutPasskey";
    private static final String STR_WebServiceUrlRequired = "Field required: Web Service Url";
    private static final String STRERR_RetrieveFailed_arg = "Information for ServiceBroker '%s' could not be retrieved.";
    private static final String STRERR_SaveFailed_arg = "Information for ServiceBroker '%s' could not be saved.";
    private static final String STRERR_DeleteFailed_arg = "Information for ServiceBroker '%s' could not be deleted.";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabServerSession labServerSession;
    private ServiceBrokersDB serviceBrokers;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String[] names;
    private String selectedName;
    private String name;
    private String guid;
    private String outPasskey;
    private String inPasskey;
    private String webServiceUrl;
    private boolean permitted;
    private boolean nameDisabled;
    private boolean deleteDisabled;
    private String holMessage;
    private String holMessageClass;

    public String[] getNames() {
        return names;
    }

    public String getSelectedName() {
        return selectedName;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getInPasskey() {
        return inPasskey;
    }

    public void setInPasskey(String incomingPasskey) {
        this.inPasskey = incomingPasskey;
    }

    public boolean isPermitted() {
        return permitted;
    }

    public void setPermitted(boolean allowed) {
        this.permitted = allowed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutPasskey() {
        return outPasskey;
    }

    public void setOutPasskey(String outPasskey) {
        this.outPasskey = outPasskey;
    }

    public String getWebServiceUrl() {
        return webServiceUrl;
    }

    public void setWebServiceUrl(String webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
    }

    public boolean isNameDisabled() {
        return nameDisabled;
    }

    public void setNameDisabled(boolean nameDisabled) {
        this.nameDisabled = nameDisabled;
    }

    public boolean isDeleteDisabled() {
        return deleteDisabled;
    }

    public void setDeleteDisabled(boolean deleteDisabled) {
        this.deleteDisabled = deleteDisabled;
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

        if (FacesContext.getCurrentInstance().isPostback() == false) {
            /*
             * Not a postback, initialise page controls
             */
            try {
                /*
                 * Get the LabServer's instance of the ServiceBroker class if it exists. It won't exist if the service
                 * has not been initialised yet
                 */
                if ((this.serviceBrokers = LabServerService.getServiceBrokers()) == null) {
                    this.serviceBrokers = new ServiceBrokersDB(this.labServerSession.getDbConnection());
                }

                /*
                 * Get the names of the ServiceBrokers
                 */
                this.names = this.serviceBrokers.GetListName();
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

        if (this.selectedName == null || this.selectedName.isEmpty()) {
            actionNew();
        } else {
            PopulateServiceBrokerInfo();
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
            if (this.nameDisabled == false) {
                /*
                 * Adding a new ServiceBroker, check that a name has been entered
                 */
                this.name = this.name.trim();
                serviceBrokerInfo.setName(this.name);
                if (serviceBrokerInfo.getName().isEmpty() == true) {
                    throw new Exception(STR_NameRequired);
                }

                /*
                 * Check if name already exists
                 */
                for (int i = 0; i < this.names.length; i++) {
                    if (this.names[i].equalsIgnoreCase(serviceBrokerInfo.getName())) {
                        throw new Exception(STR_NameAlreadyExists);
                    }
                }
            } else {
                /*
                 * Updating an existing ServiceBroker
                 */
                serviceBrokerInfo.setName(this.name);
            }

            /*
             * Check that a guid has been entered
             */
            this.guid = this.guid.trim();
            serviceBrokerInfo.setGuid(this.guid);
            if (serviceBrokerInfo.getGuid().isEmpty() == true) {
                throw new Exception(STR_GuidRequired);
            }

            /*
             * Check that an outgoing passkey has been entered
             */
            this.outPasskey = this.outPasskey.trim();
            serviceBrokerInfo.setOutPasskey(this.outPasskey);
            if (serviceBrokerInfo.getOutPasskey().isEmpty() == true) {
                throw new Exception(STR_OutPasskeyRequired);
            }

            /*
             * An incoming passkey is optional
             */
            this.inPasskey = this.inPasskey.trim();
            serviceBrokerInfo.setInPasskey(this.inPasskey);

            /*
             * Check that a web service url has been entered
             */
            this.webServiceUrl = this.webServiceUrl.trim();
            serviceBrokerInfo.setServiceUrl(this.webServiceUrl);
            if (serviceBrokerInfo.getServiceUrl().isEmpty() == true) {
                throw new Exception(STR_WebServiceUrlRequired);
            }

            /*
             * Get allowed status
             */
            serviceBrokerInfo.setPermitted(this.permitted);

            /*
             * Check if adding a new Servicebroker or updating an existing one
             */
            if (this.nameDisabled == false) {
                /*
                 * Add information for a new ServiceBroker
                 */
                if (this.serviceBrokers.Add(serviceBrokerInfo) < 0) {
                    throw new Exception(String.format(STRERR_SaveFailed_arg, serviceBrokerInfo.getName()));
                }

                /*
                 * Update ServiceBroker list
                 */
                this.names = serviceBrokers.GetListName();
                this.selectedName = serviceBrokerInfo.getName();
            } else {
                /*
                 * Update information for an existing ServiceBroker
                 */
                if (this.serviceBrokers.Update(serviceBrokerInfo) == false) {
                    throw new Exception(String.format(STRERR_SaveFailed_arg, serviceBrokerInfo.getName()));
                }
            }

            /*
             * Recache ServiceBrokers
             */
            this.serviceBrokers.CreateCache();

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
            ServiceBrokerInfo serviceBrokerInfo = this.serviceBrokers.RetrieveByName(this.selectedName);
            if (this.serviceBrokers.Delete(serviceBrokerInfo.getId()) == false) {
                throw new Exception(String.format(STRERR_DeleteFailed_arg, serviceBrokerInfo.getName()));
            }

            /*
             * Update ServiceBroker list and information
             */
            this.names = this.serviceBrokers.GetListName();
            actionNew();

            /*
             * Recache ServiceBrokers
             */
            this.serviceBrokers.CreateCache();

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

        this.selectedName = null;
        this.name = null;
        this.guid = null;
        this.outPasskey = null;
        this.inPasskey = null;
        this.webServiceUrl = null;
        this.permitted = false;

        /*
         * Update controls
         */
        this.nameDisabled = false;
        this.deleteDisabled = true;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /*
         * Navigate to the current page
         */
        return null;
    }

    /**
     *
     */
    private void PopulateServiceBrokerInfo() {
        final String methodName = "PopulateServiceBrokerInfo";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            ServiceBrokerInfo serviceBrokerInfo = this.serviceBrokers.RetrieveByName(this.selectedName);
            if (serviceBrokerInfo == null) {
                throw new Exception(String.format(STRERR_RetrieveFailed_arg, this.selectedName));
            }

            /*
             * Update information
             */
            this.name = serviceBrokerInfo.getName();
            this.guid = serviceBrokerInfo.getGuid();
            this.outPasskey = serviceBrokerInfo.getOutPasskey();
            this.inPasskey = serviceBrokerInfo.getInPasskey();
            this.webServiceUrl = serviceBrokerInfo.getServiceUrl();
            this.permitted = serviceBrokerInfo.isPermitted();

            /*
             * Update controls
             */
            this.nameDisabled = true;
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
