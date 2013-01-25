/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.StatusCodes;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labserver.engine.types.ExperimentResultInfo;

/**
 *
 * @author uqlpayne
 */
public class ExperimentResultsDB {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentResultsDB.class.getName();
    private static final Level logLevel = Level.FINEST;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Id_arg = "Id: %d";
    private static final String STRLOG_ExperimentIdSbName_arg = "ExperimentId: %d  SbName: '%s'";
    private static final String STRLOG_Count_arg = "Count: %d";
    private static final String STRLOG_StatusCode_arg = "StatusCode: %s";
    private static final String STRLOG_Success_arg = "Success: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ExperimentResultInfo = "experimentResultInfo";
    private static final String STRERR_ResultsInfoList = "resultsInfoList";
    /*
     * Database column names
     */
    private static final String STRCOL_Id = "Id";
    private static final String STRCOL_ExperimentId = "ExperimentId";
    private static final String STRCOL_SbName = "SbName";
    private static final String STRCOL_UserGroup = "UserGroup";
    private static final String STRCOL_PriorityHint = "PriorityHint";
    private static final String STRCOL_StatusCode = "StatusCode";
    private static final String STRCOL_XmlExperimentResults = "XmlExperimentResult";
    private static final String STRCOL_XmlResultExtension = "XmlResultExtension";
    private static final String STRCOL_XmlBlobExtension = "XmlBlobExtension";
    private static final String STRCOL_WarningMessages = "WarningMessages";
    private static final String STRCOL_ErrorMessage = "ErrorMessage";
    private static final String STRCOL_Notified = "Notified";
    private static final String STRCOL_DateCreated = "DateCreated";
    /*
     * String constants for SQL processing
     */
    private static final String STRSQLCMD_Add = "{ ? = call Results_Add(?,?,?,?,?,?,?,?,?,?) }";
    private static final String STRSQLCMD_Delete = "{ ? = call Results_Delete(?) }";
    private static final String STRSQLCMD_RetrieveBy = "{ call Results_RetrieveBy(?,?,?) }";
    private static final String STRSQLCMD_UpdateNotified = "{ call Results_UpdateNotified(?,?) }";
    /*
     * String constants for XML elements
     */
    private static final String STRXML_ExperimentResults = "experimentResults";
    private static final String STRXML_ExperimentResult = "experimentResult";
    private static final String STRXML_ExperimentId = "experimentId";
    private static final String STRXML_SbName = "sbName";
    private static final String STRXML_UserGroup = "userGroup";
    private static final String STRXML_PriorityHint = "priorityHint";
    private static final String STRXML_StatusCode = "statusCode";
    private static final String STRXML_XmlExperimentResult = "xmlExperimentResult";
    private static final String STRXML_XmlResultExtension = "xmlResultExtension";
    private static final String STRXML_XmlBlobExtension = "xmlBlobExtension";
    private static final String STRXML_WarningMessages = "warningMessages";
    private static final String STRXML_WarningMessage = "warningMessage";
    private static final String STRXML_ErrorMessage = "errorMessage";
    /*
     * XML experiment results template
     */
    private static final String STRXMLDOC_ExperimentResultsTemplate =
            "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
            + "<" + STRXML_ExperimentResults + ">"
            + "<" + STRXML_ExperimentResult + ">"
            + "<" + STRXML_ExperimentId + " />"
            + "<" + STRXML_SbName + " />"
            + "<" + STRXML_UserGroup + " />"
            + "<" + STRXML_PriorityHint + " />"
            + "<" + STRXML_StatusCode + " />"
            + "<" + STRXML_XmlExperimentResult + " />"
            + "<" + STRXML_XmlResultExtension + " />"
            + "<" + STRXML_XmlBlobExtension + " />"
            + "<" + STRXML_WarningMessages + ">"
            + "<" + STRXML_WarningMessage + " />"
            + "</" + STRXML_WarningMessages + ">"
            + "<" + STRXML_ErrorMessage + " />"
            + "</" + STRXML_ExperimentResult + ">"
            + "</" + STRXML_ExperimentResults + ">";
    /*
     * XML warning messages template
     */
    private static final String STRXMLDOC_WarningMessagesTemplate =
            "<" + STRXML_WarningMessages + ">"
            + "<" + STRXML_WarningMessage + " />"
            + "</" + STRXML_WarningMessages + ">";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Connection sqlConnection;
    //</editor-fold>

    /**
     *
     * @param dbConnection
     * @throws Exception
     */
    public ExperimentResultsDB(DBConnection dbConnection) throws Exception {
        final String methodName = "ExperimentResults";
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
     * @param experimentResultInfo
     * @return
     */
    public int Add(ExperimentResultInfo experimentResultInfo) {
        final String methodName = "Add";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        int id = -1;

        try {
            /*
             * Check that parameters are valid
             */
            if (experimentResultInfo == null) {
                throw new NullPointerException(STRERR_ExperimentResultInfo);
            }

            Logfile.Write(String.format(STRLOG_ExperimentIdSbName_arg, experimentResultInfo.getExperimentId(), experimentResultInfo.getSbName()));

            CallableStatement sqlStatement = null;

            try {
                /*
                 * Convert warning messages to XML format string
                 */
                String xmlWarningMessages = null;
                if (experimentResultInfo.getWarningMessages() != null) {
                    /*
                     * Load the warning messages XML template string into a document
                     */
                    Document warningMessagesDocument = XmlUtilities.GetDocumentFromString(STRXMLDOC_WarningMessagesTemplate);
                    Node warningMessagesRootNode = XmlUtilities.GetRootNode(warningMessagesDocument, STRXML_WarningMessages);

                    /*
                     * Remove the empty warning message node and add the warning messages
                     */
                    Node warningMessageNode = XmlUtilities.GetChildNode(warningMessagesRootNode, STRXML_WarningMessage);
                    warningMessagesRootNode.removeChild(warningMessageNode);
                    XmlUtilities.SetChildValues(warningMessagesRootNode, STRXML_WarningMessage, experimentResultInfo.getWarningMessages());

                    /*
                     * Convert the document to a string
                     */
                    warningMessagesDocument.normalizeDocument();
                    xmlWarningMessages = XmlUtilities.ToXmlString(warningMessagesDocument);
                }

                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_Add);
                sqlStatement.registerOutParameter(1, Types.INTEGER);
                sqlStatement.setInt(2, experimentResultInfo.getExperimentId());
                sqlStatement.setString(3, experimentResultInfo.getSbName());
                sqlStatement.setString(4, experimentResultInfo.getUserGroup());
                sqlStatement.setInt(5, experimentResultInfo.getPriorityHint());
                sqlStatement.setString(6, experimentResultInfo.getStatusCode().toString());
                sqlStatement.setString(7, experimentResultInfo.getXmlExperimentResults());
                sqlStatement.setString(8, experimentResultInfo.getXmlResultExtension());
                sqlStatement.setString(9, experimentResultInfo.getXmlBlobExtension());
                sqlStatement.setString(10, xmlWarningMessages);
                sqlStatement.setString(11, experimentResultInfo.getErrorMessage());

                /*
                 * Execute the stored procedure
                 */
                sqlStatement.execute();

                /*
                 * Get the result
                 */
                id = (int) sqlStatement.getInt(1);
            } catch (XmlUtilitiesException | DOMException | SQLException ex) {
                throw ex;
            } finally {
                try {
                    sqlStatement.close();
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
            }
        } catch (NullPointerException | XmlUtilitiesException | DOMException | SQLException ex) {
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
     * @param id
     * @return ExperimentResultInfo
     */
    public ExperimentResultInfo RetrieveById(int id) {
        ArrayList<ExperimentResultInfo> list = this.RetrieveBy(STRCOL_Id, id, null);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @param experimentId
     * @param sbName
     * @return ExperimentResultInfo
     */
    public ExperimentResultInfo RetrieveByExperimentId(int experimentId, String sbName) {
        ArrayList<ExperimentResultInfo> list = this.RetrieveBy(STRCOL_ExperimentId, experimentId, sbName);
        return (list != null) ? list.get(0) : null;
    }

    /**
     *
     * @return ArrayList of ExperimentResultInfo
     */
    public ArrayList<ExperimentResultInfo> RetrieveAllNotNotified() {
        return this.RetrieveBy(STRCOL_Notified, 0, null);
    }

    /**
     *
     * @return ArrayList of ExperimentResultInfo
     */
    public ArrayList<ExperimentResultInfo> RetrieveAll() {
        return this.RetrieveBy(null, 0, null);
    }

    /**
     * Retrieve the result report for the specified experiment Id and ServiceBroker name.
     *
     * @param experimentId The experiment number.
     * @param sbName The name of the ServiceBroker.
     * @return The result report for the specified experiment Id and ServiceBroker name.
     */
    public ResultReport RetrieveResultReport(int experimentId, String sbName) {
        final String methodName = "RetrieveResultReport";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentIdSbName_arg, experimentId, sbName));

        ResultReport resultReport = new ResultReport();

        ExperimentResultInfo info = this.RetrieveByExperimentId(experimentId, sbName);
        if (info != null) {
            resultReport.setStatusCode(info.getStatusCode());
            resultReport.setXmlExperimentResults(info.getXmlExperimentResults());
            resultReport.setXmlResultExtension(info.getXmlResultExtension());
            resultReport.setXmlBlobExtension(info.getXmlBlobExtension());
            resultReport.setWarningMessages(info.getWarningMessages());
            resultReport.setErrorMessage(info.getErrorMessage());
        } else {
            resultReport.setStatusCode(StatusCodes.Unknown);
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_StatusCode_arg, resultReport.getStatusCode()));

        return resultReport;
    }

    /**
     * Update the results for the specified experiment Id and ServiceBroker name to indicate theat the ServiceBroker has
     * been notified of experiment completion.
     *
     * @param experimentId The experiment number.
     * @param sbName The name of the ServiceBroker.
     * @return True if the database record has been successfully updated.
     * @throws Exception
     */
    public boolean UpdateNotified(int experimentId, String sbName) throws Exception {
        final String methodName = "UpdateNotified";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ExperimentIdSbName_arg, experimentId, sbName));

        boolean success = false;

        try {
            CallableStatement sqlStatement = null;

            try {
                /*
                 * Prepare the stored procedure call
                 */
                sqlStatement = this.sqlConnection.prepareCall(STRSQLCMD_UpdateNotified);
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
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @return @throws Exception
     */
    public String RetrieveAllToXmlString() throws Exception {
        final String methodName = "RetrieveAllToXmlString";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String xmlString = null;

        try {
            /*
             * Get all of the results information and convert to XML
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
     * @return ArrayList of ExperimentResultInfo
     * @throws Exception
     */
    private ArrayList<ExperimentResultInfo> RetrieveBy(String columnName, int intval, String strval) {
        final String methodName = "RetrieveBy";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        ArrayList<ExperimentResultInfo> list = new ArrayList<>();

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
                    /*
                     * Get the experiment results information
                     */
                    ExperimentResultInfo info = new ExperimentResultInfo();
                    info.setId(resultSet.getInt(STRCOL_Id));
                    info.setExperimentId(resultSet.getInt(STRCOL_ExperimentId));
                    info.setSbName(resultSet.getString(STRCOL_SbName));
                    info.setUserGroup(resultSet.getString(STRCOL_UserGroup));
                    info.setPriorityHint(resultSet.getInt(STRCOL_PriorityHint));
                    info.setStatusCode(StatusCodes.valueOf(resultSet.getString(STRCOL_StatusCode)));
                    info.setXmlExperimentResults(resultSet.getString(STRCOL_XmlExperimentResults));
                    info.setXmlResultExtension(resultSet.getString(STRCOL_XmlResultExtension));
                    info.setXmlBlobExtension(resultSet.getString(STRCOL_XmlBlobExtension));
                    info.setErrorMessage(resultSet.getString(STRCOL_ErrorMessage));
                    info.setNotified(resultSet.getBoolean(STRCOL_Notified));
                    info.setDateCreated(resultSet.getTimestamp(STRCOL_DateCreated));

                    /*
                     * Convert warning messages from XML format to string array
                     */
                    String xmlWarningMessages = resultSet.getString(STRCOL_WarningMessages);
                    if (xmlWarningMessages != null) {
                        try {
                            Document document = XmlUtilities.GetDocumentFromString(xmlWarningMessages);
                            Node rootNode = XmlUtilities.GetRootNode(document, STRXML_WarningMessages);
                            info.setWarningMessages(XmlUtilities.GetChildValues(rootNode, STRXML_WarningMessage, false));
                        } catch (Exception ex) {
                        }
                    }

                    /*
                     * Add the info to the list
                     */
                    list.add(info);
                }
            } catch (SQLException ex) {
                throw ex;
            } finally {
                try {
                    sqlStatement.close();
                } catch (SQLException ex) {
                    Logfile.WriteException(STR_ClassName, methodName, ex);
                }
            }
        } catch (SQLException ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Count_arg, list.size()));

        return (list.size() > 0) ? list : null;
    }

    /**
     *
     * @param experimentResultInfoList
     * @return
     */
    private String ToXmlString(ArrayList<ExperimentResultInfo> experimentResultInfoList) {
        final String methodName = "ToXmlString";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        String xmlExperimentResults = null;

        try {
            /*
             * Check that the statistics info array exists
             */
            if (experimentResultInfoList == null) {
                throw new NullPointerException(STRERR_ResultsInfoList);
            }

            /*
             * Load the experiment queue XML template string into a document
             */
            Document document = XmlUtilities.GetDocumentFromString(STRXMLDOC_ExperimentResultsTemplate);
            Node rootNode = XmlUtilities.GetRootNode(document, STRXML_ExperimentResults);
            Node experimentResultNode = XmlUtilities.GetChildNode(rootNode, STRXML_ExperimentResult);

            /*
             * Make a copy of the experiment node and remove it
             */
            Node experimentResultNodeCopy = experimentResultNode.cloneNode(true);
            rootNode.removeChild(experimentResultNode);

            /*
             * Take the information for each experiment and put into the XML document
             */
            Iterator iterator = experimentResultInfoList.iterator();
            while (iterator.hasNext()) {
                ExperimentResultInfo info = (ExperimentResultInfo) iterator.next();
                /*
                 * Make a copy of the experiment result node copy and fill it with values from the experiment
                 * information
                 */
                Node node = experimentResultNodeCopy.cloneNode(true);
                XmlUtilities.SetChildValue(node, STRXML_ExperimentId, info.getExperimentId());
                XmlUtilities.SetChildValue(node, STRXML_SbName, info.getSbName());
                XmlUtilities.SetChildValue(node, STRXML_UserGroup, info.getUserGroup());
                XmlUtilities.SetChildValue(node, STRXML_PriorityHint, info.getPriorityHint());
                XmlUtilities.SetChildValue(node, STRXML_StatusCode, info.getStatusCode().toString());
                XmlUtilities.SetChildValue(node, STRXML_XmlExperimentResult, info.getXmlExperimentResults());
                XmlUtilities.SetChildValue(node, STRXML_XmlResultExtension, info.getXmlResultExtension());
                XmlUtilities.SetChildValue(node, STRXML_XmlBlobExtension, info.getXmlBlobExtension());
                XmlUtilities.SetChildValue(node, STRXML_ErrorMessage, info.getErrorMessage());

                /*
                 * Remove the empty warning message node
                 */
                Node warningMessagesNode = XmlUtilities.GetChildNode(node, STRXML_WarningMessages);
                Node warningMessageNode = XmlUtilities.GetChildNode(warningMessagesNode, STRXML_WarningMessage);
                warningMessagesNode.removeChild(warningMessageNode);

                /*
                 * Add the warning messages
                 */
                XmlUtilities.SetChildValues(warningMessagesNode, STRXML_WarningMessage, info.getWarningMessages());

                /*
                 * Add the experiment node to the document
                 */
                rootNode.appendChild(node);
            }

            /*
             * Convert the document to an XML string
             */
            xmlExperimentResults = XmlUtilities.ToXmlString(document);
        } catch (NullPointerException | XmlUtilitiesException | DOMException ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return xmlExperimentResults;
    }
}
