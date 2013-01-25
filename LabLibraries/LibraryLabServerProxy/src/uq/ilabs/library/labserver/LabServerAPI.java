/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.soap.SOAPFaultException;
import uq.ilabs.labserver.AuthHeader;
import uq.ilabs.labserver.LabServerWebService;
import uq.ilabs.labserver.LabServerWebServiceSoap;
import uq.ilabs.labserver.ObjectFactory;
import uq.ilabs.library.lab.types.ExperimentStatus;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.lab.types.SubmissionReport;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class LabServerAPI {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabServerAPI.class.getName();
    private static final Level logLevel = Level.FINER;
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
    private LabServerWebServiceSoap labServerProxy;
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
     * @param serviceUrl
     * @throws Exception
     */
    public LabServerAPI(String serviceUrl) throws Exception {
        final String methodName = "LabServerAPI";
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
             * Create a proxy for the LabServer's web service and set the web service URL
             */
            LabServerWebService labServerWebService = new LabServerWebService();
            if (labServerWebService == null) {
                throw new NullPointerException(LabServerWebService.class.getSimpleName());
            }
            this.labServerProxy = labServerWebService.getLabServerWebServiceSoap();
            ((BindingProvider) this.labServerProxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUrl);

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

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param experimentId
     * @return
     */
    public boolean Cancel(int experimentId) {
        final String methodName = "Cancel";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean cancelled = false;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            cancelled = this.labServerProxy.cancel(experimentId);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return cancelled;
    }

    /**
     *
     * @param userGroup
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
            this.SetAuthHeader();
            uq.ilabs.labserver.WaitEstimate proxyWaitEstimate = this.labServerProxy.getEffectiveQueueLength(userGroup, priorityHint);
            waitEstimate = this.ConvertType(proxyWaitEstimate);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return waitEstimate;
    }

    /**
     *
     * @param experimentId
     * @return
     */
    public LabExperimentStatus GetExperimentStatus(int experimentId) {
        final String methodName = "GetExperimentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        LabExperimentStatus labExperimentStatus = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labserver.LabExperimentStatus proxyLabExperimentStatus = this.labServerProxy.getExperimentStatus(experimentId);
            labExperimentStatus = this.ConvertType(proxyLabExperimentStatus);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labExperimentStatus;
    }

    /**
     *
     * @param userGroup
     * @return
     */
    public String GetLabConfiguration(String userGroup) {
        final String methodName = "GetLabConfiguration";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String labConfiguration = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            labConfiguration = this.labServerProxy.getLabConfiguration(userGroup);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labConfiguration;
    }

    /**
     *
     * @return
     */
    public String GetLabInfo() {
        final String methodName = "GetLabInfo";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String labInfo = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            labInfo = this.labServerProxy.getLabInfo();

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labInfo;
    }

    /**
     *
     * @return
     */
    public LabStatus GetLabStatus() {
        final String methodName = "GetLabStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        LabStatus labStatus = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labserver.LabStatus proxyLabStatus = this.labServerProxy.getLabStatus();
            labStatus = this.ConvertType(proxyLabStatus);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labStatus;
    }

    /**
     *
     * @param experimentId
     * @return
     */
    public ResultReport RetrieveResult(int experimentId) {
        final String methodName = "RetrieveResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ResultReport resultReport = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labserver.ResultReport proxyResultReport = this.labServerProxy.retrieveResult(experimentId);
            resultReport = this.ConvertType(proxyResultReport);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return resultReport;
    }

    /**
     *
     * @param experimentId
     * @param experimentSpecification
     * @param userGroup
     * @param priorityHint
     * @return
     */
    public SubmissionReport Submit(int experimentId, String experimentSpecification, String userGroup, int priorityHint) {
        final String methodName = "Submit";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        SubmissionReport submissionReport = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labserver.SubmissionReport proxySubmissionReport = this.labServerProxy.submit(experimentId, experimentSpecification, userGroup, priorityHint);
            submissionReport = this.ConvertType(proxySubmissionReport);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return submissionReport;
    }

    /**
     *
     * @param experimentSpecification
     * @param userGroup
     * @return
     */
    public ValidationReport Validate(String experimentSpecification, String userGroup) {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ValidationReport validationReport = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labserver.ValidationReport proxyValidationReport = this.labServerProxy.validate(experimentSpecification, userGroup);
            validationReport = this.ConvertType(proxyValidationReport);

        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw new ProtocolException(ex.getFault().getFaultString());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ProtocolException(STRERR_LabServerUnaccessible);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return validationReport;
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
        ((BindingProvider) this.labServerProxy).getRequestContext().put(this.qnameAuthHeader.getLocalPart(), authHeader);
    }

    //<editor-fold defaultstate="collapsed" desc="ConvertType">
    /**
     *
     * @param arrayOfString
     * @return String[]
     */
    private String[] ConvertType(uq.ilabs.labserver.ArrayOfString arrayOfString) {
        String[] strings = null;

        if (arrayOfString != null) {
            strings = arrayOfString.getString().toArray(new String[0]);
        }

        return strings;
    }

    /**
     *
     * @param proxySubmissionReport
     * @return SubmissionReport
     */
    private SubmissionReport ConvertType(uq.ilabs.labserver.SubmissionReport proxySubmissionReport) {
        SubmissionReport submissionReport = null;

        if (proxySubmissionReport != null) {
            submissionReport = new SubmissionReport();
            submissionReport.setExperimentId(proxySubmissionReport.getExperimentID());
            submissionReport.setMinTimeToLive(proxySubmissionReport.getMinTimeToLive());
            submissionReport.setValidationReport(this.ConvertType(proxySubmissionReport.getVReport()));
            submissionReport.setWaitEstimate(this.ConvertType(proxySubmissionReport.getWait()));
        }

        return submissionReport;
    }

    /**
     *
     * @param proxyExperimentStatus
     * @return ExperimentStatus
     */
    private ExperimentStatus ConvertType(uq.ilabs.labserver.ExperimentStatus proxyExperimentStatus) {
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
    private LabExperimentStatus ConvertType(uq.ilabs.labserver.LabExperimentStatus proxyLabExperimentStatus) {
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
    private LabStatus ConvertType(uq.ilabs.labserver.LabStatus proxyLabStatus) {
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
    private ResultReport ConvertType(uq.ilabs.labserver.ResultReport proxyResultReport) {
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
    private ValidationReport ConvertType(uq.ilabs.labserver.ValidationReport proxyValidationReport) {
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
    private WaitEstimate ConvertType(uq.ilabs.labserver.WaitEstimate proxyWaitEstimate) {
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
