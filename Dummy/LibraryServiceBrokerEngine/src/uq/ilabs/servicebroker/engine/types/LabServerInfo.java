/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.engine.types;

import uq.ilabs.library.lab.types.ServiceTypes;

/**
 *
 * @author uqlpayne
 */
public class LabServerInfo {

    private String serviceGuid;
    private String serviceUrl;
    private ServiceTypes serviceType;
    private String outgoingPasskey;
    private String incomingPasskey;

    public String getServiceGuid() {
        return serviceGuid;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public ServiceTypes getServiceType() {
        return serviceType;
    }

    public String getOutgoingPasskey() {
        return outgoingPasskey;
    }

    public String getIncomingPasskey() {
        return incomingPasskey;
    }

    public LabServerInfo(String serviceGuid, String serviceUrl, ServiceTypes serviceType, String outgoingPasskey, String incomingPasskey) {
        this.serviceGuid = serviceGuid;
        this.serviceUrl = serviceUrl;
        this.serviceType = serviceType;
        this.outgoingPasskey = outgoingPasskey;
        this.incomingPasskey = incomingPasskey;
    }
}
