/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.util.ArrayList;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.labserver.engine.LabConfiguration;

/**
 *
 * @author uqlpayne
 */
public class Configuration extends LabConfiguration {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = Configuration.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Sources_arg = "Sources: %s";
    private static final String STRLOG_Absorbers_arg = "Absorbers: %s";
    //</editor-fold>

    /**
     *
     * @param filepath
     * @param filename
     * @throws Exception
     */
    public Configuration(String filepath, String filename) throws Exception {
        super(filepath, filename);

        final String methodName = "Configuration";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Load the configuration XML document from the string in the lab configuration
             */
            Document document = XmlUtilities.GetDocumentFromString(this.getXmlConfiguration());
            Node nodeConfiguration = XmlUtilities.GetRootNode(document, Consts.STRXML_Configuration);

            /*
             * Get all source names and their locations
             */
            Node node = XmlUtilities.GetChildNode(nodeConfiguration, Consts.STRXML_Sources);
            ArrayList nodeList = XmlUtilities.GetChildNodeList(node, Consts.STRXML_Source);
            String[] sourceNames = new String[nodeList.size()];
            for (int i = 0; i < nodeList.size(); i++) {
                /*
                 * Get the source name
                 */
                Node nodeSource = (Node) nodeList.get(i);
                sourceNames[i] = XmlUtilities.GetChildValue(nodeSource, Consts.STRXML_Name);
            }

            /*
             * Create a CSV string of absorbers
             */
            String csvSources = "";
            for (String sourceName : sourceNames) {
                csvSources += String.format("%s%s", (!csvSources.isEmpty()) ? Consts.STR_CsvSplitter : "", sourceName);
            }
            Logfile.Write(logLevel, String.format(STRLOG_Sources_arg, csvSources));

            /*
             * Get all absorber names and their locations
             */
            node = XmlUtilities.GetChildNode(nodeConfiguration, Consts.STRXML_Absorbers);
            nodeList = XmlUtilities.GetChildNodeList(node, Consts.STRXML_Absorber);
            String[] absorberNames = new String[nodeList.size()];
            for (int i = 0; i < nodeList.size(); i++) {
                /*
                 * Get the source name
                 */
                Node nodeAbsorber = (Node) nodeList.get(i);
                absorberNames[i] = XmlUtilities.GetChildValue(nodeAbsorber, Consts.STRXML_Name);
            }

            /*
             * Create a CSV string of absorbers
             */
            String csvAbsorbers = "";
            for (String absorberName : absorberNames) {
                csvAbsorbers += String.format("%s%s", (!csvAbsorbers.isEmpty()) ? Consts.STR_CsvSplitter : "", absorberName);
            }
            Logfile.Write(logLevel, String.format(STRLOG_Absorbers_arg, csvAbsorbers));

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
