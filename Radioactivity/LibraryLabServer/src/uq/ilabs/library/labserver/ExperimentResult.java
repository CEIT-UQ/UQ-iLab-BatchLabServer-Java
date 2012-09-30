/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;
import uq.ilabs.library.labserver.engine.LabExperimentResult;

/**
 *
 * @author uqlpayne
 */
public class ExperimentResult extends LabExperimentResult {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentResult.class.getName();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Types">

    public enum DataTypes {

        Unknown, Real, Simulated, Calculated
    };
    //</editor-fold>

    /**
     *
     * @param configuration
     * @throws Exception
     */
    public ExperimentResult(Configuration configuration) throws Exception {
        super(configuration);

        final String methodName = "ExperimentResult";
        Logfile.WriteCalled(STR_ClassName, methodName);

        /*
         * Check that all required XML nodes exist
         */
        XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_DataType);
        XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_DataVector);

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }

    /**
     *
     * @return @throws XmlUtilitiesException
     */
    @Override
    protected String ToXmlString() {
        String xmlString = null;

        try {
            /*
             * Call super to create the XML document and add its part
             */
            if (super.ToXmlString() != null) {
                /*
                 * Get the experiment results
                 */
                Document document = XmlUtilities.GetDocumentFromString(this.resultReport.getXmlExperimentResults());
                Node nodeRoot = XmlUtilities.GetRootNode(document, Consts.STRXML_ExperimentResults);

                /*
                 * Add the experiment specification information to the XML document
                 */
                String source = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_SourceName);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_SourceName, source);
                String absorber = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_AbsorberName);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_AbsorberName, absorber);
                String distances = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_Distance);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_Distance, distances);
                int duration = XmlUtilities.GetChildValueAsInt(nodeRoot, Consts.STRXML_Duration);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_Duration, duration);
                int trials = XmlUtilities.GetChildValueAsInt(nodeRoot, Consts.STRXML_Repeat);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_Repeat, trials);

                /*
                 * Add the experiment result information to the XML document
                 */
                String dataType = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_DataType);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_DataType, dataType);

                /*
                 * Get the XML data vector node and clone it
                 */
                Node nodeDataVector = XmlUtilities.GetChildNode(this.nodeExperimentResult, Consts.STRXML_DataVector);
                Node nodeClone = nodeDataVector.cloneNode(true);

                /*
                 * Process data vectors in the experiment results
                 */
                ArrayList<Node> nodeList = XmlUtilities.GetChildNodeList(nodeRoot, Consts.STRXML_DataVector);
                for (int i = 0; i < nodeList.size(); i++) {
                    Node nodeTemp = nodeList.get(i);

                    /*
                     * Copy the data vector's distance attribute and value
                     */
                    String attribute = XmlUtilities.GetAttribute(nodeTemp, Consts.STRXML_ATTR_Distance, false);
                    XmlUtilities.SetAttribute(nodeDataVector, Consts.STRXML_ATTR_Distance, attribute);
                    String value = XmlUtilities.GetValue(nodeTemp);
                    XmlUtilities.SetValue(nodeDataVector, value);

                    /*
                     * Add a data vector if there are more distances to process
                     */
                    if (i < nodeList.size() - 1) {
                        nodeDataVector = nodeClone.cloneNode(true);
                        this.nodeExperimentResult.appendChild(nodeDataVector);
                    }
                }

                /*
                 * Convert the XML document to an XML string
                 */
                xmlString = XmlUtilities.ToXmlString(this.nodeExperimentResult);
            }
        } catch (XmlUtilitiesException | DOMException ex) {
            Logfile.WriteError(ex.toString());
        }

        return xmlString;
    }
}
