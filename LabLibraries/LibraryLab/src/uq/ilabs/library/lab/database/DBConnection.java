/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.lab.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class DBConnection {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    public static final String STR_ClassName = DBConnection.class.getName();
    /*
     * String constants
     */
    public static final String STR_Driver = "org.postgresql.Driver";
    public static final String STR_Url_arg3 = "jdbc:postgresql://%s:%d/%s";
    public static final String STR_User = "user";
    public static final String STR_Password = "password";
    public static final String STR_DefaultHost = "localhost";
    public static final String STR_DefaultUser = "LabServer";
    public static final String STR_DefaultPassword = "ilab";
    /*
     * Constants
     */
    public static final int INT_DefaultPort = 5432;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private Properties properties;
    private String host;
    private int port;
    private String database;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
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

    public String getUrl() {
        return String.format(STR_Url_arg3, host, port, database);
    }
    //</editor-fold>

    /**
     *
     * @param database
     * @throws Exception
     */
    public DBConnection(String database) throws Exception {
        final String methodName = "DBConnection";
        Logfile.WriteCalled(STR_ClassName, methodName);

        /*
         * Initial local variables
         */
        this.database = database;
        this.host = STR_DefaultHost;
        this.port = INT_DefaultPort;
        this.properties = new Properties();
        this.properties.setProperty(STR_User, STR_DefaultUser);
        this.properties.setProperty(STR_Password, STR_DefaultPassword);

        /*
         * Load the database driver to ensure that it exists
         */
        try {
            Class.forName(STR_Driver);
        } catch (ClassNotFoundException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }

    /**
     *
     * @return @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        final String methodName = "getConnection";
        Logfile.WriteCalled(STR_ClassName, methodName);

        Connection sqlConnection;
        try {
            sqlConnection = DriverManager.getConnection(this.getUrl(), this.properties);
        } catch (SQLException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        return sqlConnection;
    }
}
