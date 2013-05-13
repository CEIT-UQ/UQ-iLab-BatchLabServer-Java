/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.database.types;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uq.ilabs.library.lab.utilities.XmlUtilities;
import uq.ilabs.library.lab.utilities.XmlUtilitiesException;

/**
 *
 * @author uqlpayne
 */
public class WarningMessages {

    /*
     * XML warning messages template
     */
    private static final String STRXML_WarningMessages = "warningMessages";
    private static final String STRXML_WarningMessage = "warningMessage";
    private static final String STRXML_Template =
            "<" + STRXML_WarningMessages + ">"
            + "<" + STRXML_WarningMessage + " />"
            + "</" + STRXML_WarningMessages + ">";
    private String[] messages;

    public String[] getMessages() {
        return messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    public WarningMessages() {
    }

    public WarningMessages(String[] messages) {
        this.messages = messages;
    }

    /**
     *
     * @return
     */
    public String ToXmlString() {
        String xmlString = null;

        try {
            if (this.messages == null || this.messages.length == 0) {
                throw new RuntimeException();
            }

            /*
             * Load the template XML document from the string
             */
            Document document = XmlUtilities.GetDocumentFromString(STRXML_Template);
            Node nodeRoot = XmlUtilities.GetRootNode(document, STRXML_WarningMessages);

            /*
             * Get the XML warning message node and clone it
             */
            Node nodeMessage = XmlUtilities.GetChildNode(nodeRoot, STRXML_WarningMessage);
            Node nodeClone = nodeMessage.cloneNode(true);

            for (int i = 0; i < this.messages.length; i++) {
                XmlUtilities.SetValue(nodeMessage, this.messages[i]);

                /*
                 * Add a node if there are more messages to process
                 */
                if (i < this.messages.length - 1) {
                    nodeMessage = nodeClone.cloneNode(true);
                    nodeRoot.appendChild(nodeMessage);
                }
            }

            xmlString = XmlUtilities.ToXmlString(document);

        } catch (RuntimeException | XmlUtilitiesException ex) {
        }

        return xmlString;
    }

    /**
     * 
     * @param xmlString
     * @return 
     */
    public static String[] XmlParse(String xmlString) {
        String[] stringArray = null;

        try {
            Document xmlDocument = XmlUtilities.GetDocumentFromString(xmlString);
            Node xmlRootNode = XmlUtilities.GetRootNode(xmlDocument, STRXML_WarningMessages);
            stringArray = XmlUtilities.GetChildValues(xmlRootNode, STRXML_WarningMessage, false);
        } catch (Exception ex) {
        }

        return (stringArray != null && stringArray.length > 0) ? stringArray : null;
    }
}
