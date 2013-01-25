/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.database;

import java.sql.*;
import java.util.logging.Level;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;

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
    /*
     * Database column names
     */
    private static final String STRCOL_ExperimentId = "ExperimentId";
    private static final String STRCOL_LabServerGuid = "LabServerGuid";
    /*
     * String constants for SQL processing
     */
    private static final String STRSQLCMD_Add = "{ call Experiments_Add(?) }";
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
                String.format(STRLOG_ExperimentId_arg, experimentId));

        return experimentId;
    }

    /**
     *
     * @return int
     * @throws Exception
     */
    public int GetNextExperimentId() throws Exception {
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
                String.format(STRLOG_ExperimentId_arg, experimentId));

        return experimentId;
    }

    /**
     *
     * @param experimentId
     * @return String
     * @throws Exception
     */
    public String RetrieveByExperimentId(int experimentId) throws Exception {
        final String methodName = "RetrieveByExperimentId";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        String labServerGuid = this.RetrieveBy(STRCOL_ExperimentId, experimentId, null);

        return labServerGuid;
    }

    //================================================================================================================//
    /**
     *
     * @param columnName
     * @param intval
     * @param strval
     * @return String
     * @throws Exception
     */
    public String RetrieveBy(String columnName, int intval, String strval) throws Exception {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String labServerGuid = null;

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
                    labServerGuid = resultSet.getString(STRCOL_LabServerGuid);
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

        return labServerGuid;
    }
}
