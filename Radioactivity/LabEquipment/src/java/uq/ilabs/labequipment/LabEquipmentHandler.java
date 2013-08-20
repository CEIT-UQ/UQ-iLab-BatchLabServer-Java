/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labequipment;

import java.util.logging.Level;
import uq.ilabs.library.lab.types.AuthHeader;
import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.LabEquipmentStatus;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.engine.ConfigProperties;

/**
 *
 * @author uqlpayne
 */
public class LabEquipmentHandler {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentHandler.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_AuthHeaderNull = "AuthHeader: null";
    private static final String STRLOG_IdentifierPasskey_arg2 = "Identifier: '%s'  Passkey: '%s'";
    private static final String STRLOG_ExecutionId_arg = "ExecutionId: %d";
    private static final String STRLOG_LabEquipmentStatus_arg2 = "Online: %s  StatusMessage: %s";
    private static final String STRLOG_TimeUntilReady_arg = "TimeUntilReady: %d seconds";
    private static final String STRLOG_Validation_arg3 = "Accepted: %s  ExecutionTime: %d  ErrorMessage: %s";
    private static final String STRLOG_ExecutionStatus_arg5 = "ExecutionId: %d  ExecuteStatus: %s  ResultStatus: %s  TimeRemaining: %d  ErrorMessage: %s";
    private static final String STRLOG_Success_arg = "Success: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_AccessDenied_arg = "LabEquipment Access Denied - %s";
    private static final String STRERR_AuthHeader = "AuthHeader";
    private static final String STRERR_LabServerGuid = "LabServer Guid";
    private static final String STRERR_Passkey = "Passkey";
    private static final String STRERR_NotSpecified_arg = "%s: Not specified!";
    private static final String STRERR_Invalid_arg = "%s: Invalid!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabEquipmentAppBean labEquipmentBean;
    //</editor-fold>

    /**
     *
     * @param labEquipmentBean
     */
    public LabEquipmentHandler(LabEquipmentAppBean labEquipmentBean) {
        final String methodName = "LabServerHandler";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        this.labEquipmentBean = labEquipmentBean;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param authHeader
     * @return LabEquipmentStatus
     */
    public LabEquipmentStatus GetLabEquipmentStatus(AuthHeader authHeader) {
        final String methodName = "GetLabEquipmentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        LabEquipmentStatus labEquipmentStatus = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the Equipment Manager
             */
            labEquipmentStatus = this.labEquipmentBean.getEquipmentManager().GetLabEquipmentStatus();

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        String message = (labEquipmentStatus != null)
                ? String.format(STRLOG_LabEquipmentStatus_arg2, labEquipmentStatus.isOnline(), labEquipmentStatus.getStatusMessage())
                : "";

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName, message);

        return labEquipmentStatus;
    }

    /**
     *
     * @param authHeader
     * @return int
     */
    public int GetTimeUntilReady(AuthHeader authHeader) {
        final String methodName = "GetTimeUntilReady";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int timeUntilReady = -1;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the Equipment Manager
             */
            timeUntilReady = this.labEquipmentBean.getEquipmentManager().GetTimeUntilReady();

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_TimeUntilReady_arg, timeUntilReady));

        return timeUntilReady;
    }

    /**
     *
     * @param authHeader
     * @param xmlSpecification
     * @return Validation
     */
    public Validation Validate(AuthHeader authHeader, String xmlSpecification) {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                xmlSpecification);

        Validation validation = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the Equipment Manager
             */
            validation = this.labEquipmentBean.getEquipmentManager().Validate(xmlSpecification);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        String message = (validation != null)
                ? String.format(STRLOG_Validation_arg3,
                validation.isAccepted(), validation.getExecutionTime(), validation.getErrorMessage()) : null;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName, message);

        return validation;
    }

    /**
     *
     * @param authHeader
     * @param xmlSpecification
     * @return ExecutionStatus
     */
    public ExecutionStatus StartLabExecution(AuthHeader authHeader, String xmlSpecification) {
        final String methodName = "StartLabExecution";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                xmlSpecification);

        ExecutionStatus executionStatus = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the Equipment Manager
             */
            executionStatus = this.labEquipmentBean.getEquipmentManager().StartLabExecution(xmlSpecification);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        String message = (executionStatus != null)
                ? String.format(STRLOG_ExecutionStatus_arg5, executionStatus.getExecutionId(), executionStatus.getExecuteStatus(),
                executionStatus.getResultStatus(), executionStatus.getTimeRemaining(), executionStatus.getErrorMessage())
                : null;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName, message);
        return executionStatus;
    }

    /**
     *
     * @param authHeader
     * @param executionId
     * @return ExecutionStatus
     */
    public ExecutionStatus GetLabExecutionStatus(AuthHeader authHeader, int executionId) {
        final String methodName = "GetLabExecutionStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionId_arg, executionId));

        ExecutionStatus executionStatus = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the Equipment Manager
             */
            executionStatus = this.labEquipmentBean.getEquipmentManager().GetLabExecutionStatus(executionId);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        String message = (executionStatus != null)
                ? String.format(STRLOG_ExecutionStatus_arg5, executionStatus.getExecutionId(), executionStatus.getExecuteStatus(),
                executionStatus.getResultStatus(), executionStatus.getTimeRemaining(), executionStatus.getErrorMessage())
                : "";


        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName, message);
        return executionStatus;
    }

    /**
     *
     * @param authHeader
     * @param executionId
     * @return String
     */
    public String GetLabExecutionResults(AuthHeader authHeader, int executionId) {
        final String methodName = "GetLabExecutionResults";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionId_arg, executionId));

        String labExecutionResults = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the Equipment Manager
             */
            labExecutionResults = this.labEquipmentBean.getEquipmentManager().GetLabExecutionResults(executionId);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                labExecutionResults);

        return labExecutionResults;
    }

    /**
     *
     * @param authHeader
     * @param executionId
     * @return boolean
     */
    public boolean CancelLabExecution(AuthHeader authHeader, int executionId) {
        final String methodName = "CancelLabExecution";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionId_arg, executionId));

        boolean success = false;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the Equipment Manager
             */
            success = this.labEquipmentBean.getEquipmentManager().CancelLabExecution(executionId);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    //================================================================================================================//
    /**
     *
     * @param authHeader
     * @return boolean
     */
    private boolean Authenticate(AuthHeader authHeader) {
        /*
         * Assume this will fail
         */
        boolean success = false;

        /*
         * Check if logging authentication information
         */
        ConfigProperties configProperties = this.labEquipmentBean.getConfigProperties();
        if (configProperties.isLogAuthentication()) {
            if (authHeader == null) {
                Logfile.Write(STRLOG_AuthHeaderNull);
            } else {
                Logfile.Write(String.format(STRLOG_IdentifierPasskey_arg2, authHeader.getIdentifier(), authHeader.getPasskey()));
            }
        }

        try {
            if (configProperties.isAuthenticating() == true) {
                /*
                 * Check that the AuthHeader is specified
                 */
                if (authHeader == null) {
                    throw new NullPointerException(String.format(STRERR_NotSpecified_arg, STRERR_AuthHeader));
                }

                /*
                 * Verify the LabServer Guid
                 */
                if (authHeader.getIdentifier() == null) {
                    throw new NullPointerException(String.format(STRERR_NotSpecified_arg, STRERR_LabServerGuid));
                }
                if (configProperties.getLabServerGuid().equalsIgnoreCase(authHeader.getIdentifier()) == false) {
                    throw new IllegalArgumentException(String.format(STRERR_Invalid_arg, STRERR_LabServerGuid));
                }

                /*
                 * Verify the passkey
                 */
                if (authHeader.getPasskey() == null) {
                    throw new NullPointerException(String.format(STRERR_NotSpecified_arg, STRERR_Passkey));
                }
                if (configProperties.getLabServerPasskey().equalsIgnoreCase(authHeader.getPasskey()) == false) {
                    throw new IllegalArgumentException(String.format(STRERR_Invalid_arg, STRERR_Passkey));
                }
            }

            /*
             * Successfully authenticated
             */
            success = true;

        } catch (NullPointerException | IllegalArgumentException ex) {
            String message = String.format(STRERR_AccessDenied_arg, ex.getMessage());
            Logfile.WriteError(message);
            throw new RuntimeException(message);
        }

        return success;
    }
}
