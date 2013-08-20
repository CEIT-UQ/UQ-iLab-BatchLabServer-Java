/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.database.types.ServiceBrokerInfo;

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
    private DBConnection dbConnection;
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
            throw new NullPointerException(DBConnection.class.getSimpleName());
        }

        /*
         * Initialise locals
         */
        this.dbConnection = dbConnection;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
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
            /*
             * Check that parameters are valid
             */
            if (serviceBrokerInfo == null) {
                throw new NullPointerException(ServiceBrokerInfo.class.getSimpleName());
            }

            Connection sqlConnection = this.dbConnection.getConnection();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_Add);
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
                id = (int) sqlStatement.getInt(1);
            } finally {
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
                this.dbConnection.putConnection(sqlConnection);
            }
        } catch (NullPointerException | SQLException ex) {
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
            Connection sqlConnection = this.dbConnection.getConnection();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_Delete);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, id);

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();
                success = ((int) sqlStatement.getInt(1) == id);
            } finally {
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
                this.dbConnection.putConnection(sqlConnection);
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
        ArrayList<ServiceBrokerInfo> arrayList = this.RetrieveBy(STRCOL_Id, id, null);
        return (arrayList != null) ? arrayList.get(0) : null;
    }

    /**
     *
     * @param name
     * @return ServiceBrokerInfo
     */
    public ServiceBrokerInfo RetrieveByName(String name) {
        ArrayList<ServiceBrokerInfo> arrayList = this.RetrieveBy(STRCOL_Name, 0, name);
        return (arrayList != null) ? arrayList.get(0) : null;
    }

    /**
     *
     * @param guid
     * @return ServiceBrokerInfo
     */
    public ServiceBrokerInfo RetrieveByGuid(String guid) {
        ArrayList<ServiceBrokerInfo> arrayList = this.RetrieveBy(STRCOL_Guid, 0, guid);
        return (arrayList != null) ? arrayList.get(0) : null;
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
            /*
             * Check that parameters are valid
             */
            if (serviceBrokerInfo == null) {
                throw new NullPointerException(ServiceBrokerInfo.class.getSimpleName());
            }

            Connection sqlConnection = this.dbConnection.getConnection();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_Update);
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
                success = ((int) sqlStatement.getInt(1) == serviceBrokerInfo.getId());
            } finally {
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
                this.dbConnection.putConnection(sqlConnection);
            }
        } catch (NullPointerException | SQLException ex) {
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

        String[] stringArray = null;

        try {
            ArrayList<String> arrayList = new ArrayList<>();
            Connection sqlConnection = this.dbConnection.getConnection();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_GetList);
                sqlStatement.setString(1, columnName);
                sqlStatement.setString(2, strval);

                /*
                 * Execute the stored procedure
                 */
                ResultSet resultSet = sqlStatement.executeQuery();
                while (resultSet.next() == true) {
                    arrayList.add(resultSet.getString(STRCOL_Name));
                }
            } finally {
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
                this.dbConnection.putConnection(sqlConnection);
            }

            /*
             * Convert the list to an array
             */
            if (arrayList.size() > 0) {
                stringArray = arrayList.toArray(new String[0]);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Count_arg, (stringArray != null) ? stringArray.length : 0));

        return stringArray;
    }

    /**
     *
     * @param columnName
     * @param intval
     * @param strval
     * @return ArrayList of ServiceBrokerInfo
     */
    private ArrayList<ServiceBrokerInfo> RetrieveBy(String columnName, int intval, String strval) {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ArrayList<ServiceBrokerInfo> arrayList = new ArrayList<>();

        try {
            Connection sqlConnection = this.dbConnection.getConnection();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_RetrieveBy);
                sqlStatement.setString(1, columnName);
                sqlStatement.setInt(2, intval);
                sqlStatement.setString(3, strval);

                /*
                 * Execute the stored procedure
                 */
                ResultSet resultSet = sqlStatement.executeQuery();
                while (resultSet.next() == true) {
                    ServiceBrokerInfo serviceBrokerInfo = new ServiceBrokerInfo();

                    serviceBrokerInfo.setId(resultSet.getInt(STRCOL_Id));
                    serviceBrokerInfo.setName(resultSet.getString(STRCOL_Name));
                    serviceBrokerInfo.setGuid(resultSet.getString(STRCOL_Guid));
                    serviceBrokerInfo.setOutPasskey(resultSet.getString(STRCOL_OutPasskey));
                    serviceBrokerInfo.setInPasskey(resultSet.getString(STRCOL_InPasskey));
                    serviceBrokerInfo.setServiceUrl(resultSet.getString(STRCOL_ServiceUrl));
                    serviceBrokerInfo.setPermitted(resultSet.getBoolean(STRCOL_Permitted));

                    Calendar calendar;
                    Timestamp timestamp;
                    if ((timestamp = resultSet.getTimestamp(STRCOL_DateCreated)) != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTime(timestamp);
                        serviceBrokerInfo.setDateCreated(calendar);
                    }
                    if ((timestamp = resultSet.getTimestamp(STRCOL_DateModified)) != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTime(timestamp);
                        serviceBrokerInfo.setDateModified(calendar);
                    }

                    /*
                     * Add the ServiceBrokerInfo to the list
                     */
                    arrayList.add(serviceBrokerInfo);
                }
            } finally {
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
                this.dbConnection.putConnection(sqlConnection);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Count_arg, arrayList.size()));

        return (arrayList.size() > 0) ? arrayList : null;
    }
}
