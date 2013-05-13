/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine.types;

import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.labserver.database.types.ExperimentQueueInfo;

/**
 *
 * @author uqlpayne
 */
public class LabExperimentInfo extends ExperimentQueueInfo {

    private String setupName;
    private String setupId;

    public String getSetupName() {
        return setupName;
    }

    public void setSetupName(String setupName) {
        this.setupName = setupName;
    }

    public String getSetupId() {
        return setupId;
    }

    public void setSetupId(String setupId) {
        this.setupId = setupId;
    }

    /**
     *
     */
    public LabExperimentInfo() {
        this(0, null);
    }

    /**
     *
     * @param experimentId
     * @param sbName
     */
    public LabExperimentInfo(int experimentId, String sbName) {
        this.experimentId = experimentId;
        this.sbName = sbName;
        this.statusCode = StatusCodes.Unknown;
        this.unitId = -1;
        this.cancelled = false;
    }

    /**
     *
     * @param experimentQueueInfo
     */
    public LabExperimentInfo(ExperimentQueueInfo experimentQueueInfo) {
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
