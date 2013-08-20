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
import uq.ilabs.library.labserver.database.types.UserInfo;

/**
 *
 * @author uqlpayne
 */
public class UsersDB {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = UsersDB.class.getName();
    private static final Level logLevel = Level.FINEST;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_UserId_arg = "UserId: %d";
    private static final String STRLOG_Count_arg = "Count: %d";
    private static final String STRLOG_Success_arg = "Success: %s";
    /*
     * Database column names
     */
    private static final String STRCOL_UserId = "UserId";
    private static final String STRCOL_Username = "Username";
    private static final String STRCOL_FirstName = "FirstName";
    private static final String STRCOL_LastName = "LastName";
    private static final String STRCOL_ContactEmail = "ContactEmail";
    private static final String STRCOL_UserGroup = "UserGroup";
    private static final String STRCOL_Password = "Password";
    private static final String STRCOL_AccountLocked = "AccountLocked";
    private static final String STRCOL_DateCreated = "DateCreated";
    private static final String STRCOL_DateModified = "DateModified";
    /*
     * String constants for SQL processing
     */
    private static final String STRSQLCMD_Add = "{ ? = call Users_Add(?,?,?,?,?,?) }";
    private static final String STRSQLCMD_Delete = "{ ? = call Users_Delete(?) }";
    private static final String STRSQLCMD_GetList = "{ call Users_GetList(?,?) }";
    private static final String STRSQLCMD_RetrieveBy = "{ call Users_RetrieveBy(?,?,?) }";
    private static final String STRSQLCMD_Update = "{ ? = call Users_Update(?,?,?,?,?,?,?) }";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private DBConnection dbConnection;
    //</editor-fold>

    /**
     *
     * @param dbConnection
     * @throws Exception
     */
    public UsersDB(DBConnection dbConnection) throws Exception {
        final String methodName = "UsersDB";
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
     * @param userInfo
     * @return int
     * @throws Exception
     */
    public int Add(UserInfo userInfo) throws Exception {
        final String methodName = "Add";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int id = -1;

        try {
            /*
             * Check that parameters are valid
             */
            if (userInfo == null) {
                throw new NullPointerException(UserInfo.class.getSimpleName());
            }

            Connection sqlConnection = this.dbConnection.getConnection();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_Add);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setString(2, userInfo.getUsername());
                sqlStatement.setString(3, userInfo.getFirstName());
                sqlStatement.setString(4, userInfo.getLastName());
                sqlStatement.setString(5, userInfo.getContactEmail());
                sqlStatement.setString(6, userInfo.getUserGroup());
                sqlStatement.setString(7, userInfo.getPassword());

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
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_UserId_arg, id));

        return id;
    }

    /**
     *
     * @param userId
     * @return boolean
     * @throws Exception
     */
    public boolean Delete(int userId) throws Exception {
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
                sqlStatement.setInt(2, userId);

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();
                success = (sqlStatement.getInt(1) == userId);
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
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return String[]
     * @throws Exception
     */
    public String[] GetListOfUsernames() throws Exception {
        return this.GetList(STRCOL_Username, null);
    }

    /**
     *
     * @param userId
     * @return UserInfo
     * @throws Exception
     */
    public UserInfo RetrieveById(int userId) throws Exception {
        ArrayList<UserInfo> arrayList = this.RetrieveBy(STRCOL_UserId, userId, null);
        return arrayList != null ? (UserInfo) arrayList.get(0) : null;
    }

    /**
     *
     * @param username
     * @return UserInfo
     * @throws Exception
     */
    public UserInfo RetrieveByUsername(String username) throws Exception {
        ArrayList<UserInfo> arrayList = this.RetrieveBy(STRCOL_Username, 0, username);
        return arrayList != null ? (UserInfo) arrayList.get(0) : null;
    }

    /**
     *
     * @param userInfo
     * @return boolean
     * @throws Exception
     */
    public boolean Update(UserInfo userInfo) throws Exception {
        final String methodName = "Update";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Check that parameters are valid
             */
            if (userInfo == null) {
                throw new NullPointerException(UserInfo.class.getSimpleName());
            }

            Connection sqlConnection = this.dbConnection.getConnection();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_Update);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, userInfo.getUserId());
                sqlStatement.setString(3, userInfo.getFirstName());
                sqlStatement.setString(4, userInfo.getLastName());
                sqlStatement.setString(5, userInfo.getContactEmail());
                sqlStatement.setString(6, userInfo.getUserGroup());
                sqlStatement.setString(7, userInfo.getPassword());
                sqlStatement.setBoolean(8, userInfo.isAccountLocked());

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();
                success = ((int) sqlStatement.getInt(1) == userInfo.getUserId());
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
            throw ex;
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
     * @throws Exception
     */
    private String[] GetList(String columnName, String strval) throws Exception {
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
                    /*
                     * Add String to the list
                     */
                    arrayList.add(resultSet.getString(columnName));
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
            throw ex;
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
     * @return ArrayList of UserInfo
     * @throws Exception
     */
    private ArrayList<UserInfo> RetrieveBy(String columnName, int intval, String strval) throws Exception {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ArrayList<UserInfo> arrayList = new ArrayList<>();

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
                    UserInfo userInfo = new UserInfo();

                    userInfo.setUserId(resultSet.getInt(STRCOL_UserId));
                    userInfo.setUsername(resultSet.getString(STRCOL_Username));
                    userInfo.setFirstName(resultSet.getString(STRCOL_FirstName));
                    userInfo.setLastName(resultSet.getString(STRCOL_LastName));
                    userInfo.setContactEmail(resultSet.getString(STRCOL_ContactEmail));
                    userInfo.setUserGroup(resultSet.getString(STRCOL_UserGroup));
                    userInfo.setPassword(resultSet.getString(STRCOL_Password));
                    userInfo.setAccountLocked(resultSet.getBoolean(STRCOL_AccountLocked));

                    Calendar calendar;
                    Timestamp timestamp;
                    if ((timestamp = resultSet.getTimestamp(STRCOL_DateCreated)) != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTime(timestamp);
                        userInfo.setDateCreated(calendar);
                    }
                    if ((timestamp = resultSet.getTimestamp(STRCOL_DateModified)) != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTime(timestamp);
                        userInfo.setDateModified(calendar);
                    }

                    /*
                     * Add UserInfo to the list
                     */
                    arrayList.add(userInfo);
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
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Count_arg, arrayList.size()));

        return (arrayList.size() > 0) ? arrayList : null;
    }
}
