/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker;

import java.util.logging.Level;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.ServiceTypes;
import uq.ilabs.library.lab.types.SubmissionReport;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.LabServerRestAPI;
import uq.ilabs.library.labserver.LabServerSoapAPI;

/**
 *
 * @author uqlpayne
 */
public class LabServerAPI {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabServerAPI.class.getName();
    private static final Level logLevel = Level.INFO;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_ServiceTypeUrl_arg2 = "ServiceType: %s  ServiceUrl: %s";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Object labServerAPI;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String identifier;
    private String passkey;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        if (this.labServerAPI instanceof LabServerRestAPI) {
            ((LabServerRestAPI) this.labServerAPI).setIdentifier(identifier);
        } else if (this.labServerAPI instanceof LabServerSoapAPI) {
            ((LabServerSoapAPI) this.labServerAPI).setIdentifier(identifier);
        }
    }

    public String getPasskey() {
        return passkey;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
        if (this.labServerAPI instanceof LabServerRestAPI) {
            ((LabServerRestAPI) this.labServerAPI).setPasskey(passkey);
        } else if (this.labServerAPI instanceof LabServerSoapAPI) {
            ((LabServerSoapAPI) this.labServerAPI).setPasskey(passkey);
        }
    }
    //</editor-fold>

    /**
     *
     * @param serviceUrl
     * @param restful
     * @throws Exception
     */
    public LabServerAPI(ServiceTypes serviceType, String serviceUrl) throws Exception {
        final String methodName = "LabServerAPI";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ServiceTypeUrl_arg2, serviceType.toString(), serviceUrl));

        switch (serviceType) {
            case Rest:
                this.labServerAPI = new LabServerRestAPI(serviceUrl);
                break;
            case Soap:
                this.labServerAPI = new LabServerSoapAPI(serviceUrl);
                break;
            default:
                throw new IllegalArgumentException(ServiceTypes.class.getSimpleName());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param experimentId
     * @return boolean
     */
    public boolean Cancel(int experimentId) {
        if (this.labServerAPI instanceof LabServerRestAPI) {
            return ((LabServerRestAPI) this.labServerAPI).Cancel(experimentId);
        }
        if (this.labServerAPI instanceof LabServerSoapAPI) {
            return ((LabServerSoapAPI) this.labServerAPI).Cancel(experimentId);
        }
        return false;
    }

    /**
     *
     * @param priorityHint
     * @return WaitEstimate
     */
    public WaitEstimate GetEffectiveQueueLength(String userGroup, int priorityHint) {
        if (this.labServerAPI instanceof LabServerRestAPI) {
            return ((LabServerRestAPI) this.labServerAPI).GetEffectiveQueueLength(userGroup, priorityHint);
        }
        if (this.labServerAPI instanceof LabServerSoapAPI) {
            return ((LabServerSoapAPI) this.labServerAPI).GetEffectiveQueueLength(userGroup, priorityHint);
        }
        return null;
    }

    /**
     *
     * @param experimentId
     * @return LabExperimentStatus
     */
    public LabExperimentStatus GetExperimentStatus(int experimentId) {
        if (this.labServerAPI instanceof LabServerRestAPI) {
            return ((LabServerRestAPI) this.labServerAPI).GetExperimentStatus(experimentId);
        }
        if (this.labServerAPI instanceof LabServerSoapAPI) {
            return ((LabServerSoapAPI) this.labServerAPI).GetExperimentStatus(experimentId);
        }
        return null;
    }

    /**
     *
     * @return String
     */
    public String GetLabConfiguration(String userGroup) {
        if (this.labServerAPI instanceof LabServerRestAPI) {
            return ((LabServerRestAPI) this.labServerAPI).GetLabConfiguration(userGroup);
        }
        if (this.labServerAPI instanceof LabServerSoapAPI) {
            return ((LabServerSoapAPI) this.labServerAPI).GetLabConfiguration(userGroup);
        }
        return null;
    }

    /**
     *
     * @return String
     */
    public String GetLabInfo() {
        if (this.labServerAPI instanceof LabServerRestAPI) {
            return ((LabServerRestAPI) this.labServerAPI).GetLabInfo();
        }
        if (this.labServerAPI instanceof LabServerSoapAPI) {
            return ((LabServerSoapAPI) this.labServerAPI).GetLabInfo();
        }
        return null;
    }

    /**
     *
     * @return LabStatus
     */
    public LabStatus GetLabStatus() {
        if (this.labServerAPI instanceof LabServerRestAPI) {
            return ((LabServerRestAPI) this.labServerAPI).GetLabStatus();
        }
        if (this.labServerAPI instanceof LabServerSoapAPI) {
            return ((LabServerSoapAPI) this.labServerAPI).GetLabStatus();
        }
        return null;
    }

    /**
     *
     * @param experimentId
     * @return ResultReport
     */
    public ResultReport RetrieveResult(int experimentId) {
        if (this.labServerAPI instanceof LabServerRestAPI) {
            return ((LabServerRestAPI) this.labServerAPI).RetrieveResult(experimentId);
        }
        if (this.labServerAPI instanceof LabServerSoapAPI) {
            return ((LabServerSoapAPI) this.labServerAPI).RetrieveResult(experimentId);
        }
        return null;
    }

    /**
     *
     * @param experimentSpecification
     * @param priorityHint
     * @param emailNotification
     * @return SubmissionReport
     */
    public SubmissionReport Submit(int experimentId, String experimentSpecification, String userGroup, int priorityHint) {
        if (this.labServerAPI instanceof LabServerRestAPI) {
            return ((LabServerRestAPI) this.labServerAPI).Submit(experimentId, experimentSpecification, userGroup, priorityHint);
        }
        if (this.labServerAPI instanceof LabServerSoapAPI) {
            return ((LabServerSoapAPI) this.labServerAPI).Submit(experimentId, experimentSpecification, userGroup, priorityHint);
        }
        return null;
    }

    /**
     *
     * @param experimentSpecification
     * @return ValidationReport
     */
    public ValidationReport Validate(String experimentSpecification, String userGroup) {
        if (this.labServerAPI instanceof LabServerRestAPI) {
            return ((LabServerRestAPI) this.labServerAPI).Validate(experimentSpecification, userGroup);
        }
        if (this.labServerAPI instanceof LabServerSoapAPI) {
            return ((LabServerSoapAPI) this.labServerAPI).Validate(experimentSpecification, userGroup);
        }
        return null;
    }

    /**
     *
     */
    public void Close() {
        if (this.labServerAPI instanceof LabServerRestAPI) {
            ((LabServerRestAPI) this.labServerAPI).Close();
        }
    }
}
