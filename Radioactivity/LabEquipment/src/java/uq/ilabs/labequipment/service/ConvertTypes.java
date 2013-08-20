/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labequipment.service;

import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.LabEquipmentStatus;
import uq.ilabs.library.lab.types.Validation;

/**
 *
 * @author uqlpayne
 */
public class ConvertTypes {

    /**
     *
     * @param executionStatus
     * @return au.edu.uq.ilab.ExecutionStatus
     */
    public static au.edu.uq.ilab.ExecutionStatus Convert(ExecutionStatus executionStatus) {
        au.edu.uq.ilab.ExecutionStatus proxyExecutionStatus = null;

        if (executionStatus != null) {
            proxyExecutionStatus = new au.edu.uq.ilab.ExecutionStatus();
            proxyExecutionStatus.setExecutionId(executionStatus.getExecutionId());
            proxyExecutionStatus.setExecuteStatus(executionStatus.getExecuteStatus().getValue());
            proxyExecutionStatus.setResultStatus(executionStatus.getResultStatus().getValue());
            proxyExecutionStatus.setTimeRemaining(executionStatus.getTimeRemaining());
            proxyExecutionStatus.setErrorMessage(executionStatus.getErrorMessage());
        }

        return proxyExecutionStatus;
    }

    /**
     *
     * @param labEquipmentStatus
     * @return au.edu.uq.ilab.LabEquipmentStatus
     */
    public static au.edu.uq.ilab.LabEquipmentStatus Convert(LabEquipmentStatus labEquipmentStatus) {
        au.edu.uq.ilab.LabEquipmentStatus proxyLabEquipmentStatus = null;

        if (labEquipmentStatus != null) {
            proxyLabEquipmentStatus = new au.edu.uq.ilab.LabEquipmentStatus();
            proxyLabEquipmentStatus.setOnline(labEquipmentStatus.isOnline());
            proxyLabEquipmentStatus.setStatusMessage(labEquipmentStatus.getStatusMessage());
        }

        return proxyLabEquipmentStatus;
    }

    /**
     *
     * @param validation
     * @return au.edu.uq.ilab.Validation
     */
    public static au.edu.uq.ilab.Validation Convert(Validation validation) {
        au.edu.uq.ilab.Validation proxyValidation = null;

        if (validation != null) {
            proxyValidation = new au.edu.uq.ilab.Validation();
            proxyValidation.setAccepted(validation.isAccepted());
            proxyValidation.setErrorMessage(validation.getErrorMessage());
            proxyValidation.setExecutionTime(validation.getExecutionTime());
        }

        return proxyValidation;
    }
}
