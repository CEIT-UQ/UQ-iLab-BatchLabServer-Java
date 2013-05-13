/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine.types;

import uq.ilabs.library.labserver.database.types.ExperimentQueueInfo;

/**
 * Information about the specified experiment in the queue.
 *
 * @author uqlpayne
 */
public class QueuedExperimentInfo extends ExperimentQueueInfo {

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

    public int getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    /**
     *
     */
    public QueuedExperimentInfo() {
        super();
    }

    /**
     *
     * @param experimentQueueInfo
     */
    public QueuedExperimentInfo(ExperimentQueueInfo experimentQueueInfo) {
        this.id = experimentQueueInfo.getId();
        this.experimentId = experimentQueueInfo.getExperimentId();
        this.sbName = experimentQueueInfo.getSbName();
        this.userGroup = experimentQueueInfo.getUserGroup();
        this.priorityHint = experimentQueueInfo.getPriorityHint();
        this.xmlSpecification = experimentQueueInfo.getXmlSpecification();
        this.estimatedExecTime = experimentQueueInfo.getEstimatedExecTime();
        this.statusCode = experimentQueueInfo.getStatusCode();
        this.unitId = experimentQueueInfo.getUnitId();
        this.cancelled = experimentQueueInfo.isCancelled();
    }
}
