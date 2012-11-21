/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.drivers;

import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.labserver.Configuration;
import uq.ilabs.library.labserver.Consts;
import uq.ilabs.library.labserver.ExperimentResult;
import uq.ilabs.library.labserver.ExperimentSpecification;
import uq.ilabs.library.labserver.ExperimentValidation;
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
    /*
     * String constants for exception messages
     */
    private static final String STRERR_Distance = "Distance";
    private static final String STRERR_Duration = "Duration";
    private static final String STRERR_Trials = "Trials";
    private static final String STRERR_SourceUnknown_arg = "%s: Source is unknown!";
    private static final String STRERR_AbsorberUnknown_arg = "%s: Absorber is unknown!";
    private static final String STRERR_ValueLessThanMinimum_arg2 = "%s: Less than minimum (%d)!";
    private static final String STRERR_ValueGreaterThanMaximum_arg2 = "%s: Greater than maximum (%d)!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private ExperimentValidation experimentValidation;
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
             * Create an instance of ExperimentValidation
             */
            this.experimentValidation = new ExperimentValidation(configuration.getXmlValidation());
            if (this.experimentValidation == null) {
                throw new NullPointerException(ExperimentValidation.class.getSimpleName());
            }

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
     */
    @Override
    public ValidationReport Validate(String xmlSpecification) throws Exception {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ValidationReport validationReport;

        try {
            /*
             * Create an instance of ExperimentSpecification
             */
            ExperimentSpecification experimentSpecification = new ExperimentSpecification(xmlSpecification);
            if (experimentSpecification == null) {
                throw new NullPointerException(ExperimentSpecification.class.getSimpleName());
            }

            /*
             * Check the setup Id
             */
            String setupId = experimentSpecification.getSetupId();
            switch (setupId) {
                case Consts.STRXML_SetupId_RadioactivityVsTime:
                case Consts.STRXML_SetupId_RadioactivityVsDistance:
                    break;
                default:
                    throw new RuntimeException(String.format(STRERR_InvalidSetupId_arg, setupId));
            }

            /*
             * Check source name is valid
             */
            Configuration configuration = (Configuration) this.labConfiguration;
            String sourceName = experimentSpecification.getSource();
            if (configuration.getSourceNameLocationMap().containsKey(sourceName) == false) {
                throw new RuntimeException(String.format(STRERR_SourceUnknown_arg, sourceName));
            }

            /*
             * Check absorber names are valid
             */
            String[] absorbers = experimentSpecification.getAbsorbers();
            for (int i = 0; i < absorbers.length; i++) {
                if (configuration.getAbsorberNameLocationMap().containsKey(absorbers[i]) == false) {
                    throw new RuntimeException(String.format(STRERR_AbsorberUnknown_arg, absorbers[i]));
                }
            }

            /*
             * Check distances are valid
             */
            int[] distances = experimentSpecification.getDistances();
            for (int i = 0; i < distances.length; i++) {
                int distance = distances[i];
                if (distance < this.experimentValidation.getDistanceMinimum()) {
                    throw new RuntimeException(String.format(STRERR_ValueLessThanMinimum_arg2, STRERR_Distance, this.experimentValidation.getDistanceMinimum()));
                }
                if (distance > this.experimentValidation.getDistanceMaximum()) {
                    throw new RuntimeException(String.format(STRERR_ValueGreaterThanMaximum_arg2, STRERR_Distance, this.experimentValidation.getDistanceMaximum()));
                }
            }

            /*
             * Check duration is valid
             */
            int duration = experimentSpecification.getDuration();
            if (duration < this.experimentValidation.getDurationMinimum()) {
                throw new RuntimeException(String.format(STRERR_ValueLessThanMinimum_arg2, STRERR_Duration, this.experimentValidation.getDurationMinimum()));
            }
            if (duration > this.experimentValidation.getDurationMaximum()) {
                throw new RuntimeException(String.format(STRERR_ValueGreaterThanMaximum_arg2, STRERR_Duration, this.experimentValidation.getDurationMaximum()));
            }

            /*
             * Check trials is valid
             */
            int trials = experimentSpecification.getTrials();
            if (trials < this.experimentValidation.getTrialsMinimum()) {
                throw new RuntimeException(String.format(STRERR_ValueLessThanMinimum_arg2, STRERR_Trials, this.experimentValidation.getTrialsMinimum()));
            }
            if (trials > this.experimentValidation.getTrialsMaximum()) {
                throw new RuntimeException(String.format(STRERR_ValueGreaterThanMaximum_arg2, STRERR_Trials, this.experimentValidation.getTrialsMaximum()));
            }

            /*
             * Modify specification - change source and absorber names to locations
             */
            xmlSpecification = ModifySpecification(experimentSpecification);

            /*
             * Pass on to the LabEquipment for validation
             */
            validationReport = super.Validate(xmlSpecification);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            validationReport = new ValidationReport(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Validation_arg3,
                validationReport.isAccepted(), validationReport.getEstRuntime(), validationReport.getErrorMessage()));

        return validationReport;
    }

    /**
     *
     * @param xmlSpecification
     * @return ExperimentResult
     * @throws Exception
     */
    @Override
    public ExperimentResult Execute(String xmlSpecification) throws Exception {
        final String methodName = "Execute";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ExperimentResult experimentResult = null;
        StatusCodes resultStatusCode = StatusCodes.Unknown;

        try {
            ExperimentSpecification experimentSpecification = new ExperimentSpecification(xmlSpecification);
            String sourceName = experimentSpecification.getSource();
            String[] absorbers = experimentSpecification.getAbsorbers();
            String csvAbsorbersNames = "";
            for (int i = 0; i < absorbers.length; i++) {
                csvAbsorbersNames += String.format("%s%s", (!csvAbsorbersNames.isEmpty()) ? Consts.STR_CsvSplitter : "", absorbers[i]);
            }

            /*
             * Modify specification - change source and absorber names to locations
             */
            xmlSpecification = ModifySpecification(experimentSpecification);

            /*
             * Pass on to the LabEquipment for execution
             */
            experimentResult = (ExperimentResult) super.Execute(xmlSpecification);

            /*
             * Modify result - change source and absorber locations back to names
             */
            String xmlExperimentResults = experimentResult.getResultReport().getXmlExperimentResults();
            xmlExperimentResults = this.ModifyXmlExperimentResults(xmlExperimentResults, sourceName, csvAbsorbersNames);
            experimentResult.getResultReport().setXmlExperimentResults(xmlExperimentResults);

            resultStatusCode = experimentResult.getResultReport().getStatusCode();

        } catch (Exception ex) {
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_StatusCode_arg, resultStatusCode.toString()));

        return experimentResult;
    }

    /**
     *
     * @param experimentSpecification
     * @return
     */
    private String ModifySpecification(ExperimentSpecification experimentSpecification) {

        Configuration configuration = (Configuration) this.labConfiguration;

        try {
            /*
             * Change source name to location
             */
            String sourceName = experimentSpecification.getSource();
            char location = configuration.getSourceNameLocationMap().get(sourceName);
            experimentSpecification.setSource(((Character) location).toString());

            /*
             * Change absorber names to locations
             */
            String[] absorbers = experimentSpecification.getAbsorbers();
            for (int i = 0; i < absorbers.length; i++) {
                location = configuration.getAbsorberNameLocationMap().get(absorbers[i]);
                absorbers[i] = ((Character) location).toString();
            }
            experimentSpecification.setAbsorbers(absorbers);

        } catch (Exception ex) {
        }

        return experimentSpecification.toString();
    }

    /**
     *
     * @param xmlExperimentResults
     * @param sourceName
     * @param csvAbsorbersNames
     * @return String
     */
    private String ModifyXmlExperimentResults(String xmlExperimentResults, String sourceName, String csvAbsorbersNames) {

        try {
            /*
             * Load the experiment specification XML document from the string
             */
            Document document = XmlUtilities.GetDocumentFromString(xmlExperimentResults);
            Node nodeRoot = XmlUtilities.GetRootNode(document, Consts.STRXML_ExperimentResults);

            /*
             * Restore source and absorber names
             */
            XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_SourceName, sourceName);
            XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_AbsorberName, csvAbsorbersNames);

            /*
             * Convert to XML string
             */
            xmlExperimentResults = XmlUtilities.ToXmlString(document);

        } catch (Exception ex) {
        }

        return xmlExperimentResults;
    }
}
