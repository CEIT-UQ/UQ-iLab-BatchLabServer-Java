/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.client;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.html.HtmlOutputLabel;
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
    private static final String STR_UsernameIsRequired = "Username is required.";
    private static final String STR_PasswordIsRequired = "Password is required.";
    private static final String STR_AccountIsLocked = "Account is locked - Email ";
    private static final String STR_PasswordIsIncorrect = "Login failed - Password is incorrect.";
    private static final String STR_UsernameDoesNotExist = "Login failed - Username does not exist.";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private LabServerSession labServerSession;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String username;
    private String password;
    private HtmlOutputLabel holMessage;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.trim();
    }

    public HtmlOutputLabel getHolMessage() {
        return holMessage;
    }

    public void setHolMessage(HtmlOutputLabel holMessage) {
        this.holMessage = holMessage;
    }
    //</editor-fold>

    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {
        labServerSession = (LabServerSession) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Consts.STRSSN_LabServer);
    }

    public String actionLogin() {
        final String methodName = "actionLogin";
        Logfile.WriteCalled(STR_ClassName, methodName);

        String redirectUrl = null;

        try {
            /*
             * Check that a username has been entered
             */
            if (this.username.isEmpty() == true) {
                throw new Exception(STR_UsernameIsRequired);
            }

            /*
             * Check that a password has been entered
             */
            if (this.password.isEmpty() == true) {
                throw new Exception(STR_PasswordIsRequired);
            }

            /*
             * Check if username exists, -1 if not
             */
            UsersDB users = new UsersDB(labServerSession.getDbConnection());
            UserInfo userInfo = users.RetrieveByUsername(this.username);
            if (userInfo == null) {
                throw new Exception(STR_UsernameDoesNotExist);
            }

            /*
             * Check password
             */
            if (Password.ToHash(this.password).equals(userInfo.getPassword()) == false) {
                throw new Exception(STR_PasswordIsIncorrect);
            }

            /*
             * Create user session information and add to ServiceBroker session
             */
            UserSession userSession = new UserSession();
            userSession.setUsername(userInfo.getUsername());
            userSession.setManager(true);
            labServerSession.setUserSession(userSession);

//            redirectUrl = "Administration/ManageUsers.xhtml";
        } catch (Exception ex) {
            ShowMessageError(ex.getMessage());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);

        /*
         * Navigate to the specified page, if null then stay on same page
         */
        return redirectUrl;
    }

    private void ShowMessageError(String message) {
        holMessage.setStyleClass(Consts.STRSTL_ErrorMessage);
        holMessage.setValue(message);
        holMessage.setRendered(true);
    }
}
