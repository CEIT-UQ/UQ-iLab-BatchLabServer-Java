/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.drivers;

import java.util.logging.Level;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.Consts;
import uq.ilabs.library.labequipment.ExperimentSpecification;
import uq.ilabs.library.labequipment.devices.DeviceFlexMotion;
import uq.ilabs.library.labequipment.devices.DeviceST360Counter;
import uq.ilabs.library.labequipment.devices.DeviceSerialLcd;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;

/**
 *
 * @author uqlpayne
 */
public class DriverRadioactivity extends DriverEquipment {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DriverRadioactivity.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants for serial LCD messages
     */
    private static final String STRLCD_Ready = "Ready.";
    private static final String STRLCD_SelectAbsorber = "Select absorber:";
    private static final String STRLCD_SelectSource = "Select source:";
    private static final String STRLCD_SetDistance = "Set distance:";
    private static final String STRLCD_Distance_arg = "%dmm";
    private static final String STRLCD_CaptureData_arg4 = "%dmm-%dsec-%d/%d";
    private static final String STRLCD_CaptureCounts = "Capture counts:";
    private static final String STRLCD_ReturnSource = "Return source";
    private static final String STRLCD_ReturnAbsorber = "Return absorber";
    private static final String STRLCD_ReturnTube = "Return tube";
    private static final String STRLCD_EmptyString = "                ";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Types">

    private enum States {

        SelectAbsorberMessageLine1, SelectAbsorberMessageLine2, SelectAbsorber,
        SelectSourceMessageLine1, SelectSourceMessageLine2, SelectSource,
        SetTubeDistanceMessageLine1, SetTubeDistanceMessageLine2, SetTubeDistance,
        CaptureDataMessageLine1, CaptureDataMessageLine2, CaptureData,
        ReturnSourceMessageLine1, ReturnSourceMessageLine2, GetSourceHomeLocation, ReturnSource,
        ReturnAbsorberMessageLine1, ReturnAbsorberMessageLine2, GetAbsorberHomeLocation, ReturnAbsorber,
        ReturnTubeMessageLine1, ReturnTubeMessageLine2, GetTubeHomeDistance, ReturnTube,
        ReturnToReadyMessageLine1, ReturnToReadyMessageLine2,
        Completed, Done
    }
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DriverRadioactivity(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration);
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
             * Check that the correct devices have been set
             */
            if (DeviceFlexMotion.class.isInstance(this.deviceFlexMotion) == false) {
                throw new NullPointerException(String.format(STRERR_InvalidDeviceInstance_arg, DeviceFlexMotion.ClassName()));
            }
            if (DeviceST360Counter.class.isInstance(this.deviceST360Counter) == false) {
                throw new NullPointerException(String.format(STRERR_DeviceNotSet_arg, DeviceST360Counter.ClassName()));
            }
            if (DeviceSerialLcd.class.isInstance(this.deviceSerialLcd) == false) {
                throw new NullPointerException(String.format(STRERR_DeviceNotSet_arg, DeviceSerialLcd.ClassName()));
            }

            /*
             * Create an instance of ExperimentSpecification
             */
            ExperimentSpecification experimentSpecification = new ExperimentSpecification(xmlSpecification);
            if (experimentSpecification == null) {
                throw new NullPointerException(ExperimentSpecification.ClassName());
            }

            /*
             * Check the setup Id
             */
            String setupId = experimentSpecification.getSetupId();
            switch (setupId) {
                case Consts.STRXML_SetupId_RadioactivityVsTime:
                case Consts.STRXML_SetupId_RadioactivityVsDistance:
                    /*
                     * Specification is valid
                     */
                    validation = new Validation(true, this.GetExecutionTime(experimentSpecification));
                    this.labExperimentSpecification = experimentSpecification;
                    break;

                default:
                    /*
                     * Don't throw an exception, a derived class will want to check the setup Id
                     */
                    validation = new Validation(String.format(STRERR_InvalidSetupId_arg, setupId));
                    break;
            }

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
    protected boolean ExecuteInitialising() {
        /*
         * Nothing to do here
         */
        return true;
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
        String absorberName = experimentSpecification.getAbsorberNames()[0];
        String sourceName = experimentSpecification.getSourceName();

        try {
            /*
             * Initialise state machine
             */
            States thisState = States.SelectAbsorberMessageLine1;

            /*
             * State machine loop
             */
            while (thisState != States.Done) {
                switch (thisState) {

                    case SelectAbsorberMessageLine1:
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.One, STRLCD_SelectAbsorber) == false) {
                            throw new RuntimeException(this.deviceSerialLcd.getLastError());
                        }

                        thisState = States.SelectAbsorberMessageLine2;
                        break;

                    case SelectAbsorberMessageLine2:
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.Two, absorberName) == false) {
                            throw new RuntimeException(this.deviceSerialLcd.getLastError());
                        }

                        thisState = States.SelectAbsorber;
                        break;

                    case SelectAbsorber:
                        if (this.deviceFlexMotion.SelectAbsorber(absorberName) == false) {
                            throw new RuntimeException(this.deviceFlexMotion.getLastError());
                        }

                        thisState = States.SelectSourceMessageLine1;
                        break;

                    case SelectSourceMessageLine1:
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.One, STRLCD_SelectSource) == false) {
                            throw new RuntimeException(this.deviceSerialLcd.getLastError());
                        }

                        thisState = States.SelectSourceMessageLine2;
                        break;

                    case SelectSourceMessageLine2:
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.Two, sourceName) == false) {
                            throw new RuntimeException(this.deviceSerialLcd.getLastError());
                        }

                        thisState = States.SelectSource;
                        break;

                    case SelectSource:
                        if (this.deviceFlexMotion.SelectSource(sourceName) == false) {
                            throw new RuntimeException(this.deviceFlexMotion.getLastError());
                        }

                        thisState = States.Done;
                        break;
                }

                /*
                 * Check if the experiment has been cancelled
                 */
                if (this.cancelled == true) {
                    thisState = States.Done;
                }
            }

            success = (this.cancelled == false);
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
             * Initialise state machine
             */
            ExperimentSpecification experimentSpecification = (ExperimentSpecification) this.labExperimentSpecification;
            this.dataVectors = new int[experimentSpecification.getDistances().length][];
            int distanceIndex = 0;
            int repeatIndex = 0;
            int distance = 0;
            String lcdMessage;
            States thisState = States.SetTubeDistanceMessageLine1;

            /*
             * State machine loop
             */
            while (thisState != States.Done) {
                switch (thisState) {

                    case SetTubeDistanceMessageLine1:
                        distance = experimentSpecification.getDistances()[distanceIndex];

                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.One, STRLCD_SetDistance) == false) {
                            throw new RuntimeException(this.deviceSerialLcd.getLastError());
                        }

                        thisState = States.SetTubeDistanceMessageLine2;
                        break;

                    case SetTubeDistanceMessageLine2:
                        lcdMessage = String.format(STRLCD_Distance_arg, distance);
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.Two, lcdMessage) == false) {
                            throw new RuntimeException(this.deviceSerialLcd.getLastError());
                        }

                        thisState = States.SetTubeDistance;
                        break;

                    case SetTubeDistance:
                        if (this.deviceFlexMotion.SelectTubeDistance(distance) == false) {
                            throw new RuntimeException(this.deviceFlexMotion.getLastError());
                        }

                        /*
                         * Allocate storage for the capture data
                         */
                        dataVectors[distanceIndex] = new int[experimentSpecification.getRepeat()];
                        repeatIndex = 0;

                        thisState = States.CaptureDataMessageLine1;
                        break;

                    case CaptureDataMessageLine1:
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.One, STRLCD_CaptureCounts) == false) {
                            throw new RuntimeException(this.deviceSerialLcd.getLastError());
                        }

                        thisState = States.CaptureDataMessageLine2;
                        break;

                    case CaptureDataMessageLine2:
                        lcdMessage = String.format(STRLCD_CaptureData_arg4, distance, experimentSpecification.getDuration(), repeatIndex + 1, experimentSpecification.getRepeat());
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.Two, lcdMessage) == false) {
                            throw new RuntimeException(this.deviceSerialLcd.getLastError());
                        }

                        thisState = States.CaptureData;
                        break;

                    case CaptureData:
                        /*
                         * Capture data for this distance and duration
                         */
                        int data = this.deviceST360Counter.CaptureData(distance, experimentSpecification.getDuration());
                        dataVectors[distanceIndex][repeatIndex] = data;

                        /*
                         * Check if all repeats at this distance have been processed
                         */
                        if (++repeatIndex == experimentSpecification.getRepeat()) {
                            /*
                             * Check if all distances have been processed
                             */
                            if (++distanceIndex == experimentSpecification.getDistances().length) {
                                thisState = States.Done;
                                break;
                            }

                            thisState = States.SetTubeDistanceMessageLine1;
                            break;
                        }

                        thisState = States.CaptureDataMessageLine2;
                        break;
                }

                /*
                 * Check if the experiment has been cancelled
                 */
                if (this.cancelled == true) {
                    thisState = States.Done;
                }
            }

            success = (this.cancelled == false);
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
    protected boolean ExecuteStopping() {
        final String methodName = "ExecuteStopping";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Initialise state machine
             */
            String lastError = null;
            States thisState = States.ReturnSourceMessageLine1;

            /*
             * State machine loop - Can't throw any exceptions if there were any errors, have to keep on going.
             */
            while (thisState != States.Done) {
                switch (thisState) {

                    case ReturnSourceMessageLine1:
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.One, STRLCD_ReturnSource) == false) {
                            if (lastError == null) {
                                lastError = this.deviceSerialLcd.getLastError();
                            }
                        }

                        thisState = States.ReturnSourceMessageLine2;
                        break;

                    case ReturnSourceMessageLine2:
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.Two, STRLCD_EmptyString) == false) {
                            if (lastError == null) {
                                lastError = this.deviceSerialLcd.getLastError();
                            }
                        }

                        thisState = States.ReturnSource;
                        break;

                    case ReturnSource:
                        if (this.deviceFlexMotion.SelectSource(this.deviceFlexMotion.getSourceHomeName()) == false) {
                            if (lastError == null) {
                                lastError = this.deviceFlexMotion.getLastError();
                            }
                        }

                        thisState = States.ReturnAbsorberMessageLine1;
                        break;

                    case ReturnAbsorberMessageLine1:
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.One, STRLCD_ReturnAbsorber) == false) {
                            if (lastError == null) {
                                lastError = this.deviceSerialLcd.getLastError();
                            }
                        }

                        thisState = States.ReturnAbsorberMessageLine2;
                        break;

                    case ReturnAbsorberMessageLine2:
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.Two, STRLCD_EmptyString) == false) {
                            if (lastError == null) {
                                lastError = this.deviceSerialLcd.getLastError();
                            }
                        }

                        thisState = States.ReturnAbsorber;
                        break;

                    case ReturnAbsorber:
                        if (this.deviceFlexMotion.SelectAbsorber(this.deviceFlexMotion.getAbsorberHomeName()) == false) {
                            if (lastError == null) {
                                lastError = this.deviceFlexMotion.getLastError();
                            }
                        }

                        thisState = States.ReturnTubeMessageLine1;
                        break;

                    case ReturnTubeMessageLine1:
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.One, STRLCD_ReturnTube) == false) {
                            if (lastError == null) {
                                lastError = this.deviceSerialLcd.getLastError();
                            }
                        }

                        thisState = States.ReturnTubeMessageLine2;
                        break;

                    case ReturnTubeMessageLine2:
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.Two, STRLCD_EmptyString) == false) {
                            if (lastError == null) {
                                lastError = this.deviceSerialLcd.getLastError();
                            }
                        }

                        thisState = States.ReturnTube;
                        break;

                    case ReturnTube:
                        if (this.deviceFlexMotion.SelectTubeDistance(this.deviceFlexMotion.getTubeHomeDistance()) == false) {
                            if (lastError == null) {
                                lastError = this.deviceFlexMotion.getLastError();
                            }
                        }

                        thisState = States.ReturnToReadyMessageLine1;
                        break;

                    case ReturnToReadyMessageLine1:
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.One, STRLCD_Ready) == false) {
                            if (lastError == null) {
                                lastError = this.deviceSerialLcd.getLastError();
                            }
                        }

                        thisState = States.ReturnToReadyMessageLine2;
                        break;

                    case ReturnToReadyMessageLine2:
                        if (this.deviceSerialLcd.WriteLine(DeviceSerialLcd.LineNumber.Two, STRLCD_EmptyString) == false) {
                            if (lastError == null) {
                                lastError = this.deviceSerialLcd.getLastError();
                            }
                        }

                        thisState = States.Done;
                        break;
                }

                /*
                 * Do not check if the experiment has been cancelled, it has finished running anyway
                 */
            }

            /*
             * Check if there were any errors
             */
            success = (lastError == null);
            if (success == false) {
                this.executionStatus.setErrorMessage(lastError);
            }
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
    protected boolean ExecuteFinalising() {
        /*
         * Nothing to do here
         */
        return true;
    }

    /**
     *
     * @param experimentSpecification
     * @return int
     */
    protected int GetExecutionTime(ExperimentSpecification experimentSpecification) {
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
        executionTime = this.deviceSerialLcd.GetWriteLineTime() * 2;
        executionTime += this.deviceFlexMotion.GetAbsorberSelectTime(experimentSpecification.getAbsorberNames()[0]);

        /*
         * Get source select time
         */
        executionTime += this.deviceSerialLcd.GetWriteLineTime() * 2;
        executionTime += this.deviceFlexMotion.GetSourceSelectTime(experimentSpecification.getSourceName());

        /*
         * Get tube move time from home to first distance
         */
        executionTime += this.deviceSerialLcd.GetWriteLineTime() * 2;
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
                executionTime += this.deviceSerialLcd.GetWriteLineTime() * 2;
                executionTime += this.deviceFlexMotion.GetTubeMoveTime(distances[i - 1], distances[i]);
            }

            /*
             * Get capture data time
             */
            executionTime += this.deviceSerialLcd.GetWriteLineTime() * 2;
            executionTime += this.deviceST360Counter.GetCaptureDataTime(experimentSpecification.getDuration() * experimentSpecification.getRepeat());
        }

        /*
         * Set running time
         */
        this.executionTimes.setRun((int) (executionTime + 0.5));

        /*
         * Get source return time
         */
        executionTime = this.deviceSerialLcd.GetWriteLineTime() * 2;
        executionTime += this.deviceFlexMotion.GetSourceReturnTime(experimentSpecification.getSourceName());

        /*
         * Get absorber return time
         */
        executionTime += this.deviceSerialLcd.GetWriteLineTime() * 2;
        executionTime += this.deviceFlexMotion.GetAbsorberReturnTime(experimentSpecification.getAbsorberNames()[0]);

        /*
         * Get tube move time from last distance to home
         */
        executionTime += this.deviceSerialLcd.GetWriteLineTime() * 2;
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
