/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.labserver;

import javax.xml.bind.JAXBElement;
import javax.xml.ws.BindingProvider;
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
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_ServiceUrl_arg = "ServiceUrl: %s";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabServerWebServiceSoap proxyLabServer;
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
        Logfile.WriteCalled(STR_ClassName, methodName,
                String.format(STRLOG_ServiceUrl_arg, serviceUrl));

        try {
            /*
             * Create a proxy for the LabServer's web service and set the web service URL
             */
            LabServerWebService labServerWebService = new LabServerWebService();
            if (labServerWebService == null) {
                throw new NullPointerException(LabServerWebService.class.getSimpleName());
            }
            this.proxyLabServer = labServerWebService.getLabServerWebServiceSoap();
            BindingProvider bp = (BindingProvider) this.proxyLabServer;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUrl);
        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }

    /**
     *
     * @param experimentId
     * @return
     */
    public boolean Cancel(int experimentId) {
        final String methodName = "Cancel";
        Logfile.WriteCalled(STR_ClassName, methodName);

        boolean cancelled = false;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            cancelled = this.proxyLabServer.cancel(experimentId);
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

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
        Logfile.WriteCalled(STR_ClassName, methodName);

        WaitEstimate waitEstimate = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labserver.WaitEstimate proxyWaitEstimate = this.proxyLabServer.getEffectiveQueueLength(userGroup, priorityHint);

            /*
             * Convert to return type
             */
            waitEstimate = new WaitEstimate();
            waitEstimate.setEffectiveQueueLength(proxyWaitEstimate.getEffectiveQueueLength());
            waitEstimate.setEstWait(proxyWaitEstimate.getEstWait());
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return waitEstimate;
    }

    /**
     *
     * @param experimentId
     * @return
     */
    public LabExperimentStatus GetExperimentStatus(int experimentId) {
        final String methodName = "GetExperimentStatus";
        Logfile.WriteCalled(STR_ClassName, methodName);

        LabExperimentStatus labExperimentStatus = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labserver.LabExperimentStatus proxyLabExperimentStatus = this.proxyLabServer.getExperimentStatus(experimentId);
            uq.ilabs.labserver.ExperimentStatus proxyExperimentStatus = proxyLabExperimentStatus.getStatusReport();
            uq.ilabs.labserver.WaitEstimate proxyWaitEstimate = proxyExperimentStatus.getWait();

            /*
             * Convert to return type
             */
            WaitEstimate waitEstimate = new WaitEstimate();
            waitEstimate.setEffectiveQueueLength(proxyWaitEstimate.getEffectiveQueueLength());
            waitEstimate.setEstWait(proxyWaitEstimate.getEstWait());

            ExperimentStatus experimentStatus = new ExperimentStatus();
            experimentStatus.setEstRemainingRuntime(proxyExperimentStatus.getEstRemainingRuntime());
            experimentStatus.setEstRuntime(proxyExperimentStatus.getEstRuntime());
            experimentStatus.setStatusCode(StatusCodes.ToStatusCode(proxyExperimentStatus.getStatusCode()));
            experimentStatus.setWaitEstimate(waitEstimate);

            labExperimentStatus = new LabExperimentStatus();
            labExperimentStatus.setMinTimetoLive(proxyLabExperimentStatus.getMinTimetoLive());
            labExperimentStatus.setExperimentStatus(experimentStatus);
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return labExperimentStatus;
    }

    /**
     *
     * @param userGroup
     * @return
     */
    public String GetLabConfiguration(String userGroup) {
        final String methodName = "GetLabConfiguration";
        Logfile.WriteCalled(STR_ClassName, methodName);

        String labConfiguration = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            labConfiguration = this.proxyLabServer.getLabConfiguration(userGroup);
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return labConfiguration;
    }

    /**
     *
     * @return
     */
    public String GetLabInfo() {
        final String methodName = "GetLabInfo";
        Logfile.WriteCalled(STR_ClassName, methodName);

        String labInfo = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            labInfo = this.proxyLabServer.getLabInfo();
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return labInfo;
    }

    /**
     *
     * @return
     */
    public LabStatus GetLabStatus() {
        final String methodName = "GetLabStatus";
        Logfile.WriteCalled(STR_ClassName, methodName);

        LabStatus labStatus = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labserver.LabStatus proxyLabStatus = this.proxyLabServer.getLabStatus();

            /*
             * Convert to return type
             */
            labStatus = new LabStatus();
            labStatus.setOnline(proxyLabStatus.isOnline());
            labStatus.setLabStatusMessage(proxyLabStatus.getLabStatusMessage());
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return labStatus;
    }

    /**
     *
     * @param experimentId
     * @return
     */
    public ResultReport RetrieveResult(int experimentId) {
        final String methodName = "RetrieveResult";
        Logfile.WriteCalled(STR_ClassName, methodName);

        ResultReport resultReport = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labserver.ResultReport proxyResultReport = this.proxyLabServer.retrieveResult(experimentId);
            uq.ilabs.labserver.ArrayOfString proxyWarningMessages = proxyResultReport.getWarningMessages();

            /*
             * Convert to return type
             */
            resultReport = new ResultReport();
            resultReport.setErrorMessage(proxyResultReport.getErrorMessage());
            resultReport.setXmlExperimentResults(proxyResultReport.getExperimentResults());
            resultReport.setStatusCode(StatusCodes.ToStatusCode(proxyResultReport.getStatusCode()));
            if (proxyWarningMessages != null) {
                String[] warningMessages = proxyWarningMessages.getString().toArray(new String[0]);
                resultReport.setWarningMessages(warningMessages);
            }
            resultReport.setXmlBlobExtension(proxyResultReport.getXmlBlobExtension());
            resultReport.setXmlResultExtension(proxyResultReport.getXmlResultExtension());
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

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
        Logfile.WriteCalled(STR_ClassName, methodName);

        SubmissionReport submissionReport = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labserver.SubmissionReport proxySubmissionReport = this.proxyLabServer.submit(experimentId, experimentSpecification, userGroup, priorityHint);
            uq.ilabs.labserver.ValidationReport proxyValidationReport = proxySubmissionReport.getVReport();
            uq.ilabs.labserver.WaitEstimate proxyWaitEstimate = proxySubmissionReport.getWait();

            /*
             * Convert to return type
             */
            submissionReport = new SubmissionReport();
            submissionReport.setExperimentId(proxySubmissionReport.getExperimentID());
            submissionReport.setMinTimeToLive(proxySubmissionReport.getMinTimeToLive());

            ValidationReport validationReport = new ValidationReport();
            validationReport.setAccepted(proxyValidationReport.isAccepted());
            validationReport.setErrorMessage(proxyValidationReport.getErrorMessage());
            validationReport.setEstRuntime(proxyValidationReport.getEstRuntime());
            if (proxyValidationReport.getWarningMessages() != null) {
                String[] warningMessages = proxyValidationReport.getWarningMessages().getString().toArray(new String[0]);
                validationReport.setWarningMessages(warningMessages);
            }

            WaitEstimate waitEstimate = new WaitEstimate();
            waitEstimate.setEffectiveQueueLength(proxyWaitEstimate.getEffectiveQueueLength());
            waitEstimate.setEstWait(proxyWaitEstimate.getEstWait());

            submissionReport.setValidationReport(validationReport);
            submissionReport.setWaitEstimate(waitEstimate);
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

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
        Logfile.WriteCalled(STR_ClassName, methodName);

        ValidationReport validationReport = null;

        try {
            /*
             * Set the authentication information and call the web service
             */
            this.SetAuthHeader();
            uq.ilabs.labserver.ValidationReport proxyValidationReport = this.proxyLabServer.validate(experimentSpecification, userGroup);

            /*
             * Convert to return type
             */
            validationReport = new ValidationReport();
            validationReport.setAccepted(proxyValidationReport.isAccepted());
            validationReport.setErrorMessage(proxyValidationReport.getErrorMessage());
            validationReport.setEstRuntime(proxyValidationReport.getEstRuntime());
            if (proxyValidationReport.getWarningMessages() != null) {
                String[] warningMessages = proxyValidationReport.getWarningMessages().getString().toArray(new String[0]);
                validationReport.setWarningMessages(warningMessages);
            }
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return validationReport;
    }

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
        if (this.proxyLabServer != null) {
            BindingProvider bindingProvider = (BindingProvider) this.proxyLabServer;
            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<AuthHeader> jaxbElement = objectFactory.createAuthHeader(authHeader);
            bindingProvider.getRequestContext().put(jaxbElement.getName().getLocalPart(), authHeader);
        }
    }
}
