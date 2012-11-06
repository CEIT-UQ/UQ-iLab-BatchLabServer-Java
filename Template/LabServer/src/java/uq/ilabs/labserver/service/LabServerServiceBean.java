/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.service;

import edu.mit.ilab.ArrayOfString;
import java.util.Arrays;
import java.util.logging.Level;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import uq.ilabs.library.lab.types.*;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.Configuration;
import uq.ilabs.library.labserver.ExperimentManager;
import uq.ilabs.library.labserver.engine.ConfigProperties;
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
    private static final String STRLOG_ExperimentIdSbName_arg2 = "ExperimentId: %d  SbName: %s";
    private static final String STRLOG_UserGroupPriorityHint_arg2 = "UserGroup: %s  PriorityHint: %d";
    private static final String STRLOG_Success_arg = "Success: %s";
    private static final String STRLOG_IdentifierPasskey_arg2 = "Identifier: '%s'  Passkey: '%s'";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ExperimentManagerCreateFailed = "ExperimentManager.Create() Failed!";
    private static final String STRERR_ExperimentManagerStartFailed = "ExperimentManager.Start() Failed!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private ConfigProperties configProperties;
    private ExperimentManager experimentManager;
    //</editor-fold>

    /**
     * Constructor - Seems that this gets called when the project is deployed which is unexpected. To get around this,
     * check to see if the LabServer has been initialised and the configuration properties set. Can't do logging until
     * the ServiceBroker has been initialised and the logger created.
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

                /*
                 * Set the ServiceBrokers for LabServer service authentication access
                 */
                LabServerService.setServiceBrokers(labManagement.getServiceBrokers());

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
     * @param experimentId
     * @return
     */
    public boolean cancel(int experimentId, String sbName) {
        final String STR_MethodName = "cancel";
        Logfile.WriteCalled(logLevel, STR_ClassName, STR_MethodName,
                String.format(STRLOG_ExperimentIdSbName_arg2, experimentId, sbName));

        boolean success = this.experimentManager.Cancel(experimentId, sbName);

        Logfile.WriteCompleted(logLevel, STR_ClassName, STR_MethodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @param userGroup
     * @param priorityHint
     * @return
     */
    public edu.mit.ilab.WaitEstimate getEffectiveQueueLength(String userGroup, int priorityHint) {
        final String STR_MethodName = "getEffectiveQueueLength";
        Logfile.WriteCalled(logLevel, STR_ClassName, STR_MethodName,
                String.format(STRLOG_UserGroupPriorityHint_arg2, userGroup, priorityHint));

        edu.mit.ilab.WaitEstimate proxyWaitEstimate = null;

        /*
         * Pass on to the experiment manager
         */
        WaitEstimate waitEstimate = this.experimentManager.GetEffectiveQueueLength(userGroup, priorityHint);

        /*
         * Convert to return type
         */
        if (waitEstimate != null) {
            proxyWaitEstimate = new edu.mit.ilab.WaitEstimate();
            proxyWaitEstimate.setEffectiveQueueLength(waitEstimate.getEffectiveQueueLength());
            proxyWaitEstimate.setEstWait(waitEstimate.getEstWait());
        }

        Logfile.WriteCompleted(STR_ClassName, STR_MethodName);

        return proxyWaitEstimate;
    }

    /**
     *
     * @param experimentID
     * @return
     */
    public edu.mit.ilab.LabExperimentStatus getExperimentStatus(int experimentId, String sbName) {
        final String STR_MethodName = "getExperimentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, STR_MethodName,
                String.format(STRLOG_ExperimentIdSbName_arg2, experimentId, sbName));

        edu.mit.ilab.LabExperimentStatus proxyLabExperimentStatus = null;

        /*
         * Pass on to the experiment manager
         */
        LabExperimentStatus labExperimentStatus = this.experimentManager.GetLabExperimentStatus(experimentId, sbName);

        /*
         * Convert to return type
         */
        if (labExperimentStatus != null) {
            proxyLabExperimentStatus = new edu.mit.ilab.LabExperimentStatus();
            proxyLabExperimentStatus.setMinTimetoLive(labExperimentStatus.getMinTimetoLive());

            ExperimentStatus experimentStatus = labExperimentStatus.getExperimentStatus();
            if (experimentStatus != null) {
                edu.mit.ilab.ExperimentStatus proxyExperimentStatus = new edu.mit.ilab.ExperimentStatus();
                proxyExperimentStatus.setEstRemainingRuntime(experimentStatus.getEstRemainingRuntime());
                proxyExperimentStatus.setEstRuntime(experimentStatus.getEstRuntime());
                proxyExperimentStatus.setStatusCode(experimentStatus.getStatusCode().ordinal());

                WaitEstimate waitEstimate = experimentStatus.getWaitEstimate();
                if (waitEstimate != null) {
                    edu.mit.ilab.WaitEstimate proxyWaitEstimate = new edu.mit.ilab.WaitEstimate();
                    proxyWaitEstimate.setEffectiveQueueLength(waitEstimate.getEffectiveQueueLength());
                    proxyWaitEstimate.setEstWait(waitEstimate.getEstWait());
                    proxyExperimentStatus.setWait(proxyWaitEstimate);
                }
                proxyLabExperimentStatus.setStatusReport(proxyExperimentStatus);
            }
        }

        Logfile.WriteCompleted(STR_ClassName, STR_MethodName);

        return proxyLabExperimentStatus;
    }

    /**
     *
     * @param userGroup
     * @return String
     */
    public String getLabConfiguration(String userGroup) {
        final String STR_MethodName = "getLabConfiguration";
        Logfile.WriteCalled(logLevel, STR_ClassName, STR_MethodName);

        String labConfiguration;

        /*
         * Pass on to the experiment manager
         */
        labConfiguration = this.experimentManager.GetXmlLabConfiguration(userGroup);

        Logfile.WriteCompleted(STR_ClassName, STR_MethodName);

        return labConfiguration;
    }

    /**
     *
     * @return String
     */
    public String getLabInfo() throws Exception {
        final String STR_MethodName = "getLabInfo";
        Logfile.WriteCalled(logLevel, STR_ClassName, STR_MethodName);

        String labInfo;

        /*
         * Pass on to the experiment manager
         */
        labInfo = this.experimentManager.GetLabInfo();

        Logfile.WriteCompleted(STR_ClassName, STR_MethodName);

        return labInfo;
    }

    /**
     *
     * @return
     */
    public edu.mit.ilab.LabStatus getLabStatus() {
        final String STR_MethodName = "getLabStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, STR_MethodName);

        edu.mit.ilab.LabStatus proxyLabStatus = null;

        /*
         * Pass on to the experiment manager
         */
        LabStatus labStatus = this.experimentManager.GetLabStatus();

        /*
         * Convert to return type
         */
        if (labStatus != null) {
            proxyLabStatus = new edu.mit.ilab.LabStatus();
            proxyLabStatus.setLabStatusMessage(labStatus.getLabStatusMessage());
            proxyLabStatus.setOnline(labStatus.isOnline());
        }

        Logfile.WriteCompleted(STR_ClassName, STR_MethodName);

        return proxyLabStatus;
    }

    /**
     *
     * @param experimentId
     * @param sbName
     * @return
     */
    public edu.mit.ilab.ResultReport retrieveResult(int experimentId, String sbName) {
        final String STR_MethodName = "retrieveResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, STR_MethodName,
                String.format(STRLOG_ExperimentIdSbName_arg2, experimentId, sbName));

        edu.mit.ilab.ResultReport proxyResultReport = null;

        /*
         * Pass on to the experiment manager
         */
        ResultReport resultReport = this.experimentManager.RetrieveResult(experimentId, sbName);

        /*
         * Convert to return type
         */
        if (resultReport != null) {
            proxyResultReport = new edu.mit.ilab.ResultReport();
            proxyResultReport.setErrorMessage(resultReport.getErrorMessage());
            proxyResultReport.setExperimentResults(resultReport.getXmlExperimentResults());
            proxyResultReport.setStatusCode(resultReport.getStatusCode().ordinal());
            proxyResultReport.setXmlBlobExtension(resultReport.getXmlBlobExtension());
            proxyResultReport.setXmlResultExtension(resultReport.getXmlResultExtension());
            if (resultReport.getWarningMessages() != null) {
                ArrayOfString proxyWarningMessages = new ArrayOfString();
                proxyWarningMessages.getString().addAll(Arrays.asList(resultReport.getWarningMessages()));
                proxyResultReport.setWarningMessages(proxyWarningMessages);
            }
        }

        Logfile.WriteCompleted(STR_ClassName, STR_MethodName);

        return proxyResultReport;
    }

    /**
     *
     * @param experimentId
     * @param sbName
     * @param experimentSpecification
     * @param userGroup
     * @param priorityHint
     * @return
     */
    public edu.mit.ilab.SubmissionReport submit(int experimentId, String sbName, String experimentSpecification, String userGroup, int priorityHint) {
        final String STR_MethodName = "submit";
        Logfile.WriteCalled(logLevel, STR_ClassName, STR_MethodName);

        edu.mit.ilab.SubmissionReport proxySubmissionReport = null;

        /*
         * Pass on to the experiment manager
         */
        SubmissionReport submissionReport = this.experimentManager.Submit(experimentId, sbName, experimentSpecification, userGroup, priorityHint);

        /*
         * Convert to return type
         */
        if (submissionReport != null) {
            proxySubmissionReport = new edu.mit.ilab.SubmissionReport();
            proxySubmissionReport.setExperimentID(experimentId);
            proxySubmissionReport.setMinTimeToLive(submissionReport.getMinTimeToLive());

            ValidationReport validationReport = submissionReport.getValidationReport();
            if (validationReport != null) {
                edu.mit.ilab.ValidationReport proxyValidationReport = new edu.mit.ilab.ValidationReport();
                proxyValidationReport.setAccepted(validationReport.isAccepted());
                proxyValidationReport.setErrorMessage(validationReport.getErrorMessage());
                proxyValidationReport.setEstRuntime(validationReport.getEstRuntime());
                if (validationReport.getWarningMessages() != null) {
                    ArrayOfString proxyWarningMessages = new ArrayOfString();
                    proxyWarningMessages.getString().addAll(Arrays.asList(validationReport.getWarningMessages()));
                    proxyValidationReport.setWarningMessages(proxyWarningMessages);
                }
                proxySubmissionReport.setVReport(proxyValidationReport);

                WaitEstimate waitEstimate = submissionReport.getWaitEstimate();
                if (waitEstimate != null) {
                    edu.mit.ilab.WaitEstimate proxyWaitEstimate = new edu.mit.ilab.WaitEstimate();
                    proxyWaitEstimate.setEffectiveQueueLength(waitEstimate.getEffectiveQueueLength());
                    proxyWaitEstimate.setEstWait(waitEstimate.getEstWait());
                    proxySubmissionReport.setWait(proxyWaitEstimate);
                }
            }
        }

        Logfile.WriteCompleted(STR_ClassName, STR_MethodName);

        return proxySubmissionReport;
    }

    /**
     *
     * @param experimentSpecification
     * @param userGroup
     * @return
     */
    public edu.mit.ilab.ValidationReport validate(String experimentSpecification, String userGroup) {
        final String STR_MethodName = "validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, STR_MethodName);

        edu.mit.ilab.ValidationReport proxyValidationReport = null;

        /*
         * Pass on to the experiment manager
         */
        ValidationReport validationReport = this.experimentManager.Validate(experimentSpecification, userGroup);

        /*
         * Convert to return type
         */
        if (validationReport != null) {
            proxyValidationReport = new edu.mit.ilab.ValidationReport();
            proxyValidationReport.setAccepted(validationReport.isAccepted());
            proxyValidationReport.setErrorMessage(validationReport.getErrorMessage());
            proxyValidationReport.setEstRuntime(validationReport.getEstRuntime());

            if (validationReport.getWarningMessages() != null) {
                ArrayOfString proxyWarningMessages = new ArrayOfString();
                proxyWarningMessages.getString().addAll(Arrays.asList(validationReport.getWarningMessages()));
                proxyValidationReport.setWarningMessages(proxyWarningMessages);
            }
        }

        Logfile.WriteCompleted(STR_ClassName, STR_MethodName);

        return proxyValidationReport;
    }

    /**
     *
     * @param identifier
     * @param passkey
     * @return
     */
    public boolean Authenticate(String identifier, String passkey) {
        /*
         * Assume this will succeed
         */
        boolean success = true;

        /*
         * Check if authenticating
         */
        if (configProperties.isAuthenticating()) {
            if (configProperties.isLogAuthentication()) {
                Logfile.Write(String.format(STRLOG_IdentifierPasskey_arg2, identifier, passkey));
            }

            success = LabServerService.getServiceBrokers().Authenticate(identifier, passkey) != null;
        }

        return success;
    }

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
        Logfile.CloseLogger();

        Logfile.WriteCompleted(Level.INFO, STR_ClassName, methodName);
    }
}
