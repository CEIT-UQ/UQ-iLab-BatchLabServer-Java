/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labequipment.service;

import au.edu.uq.ilab.AuthHeader;
import java.util.logging.Level;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;
import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.LabEquipmentStatus;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.EquipmentManager;
import uq.ilabs.library.labequipment.engine.ConfigProperties;

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
    private static final String STRERR_EquipmentManagerCreateFailed = "EquipmentManager.Create() failed!";
    private static final String STRERR_EquipmentManagerStartFailed = "EquipmentManager.Start() failed!";
    private static final String STRERR_AccessDenied_arg = "LabEquipment Access Denied - %s";
    private static final String STRERR_AuthHeader = "AuthHeader";
    private static final String STRERR_LabServerGuid = "LabServer Guid";
    private static final String STRERR_Passkey = "Passkey";
    private static final String STRERR_NotSpecified_arg = "%s: Not specified!";
    private static final String STRERR_Invalid_arg = "%s: Invalid!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private ConfigProperties configProperties;
    private EquipmentManager equipmentManager;
    //</editor-fold>

    /**
     * Constructor - Seems that this gets called when the project is deployed which is unexpected. To get around this,
     * check to see if the LabEquipment has been initialised and the configuration properties set. Can't do logging
     * until the LabEquipment has been initialised and the logger created.
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
     * @param authHeader
     * @return au.edu.uq.ilab.LabEquipmentStatus
     */
    public au.edu.uq.ilab.LabEquipmentStatus GetLabEquipmentStatus(AuthHeader authHeader) {
        final String methodName = "GetLabEquipmentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        au.edu.uq.ilab.LabEquipmentStatus proxyLabEquipmentStatus = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the Equipment Manager
             */
            LabEquipmentStatus labEquipmentStatus = this.equipmentManager.GetLabEquipmentStatus();
            proxyLabEquipmentStatus = this.ConvertType(labEquipmentStatus);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        String message = "";
        if (proxyLabEquipmentStatus != null) {
            message = String.format(STRLOG_LabEquipmentStatus_arg2,
                    proxyLabEquipmentStatus.isOnline(), proxyLabEquipmentStatus.getStatusMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName, message);

        return proxyLabEquipmentStatus;
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
            timeUntilReady = this.equipmentManager.GetTimeUntilReady();

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
     * @return au.edu.uq.ilab.Validation
     */
    public au.edu.uq.ilab.Validation Validate(AuthHeader authHeader, String xmlSpecification) {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                xmlSpecification);

        au.edu.uq.ilab.Validation proxyValidation = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the Equipment Manager
             */
            Validation validation = this.equipmentManager.Validate(xmlSpecification);
            proxyValidation = this.ConvertType(validation);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        String message = "";
        if (proxyValidation != null) {
            message = String.format(STRLOG_Validation_arg3,
                    proxyValidation.isAccepted(), proxyValidation.getExecutionTime(), proxyValidation.getErrorMessage());
        }
        
        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName, message);

        return proxyValidation;
    }

    /**
     *
     * @param authHeader
     * @param xmlSpecification
     * @return au.edu.uq.ilab.ExecutionStatus
     */
    public au.edu.uq.ilab.ExecutionStatus StartLabExecution(AuthHeader authHeader, String xmlSpecification) {
        final String methodName = "StartLabExecution";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                xmlSpecification);

        au.edu.uq.ilab.ExecutionStatus proxyExecutionStatus = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the Equipment Manager
             */
            ExecutionStatus executionStatus = this.equipmentManager.StartLabExecution(xmlSpecification);
            proxyExecutionStatus = this.ConvertType(executionStatus);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        String message = "";
        if (proxyExecutionStatus != null) {
            message = String.format(STRLOG_ExecutionStatus_arg5, proxyExecutionStatus.getExecutionId(), proxyExecutionStatus.getExecuteStatus(),
                    proxyExecutionStatus.getResultStatus(), proxyExecutionStatus.getTimeRemaining(), proxyExecutionStatus.getErrorMessage());
        }
        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName, message);

        return proxyExecutionStatus;
    }

    /**
     *
     * @param authHeader
     * @param executionId
     * @return au.edu.uq.ilab.ExecutionStatus
     */
    public au.edu.uq.ilab.ExecutionStatus GetLabExecutionStatus(AuthHeader authHeader, int executionId) {
        final String methodName = "GetLabExecutionStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionId_arg, executionId));

        au.edu.uq.ilab.ExecutionStatus proxyExecutionStatus = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the Equipment Manager
             */
            ExecutionStatus executionStatus = this.equipmentManager.GetLabExecutionStatus(executionId);
            proxyExecutionStatus = this.ConvertType(executionStatus);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        String message = "";
        if (proxyExecutionStatus != null) {
            message = String.format(STRLOG_ExecutionStatus_arg5, proxyExecutionStatus.getExecutionId(), proxyExecutionStatus.getExecuteStatus(),
                    proxyExecutionStatus.getResultStatus(), proxyExecutionStatus.getTimeRemaining(), proxyExecutionStatus.getErrorMessage());
        }
        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName, message);

        return proxyExecutionStatus;
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
            labExecutionResults = this.equipmentManager.GetLabExecutionResults(executionId);

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
            success = this.equipmentManager.CancelLabExecution(executionId);

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
     * @param identifier
     * @param passkey
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
        if (this.configProperties.isLogAuthentication()) {
            if (authHeader == null) {
                Logfile.Write(STRLOG_AuthHeaderNull);
            } else {
                Logfile.Write(String.format(STRLOG_IdentifierPasskey_arg2, authHeader.getIdentifier(), authHeader.getPassKey()));
            }
        }

        try {
            if (this.configProperties.isAuthenticating() == true) {
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
                if (this.configProperties.getLabServerGuid().equalsIgnoreCase(authHeader.getIdentifier()) == false) {
                    throw new IllegalArgumentException(String.format(STRERR_Invalid_arg, STRERR_LabServerGuid));
                }

                /*
                 * Verify the passkey
                 */
                if (authHeader.getPassKey() == null) {
                    throw new NullPointerException(String.format(STRERR_NotSpecified_arg, STRERR_Passkey));
                }
                if (this.configProperties.getLabServerPasskey().equalsIgnoreCase(authHeader.getPassKey()) == false) {
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

            /*
             * Create a SOAPFaultException to be thrown all the way back to the caller
             */
            try {
                SOAPFault fault = SOAPFactory.newInstance().createFault();
                fault.setFaultString(message);
                throw new SOAPFaultException(fault);
            } catch (SOAPException e) {
            }
        }

        return success;
    }

    /**
     *
     * @param labEquipmentStatus
     * @return au.edu.uq.ilab.LabEquipmentStatus
     */
    private au.edu.uq.ilab.LabEquipmentStatus ConvertType(LabEquipmentStatus labEquipmentStatus) {
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
    private au.edu.uq.ilab.Validation ConvertType(Validation validation) {
        au.edu.uq.ilab.Validation proxyValidation = null;

        if (validation != null) {
            proxyValidation = new au.edu.uq.ilab.Validation();
            proxyValidation.setAccepted(validation.isAccepted());
            proxyValidation.setErrorMessage(validation.getErrorMessage());
            proxyValidation.setExecutionTime(validation.getExecutionTime());
        }

        return proxyValidation;
    }

    /**
     *
     * @param executionStatus
     * @return au.edu.uq.ilab.ExecutionStatus
     */
    private au.edu.uq.ilab.ExecutionStatus ConvertType(ExecutionStatus executionStatus) {
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
     */
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

        /*
         * Close the logfile
         */
        Logfile.CloseLogger();

        Logfile.WriteCompleted(Level.INFO, STR_ClassName, methodName);
    }
}
