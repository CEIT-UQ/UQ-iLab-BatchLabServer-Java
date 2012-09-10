/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.engine.types;

/**
 *
 * @author uqlpayne
 */
public class LabServerInfo {

    private String guid;
    private String serviceUrl;
    private String outgoingPasskey;
    private String incomingPasskey;

    public String getGuid() {
        return guid;
    }

    public String getIncomingPasskey() {
        return incomingPasskey;
    }

    public String getOutgoingPasskey() {
        return outgoingPasskey;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public LabServerInfo(String guid, String serviceUrl, String outgoingPasskey, String incomingPasskey) {
        this.guid = guid;
        this.serviceUrl = serviceUrl;
        this.outgoingPasskey = outgoingPasskey;
        this.incomingPasskey = incomingPasskey;
    }
}
