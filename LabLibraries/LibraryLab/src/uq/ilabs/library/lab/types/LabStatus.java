package uq.ilabs.library.lab.types;

/**
 *
 * @author uqlpayne
 */
public class LabStatus {

    /**
     * True if the LabServer is accepting experiments.
     */
    private boolean online;
    /**
     * Domain-dependent human-readable text describing the status of Lab Server.
     */
    private String labStatusMessage;

    public String getLabStatusMessage() {
        return labStatusMessage;
    }

    public void setLabStatusMessage(String labStatusMessage) {
        this.labStatusMessage = labStatusMessage;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public LabStatus() {
        this.online = false;
        this.labStatusMessage = null;
    }

    public LabStatus(boolean online, String message) {
        this.online = online;
        this.labStatusMessage = message;
    }
}
