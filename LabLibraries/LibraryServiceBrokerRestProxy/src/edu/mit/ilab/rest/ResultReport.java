/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mit.ilab.rest;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author uqlpayne
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "ResultReport", propOrder = {
    "statusCode",
    "experimentResults",
    "xmlResultExtension",
    "xmlBlobExtension",
    "warningMessages",
    "errorMessage"
})
public class ResultReport {

    protected int statusCode;
    private String experimentResults;
    private String xmlResultExtension;
    private String xmlBlobExtension;
    private ArrayOfString warningMessages;
    private String errorMessage;

    /**
     * Gets the value of the statusCode property.
     *
     */
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int value) {
        this.statusCode = value;
    }

    public String getExperimentResults() {
        return experimentResults;
    }

    public void setExperimentResults(String value) {
        this.experimentResults = value;
    }

    public String getXmlResultExtension() {
        return xmlResultExtension;
    }

    public void setXmlResultExtension(String value) {
        this.xmlResultExtension = value;
    }

    public String getXmlBlobExtension() {
        return xmlBlobExtension;
    }

    public void setXmlBlobExtension(String value) {
        this.xmlBlobExtension = value;
    }

    public ArrayOfString getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(ArrayOfString value) {
        this.warningMessages = value;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

    /**
     *
     * @return String
     */
    public String ToXmlString() {
        String xmlString = null;

        try {
            Marshaller marshaller = JAXBContext.newInstance(this.getClass()).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            JAXBElement<ResultReport> jaxbElement = (new ObjectFactory()).createResultReport(this);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(jaxbElement, stringWriter);
            xmlString = stringWriter.toString();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return xmlString;
    }

    /**
     *
     * @param xmlString
     * @return ResultReport
     */
    public static ResultReport XmlParse(String xmlString) {
        ResultReport resultReport = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(ResultReport.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<ResultReport> jaxbElement = (JAXBElement<ResultReport>) unmarshaller.unmarshal(streamSource, ResultReport.class);
            resultReport = jaxbElement.getValue();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return resultReport;
    }
}
