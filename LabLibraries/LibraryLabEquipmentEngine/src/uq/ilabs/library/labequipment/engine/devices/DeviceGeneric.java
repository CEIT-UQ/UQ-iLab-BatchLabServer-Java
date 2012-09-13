/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.engine.devices;

import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Delay;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labequipment.engine.LabConsts;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;

/**
 *
 * @author uqlpayne
 */
public class DeviceGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DeviceGeneric.class.getName();
    private static final Level logLevel = Level.FINEST;
    /*
     * String constants for logfile messages
     */
    protected static final String STRLOG_Success_arg = "Success: %s";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    protected Node xmlNodeDevice;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    protected int initialiseDelay;

    public int getInitialiseDelay() {
        return initialiseDelay;
    }
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DeviceGeneric(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        final String methodName = "DeviceGeneric";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that parameters are valid
             */
            if (labEquipmentConfiguration == null) {
                throw new NullPointerException(LabEquipmentConfiguration.class.getSimpleName());
            }

            /*
             * Get the device configuration from the XML string
             */
            Document xmlDocument = XmlUtilities.GetDocumentFromString(labEquipmentConfiguration.GetXmlDeviceConfiguration(this.getClass().getSimpleName()));
            this.xmlNodeDevice = XmlUtilities.GetRootNode(xmlDocument, LabConsts.STRXML_Device);

            /*
             * Get the initialise delay
             */
            this.initialiseDelay = XmlUtilities.GetChildValueAsInt(this.xmlNodeDevice, LabConsts.STRXML_InitialiseDelay);

        } catch (NullPointerException | XmlUtilitiesException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    public boolean Initialise() {
        final String methodName = "Initialise";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            for (int i = 0; i < this.initialiseDelay; i++) {
                Delay.MilliSeconds(1000);
            }

            success = true;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }
}
