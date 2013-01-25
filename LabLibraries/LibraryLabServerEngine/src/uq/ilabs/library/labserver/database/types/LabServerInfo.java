/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.database.types;

import java.util.Calendar;
import uq.ilabs.library.labserver.engine.LabConsts;

/**
 *
 * @author uqlpayne
 */
public class LabServerInfo {

    private int id;
    private String name;
    private String guid;
    private String serviceUrl;
    private String contactEmail;
    private String completedEmail;
    private String failedEmail;
    private boolean authenticate;
    private Calendar dateCreated;
    private Calendar dateModified;
    private String[] completedEmailList;
    private String[] failedEmailList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getCompletedEmail() {
        return completedEmail;
    }

    public void setCompletedEmail(String completedEmail) {
        this.completedEmail = completedEmail;
        this.completedEmailList = this.CsvToStrings(completedEmail);
    }

    public String getFailedEmail() {
        return failedEmail;
    }

    public void setFailedEmail(String failedEmail) {
        this.failedEmail = failedEmail;
        this.failedEmailList = this.CsvToStrings(failedEmail);
    }

    public boolean isAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(boolean authenticate) {
        this.authenticate = authenticate;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Calendar getDateModified() {
        return dateModified;
    }

    public void setDateModified(Calendar dateModified) {
        this.dateModified = dateModified;
    }

    public String[] getCompletedEmailList() {
        return completedEmailList;
    }

    public String[] getFailedEmailList() {
        return failedEmailList;
    }

    /**
     *
     */
    public LabServerInfo() {
        this.id = -1;
        this.authenticate = true;
    }

    /**
     *
     * @return
     */
    private String[] CsvToStrings(String csvString) {
        String[] strings = null;

        if (csvString != null) {
            strings = csvString.split(LabConsts.STR_EmailSplitter);
            for (int i = 0; i < strings.length; i++) {
                strings[i] = strings[i].trim();
            }
        }

        return strings;
    }
}
