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
     * Initialisation parameters
     */
    public static final String STRPRM_XmlSimulationConfigPath = "XmlSimulationConfigPath";
    /*
     * XML elements in the LabConfiguration.xml file
     */
    public static final String STRXML_SetupId_SimActivityVsTime = "SimActivityVsTime";
    public static final String STRXML_SetupId_SimActivityVsDistance = "SimActivityVsDistance";
    public static final String STRXML_SetupId_SimActivityVsTimeNoDelay = "SimActivityVsTimeNoDelay";
    public static final String STRXML_SetupId_SimActivityVsDistanceNoDelay = "SimActivityVsDistanceNoDelay";
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
    public static final String STRXML_ATTR_Distance = "distance";
    public static final String STRXML_ATTR_Units = "units";
    /*
     * XML elements in the SimulationConfig.xml file
     */
    public static final String STRXML_SimulationConfig = "simulationConfig";
    public static final String STRXML_ATTR_title = "title";
    public static final String STRXML_ATTR_version = "version";
    public static final String STRXML_SimDistance = "distance";
    public static final String STRXML_SimDuration = "duration";
    public static final String STRXML_SimMean = "mean";
    public static final String STRXML_SimPower = "power";
    public static final String STRXML_SimDeviation = "deviation";
    public static final String STRXML_Tube = "tube";
    public static final String STRXML_OffsetDistance = "offsetDistance";
    public static final String STRXML_HomeDistance = "homeDistance";
    public static final String STRXML_MoveRate = "moveRate";
//    public static final String STRXML_Sources = "sources";
//    public static final String STRXML_Absorbers = "absorbers";
    public static final String STRXML_SelectTimes = "selectTimes";
    public static final String STRXML_ReturnTimes = "returnTimes";
    public static final String STRXML_FirstLocation = "firstLocation";
    public static final String STRXML_HomeLocation = "homeLocation";
}
