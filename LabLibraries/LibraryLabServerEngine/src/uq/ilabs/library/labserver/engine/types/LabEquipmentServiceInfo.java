/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine.types;

import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.LabEquipmentAPI;
import uq.ilabs.library.labserver.database.types.LabEquipmentInfo;

/**
 *
 * @author uqlpayne
 */
public class LabEquipmentServiceInfo {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentServiceInfo.class.getName();
    private static final Level logLevel = Level.FINER;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private LabEquipmentInfo labEquipmentInfo;
    private LabEquipmentAPI labEquipmentAPI;
    private String identifier;

    public LabEquipmentInfo getLabEquipmentInfo() {
        return labEquipmentInfo;
    }

    public LabEquipmentAPI getLabEquipmentAPI() {
        return labEquipmentAPI;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getServiceUrl() {
        return labEquipmentInfo.getServiceUrl();
    }

    public String getPasskey() {
        return labEquipmentInfo.getPasskey();
    }

    public boolean isEnabled() {
        return labEquipmentInfo.isEnabled();
    }
    //</editor-fold>

    /**
     *
     * @param identifier
     * @param labEquipmentInfo
     */
    public LabEquipmentServiceInfo(String identifier, LabEquipmentInfo labEquipmentInfo) throws Exception {
        final String methodName = "LabEquipmentServiceInfo";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that all parameters are valid
             */
            if (labEquipmentInfo == null) {
                throw new NullPointerException(LabEquipmentInfo.class.getSimpleName());
            }

            /*
             * Save to local variables
             */
            this.identifier = identifier;
            this.labEquipmentInfo = labEquipmentInfo;

            /*
             * Check if the service url is specified
             */
            if (labEquipmentInfo.getServiceUrl() != null) {
                /*
                 * Get a proxy to the LabEquipment service
                 */
                this.labEquipmentAPI = new LabEquipmentAPI(labEquipmentInfo.getServiceUrl());
                if (this.labEquipmentAPI == null) {
                    throw new NullPointerException(LabEquipmentAPI.class.getSimpleName());
                }

                /*
                 * Set the identifier and passkey
                 */
                this.labEquipmentAPI.setIdentifier(identifier);
                this.labEquipmentAPI.setPasskey(labEquipmentInfo.getPasskey());
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
