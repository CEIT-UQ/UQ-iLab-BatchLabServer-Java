/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.drivers;

import java.util.logging.Level;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.Configuration;
import uq.ilabs.library.labserver.Consts;
import uq.ilabs.library.labserver.ExperimentResult;
import uq.ilabs.library.labserver.ExperimentSpecification;
import uq.ilabs.library.labserver.ExperimentSpecification_Field;
import uq.ilabs.library.labserver.ExperimentSpecification_Load;
import uq.ilabs.library.labserver.ExperimentSpecification_Speed;
import uq.ilabs.library.labserver.engine.drivers.DriverEquipmentGeneric;
import uq.ilabs.library.labserver.engine.types.LabEquipmentServiceInfo;

/**
 *
 * @author uqlpayne
 */
public class DriverEquipment extends DriverEquipmentGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DriverEquipment.class.getName();
    private static final Level logLevel = Level.FINE;
    //</editor-fold>

    /**
     *
     * @param labEquipmentServiceInfo
     * @throws Exception
     */
    public DriverEquipment(Configuration configuration, LabEquipmentServiceInfo labEquipmentServiceInfo) throws Exception {
        super(configuration, labEquipmentServiceInfo);

        final String methodName = "DriverEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Create an instance of the experiment result ready to fill in
             */
            this.labExperimentResult = new ExperimentResult(configuration);
            if (this.labExperimentResult == null) {
                throw new NullPointerException(ExperimentResult.class.getSimpleName());
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param xmlSpecification
     * @return
     * @throws Exception
     */
    @Override
    public ValidationReport Validate(String xmlSpecification) throws Exception {
        /*
         * Parse the XML specification and then pass on for processing
         */
        return super.Validate(this.ParseExperimentSpecification(xmlSpecification));
    }

    /**
     *
     * @param xmlSpecification
     * @return
     * @throws Exception
     */
    @Override
    public ExperimentResult Execute(String xmlSpecification) throws Exception {
        /*
         * Parse the XML specification and then pass on for processing
         */
        return (ExperimentResult) super.Execute(this.ParseExperimentSpecification(xmlSpecification));
    }

    /**
     *
     * @param xmlSpecification
     * @return
     */
    private String ParseExperimentSpecification(String xmlSpecification) {
        /*
         * Create an instance of ExperimentSpecification to get the setup Id
         */
        ExperimentSpecification experimentSpecification = ExperimentSpecification.ToObject(xmlSpecification);
        String setupId = experimentSpecification.getSetupId();

        /*
         * Reformat the xml specification to remove unused fields
         */
        switch (setupId) {
            case Consts.STRXML_SetupId_VoltageVsSpeed:
            case Consts.STRXML_SetupId_SpeedVsVoltage:
                ExperimentSpecification_Speed experimentSpecification_Speed = ExperimentSpecification_Speed.ToObject(xmlSpecification);
                xmlSpecification = experimentSpecification_Speed.ToXmlString();
                break;

            case Consts.STRXML_SetupId_VoltageVsField:
            case Consts.STRXML_SetupId_SpeedVsField:
                ExperimentSpecification_Field experimentSpecification_Field = ExperimentSpecification_Field.ToObject(xmlSpecification);
                xmlSpecification = experimentSpecification_Field.ToXmlString();
                break;

            case Consts.STRXML_SetupId_VoltageVsLoad:
                ExperimentSpecification_Load experimentSpecification_Load = ExperimentSpecification_Load.ToObject(xmlSpecification);
                xmlSpecification = experimentSpecification_Load.ToXmlString();
                break;

            default:
                throw new RuntimeException(String.format(STRERR_InvalidSetupId_arg, setupId));
        }

        return xmlSpecification;
    }
}
