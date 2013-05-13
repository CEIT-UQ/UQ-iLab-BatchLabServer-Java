/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.database.types;

import uq.ilabs.library.lab.types.StatusCodes;

/**
 *
 * @author uqlpayne
 */
public class ExperimentQueueInfo {

    public static final int MAXLEN_SbName = 32;
    public static final int MAXLEN_UserGroup = 64;
    public static final int MAXLEN_StatusCode = 16;
    //
    protected int id;
    protected int experimentId;
    protected String sbName;
    protected String userGroup;
    protected int priorityHint;
    protected String xmlSpecification;
    protected int estimatedExecTime;
    protected StatusCodes statusCode;
    protected int unitId;
    protected boolean cancelled;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getEstimatedExecTime() {
        return estimatedExecTime;
    }

    public void setEstimatedExecTime(int estimatedExecTime) {
        this.estimatedExecTime = estimatedExecTime;
    }

    public StatusCodes getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCodes statusCode) {
        this.statusCode = statusCode;
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

    /**
     *
     */
    public ExperimentQueueInfo() {
        this.id = -1;
        this.unitId = -1;
        this.statusCode = StatusCodes.Unknown;
    }
}
