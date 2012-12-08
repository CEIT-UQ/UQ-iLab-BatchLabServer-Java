/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.engine.types.ServiceBrokerInfo;

/**
 *
 * @author uqlpayne
 */
public class ServiceBrokersDB {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    public static final String STR_ClassName = ServiceBrokersDB.class.getName();
    private static final Level logLevel = Level.FINEST;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Id_arg = "Id: %d";
    private static final String STRLOG_Count_arg = "Count: %d";
    private static final String STRLOG_Success_arg = "Success: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_DBConnection = "dBConnection";
    /*
     * Database column names
     */
    private static final String STRCOL_Id = "Id";
    private static final String STRCOL_Name = "Name";
    private static final String STRCOL_Guid = "Guid";
    private static final String STRCOL_OutPasskey = "OutPasskey";
    private static final String STRCOL_InPasskey = "InPasskey";
    private static final String STRCOL_ServiceUrl = "ServiceUrl";
    private static final String STRCOL_Permitted = "Permitted";
    private static final String STRCOL_DateCreated = "DateCreated";
    private static final String STRCOL_DateModified = "DateModified";
    /*
     * String constants for SQL processing
     */
    private static final String STRSQLCMD_Add = "{ ? = call ServiceBrokers_Add(?,?,?,?,?,?) }";
    private static final String STRSQLCMD_Delete = "{ ? = call ServiceBrokers_Delete(?) }";
    private static final String STRSQLCMD_GetList = "{ call ServiceBrokers_GetList(?,?) }";
    private static final String STRSQLCMD_RetrieveBy = "{ call ServiceBrokers_RetrieveBy(?,?,?) }";
    private static final String STRSQLCMD_Update = "{ ? = call ServiceBrokers_Update(?,?,?,?,?,?,?) }";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Connection sqlConnection;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private boolean authenticating;
    private boolean logAuthentication;

    public boolean isAuthenticating() {
        return authenticating;
    }

    public void setAuthenticating(boolean authenticating) {
        this.authenticating = authenticating;
    }

    public boolean isLogAuthentication() {
        return logAuthentication;
    }

    public void setLogAuthentication(boolean logAuthentication) {
        this.logAuthentication = logAuthentication;
    }
    //</editor-fold>

    /**
     *
     * @param dbConnection
     * @throws Exception
     */
    public ServiceBrokersDB(DBConnection dbConnection) throws Exception {
        final String methodName = "ServiceBrokersDB";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Check that parameters are valid
         */
        if (dbConnection == null) {
            throw new NullPointerException(STRERR_DBConnection);
        }

        /*
         * Initialise local variables and properties
         */
        this.sqlConnection = dbConnection.getConnection();
        this.authenticating = true;
        this.logAuthentication = false;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param guid
     * @return String
     */
    public String GetNameByGuid(String guid) {
        ServiceBrokerInfo serviceBrokerInfo = this.RetrieveByGuid(guid);
        return (serviceBrokerInfo != null) ? serviceBrokerInfo.getName() : null;
    }

    /**
     * Get the web service URL for the specified ServiceBroker's name. If the ServiceBroker's name was not found, null
     * is returned.
     *
     * @param name The ServiceBroker's name.
     * @return The web service URL.
     */
    public String GetServiceUrlByName(String name) {
        ServiceBrokerInfo serviceBrokerInfo = this.RetrieveByName(name);
        return (serviceBrokerInfo != null) ? serviceBrokerInfo.getServiceUrl() : null;
    }

    /**
     *
     * @param serviceBrokerInfo
     * @return integer
     */
    public int Add(ServiceBrokerInfo serviceBrokerInfo) {
        final String methodName = "Add";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int id = -1;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_Add);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setString(2, serviceBrokerInfo.getName());
                sqlStatement.setString(3, serviceBrokerInfo.getGuid());
                sqlStatement.setString(4, serviceBrokerInfo.getOutPasskey());
                sqlStatement.setString(5, serviceBrokerInfo.getInPasskey());
                sqlStatement.setString(6, serviceBrokerInfo.getServiceUrl());
                sqlStatement.setBoolean(7, serviceBrokerInfo.isPermitted());

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();

                /*
                 * Get the result
                 */
                id = (int) sqlStatement.getInt(1);
            } catch (Exception ex) {
                throw ex;
            } finally {
                try {
                    sqlStatement.close();
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Id_arg, id));

        return id;
    }

    /**
     *
     * @param id
     * @return boolean
     */
    public boolean Delete(int id) {
        final String methodName = "Delete";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_Delete);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, id);

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();

                /*
                 * Get the result
                 */
                success = ((int) sqlStatement.getInt(1) == id);
            } catch (Exception ex) {
                throw ex;
            } finally {
                try {
                    sqlStatement.close();
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return String[]
     */
    public String[] GetListOfNames() {
        return this.GetList(STRCOL_Name, null);
    }

    /**
     *
     * @return ArrayList of ServiceBrokerInfo
     */
    public ArrayList<ServiceBrokerInfo> RetrieveAll() {
        return this.RetrieveBy(null, 0, null);
    }

    /**
     *
     * @param id
     * @return ServiceBrokerInfo
     */
    public ServiceBrokerInfo RetrieveById(int id) {
        ArrayList<ServiceBrokerInfo> list = this.RetrieveBy(STRCOL_Id, id, null);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @param name
     * @return ServiceBrokerInfo
     */
    public ServiceBrokerInfo RetrieveByName(String name) {
        ArrayList<ServiceBrokerInfo> list = this.RetrieveBy(STRCOL_Name, 0, name);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @param guid
     * @return ServiceBrokerInfo
     */
    public ServiceBrokerInfo RetrieveByGuid(String guid) {
        ArrayList<ServiceBrokerInfo> list = this.RetrieveBy(STRCOL_Guid, 0, guid);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @param serviceBrokerInfo
     * @return boolean
     */
    public boolean Update(ServiceBrokerInfo serviceBrokerInfo) {
        final String methodName = "Update";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_Update);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, serviceBrokerInfo.getId());
                sqlStatement.setString(3, serviceBrokerInfo.getName());
                sqlStatement.setString(4, serviceBrokerInfo.getGuid());
                sqlStatement.setString(5, serviceBrokerInfo.getOutPasskey());
                sqlStatement.setString(6, serviceBrokerInfo.getInPasskey());
                sqlStatement.setString(7, serviceBrokerInfo.getServiceUrl());
                sqlStatement.setBoolean(8, serviceBrokerInfo.isPermitted());

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();

                /*
                 * Get the result
                 */
                success = ((int) sqlStatement.getInt(1) == serviceBrokerInfo.getId());
            } catch (Exception ex) {
                throw ex;
            } finally {
                try {
                    sqlStatement.close();
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    //================================================================================================================//
    /**
     *
     * @param columnName
     * @param strval
     * @return String[]
     */
    private String[] GetList(String columnName, String strval) {
        final String methodName = "GetList";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String[] listArray = null;

        try {
            ArrayList<String> list = new ArrayList<>();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_GetList);
                sqlStatement.setString(1, (columnName != null ? columnName.toLowerCase() : null));
                sqlStatement.setString(2, strval);

                /*
                 * Execute the stored procedure
                 */
                ResultSet resultSet = sqlStatement.executeQuery();

                /*
                 * Add result to the list
                 */
                while (resultSet.next() == true) {
                    list.add(resultSet.getString(STRCOL_Name));
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                try {
                    sqlStatement.close();
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
            }

            /*
             * Convert the list to an array
             */
            if (list.size() > 0) {
                listArray = list.toArray(new String[0]);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Count_arg, (listArray != null) ? listArray.length : 0));

        return listArray;
    }

    /**
     *
     * @param columnName
     * @param intval
     * @param strval
     * @return
     */
    private ArrayList<ServiceBrokerInfo> RetrieveBy(String columnName, int intval, String strval) {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ArrayList<ServiceBrokerInfo> list = new ArrayList<>();

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_RetrieveBy);
                sqlStatement.setString(1, (columnName != null ? columnName.toLowerCase() : null));
                sqlStatement.setInt(2, intval);
                sqlStatement.setString(3, strval);

                /*
                 * Execute the stored procedure
                 */
                ResultSet resultSet = sqlStatement.executeQuery();

                /*
                 * Process the results of the query - only want the first result
                 */
                while (resultSet.next() == true) {
                    ServiceBrokerInfo info = new ServiceBrokerInfo();
                    info.setId(resultSet.getInt(STRCOL_Id));
                    info.setName(resultSet.getString(STRCOL_Name));
                    info.setGuid(resultSet.getString(STRCOL_Guid));
                    info.setOutPasskey(resultSet.getString(STRCOL_OutPasskey));
                    info.setInPasskey(resultSet.getString(STRCOL_InPasskey));
                    info.setServiceUrl(resultSet.getString(STRCOL_ServiceUrl));
                    info.setPermitted(resultSet.getBoolean(STRCOL_Permitted));
                    info.setDateCreated(resultSet.getTimestamp(STRCOL_DateCreated));
                    info.setDateModified(resultSet.getTimestamp(STRCOL_DateModified));

                    /*
                     * Add the info to the list
                     */
                    list.add(info);
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                try {
                    sqlStatement.close();
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Count_arg, list.size()));

        return (list.size() > 0) ? list : null;
    }
}
