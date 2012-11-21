/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
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
     * String constants
     */
    private static final String STR_localhost = "localhost";
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_CachingServiceBrokers = "Caching ServiceBrokers...";
    private static final String STRLOG_CachedServiceBroker_arg = "%d: %s";
    private static final String STRLOG_GuidPasskey_arg = "Guid: %s  Passkey: %s";
    private static final String STRLOG_Id_arg = "Id: %d";
    private static final String STRLOG_SbName_arg = "sbName: %s";
    private static final String STRLOG_Count_arg = "Count: %d";
    private static final String STRLOG_Success_arg = "Success: %s";
    private static final String STRLOG_ServiceBrokerAuthenticated_arg = "ServiceBroker Authenticated: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_DBConnection = "dBConnection";
    private static final String STRERR_Name = "name";
    private static final String STRERR_Guid = "guid";
    private static final String STRERR_Passkey = "passkey";
    private static final String STRERR_AccessDenied = "Access is denied!";
    private static final String STRERR_IncorrectPasskey_arg = "Incorrect Passkey: %s";
    private static final String STRERR_ServiceBrokerNotFound = "ServiceBroker not found!";
    /*
     * Database column names - must be lowercase
     */
    private static final String STRCOL_Id = "id";
    private static final String STRCOL_Name = "name";
    private static final String STRCOL_Guid = "guid";
    private static final String STRCOL_OutPasskey = "outpasskey";
    private static final String STRCOL_InPasskey = "inpasskey";
    private static final String STRCOL_ServiceUrl = "serviceurl";
    private static final String STRCOL_Permitted = "permitted";
    private static final String STRCOL_DateCreated = "datecreated";
    private static final String STRCOL_DateModified = "datemodified";
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
    private ArrayList<ServiceBrokerInfo> serviceBrokerInfoList;
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
     * @return
     */
    public int CreateCache() {
        final String methodName = "CreateCache";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int cacheSize = -1;
        try {
            /*
             * Retrieve all ServiceBrokers
             */
            this.serviceBrokerInfoList = this.RetrieveAll();

            /*
             * Log the ServiceBroker info cache
             */
            String logMessage = STRLOG_CachingServiceBrokers + Logfile.STRLOG_Newline;
            for (int i = 0; i < this.serviceBrokerInfoList.size(); i++) {
                logMessage += String.format(STRLOG_CachedServiceBroker_arg, i + 1, this.serviceBrokerInfoList.get(i).getName()) + Logfile.STRLOG_Newline;
            }
            Logfile.Write(logMessage);

            /*
             * ServiceBrokers cached successfully
             */
            cacheSize = this.serviceBrokerInfoList.size();
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return cacheSize;
    }

    /**
     * Authenticate the ServiceBroker for the specified GUID and outgoing passkey.
     *
     * @param guid The ServiceBroker's GUID (identity).
     * @param passKey The passkey sent by the ServiceBroker to the LabServer.
     * @return The ServiceBroker's GUID if the ServiceBroker information exists and is allowed access.
     */
    public String Authenticate(String guid, String passkey) {
        final String methodName = "Authenticate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check special case where the call is made directly from the 'Test Web Service...' web page on the
             * localhost during application development and testing.
             */
            if (guid == null && passkey == null) {
                /*
                 * ServiceBroker is localhost where SOAP header contains no information
                 */
                ServiceBrokerInfo serviceBrokerInfo = RetrieveByName(STR_localhost);
                if (serviceBrokerInfo == null) {
                    throw new Exception(STRERR_ServiceBrokerNotFound);
                }
                guid = serviceBrokerInfo.getGuid();
                passkey = serviceBrokerInfo.getOutPasskey();
            }

            /*
             * Check that parameters are valid
             */
            if (guid == null) {
                throw new NullPointerException(STRERR_Guid);
            }
            if (passkey == null) {
                throw new NullPointerException(STRERR_Passkey);
            }

            /*
             * Remove whitespace
             */
            guid = guid.trim();
            passkey = passkey.trim();

            /*
             * Check if the GUID and passkey should be logged
             */
            if (this.isLogAuthentication() == true) {
                /*
                 * Log the ServiceBroker's GUID and passkey
                 */
                Logfile.Write(String.format(STRLOG_GuidPasskey_arg, guid, passkey));
            }

            /*
             * Check if using cache
             */
            ServiceBrokerInfo serviceBrokerInfo = null;
            if (this.serviceBrokerInfoList == null) {
                /*
                 * No cache, retrieve the ServiceBroker information from the database
                 */
                serviceBrokerInfo = this.RetrieveByGuid(guid);
            } else {
                /*
                 * Scan the ServiceBroker info cache for a matching entry
                 */
                Iterator iterator = this.serviceBrokerInfoList.iterator();
                while (iterator.hasNext()) {
                    serviceBrokerInfo = (ServiceBrokerInfo) iterator.next();
                    if (guid.equalsIgnoreCase(serviceBrokerInfo.getGuid()) == true) {
                        break;
                    }
                    serviceBrokerInfo = null;
                }
            }

            /*
             * Check if a ServiceBroker has been found
             */
            if (serviceBrokerInfo == null) {
                throw new RuntimeException(STRERR_ServiceBrokerNotFound);
            }

            /*
             * Check the passkey - comparison is not case-sensitive
             */
            if (passkey.equalsIgnoreCase(serviceBrokerInfo.getOutPasskey()) == false) {
                throw new RuntimeException(STRERR_IncorrectPasskey_arg);
            }

            /*
             * Check if the ServiceBroker is permitted access
             */
            if (serviceBrokerInfo.isPermitted() == false) {
                throw new RuntimeException(String.format(STRERR_AccessDenied));
            }

            /*
             * ServiceBroker authenticated
             */
            Logfile.Write(String.format(STRLOG_ServiceBrokerAuthenticated_arg, serviceBrokerInfo.getName()));
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            guid = null;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return guid;
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
                sqlStatement.setString(1, columnName);
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
                sqlStatement.setString(1, columnName);
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
