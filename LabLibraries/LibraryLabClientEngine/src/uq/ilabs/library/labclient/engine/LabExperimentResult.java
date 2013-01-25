/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient.engine;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;

/**
 *
 * @author uqlpayne
 */
public class LabExperimentResult {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabExperimentResult.class.getName();
    private static final Level logLevel = Level.FINEST;
    /*
     * String constants
     */
    private static final String STR_Timestamp = "Timestamp";
    private static final String STR_ExperimentId = "Experiment Id";
    private static final String STR_UnitId = "Unit Id";
    private static final String STR_setupName = "Setup Name";
    //
    private static final String STRTBL_ExperimentInformation = "Experiment Information";
    private static final String STRTBL_ExperimentSetup = "Experiment Setup";
    private static final String STRTBL_ExperimentResults = "Experiment Results";
    private static final String STRTBL_Begin = "<table id=\"results\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">";
    private static final String STRTBL_Header_arg = "<tr align=\"left\"><th colspan=\"3\"><nobr>%s</nobr></th></tr>";
    protected static final String STRTBL_Row_arg2 = "<tr><td class=\"label\">%s:</td><td class=\"dataright\">%s</td></tr>";
    protected static final String STRTBL_RowEmpty = "<tr><td colspan=\"3\">&nbsp;</td></tr>";
    private static final String STRTBL_End = "</table>";
    //
    private static final String STRCSV_ExperimentInformation = "---Experiment Information---";
    private static final String STRCSV_ExperimentSetup = "---Experiment Setup---";
    private static final String STRCSV_ExperimentResults = "---Experiment Results---";
    protected static final String STRCSV_NewLine = "\r\n";
    protected static final String STRCSV_Format_arg2 = "%s,%s" + STRCSV_NewLine;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    protected Node nodeExperimentResult;
    protected String tblInformation;
    protected String tblSpecification;
    protected String tblResults;
    protected String csvInformation;
    protected String csvSpecification;
    protected String csvResults;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    protected String timestamp;
    protected String title;
    protected String version;
    protected int experimentId;
    protected String sbName;
    protected int unitId;
    protected String setupId;
    protected String setupName;

    public String getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public int getExperimentId() {
        return experimentId;
    }

    public String getSbName() {
        return sbName;
    }

    public int getUnitId() {
        return unitId;
    }

    public String getSetupId() {
        return setupId;
    }

    public String getSetupName() {
        return setupName;
    }
    //</editor-fold>

    /**
     *
     * @param xmlExperimentResult
     */
    public LabExperimentResult(String xmlExperimentResult) throws Exception {
        final String methodName = "LabExperimentResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Load the lab configuration XML document from the string
             */
            Document document = XmlUtilities.GetDocumentFromString(xmlExperimentResult);
            this.nodeExperimentResult = XmlUtilities.GetRootNode(document, LabConsts.STRXML_ExperimentResult);

            /*
             * Parse the experiment result
             */
            this.timestamp = XmlUtilities.GetChildValue(nodeExperimentResult, LabConsts.STRXML_Timestamp);
            this.title = XmlUtilities.GetChildValue(nodeExperimentResult, LabConsts.STRXML_Title);
            this.version = XmlUtilities.GetChildValue(nodeExperimentResult, LabConsts.STRXML_Version);
            this.experimentId = XmlUtilities.GetChildValueAsInt(nodeExperimentResult, LabConsts.STRXML_ExperimentId);
            this.unitId = XmlUtilities.GetChildValueAsInt(nodeExperimentResult, LabConsts.STRXML_UnitId);
            this.setupId = XmlUtilities.GetChildValue(nodeExperimentResult, LabConsts.STRXML_SetupId);
            this.setupName = XmlUtilities.GetChildValue(nodeExperimentResult, LabConsts.STRXML_SetupName);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @throws IOException
     */
    public void CreateHtmlResultInfo() throws IOException {
        /*
         * Experiment information
         */
        StringWriter sw = new StringWriter();
        sw.write(String.format(STRTBL_Header_arg, STRTBL_ExperimentInformation));
        sw.write(String.format(STRTBL_Row_arg2, STR_Timestamp, this.timestamp));
        sw.write(String.format(STRTBL_Row_arg2, STR_ExperimentId, this.experimentId));
        sw.write(String.format(STRTBL_Row_arg2, STR_UnitId, this.unitId));
        tblInformation = sw.toString();

        /*
         * Experiment setup
         */
        sw = new StringWriter();
        sw.write(String.format(STRTBL_Header_arg, STRTBL_ExperimentSetup));
        sw.write(String.format(STRTBL_Row_arg2, STR_setupName, this.setupName));
        tblSpecification = sw.toString();

        /*
         * Experiment results
         */
        sw = new StringWriter();
        sw.write(String.format(STRTBL_Header_arg, STRTBL_ExperimentResults));
        tblResults = sw.toString();
    }

    /**
     *
     * @return
     */
    public String GetHtmlResultInfo() {
        return STRTBL_Begin + tblInformation + tblSpecification + tblResults + STRTBL_End;
    }

    /**
     *
     * @throws IOException
     */
    public void CreateCsvResultInfo() throws IOException {
        /*
         * Experiment information
         */
        StringWriter sw = new StringWriter();
        sw.write(STRCSV_NewLine);
        sw.write(STRCSV_ExperimentInformation + STRCSV_NewLine);
        sw.write(String.format(STRCSV_Format_arg2, STR_Timestamp, this.timestamp));
        sw.write(String.format(STRCSV_Format_arg2, STR_ExperimentId, this.experimentId));
        sw.write(String.format(STRCSV_Format_arg2, STR_UnitId, this.unitId));
        csvInformation = sw.toString();

        /*
         * Experiment setup
         */
        sw = new StringWriter();
        sw.write(STRCSV_NewLine);
        sw.write(STRCSV_ExperimentSetup + STRCSV_NewLine);
        sw.write(String.format(STRCSV_Format_arg2, STR_setupName, this.setupName));
        csvSpecification = sw.toString();

        /*
         * Experiment results
         */
        sw = new StringWriter();
        sw.write(STRCSV_NewLine);
        sw.write(STRCSV_ExperimentResults + STRCSV_NewLine);
        csvResults = sw.toString();
    }

    /**
     *
     * @return
     */
    public String GetCsvResultInfo() {
        return csvInformation + csvSpecification + csvResults;
    }
}
