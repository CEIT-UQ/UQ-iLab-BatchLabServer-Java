/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.util.logging.Level;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
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
            /* Nothing to do here */
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
                Document ownerDocument = this.nodeExperimentResult.getOwnerDocument();
                /* Nothing to do here */

                /*
                 * Add the experiment result information to the XML document
                 */
                switch (this.setupId) {
                    case Consts.STRXML_SetupId_OpenCircuitVaryField:
                        Node node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_FieldCurrent);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_Speed);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_Voltage);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        break;

                    case Consts.STRXML_SetupId_OpenCircuitVarySpeed:
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_Speed);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_FieldCurrent);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_Voltage);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        break;

                    case Consts.STRXML_SetupId_ShortCircuitVaryField:
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_FieldCurrent);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_Speed);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_StatorCurrent);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        break;

                    case Consts.STRXML_SetupId_PreSynchronisation:
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_FieldCurrent);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_SpeedSetpoint);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_MainsVoltage);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_MainsFrequency);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_SyncVoltage);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_SyncFrequency);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_SyncMainsPhase);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_Synchronism);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        break;

                    case Consts.STRXML_SetupId_Synchronisation:
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_TorqueSetpoint);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_FieldCurrent);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_SyncVoltage);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_SyncFrequency);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_PowerFactor);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_RealPower);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_ReactivePower);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        node = XmlUtilities.GetChildNode(nodeRoot, Consts.STRXML_PhaseCurrent);
                        this.nodeExperimentResult.appendChild(ownerDocument.importNode(node, true));
                        break;
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
