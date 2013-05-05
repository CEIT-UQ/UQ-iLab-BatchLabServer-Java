/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.database.types;

/**
 *
 * @author uqlpayne
 */
public class ExperimentInfo {

    private int experimentId;
    private String labServerGuid;

    public int getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    public String getLabServerGuid() {
        return labServerGuid;
    }

    public void setLabServerGuid(String labServerGuid) {
        this.labServerGuid = labServerGuid;
    }

    /**
     *
     */
    public ExperimentInfo() {
        this.experimentId = -1;
    }
}
