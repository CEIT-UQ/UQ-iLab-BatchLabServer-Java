package uq.ilabs.library.labserver.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labserver.engine.types.ExperimentStatisticInfo;
import uq.ilabs.library.labserver.engine.types.LabExperimentInfo;
import uq.ilabs.library.labserver.engine.types.QueuedExperimentInfo;

/**
 *
 * @author uqlpayne
 */
public class ExperimentStatisticsDB {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentStatisticsDB.class.getName();
    private static final Level logLevel = Level.FINEST;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Id_arg = "Id: %d";
    private static final String STRLOG_ExperimentIdSbName_arg2 = "ExperimentId: %d  SbName: '%s'";
    private static final String STRLOG_ExperimentIdSbNameUnitId_arg3 = "ExperimentId: %d  SbName: '%s'  UnitId: %d";
    private static final String STRLOG_Count_arg = "Count: %d";
    private static final String STRLOG_Success_arg = "Success: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_QueuedExperimentInfo = "queuedExperimentInfo";
    private static final String STRERR_StatisticsInfoList = "statisticsInfoList";
    /*
     * Database column names
     */
    private static final String STRCOL_Id = "Id";
    private static final String STRCOL_ExperimentId = "ExperimentId";
    private static final String STRCOL_SbName = "SbName";
    private static final String STRCOL_UserGroup = "UserGroup";
    private static final String STRCOL_PriorityHint = "PriorityHint";
    private static final String STRCOL_EstimatedExecTime = "EstimatedExecTime";
    private static final String STRCOL_TimeSubmitted = "TimeSubmitted";
    private static final String STRCOL_QueueLength = "QueueLength";
    private static final String STRCOL_EstimatedWaitTime = "EstimatedWaittime";
    private static final String STRCOL_TimeStarted = "TimeStarted";
    private static final String STRCOL_UnitId = "UnitId";
    private static final String STRCOL_TimeCompleted = "TimeCompleted";
    private static final String STRCOL_Cancelled = "Cancelled";
    /*
     * String constants for SQL processing
     */
    private static final String STRSQLCMD_Add = "{ ? = call Statistics_Add(?,?,?,?,?,?,?) }";
    private static final String STRSQLCMD_Delete = "{ ? = call Statistics_Delete(?) }";
    private static final String STRSQLCMD_RetrieveBy = "{ call Statistics_RetrieveBy(?,?,?) }";
    private static final String STRSQLCMD_UpdateCancelled = "{ call Statistics_UpdateCancelled(?,?) }";
    private static final String STRSQLCMD_UpdateCompleted = "{ call Statistics_UpdateCompleted(?,?) }";
    private static final String STRSQLCMD_UpdateStarted = "{ call Statistics_UpdateStarted(?,?,?) }";
    /*
     * String constants for XML elements
     */
    private static final String STRXML_Statistics = "statistics";
    private static final String STRXML_Experiment = "experiment";
    private static final String STRXML_ExperimentId = "experimentId";
    private static final String STRXML_SbName = "sbName";
    private static final String STRXML_UserGroup = "userGroup";
    private static final String STRXML_PriorityHint = "priorityHint";
    private static final String STRXML_EstimatedExecTime = "estimatedExecTime";
    private static final String STRXML_TimeSubmitted = "timeSubmitted";
    private static final String STRXML_QueueLength = "queueLength";
    private static final String STRXML_EstimatedWaitTime = "estimatedWaitTime";
    private static final String STRXML_TimeStarted = "timeStarted";
    private static final String STRXML_UnitId = "unitId";
    private static final String STRXML_TimeCompleted = "timeCompleted";
    private static final String STRXML_ActualExecTime = "actualExecTime";
    private static final String STRXML_Cancelled = "cancelled";
    /*
     * XML experiment statistics template
     */
    private static final String STRXMLDOC_ExperimentStatisticsTemplate =
            "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
            + "<" + STRXML_Statistics + ">"
            + " <" + STRXML_Experiment + ">"
            + "  <" + STRXML_ExperimentId + " />"
            + "  <" + STRXML_SbName + " />"
            + "  <" + STRXML_UserGroup + " />"
            + "  <" + STRXML_PriorityHint + " />"
            + "  <" + STRXML_EstimatedExecTime + " />"
            + "  <" + STRXML_TimeSubmitted + " />"
            + "  <" + STRXML_QueueLength + " />"
            + "  <" + STRXML_EstimatedWaitTime + " />"
            + "  <" + STRXML_TimeStarted + " />"
            + "  <" + STRXML_UnitId + " />"
            + "  <" + STRXML_TimeCompleted + " />"
            + "  <" + STRXML_ActualExecTime + " />"
            + "  <" + STRXML_Cancelled + " />"
            + " </" + STRXML_Experiment + ">"
            + "</" + STRXML_Statistics + ">";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Connection sqlConnection;
    //</editor-fold>

    public ExperimentStatisticsDB(DBConnection dbConnection) throws Exception {
        final String methodName = "ExperimentStatisticsDB";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that all parameters are valid
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
     * @param queuedExperimentInfo
     * @return
     */
    public int Submitted(QueuedExperimentInfo queuedExperimentInfo) {
        final String methodName = "Submitted";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int id = -1;

        try {
            /*
             * Check that parameters are valid
             */
            if (queuedExperimentInfo == null) {
                throw new NullPointerException(STRERR_QueuedExperimentInfo);
            }

            LabExperimentInfo labExperimentInfo = queuedExperimentInfo.getLabExperimentInfo();
            Logfile.Write(String.format(STRLOG_ExperimentIdSbName_arg2, labExperimentInfo.getExperimentId(), labExperimentInfo.getSbName()));

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
                sqlStatement.setInt(6, labExperimentInfo.getEstExecutionTime());
                sqlStatement.setInt(7, queuedExperimentInfo.getPosition() - 1);
                sqlStatement.setInt(8, queuedExperimentInfo.getWaitTime());

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
     * @param experimentId
     * @param sbName
     * @param unitId
     * @return
     */
    public boolean Started(int experimentId, String sbName, int unitId) {
        final String methodName = "Started";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentIdSbNameUnitId_arg3, experimentId, sbName, unitId));

        boolean success = false;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_UpdateStarted);
                sqlStatement.setInt(1, experimentId);
                sqlStatement.setString(2, sbName);
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
     * @param experimentId
     * @param sbName
     * @return
     */
    public boolean Completed(int experimentId, String sbName) {
        final String methodName = "Completed";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentIdSbName_arg2, experimentId, sbName));

        boolean success = false;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_UpdateCompleted);
                sqlStatement.setInt(1, experimentId);
                sqlStatement.setString(2, sbName);

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
     * @param experimentId
     * @param sbName
     * @return
     */
    public boolean Cancelled(int experimentId, String sbName) {
        final String methodName = "Cancelled";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentIdSbName_arg2, experimentId, sbName));

        boolean success = false;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_UpdateCancelled);
                sqlStatement.setInt(1, experimentId);
                sqlStatement.setString(2, sbName);

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
     * @param id
     * @return StatisticsInfo
     */
    public ExperimentStatisticInfo RetrieveById(int id) {
        ArrayList<ExperimentStatisticInfo> list = this.RetrieveBy(STRCOL_Id, id, null);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @param experimentId
     * @param sbName
     * @return
     */
    public ExperimentStatisticInfo RetrieveByExperimentId(int experimentId, String sbName) {
        ArrayList<ExperimentStatisticInfo> list = this.RetrieveBy(STRCOL_ExperimentId, experimentId, sbName);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @param userGroup
     * @return
     */
    public ExperimentStatisticInfo RetrieveByUserGroup(String userGroup) {
        ArrayList<ExperimentStatisticInfo> list = this.RetrieveBy(STRCOL_UserGroup, 0, userGroup);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @return ArrayList<StatisticsInfo>
     */
    public ArrayList<ExperimentStatisticInfo> RetrieveAll() {
        return this.RetrieveBy(null, 0, null);
    }

    /**
     *
     * @return
     */
    public String RetrieveAllToXmlString() {
        final String methodName = "RetrieveAllToXmlString";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String xmlString = null;

        try {
            /*
             * Get all of the statistics information and convert to XML
             */
            xmlString = this.ToXmlString(this.RetrieveAll());
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return xmlString;
    }

    //================================================================================================================//
    /**
     *
     * @return @throws Exception
     */
    private ArrayList<ExperimentStatisticInfo> RetrieveBy(String columnName, int intval, String strval) {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ArrayList<ExperimentStatisticInfo> list = new ArrayList<>();

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
                 * Process the results of the query
                 */
                while (resultSet.next() == true) {
                    ExperimentStatisticInfo info = new ExperimentStatisticInfo();
                    info.setId(resultSet.getInt(STRCOL_Id));
                    info.setExperimentId(resultSet.getInt(STRCOL_ExperimentId));
                    info.setSbName(resultSet.getString(STRCOL_SbName));
                    info.setUserGroup(resultSet.getString(STRCOL_UserGroup));
                    info.setPriorityHint(resultSet.getInt(STRCOL_PriorityHint));
                    info.setEstimatedExecTime(resultSet.getInt(STRCOL_EstimatedExecTime));
                    info.setTimeSubmitted(resultSet.getTimestamp(STRCOL_TimeSubmitted));
                    info.setQueueLength(resultSet.getInt(STRCOL_QueueLength));
                    info.setEstimatedWaitTime(resultSet.getInt(STRCOL_EstimatedWaitTime));
                    info.setTimeStarted(resultSet.getTimestamp(STRCOL_TimeStarted));
                    info.setUnitId(resultSet.getInt(STRCOL_UnitId));
                    info.setTimeCompleted(resultSet.getTimestamp(STRCOL_TimeCompleted));
                    info.setCancelled(resultSet.getBoolean(STRCOL_Cancelled));

                    /*
                     * Calculate the actual execution time
                     */
                    long startTime = info.getTimeStarted().getTime();
                    if (startTime != 0) {
                        long endTime = info.getTimeCompleted().getTime();
                        info.setActualExecTime((int) ((endTime - startTime) / 1000));
                    }

                    /*
                     * Add the info to the list
                     */
                    list.add(info);
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
     * Take the array of experiment statistics information and convert to an XML string.
     *
     * @param statisticsInfoArray an array of experiment statistics information
     * @return an XML string containing the experiment statistics information. If an error occurred return null.
     */
    private String ToXmlString(ArrayList<ExperimentStatisticInfo> statisticsInfoList) {
        final String methodName = "ToXmlString";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String xmlStatistics = null;

        try {
            /*
             * Check that parameters are valid
             */
            if (statisticsInfoList == null) {
                throw new NullPointerException(STRERR_StatisticsInfoList);
            }

            /*
             * Load the experiment queue XML template string into a document
             */
            Document document = XmlUtilities.GetDocumentFromString(STRXMLDOC_ExperimentStatisticsTemplate);
            Node rootNode = XmlUtilities.GetRootNode(document, STRXML_Statistics);
            Node experimentNode = XmlUtilities.GetChildNode(rootNode, STRXML_Experiment);

            /*
             * Make a copy of the experiment node and remove it
             */
            Node experimentNodeCopy = experimentNode.cloneNode(true);
            rootNode.removeChild(experimentNode);

            /*
             * Take the information for each experiment and put into the XML document
             */
            Iterator iterator = statisticsInfoList.iterator();
            while (iterator.hasNext()) {
                ExperimentStatisticInfo info = (ExperimentStatisticInfo) iterator.next();

                /*
                 * Make a copy of the experiment node copy and fill it with values from the experiment information
                 */
                Node node = experimentNodeCopy.cloneNode(true);
                XmlUtilities.SetChildValue(node, STRXML_ExperimentId, info.getExperimentId());
                XmlUtilities.SetChildValue(node, STRXML_SbName, info.getSbName());
                XmlUtilities.SetChildValue(node, STRXML_UserGroup, info.getUserGroup());
                XmlUtilities.SetChildValue(node, STRXML_PriorityHint, info.getPriorityHint());
                XmlUtilities.SetChildValue(node, STRXML_EstimatedExecTime, info.getEstimatedExecTime());
                XmlUtilities.SetChildValue(node, STRXML_TimeSubmitted, info.getTimeSubmitted().toString());
                XmlUtilities.SetChildValue(node, STRXML_QueueLength, info.getQueueLength());
                XmlUtilities.SetChildValue(node, STRXML_EstimatedWaitTime, info.getEstimatedWaitTime());
                XmlUtilities.SetChildValue(node, STRXML_TimeStarted, info.getTimeStarted().toString());
                XmlUtilities.SetChildValue(node, STRXML_UnitId, info.getUnitId());
                XmlUtilities.SetChildValue(node, STRXML_TimeCompleted, info.getTimeCompleted().toString());
                XmlUtilities.SetChildValue(node, STRXML_ActualExecTime, info.getActualExecTime());
                XmlUtilities.SetChildValue(node, STRXML_Cancelled, info.isCancelled());

                /*
                 * Add the experiment node to the document
                 */
                rootNode.appendChild(node);
            }

            /*
             * Convert the document to an XML string
             */
            xmlStatistics = XmlUtilities.ToXmlString(document);
        } catch (NullPointerException | XmlUtilitiesException | DOMException ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return xmlStatistics;
    }
}
