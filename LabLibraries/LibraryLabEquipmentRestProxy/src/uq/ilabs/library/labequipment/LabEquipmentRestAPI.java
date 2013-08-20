/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment;

import java.text.MessageFormat;
import java.util.logging.Level;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.WebApplicationException;
import uq.ilabs.library.lab.types.AuthHeader;
import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.LabEquipmentStatus;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class LabEquipmentRestAPI {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentRestAPI.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants
     */
    private static final String STRURI_CancelLabExecution_arg = "CancelLabExecution/{0}";
    private static final String STRURI_LabEquipmentStatus = "LabStatus";
    private static final String STRURI_LabExecutionStatus_arg = "LabExecutionStatus/{0}";
    private static final String STRURI_LabExecutionResults_arg = "LabExecutionResults/{0}";
    private static final String STRURI_StartLabExecution = "StartLabExecution";
    private static final String STRURI_TimeUntilReady = "TimeUntilReady";
    private static final String STRURI_Validate = "Validate";
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
    private WebTarget webTarget;
    private Client client;
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
    public LabEquipmentRestAPI(String serviceUrl) throws Exception {
        final String methodName = "LabEquipmentRestAPI";
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
     * @return LabEquipmentStatus
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
                WebTarget resource = this.webTarget.path(STRURI_LabEquipmentStatus);
                Builder builder = resource.request(MediaType.APPLICATION_XML);
                builder = this.SetAuthHeader(builder);
                String xmlResponse = builder.get(String.class);

                /*
                 * Parse the XML response string
                 */
                labEquipmentStatus = LabEquipmentStatus.XmlParse(xmlResponse);
                break;

            } catch (ClientErrorException ex) {
                Logfile.Write(ex.getMessage());
                throw new WebApplicationException(ex.getMessage());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebApplicationException(STRERR_LabEquipmentUnaccessible);
                }
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labEquipmentStatus;
    }

    /**
     *
     * @return String
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
                WebTarget resource = this.webTarget.path(STRURI_TimeUntilReady);
                Builder builder = resource.request(MediaType.TEXT_PLAIN);
                builder = this.SetAuthHeader(builder);
                String txtResponse = builder.put(Entity.entity("", MediaType.TEXT_PLAIN), String.class);

                /*
                 * Parse the response string
                 */
                timeUntilReady = Integer.parseInt(txtResponse);
                break;

            } catch (ClientErrorException ex) {
                Logfile.Write(ex.getMessage());
                throw new WebApplicationException(ex.getMessage());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebApplicationException(STRERR_LabEquipmentUnaccessible);
                }
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
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
                WebTarget resource = this.webTarget.path(STRURI_Validate);
                Builder builder = resource.request(MediaType.APPLICATION_XML);
                builder = this.SetAuthHeader(builder);
                String xmlResponse = builder.post(Entity.entity(xmlSpecification, MediaType.APPLICATION_XML), String.class);

                /*
                 * Parse the XML response string
                 */
                validationReport = ValidationReport.XmlParse(xmlResponse);
                break;

            } catch (ClientErrorException ex) {
                Logfile.Write(ex.getMessage());
                throw new WebApplicationException(ex.getMessage());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebApplicationException(STRERR_LabEquipmentUnaccessible);
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
                WebTarget resource = this.webTarget.path(STRURI_StartLabExecution);
                Builder builder = resource.request(MediaType.APPLICATION_XML);
                builder = this.SetAuthHeader(builder);
                String xmlResponse = builder.post(Entity.entity(xmlSpecification, MediaType.APPLICATION_XML), String.class);

                /*
                 * Parse the XML response string
                 */
                executionStatus = ExecutionStatus.XmlParse(xmlResponse);
                break;

            } catch (ClientErrorException ex) {
                Logfile.Write(ex.getMessage());
                throw new WebApplicationException(ex.getMessage());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebApplicationException(STRERR_LabEquipmentUnaccessible);
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
                WebTarget resource = this.webTarget.path(MessageFormat.format(STRURI_LabExecutionStatus_arg, new Object[]{executionId}));
                Builder builder = resource.request(MediaType.APPLICATION_XML);
                builder = this.SetAuthHeader(builder);
                String xmlResponse = builder.get(String.class);

                /*
                 * Parse the XML response string
                 */
                executionStatus = ExecutionStatus.XmlParse(xmlResponse);
                break;

            } catch (ClientErrorException ex) {
                Logfile.Write(ex.getMessage());
                throw new WebApplicationException(ex.getMessage());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebApplicationException(STRERR_LabEquipmentUnaccessible);
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
                WebTarget resource = this.webTarget.path(java.text.MessageFormat.format(STRURI_LabExecutionResults_arg, new Object[]{executionId}));
                Builder builder = resource.request(MediaType.APPLICATION_XML);
                builder = this.SetAuthHeader(builder);
                labExecutionResults = builder.get(String.class);
                break;

            } catch (ClientErrorException ex) {
                Logfile.Write(ex.getMessage());
                throw new WebApplicationException(ex.getMessage());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebApplicationException(STRERR_LabEquipmentUnaccessible);
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
                WebTarget resource = this.webTarget.path(MessageFormat.format(STRURI_CancelLabExecution_arg, new Object[]{executionId}));
                Builder builder = resource.request(MediaType.TEXT_PLAIN);
                builder = this.SetAuthHeader(builder);
                String txtResponse = builder.put(Entity.entity("", MediaType.TEXT_PLAIN), String.class);

                /*
                 * Parse the response string
                 */
                success = Boolean.parseBoolean(txtResponse);
                break;

            } catch (ClientErrorException ex) {
                Logfile.Write(ex.getMessage());
                throw new WebApplicationException(ex.getMessage());
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                if (--retries == 0) {
                    throw new WebApplicationException(STRERR_LabEquipmentUnaccessible);
                }
            }
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
    private Builder SetAuthHeader(Builder builder) {
        builder = builder.header(AuthHeader.STR_Identifier, this.identifier);
        builder = builder.header(AuthHeader.STR_Passkey, this.passkey);
        return builder;
    }
}
