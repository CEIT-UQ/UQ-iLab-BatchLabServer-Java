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
     * XML elements for specification SetupIds
     */
    public static final String STRXML_SetupId_RadioactivityVsTime = "RadioactivityVsTime";
    public static final String STRXML_SetupId_RadioactivityVsDistance = "RadioactivityVsDistance";
    /*
     * XML elements in the specification
     */
    public static final String STRXML_SourceName = "sourceName";
    public static final String STRXML_AbsorberName = "absorberName";
    public static final String STRXML_Distance = "distance";
    public static final String STRXML_Duration = "duration";
    public static final String STRXML_Repeat = "repeat";
    /*
     * XML elements in the results
     */
    public static final String STRXML_DataType = "dataType";
    public static final String STRXML_DataVector = "dataVector";
    public static final String STRXML_ATTR_Distance = "distance";
    public static final String STRXML_ATTR_Units = "units";
    /*
     * XML elements in the EquipmentConfig.xml file
     */
    public static final String STRXML_Simulation = "simulation";
    public static final String STRXML_SimDistance = "distance";
    public static final String STRXML_SimDuration = "duration";
    public static final String STRXML_SimMean = "mean";
    public static final String STRXML_SimPower = "power";
    public static final String STRXML_SimDeviation = "deviation";
    public static final String STRXML_Tube = "tube";
    public static final String STRXML_OffsetDistance = "offsetDistance";
    public static final String STRXML_HomeDistance = "homeDistance";
    public static final String STRXML_MoveRate = "moveRate";
    public static final String STRXML_Sources = "sources";
    public static final String STRXML_Absorbers = "absorbers";
    public static final String STRXML_SelectTimes = "selectTimes";
    public static final String STRXML_ReturnTimes = "returnTimes";
    public static final String STRXML_FirstLocation = "firstLocation";
    public static final String STRXML_HomeLocation = "homeLocation";
}
