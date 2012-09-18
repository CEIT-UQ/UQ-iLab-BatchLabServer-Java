/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine.types;

import java.util.Date;

/**
 *
 * @author uqlpayne
 */
public class ServiceBrokerInfo {

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
     * URL of the ServiceBroker that will be notified of experiment completion
     */
    private String serviceUrl;
    /**
     * Determines if calls from the ServiceBroker to the LabServer's web service is permitted. If true, the LabServer
     * allows calls from the ServieBroker.
     */
    private boolean permitted;
    /**
     *
     */
    private Date dateCreated;
    /**
     *
     */
    private Date dateModified;

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInPasskey() {
        return inPasskey;
    }

    public void setInPasskey(String inPasskey) {
        this.inPasskey = inPasskey;
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

    public boolean isPermitted() {
        return permitted;
    }

    public void setPermitted(boolean permitted) {
        this.permitted = permitted;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public ServiceBrokerInfo() {
    }

    public ServiceBrokerInfo(String name, String guid, String outPasskey,
            String inPasskey, String serviceUrl, boolean permitted) {
        this.name = name;
        this.guid = guid;
        this.outPasskey = outPasskey;
        this.inPasskey = inPasskey;
        this.serviceUrl = serviceUrl;
        this.permitted = permitted;
    }
}
