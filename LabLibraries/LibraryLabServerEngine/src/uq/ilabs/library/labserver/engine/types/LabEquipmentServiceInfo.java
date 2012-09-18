/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine.types;

/**
 *
 * @author uqlpayne
 */
public class LabEquipmentServiceInfo {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String serviceUrl;
    private String identifier;
    private String passkey;

    public String getIdentifier() {
        return identifier;
    }

    public String getPasskey() {
        return passkey;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }
    //</editor-fold>

    public LabEquipmentServiceInfo(String serviceUrl, String identifier, String passkey) {
        this.serviceUrl = serviceUrl;
        this.identifier = identifier;
        this.passkey = passkey;
    }
}
