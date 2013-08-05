/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.rest;

import edu.mit.ilab.rest.SbAuthHeader;
import java.util.logging.Level;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.xml.ws.ProtocolException;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.SubmissionReport;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.LabServerAPI;
import uq.ilabs.servicebroker.ServiceBrokerBean;
import uq.ilabs.servicebroker.database.ExperimentsDB;
import uq.ilabs.servicebroker.database.types.ExperimentInfo;
import uq.ilabs.servicebroker.engine.ConfigProperties;

/**
 *
 * @author uqlpayne
 */
@Singleton
public class ServiceBrokerRestBean {
    //<editor-fold defaultstate="collapsed" desc="Constants">

    private static final String STR_ClassName = ServiceBrokerRestBean.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_SbAuthHeaderNull = "SbAuthHeader: null";
    private static final String STRLOG_CouponIdPasskey_arg2 = "CouponId: %d  CouponPasskey: '%s'";
    private static final String STRLOG_LabServerGuid_arg = "LabServerGuid: %s";
    private static final String STRLOG_ExperimentId_arg = " ExperimentId: %d";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_AccessDenied_arg = "LabServer Access Denied: %s";
    private static final String STRERR_SbAuthHeaderNull = "SbAuthHeader is null";
    private static final String STRERR_CouponIdInvalid_arg = "CouponId %d is invalid";
    private static final String STRERR_CouponPasskeyNull = "CouponPasskey is null";
    private static final String STRERR_CouponPasskeyInvalid_arg = "CouponPasskey '%s' is invalid";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private ConfigProperties configProperties;
    private ExperimentsDB experimentsDB;
    //</editor-fold>

    public ServiceBrokerRestBean() {
        final String methodName = "ServiceBrokerRestBean";

        /*
         * Check if initialisation needs to be done
         */
        if (ServiceBrokerBean.isInitialised() == true && this.configProperties == null) {
            Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

            /*
             * Save the configuration properties locally
             */
            this.configProperties = ServiceBrokerBean.getConfigProperties();
            this.experimentsDB = ServiceBrokerBean.getExperimentsDB();

            Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
        }
    }

    /**
     *
     * @param httpHeaders
     * @param experimentId
     * @return boolean
     */
    public boolean cancel(SbAuthHeader sbAuthHeader, int experimentId) {
        final String methodName = "cancel";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        boolean success = false;

        try {
            this.Authenticate(sbAuthHeader);

            /*
             * Get the LabServer for the specified experiment
             */
            ExperimentInfo experimentInfo = this.experimentsDB.RetrieveByExperimentId(experimentId);
            if (experimentInfo != null) {
                /*
                 * Pass to LabServer for processing
                 */
                LabServerAPI labServerAPI = ServiceBrokerBean.GetLabServerAPI(experimentInfo.getLabServerGuid());
                success = labServerAPI.Cancel(experimentId);
            }
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return success;
    }

    /**
     *
     * @param sbAuthHeader
     * @param labServerGuid
     * @param priorityHint
     * @return edu.mit.ilab.rest.WaitEstimate
     */
    public edu.mit.ilab.rest.WaitEstimate getEffectiveQueueLength(SbAuthHeader sbAuthHeader, String labServerGuid, int priorityHint) {
        final String methodName = "getEffectiveQueueLength";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        edu.mit.ilab.rest.WaitEstimate proxyWaitEstimate = null;

        try {
            this.Authenticate(sbAuthHeader);

            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = ServiceBrokerBean.GetLabServerAPI(labServerGuid);
            WaitEstimate waitEstimate = labServerAPI.GetEffectiveQueueLength(ServiceBrokerBean.STR_UserGroup, priorityHint);
            proxyWaitEstimate = ConvertTypes.Convert(waitEstimate);

        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return proxyWaitEstimate;
    }

    /**
     * /**
     *
     * @param sbAuthHeader
     * @param experimentId
     * @return edu.mit.ilab.rest.LabExperimentStatus
     */
    public edu.mit.ilab.rest.LabExperimentStatus getExperimentStatus(SbAuthHeader sbAuthHeader, int experimentId) {
        final String methodName = "getExperimentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        edu.mit.ilab.rest.LabExperimentStatus proxyLabExperimentStatus = null;

        try {
            this.Authenticate(sbAuthHeader);

            /*
             * Get the LabServer for the specified experiment
             */
            ExperimentInfo experimentInfo = this.experimentsDB.RetrieveByExperimentId(experimentId);
            if (experimentInfo != null) {
                /*
                 * Pass to LabServer for processing
                 */
                LabServerAPI labServerAPI = ServiceBrokerBean.GetLabServerAPI(experimentInfo.getLabServerGuid());
                LabExperimentStatus labExperimentStatus = labServerAPI.GetExperimentStatus(experimentId);
                proxyLabExperimentStatus = ConvertTypes.Convert(labExperimentStatus);
            }
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return proxyLabExperimentStatus;
    }

    /**
     *
     * @param sbAuthHeader
     * @param labServerGuid
     * @return String
     */
    public String getLabConfiguration(SbAuthHeader sbAuthHeader, String labServerGuid) {
        final String methodName = "getLabConfiguration";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String labConfiguration = null;

        try {
            this.Authenticate(sbAuthHeader);

            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = ServiceBrokerBean.GetLabServerAPI(labServerGuid);
            labConfiguration = labServerAPI.GetLabConfiguration(ServiceBrokerBean.STR_UserGroup);

        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labConfiguration;
    }

    /**
     *
     * @param sbAuthHeader
     * @param labServerGuid
     * @return String
     */
    public String getLabInfo(SbAuthHeader sbAuthHeader, String labServerGuid) {
        final String methodName = "getLabInfo";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String labInfo = null;

        try {
            this.Authenticate(sbAuthHeader);

            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = ServiceBrokerBean.GetLabServerAPI(labServerGuid);
            labInfo = labServerAPI.GetLabInfo();

        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labInfo;
    }

    /**
     *
     * @param sbAuthHeader
     * @param labServerGuid
     * @return edu.mit.ilab.rest.LabStatus
     */
    public edu.mit.ilab.rest.LabStatus getLabStatus(SbAuthHeader sbAuthHeader, String labServerGuid) {
        final String methodName = "getLabStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_LabServerGuid_arg, labServerGuid));

        edu.mit.ilab.rest.LabStatus proxyLabStatus = null;

        try {
            this.Authenticate(sbAuthHeader);

            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = ServiceBrokerBean.GetLabServerAPI(labServerGuid);
            LabStatus labStatus = labServerAPI.GetLabStatus();
            proxyLabStatus = ConvertTypes.Convert(labStatus);

        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return proxyLabStatus;
    }

    /**
     *
     * @param sbAuthHeader
     * @param experimentId
     * @return edu.mit.ilab.rest.ResultReport
     */
    public edu.mit.ilab.rest.ResultReport retrieveResult(SbAuthHeader sbAuthHeader, int experimentId) {
        final String methodName = "retrieveResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        edu.mit.ilab.rest.ResultReport proxyResultReport = null;

        try {
            this.Authenticate(sbAuthHeader);

            /*
             * Get the LabServer for the specified experiment
             */
            ExperimentInfo experimentInfo = this.experimentsDB.RetrieveByExperimentId(experimentId);
            if (experimentInfo != null) {
                /*
                 * Pass to LabServer for processing
                 */
                LabServerAPI labServerAPI = ServiceBrokerBean.GetLabServerAPI(experimentInfo.getLabServerGuid());
                ResultReport resultReport = labServerAPI.RetrieveResult(experimentId);
                proxyResultReport = ConvertTypes.Convert(resultReport);
            }
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return proxyResultReport;
    }

    /**
     *
     * @param sbAuthHeader
     * @param labServerGuid
     * @param experimentSpecification
     * @param priorityHint
     * @param emailNotification
     * @return edu.mit.ilab.rest.ClientSubmissionReport
     */
    public edu.mit.ilab.rest.ClientSubmissionReport submit(SbAuthHeader sbAuthHeader, String labServerGuid, String experimentSpecification, int priorityHint, boolean emailNotification) {
        final String methodName = "submit";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        edu.mit.ilab.rest.ClientSubmissionReport proxyClientSubmissionReport = null;

        try {
            this.Authenticate(sbAuthHeader);

            /*
             * Get the next experiment Id from the experiment database
             */
            int experimentId = this.experimentsDB.GetNextExperimentId();

            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = ServiceBrokerBean.GetLabServerAPI(labServerGuid);
            SubmissionReport submissionReport = labServerAPI.Submit(experimentId, experimentSpecification, ServiceBrokerBean.STR_UserGroup, priorityHint);
            proxyClientSubmissionReport = ConvertTypes.Convert(submissionReport);

            /*
             * Check if experiment was accepted
             */
            if (submissionReport.getValidationReport().isAccepted() == true) {
                /*
                 * Add LabServer to the experiment database
                 */
                this.experimentsDB.Add(labServerGuid);
            }
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return proxyClientSubmissionReport;
    }

    /**
     *
     * @param sbAuthHeader
     * @param labServerGuid
     * @param experimentSpecification
     * @return edu.mit.ilab.rest.ValidationReport
     */
    public edu.mit.ilab.rest.ValidationReport validate(SbAuthHeader sbAuthHeader, String labServerGuid, String experimentSpecification) {
        final String methodName = "validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        edu.mit.ilab.rest.ValidationReport proxyValidationReport = null;

        try {
            this.Authenticate(sbAuthHeader);

            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = ServiceBrokerBean.GetLabServerAPI(labServerGuid);
            ValidationReport validationReport = labServerAPI.Validate(experimentSpecification, labServerGuid);
            proxyValidationReport = ConvertTypes.Convert(validationReport);

        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return proxyValidationReport;
    }

    /**
     *
     * @param sbAuthHeader
     * @param experimentId
     */
    public void notify(SbAuthHeader sbAuthHeader, int experimentId) {
        final String methodName = "notify";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

//        if (sbAuthHeader == null) {
//            sbAuthHeader = new SbAuthHeader();
//        }
//        sbAuthHeader.setCouponID(this.configProperties.getCouponId());
//        sbAuthHeader.setCouponPassKey(this.configProperties.getCouponPasskey());
//        this.retrieveResult(sbAuthHeader, experimentId);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param sbAuthHeader
     * @return boolean
     */
    private boolean Authenticate(SbAuthHeader sbAuthHeader) {
        /*
         * Assume this will fail
         */
        boolean success = false;

        /*
         * Check if authenticating
         */
        if (this.configProperties.isAuthenticating() == true) {
            if (this.configProperties.isLogAuthentication() == true) {
                if (sbAuthHeader == null) {
                    Logfile.Write(STRLOG_SbAuthHeaderNull);
                } else {
                    Logfile.Write(String.format(STRLOG_CouponIdPasskey_arg2, sbAuthHeader.getCouponID(), sbAuthHeader.getCouponPassKey()));
                }
            }
            try {
                /*
                 * Check that AuthHeader is specified
                 */
                if (sbAuthHeader == null) {
                    throw new RuntimeException(STRERR_SbAuthHeaderNull);
                }

                /*
                 * Verify the Coupon Id
                 */
                if (sbAuthHeader.getCouponID() != this.configProperties.getCouponId()) {
                    throw new RuntimeException(String.format(STRERR_CouponIdInvalid_arg, sbAuthHeader.getCouponID()));
                }

                /*
                 * Verify the Coupon Passkey
                 */
                if (sbAuthHeader.getCouponPassKey() == null) {
                    throw new RuntimeException(STRERR_CouponPasskeyNull);
                }
                if (sbAuthHeader.getCouponPassKey().equalsIgnoreCase(this.configProperties.getCouponPasskey()) == false) {
                    throw new RuntimeException(String.format(STRERR_CouponPasskeyInvalid_arg, sbAuthHeader.getCouponPassKey()));
                }

                /*
                 * Successfully authenticated
                 */
                success = true;

            } catch (Exception ex) {
                String message = String.format(STRERR_AccessDenied_arg, ex.getMessage());
                Logfile.WriteError(message);
                throw new ProtocolException(message);
            }
        } else {
            success = true;
        }

        return success;
    }

    private void ThrowSOAPFault(String message) {
    }

    /**
     *
     */
    @PreDestroy
    private void preDestroy() {
        final String methodName = "preDestroy";
        Logfile.WriteCalled(Level.INFO, STR_ClassName, methodName);

        ServiceBrokerBean.Close();

        Logfile.WriteCompleted(Level.INFO, STR_ClassName, methodName);
    }
}
