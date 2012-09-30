/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.util.Arrays;
import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labserver.engine.LabExperimentSpecification;

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
    private static final String STRERR_ValueNotSpecified_arg = "%s: Not specified!";
    private static final String STRERR_ValueNotInteger_arg = "%s: Not an integer!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String source;
    private String[] absorbers;
    private int[] distances;
    private int duration;
    private int trials;

    public String getSource() {
        return source;
    }

    public String[] getAbsorbers() {
        return absorbers;
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
             * Get the source name
             */
            try {
                this.source = XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_SourceName);
            } catch (XmlUtilitiesException ex) {
                throw new XmlUtilitiesException(String.format(STRERR_ValueNotSpecified_arg, Consts.STRXML_SourceName));
            }

            /*
             * Get list of absorber names
             */
            String csvAbsorbers = XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_AbsorberName);
            String[] csvAbsorbersSplit = csvAbsorbers.split(Consts.STR_CsvSplitter);
            this.absorbers = new String[csvAbsorbersSplit.length];
            for (int i = 0; i < csvAbsorbersSplit.length; i++) {
                this.absorbers[i] = csvAbsorbersSplit[i].trim();
            }

            /*
             * Get list of distances and sort smallest to largest
             */
            String csvDistances = XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_Distance);
            String[] csvDistancesSplit = csvDistances.split(Consts.STR_CsvSplitter);
            this.distances = new int[csvDistancesSplit.length];
            for (int i = 0; i < csvDistancesSplit.length; i++) {
                this.distances[i] = Integer.parseInt(csvDistancesSplit[i]);
            }
            Arrays.sort(distances);

            /*
             * Get the duration
             */
            try {
                this.duration = XmlUtilities.GetChildValueAsInt(this.nodeSpecification, Consts.STRXML_Duration);
            } catch (XmlUtilitiesException ex) {
                throw new XmlUtilitiesException(String.format(STRERR_ValueNotSpecified_arg, Consts.STRXML_Duration));
            } catch (NumberFormatException ex) {
                throw new NumberFormatException(String.format(STRERR_ValueNotInteger_arg, Consts.STRXML_Duration));
            }

            /*
             * Get the number of trials (repeat count)
             */
            try {
                this.trials = XmlUtilities.GetChildValueAsInt(this.nodeSpecification, Consts.STRXML_Repeat);
            } catch (XmlUtilitiesException ex) {
                throw new XmlUtilitiesException(String.format(STRERR_ValueNotSpecified_arg, Consts.STRXML_Repeat));
            } catch (NumberFormatException ex) {
                throw new NumberFormatException(String.format(STRERR_ValueNotInteger_arg, Consts.STRXML_Repeat));
            }
        } catch (XmlUtilitiesException | NumberFormatException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
