/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.service;

import edu.mit.ilab.ArrayOfString;
import edu.mit.ilab.AuthHeader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.soap.SOAPFaultException;
import uq.ilabs.library.lab.types.*;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.Configuration;
import uq.ilabs.library.labserver.ExperimentManager;
import uq.ilabs.library.labserver.database.ServiceBrokersDB;
import uq.ilabs.library.labserver.database.types.ServiceBrokerInfo;
import uq.ilabs.library.labserver.engine.ConfigProperties;
import uq.ilabs.library.labserver.engine.LabConsts;
import uq.ilabs.library.labserver.engine.LabManagement;

/**
 *
 * @author uqlpayne
 */
@Singleton
@LocalBean
public class LabServerServiceBean {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabServerServiceBean.class.getName();
    private static final Level logLevel = Level.INFO;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_AuthHeaderNull = "AuthHeader: null";
    private static final String STRLOG_IdentifierPasskey_arg2 = "Identifier: '%s'  Passkey: '%s'";
    private static final String STRLOG_ExperimentId_arg = "ExperimentId: %d";
    private static final String STRLOG_UserGroupPriorityHint_arg2 = "UserGroup: %s  PriorityHint: %d";
    private static final String STRLOG_Success_arg = "Success: %s";
    private static final String STRLOG_ClosingLogger_arg = "%s: Closing logger.";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ExperimentManagerCreateFailed = "ExperimentManager.Create() Failed!";
    private static final String STRERR_ExperimentManagerStartFailed = "ExperimentManager.Start() Failed!";
    private static final String STRERR_AccessDenied_arg = "LabServer Access Denied: %s";
    private static final String STRERR_AuthHeader = "AuthHeader";
    private static final String STRERR_ServiceBrokerGuid = "ServiceBroker Guid";
    private static final String STRERR_Passkey = "Passkey";
    private static final String STRERR_NotSpecified_arg = "%s: Not specified!";
    private static final String STRERR_Invalid_arg = "%s: Invalid!";
    private static final String STRERR_AccessNotPermitted = "Access is not permitted";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private ConfigProperties configProperties;
    private ExperimentManager experimentManager;
    private ServiceBrokersDB serviceBrokersDB;
    //</editor-fold>

    /**
     * Constructor - Seems that this gets called when the project is deployed which is unexpected. To get around this,
     * check to see if the LabServer has been initialised and the configuration properties set. Can't do logging until
     * the LabServer has been initialised and the logger created.
     */
    public LabServerServiceBean() {
        final String methodName = "LabServerServiceBean";

        /*
         * Check if initialisation needs to be done
         */
        if (LabServerService.isInitialised() == true && this.configProperties == null) {
            Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

            /*
             * Save the configuration properties locally and process
             */
            this.configProperties = LabServerService.getConfigProperties();

            try {
                /*
                 * Create an instance of the LabManagement
                 */
                Configuration configuration = new Configuration(null, this.configProperties.getXmlLabConfigurationPath());
                LabManagement labManagement = new LabManagement(this.configProperties, configuration);
                LabServerService.setLabManagement(labManagement);

                this.serviceBrokersDB = labManagement.getServiceBrokersDB();

                /*
                 * Create an instance of the the experiment manager
                 */
                this.experimentManager = new ExperimentManager(labManagement);
                if (this.experimentManager == null) {
                    throw new NullPointerException(ExperimentManager.class.getSimpleName());
                }
                if (this.experimentManager.Create() == false) {
                    throw new RuntimeException(STRERR_ExperimentManagerCreateFailed);
                }

                /*
                 * Start the ExperimentManager
                 */
                if (this.experimentManager.Start() == false) {
                    throw new RuntimeException(STRERR_ExperimentManagerStartFailed);
                }
            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
            }

            Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
        }
    }

    /**
     *
     * @param authHeader
     * @param experimentId
     * @return boolean
     */
    public boolean cancel(AuthHeader authHeader, int experimentId) {
        final String methodName = "cancel";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        boolean success = false;

        String sbName = this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            success = this.experimentManager.Cancel(experimentId, sbName);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @param authHeader
     * @param userGroup
     * @param priorityHint
     * @return edu.mit.ilab.WaitEstimate
     */
    public edu.mit.ilab.WaitEstimate getEffectiveQueueLength(AuthHeader authHeader, String userGroup, int priorityHint) {
        final String methodName = "getEffectiveQueueLength";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_UserGroupPriorityHint_arg2, userGroup, priorityHint));

        edu.mit.ilab.WaitEstimate proxyWaitEstimate = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            WaitEstimate waitEstimate = this.experimentManager.GetEffectiveQueueLength(userGroup, priorityHint);
            proxyWaitEstimate = this.ConvertType(waitEstimate);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return proxyWaitEstimate;
    }

    /**
     *
     * @param authHeader
     * @param experimentId
     * @return edu.mit.ilab.LabExperimentStatus
     */
    public edu.mit.ilab.LabExperimentStatus getExperimentStatus(AuthHeader authHeader, int experimentId) {
        final String methodName = "getExperimentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        edu.mit.ilab.LabExperimentStatus proxyLabExperimentStatus = null;

        String sbName = this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            LabExperimentStatus labExperimentStatus = this.experimentManager.GetLabExperimentStatus(experimentId, sbName);
            proxyLabExperimentStatus = this.ConvertType(labExperimentStatus);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return proxyLabExperimentStatus;
    }

    /**
     *
     * @param authHeader
     * @param userGroup
     * @return String
     */
    public String getLabConfiguration(AuthHeader authHeader, String userGroup) {
        final String methodName = "getLabConfiguration";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String labConfiguration = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            labConfiguration = this.experimentManager.GetXmlLabConfiguration(userGroup);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return labConfiguration;
    }

    /**
     *
     * @param authHeader
     * @return String
     */
    public String getLabInfo(AuthHeader authHeader) {
        final String methodName = "getLabInfo";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String labInfo = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            labInfo = this.experimentManager.GetLabInfo();

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return labInfo;
    }

    /**
     *
     * @param authHeader
     * @return edu.mit.ilab.LabStatus
     */
    public edu.mit.ilab.LabStatus getLabStatus(AuthHeader authHeader) {
        final String methodName = "getLabStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        edu.mit.ilab.LabStatus proxyLabStatus = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            LabStatus labStatus = this.experimentManager.GetLabStatus();
            proxyLabStatus = this.ConvertType(labStatus);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return proxyLabStatus;
    }

    /**
     *
     * @param authHeader
     * @param experimentId
     * @return edu.mit.ilab.ResultReport
     */
    public edu.mit.ilab.ResultReport retrieveResult(AuthHeader authHeader, int experimentId) {
        final String methodName = "retrieveResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        edu.mit.ilab.ResultReport proxyResultReport = null;

        String sbName = this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            ResultReport resultReport = this.experimentManager.RetrieveResult(experimentId, sbName);
            proxyResultReport = this.ConvertType(resultReport);

            /*
             * Convert to return type
             */

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return proxyResultReport;
    }

    /**
     *
     * @param authHeader
     * @param experimentId
     * @param experimentSpecification
     * @param userGroup
     * @param priorityHint
     * @return edu.mit.ilab.SubmissionReport
     */
    public edu.mit.ilab.SubmissionReport submit(AuthHeader authHeader, int experimentId, String experimentSpecification, String userGroup, int priorityHint) {
        final String methodName = "submit";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        edu.mit.ilab.SubmissionReport proxySubmissionReport = null;

        String sbName = this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            SubmissionReport submissionReport = this.experimentManager.Submit(experimentId, sbName, experimentSpecification, userGroup, priorityHint);
            proxySubmissionReport = this.ConvertType(submissionReport);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return proxySubmissionReport;
    }

    /**
     *
     * @param experimentSpecification
     * @param userGroup
     * @return edu.mit.ilab.ValidationReport
     */
    public edu.mit.ilab.ValidationReport validate(AuthHeader authHeader, String experimentSpecification, String userGroup) {
        final String methodName = "validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        edu.mit.ilab.ValidationReport proxyValidationReport = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            ValidationReport validationReport = this.experimentManager.Validate(experimentSpecification, userGroup);
            proxyValidationReport = this.ConvertType(validationReport);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return proxyValidationReport;
    }

    //================================================================================================================//
    /**
     *
     * @param authHeader
     * @return String
     */
    private String Authenticate(AuthHeader authHeader) {

        String sbName = null;

        /*
         * Check if logging authentication information
         */
        if (this.configProperties.isLogAuthentication() == true) {
            if (authHeader == null) {
                Logfile.Write(STRLOG_AuthHeaderNull);
            } else {
                Logfile.Write(String.format(STRLOG_IdentifierPasskey_arg2, authHeader.getIdentifier(), authHeader.getPassKey()));
            }
        }

        try {
            /*
             * Check when authenticating that AuthHeader and ServiceBroker Guid are specified
             */
            if (authHeader == null) {
                if (this.configProperties.isAuthenticating() == true) {
                    throw new ProtocolException(String.format(STRERR_NotSpecified_arg, STRERR_AuthHeader));
                }

                /*
                 * Probably using WebService tester, set name to localhost
                 */
                return LabConsts.STR_SbNameLocalHost;
            }

            if (authHeader.getIdentifier() == null) {
                throw new ProtocolException(String.format(STRERR_NotSpecified_arg, STRERR_ServiceBrokerGuid));
            }

            /*
             * Check if the ServiceBrokerInfo cache exists
             */
            HashMap<String, ServiceBrokerInfo> mapServiceBrokerInfo = LabServerService.getMapServiceBrokerInfo();
            if (mapServiceBrokerInfo == null) {
                /*
                 * Create the cache and populate
                 */
                mapServiceBrokerInfo = new HashMap<>();
                ArrayList serviceBrokerInfoList = this.serviceBrokersDB.RetrieveAll();
                Iterator iterator = serviceBrokerInfoList.iterator();
                while (iterator.hasNext()) {
                    ServiceBrokerInfo serviceBrokerInfo = (ServiceBrokerInfo) iterator.next();
                    mapServiceBrokerInfo.put(serviceBrokerInfo.getGuid(), serviceBrokerInfo);
                }
                LabServerService.setMapServiceBrokerInfo(mapServiceBrokerInfo);
            }

            /*
             * Check if the ServiceBrokerInfo for this ServiceBroker Guid exists
             */
            ServiceBrokerInfo serviceBrokerInfo = mapServiceBrokerInfo.get(authHeader.getIdentifier());
            if (serviceBrokerInfo == null) {
                throw new ProtocolException(String.format(STRERR_Invalid_arg, STRERR_ServiceBrokerGuid));
            }

            /*
             * Verify the passkey
             */
            if (authHeader.getPassKey() == null) {
                throw new ProtocolException(String.format(STRERR_NotSpecified_arg, STRERR_Passkey));
            }
            if (authHeader.getPassKey().equalsIgnoreCase(serviceBrokerInfo.getOutPasskey()) == false) {
                throw new ProtocolException(String.format(STRERR_Invalid_arg, STRERR_Passkey));
            }

            /*
             * Check that the ServiceBroker is permitted access
             */
            if (serviceBrokerInfo.isPermitted() == false) {
                throw new ProtocolException(STRERR_AccessNotPermitted);
            }

            /*
             * Successfully authenticated
             */
            sbName = serviceBrokerInfo.getName();

        } catch (ProtocolException ex) {
            String message = String.format(STRERR_AccessDenied_arg, ex.getMessage());
            Logfile.WriteError(message);

            /*
             * Create a SOAPFaultException to be thrown all the way back to the caller
             */
            try {
                SOAPFault fault = SOAPFactory.newInstance().createFault();
                fault.setFaultString(message);
                throw new SOAPFaultException(fault);
            } catch (SOAPException e) {
            }
        }

        return sbName;
    }

    //<editor-fold defaultstate="collapsed" desc="ConvertType">
    /**
     *
     * @param strings
     * @return ArrayOfString
     */
    private ArrayOfString ConvertType(String[] strings) {
        ArrayOfString arrayOfString = null;

        if (strings != null) {
            arrayOfString = new ArrayOfString();
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
            proxyLabStatus.setLabStatusMessage(labStatus.getLabStatusMessage());
            proxyLabStatus.setOnline(labStatus.isOnline());
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
            proxyResultReport.setXmlBlobExtension(resultReport.getXmlBlobExtension());
            proxyResultReport.setXmlResultExtension(resultReport.getXmlResultExtension());
            proxyResultReport.setWarningMessages(this.ConvertType(resultReport.getWarningMessages()));
        }

        return proxyResultReport;
    }

    /**
     *
     * @param submissionReport
     * @return edu.mit.ilab.SubmissionReport
     */
    private edu.mit.ilab.SubmissionReport ConvertType(SubmissionReport submissionReport) {
        edu.mit.ilab.SubmissionReport proxySubmissionReport = null;

        if (submissionReport != null) {
            proxySubmissionReport = new edu.mit.ilab.SubmissionReport();
            proxySubmissionReport.setExperimentID(submissionReport.getExperimentId());
            proxySubmissionReport.setMinTimeToLive(submissionReport.getMinTimeToLive());
            proxySubmissionReport.setVReport(this.ConvertType(submissionReport.getValidationReport()));
            proxySubmissionReport.setWait(this.ConvertType(submissionReport.getWaitEstimate()));
        }

        return proxySubmissionReport;
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
         * Close the LabExperiment Manager
         */
        if (this.experimentManager != null) {
            this.experimentManager.Close();
        }

        /*
         * Close the logfile
         */
        Logfile.Write(String.format(STRLOG_ClosingLogger_arg, STR_ClassName));
        Logfile.CloseLogger();

        Logfile.WriteCompleted(Level.INFO, STR_ClassName, methodName);
    }
}
