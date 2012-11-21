/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment;

import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labequipment.engine.LabExperimentSpecification;

/**
 *
 * @author uqlpayne
 */
public class ExperimentSpecification extends LabExperimentSpecification {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentSpecification.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for exception messages
     */
    private static final String STRERR_SomeParameter = "SomeParameter";
    private static final String STRERR_ValueNotSpecified_arg = "%s: Not specified!";
    private static final String STRERR_ValueNotNumber_arg = "%s: Not a number!";
    private static final String STRERR_ValueNotInteger_arg = "%s: Not an integer!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private char sourceLocation;
    private char[] absorberLocations;
    private int[] distances;
    private int duration;
    private int trials;

    public char getSourceLocation() {
        return sourceLocation;
    }

    public char[] getAbsorberLocations() {
        return absorberLocations;
    }

    public int[] getDistances() {
        return distances;
    }

    public int getDuration() {
        return duration;
    }

    public int getTrials() {
        return trials;
    }
    //</editor-fold>

    /**
     *
     * @param xmlSpecification
     * @throws Exception
     */
    public ExperimentSpecification(String xmlSpecification) throws Exception {
        super(xmlSpecification);

        final String methodName = "ExperimentSpecification";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        /*
         * Get the experiment parameters from the specification
         */
        try {
            /*
             * Get the source location
             */
            String source = XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_SourceName);
            this.sourceLocation = source.trim().charAt(0);

            /*
             * Get the list of absorber locations
             */
            String csvAbsorberLocations = XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_AbsorberName);
            String[] csvAbsorbersSplit = csvAbsorberLocations.split(Consts.STRCSV_SplitterChar);
            this.absorberLocations = new char[csvAbsorbersSplit.length];
            for (int i = 0; i < csvAbsorbersSplit.length; i++) {
                this.absorberLocations[i] = csvAbsorbersSplit[i].trim().charAt(0);
            }

            /*
             * Get the list of distances
             */
            String csvDistances = XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_Distance);
            String[] csvDistancesSplit = csvDistances.split(Consts.STRCSV_SplitterChar);
            this.distances = new int[csvDistancesSplit.length];
            for (int i = 0; i < csvDistancesSplit.length; i++) {
                this.distances[i] = Integer.parseInt(csvDistancesSplit[i]);
            }

            /*
             * Get the duration
             */
            this.duration = XmlUtilities.GetChildValueAsInt(this.nodeSpecification, Consts.STRXML_Duration);

            /*
             * Get the number of trials (repeat count)
             */
            this.trials = XmlUtilities.GetChildValueAsInt(this.nodeSpecification, Consts.STRXML_Repeat);

        } catch (XmlUtilitiesException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
