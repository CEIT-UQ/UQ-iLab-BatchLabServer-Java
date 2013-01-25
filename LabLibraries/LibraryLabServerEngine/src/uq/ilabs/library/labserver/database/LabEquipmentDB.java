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
import uq.ilabs.library.labserver.database.types.LabEquipmentInfo;

/**
 *
 * @author uqlpayne
 */
public class LabEquipmentDB {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    public static final String STR_ClassName = LabEquipmentDB.class.getName();
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
    private static final String STRCOL_ServiceUrl = "ServiceUrl";
    private static final String STRCOL_Passkey = "Passkey";
    private static final String STRCOL_Enabled = "Enabled";
    private static final String STRCOL_DateCreated = "DateCreated";
    private static final String STRCOL_DateModified = "DateModified";
    /*
     * String constants for SQL processing
     */
    private static final String STRSQLCMD_Add = "{ ? = call LabEquipment_Add(?,?,?) }";
    private static final String STRSQLCMD_Delete = "{ ? = call LabEquipment_Delete(?) }";
    private static final String STRSQLCMD_RetrieveBy = "{ call LabEquipment_RetrieveBy(?,?,?) }";
    private static final String STRSQLCMD_Update = "{ ? = call LabEquipment_Update(?,?,?,?) }";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Connection sqlConnection;
    //</editor-fold>

    /**
     *
     * @param dbConnection
     * @throws Exception
     */
    public LabEquipmentDB(DBConnection dbConnection) throws Exception {
        final String methodName = "LabEquipmentDB";
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
     * @param labEquipmentInfo
     * @return int
     */
    public int Add(LabEquipmentInfo labEquipmentInfo) {
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
                sqlStatement.setString(2, labEquipmentInfo.getServiceUrl());
                sqlStatement.setString(3, labEquipmentInfo.getPasskey());
                sqlStatement.setBoolean(4, labEquipmentInfo.isEnabled());

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
     * @return ArrayList of LabEquipmentInfo
     */
    public ArrayList<LabEquipmentInfo> RetrieveAll() {
        return this.RetrieveBy(null, 0, null);
    }

    /**
     *
     * @param id
     * @return LabEquipmentInfo
     */
    public LabEquipmentInfo RetrieveById(int id) {
        ArrayList<LabEquipmentInfo> list = this.RetrieveBy(STRCOL_Id, id, null);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @return ArrayList of LabEquipmentInfo
     */
    public ArrayList<LabEquipmentInfo> RetrieveByServiceUrl(String serviceUrl) {
        return this.RetrieveBy(STRCOL_ServiceUrl, 0, serviceUrl);
    }

    /**
     *
     * @param labEquipmentInfo
     * @return boolean
     */
    public boolean Update(LabEquipmentInfo labEquipmentInfo) {
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
                sqlStatement.setInt(2, labEquipmentInfo.getId());
                sqlStatement.setString(3, labEquipmentInfo.getServiceUrl());
                sqlStatement.setString(4, labEquipmentInfo.getPasskey());
                sqlStatement.setBoolean(5, labEquipmentInfo.isEnabled());

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();

                /*
                 * Get the result
                 */
                success = ((int) sqlStatement.getInt(1) == labEquipmentInfo.getId());
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
     * @param intval
     * @param strval
     * @return
     */
    private ArrayList<LabEquipmentInfo> RetrieveBy(String columnName, int intval, String strval) {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ArrayList<LabEquipmentInfo> list = new ArrayList<>();

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
                 * Process the results of the query
                 */
                while (resultSet.next() == true) {
                    LabEquipmentInfo labEquipmentInfo = new LabEquipmentInfo();
                    labEquipmentInfo.setId(resultSet.getInt(STRCOL_Id));
                    labEquipmentInfo.setServiceUrl(resultSet.getString(STRCOL_ServiceUrl));
                    labEquipmentInfo.setPasskey(resultSet.getString(STRCOL_Passkey));
                    labEquipmentInfo.setEnabled(resultSet.getBoolean(STRCOL_Enabled));

                    Calendar calendar;
                    Timestamp timestamp;
                    if ((timestamp = resultSet.getTimestamp(STRCOL_DateCreated)) != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTime(timestamp);
                        labEquipmentInfo.setDateCreated(calendar);
                    }
                    if ((timestamp = resultSet.getTimestamp(STRCOL_DateModified)) != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTime(timestamp);
                        labEquipmentInfo.setDateModified(calendar);
                    }

                    /*
                     * Add the LabEquipmentInfo to the list
                     */
                    list.add(labEquipmentInfo);
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
