/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labclient;

import java.io.Serializable;
import java.util.logging.Level;
import javax.faces.application.ViewExpiredException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labclient.engine.LabClientSession;
import uq.ilabs.library.labclient.engine.LabConsts;

/**
 *
 * @author uqlpayne
 */
@ManagedBean
@SessionScoped
public class LabClientBean implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabClientBean.class.getName();
    private static final Level logLevel = Level.FINE;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabClientSession labClientSession;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">

    public String getCheckViewExpired() {
        if (labClientSession == null) {
            throw new ViewExpiredException();
        }
        return "";
    }

    public String getTitle() {
        return labClientSession.getTitle();
    }

    public String getVersion() {
        return labClientSession.getVersion();
    }

    public String getLabCameraUrl() {
        return labClientSession.getLabCameraUrl();
    }

    public String getFeedbackEmailUrl() {
        return labClientSession.getFeedbackEmailUrl();
    }
    //</editor-fold>

    /**
     *
     */
    public LabClientBean() {
        final String methodName = "LabClientBean";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        this.labClientSession = (LabClientSession) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(LabConsts.STRSSN_LabClient);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
