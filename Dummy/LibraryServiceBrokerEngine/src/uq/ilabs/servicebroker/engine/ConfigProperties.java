/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.servicebroker.engine.types.LabServerInfo;

/**
 *
 * @author uqlpayne
 */
public class ConfigProperties {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ConfigProperties.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants
     */
    private static final String STR_CsvSplitter = ",";
    private static final String STR_LabServer_arg = "LabServer%d";
    /*
     * String constants for configuration properties
     */
    private static final String STRCFG_ServiceBrokerGuid = "ServiceBrokerGuid";
    private static final String STRCFG_DBDriver = "DBDriver";
    private static final String STRCFG_DBUrl = "DBUrl";
    private static final String STRCFG_DBUser = "DBUser";
    private static final String STRCFG_DBPassword = "DBPassword";
    private static final String STRCFG_Authenticating = "Authenticating";
    private static final String STRCFG_LogAuthentication = "LogAuthentication";
    private static final String STRCFG_CouponId = "CouponId";
    private static final String STRCFG_CouponPasskey = "CouponPasskey";
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Filename_arg = "filename: %s";
    private static final String STRLOG_LabServerInfo_arg5 = "LabServer %d - Guid: %s  ServiceUrl: %s  OutPasskey: %s  InPasskey: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_Filename = "filename";
    private static final String STRERR_ServiceBrokerGuid = "serviceBrokerGuid";
    private static final String STRERR_LabServerGuid = "guid";
    private static final String STRERR_LabServerServiceUrl = "serviceUrl";
    private static final String STRERR_LabServerOutPasskey = "outPasskey";
    private static final String STRERR_LabServerInPasskey = "inPasskey";
    /*
     * Constants
     */
    private static final int INDEX_LabServerGuid = 0;
    private static final int INDEX_LabServerUrl = 1;
    private static final int INDEX_LabServerOutPasskey = 2;
    private static final int INDEX_LabServerInPasskey = 3;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private DBConnection dbConnection;
    private String serviceBrokerGuid;
    private boolean authenticating;
    private boolean logAuthentication;
    private long couponId;
    private String couponPasskey;
    private ArrayList<LabServerInfo> labServers;

    public DBConnection getDbConnection() {
        return dbConnection;
    }

    public String getServiceBrokerGuid() {
        return serviceBrokerGuid;
    }

    public boolean isAuthenticating() {
        return authenticating;
    }

    public boolean isLogAuthentication() {
        return logAuthentication;
    }

    public long getCouponId() {
        return couponId;
    }

    public String getCouponPasskey() {
        return couponPasskey;
    }

    public ArrayList<LabServerInfo> getLabServers() {
        return labServers;
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
            Properties configProperties = new Properties();
            configProperties.loadFromXML(inputStream);

            /*
             * Get the database information
             */
            String dbDriver = configProperties.getProperty(STRCFG_DBDriver);
            if (dbDriver.trim().isEmpty()) {
                throw new IllegalArgumentException(STRCFG_DBDriver);
            }
            String dbUrl = configProperties.getProperty(STRCFG_DBUrl);
            if (dbUrl.trim().isEmpty()) {
                throw new IllegalArgumentException(STRCFG_DBUrl);
            }
            String dbUser = configProperties.getProperty(STRCFG_DBUser);
            dbUser = (dbUser.trim().isEmpty() == false) ? dbUser.trim() : null;
            String dbPassword = configProperties.getProperty(STRCFG_DBPassword);
            dbPassword = (dbPassword.trim().isEmpty() == false) ? dbPassword.trim() : null;

            /*
             * Create an instance of the database connection
             */
            this.dbConnection = new DBConnection(dbDriver, dbUrl);
            if (this.dbConnection == null) {
                throw new NullPointerException(DBConnection.class.getSimpleName());
            }
            this.dbConnection.setUser(dbUser);
            this.dbConnection.setPassword(dbPassword);

            /*
             * Get the ServiceBroker Guid
             */
            this.serviceBrokerGuid = configProperties.getProperty(STRCFG_ServiceBrokerGuid);
            if (this.serviceBrokerGuid == null) {
                /*
                 * The entry does not exist
                 */
                throw new NullPointerException(STRERR_ServiceBrokerGuid);
            }
            this.serviceBrokerGuid = this.serviceBrokerGuid.trim().toUpperCase();
            if (this.serviceBrokerGuid.isEmpty()) {
                /*
                 * The entry exists but the key is empty
                 */
                throw new IllegalArgumentException(STRERR_ServiceBrokerGuid);
            }

            /*
             * Get LabClient authentication
             */
            this.authenticating = Boolean.parseBoolean(configProperties.getProperty(STRCFG_Authenticating, Boolean.toString(true)));
            this.logAuthentication = Boolean.parseBoolean(configProperties.getProperty(STRCFG_LogAuthentication, Boolean.toString(false)));

            /*
             * Get coupon Id and passkey for LabClient authentication
             */
            this.couponId = Long.parseLong(configProperties.getProperty(STRCFG_CouponId));
            this.couponPasskey = configProperties.getProperty(STRCFG_CouponPasskey);

            /*
             * Get the LabServer service information
             */
            this.labServers = new ArrayList<>();
            for (int i = 0; true; i++) {
                /*
                 * Get the LabServer info if it exists
                 */
                String csvLabServerInfo = configProperties.getProperty(String.format(STR_LabServer_arg, i));
                if (csvLabServerInfo == null) {
                    break;
                }

                String[] splitLabServerInfo = csvLabServerInfo.split(STR_CsvSplitter);

                /*
                 * Extract guid and check
                 */
                String guid = splitLabServerInfo[INDEX_LabServerGuid];
                if (guid == null || guid.trim().isEmpty()) {
                    throw new NullPointerException(STRERR_LabServerGuid);
                }
                guid = guid.trim().toUpperCase();

                /*
                 * Extract service url and check
                 */
                String serviceUrl = splitLabServerInfo[INDEX_LabServerUrl];
                if (serviceUrl == null || serviceUrl.trim().isEmpty()) {
                    throw new NullPointerException(STRERR_LabServerServiceUrl);
                }
                serviceUrl = serviceUrl.trim();

                /*
                 * Extract the outgoing passkey and check
                 */
                String outPasskey = splitLabServerInfo[INDEX_LabServerOutPasskey];
                if (outPasskey == null || outPasskey.trim().isEmpty()) {
                    throw new NullPointerException(STRERR_LabServerOutPasskey);
                }
                outPasskey = outPasskey.trim();

                /*
                 * Extract the incoming passkey and check
                 */
                String inPasskey = splitLabServerInfo[INDEX_LabServerInPasskey];
                if (inPasskey == null || inPasskey.trim().isEmpty()) {
                    throw new NullPointerException(STRERR_LabServerInPasskey);
                }
                inPasskey = inPasskey.trim();

                /*
                 * Store information
                 */
                this.labServers.add(new LabServerInfo(guid, serviceUrl, outPasskey, inPasskey));

                Logfile.Write(String.format(STRLOG_LabServerInfo_arg5, i, guid, serviceUrl, outPasskey, inPasskey));
            }
        } catch (NullPointerException | IllegalArgumentException | IOException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param guid
     * @return LabServerInfo
     */
    public LabServerInfo GetLabServerInfo(String guid) {
        LabServerInfo labServerInfo = null;

        /*
         * Search for the specified guid
         */
        for (LabServerInfo _labServerInfo : this.labServers) {
            if (_labServerInfo.getGuid().equalsIgnoreCase(guid) == true) {
                /*
                 * Found it
                 */
                labServerInfo = _labServerInfo;
                break;
            }
        }

        return labServerInfo;
    }
}
