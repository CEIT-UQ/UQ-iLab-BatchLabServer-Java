/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.labserver.engine.LabExperimentResult;

/**
 *
 * @author uqlpayne
 */
public class ExperimentResult extends LabExperimentResult {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ExperimentResult.class.getName();
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
        XmlUtilities.GetChildValue(this.nodeExperimentResult, Consts.STRXML_SomeResult);

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
                int someParameter = XmlUtilities.GetChildValueAsInt(nodeRoot, Consts.STRXML_SomeParameter);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_SomeParameter, someParameter);

                /*
                 * Add the experiment result information to the XML document
                 */
                int someResult = XmlUtilities.GetChildValueAsInt(nodeRoot, Consts.STRXML_SomeResult);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_SomeResult, someResult);

                /*
                 * Convert the XML document to an XML string
                 */
                xmlString = XmlUtilities.ToXmlString(this.nodeExperimentResult);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        return xmlString;
    }
}
