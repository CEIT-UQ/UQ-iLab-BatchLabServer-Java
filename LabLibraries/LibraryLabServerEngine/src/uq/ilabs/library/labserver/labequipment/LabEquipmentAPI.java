/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.labequipment;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
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
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabEquipmentServiceSoap labEquipmentProxy;
    private QName qnameAuthHeader;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String identifier;
    private String passkey;

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
    }
    //</editor-fold>

    /**
     *
     * @param unitId
     */
    public LabEquipmentAPI(String serviceUrl) throws Exception {
        final String methodName = "LabEquipmentAPI";
        Logfile.WriteCalled(STR_ClassName, methodName,
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
            if (labEquipmentService == null) {
                throw new NullPointerException(LabEquipmentService.class.getSimpleName());
            }
            this.labEquipmentProxy = labEquipmentService.getLabEquipmentServiceSoap();
            ((BindingProvider) this.labEquipmentProxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUrl);

            /*
             * Get authentication header QName
             */
            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<AuthHeader> jaxbElementAuthHeader = objectFactory.createAuthHeader(new AuthHeader());
            this.qnameAuthHeader = jaxbElementAuthHeader.getName();

        } catch (NullPointerException | IllegalArgumentException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }

    /**
     *
     * @return @throws Exception
     */
    public LabEquipmentStatus GetLabEquipmentStatus() throws Exception {
        final String methodName = "GetLabEquipmentStatus";
        Logfile.WriteCalled(STR_ClassName, methodName);

        LabEquipmentStatus labEquipmentStatus = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labequipment.LabEquipmentStatus proxyLabEquipmentStatus = this.labEquipmentProxy.getLabEquipmentStatus();
            labEquipmentStatus = this.ConvertType(proxyLabEquipmentStatus);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            labEquipmentStatus = new LabEquipmentStatus();
            labEquipmentStatus.setOnline(false);
            labEquipmentStatus.setStatusMessage(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        return labEquipmentStatus;
    }

    /**
     *
     * @return @throws Exception
     */
    public int GetTimeUntilReady() throws Exception {
        final String methodName = "GetTimeUntilReady";
        Logfile.WriteCalled(STR_ClassName, methodName);

        int timeUntilReady = -1;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            timeUntilReady = this.labEquipmentProxy.getTimeUntilReady();

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
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
        Logfile.WriteCalled(STR_ClassName, methodName);

        ValidationReport validationReport = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labequipment.Validation proxyValidation = this.labEquipmentProxy.validate(xmlSpecification);
            validationReport = this.ConvertType(proxyValidation);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            validationReport = new ValidationReport();
            validationReport.setErrorMessage(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName,
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
        Logfile.WriteCalled(STR_ClassName, methodName);

        ExecutionStatus executionStatus = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labequipment.ExecutionStatus proxyExecutionStatus = this.labEquipmentProxy.startLabExecution(xmlSpecification);
            executionStatus = this.ConvertType(proxyExecutionStatus);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            executionStatus = new ExecutionStatus();
            executionStatus.setExecuteStatus(ExecutionStatus.Status.Failed);
            executionStatus.setErrorMessage(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        return executionStatus;
    }

    /**
     *
     * @return @throws Exception
     */
    public ExecutionStatus GetLabExecutionStatus(int executionId) throws Exception {
        final String methodName = "GetLabExecutionStatus";
        Logfile.WriteCalled(STR_ClassName, methodName);

        ExecutionStatus executionStatus = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labequipment.ExecutionStatus proxyExecutionStatus = this.labEquipmentProxy.getLabExecutionStatus(executionId);
            executionStatus = this.ConvertType(proxyExecutionStatus);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            executionStatus = new ExecutionStatus();
            executionStatus.setExecuteStatus(ExecutionStatus.Status.Failed);
            executionStatus.setErrorMessage(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
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
        Logfile.WriteCalled(STR_ClassName, methodName);

        String labExecutionResults = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            labExecutionResults = this.labEquipmentProxy.getLabExecutionResults(executionId);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
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
        Logfile.WriteCalled(STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            success = this.labEquipmentProxy.cancelLabExecution(executionId);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName,
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
