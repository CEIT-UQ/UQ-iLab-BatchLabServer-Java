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
    private static final String STR_MinSpeed = "Min Speed (RPM)";
    private static final String STR_MaxSpeed = "Max Speed (RPM)";
    private static final String STR_SpeedStep = "Speed Step (RPM)";
    private static final String STR_MinField = "Min Field (%)";
    private static final String STR_MaxField = "Max Field (%)";
    private static final String STR_FieldStep = "Field Step (%)";
    private static final String STR_MinLoad = "Min Load (%)";
    private static final String STR_MaxLoad = "Max Load (%)";
    private static final String STR_LoadStep = "Load Step (%)";
    private static final String STR_MotorSpeed = "Motor Speed (RPM)";
    private static final String STR_FieldCurrent = "Field Current (A)";
    private static final String STR_ArmatureVoltage = "Armature Voltage (V)";
    private static final String STR_LoadTorque = "Load Torque (%)";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private int minimum;
    private int maximum;
    private int stepSize;
    private String speedVector;
    private String fieldVector;
    private String voltageVector;
    private String loadVector;
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
            switch (this.setupId) {
                case Consts.STRXML_SetupId_VoltageVsSpeed:
                case Consts.STRXML_SetupId_SpeedVsVoltage:
                    this.minimum = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_SpeedMin);
                    this.maximum = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_SpeedMax);
                    this.stepSize = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_SpeedStep);
                    break;
                case Consts.STRXML_SetupId_VoltageVsField:
                case Consts.STRXML_SetupId_SpeedVsField:
                    this.minimum = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_FieldMin);
                    this.maximum = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_FieldMax);
                    this.stepSize = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_FieldStep);
                    break;
                case Consts.STRXML_SetupId_VoltageVsLoad:
                    this.minimum = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_LoadMin);
                    this.maximum = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_LoadMax);
                    this.stepSize = XmlUtilities.GetChildValueAsInt(this.nodeExperimentResult, Consts.STRXML_LoadStep);
                    break;
            }

            /*
             * Get result information
             */
            this.speedVector = XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_SpeedVector);
            this.fieldVector = XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_FieldVector);
            this.voltageVector = XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_VoltageVector);
            this.loadVector = XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_LoadVector);

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
         * Determine value labels
         */
        String strMinimum = null;
        String strMaximum = null;
        String strStepSize = null;
        switch (this.setupId) {
            case Consts.STRXML_SetupId_VoltageVsSpeed:
            case Consts.STRXML_SetupId_SpeedVsVoltage:
                strMinimum = STR_MinSpeed;
                strMaximum = STR_MaxSpeed;
                strStepSize = STR_SpeedStep;
                break;
            case Consts.STRXML_SetupId_VoltageVsField:
            case Consts.STRXML_SetupId_SpeedVsField:
                strMinimum = STR_MinField;
                strMaximum = STR_MaxField;
                strStepSize = STR_FieldStep;
                break;
            case Consts.STRXML_SetupId_VoltageVsLoad:
                strMinimum = STR_MinLoad;
                strMaximum = STR_MaxLoad;
                strStepSize = STR_LoadStep;
                break;
        }

        /*
         * Experiment setup
         */
        StringWriter sw = new StringWriter();
        sw.write(String.format(strFormat, strMinimum, this.minimum));
        sw.write(String.format(strFormat, strMaximum, this.maximum));
        sw.write(String.format(strFormat, strStepSize, this.stepSize));

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
        switch (this.setupId) {
            case Consts.STRXML_SetupId_VoltageVsSpeed:
                sw.write(String.format(strFormat, STR_MotorSpeed, this.speedVector));
                sw.write(String.format(strFormat, STR_ArmatureVoltage, this.voltageVector));
                break;
            case Consts.STRXML_SetupId_SpeedVsVoltage:
                sw.write(String.format(strFormat, STR_ArmatureVoltage, this.voltageVector));
                sw.write(String.format(strFormat, STR_MotorSpeed, this.speedVector));
                break;
            case Consts.STRXML_SetupId_VoltageVsField:
                sw.write(String.format(strFormat, STR_FieldCurrent, this.fieldVector));
                sw.write(String.format(strFormat, STR_ArmatureVoltage, this.voltageVector));
                break;
            case Consts.STRXML_SetupId_SpeedVsField:
                sw.write(String.format(strFormat, STR_FieldCurrent, this.fieldVector));
                sw.write(String.format(strFormat, STR_MotorSpeed, this.speedVector));
                sw.write(String.format(strFormat, STR_ArmatureVoltage, this.voltageVector));
                break;
            case Consts.STRXML_SetupId_VoltageVsLoad:
                sw.write(String.format(strFormat, STR_LoadTorque, this.loadVector));
                sw.write(String.format(strFormat, STR_ArmatureVoltage, this.voltageVector));
                sw.write(String.format(strFormat, STR_MotorSpeed, this.speedVector));
                break;
        }

        return sw.toString();
    }
}
