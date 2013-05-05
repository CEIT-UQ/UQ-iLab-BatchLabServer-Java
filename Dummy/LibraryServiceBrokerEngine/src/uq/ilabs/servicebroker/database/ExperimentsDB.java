/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.servicebroker.database.types.ExperimentInfo;

/**
 *
 * @author uqlpayne
 */
public class ExperimentsDB {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    public static final String STR_ClassName = ExperimentsDB.class.getName();
    private static final Level logLevel = Level.FINEST;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_ExperimentId_arg = "ExperimentId: %d";
    private static final String STRLOG_Count_arg = "Count: %d";
    private static final String STRLOG_Success_arg = "Success: %s";
    /*
     * Database column names
     */
    private static final String STRCOL_ExperimentId = "ExperimentId";
    private static final String STRCOL_LabServerGuid = "LabServerGuid";
    /*
     * String constants for SQL processing
     */
    private static final String STRSQLCMD_Add = "{ call Experiments_Add(?) }";
    private static final String STRSQLCMD_Delete = "{ ? = call Experiments_Delete(?) }";
    private static final String STRSQLCMD_GetNextExperimentId = "{ call Experiments_GetNextExperimentId() }";
    private static final String STRSQLCMD_RetrieveBy = "{ call Experiments_RetrieveBy(?,?,?) }";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Connection sqlConnection;
    //</editor-fold>

    /**
     *
     * @param dbConnection
     */
    public ExperimentsDB(DBConnection dbConnection) throws Exception {
        final String methodName = "ExperimentsDB";
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
     * @param labServerGuid
     * @return int
     * @throws Exception
     */
    public int Add(String labServerGuid) throws Exception {
        final String methodName = "Add";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int experimentId = -1;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_Add);
                sqlStatement.setString(1, labServerGuid);

                /*
                 * Execute the stored procedure
                 */
                ResultSet resultSet = sqlStatement.executeQuery();

                /*
                 * Process the results of the query - only want the first result
                 */
                if (resultSet.next() == true) {
                    experimentId = resultSet.getInt(1);
                }
            } catch (Exception ex) {
                throw ex;
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
                String.format(STRLOG_ExperimentId_arg, experimentId));

        return experimentId;
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
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return int
     * @throws Exception
     */
    public int GetNextExperimentId() {
        final String methodName = "GetNextExperimentId";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int experimentId = -1;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_GetNextExperimentId);

                /*
                 * Execute the stored procedure
                 */
                ResultSet resultSet = sqlStatement.executeQuery();
                if (resultSet.next() == true) {
                    experimentId = resultSet.getInt(1);
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
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        return experimentId;
    }

    /**
     *
     * @return
     */
    public ArrayList<ExperimentInfo> RetrieveAll() {
        return this.RetrieveBy(null, 0, null);
    }

    /**
     *
     * @param experimentId
     * @return
     * @throws Exception
     */
    public ExperimentInfo RetrieveByExperimentId(int experimentId) throws Exception {
        ArrayList<ExperimentInfo> arrayList = this.RetrieveBy(STRCOL_ExperimentId, experimentId, null);
        return (arrayList != null) ? arrayList.get(0) : null;
    }

    //================================================================================================================//
    /**
     *
     * @param columnName
     * @param intval
     * @param strval
     * @return
     * @throws Exception
     */
    private ArrayList<ExperimentInfo> RetrieveBy(String columnName, int intval, String strval) {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ArrayList<ExperimentInfo> arrayList = new ArrayList<>();

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
                    ExperimentInfo experimentInfo = new ExperimentInfo();

                    experimentInfo.setExperimentId(resultSet.getInt(STRCOL_ExperimentId));
                    experimentInfo.setLabServerGuid(resultSet.getString(STRCOL_LabServerGuid));

                    /*
                     * Add the ExperimentInfo to the list
                     */
                    arrayList.add(experimentInfo);
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
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Count_arg, arrayList.size()));

        return (arrayList.size() > 0) ? arrayList : null;
    }
}
