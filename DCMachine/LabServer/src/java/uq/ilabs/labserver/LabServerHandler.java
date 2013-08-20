/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver;

import java.util.logging.Level;
import uq.ilabs.library.lab.types.AuthHeader;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.SubmissionReport;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.database.types.ServiceBrokerInfo;
import uq.ilabs.library.labserver.engine.LabConsts;
import uq.ilabs.library.labserver.engine.LabManagement;

/**
 *
 * @author uqlpayne
 */
public class LabServerHandler {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabServerHandler.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_AuthHeaderNull = "AuthHeader: null";
    private static final String STRLOG_IdentifierPasskey_arg2 = "Identifier: '%s'  Passkey: '%s'";
    private static final String STRLOG_ExperimentId_arg = "ExperimentId: %d";
    private static final String STRLOG_UserGroupPriorityHint_arg2 = "UserGroup: %s  PriorityHint: %d";
    private static final String STRLOG_Success_arg = "Success: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_AccessDenied_arg = "LabServer Access Denied: %s";
    private static final String STRERR_AuthHeader = "AuthHeader";
    private static final String STRERR_ServiceBrokerGuid = "ServiceBroker Guid";
    private static final String STRERR_Passkey = "Passkey";
    private static final String STRERR_NotSpecified_arg = "%s: Not specified!";
    private static final String STRERR_Invalid_arg = "%s: Invalid!";
    private static final String STRERR_AccessNotPermitted = "Access is not permitted";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabServerAppBean labServerBean;
    //</editor-fold>

    /**
     *
     * @param labServerBean
     * @throws Exception
     */
    public LabServerHandler(LabServerAppBean labServerBean) throws Exception {
        final String methodName = "LabServerHandler";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        this.labServerBean = labServerBean;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
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
            success = this.labServerBean.getExperimentManager().Cancel(experimentId, sbName);

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
     * @return WaitEstimate
     */
    public WaitEstimate getEffectiveQueueLength(AuthHeader authHeader, String userGroup, int priorityHint) {
        final String methodName = "getEffectiveQueueLength";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_UserGroupPriorityHint_arg2, userGroup, priorityHint));

        WaitEstimate waitEstimate = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            waitEstimate = this.labServerBean.getExperimentManager().GetEffectiveQueueLength(userGroup, priorityHint);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return waitEstimate;
    }

    /**
     *
     * @param authHeader
     * @param experimentId
     * @return LabExperimentStatus
     */
    public LabExperimentStatus getExperimentStatus(AuthHeader authHeader, int experimentId) {
        final String methodName = "getExperimentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        LabExperimentStatus labExperimentStatus = null;

        String sbName = this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            labExperimentStatus = this.labServerBean.getExperimentManager().GetLabExperimentStatus(experimentId, sbName);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labExperimentStatus;
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
            labConfiguration = this.labServerBean.getExperimentManager().GetXmlLabConfiguration(userGroup);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

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
            labInfo = this.labServerBean.getExperimentManager().GetLabInfo();

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labInfo;
    }

    /**
     *
     * @param authHeader
     * @return LabStatus
     */
    public LabStatus getLabStatus(AuthHeader authHeader) {
        final String methodName = "getLabStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        LabStatus labStatus = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            labStatus = this.labServerBean.getExperimentManager().GetLabStatus();

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labStatus;
    }

    /**
     *
     * @param authHeader
     * @param experimentId
     * @return ResultReport
     */
    public ResultReport retrieveResult(AuthHeader authHeader, int experimentId) {
        final String methodName = "retrieveResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        ResultReport resultReport = null;

        String sbName = this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            resultReport = this.labServerBean.getExperimentManager().RetrieveResult(experimentId, sbName);

            /*
             * Convert to return type
             */

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return resultReport;
    }

    /**
     *
     * @param authHeader
     * @param experimentId
     * @param experimentSpecification
     * @param userGroup
     * @param priorityHint
     * @return SubmissionReport
     */
    public SubmissionReport submit(AuthHeader authHeader, int experimentId, String experimentSpecification, String userGroup, int priorityHint) {
        final String methodName = "submit";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        SubmissionReport submissionReport = null;

        String sbName = this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            submissionReport = this.labServerBean.getExperimentManager().Submit(experimentId, sbName, experimentSpecification, userGroup, priorityHint);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return submissionReport;
    }

    /**
     *
     * @param experimentSpecification
     * @param userGroup
     * @return ValidationReport
     */
    public ValidationReport validate(AuthHeader authHeader, String experimentSpecification, String userGroup) {
        final String methodName = "validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ValidationReport validationReport = null;

        this.Authenticate(authHeader);

        try {
            /*
             * Pass on to the experiment manager
             */
            validationReport = this.labServerBean.getExperimentManager().Validate(experimentSpecification, userGroup);

        } catch (Exception ex) {
            Logfile.WriteError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return validationReport;
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
        LabManagement labManagement = this.labServerBean.getLabManagement();
        if (labManagement.isLogAuthentication() == true) {
            if (authHeader == null) {
                Logfile.Write(STRLOG_AuthHeaderNull);
            } else {
                Logfile.Write(String.format(STRLOG_IdentifierPasskey_arg2, authHeader.getIdentifier(), authHeader.getPasskey()));
            }
        }

        try {
            /*
             * Check when authenticating that AuthHeader and ServiceBroker Guid are specified
             */
            if (authHeader == null) {
                if (labManagement.isAuthenticating() == true) {
                    throw new NullPointerException(String.format(STRERR_NotSpecified_arg, STRERR_AuthHeader));
                }

                /*
                 * Probably using WebService tester, set name to localhost
                 */
                return LabConsts.STR_SbNameLocalHost;
            }

            if (authHeader.getIdentifier() == null) {
                throw new NullPointerException(String.format(STRERR_NotSpecified_arg, STRERR_ServiceBrokerGuid));
            }

            /*
             * Check if the ServiceBrokerInfo for this ServiceBroker Guid exists
             */
            ServiceBrokerInfo serviceBrokerInfo = labManagement.getMapServiceBrokerInfo().get(authHeader.getIdentifier());
            if (serviceBrokerInfo == null) {
                throw new NullPointerException(String.format(STRERR_Invalid_arg, STRERR_ServiceBrokerGuid));
            }

            /*
             * Verify the passkey
             */
            if (authHeader.getPasskey() == null) {
                throw new NullPointerException(String.format(STRERR_NotSpecified_arg, STRERR_Passkey));
            }
            if (authHeader.getPasskey().equalsIgnoreCase(serviceBrokerInfo.getOutPasskey()) == false) {
                throw new IllegalArgumentException(String.format(STRERR_Invalid_arg, STRERR_Passkey));
            }

            /*
             * Check that the ServiceBroker is permitted access
             */
            if (serviceBrokerInfo.isPermitted() == false) {
                throw new RuntimeException(STRERR_AccessNotPermitted);
            }

            /*
             * Successfully authenticated
             */
            sbName = serviceBrokerInfo.getName();

        } catch (Exception ex) {
            String message = String.format(STRERR_AccessDenied_arg, ex.getMessage());
            Logfile.WriteError(message);
            throw new RuntimeException(message);
        }

        return sbName;
    }
}
