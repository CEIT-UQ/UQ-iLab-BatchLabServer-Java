/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.client;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.Password;
import uq.ilabs.library.labserver.client.Consts;
import uq.ilabs.library.labserver.client.LabServerSession;
import uq.ilabs.library.labserver.client.UserSession;
import uq.ilabs.library.labserver.database.UsersDB;
import uq.ilabs.library.labserver.engine.types.UserInfo;

/**
 *
 * @author uqlpayne
 */
@ManagedBean
@RequestScoped
public class LoginBean {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LoginBean.class.getName();
    /*
     * String constants
     */
    private static final String STRERR_NotSpecified_arg = "%s - Not specified!";
    private static final String STRERR_Username = "Username";
    private static final String STRERR_Password = "Password";
    private static final String STRERR_UnknownUsername = "Login failed - Unknown username!";
    private static final String STRERR_IncorrectPassword = "Login failed - Incorrect password!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabServerSession labServerSession;
    private UsersDB usersDB;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String hitUsername;
    private String hisPassword;
    private String holMessage;
    private String holMessageClass;

    public String getHitUsername() {
        return hitUsername;
    }

    public void setHitUsername(String hitUsername) {
        this.hitUsername = hitUsername;
    }

    public String getHisPassword() {
        return hisPassword;
    }

    public void setHisPassword(String hisPassword) {
        this.hisPassword = hisPassword;
    }

    public String getHolMessage() {
        return holMessage;
    }

    public String getHolMessageClass() {
        return holMessageClass;
    }
    //</editor-fold>

    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {
        this.labServerSession = (LabServerSession) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Consts.STRSSN_LabServer);
        try {
            this.usersDB = new UsersDB(this.labServerSession.getDbConnection());
        } catch (Exception ex) {
        }
    }

    /**
     *
     * @return
     */
    public String actionLogin() {
        final String methodName = "actionLogin";
        Logfile.WriteCalled(STR_ClassName, methodName);

        try {
            /*
             * Check that a username has been entered
             */
            this.hitUsername = this.hitUsername.toLowerCase().trim();
            if (this.hitUsername.isEmpty() == true) {
                throw new IllegalArgumentException(String.format(STRERR_NotSpecified_arg, STRERR_Username));
            }

            /*
             * Check that a password has been entered
             */
            this.hisPassword = this.hisPassword.trim();
            if (this.hisPassword.isEmpty() == true) {
                throw new IllegalArgumentException(String.format(STRERR_NotSpecified_arg, STRERR_Password));
            }

            /*
             * Check if username exists
             */
            UserInfo userInfo = this.usersDB.RetrieveByUsername(this.hitUsername);
            if (userInfo == null) {
                throw new RuntimeException(STRERR_UnknownUsername);
            }

            /*
             * Check password
             */
            if (Password.ToHash(this.hisPassword).equals(userInfo.getPassword()) == false) {
                throw new RuntimeException(STRERR_IncorrectPassword);
            }

            /*
             * Create user session information and add to ServiceBroker session
             */
            UserSession userSession = new UserSession();
            userSession.setUsername(userInfo.getUsername());
            userSession.setGroupname(userInfo.getUserGroup());
            labServerSession.setUserSession(userSession);

        } catch (Exception ex) {
            ShowMessageError(ex.getMessage());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @param message
     */
    private void ShowMessageError(String message) {
        this.holMessage = message;
        this.holMessageClass = Consts.STRSTL_ErrorMessage;
    }
}
