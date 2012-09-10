/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.servicebroker.engine.types.LabServerInfo;

/**
 *
 * @author uqlpayne
 */
public class ConfigProperties {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ConfigProperties.class.getName();
    /*
     * String constants
     */
    private static final String STR_CsvSplitter = ",";
    private static final String STR_LabServer_arg = "LabServer%d";
    /*
     * String constants for configuration properties
     */
    private static final String STRCFG_ServiceBrokerGuid = "ServiceBrokerGuid";
    private static final String STRCFG_DBDatabase = "DBDatabase";
    private static final String STRCFG_DBHost = "DBHost";
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
    /*
     * String constants for exception messages
     */
    private static final String STRERR_Filename = "filename";
    private static final String STRERR_InputStream = "inputStream";
    private static final String STRERR_ServiceBrokerGuid = "serviceBrokerGuid";
    private static final String STRERR_DBDatabase = "dbDatabase";
    private static final String STRERR_DBHost = "dbHost";
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
    private String serviceBrokerGuid;
    private String dbHost;
    private String dbDatabase;
    private String dbUser;
    private String dbPassword;
    private boolean authenticating;
    private boolean logAuthentication;
    private long couponId;
    private String couponPasskey;
    private ArrayList<LabServerInfo> labServers;

    public String getServiceBrokerGuid() {
        return serviceBrokerGuid;
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbDatabase() {
        return dbDatabase;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
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
        final String STR_MethodName = "ConfigProperties";
        Logfile.WriteCalled(STR_ClassName, STR_MethodName,
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
            Properties properties = new Properties();
            properties.loadFromXML(inputStream);

            /*
             * Get the ServiceBroker Guid
             */
            this.serviceBrokerGuid = properties.getProperty(STRCFG_ServiceBrokerGuid);
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
             * Get the database information
             */
            this.dbHost = properties.getProperty(STRCFG_DBHost);
            if (this.dbHost.trim().isEmpty()) {
                throw new IllegalArgumentException(STRERR_DBHost);
            }
            this.dbDatabase = properties.getProperty(STRCFG_DBDatabase);
            if (this.dbDatabase.trim().isEmpty()) {
                throw new IllegalArgumentException(STRERR_DBDatabase);
            }
            this.dbUser = properties.getProperty(STRCFG_DBUser);
            this.dbUser = (this.dbUser.trim().isEmpty() == false) ? this.dbUser.trim() : null;
            this.dbPassword = properties.getProperty(STRCFG_DBPassword);
            this.dbPassword = (this.dbPassword.trim().isEmpty() == false) ? this.dbPassword.trim() : null;

            /*
             * Get LabClient authentication
             */
            this.authenticating = Boolean.parseBoolean(properties.getProperty(STRCFG_Authenticating, Boolean.toString(true)));
            this.logAuthentication = Boolean.parseBoolean(properties.getProperty(STRCFG_LogAuthentication, Boolean.toString(false)));

            /*
             * Get coupon Id and passkey for LabClient authentication
             */
            this.couponId = Long.parseLong(properties.getProperty(STRCFG_CouponId));
            this.couponPasskey = properties.getProperty(STRCFG_CouponPasskey);

            /*
             * Get the LabServer service information
             */
            this.labServers = new ArrayList<>();
            for (int i = 0; true; i++) {
                /*
                 * Get the LabServer info if it exists
                 */
                String csvLabServerInfo = properties.getProperty(String.format(STR_LabServer_arg, i));
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
                String outgoingPasskey = splitLabServerInfo[INDEX_LabServerOutPasskey];
                if (outgoingPasskey == null || outgoingPasskey.trim().isEmpty()) {
                    throw new NullPointerException(STRERR_LabServerOutPasskey);
                }
                outgoingPasskey = outgoingPasskey.trim();

                /*
                 * Extract the incoming passkey and check
                 */
                String incomingPasskey = splitLabServerInfo[INDEX_LabServerInPasskey];
                if (incomingPasskey == null || incomingPasskey.trim().isEmpty()) {
                    throw new NullPointerException(STRERR_LabServerInPasskey);
                }
                incomingPasskey = incomingPasskey.trim();

                /*
                 * Store information
                 */
                this.labServers.add(new LabServerInfo(guid, serviceUrl, outgoingPasskey, incomingPasskey));
            }
        } catch (NullPointerException | IllegalArgumentException | IOException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, STR_MethodName);
    }

    /**
     *
     * @param guid
     * @return LabServerInfo
     */
    public LabServerInfo GetLabServerInfo(String guid) {
        LabServerInfo labServerInfo = null;

        /*
         * Create an iterator to search for the specified guid
         */
        Iterator iterator = this.labServers.iterator();
        while (iterator.hasNext()) {
            /*
             * Get the next LabServer information and check guid
             */
            labServerInfo = (LabServerInfo) iterator.next();
            if (labServerInfo.getGuid().equalsIgnoreCase(guid) == true) {
                /*
                 * Found it
                 */
                break;
            }
        }

        return labServerInfo;
    }
}
