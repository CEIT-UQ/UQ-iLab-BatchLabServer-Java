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
    private static final String STR_PhasePhaseVoltage = "Ph-Ph Voltage (Volts)";
    private static final String STR_PhaseCurrent = "Phase Current (Amps)";
    private static final String STR_PowerFactor = "Power Factor (Avg)";
    private static final String STR_MotorSpeed = "Motor Speed (RPM)";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private double voltage;
    private double current;
    private double powerFactor;
    private int speed;
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
            /* Nothing to do here */

            /*
             * Get result information
             */
            this.voltage = XmlUtilities.GetChildValueAsDouble(this.nodeExperimentResult, Consts.STRXML_Voltage);
            this.current = XmlUtilities.GetChildValueAsDouble(this.nodeExperimentResult, Consts.STRXML_Current);
            this.powerFactor = XmlUtilities.GetChildValueAsDouble(this.nodeExperimentResult, Consts.STRXML_PowerFactor);
            this.speed = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_Speed);
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
        /* Nothing to do here */

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
        sw.write(String.format(strFormat, STR_PhasePhaseVoltage, this.voltage));
        sw.write(String.format(strFormat, STR_PhaseCurrent, this.current));
        sw.write(String.format(strFormat, STR_PowerFactor, this.powerFactor));
        sw.write(String.format(strFormat, STR_MotorSpeed, this.speed));

        return sw.toString();
    }
}
