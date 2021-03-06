/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.servicebroker;

import java.util.Map;
import java.util.logging.Level;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import uq.ilabs.library.lab.types.ClientSubmissionReport;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.SbAuthHeader;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;
import edu.mit.ilab.servicebroker.proxy.ServiceBrokerService;
import edu.mit.ilab.servicebroker.proxy.ServiceBrokerServiceSoap;

/**
 *
 * @author uqlpayne
 */
public class ServiceBrokerSoapAPI {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokerSoapAPI.class.getName();
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
    public ServiceBrokerSoapAPI(String serviceUrl) throws Exception {
        final String methodName = "ServiceBrokerSoapAPI";
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
            throw new WebServiceException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebServiceException(STRERR_ServiceBrokerUnaccessible);
        } finally {
            this.UnsetSbAuthHeader();
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return cancelled;
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
            edu.mit.ilab.servicebroker.proxy.WaitEstimate proxyWaitEstimate = this.serviceBrokerProxy.getEffectiveQueueLength(this.labServerId, priorityHint);
            waitEstimate = ConvertTypes.Convert(proxyWaitEstimate);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebServiceException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebServiceException(STRERR_ServiceBrokerUnaccessible);
        } finally {
            this.UnsetSbAuthHeader();
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
            edu.mit.ilab.servicebroker.proxy.LabExperimentStatus proxyLabExperimentStatus = this.serviceBrokerProxy.getExperimentStatus(experimentId);
            labExperimentStatus = ConvertTypes.Convert(proxyLabExperimentStatus);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebServiceException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebServiceException(STRERR_ServiceBrokerUnaccessible);
        } finally {
            this.UnsetSbAuthHeader();
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
            throw new WebServiceException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebServiceException(STRERR_ServiceBrokerUnaccessible);
        } finally {
            this.UnsetSbAuthHeader();
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
            throw new WebServiceException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebServiceException(STRERR_ServiceBrokerUnaccessible);
        } finally {
            this.UnsetSbAuthHeader();
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
            edu.mit.ilab.servicebroker.proxy.LabStatus proxyLabStatus = this.serviceBrokerProxy.getLabStatus(this.labServerId);
            labStatus = ConvertTypes.Convert(proxyLabStatus);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebServiceException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebServiceException(STRERR_ServiceBrokerUnaccessible);
        } finally {
            this.UnsetSbAuthHeader();
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
            edu.mit.ilab.servicebroker.proxy.ResultReport proxyResultReport = this.serviceBrokerProxy.retrieveResult(experimentId);
            resultReport = ConvertTypes.Convert(proxyResultReport);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebServiceException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebServiceException(STRERR_ServiceBrokerUnaccessible);
        } finally {
            this.UnsetSbAuthHeader();
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return resultReport;
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
            edu.mit.ilab.servicebroker.proxy.ClientSubmissionReport proxyClientSubmissionReport = this.serviceBrokerProxy.submit(this.labServerId, experimentSpecification, priorityHint, emailNotification);
            clientSubmissionReport = ConvertTypes.Convert(proxyClientSubmissionReport);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebServiceException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebServiceException(STRERR_ServiceBrokerUnaccessible);
        } finally {
            this.UnsetSbAuthHeader();
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
            edu.mit.ilab.servicebroker.proxy.ValidationReport proxyValidationReport = this.serviceBrokerProxy.validate(this.labServerId, experimentSpecification);
            validationReport = ConvertTypes.Convert(proxyValidationReport);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebServiceException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebServiceException(STRERR_ServiceBrokerUnaccessible);
        } finally {
            this.UnsetSbAuthHeader();
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
            throw new WebServiceException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebServiceException(STRERR_ServiceBrokerUnaccessible);
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
        sbAuthHeader.setCouponId(this.couponId);
        sbAuthHeader.setCouponPasskey(this.couponPasskey);

        /*
         * Pass the authentication header to the message handler through the message context
         */
        Map<String, Object> requestContext = ((BindingProvider) this.serviceBrokerProxy).getRequestContext();
        requestContext.put(SbAuthHeader.class.getSimpleName(), sbAuthHeader);
    }

    /**
     *
     */
    private void UnsetSbAuthHeader() {
        Map<String, Object> requestContext = ((BindingProvider) this.serviceBrokerProxy).getRequestContext();
        requestContext.remove(SbAuthHeader.class.getSimpleName());
    }
}
