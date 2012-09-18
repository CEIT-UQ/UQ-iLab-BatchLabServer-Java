/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine.types;

import java.util.Calendar;
import uq.ilabs.library.labserver.engine.LabExperimentResult;
import uq.ilabs.library.labserver.engine.drivers.DriverGeneric;

/**
 *
 * @author uqlpayne
 */
public class LabExecutionInfo {

    private LabExperimentInfo labExperimentInfo;
    private DriverGeneric driver;
    private Calendar startTime;
    private LabExperimentResult labExperimentResult;

    public LabExperimentInfo getLabExperimentInfo() {
        return labExperimentInfo;
    }

    public void setLabExperimentInfo(LabExperimentInfo labExperimentInfo) {
        this.labExperimentInfo = labExperimentInfo;
    }

    public DriverGeneric getDriver() {
        return driver;
    }

    public void setDriver(DriverGeneric driver) {
        this.driver = driver;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public LabExperimentResult getLabExperimentResult() {
        return labExperimentResult;
    }

    public void setLabExperimentResult(LabExperimentResult labExperimentResult) {
        this.labExperimentResult = labExperimentResult;
    }
}
