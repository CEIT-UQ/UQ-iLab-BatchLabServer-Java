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
    public static final String STRXML_SetupId_NoLoad = "NoLoad";
    public static final String STRXML_SetupId_FullLoad = "FullLoad";
    public static final String STRXML_SetupId_LockedRotor = "LockedRotor";
    public static final String STRXML_SetupId_SynchronousSpeed = "SynchronousSpeed";
    /*
     * XML elements in the results
     */
    public static final String STRXML_Voltage = "voltage";
    public static final String STRXML_Current = "current";
    public static final String STRXML_PowerFactor = "powerFactor";
    public static final String STRXML_Speed = "speed";
    public static final String STRXML_ATTR_Name = "name";
    public static final String STRXML_ATTR_Units = "units";
}
