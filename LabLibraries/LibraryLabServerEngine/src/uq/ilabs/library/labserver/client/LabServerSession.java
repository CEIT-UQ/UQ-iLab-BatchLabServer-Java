/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.client;

import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.labserver.database.types.LabServerInfo;
import uq.ilabs.library.labserver.engine.LabManagement;

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
    private LabManagement labManagement;
    private UserSession userSession;

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

    public LabManagement getLabManagement() {
        return labManagement;
    }

    public void setLabManagement(LabManagement labManagement) {
        this.labManagement = labManagement;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
}
