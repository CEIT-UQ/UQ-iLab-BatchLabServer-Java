/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient;

import java.util.logging.Level;
import uq.ilabs.library.lab.types.ClientSubmissionReport;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.ServiceTypes;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.servicebroker.ServiceBrokerRestAPI;
import uq.ilabs.library.servicebroker.ServiceBrokerSoapAPI;

/**
 *
 * @author uqlpayne
 */
public class ServiceBrokerAPI {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokerAPI.class.getName();
    private static final Level logLevel = Level.INFO;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_ServiceTypeUrl_arg2 = "ServiceType: %s  ServiceUrl: %s";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Object serviceBrokerAPI;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private long couponId;
    private String couponPasskey;
    private String labServerId;

    public long getCouponId() {
        return couponId;
    }

    public void setCouponId(long couponId) {
        this.couponId = couponId;
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            ((ServiceBrokerRestAPI) this.serviceBrokerAPI).setCouponId(couponId);
        } else if (this.serviceBrokerAPI instanceof ServiceBrokerSoapAPI) {
            ((ServiceBrokerSoapAPI) this.serviceBrokerAPI).setCouponId(couponId);
        }
    }

    public String getCouponPasskey() {
        return couponPasskey;
    }

    public void setCouponPasskey(String couponPasskey) {
        this.couponPasskey = couponPasskey;
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            ((ServiceBrokerRestAPI) this.serviceBrokerAPI).setCouponPasskey(couponPasskey);
        } else if (this.serviceBrokerAPI instanceof ServiceBrokerSoapAPI) {
            ((ServiceBrokerSoapAPI) this.serviceBrokerAPI).setCouponPasskey(couponPasskey);
        }
    }

    public String getLabServerId() {
        return labServerId;
    }

    public void setLabServerId(String labServerId) {
        this.labServerId = labServerId;
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            ((ServiceBrokerRestAPI) this.serviceBrokerAPI).setLabServerId(labServerId);
        } else if (this.serviceBrokerAPI instanceof ServiceBrokerSoapAPI) {
            ((ServiceBrokerSoapAPI) this.serviceBrokerAPI).setLabServerId(labServerId);
        }
    }
    //</editor-fold>

    /**
     *
     * @param serviceUrl
     * @param restful
     * @throws Exception
     */
    public ServiceBrokerAPI(ServiceTypes serviceType, String serviceUrl) throws Exception {
        final String methodName = "ServiceBrokerAPI";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ServiceTypeUrl_arg2, serviceType.toString(), serviceUrl));

        switch (serviceType) {
            case Rest:
                this.serviceBrokerAPI = new ServiceBrokerRestAPI(serviceUrl);
                break;
            case Soap:
                this.serviceBrokerAPI = new ServiceBrokerSoapAPI(serviceUrl);
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
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            return ((ServiceBrokerRestAPI) this.serviceBrokerAPI).Cancel(experimentId);
        }
        if (this.serviceBrokerAPI instanceof ServiceBrokerSoapAPI) {
            return ((ServiceBrokerSoapAPI) this.serviceBrokerAPI).Cancel(experimentId);
        }
        return false;
    }

    /**
     *
     * @return WaitEstimate
     */
    public WaitEstimate GetEffectiveQueueLength() {
        return this.GetEffectiveQueueLength(0);
    }

    /**
     *
     * @param priorityHint
     * @return WaitEstimate
     */
    public WaitEstimate GetEffectiveQueueLength(int priorityHint) {
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            return ((ServiceBrokerRestAPI) this.serviceBrokerAPI).GetEffectiveQueueLength(priorityHint);
        }
        if (this.serviceBrokerAPI instanceof ServiceBrokerSoapAPI) {
            return ((ServiceBrokerSoapAPI) this.serviceBrokerAPI).GetEffectiveQueueLength(priorityHint);
        }
        return null;
    }

    /**
     *
     * @param experimentId
     * @return LabExperimentStatus
     */
    public LabExperimentStatus GetExperimentStatus(int experimentId) {
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            return ((ServiceBrokerRestAPI) this.serviceBrokerAPI).GetExperimentStatus(experimentId);
        }
        if (this.serviceBrokerAPI instanceof ServiceBrokerSoapAPI) {
            return ((ServiceBrokerSoapAPI) this.serviceBrokerAPI).GetExperimentStatus(experimentId);
        }
        return null;
    }

    /**
     *
     * @return String
     */
    public String GetLabConfiguration() {
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            return ((ServiceBrokerRestAPI) this.serviceBrokerAPI).GetLabConfiguration();
        }
        if (this.serviceBrokerAPI instanceof ServiceBrokerSoapAPI) {
            return ((ServiceBrokerSoapAPI) this.serviceBrokerAPI).GetLabConfiguration();
        }
        return null;
    }

    /**
     *
     * @return String
     */
    public String GetLabInfo() {
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            return ((ServiceBrokerRestAPI) this.serviceBrokerAPI).GetLabInfo();
        }
        if (this.serviceBrokerAPI instanceof ServiceBrokerSoapAPI) {
            return ((ServiceBrokerSoapAPI) this.serviceBrokerAPI).GetLabInfo();
        }
        return null;
    }

    /**
     *
     * @return LabStatus
     */
    public LabStatus GetLabStatus() {
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            return ((ServiceBrokerRestAPI) this.serviceBrokerAPI).GetLabStatus();
        }
        if (this.serviceBrokerAPI instanceof ServiceBrokerSoapAPI) {
            return ((ServiceBrokerSoapAPI) this.serviceBrokerAPI).GetLabStatus();
        }
        return null;
    }

    /**
     *
     * @param experimentId
     * @return ResultReport
     */
    public ResultReport RetrieveResult(int experimentId) {
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            return ((ServiceBrokerRestAPI) this.serviceBrokerAPI).RetrieveResult(experimentId);
        }
        if (this.serviceBrokerAPI instanceof ServiceBrokerSoapAPI) {
            return ((ServiceBrokerSoapAPI) this.serviceBrokerAPI).RetrieveResult(experimentId);
        }
        return null;
    }

    /**
     *
     * @param experimentSpecification
     * @return ClientSubmissionReport
     */
    public ClientSubmissionReport Submit(String experimentSpecification) {
        return this.Submit(experimentSpecification, 0, false);
    }

    /**
     *
     * @param experimentSpecification
     * @param priorityHint
     * @param emailNotification
     * @return ClientSubmissionReport
     */
    public ClientSubmissionReport Submit(String experimentSpecification, int priorityHint, boolean emailNotification) {
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            return ((ServiceBrokerRestAPI) this.serviceBrokerAPI).Submit(experimentSpecification, priorityHint, emailNotification);
        }
        if (this.serviceBrokerAPI instanceof ServiceBrokerSoapAPI) {
            return ((ServiceBrokerSoapAPI) this.serviceBrokerAPI).Submit(experimentSpecification, priorityHint, emailNotification);
        }
        return null;
    }

    /**
     *
     * @param experimentSpecification
     * @return ValidationReport
     */
    public ValidationReport Validate(String experimentSpecification) {
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            return ((ServiceBrokerRestAPI) this.serviceBrokerAPI).Validate(experimentSpecification);
        }
        if (this.serviceBrokerAPI instanceof ServiceBrokerSoapAPI) {
            return ((ServiceBrokerSoapAPI) this.serviceBrokerAPI).Validate(experimentSpecification);
        }
        return null;
    }

    /**
     *
     * @param experimentId
     * @return boolean
     */
    public boolean Notify(int experimentId) {
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            return ((ServiceBrokerRestAPI) this.serviceBrokerAPI).Notify(experimentId);
        }
        if (this.serviceBrokerAPI instanceof ServiceBrokerSoapAPI) {
            return ((ServiceBrokerSoapAPI) this.serviceBrokerAPI).Notify(experimentId);
        }
        return false;
    }

    /**
     *
     */
    public void Close() {
        if (this.serviceBrokerAPI instanceof ServiceBrokerRestAPI) {
            ((ServiceBrokerRestAPI) this.serviceBrokerAPI).Close();
        }
    }
}
