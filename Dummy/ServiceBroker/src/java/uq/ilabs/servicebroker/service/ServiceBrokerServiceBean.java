/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.service;

import edu.mit.ilab.SbAuthHeader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.soap.SOAPFaultException;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.types.*;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.LabServerAPI;
import uq.ilabs.servicebroker.database.ExperimentsDB;
import uq.ilabs.servicebroker.engine.ConfigProperties;
import uq.ilabs.servicebroker.engine.types.LabServerInfo;

/**
 *
 * @author uqlpayne
 */
@Singleton
@LocalBean
public class ServiceBrokerServiceBean {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokerServiceBean.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants
     */
    private static final String STR_UserGroup = "DummyServiceBroker";
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_SbAuthHeaderNull = "SbAuthHeader: null";
    private static final String STRLOG_CouponIdPasskey_arg2 = "CouponId: %d  CouponPasskey: '%s'";
    private static final String STRLOG_ServiceBrokerGuid_arg = "ServiceBrokerGuid: %s";
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
    private ExperimentsDB dbExperiments;
    private HashMap<String, LabServerAPI> mapLabServerAPI;
    //</editor-fold>

    /**
     * Constructor - Seems that this gets called when the project is deployed which is unexpected. To get around this,
     * check to see if the ServiceBroker has been initialised and the configuration properties set. Can't do logging
     * until the ServiceBroker has been initialised and the logger created.
     */
    public ServiceBrokerServiceBean() {
        final String methodName = "ServiceBrokerServiceBean";

        /*
         * Check if initialisation needs to be done
         */
        if (ServiceBrokerService.isInitialised() == true && this.configProperties == null) {
            Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

            /*
             * Save the configuration properties locally and process
             */
            this.configProperties = ServiceBrokerService.getConfigProperties();

            Logfile.Write(String.format(STRLOG_ServiceBrokerGuid_arg, this.configProperties.getServiceBrokerGuid()));

            try {
                /*
                 * Create an instance of the database connection
                 */
                DBConnection dbConnection = new DBConnection(this.configProperties.getDbDatabase());
                if (dbConnection == null) {
                    throw new NullPointerException(DBConnection.class.getSimpleName());
                }
                dbConnection.setHost(this.configProperties.getDbHost());
                dbConnection.setUser(this.configProperties.getDbUser());
                dbConnection.setPassword(this.configProperties.getDbPassword());

                /*
                 * Create instance of Experiments database API
                 */
                this.dbExperiments = new ExperimentsDB(dbConnection);
                if (this.dbExperiments == null) {
                    throw new NullPointerException(ExperimentsDB.class.getSimpleName());
                }

                /*
                 * Initialise local variables
                 */
                this.mapLabServerAPI = new HashMap<>();
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
            }

            Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
        }
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

        this.Authenticate(sbAuthHeader);

        try {
            /*
             * Get the LabServer for the specified experiment
             */
            String labServerGuid = this.dbExperiments.RetrieveByExperimentId(experimentId);
            if (labServerGuid != null) {
                /*
                 * Pass to LabServer for processing
                 */
                LabServerAPI labServerAPI = GetLabServerAPI(labServerGuid);
                success = labServerAPI.Cancel(experimentId);
            }
        } catch (ProtocolException ex) {
            /*
             * Create a SOAPFaultException to be thrown back to the caller
             */
            try {
                SOAPFault fault = SOAPFactory.newInstance().createFault();
                fault.setFaultString(ex.getMessage());
                throw new SOAPFaultException(fault);
            } catch (SOAPException e) {
                Logfile.WriteError(e.getMessage());
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
     * @return edu.mit.ilab.WaitEstimate
     */
    public edu.mit.ilab.WaitEstimate getEffectiveQueueLength(SbAuthHeader sbAuthHeader, String labServerGuid, int priorityHint) {
        final String methodName = "getEffectiveQueueLength";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        edu.mit.ilab.WaitEstimate proxyWaitEstimate = null;

        this.Authenticate(sbAuthHeader);

        try {
            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = GetLabServerAPI(labServerGuid);
            WaitEstimate waitEstimate = labServerAPI.GetEffectiveQueueLength(STR_UserGroup, priorityHint);
            proxyWaitEstimate = this.ConvertType(waitEstimate);

        } catch (ProtocolException ex) {
            /*
             * Create a SOAPFaultException to be thrown back to the caller
             */
            try {
                SOAPFault fault = SOAPFactory.newInstance().createFault();
                fault.setFaultString(ex.getMessage());
                throw new SOAPFaultException(fault);
            } catch (SOAPException e) {
                Logfile.WriteError(e.getMessage());
            }
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
     * @return edu.mit.ilab.LabExperimentStatus
     */
    public edu.mit.ilab.LabExperimentStatus getExperimentStatus(SbAuthHeader sbAuthHeader, int experimentId) {
        final String methodName = "getExperimentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        edu.mit.ilab.LabExperimentStatus proxyLabExperimentStatus = null;

        this.Authenticate(sbAuthHeader);

        try {
            /*
             * Get the LabServer for the specified experiment
             */
            String labServerGuid = this.dbExperiments.RetrieveByExperimentId(experimentId);
            if (labServerGuid != null) {
                /*
                 * Pass to LabServer for processing
                 */
                LabServerAPI labServerAPI = GetLabServerAPI(labServerGuid);
                LabExperimentStatus labExperimentStatus = labServerAPI.GetExperimentStatus(experimentId);
                proxyLabExperimentStatus = this.ConvertType(labExperimentStatus);
            }
        } catch (ProtocolException ex) {
            /*
             * Create a SOAPFaultException to be thrown back to the caller
             */
            try {
                SOAPFault fault = SOAPFactory.newInstance().createFault();
                fault.setFaultString(ex.getMessage());
                throw new SOAPFaultException(fault);
            } catch (SOAPException e) {
                Logfile.WriteError(e.getMessage());
            }
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

        this.Authenticate(sbAuthHeader);

        try {
            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = GetLabServerAPI(labServerGuid);
            labConfiguration = labServerAPI.GetLabConfiguration(STR_UserGroup);

        } catch (ProtocolException ex) {
            /*
             * Create a SOAPFaultException to be thrown back to the caller
             */
            try {
                SOAPFault fault = SOAPFactory.newInstance().createFault();
                fault.setFaultString(ex.getMessage());
                throw new SOAPFaultException(fault);
            } catch (SOAPException e) {
                Logfile.WriteError(e.getMessage());
            }
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

        this.Authenticate(sbAuthHeader);

        try {
            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = GetLabServerAPI(labServerGuid);
            labInfo = labServerAPI.GetLabInfo();

        } catch (ProtocolException ex) {
            /*
             * Create a SOAPFaultException to be thrown back to the caller
             */
            try {
                SOAPFault fault = SOAPFactory.newInstance().createFault();
                fault.setFaultString(ex.getMessage());
                throw new SOAPFaultException(fault);
            } catch (SOAPException e) {
                Logfile.WriteError(e.getMessage());
            }
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
     * @return edu.mit.ilab.LabStatus
     */
    public edu.mit.ilab.LabStatus getLabStatus(SbAuthHeader sbAuthHeader, String labServerGuid) {
        final String methodName = "getLabStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_LabServerGuid_arg, labServerGuid));

        edu.mit.ilab.LabStatus proxyLabStatus = null;

        this.Authenticate(sbAuthHeader);

        try {
            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = GetLabServerAPI(labServerGuid);
            LabStatus labStatus = labServerAPI.GetLabStatus();
            proxyLabStatus = this.ConvertType(labStatus);

        } catch (ProtocolException ex) {
            /*
             * Create a SOAPFaultException to be thrown back to the caller
             */
            try {
                SOAPFault fault = SOAPFactory.newInstance().createFault();
                fault.setFaultString(ex.getMessage());
                throw new SOAPFaultException(fault);
            } catch (SOAPException e) {
                Logfile.WriteError(e.getMessage());
            }
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
     * @return edu.mit.ilab.ResultReport
     */
    public edu.mit.ilab.ResultReport retrieveResult(SbAuthHeader sbAuthHeader, int experimentId) {
        final String methodName = "retrieveResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        edu.mit.ilab.ResultReport proxyResultReport = null;

        this.Authenticate(sbAuthHeader);

        try {
            /*
             * Get the LabServer for the specified experiment
             */
            String labServerGuid = this.dbExperiments.RetrieveByExperimentId(experimentId);
            if (labServerGuid != null) {
                /*
                 * Pass to LabServer for processing
                 */
                LabServerAPI labServerAPI = GetLabServerAPI(labServerGuid);
                ResultReport resultReport = labServerAPI.RetrieveResult(experimentId);
                proxyResultReport = this.ConvertType(resultReport);
            }
        } catch (ProtocolException ex) {
            /*
             * Create a SOAPFaultException to be thrown back to the caller
             */
            try {
                SOAPFault fault = SOAPFactory.newInstance().createFault();
                fault.setFaultString(ex.getMessage());
                throw new SOAPFaultException(fault);
            } catch (SOAPException e) {
                Logfile.WriteError(e.getMessage());
            }
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
     * @return edu.mit.ilab.ClientSubmissionReport
     */
    public edu.mit.ilab.ClientSubmissionReport submit(SbAuthHeader sbAuthHeader, String labServerGuid, String experimentSpecification, int priorityHint, boolean emailNotification) {
        final String methodName = "submit";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        edu.mit.ilab.ClientSubmissionReport proxyClientSubmissionReport = null;

        this.Authenticate(sbAuthHeader);

        try {
            /*
             * Get the next experiment Id from the experiment database
             */
            int experimentId = this.dbExperiments.GetNextExperimentId();

            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = this.GetLabServerAPI(labServerGuid);
            SubmissionReport submissionReport = labServerAPI.Submit(experimentId, experimentSpecification, STR_UserGroup, priorityHint);
            proxyClientSubmissionReport = this.ConvertType(submissionReport);

            /*
             * Check if experiment was accepted
             */
            if (submissionReport.getValidationReport().isAccepted() == true) {
                /*
                 * Add LabServer to the experiment database
                 */
                this.dbExperiments.Add(labServerGuid);
            }
        } catch (ProtocolException ex) {
            /*
             * Create a SOAPFaultException to be thrown back to the caller
             */
            try {
                SOAPFault fault = SOAPFactory.newInstance().createFault();
                fault.setFaultString(ex.getMessage());
                throw new SOAPFaultException(fault);
            } catch (SOAPException e) {
                Logfile.WriteError(e.getMessage());
            }
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
     * @return edu.mit.ilab.ValidationReport
     */
    public edu.mit.ilab.ValidationReport validate(SbAuthHeader sbAuthHeader, String labServerGuid, String experimentSpecification) {
        final String methodName = "validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        edu.mit.ilab.ValidationReport proxyValidationReport = null;

        this.Authenticate(sbAuthHeader);

        try {
            /*
             * Pass to LabServer for processing
             */
            LabServerAPI labServerAPI = this.GetLabServerAPI(labServerGuid);
            ValidationReport validationReport = labServerAPI.Validate(experimentSpecification, labServerGuid);
            proxyValidationReport = this.ConvertType(validationReport);

        } catch (ProtocolException ex) {
            /*
             * Create a SOAPFaultException to be thrown back to the caller
             */
            try {
                SOAPFault fault = SOAPFactory.newInstance().createFault();
                fault.setFaultString(ex.getMessage());
                throw new SOAPFaultException(fault);
            } catch (SOAPException e) {
                Logfile.WriteError(e.getMessage());
            }
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

        if (sbAuthHeader == null) {
            sbAuthHeader = new SbAuthHeader();
        }
        sbAuthHeader.setCouponID(this.configProperties.getCouponId());
        sbAuthHeader.setCouponPassKey(this.configProperties.getCouponPasskey());
        this.retrieveResult(sbAuthHeader, experimentId);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param couponId
     * @param passkey
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
                    throw new ProtocolException(STRERR_SbAuthHeaderNull);
                }

                /*
                 * Verify the Coupon Id
                 */
                if (sbAuthHeader.getCouponID() != this.configProperties.getCouponId()) {
                    throw new ProtocolException(String.format(STRERR_CouponIdInvalid_arg, sbAuthHeader.getCouponID()));
                }

                /*
                 * Verify the Coupon Passkey
                 */
                if (sbAuthHeader.getCouponPassKey() == null) {
                    throw new ProtocolException(STRERR_CouponPasskeyNull);
                }
                if (sbAuthHeader.getCouponPassKey().equalsIgnoreCase(this.configProperties.getCouponPasskey()) == false) {
                    throw new ProtocolException(String.format(STRERR_CouponPasskeyInvalid_arg, sbAuthHeader.getCouponPassKey()));
                }

                /*
                 * Successfully authenticated
                 */
                success = true;

            } catch (ProtocolException ex) {
                String message = String.format(STRERR_AccessDenied_arg, ex.getMessage());
                Logfile.WriteError(message);

                /*
                 * Create a SOAPFaultException to be thrown back to the caller
                 */
                try {
                    SOAPFault fault = SOAPFactory.newInstance().createFault();
                    fault.setFaultString(message);
                    throw new SOAPFaultException(fault);
                } catch (SOAPException e) {
                }
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
            if (labServerInfo != null) {
                /*
                 * Create an instance of the LabServer API for this service url
                 */
                labServerAPI = new LabServerAPI(labServerInfo.getServiceUrl());
                labServerAPI.setIdentifier(this.configProperties.getServiceBrokerGuid());
                labServerAPI.setPasskey(labServerInfo.getOutgoingPasskey());
            }

            /*
             * Add the BatchLabServerAPI to the map for next time
             */
            this.mapLabServerAPI.put(labServerGuid, labServerAPI);
        }

        return labServerAPI;
    }

    //<editor-fold defaultstate="collapsed" desc="ConvertType">
    /**
     *
     * @param strings
     * @return edu.mit.ilab.ArrayOfString
     */
    private edu.mit.ilab.ArrayOfString ConvertType(String[] strings) {
        edu.mit.ilab.ArrayOfString arrayOfString = null;

        if (strings != null) {
            arrayOfString = new edu.mit.ilab.ArrayOfString();
            arrayOfString.getString().addAll(Arrays.asList(strings));
        }

        return arrayOfString;
    }

    /**
     *
     * @param experimentStatus
     * @return edu.mit.ilab.ExperimentStatus
     */
    private edu.mit.ilab.ExperimentStatus ConvertType(ExperimentStatus experimentStatus) {
        edu.mit.ilab.ExperimentStatus proxyExperimentStatus = null;

        if (experimentStatus != null) {
            proxyExperimentStatus = new edu.mit.ilab.ExperimentStatus();
            proxyExperimentStatus.setEstRemainingRuntime(experimentStatus.getEstRemainingRuntime());
            proxyExperimentStatus.setEstRuntime(experimentStatus.getEstRuntime());
            proxyExperimentStatus.setStatusCode(experimentStatus.getStatusCode().getValue());
            proxyExperimentStatus.setWait(this.ConvertType(experimentStatus.getWaitEstimate()));
        }

        return proxyExperimentStatus;
    }

    /**
     *
     * @param labExperimentStatus
     * @return edu.mit.ilab.LabExperimentStatus
     */
    private edu.mit.ilab.LabExperimentStatus ConvertType(LabExperimentStatus labExperimentStatus) {
        edu.mit.ilab.LabExperimentStatus proxyLabExperimentStatus = null;

        if (labExperimentStatus != null) {
            proxyLabExperimentStatus = new edu.mit.ilab.LabExperimentStatus();
            proxyLabExperimentStatus.setMinTimetoLive(labExperimentStatus.getMinTimetoLive());
            proxyLabExperimentStatus.setStatusReport(this.ConvertType(labExperimentStatus.getExperimentStatus()));
        }

        return proxyLabExperimentStatus;
    }

    /**
     *
     * @param labStatus
     * @return edu.mit.ilab.LabStatus
     */
    private edu.mit.ilab.LabStatus ConvertType(LabStatus labStatus) {
        edu.mit.ilab.LabStatus proxyLabStatus = null;

        if (labStatus != null) {
            proxyLabStatus = new edu.mit.ilab.LabStatus();
            proxyLabStatus.setOnline(labStatus.isOnline());
            proxyLabStatus.setLabStatusMessage(labStatus.getLabStatusMessage());
        }

        return proxyLabStatus;
    }

    /**
     *
     * @param resultReport
     * @return edu.mit.ilab.ResultReport
     */
    private edu.mit.ilab.ResultReport ConvertType(ResultReport resultReport) {
        edu.mit.ilab.ResultReport proxyResultReport = null;

        if (resultReport != null) {
            proxyResultReport = new edu.mit.ilab.ResultReport();
            proxyResultReport.setErrorMessage(resultReport.getErrorMessage());
            proxyResultReport.setExperimentResults(resultReport.getXmlExperimentResults());
            proxyResultReport.setStatusCode(resultReport.getStatusCode().getValue());
            proxyResultReport.setWarningMessages(this.ConvertType(resultReport.getWarningMessages()));
            proxyResultReport.setXmlBlobExtension(resultReport.getXmlBlobExtension());
            proxyResultReport.setXmlResultExtension(resultReport.getXmlResultExtension());
        }

        return proxyResultReport;
    }

    /**
     *
     * @param submissionReport
     * @return edu.mit.ilab.ClientSubmissionReport
     */
    private edu.mit.ilab.ClientSubmissionReport ConvertType(SubmissionReport submissionReport) {
        edu.mit.ilab.ClientSubmissionReport proxyClientSubmissionReport = null;

        if (submissionReport != null) {
            /*
             * Convert to the return type
             */
            proxyClientSubmissionReport = new edu.mit.ilab.ClientSubmissionReport();
            proxyClientSubmissionReport.setExperimentID((int) submissionReport.getExperimentId());
            proxyClientSubmissionReport.setMinTimeToLive(submissionReport.getMinTimeToLive());
            proxyClientSubmissionReport.setVReport(this.ConvertType(submissionReport.getValidationReport()));
            proxyClientSubmissionReport.setWait(this.ConvertType(submissionReport.getWaitEstimate()));
        }

        return proxyClientSubmissionReport;
    }

    /**
     *
     * @param validationReport
     * @return edu.mit.ilab.ValidationReport
     */
    private edu.mit.ilab.ValidationReport ConvertType(ValidationReport validationReport) {
        edu.mit.ilab.ValidationReport proxyValidationReport = null;

        if (validationReport != null) {
            proxyValidationReport = new edu.mit.ilab.ValidationReport();
            proxyValidationReport.setAccepted(validationReport.isAccepted());
            proxyValidationReport.setErrorMessage(validationReport.getErrorMessage());
            proxyValidationReport.setEstRuntime(validationReport.getEstRuntime());
            proxyValidationReport.setWarningMessages(this.ConvertType(validationReport.getWarningMessages()));
        }

        return proxyValidationReport;
    }

    /**
     *
     * @param waitEstimate
     * @return edu.mit.ilab.WaitEstimate
     */
    private edu.mit.ilab.WaitEstimate ConvertType(WaitEstimate waitEstimate) {
        edu.mit.ilab.WaitEstimate proxyWaitEstimate = null;

        if (waitEstimate != null) {
            proxyWaitEstimate = new edu.mit.ilab.WaitEstimate();
            proxyWaitEstimate.setEffectiveQueueLength(waitEstimate.getEffectiveQueueLength());
            proxyWaitEstimate.setEstWait(waitEstimate.getEstWait());
        }

        return proxyWaitEstimate;
    }
    //</editor-fold>

    /**
     *
     */
    @PreDestroy
    private void preDestroy() {
        final String methodName = "preDestroy";
        Logfile.WriteCalled(Level.INFO, STR_ClassName, methodName);

        /*
         * Close the logfile
         */
        Logfile.CloseLogger();

        Logfile.WriteCompleted(Level.INFO, STR_ClassName, methodName);
    }
}
