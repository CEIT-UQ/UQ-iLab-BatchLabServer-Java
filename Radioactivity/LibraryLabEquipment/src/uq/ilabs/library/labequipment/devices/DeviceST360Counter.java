/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment.devices;

import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Delay;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.labequipment.Consts;
import uq.ilabs.library.labequipment.engine.LabEquipmentConfiguration;
import uq.ilabs.library.labequipment.engine.devices.DeviceGeneric;

/**
 *
 * @author uqlpayne
 */
public class DeviceST360Counter extends DeviceGeneric {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = DeviceST360Counter.class.getName();
    private static final Level logLevel = Level.FINER;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_DistanceDuration_arg2 = "Distance: %d  Duration: %d";
    private static final String STRLOG_Data_arg = "Data: %d";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private double simDistance;
    private int simDuration;
    private int simMean;
    private double simPower;
    private double simDeviation;
    private Random random;
    //</editor-fold>

    /**
     *
     * @param labEquipmentConfiguration
     * @throws Exception
     */
    public DeviceST360Counter(LabEquipmentConfiguration labEquipmentConfiguration) throws Exception {
        super(labEquipmentConfiguration);

        final String methodName = "DeviceST360Counter";
        Logfile.WriteCalled(Level.CONFIG, STR_ClassName, methodName);

        try {
            /*
             * Simulation settings
             */
            Node node = XmlUtilities.GetChildNode(this.xmlNodeDevice, Consts.STRXML_Simulation);
            this.simDistance = XmlUtilities.GetChildValueAsDouble(node, Consts.STRXML_SimDistance);
            this.simDuration = XmlUtilities.GetChildValueAsInt(node, Consts.STRXML_SimDuration);
            this.simMean = XmlUtilities.GetChildValueAsInt(node, Consts.STRXML_SimMean);
            this.simPower = XmlUtilities.GetChildValueAsDouble(node, Consts.STRXML_SimPower);
            this.simDeviation = XmlUtilities.GetChildValueAsDouble(node, Consts.STRXML_SimDeviation);
        } catch (Exception ex) {
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

            /*
             * Create the random number generator and randomise the seed
             */
            int seed = (int) Calendar.getInstance().getTimeInMillis();
            this.random = new Random(seed);
            success = true;

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }

    /**
     *
     * @param duration
     * @return
     */
    public double GetCaptureDataTime(int duration) {
        double seconds;

        seconds = duration;

        return seconds;
    }

    /**
     *
     * @param distance
     * @param duration
     * @return
     */
    public int CaptureData(int distance, int duration) {
        final String methodName = "CaptureData";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_DistanceDuration_arg2, distance, duration));


        for (int i = 0; i < duration; i++) {
            Delay.MilliSeconds(1000);
            System.out.println("D");
        }

        int data = this.GenerateData(distance, duration);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Data_arg, data));

        return data;
    }

    /**
     * Generate simulated radioactivity data for the specified distance and duration
     *
     * @param distance The distance in millimeters from the source
     * @param duration The time in seconds to take the measurement
     * @return
     */
    private int GenerateData(int distance, int duration) {
        /*
         * Generate a value from a Gaussian distribution of random numbers
         */
        double dataGaussian = GetGaussian(random);

        /*
         * Adjust data for duration and distance
         */
        dataGaussian = AdjustData(dataGaussian, duration, distance);

        /*
         * Convert the simulated data from 'double' to 'int'
         */
        int value = (int) (dataGaussian + 0.5);

        /*
         * The value cannot be negative
         */
        if (value < 0) {
            value = 0;
        }

        return value;
    }

    /**
     * Adjust the data for the mean, standard deviation, duration and distance.
     *
     * @param data The array of data to adjust
     * @param duration The time in seconds to adjust the data
     * @param distance The distance to adjust the data
     * @return
     */
    private double AdjustData(double data, double duration, double distance) {
        /*
         * Calculate the scaling factors
         */
        double adjustStdDev = this.simDeviation * distance / this.simDistance;
        double adjustDuration = duration / this.simDuration;
        double adjustDistance = Math.pow(distance / this.simDistance, this.simPower);

        /*
         * Adjust for the mean and standard deviation
         */
        double value = data * adjustStdDev + this.simMean;

        /*
         * Now adjust for the duration
         */
        value *= adjustDuration;

        /*
         * Finally adjust for the distance
         */
        value /= adjustDistance;

        return value;
    }

    /**
     * Generate a Gaussian distribution of data with a mean of 0.0 and a standard deviation of 1.0 using the Box–Muller
     * transform method.
     *
     * @param random
     * @return
     */
    private double GetGaussian(Random random) {
        double random1;
        while (true) {
            /*
             * random1 must be > 0.0 for Math.log()
             */
            random1 = random.nextDouble();
            if (random1 > 0.0) {
                break;
            }
        }
        double random2 = random.nextDouble();

        double gaussian1 = Math.sqrt(-2.0 * Math.log(random1)) * Math.cos(Math.PI * 2.0 * random2);

        /*
         * Don't need the second number
         * double gaussian2 = Math.sqrt(-2.0 * Math.log(random1)) * Math.sin(Math.PI * 2.0 * random2);
         */

        return gaussian1;
    }
}
