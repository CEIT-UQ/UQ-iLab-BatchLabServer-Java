/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker;

import java.util.HashMap;
import java.util.logging.Level;
import javax.xml.ws.ProtocolException;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.types.ClientSubmissionReport;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.SbAuthHeader;
import uq.ilabs.library.lab.types.SubmissionReport;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.LabServerAPI;
import uq.ilabs.servicebroker.database.ExperimentsDB;
import uq.ilabs.servicebroker.database.types.ExperimentInfo;
import uq.ilabs.servicebroker.engine.ConfigProperties;
import uq.ilabs.servicebroker.engine.types.LabServerInfo;

/**
 *
 * @author uqlpayne
 */
public class ServiceBrokerHandler {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokerBean.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_NextExperimentId_arg = "Next ExperimentId: %d";
    private static final String STRLOG_ExperimentId_arg = " ExperimentId: %d";
    private static final String STRLOG_LabServerGuid_arg = "LabServerGuid: %s";
    private static final String STRLOG_SbAuthHeaderNull = "SbAuthHeader: null";
    private static final String STRLOG_CouponIdPasskey_arg2 = "CouponId: %d  CouponPasskey: '%s'";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_AccessDenied_arg = "LabServer Access Denied: %s";
    private static final String STRERR_SbAuthHeaderNull = "SbAuthHeader is null";
    private static final String STRERR_CouponIdInvalid_arg = "CouponId %d is invalid";
    private static final String STRERR_CouponPasskeyNull = "CouponPasskey is null";
    private static final String STRERR_CouponPasskeyInvalid_arg = "CouponPasskey '%s' is invalid";
    private static final String STRERR_LabServerUnknown_arg = "LabServer Unknown: %s";
    /*
     * String constants
     */
    public static final String STR_UserGroup = "DummyServiceBroker";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private ConfigProperties configProperties;
    private ExperimentsDB experimentsDB;
    private HashMap<String, LabServerAPI> mapLabServerAPI;
    //</editor-fold>

    /**
     *
     * @param configProperties
     */
    public ServiceBrokerHandler(ConfigProperties configProperties) throws Exception {
        final String methodName = "ServiceBrokerHandler";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        this.configProperties = configProperties;
        this.mapLabServerAPI = new HashMap<>();

        try {
            /*
             * Create instance of Experiments database API
             */
            DBConnection dbConnection = this.configProperties.getDbConnection();
            this.experimentsDB = new ExperimentsDB(dbConnection);

            /*
             * Get the next experiment Id from the experiment database
             */
            int nextExperimentId = this.experimentsDB.GetNextExperimentId();
            Logfile.Write(String.format(STRLOG_NextExperimentId_arg, nextExperimentId));

        } catch (Exception ex) {
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
                LabServerAPI labServerAPI = this.GetLabServerAPI(experimentInfo.getLabServerGuid());
                success = labServerAPI.Cancel(experimentId);
            }
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
     * @return WaitEstimate
     */
    public WaitEstimate getEffectiveQueueLength(SbAuthHeader sbAuthHeader, String labServerGuid, int priorityHint) {
        final String methodName = "getEffectiveQueueLength";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        WaitEstimate waitEstimate = null;

        try {
            this.Authenticate(sbAuthHeader);

            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = this.GetLabServerAPI(labServerGuid);
            waitEstimate = labServerAPI.GetEffectiveQueueLength(ServiceBrokerBean.STR_UserGroup, priorityHint);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return waitEstimate;
    }

    /**
     * /**
     *
     * @param sbAuthHeader
     * @param experimentId
     * @return LabExperimentStatus
     */
    public LabExperimentStatus getExperimentStatus(SbAuthHeader sbAuthHeader, int experimentId) {
        final String methodName = "getExperimentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        LabExperimentStatus labExperimentStatus = null;

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
                LabServerAPI labServerAPI = this.GetLabServerAPI(experimentInfo.getLabServerGuid());
                labExperimentStatus = labServerAPI.GetExperimentStatus(experimentId);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labExperimentStatus;
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
            LabServerAPI labServerAPI = this.GetLabServerAPI(labServerGuid);
            labConfiguration = labServerAPI.GetLabConfiguration(ServiceBrokerBean.STR_UserGroup);

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
            LabServerAPI labServerAPI = this.GetLabServerAPI(labServerGuid);
            labInfo = labServerAPI.GetLabInfo();

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
     * @return LabStatus
     */
    public LabStatus getLabStatus(SbAuthHeader sbAuthHeader, String labServerGuid) {
        final String methodName = "getLabStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_LabServerGuid_arg, labServerGuid));

        LabStatus labStatus = null;

        try {
            this.Authenticate(sbAuthHeader);

            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = this.GetLabServerAPI(labServerGuid);
            labStatus = labServerAPI.GetLabStatus();

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labStatus;
    }

    /**
     *
     * @param sbAuthHeader
     * @param experimentId
     * @return ResultReport
     */
    public ResultReport retrieveResult(SbAuthHeader sbAuthHeader, int experimentId) {
        final String methodName = "retrieveResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        ResultReport resultReport = null;

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
                LabServerAPI labServerAPI = this.GetLabServerAPI(experimentInfo.getLabServerGuid());
                resultReport = labServerAPI.RetrieveResult(experimentId);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return resultReport;
    }

    /**
     *
     * @param sbAuthHeader
     * @param labServerGuid
     * @param experimentSpecification
     * @param priorityHint
     * @param emailNotification
     * @return ClientSubmissionReport
     */
    public ClientSubmissionReport submit(SbAuthHeader sbAuthHeader, String labServerGuid, String experimentSpecification, int priorityHint, boolean emailNotification) {
        final String methodName = "submit";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ClientSubmissionReport clientSubmissionReport = null;

        try {
            this.Authenticate(sbAuthHeader);

            /*
             * Get the next experiment Id from the experiment database
             */
            int experimentId = this.experimentsDB.GetNextExperimentId();

            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = this.GetLabServerAPI(labServerGuid);
            SubmissionReport submissionReport = labServerAPI.Submit(experimentId, experimentSpecification, ServiceBrokerBean.STR_UserGroup, priorityHint);
            clientSubmissionReport = new ClientSubmissionReport(submissionReport);

            /*
             * Check if experiment was accepted
             */
            ValidationReport validationReport = clientSubmissionReport.getValidationReport();
            if (validationReport != null && validationReport.isAccepted() == true) {
                /*
                 * Add LabServer to the experiment database
                 */
                this.experimentsDB.Add(labServerGuid);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return clientSubmissionReport;
    }

    /**
     *
     * @param sbAuthHeader
     * @param labServerGuid
     * @param experimentSpecification
     * @return ValidationReport
     */
    public ValidationReport validate(SbAuthHeader sbAuthHeader, String labServerGuid, String experimentSpecification) {
        final String methodName = "validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ValidationReport validationReport = null;

        try {
            this.Authenticate(sbAuthHeader);

            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = this.GetLabServerAPI(labServerGuid);
            validationReport = labServerAPI.Validate(experimentSpecification, labServerGuid);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return validationReport;
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

        if (sbAuthHeader == null) {
            sbAuthHeader = new SbAuthHeader();
        }
        sbAuthHeader.setCouponId(this.configProperties.getCouponId());
        sbAuthHeader.setCouponPasskey(this.configProperties.getCouponPasskey());
        this.retrieveResult(sbAuthHeader, experimentId);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    //================================================================================================================//
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
                    Logfile.Write(String.format(STRLOG_CouponIdPasskey_arg2, sbAuthHeader.getCouponId(), sbAuthHeader.getCouponPasskey()));
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
                if (sbAuthHeader.getCouponId() != this.configProperties.getCouponId()) {
                    throw new RuntimeException(String.format(STRERR_CouponIdInvalid_arg, sbAuthHeader.getCouponId()));
                }

                /*
                 * Verify the Coupon Passkey
                 */
                if (sbAuthHeader.getCouponPasskey() == null) {
                    throw new RuntimeException(STRERR_CouponPasskeyNull);
                }
                if (sbAuthHeader.getCouponPasskey().equalsIgnoreCase(this.configProperties.getCouponPasskey()) == false) {
                    throw new RuntimeException(String.format(STRERR_CouponPasskeyInvalid_arg, sbAuthHeader.getCouponPasskey()));
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

    /**
     *
     * @param labServerGuid
     * @return LabServerAPI
     * @throws Exception
     */
    private LabServerAPI GetLabServerAPI(String labServerGuid) throws Exception {
        LabServerAPI labServerAPI;

        /*
         * Check if the BatchLabServerAPI for this labServerGuid already exists
         */
        if ((labServerAPI = this.mapLabServerAPI.get(labServerGuid)) == null) {
            /*
             * Get LabServer information
             */
            LabServerInfo labServerInfo = this.configProperties.GetLabServerInfo(labServerGuid);
            if (labServerInfo == null) {
                throw new RuntimeException(String.format(STRERR_LabServerUnknown_arg, labServerGuid));
            }

            /*
             * Create an instance of LabServerAPI for this LabServer
             */
            labServerAPI = new LabServerAPI(labServerInfo.getServiceUrl());
            labServerAPI.setIdentifier(this.configProperties.getServiceBrokerGuid());
            labServerAPI.setPasskey(labServerInfo.getOutgoingPasskey());

            /*
             * Add the LabServerAPI to the map for next time
             */
            this.mapLabServerAPI.put(labServerGuid, labServerAPI);
        }

        return labServerAPI;
    }
}
