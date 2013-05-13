package uq.ilabs.library.labserver.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.database.types.ExperimentQueueInfo;
import uq.ilabs.library.labserver.engine.types.QueuedExperimentInfo;

/**
 *
 * @author uqlpayne
 */
public class ExperimentQueueDB {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentQueueDB.class.getName();
    private static final Level logLevel = Level.FINEST;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Id_arg = "Id: %d";
    private static final String STRLOG_Count_arg = "Count: %d";
    private static final String STRLOG_Success_arg = "Success: %s";
    private static final String STRLOG_ExperimentIdSbName_arg = "ExperimentId: %d  SbName: '%s'";
    private static final String STRLOG_QueuedExperimentInfo_arg4 = "Position: %d  QueueLength: %d  WaitTime: %d  EstExecutionTime: %d";
    private static final String STRLOG_UnitId_arg = "UnitId: %d";
    /*
     * Database column names
     */
    private static final String STRCOL_Id = "Id";
    private static final String STRCOL_ExperimentId = "ExperimentId";
    private static final String STRCOL_SbName = "SbName";
    private static final String STRCOL_UserGroup = "UserGroup";
    private static final String STRCOL_PriorityHint = "PriorityHint";
    private static final String STRCOL_XmlSpecification = "XmlSpecification";
    private static final String STRCOL_EstimatedExecTime = "EstimatedExecTime";
    private static final String STRCOL_StatusCode = "StatusCode";
    private static final String STRCOL_UnitId = "UnitId";
    private static final String STRCOL_Cancelled = "Cancelled";
    /*
     * String constants for SQL processing
     */
    private static final String STRSQLCMD_Add = "{ ? = call Queue_Add(?,?,?,?,?,?,?) }";
    private static final String STRSQLCMD_Delete = "{ ? = call Queue_Delete(?) }";
    private static final String STRSQLCMD_GetCountBy = "{ ? = call Queue_GetCountBy(?,?,?) }";
    private static final String STRSQLCMD_RetrieveBy = "{ call Queue_RetrieveBy(?,?,?) }";
    private static final String STRSQLCMD_UpdateStatus = "{ ? = call Queue_UpdateStatus(?,?) }";
    private static final String STRSQLCMD_UpdateStatusUnitId = "{ ? = call Queue_UpdateStatusUnitId(?,?,?) }";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Connection sqlConnection;
    //</editor-fold>

    /**
     *
     * @param dbConnection
     * @throws Exception
     */
    public ExperimentQueueDB(DBConnection dbConnection) throws Exception {
        final String methodName = "ExperimentQueueDB";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that parameters are valid
             */
            if (dbConnection == null) {
                throw new NullPointerException(DBConnection.class.getSimpleName());
            }

            /*
             * Initialise local variables
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
     * @param labExperimentInfo
     * @return
     */
    public synchronized QueuedExperimentInfo Enqueue(ExperimentQueueInfo experimentQueueInfo) {
        final String methodName = "Enqueue";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                (experimentQueueInfo != null)
                ? String.format(STRLOG_ExperimentIdSbName_arg, experimentQueueInfo.getExperimentId(), experimentQueueInfo.getSbName())
                : null);

        QueuedExperimentInfo queuedExperimentInfo = null;

        try {
            /*
             * Check that parameters are valid
             */
            if (experimentQueueInfo == null) {
                throw new NullPointerException(QueuedExperimentInfo.class.getSimpleName());
            }


            experimentQueueInfo.setStatusCode(StatusCodes.Waiting);
            int id = this.Add(experimentQueueInfo);
            if (id > 0) {
                queuedExperimentInfo = GetQueuedExperimentInfo(experimentQueueInfo.getExperimentId(), experimentQueueInfo.getSbName());
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return queuedExperimentInfo;
    }

    /**
     * Get the information for the next experiment waiting in the queue. If an experiment is found, update its status to
     * running and set the unit Id. If there are no experiments waiting on the queue, null is returned.
     *
     * @param unitId The unit ID of the experiment engine that is running this experiment.
     * @return Experiment information.
     */
    public synchronized ExperimentQueueInfo Dequeue(int unitId) {
        final String methodName = "Dequeue";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_UnitId_arg, unitId));

        ExperimentQueueInfo experimentQueueInfo = null;

        /*
         * Check if a waiting experiment was found
         */
        ArrayList<ExperimentQueueInfo> arrayList = this.RetrieveBy(STRCOL_StatusCode, 0, StatusCodes.Waiting.toString());
        if (arrayList != null) {
            experimentQueueInfo = arrayList.get(0);

            /*
             * Update the experiment status and unit Id
             */
            if (this.UpdateStatusUnitId(experimentQueueInfo.getId(), StatusCodes.Running, unitId) == false) {
                experimentQueueInfo = null;
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                (experimentQueueInfo != null)
                ? String.format(STRLOG_ExperimentIdSbName_arg, experimentQueueInfo.getExperimentId(), experimentQueueInfo.getSbName())
                : null);
        return experimentQueueInfo;
    }

    /**
     *
     * @param experimentId
     * @param sbName
     * @return
     */
    public boolean Cancel(int experimentId, String sbName) {
        final String methodName = "Cancel";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentIdSbName_arg, experimentId, sbName));

        boolean success = false;

        /*
         * Can only cancel the experiment if it is waiting on the queue
         */
        ExperimentQueueInfo experimentQueueInfo = this.RetrieveByExperimentId(experimentId, sbName);
        if (experimentQueueInfo != null && experimentQueueInfo.getStatusCode() == StatusCodes.Waiting) {
            success = this.UpdateStatus(experimentQueueInfo.getId(), StatusCodes.Cancelled);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     * Get the number of experiments that are waiting on the queue.
     *
     * @return int
     */
    public int GetCountWaiting() {
        return this.GetCountBy(STRCOL_StatusCode, 0, StatusCodes.Waiting.toString());
    }

    /**
     * Get the queued experiment information for the specified experiment. If the experiment is not waiting on the
     * queue, 'experimentId' is set to zero and 'sbName' is set to null. In either case, the queue length and estimated
     * queue wait are returned.
     *
     * @param experimentId Experiment number
     * @param sbName ServiceBroker's name
     * @return Queued experiment information
     * @throws Exception
     */
    public QueuedExperimentInfo GetQueuedExperimentInfo(int experimentId, String sbName) {
        final String methodName = "GetQueuedExperimentInfo";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentIdSbName_arg, experimentId, sbName));

        QueuedExperimentInfo queuedExperimentInfo = null;
        int queueLength = 0;
        int position = 1;
        int waitTime = 0;

        /*
         * Retrieve all waiting experiments
         */
        ArrayList<ExperimentQueueInfo> arrayList = this.RetrieveByStatusCode(StatusCodes.Waiting);
        if (arrayList != null) {
            /*
             * Process the list of waiting experiments
             */
            for (ExperimentQueueInfo experimentQueueInfo : arrayList) {
                if (experimentQueueInfo.getExperimentId() == experimentId
                        && sbName != null && experimentQueueInfo.getSbName().equals(sbName) == true) {
                    /*
                     * Found it
                     */
                    queuedExperimentInfo = new QueuedExperimentInfo(experimentQueueInfo);
                    queuedExperimentInfo.setPosition(position);
                    queuedExperimentInfo.setWaitTime(waitTime);
                }

                /*
                 * Add the wait time for this experiment and increment queue length and position
                 */
                waitTime += experimentQueueInfo.getEstimatedExecTime();
                queueLength++;
                position++;
            }
        }

        /*
         * Check if the experiment was found
         */
        if (queuedExperimentInfo == null) {
            /*
             * Not found, only provide the queue length and estimated wait time
             */
            queuedExperimentInfo = new QueuedExperimentInfo();
            queuedExperimentInfo.setWaitTime(waitTime);
        }
        queuedExperimentInfo.setQueueLength(queueLength);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_QueuedExperimentInfo_arg4, queuedExperimentInfo.getPosition(), queuedExperimentInfo.getQueueLength(),
                queuedExperimentInfo.getWaitTime(), queuedExperimentInfo.getEstimatedExecTime()));

        return queuedExperimentInfo;
    }

    /**
     * Get the length of the queue and estimated queue wait time.
     *
     * @return
     */
    public WaitEstimate GetWaitEstimate() {
        final String methodName = "GetWaitEstimate";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        WaitEstimate waitEstimate = new WaitEstimate();

        try {
            /*
             * Get the queued experiment information for a non-existent experiment
             */
            QueuedExperimentInfo queuedExperimentInfo = this.GetQueuedExperimentInfo(0, null);
            if (queuedExperimentInfo != null) {
                waitEstimate.setEffectiveQueueLength(queuedExperimentInfo.getQueueLength());
                waitEstimate.setEstWait(queuedExperimentInfo.getWaitTime());
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return waitEstimate;
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
     * @return ArrayList of ExperimentQueueInfo
     */
    public ArrayList<ExperimentQueueInfo> RetrieveAll() {
        return this.RetrieveBy(null, 0, null);
    }

    /**
     *
     * @param id
     * @return ExperimentQueueInfo
     */
    public ExperimentQueueInfo RetrieveById(int id) {
        ArrayList<ExperimentQueueInfo> arrayList = this.RetrieveBy(STRCOL_Id, id, null);
        return (arrayList != null) ? arrayList.get(0) : null;
    }

    /**
     *
     * @param experimentId
     * @param sbName
     * @return
     */
    public ExperimentQueueInfo RetrieveByExperimentId(int experimentId, String sbName) {
        ArrayList<ExperimentQueueInfo> list = this.RetrieveBy(STRCOL_ExperimentId, experimentId, sbName);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @param statusCode
     * @return
     */
    public ArrayList<ExperimentQueueInfo> RetrieveByStatusCode(StatusCodes statusCode) {
        return this.RetrieveBy(STRCOL_StatusCode, 0, statusCode.toString());
    }

    /**
     *
     * @param queueId
     * @param statusCode
     * @return
     */
    public boolean UpdateStatus(int id, StatusCodes statusCode) {
        final String methodName = "UpdateStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_UpdateStatus);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, id);
                sqlStatement.setString(3, statusCode.toString());

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();
                success = (sqlStatement.getInt(1) == id);
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
     * @param queueId
     * @param statusCode
     * @param unitId
     * @return
     */
    public boolean UpdateStatusUnitId(int id, StatusCodes statusCode, int unitId) {
        final String methodName = "UpdateStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_UpdateStatusUnitId);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, id);
                sqlStatement.setString(3, statusCode.toString());
                sqlStatement.setInt(4, unitId);

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();
                success = (sqlStatement.getInt(1) == id);
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

    //================================================================================================================//
    private int Add(ExperimentQueueInfo experimentQueueInfo) {
        final String methodName = "Add";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int id = -1;



        try {
            /*
             * Check that parameters are valid
             */
            if (experimentQueueInfo == null) {
                throw new NullPointerException(ExperimentQueueInfo.class.getSimpleName());
            }

            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_Add);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, experimentQueueInfo.getExperimentId());
                sqlStatement.setString(3, experimentQueueInfo.getSbName());
                sqlStatement.setString(4, experimentQueueInfo.getUserGroup());
                sqlStatement.setInt(5, experimentQueueInfo.getPriorityHint());
                sqlStatement.setString(6, experimentQueueInfo.getXmlSpecification());
                sqlStatement.setInt(7, experimentQueueInfo.getEstimatedExecTime());
                sqlStatement.setString(8, experimentQueueInfo.getStatusCode().toString());

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
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Id_arg, id));

        return id;
    }

    /**
     *
     * @return int
     */
    private int GetCountBy(String columnName, int intval, String strval) {
        final String methodName = "GetCountBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int count = -1;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_GetCountBy);
                sqlStatement.registerOutParameter(1, Types.BIGINT);
                sqlStatement.setString(2, columnName);
                sqlStatement.setInt(3, intval);
                sqlStatement.setString(4, strval);

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();
                count = (int) sqlStatement.getLong(1);
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
                String.format(STRLOG_Count_arg, count));

        return count;
    }

    /**
     *
     * @param columnName
     * @param intval
     * @param strval
     * @return
     */
    private ArrayList<ExperimentQueueInfo> RetrieveBy(String columnName, int intval, String strval) {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ArrayList<ExperimentQueueInfo> arrayList = new ArrayList<>();

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
                    /*
                     * Found a waiting experiment, get the experiment information
                     */
                    ExperimentQueueInfo experimentQueueInfo = new ExperimentQueueInfo();

                    experimentQueueInfo.setId(resultSet.getInt(STRCOL_Id));
                    experimentQueueInfo.setExperimentId(resultSet.getInt(STRCOL_ExperimentId));
                    experimentQueueInfo.setSbName(resultSet.getString(STRCOL_SbName));
                    experimentQueueInfo.setUserGroup(resultSet.getString(STRCOL_UserGroup));
                    experimentQueueInfo.setPriorityHint(resultSet.getInt(STRCOL_PriorityHint));
                    experimentQueueInfo.setXmlSpecification(resultSet.getString(STRCOL_XmlSpecification));
                    experimentQueueInfo.setEstimatedExecTime(resultSet.getInt(STRCOL_EstimatedExecTime));
                    experimentQueueInfo.setStatusCode(StatusCodes.valueOf(resultSet.getString(STRCOL_StatusCode)));
                    experimentQueueInfo.setUnitId(resultSet.getInt(STRCOL_UnitId));
                    experimentQueueInfo.setCancelled(resultSet.getBoolean(STRCOL_Cancelled));

                    /*
                     * Add the ExperimentQueueInfo to the list
                     */
                    arrayList.add(experimentQueueInfo);
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
