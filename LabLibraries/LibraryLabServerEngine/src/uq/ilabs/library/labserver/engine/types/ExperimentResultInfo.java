/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine.types;

import java.util.Date;
import uq.ilabs.library.lab.types.StatusCodes;

/**
 *
 * @author uqlpayne
 */
public class ExperimentResultInfo {

    private int id;
    private int experimentId;
    private String sbName;
    private String userGroup;
    private int priorityHint;
    private StatusCodes statusCode;
    private String xmlExperimentResults;
    private String xmlResultExtension;
    private String xmlBlobExtension;
    private String[] warningMessages;
    private String errorMessage;
    private boolean Notified;
    private Date DateCreated;

    public Date getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(Date DateCreated) {
        this.DateCreated = DateCreated;
    }

    public boolean isNotified() {
        return Notified;
    }

    public void setNotified(boolean Notified) {
        this.Notified = Notified;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriorityHint() {
        return priorityHint;
    }

    public void setPriorityHint(int priorityHint) {
        this.priorityHint = priorityHint;
    }

    public String getSbName() {
        return sbName;
    }

    public void setSbName(String sbName) {
        this.sbName = sbName;
    }

    public StatusCodes getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCodes statusCode) {
        this.statusCode = statusCode;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String[] getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(String[] warningMessages) {
        this.warningMessages = warningMessages;
    }

    public String getXmlBlobExtension() {
        return xmlBlobExtension;
    }

    public void setXmlBlobExtension(String xmlBlobExtension) {
        this.xmlBlobExtension = xmlBlobExtension;
    }

    public String getXmlExperimentResults() {
        return xmlExperimentResults;
    }

    public void setXmlExperimentResults(String xmlExperimentResults) {
        this.xmlExperimentResults = xmlExperimentResults;
    }

    public String getXmlResultExtension() {
        return xmlResultExtension;
    }

    public void setXmlResultExtension(String xmlResultExtension) {
        this.xmlResultExtension = xmlResultExtension;
    }
}
