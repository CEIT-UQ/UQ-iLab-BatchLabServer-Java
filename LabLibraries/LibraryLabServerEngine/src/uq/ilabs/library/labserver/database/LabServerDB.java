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
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int id = -1;

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
                id = (int) sqlStatement.getInt(1);
            } finally {
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
            }
        } catch (NullPointerException | SQLException ex) {
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
                success = ((int) sqlStatement.getInt(1) == id);
            } finally {
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
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
    public String[] GetListOfNames() throws Exception {
        return this.GetList(STRCOL_Name, null);
    }

    /**
     *
     * @return @throws Exception
     */
    public ArrayList<LabServerInfo> RetrieveAll() throws Exception {
        return this.RetrieveBy(null, 0, null);
    }

    /**
     *
     * @return @throws Exception
     */
    public LabServerInfo Retrieve() throws Exception {
        ArrayList<LabServerInfo> arrayList = this.RetrieveBy(null, 0, null);
        return (arrayList != null) ? arrayList.get(0) : null;
    }

    /**
     *
     * @param id
     * @return
     * @throws Exception
     */
    public LabServerInfo RetrieveById(int id) throws Exception {
        ArrayList<LabServerInfo> arrayList = this.RetrieveBy(STRCOL_Id, id, null);
        return (arrayList != null) ? arrayList.get(0) : null;
    }

    /**
     *
     * @param name
     * @return LabServerInfo
     * @throws Exception
     */
    public LabServerInfo RetrieveByName(String name) throws Exception {
        ArrayList<LabServerInfo> arrayList = this.RetrieveBy(STRCOL_Name, 0, name);
        return (arrayList != null) ? arrayList.get(0) : null;
    }

    /**
     *
     * @param labServerInfo
     * @return boolean
     * @throws Exception
     */
    public boolean Update(LabServerInfo labServerInfo) throws Exception {
        final String methodName = "Update";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

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
            } finally {
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
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
     * @return LabServerInfo
     * @throws Exception
     */
    private ArrayList<LabServerInfo> RetrieveBy(String columnName, int intval, String strval) throws Exception {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ArrayList<LabServerInfo> arrayList = new ArrayList<>();

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
                while (resultSet.next() == true) {
                    LabServerInfo labServerInfo = new LabServerInfo();

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

                    /*
                     * Add the LabServerInfo to the list
                     */
                    arrayList.add(labServerInfo);
                }
            } finally {
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
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
