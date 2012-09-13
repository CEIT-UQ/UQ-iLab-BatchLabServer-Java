/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labequipment.service;

import java.util.logging.Level;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.EquipmentManager;
import uq.ilabs.library.labequipment.engine.ConfigProperties;
import uq.ilabs.library.labequipment.engine.types.LabEquipmentStatus;

/**
 *
 * @author uqlpayne
 */
@Singleton
@LocalBean
public class LabEquipmentServiceBean {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentServiceBean.class.getName();
    private static final Level logLevel = Level.INFO;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_ExecutionId_arg = "ExecutionId: %d";
    private static final String STRLOG_IdentifierPasskey_arg2 = "Identifier: '%s'  Passkey: '%s'";
    private static final String STRLOG_LabEquipmentStatus_arg2 = "Online: %s  StatusMessage: %s";
    private static final String STRLOG_TimeUntilReady_arg = "TimeUntilReady: %d seconds";
    private static final String STRLOG_Validation_arg3 = "Accepted: %s  ExecutionTime: %d  ErrorMessage: %s";
    private static final String STRLOG_ExecutionStatus_arg5 = "ExecutionId: %d  ExecuteStatus: %s  ResultStatus: %s  TimeRemaining: %d  ErrorMessage: %s";
    private static final String STRLOG_Success_arg = "Success: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_EquipmentManagerCreateFailed = "EquipmentManager.Create() failed!";
    private static final String STRERR_EquipmentManagerStartFailed = "EquipmentManager.Start() failed!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private ConfigProperties configProperties;
    private EquipmentManager equipmentManager;
    //</editor-fold>

    /**
     * Constructor - Seems that this gets called when the project is deployed which is unexpected. To get around this,
     * check to see if the LabEquipment has been initialised and the configuration properties set. Can't do logging
     * until the ServiceBroker has been initialised and the logger created.
     */
    public LabEquipmentServiceBean() {
        final String methodName = "LabEquipmentServiceBean";

        /*
         * Check if initialisation needs to be done
         */
        if (LabEquipmentService.isInitialised() == true && this.configProperties == null) {
            Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

            /*
             * Save the configuration properties locally and process
             */
            this.configProperties = LabEquipmentService.getConfigProperties();

            try {
                /*
                 * Create an instance of the the EquipmentManager
                 */
                this.equipmentManager = new EquipmentManager(this.configProperties.getXmlEquipmentConfigPath());
                if (this.equipmentManager == null) {
                    throw new NullPointerException(EquipmentManager.class.getSimpleName());
                }
                if (this.equipmentManager.Create() == false) {
                    throw new RuntimeException(STRERR_EquipmentManagerCreateFailed);
                }

                /*
                 * Start the EquipmentManager
                 */
                if (this.equipmentManager.Start() == false) {
                    throw new RuntimeException(STRERR_EquipmentManagerStartFailed);
                }
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
            }

            Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
        }
    }

    /**
     *
     * @return
     */
    public au.edu.uq.ilab.LabEquipmentStatus GetLabEquipmentStatus() {
        final String methodName = "GetLabEquipmentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Pass on to the Equipment Manager
         */
        LabEquipmentStatus labEquipmentStatus = equipmentManager.GetLabEquipmentStatus();

        /*
         * Convert to return type
         */
        au.edu.uq.ilab.LabEquipmentStatus proxyLabEquipmentStatus = new au.edu.uq.ilab.LabEquipmentStatus();
        proxyLabEquipmentStatus.setOnline(labEquipmentStatus.isOnline());
        proxyLabEquipmentStatus.setStatusMessage(labEquipmentStatus.getStatusMessage());

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_LabEquipmentStatus_arg2,
                labEquipmentStatus.isOnline(), labEquipmentStatus.getStatusMessage()));

        return proxyLabEquipmentStatus;
    }

    /**
     *
     * @return
     */
    public int GetTimeUntilReady() {
        final String methodName = "GetTimeUntilReady";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Pass on to the Equipment Manager
         */
        int timeUntilReady = equipmentManager.GetTimeUntilReady();

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_TimeUntilReady_arg, timeUntilReady));

        return timeUntilReady;
    }

    /**
     *
     * @param xmlSpecification
     * @return
     */
    public au.edu.uq.ilab.Validation Validate(String xmlSpecification) {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                xmlSpecification);

        /*
         * Pass on to the Equipment Manager
         */
        Validation validation = equipmentManager.Validate(xmlSpecification);

        /*
         * Convert to return type
         */
        au.edu.uq.ilab.Validation proxyValidation = new au.edu.uq.ilab.Validation();
        proxyValidation.setAccepted(validation.isAccepted());
        proxyValidation.setErrorMessage(validation.getErrorMessage());
        proxyValidation.setExecutionTime(validation.getExecutionTime());

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Validation_arg3,
                validation.isAccepted(), validation.getExecutionTime(), validation.getErrorMessage()));

        return proxyValidation;
    }

    /**
     *
     * @param xmlSpecification
     * @return
     */
    public au.edu.uq.ilab.ExecutionStatus StartLabExecution(String xmlSpecification) {
        final String methodName = "StartLabExecution";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                xmlSpecification);

        /*
         * Pass on to the Equipment Manager
         */
        ExecutionStatus executionStatus = equipmentManager.StartLabExecution(xmlSpecification);

        /*
         * Convert to return type
         */
        au.edu.uq.ilab.ExecutionStatus proxyExecutionStatus = new au.edu.uq.ilab.ExecutionStatus();
        proxyExecutionStatus.setExecutionId(executionStatus.getExecutionId());
        proxyExecutionStatus.setExecuteStatus(executionStatus.getExecuteStatus().ordinal());
        proxyExecutionStatus.setResultStatus(executionStatus.getResultStatus().ordinal());
        proxyExecutionStatus.setTimeRemaining(executionStatus.getTimeRemaining());
        proxyExecutionStatus.setErrorMessage(executionStatus.getErrorMessage());

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionStatus_arg5, executionStatus.getExecutionId(), executionStatus.getExecuteStatus(),
                executionStatus.getResultStatus(), executionStatus.getTimeRemaining(), executionStatus.getErrorMessage()));

        return proxyExecutionStatus;
    }

    /**
     *
     * @return
     */
    public au.edu.uq.ilab.ExecutionStatus GetLabExecutionStatus(int executionId) {
        final String methodName = "GetLabExecutionStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionId_arg, executionId));

        /*
         * Pass on to the Equipment Manager
         */
        ExecutionStatus executionStatus = equipmentManager.GetLabExecutionStatus(executionId);

        /*
         * Convert to return type
         */
        au.edu.uq.ilab.ExecutionStatus proxyExecutionStatus = new au.edu.uq.ilab.ExecutionStatus();
        proxyExecutionStatus.setExecutionId(executionStatus.getExecutionId());
        proxyExecutionStatus.setExecuteStatus(executionStatus.getExecuteStatus().ordinal());
        proxyExecutionStatus.setResultStatus(executionStatus.getResultStatus().ordinal());
        proxyExecutionStatus.setTimeRemaining(executionStatus.getTimeRemaining());
        proxyExecutionStatus.setErrorMessage(executionStatus.getErrorMessage());

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionStatus_arg5, executionStatus.getExecutionId(), executionStatus.getExecuteStatus(),
                executionStatus.getResultStatus(), executionStatus.getTimeRemaining(), executionStatus.getErrorMessage()));

        return proxyExecutionStatus;
    }

    /**
     *
     * @return String
     */
    public String GetLabExecutionResults(int executionId) {
        final String methodName = "GetLabExecutionResults";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionId_arg, executionId));

        /*
         * Pass on to the Equipment Manager
         */
        String labExecutionResults = equipmentManager.GetLabExecutionResults(executionId);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                labExecutionResults);

        return labExecutionResults;
    }

    /**
     *
     * @return boolean
     */
    public boolean CancelLabExecution(int executionId) {
        final String methodName = "CancelLabExecution";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionId_arg, executionId));

        /*
         * Pass on to the Equipment Manager
         */
        boolean success = equipmentManager.CancelLabExecution(executionId);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @param identifier
     * @param passkey
     * @return boolean
     */
    public boolean Authenticate(String identifier, String passkey) {
        /*
         * Assume this will succeed
         */
        boolean success = true;

        /*
         * Check if authenticating
         */
        if (this.configProperties.isAuthenticating()) {
            if (this.configProperties.isLogAuthentication()) {
                Logfile.Write(String.format(STRLOG_IdentifierPasskey_arg2, identifier, passkey));
            }

            /*
             * Check the identifier and passkey
             */
            success = this.configProperties.getLabServerGuid().equalsIgnoreCase(identifier)
                    && this.configProperties.getLabServerPasskey().equalsIgnoreCase(passkey);
        }

        return success;
    }

    @PreDestroy
    private void preDestroy() {
        final String methodName = "preDestroy";
        Logfile.WriteCalled(Level.INFO, STR_ClassName, methodName);

        /*
         * Close the Equipment Manager
         */
        if (this.equipmentManager != null) {
            this.equipmentManager.Close();
        }

        Logfile.WriteCompleted(Level.INFO, STR_ClassName, methodName);
    }
}
