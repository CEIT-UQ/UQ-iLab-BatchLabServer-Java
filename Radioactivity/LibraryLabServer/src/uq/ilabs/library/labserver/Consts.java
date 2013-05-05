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
     * XML elements in the configuration
     */
    public static final String STRXML_Sources = "sources";
    public static final String STRXML_Absorbers = "absorbers";
    public static final String STRXML_ATTR_Default = "default";
    public static final String STRXML_Source = "source";
    public static final String STRXML_Absorber = "absorber";
//    public static final String STRXML_Name = "name";
    public static final String STRXML_Location = "location";
    /*
     * XML elements in the experiment specification
     */
    public static final String STRXML_SourceName = "sourceName";
    public static final String STRXML_AbsorberName = "absorberName";
    public static final String STRXML_Distance = "distance";
    public static final String STRXML_Duration = "duration";
    public static final String STRXML_Repeat = "repeat";
    /*
     * XML elements in the experiment validation
     */
    public static final String STRXML_VdnDistance = "vdnDistance";
    public static final String STRXML_VdnDuration = "vdnDuration";
    public static final String STRXML_VdnRepeat = "vdnRepeat";
    public static final String STRXML_VdnTotaltime = "vdnTotaltime";
    public static final String STRXML_Minimum = "minimum";
    public static final String STRXML_Maximum = "maximum";
    /*
     * XML elements in the experiment result
     */
    public static final String STRXML_DataType = "dataType";
    public static final String STRXML_DataVector = "dataVector";
    public static final String STRXML_ATTR_AbsorberName = "absorberName";
    public static final String STRXML_ATTR_Distance = "distance";
    public static final String STRXML_ATTR_Units = "units";
}
