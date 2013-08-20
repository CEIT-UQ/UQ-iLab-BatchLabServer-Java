/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.lab.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;
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
     * String constants for exception messages
     */
    private static final String STRERR_DriverName = "driverName";
    private static final String STRERR_Url = "url";
    private static final String STRERR_User = "user";
    private static final String STRERR_Password = "password";
    /*
     * String constants
     */
    public static final String STR_User = "user";
    public static final String STR_Password = "password";
    public static final String STR_DefaultUser = "LabServer";
    public static final String STR_DefaultPassword = "ilab";
    /*
     * Constants
     */
    private static final int INT_DefaultPoolSize = 1;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Semaphore connections;
    private final List<Connection> connectionList;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String driverName;
    private String url;
    private int poolsize;
    private Properties properties;

    public String getDriverName() {
        return driverName;
    }

    public String getUrl() {
        return url;
    }

    public int getPoolsize() {
        return poolsize;
    }

    public String getUser() {
        return this.properties.getProperty(STR_User);
    }

    public String getPassword() {
        return this.properties.getProperty(STR_Password);
    }
    //</editor-fold>

    /**
     *
     * @param driverName
     * @param url
     * @throws Exception
     */
    public DBConnection(String driverName, String url) throws Exception {
        this(driverName, url, INT_DefaultPoolSize, STR_DefaultUser, STR_DefaultPassword);
    }

    public DBConnection(String driverName, String url, String user, String password) throws Exception {
        this(driverName, url, INT_DefaultPoolSize, STR_DefaultUser, STR_DefaultPassword);
    }

    /**
     *
     * @param driverName
     * @param url
     * @param user
     * @param password
     * @throws Exception
     */
    public DBConnection(String driverName, String url, int poolCount, String user, String password) throws Exception {
        final String methodName = "DBConnection";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that parameters are valid
             */
            if (driverName == null) {
                throw new NullPointerException(STRERR_DriverName);
            }
            if (url == null) {
                throw new NullPointerException(STRERR_Url);
            }
            if (user == null) {
                throw new NullPointerException(STRERR_User);
            }
            if (password == null) {
                throw new NullPointerException(STRERR_Password);
            }

            /*
             * Initial local variables
             */
            this.driverName = driverName;
            this.url = url;
            this.poolsize = (poolCount < INT_DefaultPoolSize) ? INT_DefaultPoolSize : poolCount;
            this.properties = new Properties();
            this.properties.setProperty(STR_User, user);
            this.properties.setProperty(STR_Password, password);

            /*
             * Load the database driver to ensure that it exists
             */
            Class.forName(this.driverName);

            /*
             * Create the connection pool
             */
            this.connectionList = Collections.synchronizedList(new ArrayList<Connection>(this.poolsize));
            for (int i = 0; i < this.poolsize; i++) {
                this.connectionList.add(DriverManager.getConnection(this.url, this.properties));
            }
            this.connections = new Semaphore(this.poolsize, true);

        } catch (ClassNotFoundException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return Connection
     */
    public Connection getConnection() {
        final String methodName = "getConnection";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        Connection connection = null;

        try {
            /*
             * Get a connection from the pool
             */
            this.connections.acquire();
            synchronized (this.connectionList) {
                connection = this.connectionList.remove(0);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return connection;
    }

    /**
     *
     * @param connection
     */
    public void putConnection(Connection connection) {
        final String methodName = "putConnection";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Return the connection to the pool
         */
        synchronized (this.connectionList) {
            this.connectionList.add(connection);
        }
        this.connections.release();

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
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
