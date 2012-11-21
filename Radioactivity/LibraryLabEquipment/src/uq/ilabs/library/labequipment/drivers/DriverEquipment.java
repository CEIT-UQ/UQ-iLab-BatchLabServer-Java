/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.drivers;

import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labequipment.Consts;
import uq.ilabs.library.labequipment.ExperimentSpecification;
import uq.ilabs.library.labequipment.devices.DeviceFlexMotion;
import uq.ilabs.library.labequipment.devices.DeviceST360Counter;
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
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_AbsorberLocation_arg = "Absorber location: %c";
    private static final String STRLOG_SourceLocation_arg = "Source location: %c";
    private static final String STRLOG_TubeDistance_arg = "Tube Distance: %d";
    private static final String STRLOG_CaptureData_arg3 = "Capture Data: %d secs - %d of %d";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_SetTubeDistance = "Failed to set tube distance!";
    private static final String STRERR_SetAbsorberLocation = "Failed to set absorber location!";
    private static final String STRERR_SetSourceLocation = "Failed to set source location!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private DeviceFlexMotion deviceFlexMotion;
    private DeviceST360Counter deviceST360Counter;

    public void setDeviceFlexMotion(DeviceFlexMotion deviceFlexMotion) {
        this.deviceFlexMotion = deviceFlexMotion;
    }

    public void setDeviceST360Counter(DeviceST360Counter deviceST360Counter) {
        this.deviceST360Counter = deviceST360Counter;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Types">

    private enum States {

        SelectAbsorber, SelectSource, SetTubeDistance, CaptureData, ReturnTube, ReturnSource, ReturnAbsorber, Completed, Done
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
             * Nothing to do here
             */
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

        /*
         * Check that parameters are valid
         */
        super.Validate(xmlSpecification);

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
             * Calculate the execution time
             */
            int executionTime = this.GetExecutionTime(experimentSpecification);

            /*
             * Specification is valid
             */
            validation = new Validation(true, executionTime);
            this.labExperimentSpecification = experimentSpecification;

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
        return super.GetExperimentResults();
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean ExecuteInitialising() {
        return super.ExecuteInitialising();
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean ExecuteStarting() {
        final String methodName = "ExecuteStarting";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        ExperimentSpecification experimentSpecification = (ExperimentSpecification) this.labExperimentSpecification;

        try {
            if (this.cancelled == false) {
                /*
                 * Set absorber location
                 */
                Logfile.Write(String.format(STRLOG_AbsorberLocation_arg, experimentSpecification.getAbsorberLocations()[0]));
                if (this.deviceFlexMotion.SetAbsorberLocation(experimentSpecification.getAbsorberLocations()[0]) == false) {
                    throw new RuntimeException(STRERR_SetAbsorberLocation);
                }
            }

            if (this.cancelled == false) {
                /*
                 * Set source location
                 */
                Logfile.Write(String.format(STRLOG_SourceLocation_arg, experimentSpecification.getSourceLocation()));
                if (this.deviceFlexMotion.SetSourceLocation(experimentSpecification.getSourceLocation()) == false) {
                    throw new RuntimeException(STRERR_SetSourceLocation);
                }
            }

            success = true;

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            this.executionStatus.setErrorMessage(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean ExecuteRunning() {
        final String methodName = "ExecuteRunning";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Initialise local variables
             */
            ExperimentSpecification experimentSpecification = (ExperimentSpecification) this.labExperimentSpecification;
            int[][] dataVectors = new int[experimentSpecification.getDistances().length][];
            int distanceIndex = 0;
            int trialsIndex = 0;
            States thisState = States.SetTubeDistance;

            /*
             * State machine loop
             */
            while (thisState != States.Done) {
                switch (thisState) {

                    case SetTubeDistance:
                        /*
                         * Set the tube distance
                         */
                        Logfile.Write(String.format(STRLOG_TubeDistance_arg, experimentSpecification.getDistances()[distanceIndex]));
                        if (this.deviceFlexMotion.SetTubeDistance(experimentSpecification.getDistances()[distanceIndex]) == false) {
                            throw new RuntimeException(STRERR_SetTubeDistance);
                        }

                        /*
                         * Allocate storage for the capture data
                         */
                        dataVectors[distanceIndex] = new int[experimentSpecification.getTrials()];
                        trialsIndex = 0;
                        thisState = States.CaptureData;
                        break;

                    case CaptureData:
                        /*
                         * Capture data for this distance and duration
                         */
                        Logfile.Write(String.format(STRLOG_CaptureData_arg3, experimentSpecification.getDuration(), trialsIndex + 1, experimentSpecification.getTrials()));
                        int data = this.deviceST360Counter.CaptureData(experimentSpecification.getDistances()[distanceIndex], experimentSpecification.getDuration());
                        dataVectors[distanceIndex][trialsIndex] = data;

                        /*
                         * Check if all trials at this distance have been processed
                         */
                        if (++trialsIndex == experimentSpecification.getTrials()) {
                            /*
                             * Check if all distances have been processed
                             */
                            if (++distanceIndex == experimentSpecification.getDistances().length) {
                                thisState = States.Done;
                                break;
                            }

                            thisState = States.SetTubeDistance;
                        }
                        break;
                }

                /*
                 * Check if the experiment has been cancelled
                 */
                if (this.cancelled == true) {
                    thisState = States.Done;
                }
            }

            if (this.cancelled == false) {
                /*
                 * Load the experiment results XML document from the string
                 */
                Document document = XmlUtilities.GetDocumentFromString(this.xmlExperimentResults);
                Node nodeRoot = XmlUtilities.GetRootNode(document, Consts.STRXML_ExperimentResults);

                /*
                 * Add the experiment specification information to the XML document
                 */
                XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_SourceName, ((Character) experimentSpecification.getSourceLocation()).toString());
                XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_Duration, experimentSpecification.getDuration());
                XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_Repeat, experimentSpecification.getTrials());

                /*
                 * Create a CSV string of absorbers
                 */
                String csvAbsorbers = "";
                char[] absorbers = experimentSpecification.getAbsorberLocations();
                for (int i = 0; i < absorbers.length; i++) {
                    csvAbsorbers += String.format("%s%c", (!csvAbsorbers.isEmpty()) ? Consts.STRCSV_SplitterChar : "", absorbers[i]);
                }
                XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_AbsorberName, csvAbsorbers);

                /*
                 * Create a CSV string of distances
                 */
                String csvDistances = "";
                int[] distances = experimentSpecification.getDistances();
                for (int i = 0; i < distances.length; i++) {
                    csvDistances += String.format("%s%d", (!csvDistances.isEmpty()) ? Consts.STRCSV_SplitterChar : "", distances[i]);
                }
                XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_Distance, csvDistances);

                /*
                 * Add the experiment result information to the XML document
                 */
                Node nodeDataVector = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_DataVector);
                Node nodeClone = nodeDataVector.cloneNode(true);

                for (int i = 0; i < dataVectors.length; i++) {
                    /*
                     * Create a CSV string of radioactivity counts from the data vector
                     */
                    String csvString = "";
                    for (int j = 0; j < dataVectors[i].length; j++) {
                        csvString += String.format("%s%d", (j > 0) ? Consts.STRCSV_SplitterChar : "", dataVectors[i][j]);
                    }
                    XmlUtilities.SetValue(nodeDataVector, csvString);
                    XmlUtilities.SetAttribute(nodeDataVector, Consts.STRXML_ATTR_Distance, Integer.toString(distances[i]));

                    /*
                     * Add a data vector if there are more distances to process
                     */
                    if (i < dataVectors.length - 1) {
                        nodeDataVector = nodeClone.cloneNode(true);
                        nodeRoot.appendChild(nodeDataVector);
                    }
                }

                /*
                 * Save the experiment results information to an XML string
                 */
                this.xmlExperimentResults = XmlUtilities.ToXmlString(document);

                success = true;
            }
        } catch (RuntimeException | XmlUtilitiesException ex) {
            Logfile.WriteError(ex.toString());
            this.executionStatus.setErrorMessage(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean ExecuteStopping() {
        final String methodName = "ExecuteStopping";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success;

        /*
         * Return source to home location
         */
        Logfile.Write(String.format(STRLOG_SourceLocation_arg, this.deviceFlexMotion.getSourceHomeLocation()));
        success = this.deviceFlexMotion.SetSourceLocation(this.deviceFlexMotion.getSourceHomeLocation());

        /*
         * Return absorber to home location
         */
        Logfile.Write(String.format(STRLOG_AbsorberLocation_arg, this.deviceFlexMotion.getAbsorberHomeLocation()));
        success &= this.deviceFlexMotion.SetAbsorberLocation(this.deviceFlexMotion.getAbsorberHomeLocation());

        /*
         * Return tube to home distance
         */
        Logfile.Write(String.format(STRLOG_TubeDistance_arg, this.deviceFlexMotion.getTubeHomeDistance()));
        success &= this.deviceFlexMotion.SetTubeDistance(this.deviceFlexMotion.getTubeHomeDistance());

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean ExecuteFinalising() {
        return super.ExecuteFinalising();
    }

    /**
     *
     * @param experimentSpecification
     * @return
     */
    private int GetExecutionTime(ExperimentSpecification experimentSpecification) {
        final String methodName = "GetExecutionTime";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        double executionTime;

        /*
         * No initialising
         */
        this.executionTimes.setInitialise(0);

        /*
         * Get absorber select time
         */
        char absorberLocation = experimentSpecification.getAbsorberLocations()[0];
        executionTime = this.deviceFlexMotion.GetAbsorberSelectTime(absorberLocation);

        /*
         * Get source select time
         */
        char sourceLocation = experimentSpecification.getSourceLocation();
        executionTime += this.deviceFlexMotion.GetSourceSelectTime(sourceLocation);

        /*
         * Get tube move time from home to first distance
         */
        int[] distances = experimentSpecification.getDistances();
        executionTime += this.deviceFlexMotion.GetTubeMoveTime(this.deviceFlexMotion.getTubeHomeDistance(), distances[0]);

        /*
         * Set starting time
         */
        this.executionTimes.setStart((int) (executionTime + 0.5));

        /*
         * Calculate running time
         */
        executionTime = 0.0;
        for (int i = 0; i < distances.length; i++) {
            /*
             * Get tube move time
             */
            if (i > 0) {
                executionTime += this.deviceFlexMotion.GetTubeMoveTime(distances[i - 1], distances[i]);
            }

            /*
             * Get capture data time
             */
            executionTime += this.deviceST360Counter.GetCaptureDataTime(experimentSpecification.getDuration() * experimentSpecification.getTrials());
        }

        /*
         * Set running time
         */
        this.executionTimes.setRun((int) (executionTime + 0.5));


        /*
         * Get source and absorber return times
         */
        executionTime = this.deviceFlexMotion.GetSourceReturnTime(sourceLocation);
        executionTime += this.deviceFlexMotion.GetAbsorberReturnTime(absorberLocation);

        /*
         * Get tube move time from last distance to home
         */
        executionTime += this.deviceFlexMotion.GetTubeMoveTime(distances[distances.length - 1], this.deviceFlexMotion.getTubeHomeDistance());

        /*
         * Set stopping time
         */
        this.executionTimes.setStop((int) (executionTime + 0.5));

        /*
         * No finalising
         */
        this.executionTimes.setFinalise(0);

        /*
         * Get the total execution time
         */
        int totalExecutionTime = this.executionTimes.getTotalExecutionTime();

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionTime_arg, totalExecutionTime));

        return totalExecutionTime;
    }
}
