/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment;

import java.util.Arrays;
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
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">

    public static String ClassName() {
        return ExperimentSpecification.class.getSimpleName();
    }
    private String sourceName;
    private String[] absorberNames;
    private int[] distances;
    private int duration;
    private int repeat;

    public String getSourceName() {
        return sourceName;
    }

    public String[] getAbsorberNames() {
        return absorberNames;
    }

    public String getCsvAbsorberNames() {
        return this.GetCsvString(absorberNames);
    }

    public int[] getDistances() {
        return distances;
    }

    public String getCsvDistances() {
        return this.GetCsvString(distances);
    }

    public int getDuration() {
        return duration;
    }

    public int getRepeat() {
        return repeat;
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
            this.sourceName = XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_SourceName);

            /*
             * Get the list of absorber names
             */
            String csvAbsorberNames = XmlUtilities.GetChildValue(this.nodeSpecification, Consts.STRXML_AbsorberName);
            String[] csvAbsorberNamesSplit = csvAbsorberNames.split(Consts.STRCSV_SplitterChar);
            this.absorberNames = new String[csvAbsorberNamesSplit.length];
            for (int i = 0; i < csvAbsorberNamesSplit.length; i++) {
                this.absorberNames[i] = csvAbsorberNamesSplit[i].trim();
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
             * Sort the list of distances with smallest distance first keeping duplicates
             */
            Arrays.sort(this.distances);

            /*
             * Get the duration
             */
            this.duration = XmlUtilities.GetChildValueAsInt(this.nodeSpecification, Consts.STRXML_Duration);

            /*
             * Get the repeat count
             */
            this.repeat = XmlUtilities.GetChildValueAsInt(this.nodeSpecification, Consts.STRXML_Repeat);

        } catch (XmlUtilitiesException | NumberFormatException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param stringArray
     * @return String
     */
    private String GetCsvString(String[] stringArray) {
        String csvString = "";

        for (int i = 0; i < stringArray.length; i++) {
            csvString += String.format("%s%s", (!csvString.isEmpty()) ? Consts.STRCSV_SplitterChar : "", stringArray[i]);
        }

        return csvString;
    }

    /**
     *
     * @param intArray
     * @return String
     */
    private String GetCsvString(int[] intArray) {
        String csvString = "";

        for (int i = 0; i < intArray.length; i++) {
            csvString += String.format("%s%d", (!csvString.isEmpty()) ? Consts.STRCSV_SplitterChar : "", intArray[i]);
        }

        return csvString;
    }
}
