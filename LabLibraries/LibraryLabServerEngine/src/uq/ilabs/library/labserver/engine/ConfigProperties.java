/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.engine.types.LabEquipmentServiceInfo;

/**
 *
 * @author uqlpayne
 */
public class ConfigProperties {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ConfigProperties.class.getName();
    private static final Level logLevel = Level.INFO;
    /*
     * Strings
     */
    private static final String STR_CsvSplitter = ",";
    private static final String STR_LabEquipmentService_arg = "LabEquipmentService%d";
    /*
     * String constants for configuration properties
     */
    private static final String STRCFG_LabServerGuid = "LabServerGuid";
    private static final String STRCFG_DBDatabase = "DBDatabase";
    private static final String STRCFG_DBHost = "DBHost";
    private static final String STRCFG_DBUser = "DBUser";
    private static final String STRCFG_DBPassword = "DBPassword";
    private static final String STRCFG_Authenticating = "Authenticating";
    private static final String STRCFG_LogAuthentication = "LogAuthentication";
    private static final String STRCFG_ContactEmail = "ContactEmail";
    private static final String STRCFG_CompletedEmailList = "CompletedEmailList";
    private static final String STRCFG_FailedEmailList = "FailedEmailList";
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Filename_arg = "filename: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_Filename = "filename";
    private static final String STRERR_InputStream = "inputStream";
    private static final String STRERR_LabServerGuid = "labServerGuid";
    private static final String STRERR_DBDatabase = "dbDatabase";
    private static final String STRERR_DBHost = "dbHost";
    private static final String STRERR_ServiceUrl = "serviceUrl";
    private static final String STRERR_Passkey = "passkey";
    /*
     * Constants
     */
    private static final int INDEX_ServiceUrl = 0;
    private static final int INDEX_Passkey = 1;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String xmlLabConfigurationPath;
    private String labServerGuid;
    private String dbHost;
    private String dbDatabase;
    private String dbUser;
    private String dbPassword;
    private boolean authenticating;
    private boolean logAuthentication;
    private String contactEmail;
    private String[] completedEmailList;
    private String[] failedEmailList;
    private LabEquipmentServiceInfo[] labEquipmentServiceInfo;

    public boolean isAuthenticating() {
        return authenticating;
    }

    public String[] getCompletedEmailList() {
        return completedEmailList;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getDbDatabase() {
        return dbDatabase;
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String[] getFailedEmailList() {
        return failedEmailList;
    }

    public LabEquipmentServiceInfo[] getLabEquipmentServiceInfo() {
        return labEquipmentServiceInfo;
    }

    public String getLabServerGuid() {
        return labServerGuid;
    }

    public boolean isLogAuthentication() {
        return logAuthentication;
    }

    public String getXmlLabConfigurationPath() {
        return xmlLabConfigurationPath;
    }

    public void setXmlLabConfigurationPath(String xmlLabConfigurationPath) {
        this.xmlLabConfigurationPath = xmlLabConfigurationPath;
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
             * Get the LabServer Guid
             */
            this.labServerGuid = configProperties.getProperty(STRCFG_LabServerGuid);
            if (labServerGuid == null) {
                /*
                 * The entry does not exist
                 */
                throw new NullPointerException(STRERR_LabServerGuid);
            }
            this.labServerGuid = this.labServerGuid.trim().toUpperCase();
            if (this.labServerGuid.isEmpty()) {
                /*
                 * The entry exists but the key is empty
                 */
                throw new IllegalArgumentException(STRERR_LabServerGuid);
            }

            /*
             * Get the database information
             */
            this.dbHost = configProperties.getProperty(STRCFG_DBHost);
            if (this.dbHost.trim().isEmpty()) {
                throw new IllegalArgumentException(STRERR_DBHost);
            }
            this.dbDatabase = configProperties.getProperty(STRCFG_DBDatabase);
            if (this.dbDatabase.trim().isEmpty()) {
                throw new IllegalArgumentException(STRERR_DBDatabase);
            }
            this.dbUser = configProperties.getProperty(STRCFG_DBUser);
            this.dbUser = (this.dbUser.trim().isEmpty() == false) ? this.dbUser.trim() : null;
            this.dbPassword = configProperties.getProperty(STRCFG_DBPassword);
            this.dbPassword = (this.dbPassword.trim().isEmpty() == false) ? this.dbPassword.trim() : null;

            /*
             * Get ServiceBroker authentication
             */
            this.authenticating = Boolean.parseBoolean(configProperties.getProperty(STRCFG_Authenticating, Boolean.toString(true)));
            this.logAuthentication = Boolean.parseBoolean(configProperties.getProperty(STRCFG_LogAuthentication, Boolean.toString(false)));

            /*
             * Get contact email address i.e., Administrator's email address
             */
            this.contactEmail = configProperties.getProperty(STRCFG_ContactEmail);

            /*
             * Get list of email addresses for when the experiment completes successfully
             */
            String csvString = configProperties.getProperty(STRCFG_CompletedEmailList);
            if (csvString != null) {
                this.completedEmailList = csvString.split(STR_CsvSplitter);
                for (int i = 0; i < this.completedEmailList.length; i++) {
                    this.completedEmailList[i] = this.completedEmailList[i].trim();
                }
            }

            /*
             * Get list of email addresses for when the experiment fails
             */
            csvString = configProperties.getProperty(STRCFG_FailedEmailList);
            if (csvString != null) {
                this.failedEmailList = csvString.split(STR_CsvSplitter);
                for (int i = 0; i < this.failedEmailList.length; i++) {
                    this.failedEmailList[i] = this.failedEmailList[i].trim();
                }
            }

            /*
             * Get the lab equipment service information for each of the farm units starting at 0
             */
            ArrayList<LabEquipmentServiceInfo> serviceInfo = new ArrayList<>();
            for (int i = 0; true; i++) {
                /*
                 * Get the labequipment service info if it exists
                 */
                String csvServiceInfo = configProperties.getProperty(String.format(STR_LabEquipmentService_arg, i));
                if (csvServiceInfo == null) {
                    break;
                }

                String[] splitService = csvServiceInfo.split(STR_CsvSplitter);

                /*
                 * Extract the service url and check
                 */
                String serviceUrl = splitService[INDEX_ServiceUrl];
                if (serviceUrl == null || serviceUrl.trim().isEmpty()) {
                    throw new NullPointerException(STRERR_ServiceUrl);
                }
                serviceUrl = serviceUrl.trim();

                /*
                 * Extract the passkey and check
                 */
                String passkey = splitService[INDEX_Passkey];
                if (passkey == null || passkey.trim().isEmpty()) {
                    throw new NullPointerException(STRERR_Passkey);
                }
                passkey = passkey.trim();

                /*
                 * Store information
                 */
                serviceInfo.add(new LabEquipmentServiceInfo(serviceUrl, this.labServerGuid, passkey));
            }

            /*
             * Convert list to an array and save
             */
            this.labEquipmentServiceInfo = serviceInfo.toArray(new LabEquipmentServiceInfo[0]);
        } catch (NullPointerException | IllegalArgumentException | IOException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
