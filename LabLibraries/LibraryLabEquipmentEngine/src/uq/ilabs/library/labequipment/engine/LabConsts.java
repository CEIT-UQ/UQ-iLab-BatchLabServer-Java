/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.engine;

/**
 *
 * @author uqlpayne
 */
public class LabConsts {

    /*
     * Initialisation parameters
     */
    public static final String STRPRM_LogFilesPath = "LogFilesPath";
    public static final String STRPRM_LogLevel = "LogLevel";
    public static final String STRPRM_XmlConfigPropertiesPath = "XmlConfigPropertiesPath";
    public static final String STRPRM_XmlEquipmentConfigPath = "XmlEquipmentConfigPath";
    /*
     * String constants for configuration properties
     */
    public static final String STRCFG_Authenticating = "Authenticating";
    public static final String STRCFG_LogAuthentication = "LogAuthentication";
    public static final String STRCFG_LabServer = "LabServer";
    /*
     * Constants for LabServer CSV string information
     */
    public static final int LABSERVER_SIZE = 3;
    public static final int INDEX_LABSERVER_NAME = 0;
    public static final int INDEX_LABSERVER_GUID = 1;
    public static final int INDEX_LABSERVER_PASSKEY = 2;
    public static final String STRCSV_SplitterChar = ",";
    /*
     * XML elements in the equipment configuration file
     */
    public static final String STRXML_EquipmentConfig = "equipmentConfig";
    public static final String STRXML_ATTR_Title = "title";
    public static final String STRXML_ATTR_Version = "version";
    public static final String STRXML_PowerupDelay = "powerupDelay";
    public static final String STRXML_PowerdownTimeout = "powerdownTimeout";
    public static final String STRXML_Devices = "devices";
    public static final String STRXML_Device = "device";
    public static final String STRXML_ATTR_Name = "name";
    public static final String STRXML_InitialiseDelay = "initialiseDelay";
    public static final String STRXML_Drivers = "drivers";
    public static final String STRXML_Driver = "driver";
    public static final String STRXML_ExecutionTimes = "executionTimes";
    public static final String STRXML_Initialise = "initialise";
    public static final String STRXML_Start = "start";
    public static final String STRXML_Run = "run";
    public static final String STRXML_Stop = "stop";
    public static final String STRXML_Finalise = "finalise";
    public static final String STRXML_Validation = "validation";
    public static final String STRXML_Setups = "setups";
    public static final String STRXML_Setup = "setup";
    public static final String STRXML_ATTR_Id = "id";
    /*
     * XML elements for specification SetupIds
     */
    public static final String STRXML_SetupId_Generic = "Generic";
    /*
     * XML elements in the specification
     */
    public static final String STRXML_ExperimentSpecification = "experimentSpecification";
    public static final String STRXML_SetupId = "setupId";
    /*
     * XML elements in the results
     */
    public static final String STRXML_ExperimentResults = "experimentResults";
}
