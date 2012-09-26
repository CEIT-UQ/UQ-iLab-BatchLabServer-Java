/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labclient;

import java.io.Serializable;
import java.util.logging.Level;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import uq.ilabs.library.lab.types.ExperimentStatus;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labclient.Consts;
import uq.ilabs.library.labclient.engine.LabClientSession;

/**
 *
 * @author uqlpayne
 */
@ManagedBean
@SessionScoped
public class StatusBean implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = StatusBean.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants
     */
    private static final String STR_ExperimentStatus_arg2 = "Experiment %d - %s";
    private static final String STR_MinutesAnd_arg2 = "%d minute%s and ";
    private static final String STR_Seconds_arg2 = "%d second%s";
    private static final String STR_StatusMessageQueueLengthWaitTime_arg3 = "%s - Queue length is %d and wait time is %s";
    private static final String STR_TimeRemainingIs = " Time remaining is %s";
    private static final String STR_QueuePositionRunIn_arg2 = "Queue position is %d and it will run in %s";
    private static final String STR_ExperimentCancelled_arg = "Experiment %d has been cancelled.";
    private static final String STR_ExperimentNotCancelled_arg = "Experiment %d could not be cancelled!";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ExperimentId = "Experiment Id";
    private static final String STRERR_ValueNotSpecified_arg = "%s: Not specified!";
    private static final String STRERR_ValueNotValid_arg = "%s: Not valid!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabClientSession labClientSession;
    private String lastSelectedExperimentId;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private boolean holOnline;
    private String holLabStatusMessage;
    private String hitExperimentId;
    private String hsomSelectedExperimentId;
    private boolean hsomExperimentIdsRendered;
    private String holMessage;
    private String holMessageClass;
    private String[] experimentIds;

    public boolean isHolOnline() {
        return holOnline;
    }

    public String getHolLabStatusMessage() {
        return holLabStatusMessage;
    }

    public String getHitExperimentId() {
        return hitExperimentId;
    }

    public void setHitExperimentId(String hitExperimentId) {
        this.hitExperimentId = hitExperimentId;
    }

    public String getHsomSelectedExperimentId() {
        return hsomSelectedExperimentId;
    }

    public void setHsomSelectedExperimentId(String hsomSelectedExperimentId) {
        this.hsomSelectedExperimentId = hsomSelectedExperimentId;
        if (hsomSelectedExperimentId != null) {
            if (hsomSelectedExperimentId.equals(lastSelectedExperimentId) == false) {
                hitExperimentId = hsomSelectedExperimentId;
                lastSelectedExperimentId = hsomSelectedExperimentId;
            }
        }
    }

    public boolean isHsomExperimentIdsRendered() {
        return hsomExperimentIdsRendered;
    }

    public String getHolMessage() {
        return holMessage;
    }

    public String getHolMessageClass() {
        return holMessageClass;
    }

    public String[] getExperimentIds() {
        return experimentIds;
    }
    //</editor-fold>

    /**
     * Creates a new instance of StatusBean
     */
    public StatusBean() {
        final String methodName = "StatusBean";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        this.labClientSession = (LabClientSession) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Consts.STRSSN_LabClient);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     */
    public void pageLoad() {
        final String methodName = "pageLoad";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        if (FacesContext.getCurrentInstance().isPostback() == false) {
            /*
             * Not a postback, initialise page controls
             */
            this.ShowMessageInfo(null);
            this.PopulateSubmittedIds();
        }

        /*
         * Refresh the LabServer status
         */
        this.Refresh();

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    public String actionRefresh() {
        final String methodName = "actionRefresh";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        this.Refresh();

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @return
     */
    public String actionCheck() {
        final String methodName = "actionCheck";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            int experimentId;

            /*
             * Get the experiment Id
             */
            if (this.hitExperimentId == null || this.hitExperimentId.trim().length() == 0) {
                throw new RuntimeException(String.format(STRERR_ValueNotSpecified_arg, STRERR_ExperimentId));
            }
            try {
                experimentId = Integer.parseInt(this.hitExperimentId);
            } catch (NumberFormatException ex) {
                throw new RuntimeException(String.format(STRERR_ValueNotValid_arg, STRERR_ExperimentId));
            }
            if (experimentId <= 0) {
                throw new RuntimeException(String.format(STRERR_ValueNotValid_arg, STRERR_ExperimentId));
            }

            /*
             * Get the experiment status
             */
            LabExperimentStatus labExperimentStatus = this.labClientSession.getServiceBrokerAPI().GetExperimentStatus(experimentId);
            if (labExperimentStatus == null) {
                throw new RuntimeException(String.format(STR_ExperimentStatus_arg2, experimentId, StatusCodes.Unknown));
            }
            ExperimentStatus experimentStatus = labExperimentStatus.getExperimentStatus();
            if (experimentStatus == null) {
                throw new RuntimeException(String.format(STR_ExperimentStatus_arg2, experimentId, StatusCodes.Unknown));
            }

            /*
             * Get the status code
             */
            StatusCodes statusCode = experimentStatus.getStatusCode();
            if (statusCode == StatusCodes.Unknown) {
                throw new RuntimeException(String.format(STR_ExperimentStatus_arg2, experimentId, StatusCodes.Unknown));
            }

            if (statusCode == StatusCodes.Running) {
                /*
                 * Experiment is currently running, display time remaining
                 */
                int seconds = (int) labExperimentStatus.getExperimentStatus().getEstRemainingRuntime();
                ShowMessageInfo(String.format(STR_TimeRemainingIs, FormatTimeMessage(seconds)));
            } else if (statusCode == StatusCodes.Waiting) {
                /*
                 * Experiment is waiting to run, get queue position (zero-based)
                 */
                int position = labExperimentStatus.getExperimentStatus().getWaitEstimate().getEffectiveQueueLength();
                int seconds = (int) labExperimentStatus.getExperimentStatus().getWaitEstimate().getEstWait();
                seconds = (seconds < 0) ? 0 : seconds;
                ShowMessageInfo(String.format(STR_QueuePositionRunIn_arg2, position, FormatTimeMessage(seconds)));
            } else if (statusCode == StatusCodes.Completed || statusCode == StatusCodes.Failed || statusCode == StatusCodes.Cancelled) {
                /*
                 * Experiment status no longer needs to be checked
                 */
                this.labClientSession.DeleteSubmittedId(experimentId);
                this.labClientSession.AddCompletedId(experimentId);
                PopulateSubmittedIds();
                ShowMessageInfo(String.format(STR_ExperimentStatus_arg2, experimentId, statusCode));
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            ShowMessageError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @return
     */
    public String actionCancel() {
        final String methodName = "actionCancel";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            int experimentId;

            /*
             * Get the experiment Id
             */
            if (this.hitExperimentId == null || this.hitExperimentId.trim().length() == 0) {
                throw new RuntimeException(String.format(STRERR_ValueNotSpecified_arg, STRERR_ExperimentId));
            }
            try {
                experimentId = Integer.parseInt(this.hitExperimentId);
            } catch (NumberFormatException ex) {
                throw new RuntimeException(String.format(STRERR_ValueNotValid_arg, STRERR_ExperimentId));
            }
            if (experimentId <= 0) {
                throw new RuntimeException(String.format(STRERR_ValueNotValid_arg, STRERR_ExperimentId));
            }

            /*
             * Attempt to cancel the experiment
             */
            boolean cancelled = this.labClientSession.getServiceBrokerAPI().Cancel(experimentId);
            if (cancelled == false) {
                throw new RuntimeException(String.format(STR_ExperimentNotCancelled_arg, experimentId));
            }

            ShowMessageInfo(String.format(STR_ExperimentCancelled_arg, experimentId));

        } catch (Exception ex) {
            Logfile.Write(logLevel, ex.toString());
            ShowMessageError(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     */
    private void PopulateSubmittedIds() {
        final String methodName = "PopulateSubmittedIds";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Initialise controls
         */
        this.hitExperimentId = null;
        this.hsomExperimentIdsRendered = false;

        /*
         * Get the submitted experiment Ids
         */
        int[] submittedIds = this.labClientSession.getSubmittedIds();
        if (submittedIds != null) {
            if (submittedIds.length == 1) {
                /*
                 * Show the one that has been submitted
                 */
                this.hitExperimentId = Integer.toString(submittedIds[0]);
            } else if (submittedIds.length > 1) {
                /*
                 * More than one has been submitted, show them all
                 */
                this.experimentIds = new String[submittedIds.length];
                for (int i = 0; i < submittedIds.length; i++) {
                    this.experimentIds[i] = Integer.toString(submittedIds[i]);
                }
                this.hsomSelectedExperimentId = this.experimentIds[0];
                this.lastSelectedExperimentId = this.hsomSelectedExperimentId;
                this.hitExperimentId = this.hsomSelectedExperimentId;
                this.hsomExperimentIdsRendered = true;
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     */
    private void Refresh() {
        final String methodName = "Refresh";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Get the LabServer's status
             */
            LabStatus labStatus = this.labClientSession.getServiceBrokerAPI().GetLabStatus();
            this.holOnline = labStatus.isOnline();
            if (this.holOnline == true) {
                /*
                 * Get the queue length and wait time
                 */
                WaitEstimate waitEstimate = this.labClientSession.getServiceBrokerAPI().GetEffectiveQueueLength();
                this.holLabStatusMessage = String.format(STR_StatusMessageQueueLengthWaitTime_arg3,
                        labStatus.getLabStatusMessage(), waitEstimate.getEffectiveQueueLength(), FormatTimeMessage((int) waitEstimate.getEstWait()));
            } else {
                /*
                 * Display lab status and message
                 */
                this.holLabStatusMessage = labStatus.getLabStatusMessage();
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param seconds
     * @return
     */
    private String FormatTimeMessage(int seconds) {
        /*
         * Convert to minutes and seconds
         */
        int minutes = seconds / 60;
        seconds -= minutes * 60;

        String message = "";
        try {
            if (minutes > 0) {
                /*
                 * Display minutes
                 */
                message += String.format(STR_MinutesAnd_arg2, minutes, FormatPlural(minutes));
            }
            /*
             * Display seconds
             */
            message += String.format(STR_Seconds_arg2, seconds, FormatPlural(seconds));
        } catch (Exception ex) {
            message = ex.getMessage();
            Logfile.WriteError(ex.toString());
        }

        return message;
    }

    /**
     *
     * @param value
     * @return
     */
    private String FormatPlural(int value) {
        return (value == 1) ? "" : "s";
    }

    /**
     *
     * @param message
     */
    private void ShowMessageInfo(String message) {
        this.holMessage = message;
        this.holMessageClass = Consts.STRSTL_InfoMessage;
    }

    /**
     *
     * @param message
     */
    private void ShowMessageError(String message) {
        this.holMessage = message;
        this.holMessageClass = Consts.STRSTL_ErrorMessage;
    }
}
