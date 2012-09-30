package uq.ilabs.library.lab.utilities;

import java.io.*;
import java.util.ArrayList;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 *
 * @author payne
 */
public class XmlUtilities {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = XmlUtilities.class.getName();
    /*
     * String constants for exception messages
     */
    private static final String STRERR_DocumentIsNotSpecified = "Document is not specified!";
    private static final String STRERR_XmlStringIsNotSpecified = "XML string is not specified!";
    private static final String STRERR_XmlStringParsingFailed = "XML string parsing failed!";
    private static final String STRERR_DocumentCannotBeCreated = "Document cannot be created!";
    private static final String STRERR_FilenameIsNotSpecified = "Filename is not specified!";
    private static final String STRERR_FileDoesNotExist_Arg = "File does not exist: ";
    private static final String STRERR_RootNodeDoesNotExist_Arg = "Root node does not exist: ";
    private static final String STRERR_RootNodeIsNotUnique_Arg = "Root node is not unique: ";
    private static final String STRERR_NodeIsNull = "Node is null!";
    private static final String STRERR_NodeArrayListIsNull = "Node array list is null!";
    private static final String STRERR_NodeIsNotOfTypeElement = "Node is not of type Element";
    private static final String STRERR_NodeNameIsNotSpecified = "Node name is not specified!";
    private static final String STRERR_NodeDoesNotExist_Arg = "Node does not exist: ";
    private static final String STRERR_NodeIsNotUnique_Arg = "Node is not unique: ";
    private static final String STRERR_NodeListDoesNotExist_Arg = "Node list does not exist: ";
    private static final String STRERR_NodeValueDoesNotExist_Arg = "Node value does not exist: ";
    private static final String STRERR_NodeValueIsNotBoolean_Arg = "Node value is not a boolean: ";
    private static final String STRERR_NodeValueIsNotCharacter_Arg = "Node value is not a character: ";
    private static final String STRERR_AttributeNameIsNotSpecified = "Attribute name is not specified!";
    private static final String STRERR_AttributeDoesNotExistOrNoValue_Arg = "Attribute does not exist or has no value: ";
    //</editor-fold>

    /**
     * Parse the string as an XML document and return a new DOM Document object. An XmlUtilitiesException is thrown if
     * the string could not be parsed.
     *
     * @param xmlString The string containing the XML to parse.
     * @return A new DOM document object.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>xmlString</code> is null or an
     * empty string</li> <li>the string cannot be parsed</li> </ul>
     */
    public static Document GetDocumentFromString(String xmlString) throws XmlUtilitiesException {
        /*
         * Check that the XML string exists
         */
        if (xmlString == null || xmlString.trim().isEmpty()) {
            throw new XmlUtilitiesException(STRERR_XmlStringIsNotSpecified);
        }

        /*
         * Load the XML string into a document
         */
        Document document = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.parse(new InputSource(new StringReader(xmlString)));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new XmlUtilitiesException(STRERR_XmlStringParsingFailed, ex);
        }

        return document;
    }

    /**
     * Parse the content of the specified file as an XML document and return a new DOM document object.
     *
     * @param filepath the path to the file containing the XML to parse.
     * @param filename the name of the file containing the XML to parse.
     * @return A new DOM document object.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>filename</code> is null or an
     * empty string</li> <li>the file does not exist</li> <li>the file cannot be parsed</li> </ul>
     */
    public static Document GetDocumentFromFile(String filepath, String filename) throws XmlUtilitiesException {
        /*
         * Check that the filename has been specified
         */
        if (filename == null || filename.trim().isEmpty() == true) {
            throw new XmlUtilitiesException(STRERR_FilenameIsNotSpecified);
        }

        /*
         * Check if the file exists
         */
        File file = new File(filepath, filename);
        if (file.exists() == false) {
            throw new XmlUtilitiesException(STRERR_FileDoesNotExist_Arg + file.getAbsolutePath());
        }

        /*
         * Load the XML file into a document
         */
        Document document = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.parse(file);
//        } catch (FileNotFoundException ex) {
//            throw new XmlUtilitiesException(ex.getMessage());
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new XmlUtilitiesException(STRERR_XmlStringParsingFailed, ex);
        }

        return document;
    }

    /**
     * Obtain a new instance of a DOM Document object to build a DOM tree with.
     *
     * @return A new instance of a DOM Document object.
     * @throws XmlUtilitiesException If a new instance of a DOM Document object cannot be created.
     */
    public static Document GetNewDocument() throws XmlUtilitiesException {
        Document document = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.newDocument();
        } catch (Exception ex) {
            throw new XmlUtilitiesException(STRERR_DocumentCannotBeCreated, ex);
        }

        return document;
    }

    /**
     * Get the root node with the specified name from the DOM document object.
     *
     * @param document The DOM document object.
     * @param rootName The name of the root node to match.
     * @return The Node object containing the root node.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>document</code> is null</li>
     * <li><code>rootName</code> is null or an empty string</li> <li><code>rootName</code> does not exist</li> </ul>
     */
    public static Node GetRootNode(Document document, String rootName) throws XmlUtilitiesException {
        /*
         * Check that the document exists
         */
        if (document == null) {
            throw new XmlUtilitiesException(STRERR_DocumentIsNotSpecified);
        }

        /*
         * Check that the name of the root node has been specified
         */
        if (rootName == null || rootName.trim().isEmpty()) {
            throw new XmlUtilitiesException(STRERR_NodeNameIsNotSpecified);
        }

        /*
         * Get all nodes with the specified name and check that only one exists
         */
        NodeList nodeList = document.getElementsByTagName(rootName);
        if (nodeList == null || nodeList.getLength() == 0) {
            throw new XmlUtilitiesException(STRERR_RootNodeDoesNotExist_Arg + rootName);
        }

        return nodeList.item(0);
    }

    /**
     * Get the attribute with the specified name from the given node.
     *
     * @param node The node containing the attribute.
     * @param attributeName The name of the attribute.
     * @param mustExist If true, the attribute must exist.
     * @return The value of the attribute.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>node</code> is null</li>
     * <li><code>attributeName</code> is null or an empty string</li> <li>The attribute does not exist or does not have
     * a value and <code>mustExist</code> is true</li> </ul>
     */
    public static String GetAttribute(Node node, String attributeName, boolean mustExist) throws XmlUtilitiesException {
        /*
         * Check that the node exists
         */
        if (node == null) {
            throw new XmlUtilitiesException(STRERR_NodeIsNull);
        }

        /*
         * Check that the name of the attribute has been specified
         */
        if (attributeName == null || attributeName.trim().isEmpty()) {
            throw new XmlUtilitiesException(STRERR_AttributeNameIsNotSpecified);
        }

        /*
         * Get the attribute's value
         */
        String value = null;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            value = element.getAttribute(attributeName).trim();

            if (mustExist == true) {
                /*
                 * Check that the attribute has a value
                 */
                if (value == null || value.isEmpty()) {
                    throw new XmlUtilitiesException(STRERR_AttributeDoesNotExistOrNoValue_Arg + attributeName);
                }
            }
        }

        return value;
    }

    /**
     * Get the child node with the specified name from the given parent node.
     *
     * @param parentNode The parent node.
     * @param childName The name of the child node.
     * @param mustExist If true, the child node must exist.
     * @return The Node object containing the child node. If the child node does not exist and <code>mustExist</code> is
     * false then <code>null</code> is returned.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> <li>The child node does not exist and <code>mustExist</code> is true</li> <li>The child node is not
     * unique</li> </ul>
     */
    public static Node GetChildNode(Node parentNode, String childName, boolean mustExist) throws XmlUtilitiesException {
        /*
         * Check that the parent node exists
         */
        if (parentNode == null) {
            throw new XmlUtilitiesException(STRERR_NodeIsNull);
        }

        /*
         * Check that the parent node is an ELEMENT node
         */
        if (parentNode.getNodeType() != Node.ELEMENT_NODE) {
            throw new XmlUtilitiesException(STRERR_NodeIsNotOfTypeElement);
        }

        /*
         * Check that the name of the child node has been specified
         */
        if (childName == null || childName.trim().isEmpty()) {
            throw new XmlUtilitiesException(STRERR_NodeNameIsNotSpecified);
        }

        /*
         * Get all child nodes with the specified name
         */
        Element element = (Element) parentNode;
        NodeList nodeList = element.getElementsByTagName(childName);

        /*
         * Check if the child node exists
         */
        if (nodeList == null || nodeList.getLength() == 0) {
            if (mustExist == true) {
                throw new XmlUtilitiesException(STRERR_NodeDoesNotExist_Arg + childName);
            } else {
                return null;
            }
        }

        /*
         * Check if the child node is unique
         */
        if (nodeList.getLength() > 1) {
            throw new XmlUtilitiesException(STRERR_NodeIsNotUnique_Arg + childName);
        }

        return nodeList.item(0);
    }

    /**
     * Get the child node with the specified name from the given parent node.
     *
     * @param parentNode The parent node.
     * @param childName The name of the child node to match.
     * @return The Node object containing the child node.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> <li>The child node does not exist</li> <li>The child node is not unique</li> </ul>
     */
    public static Node GetChildNode(Node parentNode, String childName) throws XmlUtilitiesException {
        return GetChildNode(parentNode, childName, true);
    }

    /**
     * Get an ArrayList of DOM Document child nodes with the specified name from the given parent node.
     *
     * @param node The parent node.
     * @param childName The name of the child node to match.
     * @param mustExist At least one child node must exist.
     * @return ArrayList of Node objects containing the child nodes.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> <li>A child node does not exist and <code>mustExist</code> is true</li> </ul>
     */
    public static ArrayList<Node> GetChildNodeList(Node parentNode, String childName, boolean mustExist) throws XmlUtilitiesException {
        /*
         * Check that the parent node exists
         */
        if (parentNode == null) {
            throw new XmlUtilitiesException(STRERR_NodeIsNull);
        }

        /*
         * Check that the parent node is an ELEMENT node
         */
        if (parentNode.getNodeType() != Node.ELEMENT_NODE) {
            throw new XmlUtilitiesException(STRERR_NodeIsNotOfTypeElement);
        }

        /*
         * Check that the name of the child node has been specified
         */
        if (childName == null || childName.trim().isEmpty()) {
            throw new XmlUtilitiesException(STRERR_NodeNameIsNotSpecified);
        }

        /*
         * Get all child nodes with the specified name
         */
        Element element = (Element) parentNode;
        NodeList nodeList = element.getElementsByTagName(childName);

        /*
         * Check that at least one child node exists
         */
        if (mustExist == true && (nodeList == null || nodeList.getLength() == 0)) {
            throw new XmlUtilitiesException(STRERR_NodeListDoesNotExist_Arg + childName);
        }

        /*
         * Copy the child nodes to the array list
         */
        ArrayList<Node> arrayList = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            arrayList.add(nodeList.item(i));
        }

        return arrayList;
    }

    /**
     * Get an ArrayList of DOM Document child nodes with the specified name from the given parent node.
     *
     * @param node The parent node.
     * @param childName The name of the child node to match.
     * @param mustExist At least one child node must exist.
     * @return ArrayList of Node objects containing the child nodes.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> <li>A child node does not exist</li> </ul>
     */
    public static ArrayList<Node> GetChildNodeList(Node parentNode, String childName) throws XmlUtilitiesException {
        return GetChildNodeList(parentNode, childName, true);
    }

    /**
     *
     * @param node
     * @param allowEmpty
     * @return
     * @throws XmlUtilitiesException
     */
    public static String GetValue(Node node, boolean allowEmpty) throws XmlUtilitiesException {
        /*
         * Check that the node is an ELEMENT node
         */
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new XmlUtilitiesException(STRERR_NodeIsNotOfTypeElement);
        }

        /*
         * Search the child nodes for a TEXT_NODE and get its value
         */
        String value = "";
        NodeList childNodeList = node.getChildNodes();
        for (int i = 0; i < childNodeList.getLength(); i++) {
            Node childNode = childNodeList.item(i);
            if (childNode.getNodeType() == Node.TEXT_NODE) {
                value = childNode.getNodeValue().trim();
                break;
            }
        }

        /*
         * Check that the text node has a value
         */
        if (allowEmpty == false && (value == null || value.isEmpty())) {
            throw new XmlUtilitiesException(STRERR_NodeValueDoesNotExist_Arg);
        }

        return value;
    }

    /**
     *
     * @param node
     * @return
     * @throws XmlUtilitiesException
     */
    public static String GetValue(Node node) throws XmlUtilitiesException {
        return GetValue(node, true);
    }

    /**
     * Get the value of the child node with the specified name from the given parent node.
     *
     * @param parentNode The parent node.
     * @param childName The name of the child node to match.
     * @param allowEmpty If true, the child node can have no value.
     * @return The value of the child node as a string.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> <li>The child node does not exist</li> <li>The child node does not contain a value * * * *      * and <code>allowEmpty</code> is false</li> </ul>
     */
    public static String GetChildValue(Node parentNode, String childName, boolean allowEmpty) throws XmlUtilitiesException {
        /*
         * Get the child node with the specified name
         */
        Node node = GetChildNode(parentNode, childName);

        /*
         * Check that the node is an ELEMENT node
         */
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new XmlUtilitiesException(STRERR_NodeIsNotOfTypeElement);
        }

        /*
         * Search the child nodes for a TEXT_NODE and get its value
         */
        String value = "";
        NodeList childNodeList = node.getChildNodes();
        for (int i = 0; i < childNodeList.getLength(); i++) {
            Node childNode = childNodeList.item(i);
            if (childNode.getNodeType() == Node.TEXT_NODE) {
                value = childNode.getNodeValue().trim();
                break;
            }
        }

        /*
         * Check that the text node has a value
         */
        if (allowEmpty == false && (value == null || value.isEmpty())) {
            throw new XmlUtilitiesException(STRERR_NodeValueDoesNotExist_Arg + childName);
        }

        return value;
    }

    /**
     * Get the value of the child node with the specified name from the given parent node. The value may be an empty
     * string.
     *
     * @param parentNode The parent node.
     * @param childName The name of the child node.
     * @return The value of the child node as a string.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> <li>The child node does not exist</li> </ul>
     */
    public static String GetChildValue(Node parentNode, String childName) throws XmlUtilitiesException {
        return GetChildValue(parentNode, childName, true);
    }

    /**
     * Get the boolean value of the child node with the specified name from the given parent node.
     *
     * @param parentNode The parent node.
     * @param childName The name of the child node.
     * @return The value of the child node as a boolean.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> <li>The child node does not exist</li> <li>The child node does not contain a boolean value</li> </ul>
     */
    public static boolean GetChildValueAsBoolean(Node parentNode, String childName) throws XmlUtilitiesException {
        /*
         * Get the child node's value which must not be empty
         */
        String value = GetChildValue(parentNode, childName, false);

        /*
         * Convert the string to a boolean value
         */
        boolean retValue;
        if (value.equalsIgnoreCase("true") == true) {
            retValue = true;
        } else if (value.equalsIgnoreCase("false") == true) {
            retValue = false;
        } else {
            throw new XmlUtilitiesException(STRERR_NodeValueIsNotBoolean_Arg + value);
        }

        return retValue;
    }

    /**
     * Get the character value of the child node with the specified name from the given parent node.
     *
     * @param parentNode The parent node.
     * @param childName The name of the child node.
     * @return The value of the child node as a character.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> <li>The child node does not exist</li> <li>The child node does not contain a character value</li>
     * </ul>
     */
    public static char GetChildValueAsChar(Node parentNode, String childName) throws XmlUtilitiesException {
        /*
         * Get the child node's value which must not be empty
         */
        String value = GetChildValue(parentNode, childName, false);

        /*
         * Convert the string to a char value
         */
        char retValue;
        if (value.length() == 1) {
            retValue = value.toCharArray()[0];
        } else {
            throw new XmlUtilitiesException(STRERR_NodeValueIsNotCharacter_Arg + value);
        }

        return retValue;
    }

    /**
     * Get the integer value of the child node with the specified name from the given parent node.
     *
     * @param parentNode The parent node.
     * @param childName The name of the child node.
     * @return The value of the child node as an integer.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> <li>The child node does not exist</li> </ul>
     * @throws NumberFormatException <ul> <li>The child node does not contain an integer value</li> </ul>
     */
    public static int GetChildValueAsInt(Node parentNode, String childName) throws XmlUtilitiesException {
        /*
         * Get the child node's value which must not be empty
         */
        String value = GetChildValue(parentNode, childName, false);

        /*
         * Convert the string to an integer value
         */
        return Integer.parseInt(value);
    }

    /**
     * Get the double value of the child node with the specified name from the given parent node.
     *
     * @param parentNode The parent node.
     * @param childName The name of the child node.
     * @return The value of the child node as a double.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> <li>The child node does not exist</li> </ul>
     * @throws NumberFormatException <ul> <li>The child node does not contain a double value</li> </ul>
     */
    public static double GetChildValueAsDouble(Node parentNode, String childName) throws XmlUtilitiesException {
        /*
         * Get the child node's value which must not be empty
         */
        String value = GetChildValue(parentNode, childName, false);

        /*
         * Convert the string to a double value
         */
        return Double.parseDouble(value);
    }

    /**
     *
     * @param parentNode
     * @param childName
     * @param allowEmpty
     * @return
     * @throws XmlUtilitiesException
     */
    public static String[] GetChildValues(Node parentNode, String childNames, boolean allowEmpty) throws XmlUtilitiesException {
        /*
         * Get the child node array list with the specified name
         */
        ArrayList<Node> nodeArrayList = GetChildNodeList(parentNode, childNames);

        /*
         * Get the text value of each node in the array list and place in the string array
         */
        String[] values = new String[nodeArrayList.size()];
        for (int j = 0; j < values.length; j++) {
            Node node = nodeArrayList.get(j);

            /*
             * Check that the node is an ELEMENT node
             */
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                throw new XmlUtilitiesException(STRERR_NodeIsNotOfTypeElement);
            }

            /*
             * Search the child nodes for a TEXT_NODE and get its value
             */
            values[j] = "";
            NodeList childNodeList = node.getChildNodes();
            for (int i = 0; i < childNodeList.getLength(); i++) {
                Node childNode = childNodeList.item(i);
                if (childNode.getNodeType() == Node.TEXT_NODE) {
                    values[j] = childNode.getNodeValue().trim();
                    break;
                }
            }
        }

        return values;
    }

    /**
     * Set the value of the node's attribute with the specified name.
     *
     * @param node The node.
     * @param attributeName The name of the attribute.
     * @param value The string value for the attribute.
     * @throws XmlUtilitiesException
     */
    public static void SetAttribute(Node node, String attributeName, String value) throws XmlUtilitiesException {
        /*
         * Check that the node exists
         */
        if (node == null) {
            throw new XmlUtilitiesException(STRERR_NodeIsNull);
        }

        /*
         * Check that the name of the attribute has been specified
         */
        if (attributeName == null || attributeName.trim().isEmpty()) {
            throw new XmlUtilitiesException(STRERR_AttributeNameIsNotSpecified);
        }

        /*
         * Set the attribute's value
         */
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            element.setAttribute(attributeName, value);
        }
    }

    /**
     * Set the value of the child node with the specified name from the given parent node.
     *
     * @param parentNode The parent node.
     * @param childName The name of the child node.
     * @param value The string value for the child node.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> </ul>
     */
    public static void SetChildValue(Node parentNode, String childName, String value) throws XmlUtilitiesException {
        SetValue(GetChildNode(parentNode, childName), value);
    }

    /**
     * Set the value of the child node with the specified name from the given parent node.
     *
     * @param parentNode The parent node.
     * @param childName The name of the child node.
     * @param value The boolean value for the child node.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> </ul>
     */
    public static void SetChildValue(Node node, String childName, Boolean value) throws XmlUtilitiesException {
        SetChildValue(node, childName, value.toString());
    }

    /**
     * Set the value of the child node with the specified name from the given parent node.
     *
     * @param parentNode The parent node.
     * @param childName The name of the child node.
     * @param value The integer value for the child node.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> </ul>
     */
    public static void SetChildValue(Node node, String childName, Integer value) throws XmlUtilitiesException {
        SetChildValue(node, childName, value.toString());
    }

    /**
     * Set the value of the child node with the specified name from the given parent node.
     *
     * @param parentNode The parent node.
     * @param childName The name of the child node.
     * @param value The double value for the child node.
     * @throws XmlUtilitiesException If any of the following errors occur: <ul> <li><code>parentNode</code> is null</li>
     * <li><code>parentNode</code> is not an element node</li> <li><code>childName</code> is null or an empty
     * string</li> </ul>
     */
    public static void SetChildValue(Node node, String childName, Double value) throws XmlUtilitiesException {
        SetChildValue(node, childName, value.toString());
    }

    /**
     *
     * @param parentNode
     * @param childName
     * @param values
     * @throws XmlUtilitiesException
     */
    public static void SetChildValues(Node parentNode, String childNames, String[] values) throws XmlUtilitiesException {
        Document document = parentNode.getOwnerDocument();

        for (int i = 0; i < values.length; i++) {
            Element element = document.createElement(childNames);
            Text text = document.createTextNode(values[i]);
            element.appendChild(text);
            parentNode.appendChild(element);
        }

    }

    /**
     * Set the value of the node.
     *
     * @param node The node.
     * @param value The string value for the node.
     * @throws XmlUtilitiesException
     */
    public static void SetValue(Node node, String value) throws XmlUtilitiesException {
        /*
         * Check that the node exists
         */
        if (node == null) {
            throw new XmlUtilitiesException(STRERR_NodeIsNull);
        }

        /*
         * Check that the node is an ELEMENT node
         */
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new XmlUtilitiesException(STRERR_NodeIsNotOfTypeElement);
        }

        /*
         * Search the child nodes for a TEXT_NODE
         */
        Node subNode = null;
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            subNode = nodeList.item(i);
            if (subNode.getNodeType() == Node.TEXT_NODE) {
                break;
            }
        }

        /*
         * If a TEXT_NODE node was found, update its value otherwise add a new value
         */
        if (subNode != null) {
            subNode.setNodeValue(value);
        } else {
            Text textNode = node.getOwnerDocument().createTextNode(value);
            node.appendChild(textNode);
        }
    }

    /**
     * Take a DOM document object and convert it to a string in XML format.
     *
     * @param document The DOM document object.
     * @return A string representation of the document.
     * @throws XmlUtilitiesException
     */
    public static String ToXmlString(Document document) throws XmlUtilitiesException {
        return ToXmlString((Node) document);
    }

    public static String ToXmlString(DocumentFragment fragment) throws XmlUtilitiesException {
        return ToXmlString((Node) fragment);
    }

    public static String ToXmlString(Node node) {
        String xmlString = null;

        try {
            /*
             * Create a transformer to transform the XML document to a string
             */
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            /*
             * Use the transformer to read the XML document and write it to a string
             */
            DOMSource source = new DOMSource(node);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result);
            xmlString = result.getWriter().toString();
        } catch (TransformerFactoryConfigurationError | IllegalArgumentException | TransformerException ex) {
            Logfile.WriteError(ex.toString());
        }

        return xmlString;
    }
}
