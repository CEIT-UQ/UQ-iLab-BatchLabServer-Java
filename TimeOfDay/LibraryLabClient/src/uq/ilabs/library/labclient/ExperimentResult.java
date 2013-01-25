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
    public static final String STR_ServerUrl = "Server Url";
    public static final String STR_TimeFormat = "TimeFormat";
    public static final String STR_TimeOfDay = "TimeOfDay";
    public static final String STR_DayOfWeek = "DayOfWeek";
    public static final String STR_Day = "Day";
    public static final String STR_Month = "Month";
    public static final String STR_Year = "Year";
    public static final String STR_Hours = "Hours";
    public static final String STR_Minutes = "Minutes";
    public static final String STR_Seconds = "Seconds";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private String serverUrl;
    private String timeFormat;
    private String timeofday;
    private String dayofweek;
    private int day;
    private int month;
    private int year;
    private int hours;
    private int minutes;
    private int seconds;
    //</editor-fold>

    /**
     *
     * @param xmlExperimentResult
     */
    public ExperimentResult(String xmlExperimentResult) throws Exception {
        super(xmlExperimentResult);

        final String methodName = "ExperimentResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Get specification information
             */
            this.serverUrl = XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_ServerUrl);
            this.timeFormat = XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_FormatName);
            /*
             * Get result information
             */
            this.timeofday = XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_Timeofday);
            this.dayofweek = XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_Dayofweek);
            this.day = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_Day);
            this.month = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_Month);
            this.year = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_Year);
            this.hours = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_Hours);
            this.minutes = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_Minutes);
            this.seconds = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_Seconds);
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
        switch (this.setupId) {
            case Consts.STRXML_SetupId_NTPServer:
                sw.write(String.format(strFormat, STR_ServerUrl, this.serverUrl));
                break;
        }
        sw.write(String.format(strFormat, STR_TimeFormat, this.timeFormat));

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
        sw.write(String.format(strFormat, STR_TimeOfDay, this.timeofday));
        sw.write(String.format(strFormat, STR_DayOfWeek, this.dayofweek));
        sw.write(String.format(strFormat, STR_Day, this.day));
        sw.write(String.format(strFormat, STR_Month, this.month));
        sw.write(String.format(strFormat, STR_Year, this.year));
        sw.write(String.format(strFormat, STR_Hours, this.hours));
        sw.write(String.format(strFormat, STR_Minutes, this.minutes));
        sw.write(String.format(strFormat, STR_Seconds, this.seconds));

        return sw.toString();
    }
}
