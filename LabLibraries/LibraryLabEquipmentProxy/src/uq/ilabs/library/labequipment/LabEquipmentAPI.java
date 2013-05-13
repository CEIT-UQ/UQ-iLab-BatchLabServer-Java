/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment;

import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.soap.SOAPFaultException;
import uq.ilabs.labequipment.AuthHeader;
import uq.ilabs.labequipment.LabEquipmentService;
import uq.ilabs.labequipment.LabEquipmentServiceSoap;
import uq.ilabs.labequipment.ObjectFactory;
import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.LabEquipmentStatus;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class LabEquipmentAPI {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentAPI.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_ServiceUrl_arg = "ServiceUrl: '%s'";
    private static final String STRLOG_TimeUntilReady_arg = "TimeUntilReady: %d seconds";
    private static final String STRLOG_Validation_arg3 = "Accepted: %s  ExecutionTime: %.1f  ErrorMessage: %s";
    private static final String STRLOG_Success_arg = "Success: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ServiceUrl = "serviceUrl";
    private static final String STRERR_LabEquipmentUnaccessible = "LabEquipment is unaccessible!";
    /*
     * Constants
     */
    private static int INT_RetryCount = 3;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabEquipmentServiceSoap labEquipmentProxy;
    private QName qnameAuthHeader;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String identifier;
    private String passkey;
    private int retryCount;

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    //</editor-fold>

    /**
     *
     * @param unitId
     */
    public LabEquipmentAPI(String serviceUrl) throws Exception {
        final String methodName = "LabEquipmentAPI";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ServiceUrl_arg, serviceUrl));

        try {
            /*
             * Check that parameters are valid
             */
            if (serviceUrl == null) {
                throw new NullPointerException(STRERR_ServiceUrl);
            }
            serviceUrl = serviceUrl.trim();
            if (serviceUrl.isEmpty()) {
                throw new IllegalArgumentException(STRERR_ServiceUrl);
            }

            /*
             * Create a proxy for the LabEquipment's web service and set the web service URL
             */
            LabEquipmentService labEquipmentService = new LabEquipmentService();
            this.labEquipmentProxy = labEquipmentService.getLabEquipmentServiceSoap();
            ((BindingProvider) this.labEquipmentProxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUrl);

            /*
             * Get authentication header QName
             */
            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<AuthHeader> jaxbElementAuthHeader = objectFactory.createAuthHeader(new AuthHeader());
            this.qnameAuthHeader = jaxbElementAuthHeader.getName();

            /*
             * Initialise local variables
             */
            this.retryCount = INT_RetryCount;

        } catch (NullPointerException | IllegalArgumentException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return @throws Exception
     */
    public LabEquipmentStatus GetLabEquipmentStatus() throws Exception {
        final String methodName = "GetLabEquipmentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        LabEquipmentStatus labEquipmentStatus = null;

        int retries = this.retryCount;
        while (true) {
            try {
                /*
                 * Set the authentication information and call the web service
                 */
                this.SetAuthHeader();
                uq.ilabs.labequipment.LabEquipmentStatus proxyLabEquipmentStatus = this.labEquipmentProxy.getLabEquipmentStatus();
                labEquipmentStatus = this.ConvertType(proxyLabEquipmentStatus);
                break;

            } catch (SOAPFaultException ex) {
                Logfile.Write(ex.getMessage());
                throw new ProtocolException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new ProtocolException(STRERR_LabEquipmentUnaccessible);
                }
            }
        }

        return labEquipmentStatus;
    }

    /**
     *
     * @return @throws Exception
     */
    public int GetTimeUntilReady() {
        final String methodName = "GetTimeUntilReady";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int timeUntilReady = -1;

        int retries = this.retryCount;
        while (true) {
            try {
                /*
                 * Set the authentication information and call the web service
                 */
                this.SetAuthHeader();
                timeUntilReady = this.labEquipmentProxy.getTimeUntilReady();
                break;

            } catch (SOAPFaultException ex) {
                Logfile.Write(ex.getMessage());
                throw new ProtocolException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new ProtocolException(STRERR_LabEquipmentUnaccessible);
                }
            }
        }

        Logfile.WriteCompleted(STR_ClassName, methodName,
                String.format(STRLOG_TimeUntilReady_arg, timeUntilReady));

        return timeUntilReady;
    }

    /**
     *
     * @param xmlSpecification
     * @return
     * @throws Exception
     */
    public ValidationReport Validate(String xmlSpecification) throws Exception {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ValidationReport validationReport = null;

        int retries = this.retryCount;
        while (true) {
            try {
                /*
                 * Set the authentication information and call the web service
                 */
                this.SetAuthHeader();
                uq.ilabs.labequipment.Validation proxyValidation = this.labEquipmentProxy.validate(xmlSpecification);
                validationReport = this.ConvertType(proxyValidation);
                break;

            } catch (SOAPFaultException ex) {
                Logfile.Write(ex.getMessage());
                throw new ProtocolException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new ProtocolException(STRERR_LabEquipmentUnaccessible);
                }
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Validation_arg3, validationReport.isAccepted(), validationReport.getEstRuntime(), validationReport.getErrorMessage()));

        return validationReport;
    }

    /**
     *
     * @param xmlSpecification
     * @return ExecutionStatus
     * @throws Exception
     */
    public ExecutionStatus StartLabExecution(String xmlSpecification) throws Exception {
        final String methodName = "StartLabExecution";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ExecutionStatus executionStatus = null;

        int retries = this.retryCount;
        while (true) {
            try {
                /*
                 * Set the authentication information and call the web service
                 */
                this.SetAuthHeader();
                uq.ilabs.labequipment.ExecutionStatus proxyExecutionStatus = this.labEquipmentProxy.startLabExecution(xmlSpecification);
                executionStatus = this.ConvertType(proxyExecutionStatus);
                break;

            } catch (SOAPFaultException ex) {
                Logfile.Write(ex.getMessage());
                throw new ProtocolException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new ProtocolException(STRERR_LabEquipmentUnaccessible);
                }
            }
        }

        return executionStatus;
    }

    /**
     *
     * @return @throws Exception
     */
    public ExecutionStatus GetLabExecutionStatus(int executionId) throws Exception {
        final String methodName = "GetLabExecutionStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ExecutionStatus executionStatus = null;

        int retries = this.retryCount;
        while (true) {
            try {
                /*
                 * Set the authentication information and call the web service
                 */
                this.SetAuthHeader();
                uq.ilabs.labequipment.ExecutionStatus proxyExecutionStatus = this.labEquipmentProxy.getLabExecutionStatus(executionId);
                executionStatus = this.ConvertType(proxyExecutionStatus);
                break;

            } catch (SOAPFaultException ex) {
                Logfile.Write(ex.getMessage());
                throw new ProtocolException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new ProtocolException(STRERR_LabEquipmentUnaccessible);
                }
            }
        }

        return executionStatus;
    }

    /**
     *
     * @param executionId
     * @return String
     */
    public String GetLabExecutionResults(int executionId) throws Exception {
        final String methodName = "GetLabExecutionResults";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String labExecutionResults = null;

        int retries = this.retryCount;
        while (true) {
            try {
                /*
                 * Set the authentication information and call the web service
                 */
                this.SetAuthHeader();
                labExecutionResults = this.labEquipmentProxy.getLabExecutionResults(executionId);
                break;

            } catch (SOAPFaultException ex) {
                Logfile.Write(ex.getMessage());
                throw new ProtocolException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new ProtocolException(STRERR_LabEquipmentUnaccessible);
                }
            }
        }

        return labExecutionResults;
    }

    /**
     *
     * @param executionId
     * @return boolean
     */
    public boolean CancelLabExecution(int executionId) throws Exception {
        final String methodName = "CancelLabExecution";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        int retries = this.retryCount;
        while (true) {
            try {
                /*
                 * Set the authentication information and call the web service
                 */
                this.SetAuthHeader();
                success = this.labEquipmentProxy.cancelLabExecution(executionId);
                break;

            } catch (SOAPFaultException ex) {
                Logfile.Write(ex.getMessage());
                throw new ProtocolException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new ProtocolException(STRERR_LabEquipmentUnaccessible);
                }
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    //================================================================================================================//
    /**
     *
     */
    private void SetAuthHeader() {
        /*
         * Create authentication header
         */
        AuthHeader authHeader = new AuthHeader();
        authHeader.setIdentifier(this.identifier);
        authHeader.setPassKey(this.passkey);

        /*
         * Pass the authentication header to the message handler through the message context
         */
        ((BindingProvider) this.labEquipmentProxy).getRequestContext().put(this.qnameAuthHeader.getLocalPart(), authHeader);
    }

    //<editor-fold defaultstate="collapsed" desc="ConvertType">
    /**
     *
     * @param proxyExecutionStatus
     * @return ExecutionStatus
     */
    private ExecutionStatus ConvertType(uq.ilabs.labequipment.ExecutionStatus proxyExecutionStatus) {
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
    private LabEquipmentStatus ConvertType(uq.ilabs.labequipment.LabEquipmentStatus proxyLabEquipmentStatus) {
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
    private ValidationReport ConvertType(uq.ilabs.labequipment.Validation proxyValidation) {
        ValidationReport validationReport = null;

        if (proxyValidation != null) {
            validationReport = new ValidationReport();
            validationReport.setAccepted(proxyValidation.isAccepted());
            validationReport.setErrorMessage(proxyValidation.getErrorMessage());
            validationReport.setEstRuntime(proxyValidation.getExecutionTime());
        }

        return validationReport;
    }
    //</editor-fold>
}
