/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class ConfigProperties {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ConfigProperties.class.getName();
    private static final Level logLevel = Level.INFO;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Filename_arg = "Filename: '%s'";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_Filename = "filename";
    private static final String STRERR_InputStream = "inputStream";
    private static final String STRERR_LabServer = "labServer";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String labServerName;
    private String labServerGuid;
    private String labServerPasskey;
    private boolean authenticating;
    private boolean logAuthentication;
    private String xmlEquipmentConfigPath;

    public boolean isAuthenticating() {
        return authenticating;
    }

    public String getLabServerGuid() {
        return labServerGuid;
    }

    public String getLabServerName() {
        return labServerName;
    }

    public String getLabServerPasskey() {
        return labServerPasskey;
    }

    public boolean isLogAuthentication() {
        return logAuthentication;
    }

    public String getXmlEquipmentConfigPath() {
        return xmlEquipmentConfigPath;
    }

    public void setXmlEquipmentConfigPath(String xmlEquipmentConfigPath) {
        this.xmlEquipmentConfigPath = xmlEquipmentConfigPath;
    }
    //</editor-fold>

    /**
     *
     * @param filename
     * @throws Exception
     */
    public ConfigProperties(String filename) throws Exception {
        final String methodName = "ConfigProperties";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Filename_arg, filename));

        try {
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
             * Load the configuration properties from the specified file
             */
            InputStream inputStream = new FileInputStream(filename);
            if (inputStream == null) {
                throw new NullPointerException(STRERR_InputStream);
            }
            Properties configProperties = new Properties();
            configProperties.loadFromXML(inputStream);

            /*
             * Get LabServer information and split into its parts
             */
            String labServer = configProperties.getProperty(LabConsts.STRCFG_LabServer);
            if (labServer == null) {
                throw new NullPointerException(STRERR_LabServer);
            }
            if (labServer.trim().isEmpty()) {
                throw new IllegalArgumentException(STRERR_LabServer);
            }
            String[] splitLabServer = labServer.split(LabConsts.STRCSV_SplitterChar);
            if (splitLabServer.length != LabConsts.LABSERVER_SIZE) {
                throw new IllegalArgumentException(STRERR_LabServer);
            }

            /*
             * Check that each part exists
             */
            for (int i = 0; i < LabConsts.LABSERVER_SIZE; i++) {
                splitLabServer[i] = splitLabServer[i].trim();
                if (splitLabServer[i].isEmpty()) {
                    throw new IllegalArgumentException(STRERR_LabServer);
                }
            }
            labServerName = splitLabServer[LabConsts.INDEX_LABSERVER_NAME];
            labServerGuid = splitLabServer[LabConsts.INDEX_LABSERVER_GUID];
            labServerPasskey = splitLabServer[LabConsts.INDEX_LABSERVER_PASSKEY];

            /*
             * Get ServiceBroker authentication
             */
            authenticating = Boolean.parseBoolean(configProperties.getProperty(LabConsts.STRCFG_Authenticating, Boolean.toString(true)));
            logAuthentication = Boolean.parseBoolean(configProperties.getProperty(LabConsts.STRCFG_LogAuthentication, Boolean.toString(false)));
        } catch (NullPointerException | IllegalArgumentException | IOException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
