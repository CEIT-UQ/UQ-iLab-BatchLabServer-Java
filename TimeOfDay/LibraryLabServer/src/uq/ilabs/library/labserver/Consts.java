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
    public static final String STRXML_SetupId_LocalClock = "LocalClock";
    public static final String STRXML_SetupId_NTPServer = "NTPServer";
    /*
     * XML elements in the specification
     */
    public static final String STRXML_FormatName = "formatName";
    public static final String STRXML_ServerUrl = "serverUrl";
    /*
     * XML elements in the results
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
