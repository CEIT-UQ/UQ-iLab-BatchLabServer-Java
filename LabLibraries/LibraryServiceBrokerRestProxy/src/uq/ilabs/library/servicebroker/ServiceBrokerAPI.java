/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.servicebroker;

import java.text.MessageFormat;
import java.util.logging.Level;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.ProtocolException;
import uq.ilabs.library.lab.types.ClientSubmissionReport;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class ServiceBrokerAPI {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokerAPI.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants
     */
    private static final String STRURI_Cancel_arg = "Cancel/{0}";
    private static final String STRURI_EffectiveQueueLength_arg2 = "EffectiveQueueLength/{0}/{1}";
    private static final String STRURI_ExperimentStatus_arg = "ExperimentStatus/{0}";
    private static final String STRURI_LabConfiguration_arg = "LabConfiguration/{0}";
    private static final String STRURI_LabInfo_arg = "LabInfo/{0}";
    private static final String STRURI_LabStatus_arg = "LabStatus/{0}";
    private static final String STRURI_Notify_arg = "Notify/{0}";
    private static final String STRURI_RetrieveResult_arg = "RetrieveResult/{0}";
    private static final String STRURI_Submit_arg3 = "Submit/{0}/{1}/{2}";
    private static final String STRURI_Validate_arg = "Validate/{0}";
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
    private WebTarget webTarget;
    private Client client;
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
             * Create instance of ServiceBrokerRestClient
             */
            this.client = javax.ws.rs.client.ClientBuilder.newClient();
            this.webTarget = this.client.target(serviceUrl);

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
            WebTarget resource = this.webTarget.path(MessageFormat.format(STRURI_Cancel_arg, new Object[]{experimentId}));
            Builder builder = resource.request(MediaType.TEXT_PLAIN);
            builder = this.SetSbAuthHeader(builder);
            String txtResponse = builder.put(Entity.entity("", MediaType.TEXT_PLAIN), String.class);

            /*
             * Parse the response string and convert to return type
             */
            cancelled = Boolean.parseBoolean(txtResponse);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getMessage());
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
            WebTarget resource = this.webTarget.path(java.text.MessageFormat.format(STRURI_EffectiveQueueLength_arg2, new Object[]{labServerId, priorityHint}));
            Builder builder = resource.request(MediaType.APPLICATION_XML);
            builder = this.SetSbAuthHeader(builder);
            String xmlResponse = builder.get(String.class);

            /*
             * Parse the XML response string and convert to return type
             */
            edu.mit.ilab.rest.WaitEstimate proxyWaitEstimate = edu.mit.ilab.rest.WaitEstimate.XmlParse(xmlResponse);
            waitEstimate = ConvertTypes.Convert(proxyWaitEstimate);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getMessage());
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
            WebTarget resource = this.webTarget.path(java.text.MessageFormat.format(STRURI_ExperimentStatus_arg, new Object[]{experimentId}));
            Builder builder = resource.request(MediaType.APPLICATION_XML);
            builder = this.SetSbAuthHeader(builder);
            String xmlResponse = builder.get(String.class);

            /*
             * Parse the XML response string and convert to return type
             */
            edu.mit.ilab.rest.LabExperimentStatus proxyLabExperimentStatus = edu.mit.ilab.rest.LabExperimentStatus.XmlParse(xmlResponse);
            labExperimentStatus = ConvertTypes.Convert(proxyLabExperimentStatus);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getMessage());
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
            WebTarget resource = this.webTarget.path(java.text.MessageFormat.format(STRURI_LabConfiguration_arg, new Object[]{labServerId}));
            Builder builder = resource.request(MediaType.APPLICATION_XML);
            builder = this.SetSbAuthHeader(builder);
            labConfiguration = builder.get(String.class);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getMessage());
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
            WebTarget resource = this.webTarget.path(java.text.MessageFormat.format(STRURI_LabInfo_arg, new Object[]{labServerId}));
            Builder builder = resource.request(MediaType.TEXT_PLAIN);
            builder = this.SetSbAuthHeader(builder);
            labInfo = builder.get(String.class);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getMessage());
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
            WebTarget resource = this.webTarget.path(MessageFormat.format(STRURI_LabStatus_arg, new Object[]{labServerId}));
            Builder builder = resource.request(MediaType.APPLICATION_XML);
            builder = this.SetSbAuthHeader(builder);
            String xmlResponse = builder.get(String.class);

            /*
             * Parse the XML response string and convert to return type
             */
            edu.mit.ilab.rest.LabStatus proxyLabStatus = edu.mit.ilab.rest.LabStatus.XmlParse(xmlResponse);
            labStatus = ConvertTypes.Convert(proxyLabStatus);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getMessage());
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
            WebTarget resource = this.webTarget.path(java.text.MessageFormat.format(STRURI_RetrieveResult_arg, new Object[]{experimentId}));
            Builder builder = resource.request(MediaType.APPLICATION_XML);
            builder = this.SetSbAuthHeader(builder);
            String xmlResponse = builder.get(String.class);

            /*
             * Parse the XML response string and convert to return type
             */
            edu.mit.ilab.rest.ResultReport proxyResultReport = edu.mit.ilab.rest.ResultReport.XmlParse(xmlResponse);
            resultReport = ConvertTypes.Convert(proxyResultReport);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getMessage());
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
            WebTarget resource = this.webTarget.path(java.text.MessageFormat.format(STRURI_Submit_arg3, new Object[]{labServerId, priorityHint, emailNotification}));
            Builder builder = resource.request(MediaType.APPLICATION_XML);
            builder = this.SetSbAuthHeader(builder);
            String xmlResponse = builder.post(Entity.entity(experimentSpecification, MediaType.APPLICATION_XML), String.class);

            /*
             * Parse the XML response string and convert to return type
             */
            edu.mit.ilab.rest.ClientSubmissionReport proxyClientSubmissionReport = edu.mit.ilab.rest.ClientSubmissionReport.XmlParse(xmlResponse);
            clientSubmissionReport = ConvertTypes.Convert(proxyClientSubmissionReport);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getMessage());
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
            WebTarget resource = this.webTarget.path(java.text.MessageFormat.format(STRURI_Validate_arg, new Object[]{labServerId}));
            Builder builder = resource.request(MediaType.APPLICATION_XML);
            builder = this.SetSbAuthHeader(builder);
            String xmlResponse = builder.post(Entity.entity(experimentSpecification, MediaType.APPLICATION_XML), String.class);

            /*
             * Parse the XML response string and convert to return type
             */
            edu.mit.ilab.rest.ValidationReport proxyValidationReport = edu.mit.ilab.rest.ValidationReport.XmlParse(xmlResponse);
            validationReport = ConvertTypes.Convert(proxyValidationReport);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getMessage());
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
            WebTarget resource = this.webTarget.path(MessageFormat.format(STRURI_Notify_arg, new Object[]{experimentId}));
            Builder builder = resource.request();
            builder.put(Entity.entity("", MediaType.TEXT_PLAIN));

            success = true;

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_ServiceBrokerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     */
    public void Close() {
        this.client.close();
    }

    //================================================================================================================//
    /**
     *
     * @param builder
     * @return Builder
     */
    private Builder SetSbAuthHeader(Builder builder) {
        builder = builder.header(edu.mit.ilab.rest.SbAuthHeader.STR_CouponId, this.couponId);
        builder = builder.header(edu.mit.ilab.rest.SbAuthHeader.STR_CouponPasskey, this.couponPasskey);
        return builder;
    }
}
