/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.devices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labequipment.Consts;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;
import uq.ilabs.library.labequipment.engine.devices.DeviceGeneric;

/**
 *
 * @author uqlpayne
 */
public class DeviceFlexMotion extends DeviceGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DeviceFlexMotion.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants for logfile messages
     */
    protected static final String STRLOG_Location_arg = "Location: %s";
    protected static final String STRLOG_Distance_arg = "Distance: %d";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private HashMap<String, Character> mapSources;
    private HashMap<String, Character> mapAbsorbers;
    //
    protected int tubeOffsetDistance;
    protected double tubeMoveRate;
    protected char sourceFirstLocation;
    protected char sourceHomeLocation;
    protected double[] sourceSelectTimes;
    protected double[] sourceReturnTimes;
    protected boolean absorbersPresent;
    protected char absorberFirstLocation;
    protected char absorberHomeLocation;
    protected double[] absorberSelectTimes;
    protected double[] absorberReturnTimes;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">

    public static String ClassName() {
        return DeviceFlexMotion.class.getSimpleName();
    }
    protected int tubeHomeDistance;
    protected String sourceHomeName;
    protected String absorberHomeName;

    public int getTubeHomeDistance() {
        return tubeHomeDistance;
    }

    public String getSourceHomeName() {
        return sourceHomeName;
    }

    public String getAbsorberHomeName() {
        return absorberHomeName;
    }
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DeviceFlexMotion(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration, DeviceFlexMotion.class.getSimpleName());

        final String methodName = "DeviceFlexMotion";
        Logfile.WriteCalled(Level.CONFIG, STR_ClassName, methodName);

        try {
            /*
             * Tube settings
             */
            Node node = XmlUtilities.GetChildNode(this.xmlNodeDevice, Consts.STRXML_Tube);
            this.tubeOffsetDistance = XmlUtilities.GetChildValueAsInt(node, Consts.STRXML_OffsetDistance);
            this.tubeHomeDistance = XmlUtilities.GetChildValueAsInt(node, Consts.STRXML_HomeDistance);
            this.tubeMoveRate = XmlUtilities.GetChildValueAsDouble(node, Consts.STRXML_MoveRate);

            /*
             * Source settings
             */
            node = XmlUtilities.GetChildNode(this.xmlNodeDevice, Consts.STRXML_Sources);
            this.sourceFirstLocation = XmlUtilities.GetChildValueAsChar(node, Consts.STRXML_FirstLocation);
            this.sourceHomeLocation = XmlUtilities.GetChildValueAsChar(node, Consts.STRXML_HomeLocation);

            /*
             * Source select times
             */
            String csvTimes = XmlUtilities.GetChildValue(node, Consts.STRXML_SelectTimes);
            String[] csvTimesSplit = csvTimes.split(Consts.STRCSV_SplitterChar);
            this.sourceSelectTimes = new double[csvTimesSplit.length];
            for (int i = 0; i < csvTimesSplit.length; i++) {
                this.sourceSelectTimes[i] = Double.parseDouble(csvTimesSplit[i]);
            }

            /*
             * Source return times
             */
            csvTimes = XmlUtilities.GetChildValue(node, Consts.STRXML_ReturnTimes);
            csvTimesSplit = csvTimes.split(Consts.STRCSV_SplitterChar);
            this.sourceReturnTimes = new double[csvTimesSplit.length];
            for (int i = 0; i < csvTimesSplit.length; i++) {
                this.sourceReturnTimes[i] = Double.parseDouble(csvTimesSplit[i]);
            }

            /*
             * Source name to location mapping
             */
            this.mapSources = new HashMap<>();
            ArrayList nodeList = XmlUtilities.GetChildNodeList(node, Consts.STRXML_Source);
            for (int i = 0; i < nodeList.size(); i++) {
                Node nodeSource = (Node) nodeList.get(i);

                /*
                 * Get the source name and location
                 */
                String name = XmlUtilities.GetChildValue(nodeSource, Consts.STRXML_Name);
                char location = XmlUtilities.GetChildValueAsChar(nodeSource, Consts.STRXML_Location);

                /*
                 * Check if this is the home location, if it is save the source name
                 */
                if (location == this.sourceHomeLocation) {
                    this.sourceHomeName = name;
                }

                /*
                 * Add the mapping
                 */
                this.mapSources.put(name, location);
            }

            /*
             * Absorber settings, may not be present
             */
            node = XmlUtilities.GetChildNode(this.xmlNodeDevice, Consts.STRXML_Absorbers);

            try {
                this.absorberFirstLocation = XmlUtilities.GetChildValueAsChar(node, Consts.STRXML_FirstLocation);
                this.absorberHomeLocation = XmlUtilities.GetChildValueAsChar(node, Consts.STRXML_HomeLocation);

                /*
                 * Absorber select times
                 */
                csvTimes = XmlUtilities.GetChildValue(node, Consts.STRXML_SelectTimes);
                csvTimesSplit = csvTimes.split(Consts.STRCSV_SplitterChar);
                this.absorberSelectTimes = new double[csvTimesSplit.length];
                for (int i = 0; i < csvTimesSplit.length; i++) {
                    this.absorberSelectTimes[i] = Double.parseDouble(csvTimesSplit[i]);
                }

                /*
                 * Absorber return times
                 */
                csvTimes = XmlUtilities.GetChildValue(node, Consts.STRXML_ReturnTimes);
                csvTimesSplit = csvTimes.split(Consts.STRCSV_SplitterChar);
                this.absorberReturnTimes = new double[csvTimesSplit.length];
                for (int i = 0; i < csvTimesSplit.length; i++) {
                    this.absorberReturnTimes[i] = Double.parseDouble(csvTimesSplit[i]);
                }

                this.absorbersPresent = true;
            } catch (XmlUtilitiesException | NumberFormatException ex) {
                /*
                 * No absorbers
                 */
                this.absorbersPresent = false;
            }

            /*
             * Absorber name to location mapping
             */
            this.mapAbsorbers = new HashMap<>();
            nodeList = XmlUtilities.GetChildNodeList(node, Consts.STRXML_Absorber, false);
            for (int i = 0; i < nodeList.size(); i++) {
                Node nodeAbsorber = (Node) nodeList.get(i);

                /*
                 * Get the source name and location
                 */
                String name = XmlUtilities.GetChildValue(nodeAbsorber, Consts.STRXML_Name);
                char location = XmlUtilities.GetChildValueAsChar(nodeAbsorber, Consts.STRXML_Location);

                /*
                 * Check if this is the home location, if it is save the absorber name
                 */
                if (location == this.absorberHomeLocation) {
                    this.absorberHomeName = name;
                }

                /*
                 * Add the mapping
                 */
                this.mapAbsorbers.put(name, location);
            }
        } catch (XmlUtilitiesException | NumberFormatException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(Level.CONFIG, STR_ClassName, methodName);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean Initialise() {
        final String methodName = "Initialise";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Nothing to do here
             */

            success = true;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     * Get the time in seconds it takes to move the tube from one position to another.
     *
     * @param startDistance The distance in millimeters for the start of the move.
     * @param endDistance The distance in millimeters for the end of the move.
     * @return
     */
    public double GetTubeMoveTime(int startDistance, int endDistance) {
        return 0.0;
    }

    /**
     *
     * @param toName
     * @return double
     */
    public double GetSourceSelectTime(String toName) {
        double time = 0.0;

        Character location = this.mapSources.get(toName);
        if (location != null) {
            time = this.GetSourceSelectTime(location);
        }

        return time;
    }

    /**
     *
     * @param fromName
     * @return double
     */
    public double GetSourceReturnTime(String fromName) {
        double time = 0.0;

        Character location = this.mapSources.get(fromName);
        if (location != null) {
            time = this.GetSourceReturnTime(location);
        }

        return time;
    }

    /**
     *
     * @param toName
     * @return double
     */
    public double GetAbsorberSelectTime(String toName) {
        double time = 0.0;

        Character location = this.mapAbsorbers.get(toName);
        if (location != null) {
            time = this.GetAbsorberSelectTime(location);
        }

        return time;
    }

    /**
     *
     * @param fromName
     * @return double
     */
    public double GetAbsorberReturnTime(String fromName) {
        double time = 0.0;

        Character location = this.mapAbsorbers.get(fromName);
        if (location != null) {
            time = this.GetAbsorberReturnTime(location);
        }

        return time;
    }

    /**
     *
     * @param distance
     * @return boolean
     */
    public boolean SelectTubeDistance(int distance) {
        return this.SetTubeDistance(distance);
    }

    /**
     *
     * @param name
     * @return
     */
    public boolean SelectSource(String name) {
        boolean success = false;

        Character location = this.mapSources.get(name);
        if (location != null) {
            success = this.SetSourceLocation(location.charValue());
        }

        return success;
    }

    /**
     *
     * @param name
     * @return boolean
     */
    public boolean SelectAbsorber(String name) {
        boolean success = true;

        if (this.absorbersPresent == true) {
            success = false;

            Character location = this.mapAbsorbers.get(name);
            if (location != null) {
                success = this.SetAbsorberLocation(location.charValue());
            }
        }

        return success;
    }

    /**
     *
     * @param toLocation
     * @return
     */
    protected double GetSourceSelectTime(char toLocation) {
        return 0.0;
    }

    /**
     *
     * @param fromLocation
     * @return
     */
    protected double GetSourceReturnTime(char fromLocation) {
        return 0.0;
    }

    /**
     *
     * @param toLocation
     * @return
     */
    protected double GetAbsorberSelectTime(char toLocation) {
        return 0.0;
    }

    /**
     *
     * @param fromLocation
     * @return
     */
    protected double GetAbsorberReturnTime(char fromLocation) {
        return 0.0;
    }

    /**
     *
     * @param distance
     * @return boolean
     */
    protected boolean SetTubeDistance(int distance) {
        return true;
    }

    /**
     *
     * @param location
     * @return boolean
     */
    protected boolean SetAbsorberLocation(char location) {
        return true;
    }

    /**
     *
     * @param location
     * @return boolean
     */
    protected boolean SetSourceLocation(char location) {
        return true;
    }
}
