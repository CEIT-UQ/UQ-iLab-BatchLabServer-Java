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
    public static final String STRXML_SetupId_LockedRotor = "LockedRotor";
    public static final String STRXML_SetupId_NoLoad = "NoLoad";
    public static final String STRXML_SetupId_SynchronousSpeed = "SynchronousSpeed";
    public static final String STRXML_SetupId_FullLoad = "FullLoad";
    /*
     * XML elements in the experiment result
     */
    public static final String STRXML_Voltage = "voltage";
    public static final String STRXML_Current = "current";
    public static final String STRXML_PowerFactor = "powerFactor";
    public static final String STRXML_Speed = "speed";
}
