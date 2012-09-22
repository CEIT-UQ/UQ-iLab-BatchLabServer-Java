/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labclient;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author uqlpayne
 */
@ManagedBean
@SessionScoped
public class HomeBean implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_Less = "Less";
    private static final String STR_More = "More";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String hcbSetupInfoValue;
    private String hcbStatusInfoValue;
    private String hcbResultsInfoValue;
    private boolean hpgSetupInfoRendered;
    private boolean hpgStatusInfoRendered;
    private boolean hpgResultsInfoRendered;

    public String getHcbResultsInfoValue() {
        return hcbResultsInfoValue;
    }

    public String getHcbSetupInfoValue() {
        return hcbSetupInfoValue;
    }

    public String getHcbStatusInfoValue() {
        return hcbStatusInfoValue;
    }

    public boolean isHpgResultsInfoRendered() {
        return hpgResultsInfoRendered;
    }

    public boolean isHpgSetupInfoRendered() {
        return hpgSetupInfoRendered;
    }

    public boolean isHpgStatusInfoRendered() {
        return hpgStatusInfoRendered;
    }
    //</editor-fold>

    /**
     * Creates a new instance of HomeBean
     */
    public HomeBean() {
    }

    /**
     *
     */
    public void pageLoad() {
        if (FacesContext.getCurrentInstance().isPostback() == false) {
            /*
             * Not a postback, initialise page controls
             */
            this.hpgSetupInfoRendered = false;
            this.hcbSetupInfoValue = STR_More;
            this.hpgStatusInfoRendered = false;
            this.hcbStatusInfoValue = STR_More;
            this.hpgResultsInfoRendered = false;
            this.hcbResultsInfoValue = STR_More;
        }
    }

    /**
     *
     * @return
     */
    public String actionSetupInfo() {
        this.hpgSetupInfoRendered = !this.hpgSetupInfoRendered;
        this.hcbSetupInfoValue = this.hpgSetupInfoRendered ? STR_Less : STR_More;

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @return
     */
    public String actionStatusInfo() {
        this.hpgStatusInfoRendered = !this.hpgStatusInfoRendered;
        this.hcbStatusInfoValue = this.hpgStatusInfoRendered ? STR_Less : STR_More;

        /* Navigate to the current page */
        return null;
    }

    /**
     *
     * @return
     */
    public String actionResultsInfo() {
        this.hpgResultsInfoRendered = !this.hpgResultsInfoRendered;
        this.hcbResultsInfoValue = this.hpgResultsInfoRendered ? STR_Less : STR_More;

        /* Navigate to the current page */
        return null;
    }
}
