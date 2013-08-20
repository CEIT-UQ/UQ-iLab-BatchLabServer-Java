/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import uq.ilabs.labserver.LabServerAppBean;
import uq.ilabs.library.lab.types.LabEquipmentStatus;
import uq.ilabs.library.lab.types.ServiceTypes;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.LabEquipmentAPI;
import uq.ilabs.library.labserver.client.Consts;
import uq.ilabs.library.labserver.client.LabServerSession;
import uq.ilabs.library.labserver.database.LabEquipmentDB;
import uq.ilabs.library.labserver.database.types.LabEquipmentInfo;
import uq.ilabs.library.labserver.database.types.LabServerInfo;
import uq.ilabs.library.labserver.engine.types.LabEquipmentServiceInfo;

/**
 *
 * @author uqlpayne
 */
@Named(value = "labEquipmentBean")
@SessionScoped
public class LabEquipmentBean implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokersBean.class.getName();
    private static final Level logLevel = Level.CONFIG;
    /*
     * String constants
     */
    private static final String STR_LabEquipmentUnit_arg = "LabEquipment Unit %d ";
    private static final String STR_RestartRequired = " LabServer restart is required for settings to take effect.";
    private static final String STR_SaveSuccessful_arg = STR_LabEquipmentUnit_arg + "saved successfully.";
    private static final String STR_UpdateSuccessful_arg = STR_LabEquipmentUnit_arg + "updated successfully.";
    private static final String STR_DeleteSuccessful_arg = STR_LabEquipmentUnit_arg + "deleted successfully.";
    private static final String STR_Online = "Online";
    private static final String STR_Offline = "Offline";
    private static final String STR_LabEquipmentStatus_arg2 = "LabEquipment Status: %s - %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ServiceType = "Service Type";
    private static final String STRERR_ServiceUrl = "Service Url";
    private static final String STRERR_Passkey = "Passkey";
    private static final String STRERR_NotSpecified_arg = "%s: Not specified!";
    private static final String STRERR_RetrieveFailed_arg = STR_LabEquipmentUnit_arg + "could not be retrieved.";
    private static final String STRERR_SaveFailed_arg = STR_LabEquipmentUnit_arg + "could not be saved.";
    private static final String STRERR_UpdateFailed_arg = STR_LabEquipmentUnit_arg + "could not be updated.";
    private static final String STRERR_DeleteFailed_arg = STR_LabEquipmentUnit_arg + "could not be deleted.";
    private static final String STRERR_LabServerNotRegistered = "LabServer is not registered yet!";
    private static final String STRERR_LabEquipmentUnaccessible = "LabEquipment is unaccessible!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @EJB
    private LabServerAppBean labServerAppBean;
    private LabServerSession labServerSession;
    private LabEquipmentDB labEquipmentDB;
    private int[] labEquipmentIds;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String hsomLabEquipmentUnit;
    private String hitLabEquipmentUnit;
    private String hsorServiceType;
    private String hitServiceUrl;
    private String hitPasskey;
    private boolean hcbEnabled;
    private String[] labEquipmentUnits;
    private String[] serviceTypes;
    private boolean registered;
    private String holMessage;
    private String holMessageClass;

    public String getHsomLabEquipmentUnit() {
        return hsomLabEquipmentUnit;
    }

    public void setHsomLabEquipmentUnit(String hsomLabEquipmentUnit) {
        this.hsomLabEquipmentUnit = hsomLabEquipmentUnit;
    }

    public String getHitLabEquipmentUnit() {
        return hitLabEquipmentUnit;
    }

    public void setHitLabEquipmentUnit(String hitLabEquipmentUnit) {
        this.hitLabEquipmentUnit = hitLabEquipmentUnit;
    }

    public String getHsorServiceType() {
        return hsorServiceType;
    }

    public void setHsorServiceType(String hsorServiceType) {
        this.hsorServiceType = hsorServiceType;
    }

    public String getHitServiceUrl() {
        return hitServiceUrl;
    }

    public void setHitServiceUrl(String hitServiceUrl) {
        this.hitServiceUrl = hitServiceUrl;
    }

    public String getHitPasskey() {
        return hitPasskey;
    }

    public void setHitPasskey(String hitPasskey) {
        this.hitPasskey = hitPasskey;
    }

    public boolean isHcbEnabled() {
        return hcbEnabled;
    }

    public void setHcbEnabled(boolean hcbEnabled) {
        this.hcbEnabled = hcbEnabled;
    }

    public String[] getLabEquipmentUnits() {
        return labEquipmentUnits;
    }

    public String[] getServiceTypes() {
        return serviceTypes;
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
     * Creates a new instance of LabEquipmentBean
     */
    public LabEquipmentBean() {
        final String methodName = "ServiceBrokersBean";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        this.labServerSession = (LabServerSession) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Consts.STRSSN_LabServer);
        this.labEquipmentDB = this.labServerSession.getLabManagement().getLabEquipmentDB();

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
            this.CreateLabEquipmentList();
            this.CreateServiceTypeList();

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
    public String actionCreateGuid() {

        this.hitPasskey = UUID.randomUUID().toString();

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @return
     */
    public String actionSelect() {

        if (this.hsomLabEquipmentUnit != null && this.hsomLabEquipmentUnit.equals(this.labEquipmentUnits[0]) == false) {
            PopulateLabEquipmentInfo();
            this.hsomLabEquipmentUnit = this.labEquipmentUnits[0];
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
        LabEquipmentInfo labEquipmentInfo = this.Parse(null);
        if (labEquipmentInfo != null) {
            try {
                /*
                 * Add information for a new LabEquipment unit
                 */
                int unit = labEquipmentInfo.getId();
                if (this.labEquipmentDB.Add(labEquipmentInfo) < 0) {
                    throw new Exception(String.format(STRERR_SaveFailed_arg, labEquipmentInfo.getId()));
                }

                /*
                 * Refresh the LabEquipment list
                 */
                this.CreateLabEquipmentList();

                /*
                 * Information saved successfully
                 */
                this.registered = true;
                String message = String.format(STR_SaveSuccessful_arg, unit);
                if (this.labServerAppBean.isServiceStarted() == true) {
                    message += STR_RestartRequired;
                }
                ShowMessageInfo(message);

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
             * Get the LabEquipmentInfo for the selected LabEquipment unit
             */
            int unit = Integer.parseInt(this.hitLabEquipmentUnit);
            LabEquipmentInfo labEquipmentInfo = this.labEquipmentDB.RetrieveById(this.labEquipmentIds[unit]);
            if (labEquipmentInfo == null) {
                throw new Exception(String.format(STRERR_RetrieveFailed_arg, this.hitLabEquipmentUnit));
            }

            /*
             * Save these before parsing to check if they have changed and a LabServer restart is required
             */
            ServiceTypes serviceType = labEquipmentInfo.getServiceType();
            String serviceUrl = labEquipmentInfo.getServiceUrl();
            String passkey = labEquipmentInfo.getPasskey();

            /*
             * Parse the web page information
             */
            labEquipmentInfo = this.Parse(labEquipmentInfo);
            if (labEquipmentInfo != null) {
                /*
                 * Update information for an existing LabEquipment unit
                 */
                if (this.labEquipmentDB.Update(labEquipmentInfo) == false) {
                    throw new Exception(String.format(STRERR_UpdateFailed_arg, unit));
                }

                /*
                 * Check to see if the LabServer service is running
                 */
                boolean restartRequired = false;
                if (this.labServerAppBean.isServiceStarted() == true) {
                    /*
                     * Check what has changed that requires a LabServer restart
                     */
                    if (serviceType != labEquipmentInfo.getServiceType()) {
                        /*
                         * Service type has changed
                         */
                        restartRequired = true;
                    }
                    if ((serviceUrl != null && serviceUrl.equalsIgnoreCase(labEquipmentInfo.getServiceUrl()) == false)
                            || (labEquipmentInfo.getServiceUrl() != null && labEquipmentInfo.getServiceUrl().equalsIgnoreCase(serviceUrl) == false)) {
                        /*
                         * Service url has changed
                         */
                        restartRequired = true;
                    }
                    if ((passkey != null && passkey.equalsIgnoreCase(labEquipmentInfo.getPasskey()) == false)
                            || (labEquipmentInfo.getPasskey() != null && labEquipmentInfo.getPasskey().equalsIgnoreCase(passkey) == false)) {
                        /*
                         * Passkey has changed
                         */
                        restartRequired = true;
                    }

                    /*
                     * If a restart is not required then update the LabEquipmentServiceInfo
                     */
                    if (restartRequired == false) {
                        LabEquipmentServiceInfo labEquipmentServiceInfo = this.labServerAppBean.getLabManagement().GetLabEquipmentServiceInfo(labEquipmentInfo.getId());
                        labEquipmentServiceInfo.getLabEquipmentInfo().setEnabled(labEquipmentInfo.isEnabled());
                    }
                }

                /*
                 * Information updated successfully
                 */
                String message = String.format(STR_UpdateSuccessful_arg, unit);
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
     * @return
     */
    public String actionDelete() {
        final String methodName = "actionDelete";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Get the LabEquipmentInfo for the selected LabEquipment unit
             */
            int unit = Integer.parseInt(this.hitLabEquipmentUnit);
            LabEquipmentInfo labEquipmentInfo = this.labEquipmentDB.RetrieveById(this.labEquipmentIds[unit]);
            if (labEquipmentInfo == null) {
                throw new Exception(String.format(STRERR_RetrieveFailed_arg, unit));
            }

            /*
             * Delete the LabEquipment unit
             */
            if (this.labEquipmentDB.Delete(labEquipmentInfo.getId()) == false) {
                throw new Exception(String.format(STRERR_DeleteFailed_arg, unit));
            }

            /*
             * Refresh the LabEquipment list and clear the page
             */
            this.CreateLabEquipmentList();
            actionNew();

            /*
             * Information deleted successfully
             */
            String message = String.format(STR_DeleteSuccessful_arg, unit);
            if (this.labServerAppBean.isServiceStarted() == true) {
                message += STR_RestartRequired;
            }
            ShowMessageInfo(message);

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
    public String actionTest() {
        final String methodName = "actionTest";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Get the LabServer Guid
             */
            LabServerInfo labServerInfo = this.labServerAppBean.getLabManagement().getLabServerInfo();
            if (labServerInfo == null) {
                throw new NullPointerException(STRERR_LabServerNotRegistered);
            }
            String labServerGuid = labServerInfo.getGuid();
            if (labServerGuid == null) {
                throw new NullPointerException(STRERR_LabServerNotRegistered);
            }

            /*
             * Get a proxy to the LabEquipment service
             */
            LabEquipmentAPI labEquipmentAPI = new LabEquipmentAPI(ServiceTypes.ToType(this.hsorServiceType), this.hitServiceUrl);
            labEquipmentAPI.setIdentifier(labServerGuid);
            labEquipmentAPI.setPasskey(this.hitPasskey);

            /*
             * Get the status of the LabEquipment
             */
            LabEquipmentStatus labEquipmentStatus = labEquipmentAPI.GetLabEquipmentStatus();
            if (labEquipmentStatus == null) {
                throw new NullPointerException(STRERR_LabEquipmentUnaccessible);
            }

            ShowMessageInfo(String.format(STR_LabEquipmentStatus_arg2,
                    labEquipmentStatus.isOnline() ? STR_Online : STR_Offline, labEquipmentStatus.getStatusMessage()));

        } catch (Exception ex) {
            ShowMessageError(ex.getMessage().split("\n")[0]);
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
        this.hsomLabEquipmentUnit = null;
        this.hsorServiceType = null;
        this.hitLabEquipmentUnit = null;
        this.hitServiceUrl = null;
        this.hitPasskey = null;
        this.hcbEnabled = false;

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
     */
    private void CreateLabEquipmentList() {

        try {
            /*
             * Get the LabEquipment units
             */
            ArrayList<LabEquipmentInfo> labEquipmentInfoList = this.labEquipmentDB.RetrieveAll();
            if (labEquipmentInfoList != null && labEquipmentInfoList.size() > 0) {
                this.labEquipmentIds = new int[labEquipmentInfoList.size()];
                this.labEquipmentUnits = new String[labEquipmentInfoList.size() + 1];
                for (int i = 0; i < labEquipmentInfoList.size(); i++) {
                    this.labEquipmentIds[i] = labEquipmentInfoList.get(i).getId();
                    this.labEquipmentUnits[i + 1] = Integer.toString(i);
                }
            } else {
                this.labEquipmentUnits = new String[1];
            }
            this.labEquipmentUnits[0] = "";
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }
    }

    /**
     *
     */
    private void CreateServiceTypeList() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (ServiceTypes serviceType : ServiceTypes.values()) {
            if (serviceType.getValue() >= 0) {
                arrayList.add(serviceType.toString());
            }
        }
        this.serviceTypes = (String[]) arrayList.toArray(new String[0]);
    }

    /**
     *
     */
    private void PopulateLabEquipmentInfo() {
        final String methodName = "PopulateLabEquipmentInfo";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            int unit = Integer.parseInt(this.hsomLabEquipmentUnit);
            LabEquipmentInfo labEquipmentInfo = this.labEquipmentDB.RetrieveById(this.labEquipmentIds[unit]);
            if (labEquipmentInfo == null) {
                throw new Exception(String.format(STRERR_RetrieveFailed_arg, this.hsomLabEquipmentUnit));
            }

            /*
             * Update information
             */
            this.hitLabEquipmentUnit = Integer.toString(unit);
            this.hsorServiceType = labEquipmentInfo.getServiceType().toString();
            this.hitServiceUrl = labEquipmentInfo.getServiceUrl();
            this.hitPasskey = labEquipmentInfo.getPasskey();
            this.hcbEnabled = labEquipmentInfo.isEnabled();

            /*
             * Update controls
             */
            this.registered = true;

        } catch (Exception ex) {
            ShowMessageError(ex.getMessage());
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param labEquipmentInfo
     * @return LabEquipmentInfo
     */
    private LabEquipmentInfo Parse(LabEquipmentInfo labEquipmentInfo) {
        final String methodName = "Parse";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check if LabEquipmentInfo has been provided
             */
            if (labEquipmentInfo == null) {
                /*
                 * Create instance of LabEquipmentInfo ready to fill in
                 */
                labEquipmentInfo = new LabEquipmentInfo();

                /*
                 * Assign the next available LabEquipment unit number
                 */
                labEquipmentInfo.setId((this.labEquipmentIds != null) ? this.labEquipmentIds.length : 0);
                this.hitLabEquipmentUnit = Integer.toString(labEquipmentInfo.getId());
            }

            /*
             * Check that ServiceType has been specified
             */
            ServiceTypes serviceType = ServiceTypes.ToType(this.hsorServiceType);
            if (serviceType == ServiceTypes.Unknown) {
                throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_ServiceType));
            }
            labEquipmentInfo.setServiceType(serviceType);

            /*
             * Check that ServiceUrl has been entered
             */
            this.hitServiceUrl = this.hitServiceUrl.trim();
            if (this.hitServiceUrl.isEmpty() == true) {
                throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_ServiceUrl));
            }
            labEquipmentInfo.setServiceUrl(this.hitServiceUrl);

            /*
             * Check that Passkey has been entered
             */
            this.hitPasskey = this.hitPasskey.trim();
            if (this.hitPasskey.isEmpty() == true) {
                throw new Exception(String.format(STRERR_NotSpecified_arg, STRERR_Passkey));
            }
            labEquipmentInfo.setPasskey(this.hitPasskey);

            labEquipmentInfo.setEnabled(this.hcbEnabled);

        } catch (Exception ex) {
            this.ShowMessageError(ex.getMessage());
            labEquipmentInfo = null;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labEquipmentInfo;
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
