/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.database.types;

import java.util.Calendar;

/**
 *
 * @author uqlpayne
 */
public class ExperimentStatisticsInfo {

    public static final int MAXLEN_SbName = 32;
    public static final int MAXLEN_UserGroup = 64;
    //
    private int id;
    private int experimentId;
    private String sbName;
    private String userGroup;
    private int priorityHint;
    private int estimatedExecTime;
    private Calendar timeSubmitted;
    private int queueLength;
    private int estimatedWaitTime;
    private Calendar timeStarted;
    private int unitId;
    private Calendar timeCompleted;
    private boolean cancelled;
    private int actualExecTime;

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

    public int getEstimatedExecTime() {
        return estimatedExecTime;
    }

    public void setEstimatedExecTime(int estimatedExecTime) {
        this.estimatedExecTime = estimatedExecTime;
    }

    public Calendar getTimeSubmitted() {
        return timeSubmitted;
    }

    public void setTimeSubmitted(Calendar timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
    }

    public int getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
    }

    public int getEstimatedWaitTime() {
        return estimatedWaitTime;
    }

    public void setEstimatedWaitTime(int estimatedWaitTime) {
        this.estimatedWaitTime = estimatedWaitTime;
    }

    public Calendar getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(Calendar timeStarted) {
        this.timeStarted = timeStarted;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public Calendar getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(Calendar timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public int getActualExecTime() {
        return actualExecTime;
    }

    public void setActualExecTime(int actualExecTime) {
        this.actualExecTime = actualExecTime;
    }

    /**
     *
     */
    public ExperimentStatisticsInfo() {
        this.id = -1;
        this.unitId = -1;
    }
}
