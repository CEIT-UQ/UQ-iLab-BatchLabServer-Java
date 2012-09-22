/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labclient.engine.LabExperimentResult;

/**
 *
 * @author uqlpayne
 */
public class ExperimentResult extends LabExperimentResult {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentResult.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants
     */
    private static final String STR_Source = "Source";
    private static final String STR_Absorber = "Absorber";
    private static final String STR_AbsorberList = "Absorber List";
    private static final String STR_Distance = "Distance (mm)";
    private static final String STR_DistanceList = "Distance List (mm)";
    private static final String STR_Duration = "Duration (secs)";
    private static final String STR_Trials = "Trials";
    private static final String STR_DataType = "Data Type";
    private static final String STR_CountsAtDistance = "Counts at Distance:";
    private static final String STR_CountsForAbsorber = "Counts for Absorber";
    private static final String STR_Millimetres = "mm";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private String source;
    private String[] absorberList;
    private int[] distanceList;
    private int duration;
    private int trials;
    private String dataType;
    private int[][] dataVectors;
    //</editor-fold>

    /**
     *
     * @param xmlExperimentResult
     */
    public ExperimentResult(String xmlExperimentResult) {
        super(xmlExperimentResult);

        final String methodName = "ExperimentResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Get specification information
             */
            this.source = XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_SourceName);
            this.duration = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_Duration);
            this.trials = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_Repeat);

            /*
             * Get the CSV list of absorbers into a string array
             */
            String csvString = XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_AbsorberName);
            this.absorberList = csvString.split(Consts.STR_CsvSplitter);

            /*
             * Get the CSV list of distances into an integer array
             */
            csvString = XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_Distance);
            String[] csvStringSplit = csvString.split(Consts.STR_CsvSplitter);
            this.distanceList = new int[csvStringSplit.length];
            for (int i = 0; i < this.distanceList.length; i++) {
                try {
                    this.distanceList[i] = Integer.parseInt(csvStringSplit[i]);
                } catch (Exception ex) {
                }
            }

            /*
             * Get result information
             */
            this.dataType = XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_DataType);

            /*
             * Get the radioactivity counts into a two dimensional array. Each data vector contains the trial counts for
             * a particular distance and is provided as a comma-seperated-value string.
             */
            String csvStrings[] = XmlUtilities.GetChildValues(this.nodeExperimentResult, Consts.STRXML_DataVector, false);
            this.dataVectors = new int[csvStrings.length][];
            for (int i = 0; i < this.dataVectors.length; i++) {
                csvStringSplit = csvStrings[i].split(Consts.STR_CsvSplitter);
                this.dataVectors[i] = new int[csvStringSplit.length];
                for (int j = 0; j < this.dataVectors[i].length; j++) {
                    try {
                        this.dataVectors[i][j] = Integer.parseInt(csvStringSplit[j]);
                    } catch (Exception ex) {
                    }
                }
            }
        } catch (XmlUtilitiesException ex) {
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void CreateHtmlResultInfo() throws IOException {
        super.CreateHtmlResultInfo();

        this.tblSpecification += this.CreateSpecificationInfo(STRTBL_Row_arg2);
        this.tblResults += this.CreateResultsInfo(STRTBL_Row_arg2);
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void CreateCsvResultInfo() throws IOException {
        super.CreateCsvResultInfo();

        this.csvSpecification += this.CreateSpecificationInfo(STRCSV_Format_arg2);
        this.csvResults += this.CreateResultsInfo(STRCSV_Format_arg2);
    }

    /**
     *
     * @param strFormat
     * @return
     */
    private String CreateSpecificationInfo(String strFormat) {
        /*
         * Experiment setup
         */
        StringWriter sw = new StringWriter();

        /*
         * Create a CSV string of absorbers
         */
        String csvAbsorbers = "";
        for (int i = 0; i < this.absorberList.length; i++) {
            csvAbsorbers += String.format("%s%s", (i > 0) ? Consts.STR_CsvSplitter : "", this.absorberList[i]);
        }

        /*
         * Create a CSV string of distances
         */
        String csvDistances = "";
        for (int i = 0; i < this.distanceList.length; i++) {
            csvDistances += String.format("%s%d", (i > 0) ? Consts.STR_CsvSplitter : "", this.distanceList[i]);
        }

        sw.write(String.format(strFormat, STR_Source, this.source));
        sw.write(String.format(strFormat, (this.absorberList.length > 1) ? STR_AbsorberList : STR_Absorber, csvAbsorbers));
        sw.write(String.format(strFormat, (this.distanceList.length > 1) ? STR_DistanceList : STR_Distance, csvDistances));
        sw.write(String.format(strFormat, STR_Duration, this.duration));
        sw.write(String.format(strFormat, STR_Trials, this.trials));

        return sw.toString();
    }

    /**
     *
     * @param strFormat
     * @return
     */
    private String CreateResultsInfo(String strFormat) {
        /*
         * Experiment results
         */
        StringWriter sw = new StringWriter();
        sw.write(String.format(strFormat, STR_DataType, this.dataType));
        switch (this.setupId) {
            case Consts.STRXML_SetupId_RadioactivityVsAbsorber:
                sw.write(String.format(strFormat, STR_CountsForAbsorber, ""));
                break;
            default:
                sw.write(String.format(strFormat, STR_CountsAtDistance, ""));
                break;
        }

        /*
         * Create a CSV string of radioactivity counts from the data vector
         */
        for (int i = 0; i < this.dataVectors.length; i++) {
            String csvString = "";
            for (int j = 0; j < this.dataVectors[i].length; j++) {
                csvString += String.format("%s%d", (j > 0) ? Consts.STR_CsvSplitter : "", this.dataVectors[i][j]);
            }
            switch (this.setupId) {
                case Consts.STRXML_SetupId_RadioactivityVsAbsorber:
                    sw.write(String.format(strFormat, this.absorberList[i], csvString));
                    break;
                default:
                    sw.write(String.format(strFormat, String.format("%d%s", this.distanceList[i], STR_Millimetres), csvString));
                    break;
            }
        }

        return sw.toString();
    }
}
