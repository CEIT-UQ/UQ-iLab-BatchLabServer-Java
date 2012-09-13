/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;

/**
 *
 * @author uqlpayne
 */
public class LabEquipmentConfiguration {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentConfiguration.class.getName();
    private static final Level logLevel = Level.CONFIG;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Filename_arg = "Filename: '%s'";
    private static final String STRLOG_ParsingEquipmentConfiguration = "Parsing equipment configuration...";
    private static final String STRLOG_Title_arg = "Title: '%s'";
    private static final String STRLOG_Version_arg = "Version: '%s'";
    private static final String STRLOG_PowerupDelay_arg = "Powerup Delay: %d secs";
    private static final String STRLOG_PowerdownTimeout_arg = "Powerdown Timeout: %d secs";
    private static final String STRLOG_PoweroffDelay_arg = "Poweroff Delay: %d secs";
    private static final String STRLOG_PowerdownDisabled = "Powerdown disabled";
    private static final String STRLOG_DeviceName_arg = "Device: %s";
    private static final String STRLOG_InitialiseDelay_arg = "Initialise Delay: %d secs";
    private static final String STRLOG_DriverName_arg = "Driver: %s";
    private static final String STRLOG_SetupId_arg = "Setup Id: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_Filename = "filename";
    private static final String STRERR_NumberIsNegative = "Number cannot be negative!";
    /*
     * Constants
     */
    /**
     * Time in seconds to wait after the equipment is powered up if not already specified.
     */
    private static final int DELAY_SECS_PowerupDefault = 5;
    /**
     * Minimum time in seconds to wait to power up the equipment after it has been powered down.
     */
    private static final int DELAY_SECS_PoweroffMinimum = 5;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private HashMap<String, String> mapDevices;
    private HashMap<String, String> mapDrivers;
    private HashMap<String, String> mapSetups;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String filename;
    private String title;
    private String version;
    private int powerupDelay;
    private int powerdownTimeout;
    private int poweroffDelay;
    private boolean powerdownEnabled;
    private int initialiseDelay;
    protected String xmlStringDevices;
    protected String xmlStringDrivers;
    protected String xmlStringSetups;

    public String getFilename() {
        return filename;
    }

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public int getPowerupDelay() {
        return powerupDelay;
    }

    public int getPowerdownTimeout() {
        return powerdownTimeout;
    }

    public int getPoweroffDelay() {
        return poweroffDelay;
    }

    public boolean isPowerdownEnabled() {
        return powerdownEnabled;
    }

    public int getInitialiseDelay() {
        return initialiseDelay;
    }

    public String getXmlStringDevices() {
        return xmlStringDevices;
    }

//    public String getXmlStringDrivers() {
//        return xmlStringDrivers;
//    }
//
//    public String getXmlStringSetups() {
//        return xmlStringSetups;
//    }
    //</editor-fold>
    /**
     * Constructor - Parse the equipment configuration XML string for information specific to the LabEquipment.
     *
     * @param xmlEquipmentConfiguration The string containing the XML to parse.
     */
    public LabEquipmentConfiguration(String xmlEquipmentConfiguration) throws Exception {
        this(null, null, xmlEquipmentConfiguration);
    }

    /**
     * Constructor - Parse the equipment configuration XML file for information specific to the LabEquipment.
     *
     * @param filepath Path to the lab equipment configuration XML file which may be null.
     * @param filename Name of the lab equipment configuration XML file which may include the path.
     */
    public LabEquipmentConfiguration(String filepath, String filename) throws Exception {
        this(filepath, filename, null);
    }

    /**
     * Constructor - Parse the equipment configuration for information specific to the LabEquipment.
     *
     * @param filepath Path to the lab equipment configuration XML file which may be null.
     * @param filename Name of the lab equipment configuration XML file which may include the path.
     * @param xmlEquipmentConfiguration The string containing the XML to parse.
     */
    public LabEquipmentConfiguration(String filepath, String filename, String xmlEquipmentConfiguration) throws Exception {
        final String methodName = "LabEquipmentConfiguration";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String logMessage = Logfile.STRLOG_Newline;

        try {
            Document xmlDocument;
            if (xmlEquipmentConfiguration != null) {
                /*
                 * Get the equipment configuration from the XML string
                 */
                xmlDocument = XmlUtilities.GetDocumentFromString(xmlEquipmentConfiguration);
            } else {
                /*
                 * Check that parameters are valid
                 */
                if (filename == null) {
                    throw new NullPointerException(STRERR_Filename);
                }
                if (filename.trim().isEmpty()) {
                    throw new IllegalArgumentException(STRERR_Filename);
                }

                /*
                 * Combine the file path and name and check if the file exists
                 */
                File file = new File(filepath, filename);
                if (file.exists() == false) {
                    throw new FileNotFoundException(file.getAbsolutePath());
                }

                this.filename = file.getAbsolutePath();
                logMessage += String.format(STRLOG_Filename_arg, this.filename) + Logfile.STRLOG_Newline;

                /*
                 * Load the equipment configuration from the file
                 */
                xmlDocument = XmlUtilities.GetDocumentFromFile(null, this.filename);
            }

            /*
             * Get the document's root node
             */
            Node xmlNodeEquipmentConfiguration = XmlUtilities.GetRootNode(xmlDocument, LabConsts.STRXML_EquipmentConfig);

            logMessage += STRLOG_ParsingEquipmentConfiguration + Logfile.STRLOG_Newline;

            /*
             * Get information from the equipment configuration node
             */
            this.title = XmlUtilities.GetAttribute(xmlNodeEquipmentConfiguration, LabConsts.STRXML_ATTR_Title, false);
            this.version = XmlUtilities.GetAttribute(xmlNodeEquipmentConfiguration, LabConsts.STRXML_ATTR_Version, false);
            logMessage += String.format(STRLOG_Title_arg, this.title) + Logfile.STRLOG_Newline
                    + String.format(STRLOG_Version_arg, this.version) + Logfile.STRLOG_Newline;

            /*
             * Get powerup delay, may not be specified
             */
            try {
                this.powerupDelay = XmlUtilities.GetChildValueAsInt(xmlNodeEquipmentConfiguration, LabConsts.STRXML_PowerupDelay);
                if (this.powerupDelay < 0) {
                    throw new ArithmeticException(STRERR_NumberIsNegative);
                }
            } catch (XmlUtilitiesException ex) {
                /*
                 * Powerup delay is not specified, use the default
                 */
                this.powerupDelay = DELAY_SECS_PowerupDefault;
            }

            /*
             * Get powerdown timeout, may not be specified
             */
            try {
                this.powerdownTimeout = XmlUtilities.GetChildValueAsInt(xmlNodeEquipmentConfiguration, LabConsts.STRXML_PowerdownTimeout);
                if (this.powerdownTimeout < 0) {
                    throw new ArithmeticException(STRERR_NumberIsNegative);
                }

                /*
                 * Powerdown timeout is specified so enable powerdown
                 */
                this.powerdownEnabled = true;
                this.poweroffDelay = DELAY_SECS_PoweroffMinimum;
            } catch (XmlUtilitiesException ex) {
                /*
                 * Powerdown timeout is not specified, disable powerdown
                 */
                this.powerdownEnabled = false;
                this.poweroffDelay = 0;
            }

            /*
             * Log details
             */
            logMessage += String.format(STRLOG_PowerupDelay_arg, this.powerupDelay) + Logfile.STRLOG_Newline;

            if (this.powerdownEnabled == true) {
                logMessage += String.format(STRLOG_PowerdownTimeout_arg, this.powerdownTimeout) + Logfile.STRLOG_Newline;
                logMessage += String.format(STRLOG_PoweroffDelay_arg, this.poweroffDelay) + Logfile.STRLOG_Newline;
            } else {
                logMessage += STRLOG_PowerdownDisabled + Logfile.STRLOG_Newline;
            }

            /*
             * Get the device nodes and accumulate the initialise delay
             */
            this.initialiseDelay = 0;
            this.mapDevices = new HashMap<>();
            Node xmlNodeDevices = XmlUtilities.GetChildNode(xmlNodeEquipmentConfiguration, LabConsts.STRXML_Devices);
            this.xmlStringDevices = XmlUtilities.ToXmlString(xmlNodeDevices);
            ArrayList xmlNodeList = XmlUtilities.GetChildNodeList(xmlNodeDevices, LabConsts.STRXML_Device, false);
            for (int i = 0; i < xmlNodeList.size(); i++) {
                Node xmlNodeDevice = (Node) xmlNodeList.get(i);

                /*
                 * Check that the required device information exists
                 */
                String name = XmlUtilities.GetAttribute(xmlNodeDevice, LabConsts.STRXML_ATTR_Name, false);
                logMessage += String.format(STRLOG_DeviceName_arg, name) + Logfile.STRLOG_Spacer;

                /*
                 * Get the initialise delay and add to total
                 */
                int initialiseDelayDevice = XmlUtilities.GetChildValueAsInt(xmlNodeDevice, LabConsts.STRXML_InitialiseDelay);
                this.initialiseDelay += initialiseDelayDevice;
                logMessage += String.format(STRLOG_InitialiseDelay_arg, initialiseDelayDevice) + Logfile.STRLOG_Newline;

                /*
                 * Add device XML to map
                 */
                String xmlDevice = XmlUtilities.ToXmlString(xmlNodeDevice);
                this.mapDevices.put(name, xmlDevice);
            }

            /*
             * Get the driver nodes
             */
            this.mapDrivers = new HashMap<>();
            Node xmlNodeDrivers = XmlUtilities.GetChildNode(xmlNodeEquipmentConfiguration, LabConsts.STRXML_Drivers);
            this.xmlStringDrivers = XmlUtilities.ToXmlString(xmlNodeDrivers);
            xmlNodeList = XmlUtilities.GetChildNodeList(xmlNodeDrivers, LabConsts.STRXML_Driver, false);
            for (int i = 0; i < xmlNodeList.size(); i++) {
                Node xmlNodeDriver = (Node) xmlNodeList.get(i);

                /*
                 * Check that the required driver information exists
                 */
                String name = XmlUtilities.GetAttribute(xmlNodeDriver, LabConsts.STRXML_ATTR_Name, false);
                logMessage += String.format(STRLOG_DriverName_arg, name) + Logfile.STRLOG_Newline;

                /*
                 * Add driver XML to map
                 */
                String xmlDriver = XmlUtilities.ToXmlString(xmlNodeDriver);
                this.mapDrivers.put(name, xmlDriver);
            }

            /*
             * Get the setup nodes
             */
            this.mapSetups = new HashMap<>();
            Node xmlNodeSetups = XmlUtilities.GetChildNode(xmlNodeEquipmentConfiguration, LabConsts.STRXML_Setups);
            this.xmlStringSetups = XmlUtilities.ToXmlString(xmlNodeSetups);
            xmlNodeList = XmlUtilities.GetChildNodeList(xmlNodeSetups, LabConsts.STRXML_Setup, false);
            for (int i = 0; i < xmlNodeList.size(); i++) {
                Node xmlNodeSetup = (Node) xmlNodeList.get(i);

                /*
                 * Get the setup id
                 */
                String id = XmlUtilities.GetAttribute(xmlNodeSetup, LabConsts.STRXML_ATTR_Id, false);
                logMessage += String.format(STRLOG_SetupId_arg, id) + Logfile.STRLOG_Newline;

                /*
                 * Get the driver and add to map
                 */
                String strDriver = XmlUtilities.GetChildValue(xmlNodeSetup, LabConsts.STRXML_Driver);
                this.mapSetups.put(id, strDriver);
            }

            Logfile.Write(logLevel, logMessage);
        } catch (XmlUtilitiesException | NullPointerException | IllegalArgumentException | FileNotFoundException | ArithmeticException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param setupId
     * @return
     */
    public String GetDriverName(String setupId) {
        return this.mapSetups.get(setupId);
    }

    /**
     *
     * @param deviceName
     * @return
     */
    public String GetXmlDeviceConfiguration(String deviceName) {
        return this.mapDevices.get(deviceName);
    }

    /**
     *
     * @param driverName
     * @return
     */
    public String GetXmlDriverConfiguration(String driverName) {
        return this.mapDrivers.get(driverName);
    }
}
