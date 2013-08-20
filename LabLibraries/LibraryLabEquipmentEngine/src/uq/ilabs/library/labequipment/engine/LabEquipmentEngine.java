/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.engine;

import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.LabEquipmentStatus;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Delay;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.engine.devices.DeviceGeneric;
import uq.ilabs.library.labequipment.engine.drivers.DriverGeneric;

/**
 *
 * @author uqlpayne
 */
public class LabEquipmentEngine implements Runnable {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentEngine.class.getName();
    private static final Level logLevel = Level.FINER;
    private static final boolean debugTrace = false;
    /*
     * String constants
     */
    private static final String STR_NotInitialised = "Not Initialised!";
    private static final String STR_PoweringUp = "Powering up";
    private static final String STR_Initialising = "Initialising";
    private static final String STR_PoweringDown = "Powering down";
    private static final String STR_Ready = "Ready";
    private static final String STR_PoweredDown = "Powered down";
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_TimeUntilReady_arg2 = "RunState: %s  TimeUntilReady: %d seconds";
    private static final String STRLOG_StateChange_arg = "[LE: %s->%s]";
    //
    protected static final String STRLOG_ExecutionId_arg = "ExecutionId: %d";
    protected static final String STRLOG_SetupId_arg = "SetupId: %s";
    protected static final String STRLOG_EquipmentStatus_arg2 = "Online: %s  StatusMessage: %s";
    protected static final String STRLOG_Validation_arg3 = "Accepted: %s  ExecutionTime: %d  ErrorMessage: %s";
    protected static final String STRLOG_ExecutionStatus_arg3 = "ExecuteStatus: %s  ResultStatus: %s  ErrorMessage: %s";
    protected static final String STRLOG_ExecutionStatus_arg5 = "ExecutionId: %d  ExecuteStatus: %s  ResultStatus: %s  TimeRemaining: %d  ErrorMessage: %s";
    protected static final String STRLOG_Success_arg = "Success: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_InvalidSetupId_arg = "Invalid SetupId: %s";
    private static final String STRERR_ThreadFailedToStart = "Thread failed to start!";
    private static final String STRERR_InvalidExecutionId_arg = "Invalid ExecutionId: %d";
    //
    protected static final String STRERR_AlreadyExecuting = "Already executing!";
    protected static final String STRERR_FailedToStartExecution = "Failed to start execution!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Thread thread;
    private States runState;
    private boolean powerdownEnabled;
    private boolean powerdownSuspended;
    private int powerupTimeRemaining;
    private int poweroffTimeRemaining;
    private boolean statusReady;
    private String statusMessage;
    private Calendar initialiseStartTime;
    //
    protected LabEquipmentConfiguration labEquipmentConfiguration;
    protected WaitNotify signalStartExecution;
    protected DriverGeneric driver;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private int initialiseDelay;
    private int powerdownTimeout;
    private int poweroffDelay;
    private int powerupDelay;
    private int timeUntilPowerdown;
    private boolean running;

    public int getInitialiseDelay() {
        return initialiseDelay;
    }

    public int getPowerdownTimeout() {
        return powerdownTimeout;
    }

    public int getPoweroffDelay() {
        return poweroffDelay;
    }

    public int getPowerupDelay() {
        return powerupDelay;
    }

    public int getTimeUntilPowerdown() {
        return timeUntilPowerdown;
    }

    public boolean isRunning() {
        return running;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Types">

    private enum States {

        PowerOff, PowerUp, PowerUpDelay, PowerOnInit, PowerOnReady, PowerdownSuspended, DriverExecution, PowerDown, PowerOffDelay, Done
    }
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public LabEquipmentEngine(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        final String methodName = "LabEquipmentEngine";
        Logfile.WriteCalled(Level.CONFIG, STR_ClassName, methodName);

        try {
            /*
             * Check that parameters are valid
             */
            if (labEquipmentConfiguration == null) {
                throw new NullPointerException(LabEquipmentConfiguration.class.getSimpleName());
            }

            /*
             * Save to local variables
             */
            this.labEquipmentConfiguration = labEquipmentConfiguration;

            /*
             * Initialise local variables
             */
            this.signalStartExecution = new WaitNotify();
            if (this.signalStartExecution == null) {
                throw new NullPointerException(WaitNotify.class.getSimpleName());
            }
            this.runState = States.PowerOff;
            this.powerdownEnabled = this.labEquipmentConfiguration.isPowerdownEnabled();
            this.powerdownSuspended = false;
            this.statusReady = false;
            this.statusMessage = STR_NotInitialised;

            /*
             * Initialise properties
             */
            this.initialiseDelay = this.labEquipmentConfiguration.getInitialiseDelay();
            this.powerdownTimeout = this.labEquipmentConfiguration.getPowerdownTimeout();
            this.poweroffDelay = this.labEquipmentConfiguration.getPoweroffDelay();
            this.powerupDelay = this.labEquipmentConfiguration.getPowerupDelay();
            this.timeUntilPowerdown = 0;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(Level.CONFIG, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    public boolean Start() {
        final String methodName = "Start";
        Logfile.WriteCalled(STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Create a new thread and start it
             */
            this.thread = new Thread(this);
            if (this.thread == null) {
                throw new NullPointerException(Thread.class.getSimpleName());
            }
            this.thread.start();

            /*
             * Give it a chance to start running and then check that it has started
             */
            for (int i = 0; i < 5; i++) {
                if ((success = this.running) == true) {
                    break;
                }

                Delay.MilliSeconds(500);
                System.out.println('!');
            }

            if (success == false) {
                throw new RuntimeException(STRERR_ThreadFailedToStart);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     * Return the time in seconds before the equipment engine is ready to execute commands after the equipment has been
     * powered up.
     *
     * @return
     */
    public int GetTimeUntilReady() {
        final String methodName = "GetTimeUntilReady";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int timeUntilReady;

        switch (this.runState) {
            case Done:
            case PowerOff:
            case PowerUp:
                timeUntilReady = this.powerupDelay + this.initialiseDelay;
                break;
            case PowerUpDelay:
                timeUntilReady = this.powerupTimeRemaining + this.initialiseDelay;
                break;
            case PowerOnInit:
                /*
                 * Don't say initialisation has completed until it actually has
                 */
                timeUntilReady = this.initialiseDelay - (int) ((Calendar.getInstance().getTimeInMillis() - this.initialiseStartTime.getTimeInMillis()) / 1000);
                if (timeUntilReady < 1) {
                    timeUntilReady = 1;
                }
                break;
            case PowerDown:
                timeUntilReady = this.poweroffDelay + this.powerupDelay + this.initialiseDelay;
                break;
            case PowerOffDelay:
                timeUntilReady = this.poweroffTimeRemaining + this.powerupDelay + this.initialiseDelay;
                break;
            default:
                timeUntilReady = 0;
                break;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_TimeUntilReady_arg2, this.runState.toString(), timeUntilReady));

        return timeUntilReady;
    }

    /**
     *
     * @return
     */
    public LabEquipmentStatus GetLabEquipmentStatus() {
        final String methodName = "GetLabEquipmentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        LabEquipmentStatus labEquipmentStatus = new LabEquipmentStatus(this.statusReady, this.statusMessage);

        if (this.driver != null) {
            ExecutionStatus executionStatus = this.driver.GetExecutionStatus();
            labEquipmentStatus.setStatusMessage(executionStatus.getExecuteStatus().toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_EquipmentStatus_arg2, labEquipmentStatus.isOnline(), labEquipmentStatus.getStatusMessage()));

        return labEquipmentStatus;
    }

    /**
     *
     * @param xmlSpecification
     * @return
     */
    public Validation Validate(String xmlSpecification) {
        final String methodName = "Validate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        Validation validation = new Validation();

        try {
            /*
             * Get the setup Id from the experiment specification
             */
            LabExperimentSpecification labExperimentSpecification = new LabExperimentSpecification(xmlSpecification);
            String setupId = labExperimentSpecification.getSetupId();

            /*
             * Get the driver for the setup Id and validate the experiment specification
             */
            validation = this.GetDriver(setupId).Validate(xmlSpecification);

            /*
             * Check that the specification is accepted before adding in the time until ready
             */
            if (validation.isAccepted() == true) {
                validation.setExecutionTime(validation.getExecutionTime() + this.GetTimeUntilReady());
            }

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            validation.setErrorMessage(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Validation_arg3,
                validation.isAccepted(), validation.getExecutionTime(), validation.getErrorMessage()));

        return validation;
    }

    /**
     *
     * @param xmlSpecification
     * @return
     */
    public ExecutionStatus StartExecution(String xmlSpecification) {
        final String methodName = "StartExecution";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ExecutionStatus executionStatus;

        try {
            /*
             * Check if an experiment is already running
             */
            if (this.driver != null) {
                executionStatus = this.driver.GetExecutionStatus();
                if (executionStatus.getExecuteStatus() != ExecutionStatus.Status.Completed) {
                    throw new RuntimeException(STRERR_AlreadyExecuting);
                }
            }

            /*
             * Get the setup Id from the experiment specification
             */
            LabExperimentSpecification labExperimentSpecification = new LabExperimentSpecification(xmlSpecification);
            String setupId = labExperimentSpecification.getSetupId();

            /*
             * Get the driver for the setup Id and validate the experiment specification
             */
            this.driver = this.GetDriver(setupId);
            Validation validation = this.driver.Validate(xmlSpecification);
            if (validation.isAccepted() == false) {
                throw new RuntimeException(validation.getErrorMessage());
            }

            /*
             * Generate a random number for the execution Id
             */
            Random random = new Random();
            this.driver.setExecutionId(random.nextInt());

            /*
             * Start the driver executing but this may require powering up the equipment first
             */
            if (this.SuspendPowerdown() == false) {
                throw new RuntimeException(STRERR_FailedToStartExecution);
            }

            /*
             * Tell the thread that there is an experiment to execute
             */
            this.signalStartExecution.Notify();

            /*
             * Get the execution status and update the execution time including the time until the LabEquipment is ready
             */
            executionStatus = this.driver.GetExecutionStatus();
            executionStatus.setTimeRemaining(executionStatus.getTimeRemaining() + this.GetTimeUntilReady());

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            executionStatus = new ExecutionStatus();
            executionStatus.setResultStatus(ExecutionStatus.Status.Failed);
            executionStatus.setErrorMessage(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionStatus_arg5, executionStatus.getExecutionId(), executionStatus.getExecuteStatus(),
                executionStatus.getResultStatus(), executionStatus.getTimeRemaining(), executionStatus.getErrorMessage()));

        return executionStatus;
    }

    /**
     *
     * @param executionId
     * @return
     */
    public ExecutionStatus GetExecutionStatus(int executionId) {
        final String methodName = "GetExecutionStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionId_arg, executionId));

        ExecutionStatus executionStatus = new ExecutionStatus();

        try {
            if (this.driver != null) {
                /*
                 * Get the execution status and check the execution Id
                 */
                executionStatus = this.driver.GetExecutionStatus();
                if (executionStatus.getExecutionId() != executionId && executionStatus.getExecutionId() != 0) {
                    throw new RuntimeException(String.format(STRERR_InvalidExecutionId_arg, executionId));
                }

                /*
                 * Check if the experiment has completed
                 */
                if (executionStatus.getExecuteStatus() == ExecutionStatus.Status.Completed) {
                    /*
                     * Check if the experiment has completed successfully
                     */
                    if (executionStatus.getResultStatus() != ExecutionStatus.Status.Completed) {
                        /*
                         * The driver is no longer needed
                         */
                        this.driver = null;
                        this.ResumePowerdown();
                    }
                } else {
                    /*
                     *  Update the execution time including the time until the LabEquipment is ready
                     */
                    executionStatus.setTimeRemaining(executionStatus.getTimeRemaining() + this.GetTimeUntilReady());
                }
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionStatus_arg5, executionStatus.getExecutionId(), executionStatus.getExecuteStatus(),
                executionStatus.getResultStatus(), executionStatus.getTimeRemaining(), executionStatus.getErrorMessage()));

        return executionStatus;
    }

    /**
     *
     * @param executionId
     * @return
     */
    public String GetExperimentResults(int executionId) {
        final String methodName = "GetExperimentResults";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionId_arg, executionId));

        String experimentResults = null;

        try {
            if (this.driver != null) {
                /*
                 * Get the execution status and check the execution Id
                 */
                ExecutionStatus executionStatus = this.driver.GetExecutionStatus();
                if (executionStatus.getExecutionId() != executionId) {
                    throw new RuntimeException(String.format(STRERR_InvalidExecutionId_arg, executionId));
                }

                /*
                 * Get the results from the driver
                 */
                experimentResults = this.driver.GetExperimentResults();

                /*
                 * The driver is no longer needed
                 */
                this.driver = null;
                this.ResumePowerdown();
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                experimentResults);

        return experimentResults;
    }

    /**
     *
     * @param executionId
     * @return
     */
    public boolean CancelLabExecution(int executionId) {
        final String methodName = "CancelLabExecution";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionId_arg, executionId));

        boolean success = false;

        try {
            if (this.driver != null) {
                /*
                 * Get the execution status and check the execution Id
                 */
                ExecutionStatus executionStatus = this.driver.GetExecutionStatus();
                if (executionStatus.getExecutionId() != executionId) {
                    throw new RuntimeException(String.format(STRERR_InvalidExecutionId_arg, executionId));
                }

                /*
                 * Cancel driver execution
                 */
                this.driver.Cancel();
                success = true;
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @param setupId
     * @return
     * @throws Exception
     */
    protected DriverGeneric GetDriver(String setupId) throws Exception {
        final String methodName = "GetDriver";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_SetupId_arg, setupId));

        DriverGeneric driverGeneric = null;

        /*
         * Create an instance of the driver for the specified setup Id
         */
        switch (setupId) {
            case LabConsts.STRXML_SetupId_Generic:
                driverGeneric = new DriverGeneric(this.labEquipmentConfiguration);
                break;
            default:
                throw new RuntimeException(String.format(STRERR_InvalidSetupId_arg, setupId));
        }

        String message = (driverGeneric != null) ? driverGeneric.getDriverName() : null;
        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName, message);

        return driverGeneric;
    }

    /**
     *
     * @return
     */
    protected boolean PowerupEquipment() {
        final String methodName = "PowerupEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = true;

        /*
         * Nothing to do here, this will be overridden
         */

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    protected boolean InitialiseEquipment() {
        final String methodName = "InitialiseEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        /*
         * Create and initialise the generic device, this will be overridden
         */
        try {
            DeviceGeneric device = new DeviceGeneric(this.labEquipmentConfiguration, DeviceGeneric.class.getSimpleName());
            success = device.Initialise();
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    protected boolean PowerdownEquipment() {
        final String methodName = "PowerdownEquipment";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = true;

        /*
         * Nothing to do here, this will be overridden
         */

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     */
    public void Close() {
        final String methodName = "Close";
        Logfile.WriteCalled(STR_ClassName, methodName);

        /*
         * Stop the LabEquipmentEngine thread
         */
        if (this.running == true) {
            this.running = false;

            try {
                this.thread.join();
            } catch (InterruptedException ex) {
                Logfile.WriteError(ex.toString());
            }
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }

    @Override
    public void run() {
        final String methodName = "run";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Initialise state machine
         */
        States lastState = States.PowerOff;
        this.runState = States.PowerUp;
        this.running = true;

        /*
         * Allow other threads to check the state of this thread
         */
        Delay.MilliSeconds(500);

        /*
         * State machine loop
         */
        try {
            while (this.runState != States.Done) {
                /*
                 * Chec if a state change has occurred
                 */
                if (this.runState != lastState) {
                    String logMessage = String.format(STRLOG_StateChange_arg, lastState.toString(), this.runState.toString());
                    if (debugTrace == true) {
                        System.out.println(logMessage);
                    }
//                    Logfile.Write(logMessage);

                    lastState = this.runState;
                }

                switch (this.runState) {
                    case PowerUp:
                        /*
                         * Powerup the equipment
                         */
                        this.statusMessage = STR_PoweringUp;
                        if (this.PowerupEquipment() == false) {
                            /*
                             * Equipment failed to powerup
                             */
                            this.statusReady = false;
                            this.statusMessage = STR_NotInitialised;
                            this.runState = States.PowerOff;
                            break;
                        }

                        this.powerupTimeRemaining = this.powerupDelay;
                        this.runState = States.PowerUpDelay;
                        break;

                    case PowerUpDelay:
                        /*
                         * Wait a bit
                         */
                        Delay.MilliSeconds(1000);

                        /*
                         * Check if powerup delay has timed out
                         */
                        if (--this.powerupTimeRemaining > 0) {
                            /*
                             * Equipment is still powering up
                             */
                            if (debugTrace == true) {
                                System.out.println("[u]");
                            }
                            continue;
                        }

                        /*
                         * Equipment is now powered up
                         */
                        this.initialiseStartTime = Calendar.getInstance();
                        this.runState = States.PowerOnInit;
                        break;

                    case PowerOnInit:
                        /*
                         * Set the intialisation start time
                         */
                        this.statusMessage = STR_Initialising;

                        /*
                         * Initialise the equipment
                         */
                        if (this.InitialiseEquipment() == false) {
                            /*
                             * Equipment failed to initialise
                             */
                            this.statusReady = false;
                            this.statusMessage = STR_NotInitialised;
                            this.runState = States.PowerDown;
                            break;
                        }

                        /*
                         * Equipment is now ready to use
                         */
                        this.statusReady = true;
                        this.statusMessage = STR_Ready;
                        this.timeUntilPowerdown = this.powerdownTimeout;

                        /*
                         * Suspend equipment powerdown if not enabled
                         */
                        if (this.powerdownEnabled == false) {
                            this.powerdownSuspended = true;
                        } else {
                            /*
                             * Log the time remaining before the equipment is powered down
                             */
                            LogPowerDown(this.powerdownTimeout, true);
                        }

                        this.runState = States.PowerOnReady;
                        break;

                    case PowerOnReady:
                        /*
                         * Wait a bit
                         */
                        Delay.MilliSeconds(1000);

                        /*
                         * Check if LabEquipment is closing
                         */
                        if (this.running == false) {
                            this.runState = States.PowerDown;
                            break;
                        }

                        /*
                         * Check if equipment powerdown is suspended
                         */
                        if (this.powerdownSuspended == true) {
                            this.runState = States.PowerdownSuspended;
                            break;
                        }

                        /*
                         * Log the time remaining before power is removed
                         */
                        this.LogPowerDown(this.timeUntilPowerdown);

                        /*
                         * Check powerdown timeout
                         */
                        if (--this.timeUntilPowerdown == 0) {
                            /*
                             * Equipment is powering down
                             */
                            this.runState = States.PowerDown;
                            break;
                        }

                        /*
                         * Still counting down
                         */
                        if (debugTrace == true) {
                            System.out.println("[t]");
                        }
                        break;

                    case PowerdownSuspended:
                        /*
                         * Check if there is an experiment to execute
                         */
                        if (this.signalStartExecution.Wait(1000) == true) {
                            /*
                             * Start execution of the experiment specification
                             */
                            this.runState = States.DriverExecution;
                            break;
                        }

                        /*
                         * Check if LabEquipment is closing
                         */
                        if (this.running == false) {
                            this.runState = States.PowerDown;
                            break;
                        }

                        /*
                         * Check if powerdown is no longer suspended
                         */
                        if (this.powerdownSuspended == false) {
                            /*
                             * Reset the powerdown timeout
                             */
                            this.timeUntilPowerdown = this.powerdownTimeout;
                            this.runState = States.PowerOnReady;
                        }
                        break;

                    case DriverExecution:
                        /*
                         * Execute the experiment
                         */
                        this.driver.Execute();
                        this.signalStartExecution.Reset();
                        this.runState = States.PowerdownSuspended;
                        break;

                    case PowerDown:
                        /*
                         * Powerdown the equipment
                         */
                        this.statusMessage = STR_PoweringDown;
                        Logfile.Write(Level.INFO, STR_PoweringDown);
                        this.PowerdownEquipment();

                        this.poweroffTimeRemaining = this.poweroffDelay;
                        this.runState = States.PowerOffDelay;
                        break;

                    case PowerOffDelay:
                        /*
                         * Wait a bit
                         */
                        Delay.MilliSeconds(1000);

                        /*
                         * Check if LabEquipment is closing
                         */
                        if (this.running == false) {
                            this.runState = States.PowerOff;
                            break;
                        }

                        /*
                         * Check timeout
                         */
                        if (--this.poweroffTimeRemaining > 0) {
                            /*
                             * Poweroff delay is still counting down
                             */
                            if (debugTrace == true) {
                                System.out.println("[o]");
                            }
                        } else {
                            /*
                             * Check if powerup has been requested
                             */
                            if (this.powerdownSuspended == true) {
                                this.runState = States.PowerUp;
                            } else {
                                /*
                                 * Powerdown has completed
                                 */
                                this.statusMessage = STR_PoweredDown;
                                Logfile.Write(Level.INFO, STR_PoweredDown);
                                this.runState = States.PowerOff;
                            }
                        }
                        break;

                    case PowerOff:
                        this.runState = States.Done;
                        break;
                }
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        /*
         * Thread is no longer running
         */
        this.running = false;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    protected boolean SuspendPowerdown() {
        final String methodName = "SuspendPowerdown";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = true;

        /*
         * Check if powerdown is enabled
         */
        if (this.powerdownEnabled == true && this.powerdownSuspended == false) {
            /*
             * Start by suspending equipment powerdown
             */
            this.powerdownSuspended = true;

            /*
             * Check if the thread is still running
             */
            if (this.running == false) {
                /*
                 * Start the lab equipment engine
                 */
                success = this.Start();
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    protected void ResumePowerdown() {
        final String methodName = "ResumePowerdown";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Check if powerdown is enabled
         */
        if (this.powerdownEnabled == true) {
            /*
             * The equipment may already be powered down, doesn't matter
             */
            this.powerdownSuspended = false;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param seconds
     */
    private void LogPowerDown(int seconds) {
        LogPowerDown(seconds, false);
    }

    /**
     *
     * @param seconds
     * @param logItNow
     */
    private void LogPowerDown(int seconds, boolean logItNow) {
        final String STR_PowerdownIn = "Powerdown in ";
        final String STR_Minutes = "%d minute%s";
        final String STR_And = " and ";
        final String STR_Seconds = "%d second%s";

        int minutes = seconds / 60;
        String strMinutes = String.format(STR_Minutes, minutes, ((minutes != 1) ? "s" : ""));
        String strSeconds = String.format(STR_Seconds, seconds, ((seconds != 1) ? "s" : ""));

        if (logItNow == true && seconds > 0) {
            /*
             * Log message now
             */
            String logMessage = STR_PowerdownIn;
            seconds %= 60;
            if (minutes > 0) {
                logMessage += strMinutes;
                if (seconds != 0) {
                    logMessage += STR_And;
                }
            }
            if (seconds != 0) {
                logMessage += strSeconds;
            }
            Logfile.Write(Level.INFO, logMessage);
        } else {
            if (minutes > 5) {
                if (seconds % (5 * 60) == 0) {
                    /*
                     * Log message every 5 minutes
                     */
                    Logfile.Write(Level.INFO, STR_PowerdownIn + strMinutes);
                }
            } else if (seconds > 5) {
                if (seconds % 60 == 0 && seconds != 0) {
                    /*
                     * Log message every minute
                     */
                    Logfile.Write(Level.INFO, STR_PowerdownIn + strMinutes);
                }
            } else if (seconds > 0) {
                /*
                 * Log message every second
                 */
                Logfile.Write(Level.INFO, STR_PowerdownIn + strSeconds);
            }
        }
    }
}
