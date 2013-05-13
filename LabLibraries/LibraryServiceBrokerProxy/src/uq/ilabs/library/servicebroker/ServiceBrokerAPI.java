/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.servicebroker;

import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.soap.SOAPFaultException;
import uq.ilabs.library.lab.types.ClientSubmissionReport;
import uq.ilabs.library.lab.types.ExperimentStatus;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.servicebroker.ObjectFactory;
import uq.ilabs.servicebroker.SbAuthHeader;
import uq.ilabs.servicebroker.ServiceBrokerService;
import uq.ilabs.servicebroker.ServiceBrokerServiceSoap;

/**
 *
 * @author uqlpayne
 */
public class ServiceBrokerAPI {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokerAPI.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_ServiceUrl_arg = "ServiceUrl: '%s'";
    private static final String STRLOG_ExperimentId_arg = "ExperimentId: %d";
    private static final String STRLOG_Success_arg = "Success: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ServiceUrl = "serviceUrl";
    private static final String STRERR_ServiceBrokerUnaccessible = "ServiceBroker is unaccessible!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private ServiceBrokerServiceSoap serviceBrokerProxy;
    private QName qnameSbAuthHeader;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private long couponId;
    private String couponPasskey;
    private String labServerId;

    public long getCouponId() {
        return couponId;
    }

    public void setCouponId(long couponId) {
        this.couponId = couponId;
    }

    public String getCouponPasskey() {
        return couponPasskey;
    }

    public void setCouponPasskey(String couponPasskey) {
        this.couponPasskey = couponPasskey;
    }

    public String getLabServerId() {
        return labServerId;
    }

    public void setLabServerId(String labServerId) {
        this.labServerId = labServerId;
    }
    //</editor-fold>

    /**
     *
     * @param serviceUrl
     * @throws Exception
     */
    public ServiceBrokerAPI(String serviceUrl) throws Exception {
        final String methodName = "ServiceBrokerAPI";
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
             * Create a proxy for the web service and set the web service URL
             */
            ServiceBrokerService serviceBrokerService = new ServiceBrokerService();
            this.serviceBrokerProxy = serviceBrokerService.getServiceBrokerServiceSoap();
            ((BindingProvider) this.serviceBrokerProxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUrl);

            /*
             * Get authentication header QName
             */
            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<SbAuthHeader> jaxbElementSbAuthHeader = objectFactory.createSbAuthHeader(new SbAuthHeader());
            this.qnameSbAuthHeader = jaxbElementSbAuthHeader.getName();

        } catch (NullPointerException | IllegalArgumentException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param experimentId
     * @return boolean
     */
    public boolean Cancel(int experimentId) {
        final String methodName = "Cancel";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean cancelled = false;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetSbAuthHeader();
            cancelled = serviceBrokerProxy.cancel(experimentId);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_ServiceBrokerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return cancelled;
    }

    /**
     *
     * @return WaitEstimate
     */
    public WaitEstimate GetEffectiveQueueLength() {
        return this.GetEffectiveQueueLength(0);
    }

    /**
     *
     * @param priorityHint
     * @return
     */
    public WaitEstimate GetEffectiveQueueLength(int priorityHint) {
        final String methodName = "GetEffectiveQueueLength";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        WaitEstimate waitEstimate = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetSbAuthHeader();
            uq.ilabs.servicebroker.WaitEstimate proxyWaitEstimate = this.serviceBrokerProxy.getEffectiveQueueLength(this.labServerId, priorityHint);
            waitEstimate = this.ConvertType(proxyWaitEstimate);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_ServiceBrokerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return waitEstimate;
    }

    /**
     *
     * @param experimentId
     * @return LabExperimentStatus
     */
    public LabExperimentStatus GetExperimentStatus(int experimentId) {
        final String methodName = "GetExperimentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        LabExperimentStatus labExperimentStatus = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetSbAuthHeader();
            uq.ilabs.servicebroker.LabExperimentStatus proxyLabExperimentStatus = this.serviceBrokerProxy.getExperimentStatus(experimentId);
            labExperimentStatus = this.ConvertType(proxyLabExperimentStatus);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_ServiceBrokerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labExperimentStatus;
    }

    /**
     *
     * @return String
     */
    public String GetLabConfiguration() {
        final String methodName = "GetLabConfiguration";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String labConfiguration = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetSbAuthHeader();
            labConfiguration = serviceBrokerProxy.getLabConfiguration(this.labServerId);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_ServiceBrokerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labConfiguration;
    }

    /**
     *
     * @return String
     */
    public String GetLabInfo() {
        final String methodName = "GetLabInfo";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String labInfo = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetSbAuthHeader();
            labInfo = serviceBrokerProxy.getLabInfo(this.labServerId);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_ServiceBrokerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labInfo;
    }

    /**
     *
     * @return LabStatus
     */
    public LabStatus GetLabStatus() {
        final String methodName = "GetLabStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        LabStatus labStatus = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetSbAuthHeader();
            uq.ilabs.servicebroker.LabStatus proxyLabStatus = this.serviceBrokerProxy.getLabStatus(this.labServerId);
            labStatus = this.ConvertType(proxyLabStatus);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_ServiceBrokerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labStatus;
    }

    /**
     *
     * @param experimentId
     * @return ResultReport
     */
    public ResultReport RetrieveResult(int experimentId) {
        final String methodName = "RetrieveResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ResultReport resultReport = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetSbAuthHeader();
            uq.ilabs.servicebroker.ResultReport proxyResultReport = this.serviceBrokerProxy.retrieveResult(experimentId);
            resultReport = this.ConvertType(proxyResultReport);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_ServiceBrokerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return resultReport;
    }

    /**
     *
     * @param experimentSpecification
     * @return ClientSubmissionReport
     */
    public ClientSubmissionReport Submit(String experimentSpecification) {
        return this.Submit(experimentSpecification, 0, false);
    }

    /**
     *
     * @param experimentSpecification
     * @param priorityHint
     * @param emailNotification
     * @return ClientSubmissionReport
     */
    public ClientSubmissionReport Submit(String experimentSpecification, int priorityHint, boolean emailNotification) {
        final String methodName = "Submit";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ClientSubmissionReport clientSubmissionReport = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetSbAuthHeader();
            uq.ilabs.servicebroker.ClientSubmissionReport proxyClientSubmissionReport = this.serviceBrokerProxy.submit(this.labServerId, experimentSpecification, priorityHint, emailNotification);
            clientSubmissionReport = this.ConvertType(proxyClientSubmissionReport);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_ServiceBrokerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return clientSubmissionReport;
    }

    /**
     *
     * @param experimentSpecification
     * @return ValidationReport
     */
    public ValidationReport Validate(String experimentSpecification) {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ValidationReport validationReport = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetSbAuthHeader();
            uq.ilabs.servicebroker.ValidationReport proxyValidationReport = this.serviceBrokerProxy.validate(this.labServerId, experimentSpecification);
            validationReport = this.ConvertType(proxyValidationReport);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_ServiceBrokerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return validationReport;
    }

    /**
     *
     * @param experimentID
     */
    public boolean Notify(int experimentId) {
        final String methodName = "Notify";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        boolean success = false;

        try {
            this.serviceBrokerProxy.notify(experimentId);
            success = true;

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_ServiceBrokerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    //================================================================================================================//
    /**
     *
     */
    private void SetSbAuthHeader() {
        /*
         * Create authentication header
         */
        SbAuthHeader sbAuthHeader = new SbAuthHeader();
        sbAuthHeader.setCouponID(this.couponId);
        sbAuthHeader.setCouponPassKey(this.couponPasskey);

        /*
         * Pass the authentication header to the message handler through the message context
         */
        ((BindingProvider) this.serviceBrokerProxy).getRequestContext().put(this.qnameSbAuthHeader.getLocalPart(), sbAuthHeader);
    }

    //<editor-fold defaultstate="collapsed" desc="ConvertType">
    /**
     *
     * @param arrayOfString
     * @return String[]
     */
    private String[] ConvertType(uq.ilabs.servicebroker.ArrayOfString arrayOfString) {
        String[] strings = null;

        if (arrayOfString != null) {
            strings = arrayOfString.getString().toArray(new String[0]);
        }

        return strings;
    }

    /**
     *
     * @param proxyClientSubmissionReport
     * @return ClientSubmissionReport
     */
    private ClientSubmissionReport ConvertType(uq.ilabs.servicebroker.ClientSubmissionReport proxyClientSubmissionReport) {
        ClientSubmissionReport clientSubmissionReport = null;

        if (proxyClientSubmissionReport != null) {
            clientSubmissionReport = new ClientSubmissionReport();
            clientSubmissionReport.setExperimentId(proxyClientSubmissionReport.getExperimentID());
            clientSubmissionReport.setMinTimeToLive(proxyClientSubmissionReport.getMinTimeToLive());
            clientSubmissionReport.setValidationReport(this.ConvertType(proxyClientSubmissionReport.getVReport()));
            clientSubmissionReport.setWaitEstimate(this.ConvertType(proxyClientSubmissionReport.getWait()));
        }

        return clientSubmissionReport;
    }

    /**
     *
     * @param proxyExperimentStatus
     * @return ExperimentStatus
     */
    private ExperimentStatus ConvertType(uq.ilabs.servicebroker.ExperimentStatus proxyExperimentStatus) {
        ExperimentStatus experimentStatus = null;

        if (proxyExperimentStatus != null) {
            experimentStatus = new ExperimentStatus();
            experimentStatus.setEstRemainingRuntime(proxyExperimentStatus.getEstRemainingRuntime());
            experimentStatus.setEstRuntime(proxyExperimentStatus.getEstRuntime());
            experimentStatus.setStatusCode(StatusCodes.ToStatusCode(proxyExperimentStatus.getStatusCode()));
            experimentStatus.setWaitEstimate(this.ConvertType(proxyExperimentStatus.getWait()));
        }

        return experimentStatus;
    }

    /**
     *
     * @param proxyLabExperimentStatus
     * @return LabExperimentStatus
     */
    private LabExperimentStatus ConvertType(uq.ilabs.servicebroker.LabExperimentStatus proxyLabExperimentStatus) {
        LabExperimentStatus labExperimentStatus = null;

        if (proxyLabExperimentStatus != null) {
            labExperimentStatus = new LabExperimentStatus();
            labExperimentStatus.setMinTimetoLive(proxyLabExperimentStatus.getMinTimetoLive());
            labExperimentStatus.setExperimentStatus(this.ConvertType(proxyLabExperimentStatus.getStatusReport()));
        }

        return labExperimentStatus;
    }

    /**
     *
     * @param proxyLabStatus
     * @return LabStatus
     */
    private LabStatus ConvertType(uq.ilabs.servicebroker.LabStatus proxyLabStatus) {
        LabStatus labStatus = null;

        if (proxyLabStatus != null) {
            labStatus = new LabStatus();
            labStatus.setOnline(proxyLabStatus.isOnline());
            labStatus.setLabStatusMessage(proxyLabStatus.getLabStatusMessage());
        }

        return labStatus;
    }

    /**
     *
     * @param proxyResultReport
     * @return ResultReport
     */
    private ResultReport ConvertType(uq.ilabs.servicebroker.ResultReport proxyResultReport) {
        ResultReport resultReport = null;

        if (proxyResultReport != null) {
            resultReport = new ResultReport();
            resultReport.setErrorMessage(proxyResultReport.getErrorMessage());
            resultReport.setXmlExperimentResults(proxyResultReport.getExperimentResults());
            resultReport.setStatusCode(StatusCodes.ToStatusCode(proxyResultReport.getStatusCode()));
            resultReport.setXmlBlobExtension(proxyResultReport.getXmlBlobExtension());
            resultReport.setXmlResultExtension(proxyResultReport.getXmlResultExtension());
            resultReport.setWarningMessages(this.ConvertType(proxyResultReport.getWarningMessages()));
        }

        return resultReport;
    }

    /**
     *
     * @param proxyValidationReport
     * @return ValidationReport
     */
    private ValidationReport ConvertType(uq.ilabs.servicebroker.ValidationReport proxyValidationReport) {
        ValidationReport validationReport = null;

        if (proxyValidationReport != null) {
            validationReport = new ValidationReport();
            validationReport.setAccepted(proxyValidationReport.isAccepted());
            validationReport.setErrorMessage(proxyValidationReport.getErrorMessage());
            validationReport.setEstRuntime(proxyValidationReport.getEstRuntime());
            validationReport.setWarningMessages(ConvertType(proxyValidationReport.getWarningMessages()));
        }

        return validationReport;
    }

    /**
     *
     * @param proxyWaitEstimate
     * @return WaitEstimate
     */
    private WaitEstimate ConvertType(uq.ilabs.servicebroker.WaitEstimate proxyWaitEstimate) {
        WaitEstimate waitEstimate = null;

        if (proxyWaitEstimate != null) {
            waitEstimate = new WaitEstimate();
            waitEstimate.setEffectiveQueueLength(proxyWaitEstimate.getEffectiveQueueLength());
            waitEstimate.setEstWait(proxyWaitEstimate.getEstWait());
        }

        return waitEstimate;
    }
    //</editor-fold>
}
