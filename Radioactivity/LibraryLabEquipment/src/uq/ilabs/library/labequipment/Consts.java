/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment;

import uq.ilabs.library.labequipment.engine.LabConsts;

/**
 *
 * @author uqlpayne
 */
public class Consts extends LabConsts {

    /*
     * XML elements in the EquipmentConfig.xml file
     */
    public static final String STRXML_Type = "type";
    public static final String STRXML_Simulation = "simulation";
    public static final String STRXML_Hardware = "hardware";
    public static final String STRXML_BoardId = "boardId";
    public static final String STRXML_Tube = "tube";
    public static final String STRXML_OffsetDistance = "offsetDistance";
    public static final String STRXML_HomeDistance = "homeDistance";
    public static final String STRXML_MoveRate = "moveRate";
    public static final String STRXML_Sources = "sources";
    public static final String STRXML_Source = "source";
    public static final String STRXML_Name = "name";
    public static final String STRXML_Location = "location";
    public static final String STRXML_Absorbers = "absorbers";
    public static final String STRXML_Absorber = "absorber";
    public static final String STRXML_SelectTimes = "selectTimes";
    public static final String STRXML_ReturnTimes = "returnTimes";
    public static final String STRXML_FirstLocation = "firstLocation";
    public static final String STRXML_HomeLocation = "homeLocation";
    public static final String STRXML_SimDistance = "distance";
    public static final String STRXML_SimDuration = "duration";
    public static final String STRXML_SimMean = "mean";
    public static final String STRXML_SimPower = "power";
    public static final String STRXML_SimDeviation = "deviation";
    public static final String STRXML_Network = "network";
    public static final String STRXML_IPaddr = "ipaddr";
    public static final String STRXML_Port = "port";
    public static final String STRXML_Serial = "serial";
    public static final String STRXML_Baud = "baud";
    public static final String STRXML_WriteLineTime = "writeLineTime";
    public static final String STRXML_RadiationCounter = "radiationCounter";
    public static final String STRXML_GeigerTubeVoltage = "geigerTubeVoltage";
    public static final String STRXML_SpeakerVolume = "speakerVolume";
    public static final String STRXML_TimeAdjustment = "timeAdjustment";
    public static final String STRXML_Capture = "capture";
    public static final String STRXML_TypeNone = "None";
    public static final String STRXML_TypeSimulation = "Simulation";
    public static final String STRXML_TypeHardware = "Hardware";
    public static final String STRXML_TypeSerial = "Serial";
    public static final String STRXML_TypeNetwork = "Network";
    /*
     * XML elements for specification SetupIds
     */
    public static final String STRXML_SetupId_RadioactivityVsTime = "RadioactivityVsTime";
    public static final String STRXML_SetupId_RadioactivityVsDistance = "RadioactivityVsDistance";
    public static final String STRXML_SetupId_SimActivityVsTime = "SimActivityVsTime";
    public static final String STRXML_SetupId_SimActivityVsDistance = "SimActivityVsDistance";
    public static final String STRXML_SetupId_SimActivityVsTimeNoDelay = "SimActivityVsTimeNoDelay";
    public static final String STRXML_SetupId_SimActivityVsDistanceNoDelay = "SimActivityVsDistanceNoDelay";
    /*
     * XML elements in the specification
     */
    public static final String STRXML_SourceName = "sourceName";
    public static final String STRXML_AbsorberName = "absorberName";
    public static final String STRXML_Distance = "distance";
    public static final String STRXML_Duration = "duration";
    public static final String STRXML_Repeat = "repeat";
    /*
     * XML elements in the validation
     */
    public static final String STRXML_VdnDistance = "distance";
    public static final String STRXML_VdnDuration = "duration";
    public static final String STRXML_VdnRepeat = "repeat";
    public static final String STRXML_VdnTotaltime = "totaltime";
    public static final String STRXML_VdnMinimum = "minimum";
    public static final String STRXML_VdnMaximum = "maximum";
    /*
     * XML elements in the results
     */
    public static final String STRXML_DataType = "dataType";
    public static final String STRXML_DataVector = "dataVector";
    public static final String STRXML_ATTR_Distance = "distance";
    public static final String STRXML_ATTR_Units = "units";
}
