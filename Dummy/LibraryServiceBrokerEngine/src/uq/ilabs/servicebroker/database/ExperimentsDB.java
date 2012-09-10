/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.database;

import java.sql.*;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class ExperimentsDB {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    public static final String STR_ClassName = ExperimentsDB.class.getName();
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_ExperimentId_arg = "experimentId: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_DBConnection = "dBConnection";
    /*
     * Database column names - must be lowercase
     */
    private static final String COL_ExperimentId = "experimentid";
    /*
     * String constants for SQL processing
     */
    private static final String STRSQLCMD_Add = "{ call Experiments_Add(?) }";
    private static final String STRSQLCMD_GetNextExperimentId = "{ call Experiments_GetNextExperimentId() }";
    private static final String STRSQLCMD_RetrieveBy = "{ call Experiments_RetrieveBy(?,?,?) }";
    /*
     * String constants for SQL result sets
     */
    private static final String STRSQL_ExperimentId = "ExperimentId";
    private static final String STRSQL_LabServerGuid = "LabServerGuid";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Connection sqlConnection;
    //</editor-fold>

    /**
     *
     * @param dbConnection
     */
    public ExperimentsDB(DBConnection dbConnection) throws SQLException {
        final String STR_MethodName = "ExperimentsDB";
        Logfile.WriteCalled(STR_ClassName, STR_MethodName);

        /*
         * Check that parameters are valid
         */
        if (dbConnection == null) {
            throw new NullPointerException(STRERR_DBConnection);
        }

        /*
         * Initialise locals
         */
        this.sqlConnection = dbConnection.getConnection();

        Logfile.WriteCompleted(STR_ClassName, STR_MethodName);
    }

    /**
     *
     * @param labServerGuid
     * @return
     * @throws Exception
     */
    public int Add(String labServerGuid) throws Exception {
        final String STR_MethodName = "Add";
        Logfile.WriteCalled(STR_ClassName, STR_MethodName);

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
                    Logfile.WriteException(STR_ClassName, STR_MethodName, ex);
                }
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, STR_MethodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        return experimentId;
    }

    /**
     *
     * @return @throws Exception
     */
    public int GetNextExperimentId() throws Exception {
        final String STR_MethodName = "GetNextExperimentId";
        Logfile.WriteCalled(STR_ClassName, STR_MethodName);

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
                    Logfile.WriteException(STR_ClassName, STR_MethodName, ex);
                }
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, STR_MethodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        return experimentId;
    }

    /**
     *
     * @return @throws Exception
     */
    public String RetrieveByExperimentId(int experimentId) throws Exception {
        final String STR_MethodName = "RetrieveByExperimentId";
        Logfile.WriteCalled(STR_ClassName, STR_MethodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        String labServerGuid = null;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_RetrieveBy);
                sqlStatement.setString(1, COL_ExperimentId);
                sqlStatement.setInt(2, experimentId);
                sqlStatement.setString(3, null);

                /*
                 * Execute the stored procedure
                 */
                ResultSet resultSet = sqlStatement.executeQuery();

                /*
                 * Process the results of the query - only want the first result
                 */
                if (resultSet.next() == true) {
                    labServerGuid = resultSet.getString(STRSQL_LabServerGuid);
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                try {
                    sqlStatement.close();
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, STR_MethodName, ex);
                }
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, STR_MethodName);

        return labServerGuid;
    }
}
