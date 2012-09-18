/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.client;

import java.io.Serializable;
import java.util.logging.Level;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.client.Consts;
import uq.ilabs.library.labserver.client.LabServerSession;
import uq.ilabs.library.labserver.client.UserSession;

/**
 *
 * @author uqlpayne
 */
@ManagedBean
@SessionScoped
public class LabServerBean implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabServerBean.class.getName();
    private static final Level logLevel = Level.FINE;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabServerSession labServerSession;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">

    public String getTitle() {
        return labServerSession.getTitle();
    }

    public String getVersion() {
        return labServerSession.getVersion();
    }

    public String getCameraUrl() {
        return labServerSession.getCameraUrl();
    }

    public String getLabInfoUrl() {
        return labServerSession.getLabInfoUrl();
    }

    public String getPhotoUrl() {
        return labServerSession.getPhotoUrl();
    }

    public String getContactEmail() {
        return labServerSession.getContactEmail();
    }

    public String getUsername() {
        String username = null;

        UserSession userSession = labServerSession.getUserSession();
        if (userSession != null) {
            username = userSession.getUsername();
            if (username != null && username.length() > 0) {
                username = "User: " + username;
            }
        }

        return username;
    }

    public boolean isManager() {
        boolean manager = false;

        UserSession userSession = labServerSession.getUserSession();
        if (userSession != null) {
            manager = userSession.isManager();
        }

        return manager;
    }

    public boolean isLoggedIn() {
        return labServerSession.getUserSession() != null;
    }
    //</editor-fold>

    /**
     *
     */
    public LabServerBean() {
        final String methodName = "LabServerBean";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        this.labServerSession = (LabServerSession) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Consts.STRSSN_LabServer);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
