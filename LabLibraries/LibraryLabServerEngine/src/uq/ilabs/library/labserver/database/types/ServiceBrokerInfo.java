/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.database.types;

import java.util.Calendar;

/**
 *
 * @author uqlpayne
 */
public class ServiceBrokerInfo {

    public static final int MAXLEN_Name = 32;
    public static final int MAXLEN_Guid = 40;
    public static final int MAXLEN_OutPasskey = 40;
    public static final int MAXLEN_InPasskey = 40;
    public static final int MAXLEN_ServiceUrl = 256;
    /**
     * ServiceBroker's Id in the database
     */
    private int id;
    /**
     * ServiceBroker's name, typically an alias to identify the ServiceBroker by name.
     */
    private String name;
    /**
     * ServiceBroker's GUID, typically a 32 hexadecimal character string.
     */
    private String guid;
    /**
     * URL of the ServiceBroker that will be notified of experiment completion
     */
    private String serviceUrl;
    /**
     * The passkey sent to the LabServer in the SOAP header object. The passkey identifies the calling ServiceBroker to
     * the LabServer.
     */
    private String outPasskey;
    /**
     * The passkey sent to the ServiceBroker in the SOAP header object. The passkey identifies the calling LabServer to
     * the ServiceBroker.
     */
    private String inPasskey;
    /**
     * Determines if calls from the ServiceBroker to the LabServer's web service are permitted. If true, the LabServer
     * allows calls from the ServiceBroker.
     */
    private boolean permitted;
    /**
     *
     */
    private Calendar dateCreated;
    /**
     *
     */
    private Calendar dateModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getOutPasskey() {
        return outPasskey;
    }

    public void setOutPasskey(String outPasskey) {
        this.outPasskey = outPasskey;
    }

    public String getInPasskey() {
        return inPasskey;
    }

    public void setInPasskey(String inPasskey) {
        this.inPasskey = inPasskey;
    }

    public boolean isPermitted() {
        return permitted;
    }

    public void setPermitted(boolean permitted) {
        this.permitted = permitted;
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

    public ServiceBrokerInfo() {
        this.id = -1;
    }
}
