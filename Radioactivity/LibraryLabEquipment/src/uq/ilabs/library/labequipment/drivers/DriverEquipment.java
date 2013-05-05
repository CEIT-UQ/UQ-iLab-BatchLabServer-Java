/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.drivers;

import java.util.logging.Level;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labequipment.Consts;
import uq.ilabs.library.labequipment.ExperimentSpecification;
import uq.ilabs.library.labequipment.ExperimentValidation;
import uq.ilabs.library.labequipment.devices.DeviceFlexMotion;
import uq.ilabs.library.labequipment.devices.DeviceST360Counter;
import uq.ilabs.library.labequipment.devices.DeviceSerialLcd;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;
import uq.ilabs.library.labequipment.engine.drivers.DriverGeneric;

/**
 *
 * @author uqlpayne
 */
public class DriverEquipment extends DriverGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DriverEquipment.class.getName();
    private static final Level logLevel = Level.FINER;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    protected int[][] dataVectors;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    protected DeviceFlexMotion deviceFlexMotion;
    protected DeviceST360Counter deviceST360Counter;
    protected DeviceSerialLcd deviceSerialLcd;

    public void setDeviceFlexMotion(DeviceFlexMotion deviceFlexMotion) {
        this.deviceFlexMotion = deviceFlexMotion;
    }

    public void setDeviceST360Counter(DeviceST360Counter deviceST360Counter) {
        this.deviceST360Counter = deviceST360Counter;
    }

    public void setDeviceSerialLcd(DeviceSerialLcd deviceSerialLcd) {
        this.deviceSerialLcd = deviceSerialLcd;
    }
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DriverEquipment(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration);

        final String methodName = "DriverEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Create an instance of ExperimentValidation
             */
            this.labExperimentValidation = new ExperimentValidation(labEquipmentConfiguration.getXmlValidation());
            if (this.labExperimentValidation == null) {
                throw new NullPointerException(ExperimentValidation.ClassName());
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
    public Validation Validate(String xmlSpecification) throws Exception {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        Validation validation;

        try {
            /*
             * Check that parameters are valid
             */
            super.Validate(xmlSpecification);

            /*
             * Check that the devices have been set
             */
            if (this.deviceFlexMotion == null) {
                throw new NullPointerException(DeviceFlexMotion.ClassName());
            }
            if (this.deviceST360Counter == null) {
                throw new NullPointerException(DeviceST360Counter.ClassName());
            }
            if (this.deviceSerialLcd == null) {
                throw new NullPointerException(DeviceSerialLcd.ClassName());
            }

            /*
             * Create an instance of ExperimentSpecification
             */
            ExperimentSpecification experimentSpecification = new ExperimentSpecification(xmlSpecification);

            /*
             * Validate the experiment specification parameters
             */
            ExperimentValidation experimentValidation = (ExperimentValidation) this.labExperimentValidation;
            for (int i = 0; i < experimentSpecification.getDistances().length; i++) {
                experimentValidation.ValidateDistance(experimentSpecification.getDistances()[i]);
            }
            experimentValidation.ValidateDuration(experimentSpecification.getDuration());
            experimentValidation.ValidateRepeat(experimentSpecification.getRepeat());

            /*
             * Specification is valid so far
             */
            validation = new Validation(true, 0);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            validation = new Validation(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Validation_arg3,
                validation.isAccepted(), validation.getExecutionTime(), validation.getErrorMessage()));

        return validation;
    }

    /**
     *
     * @return
     */
    @Override
    public String GetExperimentResults() {
        final String methodName = "GetExperimentResults";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String xmlExperimentResults = null;

        try {
            /*
             * Get the experiment specification
             */
            ExperimentSpecification experimentSpecification = (ExperimentSpecification) this.labExperimentSpecification;

            /*
             * Load the experiment results template XML document from the string
             */
            Document document = XmlUtilities.GetDocumentFromString(this.xmlExperimentResultsTemplate);
            Node nodeRoot = XmlUtilities.GetRootNode(document, Consts.STRXML_ExperimentResults);

            /*
             * Add the experiment specification information to the XML document
             */
            XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_SourceName, experimentSpecification.getSourceName());
            XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_AbsorberName, experimentSpecification.getCsvAbsorberNames());
            XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_Distance, experimentSpecification.getCsvDistances());
            XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_Duration, experimentSpecification.getDuration());
            XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_Repeat, experimentSpecification.getRepeat());

            /*
             * Add the experiment result information to the XML document
             */
            String dataType = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_DataType);
            XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_DataType, dataType);

            /*
             * Get the XML data vector node and clone it
             */
            Node nodeDataVector = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_DataVector);
            Node nodeClone = nodeDataVector.cloneNode(true);

            /*
             * Process data vectors in the experiment results
             */
            int[] distances = experimentSpecification.getDistances();
            for (int i = 0; i < this.dataVectors.length; i++) {
                /*
                 * Create a CSV string of radioactivity counts from the data vector
                 */
                XmlUtilities.SetValue(nodeDataVector, this.GetCsvString(this.dataVectors[i]));
                XmlUtilities.SetAttributeValue(nodeDataVector, Consts.STRXML_ATTR_Distance, Integer.toString(distances[i]));

                /*
                 * Add a data vector if there are more distances to process
                 */
                if (i < this.dataVectors.length - 1) {
                    nodeDataVector = nodeClone.cloneNode(true);
                    nodeRoot.appendChild(nodeDataVector);
                }
            }

            /*
             * Save the experiment results information to an XML string
             */
            xmlExperimentResults = XmlUtilities.ToXmlString(document);

        } catch (XmlUtilitiesException | DOMException ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return xmlExperimentResults;
    }

    /**
     *
     * @param intArray
     * @return String
     */
    private String GetCsvString(int[] intArray) {
        String csvString = "";

        for (int i = 0; i < intArray.length; i++) {
            csvString += String.format("%s%d", (!csvString.isEmpty()) ? Consts.STRCSV_SplitterChar : "", intArray[i]);
        }

        return csvString;
    }
}
