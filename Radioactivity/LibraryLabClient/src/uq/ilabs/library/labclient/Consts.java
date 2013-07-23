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
    public static final String STRXML_SetupId_RadioactivityVsTime = "RadioactivityVsTime";
    public static final String STRXML_SetupId_RadioactivityVsDistance = "RadioactivityVsDistance";
    public static final String STRXML_SetupId_RadioactivityVsAbsorber = "RadioactivityVsAbsorber";
    public static final String STRXML_SetupId_SimActivityVsTime = "SimActivityVsTime";
    public static final String STRXML_SetupId_SimActivityVsDistance = "SimActivityVsDistance";
    public static final String STRXML_SetupId_SimActivityVsAbsorber = "SimActivityVsAbsorber";
    public static final String STRXML_SetupId_SimActivityVsTimeNoDelay = "SimActivityVsTimeNoDelay";
    public static final String STRXML_SetupId_SimActivityVsDistanceNoDelay = "SimActivityVsDistanceNoDelay";
    public static final String STRXML_SetupId_SimActivityVsAbsorberNoDelay = "SimActivityVsAbsorberNoDelay";
    /*
     * XML Configuration
     */
    public static final String STRXML_Sources = "sources";
    public static final String STRXML_Absorbers = "absorbers";
    public static final String STRXML_ATTR_Default = "default";
    public static final String STRXML_Source = "source";
    public static final String STRXML_Absorber = "absorber";
//    public static final String STRXML_Name = "name";
    public static final String STRXML_Distances = "distances";
    public static final String STRXML_Minimum = "minimum";
    public static final String STRXML_Maximum = "maximum";
    public static final String STRXML_Stepsize = "stepsize";
    /*
     * XML elements in the validation
     */
    public static final String STRXML_ValidationDistance = "vdnDistance";
    public static final String STRXML_ValidationDuration = "vdnDuration";
    public static final String STRXML_ValidationRepeat = "vdnRepeat";
    public static final String STRXML_ValidationTotaltime = "vdnTotaltime";
    /*
     * XML elements in the experiment specification
     */
    public static final String STRXML_SourceName = "sourceName";
    public static final String STRXML_AbsorberName = "absorberName";
    public static final String STRXML_Distance = "distance";
    public static final String STRXML_Duration = "duration";
    public static final String STRXML_Repeat = "repeat";
    /*
     * XML elements in the experiment result
     */
    public static final String STRXML_DataType = "dataType";
    public static final String STRXML_DataVector = "dataVector";
}
