/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient.servicebroker;

import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.BindingProvider;
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
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ServiceUrl = "serviceUrl";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private ServiceBrokerServiceSoap proxyServiceBroker;
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
            if (serviceBrokerService == null) {
                throw new NullPointerException(ServiceBrokerService.class.getSimpleName());
            }
            this.proxyServiceBroker = serviceBrokerService.getServiceBrokerServiceSoap();
            BindingProvider bp = (BindingProvider) this.proxyServiceBroker;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUrl);
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
            this.SetSbAuthHeader();
            cancelled = proxyServiceBroker.cancel(experimentId);
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return cancelled;
    }

    /**
     *
     * @return
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
            uq.ilabs.servicebroker.WaitEstimate proxyWaitEstimate = this.proxyServiceBroker.getEffectiveQueueLength(this.labServerId, priorityHint);

            /*
             * Check the return value
             */
            if (proxyWaitEstimate == null) {
                throw new NullPointerException(WaitEstimate.class.getSimpleName());
            }

            /*
             * Convert to return type
             */
            waitEstimate = new WaitEstimate();
            waitEstimate.setEffectiveQueueLength(proxyWaitEstimate.getEffectiveQueueLength());
            waitEstimate.setEstWait(proxyWaitEstimate.getEstWait());
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
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
            this.SetSbAuthHeader();
            uq.ilabs.servicebroker.LabExperimentStatus proxyLabExperimentStatus = this.proxyServiceBroker.getExperimentStatus(experimentId);

            /*
             * Check the return value
             */
            if (proxyLabExperimentStatus == null) {
                throw new NullPointerException(LabExperimentStatus.class.getSimpleName());
            }
            uq.ilabs.servicebroker.ExperimentStatus proxyExperimentStatus = proxyLabExperimentStatus.getStatusReport();
            if (proxyExperimentStatus == null) {
                throw new NullPointerException(ExperimentStatus.class.getSimpleName());
            }
            uq.ilabs.servicebroker.WaitEstimate proxyWaitEstimate = proxyExperimentStatus.getWait();
            if (proxyWaitEstimate == null) {
                throw new NullPointerException(WaitEstimate.class.getSimpleName());
            }

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
            throw ex;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labExperimentStatus;
    }

    /**
     *
     * @return
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
            labConfiguration = proxyServiceBroker.getLabConfiguration(this.labServerId);
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
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
            this.SetSbAuthHeader();
            labInfo = proxyServiceBroker.getLabInfo(this.labServerId);
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
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
            this.SetSbAuthHeader();
            uq.ilabs.servicebroker.LabStatus proxyLabStatus = this.proxyServiceBroker.getLabStatus(this.labServerId);

            /*
             * Check the return value
             */
            if (proxyLabStatus == null) {
                throw new NullPointerException(LabStatus.class.getSimpleName());
            }

            /*
             * Convert to return type
             */
            labStatus = new LabStatus();
            labStatus.setOnline(proxyLabStatus.isOnline());
            labStatus.setLabStatusMessage(proxyLabStatus.getLabStatusMessage());
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
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
            this.SetSbAuthHeader();
            uq.ilabs.servicebroker.ResultReport proxyResultReport = this.proxyServiceBroker.retrieveResult(experimentId);

            /*
             * Check the return value
             */
            if (proxyResultReport == null) {
                throw new NullPointerException(ResultReport.class.getSimpleName());
            }

            /*
             * Convert to return type
             */
            resultReport = new ResultReport();
            resultReport.setErrorMessage(proxyResultReport.getErrorMessage());
            resultReport.setXmlExperimentResults(proxyResultReport.getExperimentResults());
            resultReport.setStatusCode(StatusCodes.ToStatusCode(proxyResultReport.getStatusCode()));
            resultReport.setXmlBlobExtension(proxyResultReport.getXmlBlobExtension());
            resultReport.setXmlResultExtension(proxyResultReport.getXmlResultExtension());
            if (proxyResultReport.getWarningMessages() != null) {
                String[] warningMessages = proxyResultReport.getWarningMessages().getString().toArray(new String[0]);
                resultReport.setWarningMessages(warningMessages);
            }
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return resultReport;
    }

    /**
     *
     * @param experimentSpecification
     * @return
     */
    public ClientSubmissionReport Submit(String experimentSpecification) {
        return this.Submit(experimentSpecification, 0, false);
    }

    /**
     *
     * @param experimentSpecification
     * @param priorityHint
     * @param emailNotification
     * @return
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
            uq.ilabs.servicebroker.ClientSubmissionReport proxySubmissionReport = this.proxyServiceBroker.submit(this.labServerId, experimentSpecification, priorityHint, emailNotification);

            /*
             * Check the return value
             */
            if (proxySubmissionReport == null) {
                throw new NullPointerException(ClientSubmissionReport.class.getSimpleName());
            }
            uq.ilabs.servicebroker.ValidationReport proxyValidationReport = proxySubmissionReport.getVReport();
            if (proxyValidationReport == null) {
                throw new NullPointerException(ValidationReport.class.getSimpleName());
            }
            uq.ilabs.servicebroker.WaitEstimate proxyWaitEstimate = proxySubmissionReport.getWait();
            if (proxyWaitEstimate == null) {
                throw new NullPointerException(WaitEstimate.class.getSimpleName());
            }

            /*
             * Convert to return type
             */
            clientSubmissionReport = new ClientSubmissionReport();
            clientSubmissionReport.setExperimentId(proxySubmissionReport.getExperimentID());
            clientSubmissionReport.setMinTimeToLive(proxySubmissionReport.getMinTimeToLive());

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

            clientSubmissionReport.setValidationReport(validationReport);
            clientSubmissionReport.setWaitEstimate(waitEstimate);
        } catch (SOAPFaultException ex) {
            Logfile.Write(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return clientSubmissionReport;
    }

    /**
     *
     * @param experimentSpecification
     * @return
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
            uq.ilabs.servicebroker.ValidationReport proxyValidationReport = this.proxyServiceBroker.validate(this.labServerId, experimentSpecification);

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
            throw ex;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return validationReport;
    }

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
        if (this.proxyServiceBroker != null) {
            BindingProvider bindingProvider = (BindingProvider) this.proxyServiceBroker;
            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<SbAuthHeader> jaxbElement = objectFactory.createSbAuthHeader(sbAuthHeader);
            bindingProvider.getRequestContext().put(jaxbElement.getName().getLocalPart(), sbAuthHeader);
        }
    }
}
