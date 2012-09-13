/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.engine.types;

/**
 *
 * @author uqlpayne
 */
public class LabEquipmentStatus {

    /**
     * True if the LabEquipment is ready to use.
     */
    private boolean online;
    /**
     * Domain-dependent human-readable text describing the status of the LabEquipment.
     */
    private String statusMessage;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public LabEquipmentStatus() {
    }

    public LabEquipmentStatus(boolean online, String statusMessage) {
        this.online = online;
        this.statusMessage = statusMessage;
    }
}
