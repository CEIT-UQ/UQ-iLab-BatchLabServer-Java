/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.database.types;

import uq.ilabs.library.lab.types.StatusCodes;

/**
 *
 * @author uqlpayne
 */
public class ExperimentResultInfo {

    public static final int MAXLEN_SbName = 32;
    public static final int MAXLEN_UserGroup = 64;
    public static final int MAXLEN_StatusCode = 16;
    public static final int MAXLEN_XmlResultExtension = 2048;
    public static final int MAXLEN_XmlBlobExtension = 2048;
    public static final int MAXLEN_WarningMessages = 2048;
    public static final int MAXLEN_ErrorMessage = 2048;
    //
    private int id;
    private int experimentId;
    private String sbName;
    private String userGroup;
    private int priorityHint;
    private StatusCodes statusCode;
    private String xmlExperimentResult;
    private String xmlResultExtension;
    private String xmlBlobExtension;
    private String[] warningMessages;
    private String errorMessage;
    private boolean Notified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    public String getSbName() {
        return sbName;
    }

    public void setSbName(String sbName) {
        this.sbName = sbName;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public int getPriorityHint() {
        return priorityHint;
    }

    public void setPriorityHint(int priorityHint) {
        this.priorityHint = priorityHint;
    }

    public StatusCodes getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCodes statusCode) {
        this.statusCode = statusCode;
    }

    public String getXmlExperimentResult() {
        return xmlExperimentResult;
    }

    public void setXmlExperimentResult(String xmlExperimentResult) {
        this.xmlExperimentResult = xmlExperimentResult;
    }

    public String getXmlResultExtension() {
        return xmlResultExtension;
    }

    public void setXmlResultExtension(String xmlResultExtension) {
        this.xmlResultExtension = xmlResultExtension;
    }

    public String getXmlBlobExtension() {
        return xmlBlobExtension;
    }

    public void setXmlBlobExtension(String xmlBlobExtension) {
        this.xmlBlobExtension = xmlBlobExtension;
    }

    public String[] getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(String[] warningMessages) {
        this.warningMessages = warningMessages;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isNotified() {
        return Notified;
    }

    public void setNotified(boolean Notified) {
        this.Notified = Notified;
    }

    /**
     *
     */
    public ExperimentResultInfo() {
        this.id = -1;
        this.statusCode = StatusCodes.Unknown;
    }
}
