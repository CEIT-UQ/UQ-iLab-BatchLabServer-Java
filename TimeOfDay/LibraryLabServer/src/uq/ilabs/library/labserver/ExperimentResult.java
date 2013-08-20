/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.util.logging.Level;
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
    private static final Level logLevel = Level.FINE;
    //</editor-fold>

    /**
     *
     * @param configuration
     * @throws Exception
     */
    public ExperimentResult(Configuration configuration) throws Exception {
        super(configuration);

        final String methodName = "ExperimentResult";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that all required XML nodes exist
             */
            XmlUtilities.GetChildNode(this.nodeExperimentResult, Consts.STRXML_Timeofday);
            XmlUtilities.GetChildNode(this.nodeExperimentResult, Consts.STRXML_Dayofweek);
            XmlUtilities.GetChildNode(this.nodeExperimentResult, Consts.STRXML_Day);
            XmlUtilities.GetChildNode(this.nodeExperimentResult, Consts.STRXML_Month);
            XmlUtilities.GetChildNode(this.nodeExperimentResult, Consts.STRXML_Year);
            XmlUtilities.GetChildNode(this.nodeExperimentResult, Consts.STRXML_Hours);
            XmlUtilities.GetChildNode(this.nodeExperimentResult, Consts.STRXML_Minutes);
            XmlUtilities.GetChildNode(this.nodeExperimentResult, Consts.STRXML_Seconds);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
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
                String value = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_FormatName);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_FormatName, value);
                switch (this.setupId) {
                    case Consts.STRXML_SetupId_NTPServer:
                        value = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_ServerUrl);
                        XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_ServerUrl, value);
                        break;
                }

                /*
                 * Add the experiment result information to the XML document
                 */
                value = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_Timeofday);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_Timeofday, value);
                value = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_Dayofweek);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_Dayofweek, value);
                value = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_Day);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_Day, value);
                value = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_Month);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_Month, value);
                value = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_Year);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_Year, value);
                value = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_Hours);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_Hours, value);
                value = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_Minutes);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_Minutes, value);
                value = XmlUtilities.GetChildValue(nodeRoot, Consts.STRXML_Seconds);
                XmlUtilities.SetChildValue(this.nodeExperimentResult, Consts.STRXML_Seconds, value);

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
