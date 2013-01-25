/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import uq.ilabs.library.labserver.engine.LabConsts;

/**
 *
 * @author uqlpayne
 */
public class Consts extends LabConsts {

    /*
     * XML elements in the LabConfiguration.xml file
     */
    public static final String STRXML_SetupId_OpenCircuitVaryField = "OpenCircuitVaryField";
    public static final String STRXML_SetupId_OpenCircuitVarySpeed = "OpenCircuitVarySpeed";
    public static final String STRXML_SetupId_ShortCircuitVaryField = "ShortCircuitVaryField";
    public static final String STRXML_SetupId_PreSynchronisation = "PreSynchronisation";
    public static final String STRXML_SetupId_Synchronisation = "Synchronisation";
    /*
     * XML elements in the results
     */
    public static final String STRXML_measurements = "measurements";
    public static final String STRXML_FieldCurrent = "fieldCurrent";
    public static final String STRXML_Speed = "speed";
    public static final String STRXML_Voltage = "voltage";
    public static final String STRXML_StatorCurrent = "statorCurrent";
    public static final String STRXML_SpeedSetpoint = "speedSetpoint";
    public static final String STRXML_MainsVoltage = "mainsVoltage";
    public static final String STRXML_MainsFrequency = "mainsFrequency";
    public static final String STRXML_SyncVoltage = "syncVoltage";
    public static final String STRXML_SyncFrequency = "syncFrequency";
    public static final String STRXML_SyncMainsPhase = "syncMainsPhase";
    public static final String STRXML_Synchronism = "synchronism";
    public static final String STRXML_TorqueSetpoint = "torqueSetpoint";
    public static final String STRXML_PowerFactor = "powerFactor";
    public static final String STRXML_RealPower = "realPower";
    public static final String STRXML_ReactivePower = "reactivePower";
    public static final String STRXML_PhaseCurrent = "phaseCurrent";
    public static final String STRXML_ATTR_Name = "name";
    public static final String STRXML_ATTR_Units = "units";
    public static final String STRXML_ATTR_Format = "format";
}
