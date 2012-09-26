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
    public static final String STRXML_SetupId_LocalClock = "LocalClock";
    public static final String STRXML_SetupId_NTPServer = "NTPServer";
    /*
     * XML elements in the LabConfiguration
     */
    public static final String STRXML_TimeServers = "timeServers";
    public static final String STRXML_TimeServerUrl = "url";
    public static final String STRXML_TimeFormats = "timeFormats";
    public static final String STRXML_TimeFormat = "timeFormat";
    public static final String STRXML_ATTR_Default = "default";
    /*
     * XML elements in the experiment specification
     */
    public static final String STRXML_ServerUrl = "serverUrl";
    public static final String STRXML_FormatName = "formatName";
    /*
     * XML elements in the experiment result
     */
    public static final String STRXML_Timeofday = "timeofday";
    public static final String STRXML_Dayofweek = "dayofweek";
    public static final String STRXML_Day = "day";
    public static final String STRXML_Month = "month";
    public static final String STRXML_Year = "year";
    public static final String STRXML_Hours = "hours";
    public static final String STRXML_Minutes = "minutes";
    public static final String STRXML_Seconds = "seconds";
}
