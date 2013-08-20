/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.database.types;

import java.util.Calendar;
import uq.ilabs.library.lab.types.ServiceTypes;

/**
 *
 * @author uqlpayne
 */
public class LabEquipmentInfo {

    public static final int MAXLEN_ServiceUrl = 256;
    public static final int MAXLEN_Passkey = 40;
    //
    private int id;
    private ServiceTypes serviceType;
    private String serviceUrl;
    private String passkey;
    private boolean enabled;
    private Calendar dateCreated;
    private Calendar dateModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ServiceTypes getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceTypes serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getPasskey() {
        return passkey;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Calendar getDateModified() {
        return dateModified;
    }

    public void setDateModified(Calendar dateModified) {
        this.dateModified = dateModified;
    }

    /**
     *
     */
    public LabEquipmentInfo() {
        this.id = -1;
    }
}
