/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class ConfigProperties {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ConfigProperties.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for configuration properties
     */
    private static final String STRCFG_DBDatabase = "DBDatabase";
    private static final String STRCFG_DBHost = "DBHost";
    private static final String STRCFG_DBUser = "DBUser";
    private static final String STRCFG_DBPassword = "DBPassword";
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Filename_arg = "Filename: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_Filename = "filename";
    private static final String STRERR_InputStream = "inputStream";
    private static final String STRERR_DBDatabase = "dbDatabase";
    private static final String STRERR_DBHost = "dbHost";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private DBConnection dbConnection;
    private String xmlLabConfigurationPath;
    private String xmlSimulationConfigPath;
    private boolean authenticating;
    private boolean logAuthentication;

    public DBConnection getDbConnection() {
        return dbConnection;
    }

    public String getXmlLabConfigurationPath() {
        return xmlLabConfigurationPath;
    }

    public void setXmlLabConfigurationPath(String xmlLabConfigurationPath) {
        this.xmlLabConfigurationPath = xmlLabConfigurationPath;
    }

    public String getXmlSimulationConfigPath() {
        return xmlSimulationConfigPath;
    }

    public void setXmlSimulationConfigPath(String xmlSimulationConfigPath) {
        this.xmlSimulationConfigPath = xmlSimulationConfigPath;
    }

    public boolean isAuthenticating() {
        return authenticating;
    }

    public void setAuthenticating(boolean authenticating) {
        this.authenticating = authenticating;
    }

    public boolean isLogAuthentication() {
        return logAuthentication;
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
             * Get the database information
             */
            String dbHost = configProperties.getProperty(STRCFG_DBHost);
            if (dbHost.trim().isEmpty()) {
                throw new IllegalArgumentException(STRERR_DBHost);
            }
            String dbDatabase = configProperties.getProperty(STRCFG_DBDatabase);
            if (dbDatabase.trim().isEmpty()) {
                throw new IllegalArgumentException(STRERR_DBDatabase);
            }
            String dbUser = configProperties.getProperty(STRCFG_DBUser);
            dbUser = (dbUser.trim().isEmpty() == false) ? dbUser.trim() : null;
            String dbPassword = configProperties.getProperty(STRCFG_DBPassword);
            dbPassword = (dbPassword.trim().isEmpty() == false) ? dbPassword.trim() : null;

            /*
             * Create an instance of the database connection
             */
            this.dbConnection = new DBConnection(dbDatabase);
            if (this.dbConnection == null) {
                throw new NullPointerException(DBConnection.class.getSimpleName());
            }
            this.dbConnection.setHost(dbHost);
            this.dbConnection.setUser(dbUser);
            this.dbConnection.setPassword(dbPassword);

        } catch (NullPointerException | IllegalArgumentException | IOException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
