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
    private static final String STRSQLCMD_GetRecordCount = "{ ? = call Users_GetRecordCount() }";
    private static final String STRSQLCMD_RetrieveBy = "{ call Users_RetrieveBy(?,?,?) }";
    private static final String STRSQLCMD_Update = "{ ? = call Users_Update(?,?,?,?,?,?,?) }";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Connection sqlConnection;
    //</editor-fold>

    /**
     *
     * @param dbConnection
     * @throws Exception
     */
    public UsersDB(DBConnection dbConnection) throws Exception {
        final String methodName = "UsersDB";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that parameters are valid
             */
            if (dbConnection == null) {
                throw new NullPointerException(DBConnection.class.getSimpleName());
            }

            /*
             * Initialise locals
             */
            this.sqlConnection = dbConnection.getConnection();

        } catch (NullPointerException | SQLException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param userInfo
     * @return
     * @throws Exception
     */
    public int Add(UserInfo userInfo) throws Exception {
        final String methodName = "Add";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int userId = -1;

        try {
            /*
             * Check that parameters are valid
             */
            if (userInfo == null) {
                throw new NullPointerException(UserInfo.class.getSimpleName());
            }

            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_Add);
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

                /*
                 * Get the result
                 */
                userId = (int) sqlStatement.getInt(1);
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
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_UserId_arg, userId));

        return userId;
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
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_Delete);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, userId);

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();

                /*
                 * Get the result
                 */
                success = ((int) sqlStatement.getInt(1) == userId);
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
    public String[] GetListUsername() throws Exception {
        return this.GetList(STRCOL_Username, null);
    }

    /**
     *
     * @param usergroup
     * @return String[]
     * @throws Exception
     */
    public String[] GetListUsergroup(String usergroup) throws Exception {
        return this.GetList(STRCOL_UserGroup, usergroup);
    }

    /**
     *
     * @return @throws Exception
     */
    public int GetRecordCount() throws Exception {
        final String methodName = "GetRecordCount";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int count = -1;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_GetRecordCount);
                sqlStatement.registerOutParameter(1, Types.BIGINT);

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();

                /*
                 * Get the result
                 */
                count = (int) sqlStatement.getLong(1);
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
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Count_arg, count));

        return count;
    }

    /**
     *
     * @param username
     * @return
     * @throws Exception
     */
    public UserInfo RetrieveByUserId(int userId) throws Exception {
        return this.RetrieveBy(STRCOL_UserId, userId, null);
    }

    /**
     *
     * @param username
     * @return
     * @throws Exception
     */
    public UserInfo RetrieveByUsername(String username) throws Exception {
        return this.RetrieveBy(STRCOL_Username, 0, username);
    }

    /**
     *
     * @param userInfo
     * @return
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

            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_Update);
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

                /*
                 * Get the result
                 */
                success = ((int) sqlStatement.getInt(1) == userInfo.getUserId());
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
                    list.add(resultSet.getString(STRCOL_Username));
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
            throw ex;
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
     * @throws Exception
     */
    private UserInfo RetrieveBy(String columnName, int intval, String strval) throws Exception {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        UserInfo userInfo = null;

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
                if (resultSet.next() == true) {
                    userInfo = new UserInfo();
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
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return userInfo;
    }
}
