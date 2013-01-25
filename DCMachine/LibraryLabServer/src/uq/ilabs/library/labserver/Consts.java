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
    public static final String STRXML_SetupId_VoltageVsSpeed = "VoltageVsSpeed";
    public static final String STRXML_SetupId_VoltageVsField = "VoltageVsField";
    public static final String STRXML_SetupId_VoltageVsLoad = "VoltageVsLoad";
    public static final String STRXML_SetupId_SpeedVsVoltage = "SpeedVsVoltage";
    public static final String STRXML_SetupId_SpeedVsField = "SpeedVsField";
    /*
     * XML elements in the specification
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
     * XML elements in the results
     */
    public static final String STRXML_SpeedVector = "speedVector";
    public static final String STRXML_FieldVector = "fieldVector";
    public static final String STRXML_VoltageVector = "voltageVector";
    public static final String STRXML_LoadVector = "loadVector";
    public static final String STRXML_ATTR_Name = "name";
    public static final String STRXML_ATTR_Units = "units";
}
