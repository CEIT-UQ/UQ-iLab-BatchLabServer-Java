/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.lab.types;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author uqlpayne
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Validation", propOrder = {
    "accepted",
    "executionTime",
    "errorMessage"
})
public class Validation {

    @XmlElement(name = "Accepted")
    protected boolean accepted;
    @XmlElement(name = "ExecutionTime")
    protected int executionTime;
    @XmlElement(name = "ErrorMessage")
    protected String errorMessage;

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    public Validation() {
    }

    public Validation(boolean accepted, int executionTime) {
        this.accepted = accepted;
        this.executionTime = executionTime;
    }

    public Validation(String errorMessage) {
        this.accepted = false;
        this.errorMessage = errorMessage;
        this.executionTime = -1;
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
            JAXBElement<Validation> jaxbElement = (new ObjectFactory()).createValidation(this);
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
     * @return Validation
     */
    public static Validation XmlParse(String xmlString) {
        Validation validation = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(Validation.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<Validation> jaxbElement = (JAXBElement<Validation>) unmarshaller.unmarshal(streamSource, Validation.class);
            validation = jaxbElement.getValue();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return validation;
    }
}
