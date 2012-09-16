/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labclient;

import java.io.Serializable;
import java.io.Writer;
import java.util.logging.Level;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labclient.Consts;
import uq.ilabs.library.labclient.ExperimentResult;
import uq.ilabs.library.labclient.engine.LabClientSession;

/**
 *
 * @author uqlpayne
 */
@ManagedBean
@SessionScoped
public class ResultsBean implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ResultsBean.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants
     */
    private static final String STR_ResultStatus_arg2 = "Experiment %d - %s";
    private static final String STR_CsvFilename_arg2 = "%s_%s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ExperimentId = "Experiment Id";
    private static final String STRERR_ValueNotSpecified_arg = "%s: Not specified!";
    private static final String STRERR_ValueNotValid_arg = "%s: Not valid!";
    private static final String STRERR_ExperimentFailed_arg3 = "Experiment %d - %s: %s";
    private static final String STRERR_NoResultsAvailable = "No experiment results available!";
    private static final String STRERR_WrongExperimentType_arg = "Wrong experiment type: %s";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabClientSession labClientSession;
    private String lastSelectedExperimentId;
    private int retrievedExperimentId;
    private String csvExperimentResults;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String hitExperimentId;
    private String hsomSelectedExperimentId;
    private boolean hsomExperimentIdsRendered;
    private boolean hcbSaveDisabled;
    private String holMessage;
    private String holMessageClass;
    private boolean hpgResultsRendered;
    private String hotResultsTableValue;
    private String[] experimentIds;

    public String getHitExperimentId() {
        return hitExperimentId;
    }

    public void setHitExperimentId(String hitExperimentId) {
        this.hitExperimentId = hitExperimentId;
    }

    public String[] getExperimentIds() {
        return experimentIds;
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
                hcbSaveDisabled = true;
            }
        }
    }

    public boolean isHsomExperimentIdsRendered() {
        return hsomExperimentIdsRendered;
    }

    public boolean isHcbSaveDisabled() {
        return hcbSaveDisabled;
    }

    public String getHolMessage() {
        return holMessage;
    }

    public String getHolMessageClass() {
        return holMessageClass;
    }

    public boolean isHpgResultsRendered() {
        return hpgResultsRendered;
    }

    public String getHotResultsTableValue() {
        return hotResultsTableValue;
    }
    //</editor-fold>

    /**
     * Creates a new instance of ResultsBean
     */
    public ResultsBean() {
        final String methodName = "ResultsBean";
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
            this.hcbSaveDisabled = true;
            this.hpgResultsRendered = false;
            this.PopulateCompletedIds();
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    public String actionRetrieve() {
        final String methodName = "actionRetrieve";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        this.hpgResultsRendered = false;
        this.hcbSaveDisabled = true;

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
             * Retrieve the experiment results
             */
            ResultReport resultReport = this.labClientSession.getServiceBrokerAPI().RetrieveResult(experimentId);
            if (resultReport == null) {
                throw new RuntimeException(String.format(STR_ResultStatus_arg2, experimentId, StatusCodes.Unknown));
            }

            /*
             * Get the experiment result status
             */
            StatusCodes statusCode = resultReport.getStatusCode();
            if (statusCode == StatusCodes.Unknown || statusCode == StatusCodes.Cancelled) {
                throw new RuntimeException(String.format(STR_ResultStatus_arg2, experimentId, statusCode));
            }

            if (statusCode == StatusCodes.Failed) {
                throw new RuntimeException(String.format(STRERR_ExperimentFailed_arg3, experimentId, statusCode, resultReport.getErrorMessage()));
            }

            /*
             * Get result information
             */
            if (resultReport.getXmlExperimentResults() == null) {
                throw new RuntimeException(STRERR_NoResultsAvailable);
            }

            ExperimentResult experimentResult = new ExperimentResult(resultReport.getXmlExperimentResults());
            if (experimentResult == null) {
                throw new NullPointerException(ExperimentResult.class.getSimpleName());
            }

            /*
             * Check for correct experiment type
             */
            if (experimentResult.getTitle().equalsIgnoreCase(this.labClientSession.getTitle()) == false) {
                throw new Exception(String.format(STRERR_WrongExperimentType_arg, experimentResult.getTitle()));
            }

            /*
             * Finally.... display the results on the web page
             */
            experimentResult.CreateHtmlResultInfo();
            this.hotResultsTableValue = experimentResult.GetHtmlResultInfo();
            this.hpgResultsRendered = true;

            /*
             * Build a CSV string from the result report and store in a hidden label
             */
            experimentResult.CreateCsvResultInfo();
            this.retrievedExperimentId = experimentResult.getExperimentId();
            this.csvExperimentResults = experimentResult.GetCsvResultInfo();
            this.hcbSaveDisabled = false;

            ShowMessageInfo(String.format(STR_ResultStatus_arg2, experimentId, statusCode));

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
    public String actionSave() {
        final String methodName = "actionSave";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Download the result string as an Excel csv file
             */
            String filename = String.format(STR_CsvFilename_arg2, this.labClientSession.getTitle(), this.retrievedExperimentId);
            String attachmentCsv = String.format(Consts.STRRSP_AttachmentCsv_arg, filename);
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.setResponseContentType(Consts.STRRSP_ContentTypeCsv);
            externalContext.setResponseHeader(Consts.STRRSP_Disposition, attachmentCsv);
            try (Writer writer = externalContext.getResponseOutputWriter()) {
                writer.write(this.csvExperimentResults);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     */
    private void PopulateCompletedIds() {
        final String methodName = "PopulateCompletedIds";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Initialise controls
         */
        this.hitExperimentId = null;
        this.hsomExperimentIdsRendered = false;

        /*
         * Get the completed experiment Ids
         */
        int[] completedIds = this.labClientSession.getCompletedIds();
        if (completedIds != null) {
            if (completedIds.length == 1) {
                /*
                 * Show the one that has been completed
                 */
                this.hitExperimentId = Integer.toString(completedIds[0]);
            } else if (completedIds.length > 1) {
                /*
                 * More than one has been completed, show them all
                 */
                this.experimentIds = new String[completedIds.length];
                for (int i = 0; i < completedIds.length; i++) {
                    this.experimentIds[i] = Integer.toString(completedIds[i]);
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
