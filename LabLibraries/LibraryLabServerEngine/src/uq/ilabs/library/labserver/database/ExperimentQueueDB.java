package uq.ilabs.library.labserver.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labserver.engine.types.LabExperimentInfo;
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
    private static final String STRLOG_ExperimentIdSbName_arg = "ExperimentId: %d  SbName: '%s'";
    private static final String STRLOG_QueuedExperimentInfo_arg4 = "Position: %d  QueueLength: %d  WaitTime: %d  EstExecutionTime: %d";
    private static final String STRLOG_UnitId_arg = "UnitId: %d";
    private static final String STRLOG_StatusCode_arg = "StatusCode: %s";
    private static final String STRLOG_Success_arg = "Success: %s";
    private static final String STRLOG_Count_arg = "Count: %d";
    private static final String STRLOG_Null = "null";
    /*
     * Database column names
     */
    private static final String STRCOL_Id = "id";
    private static final String STRCOL_ExperimentId = "experimentid";
    private static final String STRCOL_SbName = "sbname";
    private static final String STRCOL_UserGroup = "usergroup";
    private static final String STRCOL_PriorityHint = "priorityhint";
    private static final String STRCOL_XmlSpecification = "XmlSpecification";
    private static final String STRCOL_EstExecutionTime = "estexecutiontime";
    private static final String STRCOL_StatusCode = "statuscode";
    private static final String STRCOL_UnitId = "unitid";
    private static final String STRCOL_Cancelled = "cancelled";
    private static final String STRCOL_DateCreated = "datecreated";
    /*
     * String constants for SQL processing
     */
    private static final String STRSQLCMD_Add = "{ ? = call Queue_Add(?,?,?,?,?,?,?) }";
    private static final String STRSQLCMD_GetCountBy = "{ ? = call Queue_GetCountBy(?,?,?) }";
    private static final String STRSQLCMD_RetrieveBy = "{ call Queue_RetrieveBy(?,?,?) }";
    private static final String STRSQLCMD_RetrieveAllWithStatus = "{ call Queue_RetrieveAllWithStatus(?) }";
    private static final String STRSQLCMD_UpdateStatus = "{ call Queue_UpdateStatus(?,?) }";
    private static final String STRSQLCMD_UpdateStatusUnitId = "{ call Queue_UpdateStatusUnitId(?,?,?) }";
    /*
     * String constants for the XML experiment queue template
     */
    private static final String STRXML_ExperimentQueue = "experimentQueue";
    private static final String STRXML_Experiment = "experiment";
    private static final String STRXML_ExperimentId = "experimentId";
    private static final String STRXML_SbName = "sbName";
    private static final String STRXML_UserGroup = "userGroup";
    private static final String STRXML_PriorityHint = "priorityHint";
    private static final String STRXML_Specification = "specification";
    private static final String STRXML_EstExecutionTime = "estExecutionTime";
    private static final String STRXML_Cancelled = "cancelled";
    /*
     * XML experiment queue template
     */
    private static final String STRXMLDOC_ExperimentQueueTemplate =
            "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
            + "<" + STRXML_ExperimentQueue + ">"
            + "<" + STRXML_Experiment + ">"
            + "<" + STRXML_ExperimentId + " />"
            + "<" + STRXML_SbName + " />"
            + "<" + STRXML_UserGroup + " />"
            + "<" + STRXML_PriorityHint + " />"
            + "<" + STRXML_Specification + " />"
            + "<" + STRXML_EstExecutionTime + " />"
            + "<" + STRXML_Cancelled + " />"
            + "</" + STRXML_Experiment + ">"
            + "</" + STRXML_ExperimentQueue + ">";
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
    public synchronized QueuedExperimentInfo Enqueue(LabExperimentInfo labExperimentInfo) {
        final String methodName = "Enqueue";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                (labExperimentInfo != null)
                ? String.format(STRLOG_ExperimentIdSbName_arg, labExperimentInfo.getExperimentId(), labExperimentInfo.getSbName())
                : null);

        QueuedExperimentInfo queuedExperimentInfo = null;

        try {
            /*
             * Check that parameters are valid
             */
            if (labExperimentInfo == null) {
                throw new NullPointerException(LabExperimentInfo.class.getSimpleName());
            }

            int queueId = -1;
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_Add);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, labExperimentInfo.getExperimentId());
                sqlStatement.setString(3, labExperimentInfo.getSbName());
                sqlStatement.setString(4, labExperimentInfo.getUserGroup());
                sqlStatement.setInt(5, labExperimentInfo.getPriorityHint());
                sqlStatement.setString(6, labExperimentInfo.getXmlSpecification());
                sqlStatement.setInt(7, labExperimentInfo.getEstExecutionTime());
                sqlStatement.setString(8, StatusCodes.Waiting.toString());

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();

                /*
                 * Get the result
                 */
                queueId = (int) sqlStatement.getInt(1);
            } catch (Exception ex) {
                throw ex;
            } finally {
                try {
                    sqlStatement.close();
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
            }

            labExperimentInfo.setQueueId(queueId);
            queuedExperimentInfo = GetQueuedExperimentInfo(labExperimentInfo.getExperimentId(), labExperimentInfo.getSbName());
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
    public synchronized LabExperimentInfo Dequeue(int unitId) {
        final String methodName = "Dequeue";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_UnitId_arg, unitId));

        LabExperimentInfo labExperimentInfo = null;

        /*
         * Check if a waiting experiment was found
         */
        ArrayList<LabExperimentInfo> list = this.RetrieveBy(STRCOL_StatusCode, 0, StatusCodes.Waiting.toString());
        if (list != null) {
            labExperimentInfo = list.get(0);

            /*
             * Update the experiment status and unit Id
             */
            if (this.UpdateStatus(labExperimentInfo.getQueueId(), StatusCodes.Running, unitId) == false) {
                labExperimentInfo = null;
            }
        }

        if (labExperimentInfo != null) {
            Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                    String.format(STRLOG_ExperimentIdSbName_arg, labExperimentInfo.getExperimentId(), labExperimentInfo.getSbName()));
        } else {
            Logfile.WriteCompleted(logLevel, STR_ClassName, methodName, STRLOG_Null);
        }

        return labExperimentInfo;
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
        LabExperimentInfo labExperimentInfo = this.RetrieveByExperimentId(experimentId, sbName);
        if (labExperimentInfo != null && labExperimentInfo.getStatusCode() == StatusCodes.Waiting) {
            success = this.UpdateStatus(labExperimentInfo.getQueueId(), StatusCodes.Cancelled);
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
     * Get the status of the specified experiment. If the experiment is not found then the status of 'UNKNOWN' will be
     * returned.
     *
     * @param experimentId The experiment identification number
     * @param sbName The name of the ServiceBroker
     * @return the status of the specified experiment
     */
    public StatusCodes GetStatus(int experimentId, String sbName) {
        final String methodName = "GetStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentIdSbName_arg, experimentId, sbName));

        StatusCodes statusCode = StatusCodes.Unknown;

        LabExperimentInfo labExperimentInfo = this.RetrieveByExperimentId(experimentId, sbName);
        if (labExperimentInfo != null) {
            statusCode = labExperimentInfo.getStatusCode();
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_StatusCode_arg, statusCode.toString()));

        return statusCode;
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
        ArrayList<LabExperimentInfo> list = this.RetrieveByStatus(StatusCodes.Waiting);
        if (list != null) {
            /*
             * Process the list of waiting experiments
             */
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                LabExperimentInfo labExperimentInfo = (LabExperimentInfo) iterator.next();

                /*
                 * Check if this is the experiment we are looking for
                 */
                if (labExperimentInfo.getExperimentId() == experimentId
                        && sbName != null && labExperimentInfo.getSbName().equals(sbName) == true) {
                    /*
                     * Found it
                     */
                    queuedExperimentInfo = new QueuedExperimentInfo();
                    queuedExperimentInfo.setLabExperimentInfo(labExperimentInfo);
                    queuedExperimentInfo.setPosition(position);
                    queuedExperimentInfo.setWaitTime(waitTime);
                }

                /*
                 * Add the wait time for this experiment and increment queue length and position
                 */
                waitTime += labExperimentInfo.getEstExecutionTime();
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
                String.format(STRLOG_QueuedExperimentInfo_arg4, queuedExperimentInfo.getPosition(), queuedExperimentInfo.getQueueLength(), queuedExperimentInfo.getWaitTime(),
                (queuedExperimentInfo.getLabExperimentInfo() != null) ? queuedExperimentInfo.getLabExperimentInfo().getEstExecutionTime() : 0));

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

        /*
         * Get the queued experiment information for a non-existent experiment
         */
        try {
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
     * @param experimentId
     * @param sbName
     * @return
     */
    public LabExperimentInfo RetrieveByExperimentId(int experimentId, String sbName) {
        ArrayList<LabExperimentInfo> list = this.RetrieveBy(STRCOL_ExperimentId, experimentId, sbName);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @param statusCode
     * @return
     */
    public ArrayList<LabExperimentInfo> RetrieveByStatus(StatusCodes statusCode) {
        return this.RetrieveBy(STRCOL_StatusCode, 0, statusCode.toString());
    }

    /**
     *
     * @param queueId
     * @param statusCode
     * @param unitId
     * @return
     */
    public boolean UpdateStatus(int queueId, StatusCodes statusCode, int unitId) {
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
                sqlStatement.setInt(1, queueId);
                sqlStatement.setString(2, statusCode.toString());
                sqlStatement.setInt(3, unitId);

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();

                success = true;
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
     * @param queueId
     * @param statusCode
     * @return
     */
    public boolean UpdateStatus(int queueId, StatusCodes statusCode) {
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
                sqlStatement.setInt(1, queueId);
                sqlStatement.setString(2, statusCode.toString());

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();

                success = true;
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
     * @param status
     * @return
     * @throws Exception
     */
    public synchronized LabExperimentInfo[] RetrieveAllWithStatus(StatusCodes statusCode) {
        final String methodName = "RetrieveAllWithStatus";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        LabExperimentInfo[] labExperimentInfoArray = null;

        try {
            ArrayList<LabExperimentInfo> labExperimentInfoList = new ArrayList<>();

            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_RetrieveAllWithStatus);
                sqlStatement.setString(1, statusCode.toString());

                /*
                 * Execute the stored procedure
                 */
                ResultSet resultSet = sqlStatement.executeQuery();

                /*
                 * Process the results of the query
                 */
                while (resultSet.next() == true) {
                    /*
                     * Found a waiting experiment, get experiment information
                     */
                    LabExperimentInfo labExperimentInfo = new LabExperimentInfo();
                    labExperimentInfo.setQueueId(resultSet.getInt(STRCOL_Id));
                    labExperimentInfo.setExperimentId(resultSet.getInt(STRCOL_ExperimentId));
                    labExperimentInfo.setSbName(resultSet.getString(STRCOL_SbName));
                    labExperimentInfo.setUserGroup(resultSet.getString(STRCOL_UserGroup));
                    labExperimentInfo.setPriorityHint(resultSet.getInt(STRCOL_PriorityHint));
                    labExperimentInfo.setXmlSpecification(resultSet.getString(STRCOL_XmlSpecification));
                    labExperimentInfo.setEstExecutionTime(resultSet.getInt(STRCOL_EstExecutionTime));
                    labExperimentInfo.setStatusCode(StatusCodes.valueOf(resultSet.getString(STRCOL_StatusCode)));
                    labExperimentInfo.setCancelled(resultSet.getBoolean(STRCOL_Cancelled));

                    /*
                     * Add the experiment info to the list
                     */
                    labExperimentInfoList.add(labExperimentInfo);
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
            labExperimentInfoArray = new LabExperimentInfo[labExperimentInfoList.size()];
            labExperimentInfoList.toArray(labExperimentInfoArray);
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Count_arg, labExperimentInfoArray.length));

        return labExperimentInfoArray;
    }

    /**
     *
     * @return
     */
    public String RetrieveWaitingToXmlString() {
        final String methodName = "RetrieveWaitingToXmlString";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String xmlString = null;

        try {
            /*
             * Get all waiting experiments and convert to XML
             */
            LabExperimentInfo[] labExperimentInfoArray = this.RetrieveAllWithStatus(StatusCodes.Waiting);
            xmlString = ToXmlString(labExperimentInfoArray);
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return xmlString;
    }

    //================================================================================================================//
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
                sqlStatement.setString(2, (columnName != null ? columnName.toLowerCase() : null));
                sqlStatement.setInt(3, intval);
                sqlStatement.setString(4, strval);

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
    private ArrayList<LabExperimentInfo> RetrieveBy(String columnName, int intval, String strval) {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ArrayList<LabExperimentInfo> list = new ArrayList<>();

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
                    /*
                     * Found a waiting experiment, get the experiment information
                     */
                    LabExperimentInfo labExperimentInfo = new LabExperimentInfo();
                    labExperimentInfo.setQueueId(resultSet.getInt(STRCOL_Id));
                    labExperimentInfo.setExperimentId(resultSet.getInt(STRCOL_ExperimentId));
                    labExperimentInfo.setSbName(resultSet.getString(STRCOL_SbName));
                    labExperimentInfo.setUserGroup(resultSet.getString(STRCOL_UserGroup));
                    labExperimentInfo.setPriorityHint(resultSet.getInt(STRCOL_PriorityHint));
                    labExperimentInfo.setXmlSpecification(resultSet.getString(STRCOL_XmlSpecification));
                    labExperimentInfo.setEstExecutionTime(resultSet.getInt(STRCOL_EstExecutionTime));
                    labExperimentInfo.setStatusCode(StatusCodes.valueOf(resultSet.getString(STRCOL_StatusCode)));
                    labExperimentInfo.setUnitId(resultSet.getInt(STRCOL_UnitId));
                    labExperimentInfo.setCancelled(resultSet.getBoolean(STRCOL_Cancelled));

                    /*
                     * Add the info to the list
                     */
                    list.add(labExperimentInfo);
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

    /**
     * Take the array of experiments and convert to an XML string.
     *
     * @param labExperimentInfoArray an array of experiment information
     * @return an XML string containing the experiment information. If an error occurred return null.
     */
    private String ToXmlString(LabExperimentInfo[] labExperimentInfoArray) {
        final String methodName = "ToXmlString";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Catch all exceptions thrown and return an empty string if an error occurred
         */
        String xmlExperimentQueue = null;
        try {
            /*
             * Check that parameters are valid
             */
            if (labExperimentInfoArray == null) {
                throw new NullPointerException(LabExperimentInfo.class.getSimpleName() + "[]");
            }

            /*
             * Load the experiment queue XML template string into a document
             */
            Document document = XmlUtilities.GetDocumentFromString(STRXMLDOC_ExperimentQueueTemplate);
            Node rootNode = XmlUtilities.GetRootNode(document, STRXML_ExperimentQueue);
            Node experimentNode = XmlUtilities.GetChildNode(rootNode, STRXML_Experiment);

            /*
             * Make a copy of the experiment node and remove it
             */
            Node experimentNodeCopy = experimentNode.cloneNode(true);
            rootNode.removeChild(experimentNode);

            /*
             * Take the information for each experiment and put into the XML document
             */
            for (int i = 0; i < labExperimentInfoArray.length; i++) {
                LabExperimentInfo labExperimentInfo = labExperimentInfoArray[i];

                /*
                 * Make a copy of the experiment node copy and fill it with values from the experiment information
                 */
                Node node = experimentNodeCopy.cloneNode(true);
                XmlUtilities.SetChildValue(node, STRXML_ExperimentId, labExperimentInfo.getExperimentId());
                XmlUtilities.SetChildValue(node, STRXML_SbName, labExperimentInfo.getSbName());
                XmlUtilities.SetChildValue(node, STRXML_UserGroup, labExperimentInfo.getUserGroup());
                XmlUtilities.SetChildValue(node, STRXML_PriorityHint, labExperimentInfo.getPriorityHint());
                XmlUtilities.SetChildValue(node, STRXML_Specification, labExperimentInfo.getXmlSpecification());
                XmlUtilities.SetChildValue(node, STRXML_EstExecutionTime, labExperimentInfo.getEstExecutionTime());
                XmlUtilities.SetChildValue(node, STRXML_Cancelled, labExperimentInfo.isCancelled());

                /*
                 * Add the experiment node to the document
                 */
                rootNode.appendChild(node);
            }

            /*
             * Convert the XML document to a string
             */
            xmlExperimentQueue = XmlUtilities.ToXmlString(document);
        } catch (NullPointerException | XmlUtilitiesException | DOMException ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return xmlExperimentQueue;
    }
}
