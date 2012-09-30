/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;

/**
 *
 * @author uqlpayne
 */
public class SimulationConfig {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = SimulationConfig.class.getName();
    private static final Level logLevel = Level.CONFIG;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Filename_arg = "Filename: '%s'";
    private static final String STRLOG_ParsingSimulationConfig = "Parsing SimulationConfig...";
    private static final String STRLOG_TitleVersion_arg2 = "Title: '%s'  Version: '%s'";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_XmlSimulationConfig = "xmlSimulationConfig";
    private static final String STRERR_Filename = "filename";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String title;
    private String version;
    private double simDistance;
    private int simDuration;
    private int simMean;
    private double simPower;
    private double simDeviation;
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

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public double getSimDistance() {
        return simDistance;
    }

    public int getSimDuration() {
        return simDuration;
    }

    public int getSimMean() {
        return simMean;
    }

    public double getSimPower() {
        return simPower;
    }

    public double getSimDeviation() {
        return simDeviation;
    }

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
     * @param xmlSimulationConfig
     * @throws Exception
     */
    public SimulationConfig(String xmlSimulationConfig) throws Exception {
        this(null, null, xmlSimulationConfig);
    }

    /**
     *
     * @param filepath
     * @param filename
     * @throws Exception
     */
    public SimulationConfig(String filepath, String filename) throws Exception {
        this(filepath, filename, null);
    }

    /**
     *
     * @param filepath
     * @param filename
     * @param xmlSimulationConfig
     * @throws Exception
     */
    private SimulationConfig(String filepath, String filename, String xmlSimulationConfig) throws Exception {
        final String methodName = "SimulationConfig";
        Logfile.WriteCalled(STR_ClassName, methodName);

        String logMessage = Logfile.STRLOG_Newline;

        try {
            Document document;

            /*
             * Check if an XML lab configuration string is specified
             */
            if (xmlSimulationConfig != null) {
                if (xmlSimulationConfig.trim().isEmpty()) {
                    throw new IllegalArgumentException(STRERR_XmlSimulationConfig);
                }

                /*
                 * Load the simulation configuration XML document from the string
                 */
                document = XmlUtilities.GetDocumentFromString(xmlSimulationConfig);
            } else {
                /*
                 * Check that a filename is specified
                 */
                if (filename == null) {
                    throw new NullPointerException(STRERR_Filename);
                }
                if (filename.trim().isEmpty()) {
                    throw new IllegalArgumentException(STRERR_Filename);
                }

                /*
                 * Combine the file path and name and check if the file exists
                 */
                File file = new File(filepath, filename);
                if (file.exists() == false) {
                    throw new FileNotFoundException(file.getAbsolutePath());
                }

                filename = file.getAbsolutePath();
                logMessage += String.format(STRLOG_Filename_arg, filename) + Logfile.STRLOG_Newline;

                /*
                 * Load the lab configuration XML document from the file
                 */
                document = XmlUtilities.GetDocumentFromFile(filepath, filename);
            }

            /*
             * Get the document's root node
             */
            Node nodeRoot = XmlUtilities.GetRootNode(document, Consts.STRXML_SimulationConfig);

            logMessage += STRLOG_ParsingSimulationConfig + Logfile.STRLOG_Newline;

            /*
             * Get information from the lab configuration node
             */
            this.title = XmlUtilities.GetAttribute(nodeRoot, Consts.STRXML_ATTR_Title, false);
            this.version = XmlUtilities.GetAttribute(nodeRoot, Consts.STRXML_ATTR_Version, false);
            logMessage += String.format(STRLOG_TitleVersion_arg2, this.title, this.version) + Logfile.STRLOG_Newline;

            /*
             * Simulation settings
             */
            this.simDistance = XmlUtilities.GetChildValueAsDouble(nodeRoot, Consts.STRXML_SimDistance);
            this.simDuration = XmlUtilities.GetChildValueAsInt(nodeRoot, Consts.STRXML_SimDuration);
            this.simMean = XmlUtilities.GetChildValueAsInt(nodeRoot, Consts.STRXML_SimMean);
            this.simPower = XmlUtilities.GetChildValueAsDouble(nodeRoot, Consts.STRXML_SimPower);
            this.simDeviation = XmlUtilities.GetChildValueAsDouble(nodeRoot, Consts.STRXML_SimDeviation);

            /*
             * Tube settings
             */
            Node node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_Tube);
            this.tubeOffsetDistance = XmlUtilities.GetChildValueAsInt(node, Consts.STRXML_OffsetDistance);
            this.tubeHomeDistance = XmlUtilities.GetChildValueAsInt(node, Consts.STRXML_HomeDistance);
            this.tubeMoveRate = XmlUtilities.GetChildValueAsDouble(node, Consts.STRXML_MoveRate);

            /*
             * Source settings
             */
            node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_Sources);
            this.sourceFirstLocation = XmlUtilities.GetChildValueAsChar(node, Consts.STRXML_FirstLocation);
            this.sourceHomeLocation = XmlUtilities.GetChildValueAsChar(node, Consts.STRXML_HomeLocation);

            /*
             * Source select times
             */
            String csvTimes = XmlUtilities.GetChildValue(node, Consts.STRXML_SelectTimes);
            String[] csvTimesSplit = csvTimes.split(Consts.STR_CsvSplitter);
            this.sourceSelectTimes = new double[csvTimesSplit.length];
            for (int i = 0; i < csvTimesSplit.length; i++) {
                this.sourceSelectTimes[i] = Double.parseDouble(csvTimesSplit[i]);
            }

            /*
             * Source return times
             */
            csvTimes = XmlUtilities.GetChildValue(node, Consts.STRXML_ReturnTimes);
            csvTimesSplit = csvTimes.split(Consts.STR_CsvSplitter);
            this.sourceReturnTimes = new double[csvTimesSplit.length];
            for (int i = 0; i < csvTimesSplit.length; i++) {
                this.sourceReturnTimes[i] = Double.parseDouble(csvTimesSplit[i]);
            }

            /*
             * Check if absorber settings are present
             */
            this.absorbersPresent = true;
            try {
                node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_Absorbers, true);
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
                csvTimesSplit = csvTimes.split(Consts.STR_CsvSplitter);
                this.absorberSelectTimes = new double[csvTimesSplit.length];
                for (int i = 0; i < csvTimesSplit.length; i++) {
                    this.absorberSelectTimes[i] = Double.parseDouble(csvTimesSplit[i]);
                }

                /*
                 * Absorber return times
                 */
                csvTimes = XmlUtilities.GetChildValue(node, Consts.STRXML_ReturnTimes);
                csvTimesSplit = csvTimes.split(Consts.STR_CsvSplitter);
                this.absorberReturnTimes = new double[csvTimesSplit.length];
                for (int i = 0; i < csvTimesSplit.length; i++) {
                    this.absorberReturnTimes[i] = Double.parseDouble(csvTimesSplit[i]);
                }
            }

            Logfile.Write(logLevel, logMessage);
        } catch (XmlUtilitiesException | NumberFormatException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }
}
