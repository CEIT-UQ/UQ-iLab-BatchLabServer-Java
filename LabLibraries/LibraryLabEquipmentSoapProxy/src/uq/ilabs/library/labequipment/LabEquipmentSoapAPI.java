/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment;

import java.util.logging.Level;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import uq.ilabs.labequipment.proxy.LabEquipmentService;
import uq.ilabs.labequipment.proxy.LabEquipmentServiceSoap;
import uq.ilabs.library.lab.types.AuthHeader;
import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.LabEquipmentStatus;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class LabEquipmentSoapAPI {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentSoapAPI.class.getName();
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
    public LabEquipmentSoapAPI(String serviceUrl) throws Exception {
        final String methodName = "LabEquipmentSoapAPI";
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
                uq.ilabs.labequipment.proxy.LabEquipmentStatus proxyLabEquipmentStatus = this.labEquipmentProxy.getLabEquipmentStatus();
                labEquipmentStatus = ConvertTypes.Convert(proxyLabEquipmentStatus);
                break;

            } catch (SOAPFaultException ex) {
                Logfile.Write(ex.getMessage());
                throw new WebServiceException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebServiceException(STRERR_LabEquipmentUnaccessible);
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
                throw new WebServiceException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebServiceException(STRERR_LabEquipmentUnaccessible);
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
                uq.ilabs.labequipment.proxy.Validation proxyValidation = this.labEquipmentProxy.validate(xmlSpecification);
                validationReport = ConvertTypes.Convert(proxyValidation);
                break;

            } catch (SOAPFaultException ex) {
                Logfile.Write(ex.getMessage());
                throw new WebServiceException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebServiceException(STRERR_LabEquipmentUnaccessible);
                }
            }
        }

        String message = (validationReport != null)
                ? String.format(STRLOG_Validation_arg3, validationReport.isAccepted(), validationReport.getEstRuntime(), validationReport.getErrorMessage())
                : null;
        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName, message);

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
                uq.ilabs.labequipment.proxy.ExecutionStatus proxyExecutionStatus = this.labEquipmentProxy.startLabExecution(xmlSpecification);
                executionStatus = ConvertTypes.Convert(proxyExecutionStatus);
                break;

            } catch (SOAPFaultException ex) {
                Logfile.Write(ex.getMessage());
                throw new WebServiceException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebServiceException(STRERR_LabEquipmentUnaccessible);
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
                uq.ilabs.labequipment.proxy.ExecutionStatus proxyExecutionStatus = this.labEquipmentProxy.getLabExecutionStatus(executionId);
                executionStatus = ConvertTypes.Convert(proxyExecutionStatus);
                break;

            } catch (SOAPFaultException ex) {
                Logfile.Write(ex.getMessage());
                throw new WebServiceException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebServiceException(STRERR_LabEquipmentUnaccessible);
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
                throw new WebServiceException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebServiceException(STRERR_LabEquipmentUnaccessible);
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
                throw new WebServiceException(ex.getFault().getFaultString());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebServiceException(STRERR_LabEquipmentUnaccessible);
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
        authHeader.setPasskey(this.passkey);

        /*
         * Pass the authentication header to the message handler through the message context
         */
        ((BindingProvider) this.labEquipmentProxy).getRequestContext().put(AuthHeader.class.getSimpleName(), authHeader);
    }
}
