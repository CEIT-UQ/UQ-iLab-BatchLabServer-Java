/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.devices;

import java.util.logging.Level;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Delay;
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
    private static final String STRLOG_Location_arg = "Location: %s";
    private static final String STRLOG_Distance_arg = "Distance: %d";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private int currentTubeDistance;
    private char currentSourceLocation;
    private char currentAbsorberLocation;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private int tubeOffsetDistance;
    private int tubeHomeDistance;
    private double tubeMoveRate;
    private char sourceFirstLocation;
    private char sourceLastLocation;
    private char sourceHomeLocation;
    private double[] sourceSelectTimes;
    private double[] sourceReturnTimes;
    private boolean absorbersPresent;
    private char absorberFirstLocation;
    private char absorberLastLocation;
    private char absorberHomeLocation;
    private double[] absorberSelectTimes;
    private double[] absorberReturnTimes;

    public int getTubeOffsetDistance() {
        return tubeOffsetDistance;
    }

    public int getTubeHomeDistance() {
        return tubeHomeDistance;
    }

    public double getTubeMoveRate() {
        return tubeMoveRate;
    }

    public char getSourceFirstLocation() {
        return sourceFirstLocation;
    }

    public char getSourceLastLocation() {
        return sourceLastLocation;
    }

    public char getSourceHomeLocation() {
        return sourceHomeLocation;
    }

    public double[] getSourceSelectTimes() {
        return sourceSelectTimes;
    }

    public double[] getSourceReturnTimes() {
        return sourceReturnTimes;
    }

    public boolean isAbsorbersPresent() {
        return absorbersPresent;
    }

    public char getAbsorberFirstLocation() {
        return absorberFirstLocation;
    }

    public char getAbsorberLastLocation() {
        return absorberLastLocation;
    }

    public char getAbsorberHomeLocation() {
        return absorberHomeLocation;
    }

    public double[] getAbsorberSelectTimes() {
        return absorberSelectTimes;
    }

    public double[] getAbsorberReturnTimes() {
        return absorberReturnTimes;
    }
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DeviceFlexMotion(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration);

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
             * Check if absorber settings are present
             */
            this.absorbersPresent = true;
            try {
                node = XmlUtilities.GetChildNode(this.xmlNodeDevice, Consts.STRXML_Absorbers, true);
            } catch (Exception ex) {
                /*
                 * No absorbers
                 */
                this.absorbersPresent = false;
                this.absorberFirstLocation = this.sourceFirstLocation;
                this.absorberHomeLocation = this.sourceFirstLocation;
            }

            /*
             * Absorber settings
             */
            if (this.absorbersPresent == true) {
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
        Logfile.WriteCalled(STR_ClassName, methodName);

        boolean success = false;

        try {
//            success = super.Initialise();
            this.currentSourceLocation = this.sourceHomeLocation;
            this.currentAbsorberLocation = this.absorberHomeLocation;
            this.currentTubeDistance = this.tubeHomeDistance;
            success = true;

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName,
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
        double seconds;

        /*
         * Get the absolute distance
         */
        int distance = endDistance - startDistance;
        if (distance < 0) {
            distance = -distance;
        }

        /*
         * Tube move rate is in seconds per millimetre
         */
        seconds = (distance * this.tubeMoveRate);

        return seconds;
    }

    /**
     *
     * @param toLocation
     * @return
     */
    public double GetSourceSelectTime(char toLocation) {
        double seconds = 0.0;

        int index = toLocation - this.sourceFirstLocation;
        if (index >= 0 && index < this.sourceSelectTimes.length) {
            seconds = this.sourceSelectTimes[index];
        }

        return seconds;
    }

    /**
     *
     * @param fromLocation
     * @return
     */
    public double GetSourceReturnTime(char fromLocation) {
        double seconds = 0.0;

        int index = fromLocation - this.sourceFirstLocation;
        if (index >= 0 && index < this.sourceReturnTimes.length) {
            seconds = this.sourceReturnTimes[index];
        }

        return seconds;
    }

    /**
     *
     * @param toLocation
     * @return
     */
    public double GetAbsorberSelectTime(char toLocation) {
        double seconds = 0.0;

        if (this.absorbersPresent == true) {
            int index = toLocation - this.absorberFirstLocation;
            if (index >= 0 && index < this.absorberSelectTimes.length) {
                seconds = this.absorberSelectTimes[index];
            }
        }

        return seconds;
    }

    /**
     *
     * @param fromLocation
     * @return
     */
    public double GetAbsorberReturnTime(char fromLocation) {
        double seconds = 0.0;

        if (this.absorbersPresent == true) {
            int index = fromLocation - this.absorberFirstLocation;
            if (index >= 0 && index < this.absorberReturnTimes.length) {
                seconds = this.absorberReturnTimes[index];
            }
        }

        return seconds;
    }

    /**
     *
     * @param location
     * @return boolean
     */
    public boolean SetAbsorberLocation(char location) {
        final String methodName = "SetAbsorberLocation";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Location_arg, location));

        if (this.absorbersPresent == true) {
            /*
             * Determine if selecting or returning absorber
             */
            int seconds;
            if (location != this.absorberHomeLocation) {
                seconds = (int) this.GetAbsorberSelectTime(location);
            } else {
                seconds = (int) this.GetAbsorberReturnTime(this.currentAbsorberLocation);
            }

            for (int i = 0; i < seconds; i++) {
                Delay.MilliSeconds(1000);
                System.out.println("A");
            }

            this.currentAbsorberLocation = location;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return true;
    }

    /**
     *
     * @param location
     * @return boolean
     */
    public boolean SetSourceLocation(char location) {
        final String methodName = "SetSourceLocation";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Location_arg, location));

        /*
         * Determine if selecting or returning source
         */
        int seconds;
        if (location != this.sourceHomeLocation) {
            seconds = (int) this.GetSourceSelectTime(location);
        } else {
            seconds = (int) this.GetSourceReturnTime(this.currentSourceLocation);
        }

        for (int i = 0; i < seconds; i++) {
            Delay.MilliSeconds(1000);
            System.out.println("S");
        }

        this.currentSourceLocation = location;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return true;
    }

    /**
     *
     * @param targetDistance
     * @return boolean
     */
    public boolean SetTubeDistance(int distance) {
        final String methodName = "SetTubeDistance";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Distance_arg, distance));

        int seconds = (int) this.GetTubeMoveTime(this.currentTubeDistance, distance);

        for (int i = 0; i < seconds; i++) {
            Delay.MilliSeconds(1000);
            System.out.println("T");
        }

        this.currentTubeDistance = distance;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return true;
    }
}
