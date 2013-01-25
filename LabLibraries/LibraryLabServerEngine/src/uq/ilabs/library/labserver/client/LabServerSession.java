/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.client;

import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.labserver.database.types.LabServerInfo;

/**
 *
 * @author uqlpayne
 */
public class LabServerSession {

    private String title;
    private String version;
    private String navmenuPhotoUrl;
    private String labCameraUrl;
    private String labInfoUrl;
    private LabServerInfo labServerInfo;
    private UserSession userSession;
    private DBConnection dbConnection;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNavmenuPhotoUrl() {
        return navmenuPhotoUrl;
    }

    public void setNavmenuPhotoUrl(String navmenuPhotoUrl) {
        this.navmenuPhotoUrl = navmenuPhotoUrl;
    }

    public String getLabCameraUrl() {
        return labCameraUrl;
    }

    public void setLabCameraUrl(String labCameraUrl) {
        this.labCameraUrl = labCameraUrl;
    }

    public String getLabInfoUrl() {
        return labInfoUrl;
    }

    public void setLabInfoUrl(String labInfoUrl) {
        this.labInfoUrl = labInfoUrl;
    }

    public LabServerInfo getLabServerInfo() {
        return labServerInfo;
    }

    public void setLabServerInfo(LabServerInfo labServerInfo) {
        this.labServerInfo = labServerInfo;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public DBConnection getDbConnection() {
        return dbConnection;
    }

    public void setDbConnection(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
}
