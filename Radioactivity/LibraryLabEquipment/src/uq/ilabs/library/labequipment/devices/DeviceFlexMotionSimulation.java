/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.devices;

import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Delay;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;

/**
 *
 * @author uqlpayne
 */
public class DeviceFlexMotionSimulation extends DeviceFlexMotion {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DeviceFlexMotionSimulation.class.getName();
    private static final Level logLevel = Level.FINER;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private int currentTubeDistance;
    private char currentSourceLocation;
    private char currentAbsorberLocation;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private boolean delaysSimulated;

    public boolean isDelaysSimulated() {
        return delaysSimulated;
    }

    public void setDelaysSimulated(boolean delaysSimulated) {
        this.delaysSimulated = delaysSimulated;
    }
    //</editor-fold>

    public DeviceFlexMotionSimulation(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration);

        final String methodName = "DeviceFlexMotionSimulation";
        Logfile.WriteCalled(Level.CONFIG, STR_ClassName, methodName);

        try {
            /*
             * Initialise properties
             */
            this.delaysSimulated = true;

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(Level.CONFIG, STR_ClassName, methodName);
    }

    @Override
    public boolean Initialise() {
        final String methodName = "Initialise";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        boolean success = false;

        try {
            /*
             * Initialise local variables and properties
             */
            this.currentSourceLocation = this.sourceHomeLocation;
            this.currentAbsorberLocation = this.absorberHomeLocation;
            this.currentTubeDistance = this.tubeHomeDistance;

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
    @Override
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
    @Override
    protected double GetSourceSelectTime(char toLocation) {
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
    @Override
    protected double GetSourceReturnTime(char fromLocation) {
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
    @Override
    protected double GetAbsorberSelectTime(char toLocation) {
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
    @Override
    protected double GetAbsorberReturnTime(char fromLocation) {
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
     * @param targetDistance
     * @return boolean
     */
    @Override
    public boolean SetTubeDistance(int distance) {
        final String methodName = "SetTubeDistance";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Distance_arg, distance));

        /*
         * Check if simulating delays
         */
        if (this.delaysSimulated == true) {
            int seconds = (int) this.GetTubeMoveTime(this.currentTubeDistance, distance);

            for (int i = 0; i < seconds; i++) {
                Delay.MilliSeconds(1000);
                System.out.println("T");
            }
        }

        this.currentTubeDistance = distance;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return true;
    }

    /**
     *
     * @param location
     * @return boolean
     */
    @Override
    public boolean SetAbsorberLocation(char location) {
        final String methodName = "SetAbsorberLocation";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Location_arg, location));

        if (this.absorbersPresent == true) {
            /*
             * Check if simulating delays
             */
            if (this.delaysSimulated == true) {
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
    @Override
    public boolean SetSourceLocation(char location) {
        final String methodName = "SetSourceLocation";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Location_arg, location));

        /*
         * Check if simulating delays
         */
        if (this.delaysSimulated == true) {
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
        }

        this.currentSourceLocation = location;

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);

        return true;
    }
}
