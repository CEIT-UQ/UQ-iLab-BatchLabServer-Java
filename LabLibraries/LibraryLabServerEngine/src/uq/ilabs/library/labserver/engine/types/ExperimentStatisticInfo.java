/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine.types;

import java.util.Date;

/**
 *
 * @author uqlpayne
 */
public class ExperimentStatisticInfo {

    private int id;
    private int experimentId;
    private String sbName;
    private String userGroup;
    private int priorityHint;
    private int estimatedExecTime;
    private Date timeSubmitted;
    private int queueLength;
    private int estimatedWaitTime;
    private Date timeStarted;
    private int unitId;
    private Date timeCompleted;
    private int actualExecTime;
    private boolean cancelled;

    public int getActualExecTime() {
        return actualExecTime;
    }

    public void setActualExecTime(int actualExecTime) {
        this.actualExecTime = actualExecTime;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public int getEstimatedExecTime() {
        return estimatedExecTime;
    }

    public void setEstimatedExecTime(int estimatedExecTime) {
        this.estimatedExecTime = estimatedExecTime;
    }

    public int getEstimatedWaitTime() {
        return estimatedWaitTime;
    }

    public void setEstimatedWaitTime(int estimatedWaitTime) {
        this.estimatedWaitTime = estimatedWaitTime;
    }

    public int getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriorityHint() {
        return priorityHint;
    }

    public void setPriorityHint(int priorityHint) {
        this.priorityHint = priorityHint;
    }

    public int getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
    }

    public String getSbName() {
        return sbName;
    }

    public void setSbName(String sbName) {
        this.sbName = sbName;
    }

    public Date getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(Date timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    public Date getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(Date timeStarted) {
        this.timeStarted = timeStarted;
    }

    public Date getTimeSubmitted() {
        return timeSubmitted;
    }

    public void setTimeSubmitted(Date timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }
}
