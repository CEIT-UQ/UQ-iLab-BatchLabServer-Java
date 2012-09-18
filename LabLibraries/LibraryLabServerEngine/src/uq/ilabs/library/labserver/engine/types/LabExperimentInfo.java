/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine.types;

import uq.ilabs.library.lab.types.StatusCodes;

/**
 *
 * @author uqlpayne
 */
public class LabExperimentInfo {

    private int queueId;
    /**
     * Experiment number (greater than zero).
     */
    private int experimentId;
    /**
     * ServiceBroker's name.
     */
    private String sbName;
    /**
     * User Group for the lab experiment.
     */
    private String userGroup;
    /**
     * Priority of the experiment - unused.
     */
    private int priorityHint;
    /**
     * Experiment specification in XML format.
     */
    private String xmlSpecification;
    /**
     * Estimated execution time of the experiment in seconds.
     */
    private int estExecutionTime;
    /**
     * Status of this experiment.
     */
    private StatusCodes statusCode;
    /**
     * The Id of the setup that will be used to execute this experiment
     */
    private String setupId;
    /**
     * The farm unit number that is executing this experiment.
     */
    private int unitId;
    /**
     * Flag to indicate if the experiment has been cancelled while waiting on the queue.
     */
    private boolean cancelled;

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public int getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    public String getSbName() {
        return sbName;
    }

    public void setSbName(String sbName) {
        this.sbName = sbName;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public int getPriorityHint() {
        return priorityHint;
    }

    public void setPriorityHint(int priorityHint) {
        this.priorityHint = priorityHint;
    }

    public String getXmlSpecification() {
        return xmlSpecification;
    }

    public void setXmlSpecification(String xmlSpecification) {
        this.xmlSpecification = xmlSpecification;
    }

    public int getEstExecutionTime() {
        return estExecutionTime;
    }

    public void setEstExecutionTime(int estExecutionTime) {
        this.estExecutionTime = estExecutionTime;
    }

    public StatusCodes getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCodes statusCode) {
        this.statusCode = statusCode;
    }

    public String getSetupId() {
        return setupId;
    }

    public void setSetupId(String setupId) {
        this.setupId = setupId;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public LabExperimentInfo() {
        this(0, null);
    }

    public LabExperimentInfo(int experimentId, String sbName) {
        this.experimentId = experimentId;
        this.sbName = sbName;
        this.statusCode = StatusCodes.Unknown;
        this.unitId = -1;
        this.cancelled = false;
    }
}
