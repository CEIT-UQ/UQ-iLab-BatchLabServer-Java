/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.database.types.LabServerInfo;

/**
 *
 * @author uqlpayne
 */
public class LabServerDB {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabServerDB.class.getName();
    private static final Level logLevel = Level.FINEST;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Id_arg = "Id: %d";
    private static final String STRLOG_Name_arg = "Name: %s";
    private static final String STRLOG_Count_arg = "Count: %d";
    private static final String STRLOG_Success_arg = "Success: %s";
    /*
     * Database column names
     */
    private static final String STRCOL_Id = "Id";
    private static final String STRCOL_Name = "Name";
    private static final String STRCOL_Guid = "Guid";
    private static final String STRCOL_ServiceUrl = "ServiceUrl";
    private static final String STRCOL_ContactEmail = "ContactEmail";
    private static final String STRCOL_CompletedEmail = "CompletedEmail";
    private static final String STRCOL_FailedEmail = "FailedEmail";
    private static final String STRCOL_Authenticate = "Authenticate";
    private static final String STRCOL_DateCreated = "DateCreated";
    private static final String STRCOL_DateModified = "DateModified";
    /*
     * String constants for SQL processing
     */
    private static final String STRSQLCMD_Add = "{ ? = call LabServer_Add(?,?,?,?,?,?,?) }";
    private static final String STRSQLCMD_Delete = "{ ? = call LabServer_Delete(?) }";
    private static final String STRSQLCMD_GetList = "{ call LabServer_GetList(?,?) }";
    private static final String STRSQLCMD_RetrieveBy = "{ call LabServer_RetrieveBy(?,?,?) }";
    private static final String STRSQLCMD_Update = "{ ? = call LabServer_Update(?,?,?,?,?,?,?,?) }";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Connection sqlConnection;
    //</editor-fold>

    /**
     *
     * @param dbConnection
     * @throws Exception
     */
    public LabServerDB(DBConnection dbConnection) throws Exception {
        final String methodName = "LabServerDB";
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
     * @param labServerInfo
     * @return int
     * @throws Exception
     */
    public int Add(LabServerInfo labServerInfo) throws Exception {
        final String methodName = "Add";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Name_arg, labServerInfo.getName()));

        int id = -1;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_Add);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setString(2, labServerInfo.getName());
                sqlStatement.setString(3, labServerInfo.getGuid());
                sqlStatement.setString(4, labServerInfo.getServiceUrl());
                sqlStatement.setString(5, labServerInfo.getContactEmail());
                sqlStatement.setString(6, labServerInfo.getCompletedEmail());
                sqlStatement.setString(7, labServerInfo.getFailedEmail());
                sqlStatement.setBoolean(8, labServerInfo.isAuthenticate());

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
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Id_arg, id));

        return id;
    }

    /**
     *
     * @param id
     * @return boolean
     * @throws Exception
     */
    public boolean Delete(int id) throws Exception {
        final String methodName = "Delete";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Id_arg, id));

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
    public String[] GetListName() throws Exception {
        return this.GetList(STRCOL_Name, null);
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public LabServerInfo Retrieve() throws Exception {
        return this.RetrieveBy(null, 0, null);
    }

    /**
     *
     * @param id
     * @return
     * @throws Exception
     */
    public LabServerInfo RetrieveById(int id) throws Exception {
        return this.RetrieveBy(STRCOL_Id, id, null);
    }

    /**
     *
     * @param name
     * @return LabServerInfo
     * @throws Exception
     */
    public LabServerInfo RetrieveByName(String name) throws Exception {
        return this.RetrieveBy(STRCOL_Name, 0, name);
    }

    /**
     *
     * @param labServerInfo
     * @return boolean
     * @throws Exception
     */
    public boolean Update(LabServerInfo labServerInfo) throws Exception {
        final String methodName = "Update";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Id_arg, labServerInfo.getId()));

        boolean success = false;

        try {
            /*
             * Check that parameters are valid
             */
            if (labServerInfo == null) {
                throw new NullPointerException(LabServerInfo.class.getSimpleName());
            }

            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_Update);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, labServerInfo.getId());
                sqlStatement.setString(3, labServerInfo.getName());
                sqlStatement.setString(4, labServerInfo.getGuid());
                sqlStatement.setString(5, labServerInfo.getServiceUrl());
                sqlStatement.setString(6, labServerInfo.getContactEmail());
                sqlStatement.setString(7, labServerInfo.getCompletedEmail());
                sqlStatement.setString(8, labServerInfo.getFailedEmail());
                sqlStatement.setBoolean(9, labServerInfo.isAuthenticate());

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();

                /*
                 * Get the result
                 */
                success = ((int) sqlStatement.getInt(1) == labServerInfo.getId());
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
                sqlStatement.setString(1, (columnName != null) ? columnName.toLowerCase() : null);
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
     * @return LabServerInfo
     * @throws Exception
     */
    private LabServerInfo RetrieveBy(String columnName, int intval, String strval) throws Exception {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        LabServerInfo labServerInfo = null;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_RetrieveBy);
                sqlStatement.setString(1, (columnName != null) ? columnName.toLowerCase() : null);
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
                    labServerInfo = new LabServerInfo();
                    labServerInfo.setId(resultSet.getInt(STRCOL_Id));
                    labServerInfo.setName(resultSet.getString(STRCOL_Name));
                    labServerInfo.setGuid(resultSet.getString(STRCOL_Guid));
                    labServerInfo.setServiceUrl(resultSet.getString(STRCOL_ServiceUrl));
                    labServerInfo.setContactEmail(resultSet.getString(STRCOL_ContactEmail));
                    labServerInfo.setCompletedEmail(resultSet.getString(STRCOL_CompletedEmail));
                    labServerInfo.setFailedEmail(resultSet.getString(STRCOL_FailedEmail));
                    labServerInfo.setAuthenticate(resultSet.getBoolean(STRCOL_Authenticate));

                    Calendar calendar;
                    Timestamp timestamp;
                    if ((timestamp = resultSet.getTimestamp(STRCOL_DateCreated)) != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTime(timestamp);
                        labServerInfo.setDateCreated(calendar);
                    }
                    if ((timestamp = resultSet.getTimestamp(STRCOL_DateModified)) != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTime(timestamp);
                        labServerInfo.setDateModified(calendar);
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

        return labServerInfo;
    }
}
