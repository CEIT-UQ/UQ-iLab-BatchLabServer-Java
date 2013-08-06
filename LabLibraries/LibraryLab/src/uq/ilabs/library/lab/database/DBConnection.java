/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.lab.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class DBConnection {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    public static final String STR_ClassName = DBConnection.class.getName();
    private static final Level logLevel = Level.FINEST;
    /*
     * String constants
     */
    public static final String STR_User = "user";
    public static final String STR_Password = "password";
    public static final String STR_DefaultUser = "LabServer";
    public static final String STR_DefaultPassword = "ilab";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String driverName;
    private String url;
    private Properties properties;

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return this.properties.getProperty(STR_User);
    }

    public void setUser(String user) {
        this.properties.setProperty(STR_User, user);
    }

    public String getPassword() {
        return this.properties.getProperty(STR_Password);
    }

    public void setPassword(String password) {
        this.properties.setProperty(STR_Password, password);
    }
    //</editor-fold>

    /**
     *
     * @param database
     * @throws Exception
     */
    public DBConnection(String driverName, String url) throws Exception {
        final String methodName = "DBConnection";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Initial local variables
         */
        this.driverName = driverName;
        this.url = url;
        this.properties = new Properties();
        this.properties.setProperty(STR_User, STR_DefaultUser);
        this.properties.setProperty(STR_Password, STR_DefaultPassword);

        /*
         * Load the database driver to ensure that it exists
         */
        try {
            Class.forName(this.driverName);
        } catch (ClassNotFoundException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        final String methodName = "getConnection";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        Connection sqlConnection;
        try {
            sqlConnection = DriverManager.getConnection(this.url, this.properties);
        } catch (SQLException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return sqlConnection;
    }

    /**
     * Deregister the loaded driver
     */
    public void DeRegister() {
        try {
            Driver driver = DriverManager.getDriver(this.url);
            if (driver != null) {
                DriverManager.deregisterDriver(driver);
            }
        } catch (SQLException ex) {
            Logfile.WriteError(ex.toString());
        }
    }
}
