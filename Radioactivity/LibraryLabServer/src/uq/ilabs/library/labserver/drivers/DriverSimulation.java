/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.drivers;

import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.utilities.Delay;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labserver.Configuration;
import uq.ilabs.library.labserver.Consts;
import uq.ilabs.library.labserver.ExperimentResult;
import uq.ilabs.library.labserver.ExperimentSpecification;
import uq.ilabs.library.labserver.ExperimentValidation;
import uq.ilabs.library.labserver.SimulationConfig;
import uq.ilabs.library.labserver.engine.drivers.DriverGeneric;

/**
 *
 * @author uqlpayne
 */
public class DriverSimulation extends DriverGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DriverSimulation.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_StateChange_arg2 = "[SIM: %s->%s]";
    private static final String STRLOG_Location_arg = "Location: %s";
    private static final String STRLOG_Distance_arg = "Distance: %d";
    private static final String STRLOG_DistanceDuration_arg2 = "Distance: %d  Duration: %d";
    private static final String STRLOG_Data_arg = "Data: %d";
    private static final String STRLOG_Cancelled = "Cancelled...";
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
    /*
     * Constants
     */
    private static final int DELAY_MSECS_NoSimulateDelays = 10;
    /*
     * String constants
     */
    private static final String STR_XmlExperimentResults =
            "<" + Consts.STRXML_ExperimentResults + ">"
            + "<" + Consts.STRXML_SourceName + " />"
            + "<" + Consts.STRXML_AbsorberName + " />"
            + "<" + Consts.STRXML_Distance + " />"
            + "<" + Consts.STRXML_Duration + " />"
            + "<" + Consts.STRXML_Repeat + " />"
            + "<" + Consts.STRXML_DataType + " />"
            + "<" + Consts.STRXML_DataVector + " " + Consts.STRXML_ATTR_Distance + "=\"\" />"
            + "</" + Consts.STRXML_ExperimentResults + ">";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private ExperimentValidation experimentValidation;
    private SimulationConfig simulationConfig;
    private boolean simulateDelays;
    private int currentTubeDistance;
    private char currentSourceLocation;
    private char currentAbsorberLocation;
    private Random random;
    private String dataType;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Types">

    private enum States {

        SelectAbsorber, SelectSource, SetTubeDistance, CaptureData, ReturnTube, ReturnSource, ReturnAbsorber, Completed, Done
    }
    //</editor-fold>

    /**
     *
     * @param configuration
     * @throws Exception
     */
    public DriverSimulation(Configuration configuration, String xmlSimulationConfigPath) throws Exception {
        this(configuration, xmlSimulationConfigPath, true);
    }

    /**
     *
     * @param configuration
     * @param simulateDelays
     * @throws Exception
     */
    public DriverSimulation(Configuration configuration, String xmlSimulationConfigPath, boolean simulateDelays) throws Exception {
        super(configuration);

        final String methodName = "DriverSimulation";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Create an instance of SimulationConfig
             */
            this.simulationConfig = new SimulationConfig(null, xmlSimulationConfigPath);
            if (this.simulationConfig == null) {
                throw new NullPointerException(SimulationConfig.class.getSimpleName());
            }

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

            /*
             * Initialise local variables
             */
            this.simulateDelays = simulateDelays;
            this.dataType = (simulateDelays == true) ? ExperimentResult.DataTypes.Simulated.toString() : ExperimentResult.DataTypes.Calculated.toString();
            this.currentSourceLocation = this.simulationConfig.getSourceHomeLocation();
            this.currentAbsorberLocation = this.simulationConfig.getAbsorberHomeLocation();
            this.currentTubeDistance = this.simulationConfig.getTubeHomeDistance();

            /*
             * Create the random number generator and randomise the seed
             */
            int seed = (int) Calendar.getInstance().getTimeInMillis();
            this.random = new Random(seed);

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
                case Consts.STRXML_SetupId_SimActivityVsTime:
                case Consts.STRXML_SetupId_SimActivityVsDistance:
                case Consts.STRXML_SetupId_SimActivityVsTimeNoDelay:
                case Consts.STRXML_SetupId_SimActivityVsDistanceNoDelay:
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
             * Specification is valid, set the execution time
             */
            validationReport = new ValidationReport(true, this.GetExecutionTime(experimentSpecification));

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
     * @return
     * @throws Exception
     */
    @Override
    public ExperimentResult Execute(String xmlSpecification) throws Exception {
        final String methodName = "Execute";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ExperimentResult experimentResult = (ExperimentResult) this.labExperimentResult;

        try {
            /*
             * Validate the specification and get the estimated execution time
             */
            ValidationReport validationReport = this.Validate(xmlSpecification);
            if (validationReport.isAccepted() == false) {
                throw new RuntimeException(validationReport.getErrorMessage());
            }

            /*
             * Create an instance of ExperimentSpecification to get specification information
             */
            ExperimentSpecification experimentSpecification = new ExperimentSpecification(xmlSpecification);
            if (experimentSpecification == null) {
                throw new NullPointerException(ExperimentSpecification.class.getSimpleName());
            }

            try {
                /*
                 * Set the start and completion times
                 */
                this.timeStarted = Calendar.getInstance();
                this.timeCompleted = Calendar.getInstance();
                this.timeCompleted.setTimeInMillis(this.timeStarted.getTimeInMillis() + (int) validationReport.getEstRuntime() * 1000);

                /*
                 * Start execution of the lab experiment specification
                 */
                this.statusCode = StatusCodes.Running;

                /*
                 * Initialise state machine
                 */
                Configuration configuration = (Configuration) this.labConfiguration;
                int[][] dataVectors = new int[experimentSpecification.getDistances().length][];
                int distanceIndex = 0;
                int trialsIndex = 0;
                States lastState = States.Done;
                States thisState = States.SelectAbsorber;

                /*
                 * State machine loop
                 */
                while (thisState != States.Done) {
                    /*
                     * Display message on each state change
                     */
                    if (thisState != lastState) {
                        String logMessage = String.format(STRLOG_StateChange_arg2, lastState.toString(), thisState.toString());
                        System.out.println(logMessage);
//                        Logfile.Write(logLevel, logMessage);

                        lastState = thisState;
                    }

                    switch (thisState) {
                        case SelectAbsorber:
                            char absorberLocation = configuration.getAbsorberNameLocationMap().get(experimentSpecification.getAbsorbers()[0]);
                            this.SetAbsorberLocation(absorberLocation);

                            /*
                             * Check if the experiment has been cancelled
                             */
                            if (this.isCancelled() == true) {
                                Logfile.Write(STRLOG_Cancelled);
                                statusCode = StatusCodes.Cancelled;
                                thisState = States.ReturnAbsorber;
                                break;
                            }

                            thisState = States.SelectSource;
                            break;

                        case SelectSource:
                            char sourceLocation = configuration.getSourceNameLocationMap().get(experimentSpecification.getSource());
                            this.SetSourceLocation(sourceLocation);

                            /*
                             * Check if the experiment has been cancelled
                             */
                            if (this.isCancelled() == true) {
                                Logfile.Write(STRLOG_Cancelled);
                                statusCode = StatusCodes.Cancelled;
                                thisState = States.ReturnSource;
                                break;
                            }

                            thisState = States.SetTubeDistance;
                            break;

                        case SetTubeDistance:
                            this.SetTubeDistance(experimentSpecification.getDistances()[distanceIndex]);

                            /*
                             * Check if the experiment has been cancelled
                             */
                            if (this.isCancelled() == true) {
                                Logfile.Write(STRLOG_Cancelled);
                                statusCode = StatusCodes.Cancelled;
                                thisState = States.ReturnTube;
                                break;
                            }

                            dataVectors[distanceIndex] = new int[experimentSpecification.getTrials()];
                            trialsIndex = 0;
                            thisState = States.CaptureData;
                            break;

                        case CaptureData:
                            /*
                             * Capture data for this distance and duration
                             */
                            int data = this.CaptureData(experimentSpecification.getDistances()[distanceIndex], experimentSpecification.getDuration());
                            dataVectors[distanceIndex][trialsIndex] = data;

                            /*
                             * Check if the experiment has been cancelled
                             */
                            if (this.isCancelled() == true) {
                                Logfile.Write(STRLOG_Cancelled);
                                statusCode = StatusCodes.Cancelled;
                                thisState = States.ReturnTube;
                                break;
                            }

                            /*
                             * Check if all trials at this distance have been processed
                             */
                            if (++trialsIndex == experimentSpecification.getTrials()) {
                                /*
                                 * Check if all distances have been processed
                                 */
                                if (++distanceIndex == experimentSpecification.getDistances().length) {
                                    thisState = States.ReturnTube;
                                    break;
                                }

                                thisState = States.SetTubeDistance;
                            }

                            break;

                        case ReturnTube:
                            this.SetTubeDistance(this.simulationConfig.getTubeHomeDistance());
                            thisState = States.ReturnSource;
                            break;

                        case ReturnSource:
                            this.SetSourceLocation(this.simulationConfig.getSourceHomeLocation());
                            thisState = States.ReturnAbsorber;
                            break;

                        case ReturnAbsorber:
                            this.SetAbsorberLocation(this.simulationConfig.getAbsorberHomeLocation());
                            thisState = States.Completed;
                            break;

                        case Completed:
                            /*
                             * Update status code
                             */
                            if (statusCode == StatusCodes.Running) {
                                statusCode = StatusCodes.Completed;
                            }
                            thisState = States.Done;
                            break;
                    }
                }

                /*
                 * Check if the experiment was cancelled
                 */
                if (statusCode == StatusCodes.Cancelled) {
                    /*
                     * Yes, it was
                     */
                    this.labExperimentResult.getResultReport().setStatusCode(StatusCodes.Cancelled);
                } else {
                    /*
                     * Get the actual execution time
                     */
                    int executionTime = (int) ((Calendar.getInstance().getTimeInMillis() - this.timeStarted.getTimeInMillis()) / 1000);

                    /*
                     * Load the experiment result XML document from the string
                     */
                    Document document = XmlUtilities.GetDocumentFromString(STR_XmlExperimentResults);
                    Node nodeRoot = XmlUtilities.GetRootNode(document, Consts.STRXML_ExperimentResults);

                    /*
                     * Add the experiment specification information to the XML document
                     */
                    XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_SourceName, experimentSpecification.getSource());
                    XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_Duration, experimentSpecification.getDuration());
                    XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_Repeat, experimentSpecification.getTrials());

                    /*
                     * Create a CSV string of distances
                     */
                    String csvAbsorbers = "";
                    String[] absorbers = experimentSpecification.getAbsorbers();
                    for (int i = 0; i < absorbers.length; i++) {
                        csvAbsorbers += String.format("%s%s", (!csvAbsorbers.isEmpty()) ? Consts.STR_CsvSplitter : "", absorbers[i]);
                    }
                    XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_AbsorberName, csvAbsorbers);

                    /*
                     * Create a CSV string of distances
                     */
                    String csvDistances = "";
                    int[] distances = experimentSpecification.getDistances();
                    for (int i = 0; i < distances.length; i++) {
                        csvDistances += String.format("%s%d", (!csvDistances.isEmpty()) ? Consts.STR_CsvSplitter : "", distances[i]);
                    }
                    XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_Distance, csvDistances);

                    /*
                     * Add the experiment result information to the XML document
                     */
                    XmlUtilities.SetChildValue(nodeRoot, Consts.STRXML_DataType, this.dataType);
                    Node nodeDataVector = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_DataVector);
                    Node nodeClone = nodeDataVector.cloneNode(true);

                    for (int i = 0; i < dataVectors.length; i++) {
                        /*
                         * Create a CSV string of radioactivity counts from the data vector
                         */
                        String csvString = "";
                        for (int j = 0; j < dataVectors[i].length; j++) {
                            csvString += String.format("%s%d", (j > 0) ? Consts.STR_CsvSplitter : "", dataVectors[i][j]);
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
                     * Process the execution result
                     */
                    ResultReport resultReport = experimentResult.getResultReport();
                    resultReport.setStatusCode(statusCode);
                    resultReport.setXmlExperimentResults(XmlUtilities.ToXmlString(document));
                    experimentResult.setExecutionTime(executionTime);
                }
            } catch (XmlUtilitiesException | NullPointerException ex) {
                ResultReport resultReport = experimentResult.getResultReport();
                resultReport.setStatusCode(StatusCodes.Failed);
                resultReport.setErrorMessage(ex.getMessage());
            } finally {
                experimentResult.setTimeCompleted(this.timeCompleted);
                this.timeCompleted = null;
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_StatusCode_arg, experimentResult.getResultReport().getStatusCode().toString()));

        return experimentResult;
    }

    /**
     *
     * @param experimentSpecification
     * @return
     */
    private int GetExecutionTime(ExperimentSpecification experimentSpecification) {
        final String methodName = "GetExecutionTime";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        Configuration configuration = (Configuration) this.labConfiguration;
        double executionTime = 0.0;

        /*
         * Get absorber select time
         */
        char absorberLocation = configuration.getAbsorberNameLocationMap().get(experimentSpecification.getAbsorbers()[0]);
        executionTime += this.GetAbsorberSelectTime(absorberLocation);

        /*
         * Get source select time
         */
        char sourceLocation = configuration.getSourceNameLocationMap().get(experimentSpecification.getSource());
        executionTime += this.GetSourceSelectTime(sourceLocation);

        /*
         * Get tube move times
         */
        int[] distances = experimentSpecification.getDistances();
        for (int i = 0; i <= distances.length; i++) {
            /*
             * Determine the 'from' and 'to' distances
             */
            int fromDistance;
            int toDistance;
            if (i == 0) {
                /*
                 * From home to first distance
                 */
                fromDistance = this.simulationConfig.getTubeHomeDistance();
                toDistance = distances[i];
            } else if (i == distances.length) {
                /*
                 * Get tube return to home time
                 */
                fromDistance = distances[i - 1];
                toDistance = this.simulationConfig.getTubeHomeDistance();
                executionTime += this.GetTubeMoveTime(fromDistance, toDistance);
            } else {
                /*
                 * Everything in between
                 */
                fromDistance = distances[i - 1];
                toDistance = distances[i];
            }

            /*
             * Get tube move time
             */
            executionTime += this.GetTubeMoveTime(fromDistance, toDistance);

            /*
             * Get capture data time
             */
            executionTime += this.GetCaptureDataTime(experimentSpecification.getDuration() * experimentSpecification.getTrials());
        }

        /*
         * Get source return time
         */
        executionTime += this.GetSourceReturnTime(sourceLocation);

        /*
         * Get absorber return time
         */
        executionTime += this.GetAbsorberReturnTime(absorberLocation);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionTime_arg, (int) executionTime));

        return (int) executionTime;
    }

    /**
     * Get the time in seconds it takes to move the tube from one position to another.
     *
     * @param startDistance The distance in millimeters for the start of the move.
     * @param endDistance The distance in millimeters for the end of the move.
     * @return
     */
    private double GetTubeMoveTime(int startDistance, int endDistance) {
        double seconds;

        if (this.simulateDelays == true) {
            /*
             * Get the absolute distance
             */
            int distance = endDistance - startDistance;
            if (distance < 0) {
                distance = -distance;
            }

            /*
             * Tube move rate is in seconds per millimetre
             */
            seconds = (distance * this.simulationConfig.getTubeMoveRate());
        } else {
            seconds = (double) DELAY_MSECS_NoSimulateDelays / 1000.0;
        }

        return seconds;
    }

    /**
     *
     * @param toLocation
     * @return
     */
    private double GetSourceSelectTime(char toLocation) {
        double seconds = 0.0;

        if (this.simulateDelays == true) {
            int index = toLocation - this.simulationConfig.getSourceFirstLocation();
            double[] sourceSelectTimes = this.simulationConfig.getSourceSelectTimes();
            if (index >= 0 && index < sourceSelectTimes.length) {
                seconds = sourceSelectTimes[index];
            }
        } else {
            seconds = (double) DELAY_MSECS_NoSimulateDelays / 1000.0;
        }

        return seconds;
    }

    /**
     *
     * @param fromLocation
     * @return
     */
    private double GetSourceReturnTime(char fromLocation) {
        double seconds = 0.0;

        if (this.simulateDelays == true) {
            int index = fromLocation - this.simulationConfig.getSourceFirstLocation();
            double[] sourceReturnTimes = this.simulationConfig.getSourceReturnTimes();
            if (index >= 0 && index < sourceReturnTimes.length) {
                seconds = sourceReturnTimes[index];
            }
        } else {
            seconds = (double) DELAY_MSECS_NoSimulateDelays / 1000.0;
        }

        return seconds;
    }

    /**
     *
     * @param toLocation
     * @return
     */
    private double GetAbsorberSelectTime(char toLocation) {
        double seconds = 0.0;

        if (this.simulationConfig.isAbsorbersPresent() == true) {
            if (this.simulateDelays == true) {
                int index = toLocation - this.simulationConfig.getAbsorberFirstLocation();
                double[] absorberSelectTimes = this.simulationConfig.getAbsorberSelectTimes();
                if (index >= 0 && index < absorberSelectTimes.length) {
                    seconds = absorberSelectTimes[index];
                }
            } else {
                seconds = (double) DELAY_MSECS_NoSimulateDelays / 1000.0;
            }
        }

        return seconds;
    }

    /**
     *
     * @param fromLocation
     * @return
     */
    private double GetAbsorberReturnTime(char fromLocation) {
        double seconds = 0.0;

        if (this.simulationConfig.isAbsorbersPresent() == true) {
            if (this.simulateDelays == true) {
                int index = fromLocation - this.simulationConfig.getAbsorberFirstLocation();
                double[] absorberReturnTimes = this.simulationConfig.getAbsorberReturnTimes();
                if (index >= 0 && index < absorberReturnTimes.length) {
                    seconds = absorberReturnTimes[index];
                }
            } else {
                seconds = (double) DELAY_MSECS_NoSimulateDelays / 1000.0;
            }
        }

        return seconds;
    }

    /**
     *
     * @param duration
     * @return
     */
    private double GetCaptureDataTime(int duration) {
        double seconds;

        if (this.simulateDelays == true) {
            seconds = duration;
        } else {
            seconds = (double) DELAY_MSECS_NoSimulateDelays / 1000.0;
        }

        return seconds;
    }

    /**
     *
     * @param location
     * @return
     */
    private void SetAbsorberLocation(char location) {
        final String methodName = "SetAbsorberLocation";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Location_arg, location));

        if (this.simulationConfig.isAbsorbersPresent() == true) {
            if (this.simulateDelays == true) {
                /*
                 * Determine if selecting or returning absorber
                 */
                int seconds;
                if (location != this.simulationConfig.getAbsorberHomeLocation()) {
                    seconds = (int) this.GetAbsorberSelectTime(location);
                } else {
                    seconds = (int) this.GetAbsorberReturnTime(this.currentAbsorberLocation);
                }

                for (int i = 0; i < seconds; i++) {
                    Delay.MilliSeconds(1000);
                    System.out.println("A");
                }
            } else {
                Delay.MilliSeconds(DELAY_MSECS_NoSimulateDelays);
            }

            this.currentAbsorberLocation = location;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param location
     * @return
     */
    private void SetSourceLocation(char location) {
        final String methodName = "SetSourceLocation";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Location_arg, location));

        if (this.simulateDelays == true) {
            /*
             * Determine if selecting or returning source
             */
            int seconds;
            if (location != this.simulationConfig.getSourceHomeLocation()) {
                seconds = (int) this.GetSourceSelectTime(location);
            } else {
                seconds = (int) this.GetSourceReturnTime(this.currentSourceLocation);
            }

            for (int i = 0; i < seconds; i++) {
                Delay.MilliSeconds(1000);
                System.out.println("S");
            }
        } else {
            Delay.MilliSeconds(DELAY_MSECS_NoSimulateDelays);
        }

        this.currentSourceLocation = location;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param targetDistance
     * @return
     */
    private void SetTubeDistance(int distance) {
        final String methodName = "SetTubeDistance";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Distance_arg, distance));

        if (this.simulateDelays == true) {
            int seconds = (int) this.GetTubeMoveTime(this.currentTubeDistance, distance);

            for (int i = 0; i < seconds; i++) {
                Delay.MilliSeconds(1000);
                System.out.println("T");
            }
        } else {
            Delay.MilliSeconds(DELAY_MSECS_NoSimulateDelays);
        }

        this.currentTubeDistance = distance;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param distance
     * @param duration
     * @return
     */
    private int CaptureData(int distance, int duration) {
        final String methodName = "CaptureData";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_DistanceDuration_arg2, distance, duration));


        if (this.simulateDelays == true) {
            for (int i = 0; i < duration; i++) {
                Delay.MilliSeconds(1000);
                System.out.println("D");
            }
        } else {
            Delay.MilliSeconds(DELAY_MSECS_NoSimulateDelays);
        }

        int data = this.GenerateData(distance, duration);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Data_arg, data));

        return data;
    }

    /**
     * Generate simulated radioactivity data for the specified distance and duration
     *
     * @param distance The distance in millimeters from the source
     * @param duration The time in seconds to take the measurement
     * @return
     */
    private int GenerateData(int distance, int duration) {
        /*
         * Generate a value from a Gaussian distribution of random numbers
         */
        double dataGaussian = GetGaussian(random);

        /*
         * Adjust data for duration and distance
         */
        dataGaussian = AdjustData(dataGaussian, duration, distance);

        /*
         * Convert the simulated data from 'double' to 'int'
         */
        int value = (int) (dataGaussian + 0.5);

        /*
         * The value cannot be negative
         */
        if (value < 0) {
            value = 0;
        }

        return value;
    }

    /**
     * Adjust the data for the mean, standard deviation, duration and distance.
     *
     * @param data The array of data to adjust
     * @param duration The time in seconds to adjust the data
     * @param distance The distance to adjust the data
     * @return
     */
    private double AdjustData(double data, double duration, double distance) {
        /*
         * Calculate the scaling factors
         */
        double adjustStdDev = this.simulationConfig.getSimDeviation() * distance / this.simulationConfig.getSimDistance();
        double adjustDuration = duration / this.simulationConfig.getSimDuration();
        double adjustDistance = Math.pow(distance / this.simulationConfig.getSimDistance(), this.simulationConfig.getSimPower());

        /*
         * Adjust for the mean and standard deviation
         */
        double value = data * adjustStdDev + this.simulationConfig.getSimMean();

        /*
         * Now adjust for the duration
         */
        value *= adjustDuration;

        /*
         * Finally adjust for the distance
         */
        value /= adjustDistance;

        return value;
    }

    /**
     * Generate a Gaussian distribution of data with a mean of 0.0 and a standard deviation of 1.0 using the Box–Muller
     * transform method.
     *
     * @param random
     * @return
     */
    private double GetGaussian(Random random) {
        double random1;
        while (true) {
            /*
             * random1 must be > 0.0 for Math.log()
             */
            random1 = random.nextDouble();
            if (random1 > 0.0) {
                break;
            }
        }
        double random2 = random.nextDouble();

        double gaussian1 = Math.sqrt(-2.0 * Math.log(random1)) * Math.cos(Math.PI * 2.0 * random2);

        /*
         * Don't need the second number
         * double gaussian2 = Math.sqrt(-2.0 * Math.log(random1)) * Math.sin(Math.PI * 2.0 * random2);
         */

        return gaussian1;
    }
}
