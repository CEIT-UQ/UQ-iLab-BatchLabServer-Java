/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import org.w3c.dom.Node;
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
    private static final String STR_NameUnits_arg2 = "%s (%s)";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Measurement fieldCurrent;
    private Measurement speed;
    private Measurement voltage;
    private Measurement statorCurrent;
    /*
     * Pre-synchronisation
     */
    private Measurement speedSetpoint;
    private Measurement syncVoltage;
    private Measurement syncFrequency;
    private Measurement mainsVoltage;
    private Measurement mainsFrequency;
    private Measurement syncMainsPhase;
    private Measurement synchronism;
    /*
     * Synchronisation
     */
    private Measurement torqueSetpoint;
    private Measurement powerFactor;
    private Measurement realPower;
    private Measurement reactivePower;
    private Measurement phaseCurrent;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Types">

    private class Measurement {

        private String name;
        private String units;
        private String values;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }

        public String getValues() {
            return values;
        }

        public void setValues(String values) {
            this.values = values;
        }
    }
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
            switch (this.setupId) {
                case Consts.STRXML_SetupId_OpenCircuitVaryField:
                    this.fieldCurrent = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_FieldCurrent);
                    this.speed = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_Speed);
                    this.voltage = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_Voltage);
                    break;

                case Consts.STRXML_SetupId_OpenCircuitVarySpeed:
                    this.speed = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_Speed);
                    this.fieldCurrent = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_FieldCurrent);
                    this.voltage = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_Voltage);
                    break;

                case Consts.STRXML_SetupId_ShortCircuitVaryField:
                    this.fieldCurrent = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_FieldCurrent);
                    this.speed = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_Speed);
                    this.statorCurrent = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_StatorCurrent);
                    break;

                case Consts.STRXML_SetupId_PreSynchronisation:
                    this.fieldCurrent = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_FieldCurrent);
                    this.speedSetpoint = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_SpeedSetpoint);
                    this.mainsVoltage = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_MainsVoltage);
                    this.mainsFrequency = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_MainsFrequency);
                    this.syncVoltage = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_SyncVoltage);
                    this.syncFrequency = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_SyncFrequency);
                    this.syncMainsPhase = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_SyncMainsPhase);
                    this.synchronism = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_Synchronism);
                    break;

                case Consts.STRXML_SetupId_Synchronisation:
                    this.torqueSetpoint = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_TorqueSetpoint);
                    this.fieldCurrent = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_FieldCurrent);
                    this.syncVoltage = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_SyncVoltage);
                    this.syncFrequency = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_SyncFrequency);
                    this.powerFactor = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_PowerFactor);
                    this.realPower = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_RealPower);
                    this.reactivePower = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_ReactivePower);
                    this.phaseCurrent = GetMeasurement(this.nodeExperimentResult, Consts.STRXML_PhaseCurrent);
                    break;
            }

        } catch (XmlUtilitiesException ex) {
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param parentNode
     * @param childName
     * @return
     * @throws XmlUtilitiesException
     */
    private Measurement GetMeasurement(Node parentNode, String childName) throws XmlUtilitiesException {
        Measurement measurement = new Measurement();

        Node node = XmlUtilities.GetChildNode(parentNode, childName);
        measurement.setName(XmlUtilities.GetAttribute(node, Consts.STRXML_ATTR_Name, false));
        measurement.setUnits(XmlUtilities.GetAttribute(node, Consts.STRXML_ATTR_Units, false));
        measurement.setValues(XmlUtilities.GetChildValue(parentNode, childName));

        return measurement;
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
         * Create an array of measurements for the specified setup
         */
        Measurement[] measurements;
        switch (this.setupId) {
            case Consts.STRXML_SetupId_OpenCircuitVaryField:
                measurements = new Measurement[]{
                    this.fieldCurrent, this.speed, this.voltage
                };
                break;

            case Consts.STRXML_SetupId_OpenCircuitVarySpeed:
                measurements = new Measurement[]{
                    this.speed, this.fieldCurrent, this.voltage
                };
                break;

            case Consts.STRXML_SetupId_ShortCircuitVaryField:
                measurements = new Measurement[]{
                    this.fieldCurrent, this.speed, this.statorCurrent
                };
                break;

            case Consts.STRXML_SetupId_PreSynchronisation:
                measurements = new Measurement[]{
                    this.fieldCurrent, this.speedSetpoint, this.mainsVoltage, this.mainsFrequency,
                    this.syncVoltage, this.syncFrequency, this.syncMainsPhase, this.synchronism
                };
                break;

            case Consts.STRXML_SetupId_Synchronisation:
                measurements = new Measurement[]{
                    this.torqueSetpoint, this.fieldCurrent, this.syncVoltage, this.syncFrequency,
                    this.powerFactor, this.realPower, this.reactivePower, this.phaseCurrent
                };
                break;

            default:
                measurements = new Measurement[0];
                break;
        }

        /*
         * Process each measurement in the array
         */
        StringWriter sw = new StringWriter();
        for (int i = 0; i < measurements.length; i++) {
            Measurement measurement = measurements[i];
            if (measurement != null) {
                /*
                 * Check if the units have been specified
                 */
                if (measurement.getUnits() == null || measurement.getUnits().isEmpty()) {
                    sw.write(String.format(strFormat, measurement.getName(), measurement.getValues()));
                } else {
                    sw.write(String.format(strFormat, String.format(STR_NameUnits_arg2, measurement.getName(), measurement.getUnits()), measurement.getValues()));
                }
            }
        }

        return sw.toString();
    }
}
