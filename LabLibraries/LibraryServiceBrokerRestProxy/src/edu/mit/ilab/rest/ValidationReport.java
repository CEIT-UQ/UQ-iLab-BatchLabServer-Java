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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValidationReport", propOrder = {
    "accepted",
    "warningMessages",
    "errorMessage",
    "estRuntime"
})
public class ValidationReport {

    protected boolean accepted;
    protected ArrayOfString warningMessages;
    protected String errorMessage;
    protected double estRuntime;

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean value) {
        this.accepted = value;
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

    public double getEstRuntime() {
        return estRuntime;
    }

    public void setEstRuntime(double value) {
        this.estRuntime = value;
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
            JAXBElement<ValidationReport> jaxbElement = (new ObjectFactory()).createValidationReport(this);
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
     * @return ValidationReport
     */
    public static ValidationReport XmlParse(String xmlString) {
        ValidationReport validationReport = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(ValidationReport.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<ValidationReport> jaxbElement = (JAXBElement<ValidationReport>) unmarshaller.unmarshal(streamSource, ValidationReport.class);
            validationReport = jaxbElement.getValue();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return validationReport;
    }
}
