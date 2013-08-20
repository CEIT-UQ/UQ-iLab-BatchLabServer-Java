/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.text.MessageFormat;
import java.util.logging.Level;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.WebApplicationException;
import uq.ilabs.library.lab.types.AuthHeader;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.SubmissionReport;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class LabServerRestAPI {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabServerRestAPI.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants
     */
    private static final String STRURI_Cancel_arg = "Cancel/{0}";
    private static final String STRURI_EffectiveQueueLength_arg2 = "EffectiveQueueLength/{0}/{1}";
    private static final String STRURI_ExperimentStatus_arg = "ExperimentStatus/{0}";
    private static final String STRURI_LabConfiguration_arg = "LabConfiguration/{0}";
    private static final String STRURI_LabInfo = "LabInfo";
    private static final String STRURI_LabStatus = "LabStatus";
    private static final String STRURI_RetrieveResult_arg = "RetrieveResult/{0}";
    private static final String STRURI_Submit_arg3 = "Submit/{0}/{1}/{2}";
    private static final String STRURI_Validate_arg = "Validate/{0}";
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_ServiceUrl_arg = "ServiceUrl: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ServiceUrl = "serviceUrl";
    private static final String STRERR_LabServerUnaccessible = "LabServer is unaccessible!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private WebTarget webTarget;
    private Client client;
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
     * @param serviceUrl
     * @throws Exception
     */
    public LabServerRestAPI(String serviceUrl) throws Exception {
        final String methodName = "LabServerRestAPI";
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
            this.client = ClientBuilder.newClient();
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
            builder = this.SetAuthHeader(builder);
            String txtResponse = builder.put(Entity.entity("", MediaType.TEXT_PLAIN), String.class);

            /*
             * Parse the response string
             */
            cancelled = Boolean.parseBoolean(txtResponse);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebApplicationException(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebApplicationException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return cancelled;
    }

    /**
     *
     * @param priorityHint
     * @return
     */
    public WaitEstimate GetEffectiveQueueLength(String userGroup, int priorityHint) {
        final String methodName = "GetEffectiveQueueLength";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        WaitEstimate waitEstimate = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            WebTarget resource = this.webTarget.path(java.text.MessageFormat.format(STRURI_EffectiveQueueLength_arg2, new Object[]{userGroup, priorityHint}));
            Builder builder = resource.request(MediaType.APPLICATION_XML);
            builder = this.SetAuthHeader(builder);
            String xmlResponse = builder.get(String.class);

            /*
             * Parse the XML response string
             */
            waitEstimate = WaitEstimate.XmlParse(xmlResponse);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebApplicationException(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebApplicationException(STRERR_LabServerUnaccessible);
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
            builder = this.SetAuthHeader(builder);
            String xmlResponse = builder.get(String.class);

            /*
             * Parse the XML response string
             */
            labExperimentStatus = LabExperimentStatus.XmlParse(xmlResponse);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebApplicationException(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebApplicationException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labExperimentStatus;
    }

    /**
     *
     * @return String
     */
    public String GetLabConfiguration(String userGroup) {
        final String methodName = "GetLabConfiguration";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String labConfiguration = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            WebTarget resource = this.webTarget.path(java.text.MessageFormat.format(STRURI_LabConfiguration_arg, new Object[]{userGroup}));
            Builder builder = resource.request(MediaType.APPLICATION_XML);
            builder = this.SetAuthHeader(builder);
            labConfiguration = builder.get(String.class);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebApplicationException(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebApplicationException(STRERR_LabServerUnaccessible);
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
            WebTarget resource = this.webTarget.path(STRURI_LabInfo);
            Builder builder = resource.request(MediaType.TEXT_PLAIN);
            builder = this.SetAuthHeader(builder);
            labInfo = builder.get(String.class);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebApplicationException(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebApplicationException(STRERR_LabServerUnaccessible);
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
            WebTarget resource = this.webTarget.path(STRURI_LabStatus);
            Builder builder = resource.request(MediaType.APPLICATION_XML);
            builder = this.SetAuthHeader(builder);
            String xmlResponse = builder.get(String.class);

            /*
             * Parse the XML response string
             */
            labStatus = LabStatus.XmlParse(xmlResponse);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebApplicationException(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebApplicationException(STRERR_LabServerUnaccessible);
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
            builder = this.SetAuthHeader(builder);
            String xmlResponse = builder.get(String.class);

            /*
             * Parse the XML response string
             */
            resultReport = ResultReport.XmlParse(xmlResponse);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebApplicationException(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebApplicationException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return resultReport;
    }

    /**
     *
     * @param experimentSpecification
     * @param priorityHint
     * @param emailNotification
     * @return SubmissionReport
     */
    public SubmissionReport Submit(int experimentId, String experimentSpecification, String userGroup, int priorityHint) {
        final String methodName = "Submit";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        SubmissionReport submissionReport = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            WebTarget resource = this.webTarget.path(java.text.MessageFormat.format(STRURI_Submit_arg3, new Object[]{experimentId, userGroup, priorityHint}));
            Builder builder = resource.request(MediaType.APPLICATION_XML);
            builder = this.SetAuthHeader(builder);
            String xmlResponse = builder.post(Entity.entity(experimentSpecification, MediaType.APPLICATION_XML), String.class);

            /*
             * Parse the XML response string
             */
            submissionReport = SubmissionReport.XmlParse(xmlResponse);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebApplicationException(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebApplicationException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return submissionReport;
    }

    /**
     *
     * @param experimentSpecification
     * @return ValidationReport
     */
    public ValidationReport Validate(String experimentSpecification, String userGroup) {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ValidationReport validationReport = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            WebTarget resource = this.webTarget.path(MessageFormat.format(STRURI_Validate_arg, new Object[]{userGroup}));
            Builder builder = resource.request(MediaType.APPLICATION_XML);
            builder = this.SetAuthHeader(builder);
            String xmlResponse = builder.post(Entity.entity(experimentSpecification, MediaType.APPLICATION_XML), String.class);

            /*
             * Parse the XML response string
             */
            validationReport = ValidationReport.XmlParse(xmlResponse);

        } catch (ClientErrorException ex) {
            Logfile.Write(ex.getMessage());
            throw new WebApplicationException(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new WebApplicationException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return validationReport;
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
    private Builder SetAuthHeader(Builder builder) {
        builder = builder.header(AuthHeader.STR_Identifier, this.identifier);
        builder = builder.header(AuthHeader.STR_Passkey, this.passkey);
        return builder;
    }
}
