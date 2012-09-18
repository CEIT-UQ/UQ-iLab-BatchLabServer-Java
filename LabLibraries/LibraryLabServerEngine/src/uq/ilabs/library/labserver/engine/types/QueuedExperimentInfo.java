/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine.types;

/**
 * Information about the specified experiment in the queue.
 *
 * @author uqlpayne
 */
public class QueuedExperimentInfo {

    /**
     * The number of experiments currently in the queue.
     */
    private int queueLength;
    /**
     * Position of this experiment in the queue which start at 1.
     */
    private int position;
    /**
     * The time in seconds that a new experiment will have to wait before can begin execution based on the estimated
     * execution times of the experiments currently in the queue.
     */
    private int waitTime;
    /**
     * The information for this experiment in the queue
     */
    private LabExperimentInfo labExperimentInfo;

    public LabExperimentInfo getLabExperimentInfo() {
        return labExperimentInfo;
    }

    public void setLabExperimentInfo(LabExperimentInfo labExperimentInfo) {
        this.labExperimentInfo = labExperimentInfo;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }
}
