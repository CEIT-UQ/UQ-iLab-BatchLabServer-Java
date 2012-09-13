/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.engine;

import java.util.logging.Level;
import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.engine.types.LabEquipmentStatus;

/**
 *
 * @author uqlpayne
 */
public class LabEquipmentManager {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentManager.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants
     */
    protected static final String STR_NotInitialised = "Not Initialised!";
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_EquipmentConfigFilename_arg = "equipmentConfigFilename: %s";
    //
    protected static final String STRLOG_ExecutionId_arg = "ExecutionId: %d";
    protected static final String STRLOG_Success_arg = "Success: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_EquipmentConfigFilename = "equipmentConfigFilename";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    protected LabEquipmentConfiguration labEquipmentConfiguration;
    protected LabEquipmentEngine labEquipmentEngine;
    //</editor-fold>

    /**
     *
     * @param equipmentConfigFilename
     * @throws Exception
     */
    public LabEquipmentManager(String equipmentConfigFilename) throws Exception {
        final String methodName = "LabEquipmentManager";
        Logfile.WriteCalled(Level.CONFIG, STR_ClassName, methodName,
                String.format(STRLOG_EquipmentConfigFilename_arg, equipmentConfigFilename));

        try {
            /*
             * Check that parameters are valid
             */
            if (equipmentConfigFilename == null) {
                throw new NullPointerException(STRERR_EquipmentConfigFilename);
            }

            /*
             * Create class instances and objects that are used by the LabEquipmentEngine
             */
            this.labEquipmentConfiguration = new LabEquipmentConfiguration(null, equipmentConfigFilename);
            if (labEquipmentConfiguration == null) {
                throw new NullPointerException(LabEquipmentConfiguration.class.getSimpleName());
            }
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
    public boolean Create() {
        final String methodName = "Create";
        Logfile.WriteCalled(Level.CONFIG, STR_ClassName, methodName);

        boolean success = false;

        /*
         * Create an instance of the equipment engine
         */
        try {
            this.labEquipmentEngine = new LabEquipmentEngine(this.labEquipmentConfiguration);
            if (this.labEquipmentEngine == null) {
                throw new NullPointerException(LabEquipmentEngine.class.getSimpleName());
            }

            success = true;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(Level.CONFIG, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    public boolean Start() {
        final String methodName = "Start";
        Logfile.WriteCalled(STR_ClassName, methodName);

        boolean success = false;

        if (this.labEquipmentEngine != null) {
            success = this.labEquipmentEngine.Start();
        }

        Logfile.WriteCompleted(STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return
     */
    public int GetTimeUntilReady() {
        int timeUntilReady = -1;

        if (this.labEquipmentEngine != null) {
            timeUntilReady = this.labEquipmentEngine.GetTimeUntilReady();
        }

        return timeUntilReady;
    }

    /**
     *
     * @return
     */
    public int GetTimeUntilPowerdown() {
        int timeUntilPowerdown = -1;

        if (this.labEquipmentEngine != null) {
            timeUntilPowerdown = this.labEquipmentEngine.getTimeUntilPowerdown();
        }

        return timeUntilPowerdown;
    }

    /**
     *
     * @return
     */
    public LabEquipmentStatus GetLabEquipmentStatus() {
        final String methodName = "GetLabEquipmentStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        LabEquipmentStatus labEquipmentStatus;

        if (this.labEquipmentEngine != null) {
            labEquipmentStatus = this.labEquipmentEngine.GetLabEquipmentStatus();
        } else {
            labEquipmentStatus = new LabEquipmentStatus();
            labEquipmentStatus.setOnline(false);
            labEquipmentStatus.setStatusMessage(STR_NotInitialised);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

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

        Validation validation = null;
        if (this.labEquipmentEngine != null) {
            validation = this.labEquipmentEngine.Validate(xmlSpecification);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return validation;
    }

    /**
     *
     * @param xmlSpecification
     * @return
     */
    public ExecutionStatus StartLabExecution(String xmlSpecification) {
        final String methodName = "StartLabExecution";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ExecutionStatus executionStatus = null;

        if (this.labEquipmentEngine != null) {
            executionStatus = this.labEquipmentEngine.StartExecution(xmlSpecification);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return executionStatus;
    }

    /**
     *
     * @param executionId
     * @return
     */
    public ExecutionStatus GetLabExecutionStatus(int executionId) {
        final String methodName = "GetLabExecutionStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionId_arg, executionId));

        ExecutionStatus executionStatus = null;

        if (this.labEquipmentEngine != null) {
            executionStatus = this.labEquipmentEngine.GetExecutionStatus(executionId);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return executionStatus;
    }

    /**
     *
     * @param executionId
     * @return
     */
    public String GetLabExecutionResults(int executionId) {
        final String methodName = "GetLabExecutionResults";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExecutionId_arg, executionId));

        String labExecutionResults = null;

        if (this.labEquipmentEngine != null) {
            labExecutionResults = this.labEquipmentEngine.GetExperimentResults(executionId);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return labExecutionResults;
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

        boolean cancelled = false;

        if (this.labEquipmentEngine != null) {
            cancelled = this.labEquipmentEngine.CancelLabExecution(executionId);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return cancelled;
    }

    /**
     *
     */
    public void Close() {
        final String methodName = "Close";
        Logfile.WriteCalled(STR_ClassName, methodName);

        if (this.labEquipmentEngine != null) {
            this.labEquipmentEngine.Close();
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }
}
