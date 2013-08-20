package uq.ilabs.library.labserver.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labserver.database.types.ExperimentStatisticsInfo;
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
    private static final String STRLOG_Count_arg = "Count: %d";
    private static final String STRLOG_Success_arg = "Success: %s";
    private static final String STRLOG_ExperimentIdSbName_arg2 = "ExperimentId: %d  SbName: '%s'";
    private static final String STRLOG_ExperimentIdSbNameUnitId_arg3 = "ExperimentId: %d  SbName: '%s'  UnitId: %d";
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
    private static final String STRSQLCMD_UpdateStarted = "{ ? = call Statistics_UpdateStarted(?,?,?) }";
    private static final String STRSQLCMD_UpdateCompleted = "{ ? = call Statistics_UpdateCompleted(?,?) }";
    private static final String STRSQLCMD_UpdateCancelled = "{ ? = call Statistics_UpdateCancelled(?,?) }";
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
    private DBConnection dbConnection;
    //</editor-fold>

    /**
     *
     * @param dbConnection
     * @throws Exception
     */
    public ExperimentStatisticsDB(DBConnection dbConnection) throws Exception {
        final String methodName = "ExperimentStatisticsDB";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Check that parameters are valid
         */
        if (dbConnection == null) {
            throw new NullPointerException(DBConnection.class.getSimpleName());
        }

        /*
         * Initialise locals
         */
        this.dbConnection = dbConnection;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param queuedExperimentInfo
     * @return boolean
     */
    public boolean Submitted(QueuedExperimentInfo queuedExperimentInfo) {
        final String methodName = "Submitted";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ExperimentStatisticsInfo experimentStatisticsInfo = new ExperimentStatisticsInfo();
        experimentStatisticsInfo.setExperimentId(queuedExperimentInfo.getExperimentId());
        experimentStatisticsInfo.setSbName(queuedExperimentInfo.getSbName());
        experimentStatisticsInfo.setUserGroup(queuedExperimentInfo.getUserGroup());
        experimentStatisticsInfo.setPriorityHint(queuedExperimentInfo.getPriorityHint());
        experimentStatisticsInfo.setEstimatedExecTime(queuedExperimentInfo.getEstimatedExecTime());
        experimentStatisticsInfo.setQueueLength(queuedExperimentInfo.getPosition() - 1);
        experimentStatisticsInfo.setEstimatedWaitTime(queuedExperimentInfo.getWaitTime());

        int id = this.Add(experimentStatisticsInfo);

        return id > 0;
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
            Connection sqlConnection = this.dbConnection.getConnection();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_Delete);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, id);

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
                this.dbConnection.putConnection(sqlConnection);
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
     * @return ExperimentStatisticsInfo
     */
    public ExperimentStatisticsInfo RetrieveById(int id) {
        ArrayList<ExperimentStatisticsInfo> list = this.RetrieveBy(STRCOL_Id, id, null);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @param experimentId
     * @param sbName
     * @return ExperimentStatisticsInfo
     */
    public ExperimentStatisticsInfo RetrieveByExperimentId(int experimentId, String sbName) {
        ArrayList<ExperimentStatisticsInfo> list = this.RetrieveBy(STRCOL_ExperimentId, experimentId, sbName);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @param userGroup
     * @return ExperimentStatisticsInfo
     */
    public ExperimentStatisticsInfo RetrieveByUserGroup(String userGroup) {
        ArrayList<ExperimentStatisticsInfo> list = this.RetrieveBy(STRCOL_UserGroup, 0, userGroup);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @return ArrayList of ExperimentStatisticsInfo
     */
    public ArrayList<ExperimentStatisticsInfo> RetrieveAll() {
        return this.RetrieveBy(null, 0, null);
    }

    /**
     *
     * @return String
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

    /**
     *
     * @param experimentId
     * @param sbName
     * @param unitId
     * @return boolean
     */
    public boolean UpdateStarted(int experimentId, String sbName, int unitId) {
        final String methodName = "UpdateStarted";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentIdSbNameUnitId_arg3, experimentId, sbName, unitId));

        boolean success = false;

        try {
            Connection sqlConnection = this.dbConnection.getConnection();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_UpdateStarted);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, experimentId);
                sqlStatement.setString(3, sbName);
                sqlStatement.setInt(4, unitId);

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();
                success = (sqlStatement.getInt(1) != 0);
            } finally {
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
                this.dbConnection.putConnection(sqlConnection);
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
     * @return boolean
     */
    public boolean UpdateCompleted(int experimentId, String sbName) {
        final String methodName = "UpdateCompleted";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentIdSbName_arg2, experimentId, sbName));

        boolean success = false;

        try {
            Connection sqlConnection = this.dbConnection.getConnection();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_UpdateCompleted);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, experimentId);
                sqlStatement.setString(3, sbName);

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();
                success = (sqlStatement.getInt(1) != 0);
            } finally {
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
                this.dbConnection.putConnection(sqlConnection);
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
     * @return boolean
     */
    public boolean UpdateCancelled(int experimentId, String sbName) {
        final String methodName = "UpdateCancelled";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentIdSbName_arg2, experimentId, sbName));

        boolean success = false;

        try {
            Connection sqlConnection = this.dbConnection.getConnection();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_UpdateCancelled);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, experimentId);
                sqlStatement.setString(3, sbName);

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();
                success = (sqlStatement.getInt(1) != 0);
            } finally {
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
                this.dbConnection.putConnection(sqlConnection);
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
     * @param experimentStatisticsInfo
     * @return int
     */
    public int Add(ExperimentStatisticsInfo experimentStatisticsInfo) {
        final String methodName = "Add";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int id = -1;

        try {
            /*
             * Check that parameters are valid
             */
            if (experimentStatisticsInfo == null) {
                throw new NullPointerException(ExperimentStatisticsInfo.class.getSimpleName());
            }

            Connection sqlConnection = this.dbConnection.getConnection();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_Add);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, experimentStatisticsInfo.getExperimentId());
                sqlStatement.setString(3, experimentStatisticsInfo.getSbName());
                sqlStatement.setString(4, experimentStatisticsInfo.getUserGroup());
                sqlStatement.setInt(5, experimentStatisticsInfo.getPriorityHint());
                sqlStatement.setInt(6, experimentStatisticsInfo.getEstimatedExecTime());
                sqlStatement.setInt(7, experimentStatisticsInfo.getQueueLength());
                sqlStatement.setInt(8, experimentStatisticsInfo.getEstimatedWaitTime());

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
                this.dbConnection.putConnection(sqlConnection);
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
     * @param columnName
     * @param intval
     * @param strval
     * @return ArrayList of ExperimentStatisticsInfo
     */
    private ArrayList<ExperimentStatisticsInfo> RetrieveBy(String columnName, int intval, String strval) {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ArrayList<ExperimentStatisticsInfo> arrayList = new ArrayList<>();

        try {
            Connection sqlConnection = this.dbConnection.getConnection();
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = sqlConnection.prepareCall(STRSQLCMD_RetrieveBy);
                sqlStatement.setString(1, columnName);
                sqlStatement.setInt(2, intval);
                sqlStatement.setString(3, strval);

                /*
                 * Execute the stored procedure
                 */
                ResultSet resultSet = sqlStatement.executeQuery();
                while (resultSet.next() == true) {
                    ExperimentStatisticsInfo experimentStatisticsInfo = new ExperimentStatisticsInfo();

                    experimentStatisticsInfo.setId(resultSet.getInt(STRCOL_Id));
                    experimentStatisticsInfo.setExperimentId(resultSet.getInt(STRCOL_ExperimentId));
                    experimentStatisticsInfo.setSbName(resultSet.getString(STRCOL_SbName));
                    experimentStatisticsInfo.setUserGroup(resultSet.getString(STRCOL_UserGroup));
                    experimentStatisticsInfo.setPriorityHint(resultSet.getInt(STRCOL_PriorityHint));
                    experimentStatisticsInfo.setEstimatedExecTime(resultSet.getInt(STRCOL_EstimatedExecTime));
                    experimentStatisticsInfo.setQueueLength(resultSet.getInt(STRCOL_QueueLength));
                    experimentStatisticsInfo.setEstimatedWaitTime(resultSet.getInt(STRCOL_EstimatedWaitTime));
                    experimentStatisticsInfo.setUnitId(resultSet.getInt(STRCOL_UnitId));
                    experimentStatisticsInfo.setCancelled(resultSet.getBoolean(STRCOL_Cancelled));

                    Calendar calendar;
                    Timestamp timestamp;
                    if ((timestamp = resultSet.getTimestamp(STRCOL_TimeSubmitted)) != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTime(timestamp);
                        experimentStatisticsInfo.setTimeSubmitted(calendar);
                    }
                    if ((timestamp = resultSet.getTimestamp(STRCOL_TimeStarted)) != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTime(timestamp);
                        experimentStatisticsInfo.setTimeStarted(calendar);
                    }
                    if ((timestamp = resultSet.getTimestamp(STRCOL_TimeCompleted)) != null) {
                        calendar = Calendar.getInstance();
                        calendar.setTime(timestamp);
                        experimentStatisticsInfo.setTimeCompleted(calendar);
                    }

                    /*
                     * Calculate the actual execution time
                     */
                    if (experimentStatisticsInfo.getTimeStarted() != null) {
                        long startTime = experimentStatisticsInfo.getTimeStarted().getTimeInMillis();
                        if (experimentStatisticsInfo.getTimeCompleted() != null) {
                            long endTime = experimentStatisticsInfo.getTimeCompleted().getTimeInMillis();
                            experimentStatisticsInfo.setActualExecTime((int) ((endTime - startTime) / 1000));
                        }
                    }

                    /*
                     * Add the ExperimentStatisticsInfo to the list
                     */
                    arrayList.add(experimentStatisticsInfo);
                }
            } finally {
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
                this.dbConnection.putConnection(sqlConnection);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Count_arg, arrayList.size()));

        return (arrayList.size() > 0) ? arrayList : null;
    }

    /**
     * Take the array of experiment statistics information and convert to an XML string.
     *
     * @param statisticsInfoList an ArrayList of experiment statistics information
     * @return an XML string containing the experiment statistics information. If an error occurred return null.
     */
    private String ToXmlString(ArrayList<ExperimentStatisticsInfo> statisticsInfoList) {
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
            for (ExperimentStatisticsInfo info : statisticsInfoList) {
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
