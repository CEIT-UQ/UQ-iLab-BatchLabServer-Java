/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.util.ArrayList;
import java.util.HashMap;
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
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    HashMap<String, Character> sourceNameLocationMap;
    HashMap<String, Character> absorberNameLocationMap;

    public HashMap<String, Character> getSourceNameLocationMap() {
        return sourceNameLocationMap;
    }

    public HashMap<String, Character> getAbsorberNameLocationMap() {
        return absorberNameLocationMap;
    }
    //</editor-fold>

    /**
     *
     * @param xmlLabConfiguration
     * @throws Exception
     */
    public Configuration(String xmlLabConfiguration) throws Exception {
        this(null, null, xmlLabConfiguration);
    }

    /**
     *
     * @param filepath
     * @param filename
     * @throws Exception
     */
    public Configuration(String filepath, String filename) throws Exception {
        this(filepath, filename, null);
    }

    /**
     *
     * @param filepath
     * @param filename
     * @param xmlLabConfiguration
     * @throws Exception
     */
    private Configuration(String filepath, String filename, String xmlLabConfiguration) throws Exception {
        super(filepath, filename, xmlLabConfiguration);

        final String methodName = "Configuration";
        Logfile.WriteCalled(STR_ClassName, methodName);

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
            this.sourceNameLocationMap = new HashMap<>();
            for (int i = 0; i < nodeList.size(); i++) {
                /*
                 * Get the source name
                 */
                Node nodeSource = (Node) nodeList.get(i);
                String name = XmlUtilities.GetChildValue(nodeSource, Consts.STRXML_Name);
                String location = XmlUtilities.GetChildValue(nodeSource, Consts.STRXML_Location);
                this.sourceNameLocationMap.put(name, location.charAt(0));
            }

            /*
             * Get all absorber names and their locations
             */
            node = XmlUtilities.GetChildNode(nodeConfiguration, Consts.STRXML_Absorbers);
            nodeList = XmlUtilities.GetChildNodeList(node, Consts.STRXML_Absorber);
            this.absorberNameLocationMap = new HashMap<>();
            for (int i = 0; i < nodeList.size(); i++) {
                /*
                 * Get the source name
                 */
                Node nodeSource = (Node) nodeList.get(i);
                String name = XmlUtilities.GetChildValue(nodeSource, Consts.STRXML_Name);
                String location = XmlUtilities.GetChildValue(nodeSource, Consts.STRXML_Location);
                this.absorberNameLocationMap.put(name, location.charAt(0));
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }
}
