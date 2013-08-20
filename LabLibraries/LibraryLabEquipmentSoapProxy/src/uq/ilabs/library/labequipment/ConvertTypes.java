/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment;

import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.LabEquipmentStatus;
import uq.ilabs.library.lab.types.ValidationReport;

/**
 *
 * @author uqlpayne
 */
public class ConvertTypes {

    /**
     *
     * @param proxyExecutionStatus
     * @return ExecutionStatus
     */
    public static ExecutionStatus Convert(uq.ilabs.labequipment.proxy.ExecutionStatus proxyExecutionStatus) {
        ExecutionStatus executionStatus = null;

        if (proxyExecutionStatus != null) {
            executionStatus = new uq.ilabs.library.lab.types.ExecutionStatus();
            executionStatus.setExecutionId(proxyExecutionStatus.getExecutionId());
            executionStatus.setExecuteStatus(ExecutionStatus.Status.ToStatus(proxyExecutionStatus.getExecuteStatus()));
            executionStatus.setResultStatus(ExecutionStatus.Status.ToStatus(proxyExecutionStatus.getResultStatus()));
            executionStatus.setTimeRemaining(proxyExecutionStatus.getTimeRemaining());
            executionStatus.setErrorMessage(proxyExecutionStatus.getErrorMessage());
        }

        return executionStatus;
    }

    /**
     *
     * @param proxyLabEquipmentStatus
     * @return LabEquipmentStatus
     */
    public static LabEquipmentStatus Convert(uq.ilabs.labequipment.proxy.LabEquipmentStatus proxyLabEquipmentStatus) {
        LabEquipmentStatus labEquipmentStatus = null;

        if (proxyLabEquipmentStatus != null) {
            labEquipmentStatus = new LabEquipmentStatus();
            labEquipmentStatus.setOnline(proxyLabEquipmentStatus.isOnline());
            labEquipmentStatus.setStatusMessage(proxyLabEquipmentStatus.getStatusMessage());
        }

        return labEquipmentStatus;
    }

    /**
     *
     * @param proxyValidation
     * @return ValidationReport
     */
    public static ValidationReport Convert(uq.ilabs.labequipment.proxy.Validation proxyValidation) {
        ValidationReport validationReport = null;

        if (proxyValidation != null) {
            validationReport = new ValidationReport();
            validationReport.setAccepted(proxyValidation.isAccepted());
            validationReport.setErrorMessage(proxyValidation.getErrorMessage());
            validationReport.setEstRuntime(proxyValidation.getExecutionTime());
        }

        return validationReport;
    }
}
