/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.util.logging.Level;
import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.LabEquipmentStatus;
import uq.ilabs.library.lab.types.ServiceTypes;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.LabEquipmentRestAPI;
import uq.ilabs.library.labequipment.LabEquipmentSoapAPI;

/**
 *
 * @author uqlpayne
 */
public class LabEquipmentAPI {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentAPI.class.getName();
    private static final Level logLevel = Level.INFO;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_ServiceTypeUrl_arg2 = "ServiceType: %s  ServiceUrl: %s";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private Object labEquipmentAPI;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String identifier;
    private String passkey;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        if (this.labEquipmentAPI instanceof LabEquipmentRestAPI) {
            ((LabEquipmentRestAPI) this.labEquipmentAPI).setIdentifier(identifier);
        } else if (this.labEquipmentAPI instanceof LabEquipmentSoapAPI) {
            ((LabEquipmentSoapAPI) this.labEquipmentAPI).setIdentifier(identifier);
        }
    }

    public String getPasskey() {
        return passkey;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
        if (this.labEquipmentAPI instanceof LabEquipmentRestAPI) {
            ((LabEquipmentRestAPI) this.labEquipmentAPI).setPasskey(passkey);
        } else if (this.labEquipmentAPI instanceof LabEquipmentSoapAPI) {
            ((LabEquipmentSoapAPI) this.labEquipmentAPI).setPasskey(passkey);
        }
    }
    //</editor-fold>

    /**
     *
     * @param serviceType
     * @param serviceUrl
     * @throws Exception
     */
    public LabEquipmentAPI(ServiceTypes serviceType, String serviceUrl) throws Exception {
        final String methodName = "LabEquipmentAPI";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_ServiceTypeUrl_arg2, serviceType.toString(), serviceUrl));

        switch (serviceType) {
            case Rest:
                this.labEquipmentAPI = new LabEquipmentRestAPI(serviceUrl);
                break;
            case Soap:
                this.labEquipmentAPI = new LabEquipmentSoapAPI(serviceUrl);
                break;
            default:
                throw new IllegalArgumentException(ServiceTypes.class.getSimpleName());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return LabEquipmentStatus
     * @throws Exception
     */
    public LabEquipmentStatus GetLabEquipmentStatus() throws Exception {
        if (this.labEquipmentAPI instanceof LabEquipmentRestAPI) {
            return ((LabEquipmentRestAPI) this.labEquipmentAPI).GetLabEquipmentStatus();
        }
        if (this.labEquipmentAPI instanceof LabEquipmentSoapAPI) {
            return ((LabEquipmentSoapAPI) this.labEquipmentAPI).GetLabEquipmentStatus();
        }
        return null;
    }

    /**
     *
     * @return int
     * @throws Exception
     */
    public int GetTimeUntilReady() throws Exception {
        if (this.labEquipmentAPI instanceof LabEquipmentRestAPI) {
            return ((LabEquipmentRestAPI) this.labEquipmentAPI).GetTimeUntilReady();
        }
        if (this.labEquipmentAPI instanceof LabEquipmentSoapAPI) {
            return ((LabEquipmentSoapAPI) this.labEquipmentAPI).GetTimeUntilReady();
        }
        return -1;
    }

    /**
     *
     * @param xmlSpecification
     * @return ValidationReport
     * @throws Exception
     */
    public ValidationReport Validate(String xmlSpecification) throws Exception {
        if (this.labEquipmentAPI instanceof LabEquipmentRestAPI) {
            return ((LabEquipmentRestAPI) this.labEquipmentAPI).Validate(xmlSpecification);
        }
        if (this.labEquipmentAPI instanceof LabEquipmentSoapAPI) {
            return ((LabEquipmentSoapAPI) this.labEquipmentAPI).Validate(xmlSpecification);
        }
        return null;
    }

    /**
     *
     * @param xmlSpecification
     * @return ExecutionStatus
     * @throws Exception
     */
    public ExecutionStatus StartLabExecution(String xmlSpecification) throws Exception {
        if (this.labEquipmentAPI instanceof LabEquipmentRestAPI) {
            return ((LabEquipmentRestAPI) this.labEquipmentAPI).StartLabExecution(xmlSpecification);
        }
        if (this.labEquipmentAPI instanceof LabEquipmentSoapAPI) {
            return ((LabEquipmentSoapAPI) this.labEquipmentAPI).StartLabExecution(xmlSpecification);
        }
        return null;
    }

    /**
     *
     * @param executionId
     * @return ExecutionStatus
     * @throws Exception
     */
    public ExecutionStatus GetLabExecutionStatus(int executionId) throws Exception {
        if (this.labEquipmentAPI instanceof LabEquipmentRestAPI) {
            return ((LabEquipmentRestAPI) this.labEquipmentAPI).GetLabExecutionStatus(executionId);
        }
        if (this.labEquipmentAPI instanceof LabEquipmentSoapAPI) {
            return ((LabEquipmentSoapAPI) this.labEquipmentAPI).GetLabExecutionStatus(executionId);
        }
        return null;
    }

    /**
     *
     * @param executionId
     * @return String
     * @throws Exception
     */
    public String GetLabExecutionResults(int executionId) throws Exception {
        if (this.labEquipmentAPI instanceof LabEquipmentRestAPI) {
            return ((LabEquipmentRestAPI) this.labEquipmentAPI).GetLabExecutionResults(executionId);
        }
        if (this.labEquipmentAPI instanceof LabEquipmentSoapAPI) {
            return ((LabEquipmentSoapAPI) this.labEquipmentAPI).GetLabExecutionResults(executionId);
        }
        return null;
    }

    /**
     *
     * @param executionId
     * @return boolean
     * @throws Exception
     */
    public boolean CancelLabExecution(int executionId) throws Exception {
        if (this.labEquipmentAPI instanceof LabEquipmentRestAPI) {
            return ((LabEquipmentRestAPI) this.labEquipmentAPI).CancelLabExecution(executionId);
        }
        if (this.labEquipmentAPI instanceof LabEquipmentSoapAPI) {
            return ((LabEquipmentSoapAPI) this.labEquipmentAPI).CancelLabExecution(executionId);
        }
        return false;
    }
}
