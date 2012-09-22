/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient;

import uq.ilabs.library.labclient.engine.LabConsts;

/**
 *
 * @author uqlpayne
 */
public class Consts extends LabConsts {

    /*
     * String constants for XML setup Ids
     */
    public static final String STRXML_SetupId_VoltageVsSpeed = "VoltageVsSpeed";
    public static final String STRXML_SetupId_VoltageVsField = "VoltageVsField";
    public static final String STRXML_SetupId_VoltageVsLoad = "VoltageVsLoad";
    public static final String STRXML_SetupId_SpeedVsVoltage = "SpeedVsVoltage";
    public static final String STRXML_SetupId_SpeedVsField = "SpeedVsField";
    /*
     * XML elements in the LabConfiguration
     */
    public static final String STRXML_ParamsTitle = "paramsTitle";
    /*
     * XML elements in the validation
     */
    public static final String STRXML_ValidationSpeed = "vdnSpeed";
    public static final String STRXML_ValidationField = "vdnField";
    public static final String STRXML_ValidationLoad = "vdnLoad";
    public static final String STRXML_ValidationMinimum = "minimum";
    public static final String STRXML_ValidationMaximum = "maximum";
    public static final String STRXML_ValidationStepMin = "stepMin";
    public static final String STRXML_ValidationStepMax = "stepMax";
    /*
     * XML elements in the experiment specification
     */
    public static final String STRXML_SpeedMin = "speedMin";
    public static final String STRXML_SpeedMax = "speedMax";
    public static final String STRXML_SpeedStep = "speedStep";
    public static final String STRXML_FieldMin = "fieldMin";
    public static final String STRXML_FieldMax = "fieldMax";
    public static final String STRXML_FieldStep = "fieldStep";
    public static final String STRXML_LoadMin = "loadMin";
    public static final String STRXML_LoadMax = "loadMax";
    public static final String STRXML_LoadStep = "loadStep";
    /*
     * XML elements in the experiment result
     */
    public static final String STRXML_SpeedVector = "speedVector";
    public static final String STRXML_FieldVector = "fieldVector";
    public static final String STRXML_VoltageVector = "voltageVector";
    public static final String STRXML_LoadVector = "loadVector";
}
