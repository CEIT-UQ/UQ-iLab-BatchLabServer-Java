/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine;

import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.labserver.engine.types.ExperimentResultInfo;
import uq.ilabs.library.labserver.engine.types.LabExperimentInfo;

/**
 *
 * @author uqlpayne
 */
public class LabExperimentResult {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabExperimentResult.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants
     */
    private static final String STR_DateTimeFormat = "ddd dd MMM yyyy h:mm:ss tt";
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_ExperimentIdSbNameUnitIdSetupId_arg = " experimentId: %d  sbName: '%s'  unitId: %d  setupId: '%s'";
    private static final String STRLOG_Success_arg = "Success: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_XmlExperimentResult = "experimentResult";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Properties setupNames;
    private String xmlExperimentResult;
    //
    protected Node nodeExperimentResult;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    protected String title;
    protected String version;
    protected int queueId;
    protected int experimentId;
    protected String sbName;
    protected String setupId;
    protected String userGroup;
    protected int priorityHint;
    protected int unitId;
    protected Calendar timeCompleted;
    protected int executionTime;
    protected ResultReport resultReport;

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public int getQueueId() {
        return queueId;
    }

    public int getExperimentId() {
        return experimentId;
    }

    public String getSbName() {
        return sbName;
    }

    public String getSetupId() {
        return setupId;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public int getPriorityHint() {
        return priorityHint;
    }

    public int getUnitId() {
        return unitId;
    }

    public Calendar getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(Calendar timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    public ResultReport getResultReport() {
        return resultReport;
    }

    public void setResultReport(ResultReport resultReport) {
        this.resultReport = resultReport;
    }
    //</editor-fold>

    /**
     *
     * @param labConfiguration
     * @throws Exception
     */
    public LabExperimentResult(LabConfiguration labConfiguration) throws Exception {
        final String methodName = "LabExperimentResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that parameters are valid
             */
            if (labConfiguration == null) {
                throw new NullPointerException(LabConfiguration.class.getSimpleName());
            }
            if (labConfiguration.getXmlExperimentResult() == null) {
                throw new NullPointerException(STRERR_XmlExperimentResult);
            }
            if (labConfiguration.getXmlExperimentResult().trim().isEmpty()) {
                throw new IllegalArgumentException(STRERR_XmlExperimentResult);
            }

            /*
             * Load the experiment result XML document from the string
             */
            Document document = XmlUtilities.GetDocumentFromString(labConfiguration.getXmlExperimentResult());
            Node nodeRoot = XmlUtilities.GetRootNode(document, LabConsts.STRXML_ExperimentResult);

            /*
             * Check that all required XML nodes exist
             */
            XmlUtilities.GetChildNode(nodeRoot, LabConsts.STRXML_Title);
            XmlUtilities.GetChildNode(nodeRoot, LabConsts.STRXML_Version);
            XmlUtilities.GetChildNode(nodeRoot, LabConsts.STRXML_ExperimentId);
            XmlUtilities.GetChildNode(nodeRoot, LabConsts.STRXML_SbName);
            XmlUtilities.GetChildNode(nodeRoot, LabConsts.STRXML_UnitId);
            XmlUtilities.GetChildNode(nodeRoot, LabConsts.STRXML_SetupId);
            XmlUtilities.GetChildNode(nodeRoot, LabConsts.STRXML_SetupName);
            XmlUtilities.GetChildNode(nodeRoot, LabConsts.STRXML_Timestamp);

            /*
             * Save to local variables
             */
            this.title = labConfiguration.getTitle();
            this.version = labConfiguration.getVersion();
            this.setupNames = labConfiguration.getSetupNames();
            this.xmlExperimentResult = labConfiguration.getXmlExperimentResult();
            this.nodeExperimentResult = nodeRoot;

            /*
             * Initialise local variables
             */
            this.resultReport = new ResultReport(StatusCodes.Unknown);
            if (this.resultReport == null) {
                throw new NullPointerException(ResultReport.class.getSimpleName());
            }
        } catch (NullPointerException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param labExperimentInfo
     * @return
     */
    public boolean SetLabExperimentInfo(LabExperimentInfo labExperimentInfo) {
        final String methodName = "SetLabExperimentInfo";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Check that parameters are valid
             */
            if (labExperimentInfo == null) {
                throw new NullPointerException(LabExperimentInfo.class.getSimpleName());
            }

            /*
             * Set local variables
             */
            this.queueId = labExperimentInfo.getQueueId();
            this.experimentId = labExperimentInfo.getExperimentId();
            this.sbName = labExperimentInfo.getSbName();
            this.userGroup = labExperimentInfo.getUserGroup();
            this.priorityHint = labExperimentInfo.getPriorityHint();
            this.unitId = labExperimentInfo.getUnitId();
            this.setupId = labExperimentInfo.getSetupId();

            success = true;
        } catch (NullPointerException ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return success;
    }

    /**
     *
     * @return
     */
    public ExperimentResultInfo GetExperimentResultInfo() {
        final String methodName = "GetExperimentResultInfo";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ExperimentResultInfo experimentResultInfo = null;

        try {
            /*
             * Check that the result report exists
             */
            if (this.resultReport == null) {
                throw new NullPointerException(ResultReport.class.getSimpleName());
            }

            /*
             * Create an instance of ExperimentResultInfo
             */
            experimentResultInfo = new ExperimentResultInfo();
            if (experimentResultInfo == null) {
                throw new NullPointerException(ExperimentResultInfo.class.getSimpleName());
            }

            /*
             * Fill in the result information
             */
            experimentResultInfo.setExperimentId(this.experimentId);
            experimentResultInfo.setSbName(this.sbName);
            experimentResultInfo.setUserGroup(this.userGroup);
            experimentResultInfo.setPriorityHint(this.priorityHint);

            /*
             * Fill in the result report information
             */
            experimentResultInfo.setStatusCode(this.resultReport.getStatusCode());
            experimentResultInfo.setXmlResultExtension(this.resultReport.getXmlResultExtension());
            experimentResultInfo.setXmlBlobExtension(this.resultReport.getXmlBlobExtension());
            experimentResultInfo.setWarningMessages(this.resultReport.getWarningMessages());
            experimentResultInfo.setErrorMessage(this.resultReport.getErrorMessage());
            experimentResultInfo.setXmlExperimentResults(this.ToXmlString());

        } catch (NullPointerException ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return experimentResultInfo;
    }

    /**
     *
     * @return String
     */
    protected String ToXmlString() {
        final String methodName = "ToXmlString";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String xmlString;

        try {
            /*
             * Load the experiment results XML document from the string
             */
            Document document = XmlUtilities.GetDocumentFromString(this.xmlExperimentResult);
            this.nodeExperimentResult = XmlUtilities.GetRootNode(document, LabConsts.STRXML_ExperimentResult);

            /*
             * Add lab configuration information
             */
            XmlUtilities.SetChildValue(this.nodeExperimentResult, LabConsts.STRXML_Title, this.title);
            XmlUtilities.SetChildValue(this.nodeExperimentResult, LabConsts.STRXML_Version, this.version);

            /*
             * Add experiment information to the XML document
             */
            XmlUtilities.SetChildValue(this.nodeExperimentResult, LabConsts.STRXML_ExperimentId, this.experimentId);
            XmlUtilities.SetChildValue(this.nodeExperimentResult, LabConsts.STRXML_SbName, this.sbName);
            XmlUtilities.SetChildValue(this.nodeExperimentResult, LabConsts.STRXML_UnitId, this.unitId);
            XmlUtilities.SetChildValue(this.nodeExperimentResult, LabConsts.STRXML_SetupId, this.setupId);
            String setupName = this.setupNames.getProperty(this.setupId);
            if (setupName != null) {
                XmlUtilities.SetChildValue(this.nodeExperimentResult, LabConsts.STRXML_SetupName, setupName);
            }

            /*
             * Add result information to the XML document
             */
            if (this.timeCompleted != null) {
                XmlUtilities.SetChildValue(this.nodeExperimentResult, LabConsts.STRXML_Timestamp, this.timeCompleted.getTime().toString());
            }

            /*
             * Convert the XML document to an XML string
             */
            xmlString = XmlUtilities.ToXmlString(this.nodeExperimentResult);
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            xmlString = null;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return xmlString;
    }
}
