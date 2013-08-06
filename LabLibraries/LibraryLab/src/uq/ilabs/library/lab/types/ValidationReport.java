package uq.ilabs.library.lab.types;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
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
@XmlType(name = "ValidationReport", propOrder = {
    "accepted",
    "warningMessages",
    "errorMessage",
    "estRuntime"
})
public class ValidationReport {

    @XmlElement(name = "accepted")
    protected boolean accepted;
    @XmlElement(name = "warningMessages")
    protected ArrayOfString warningMessages;
    @XmlElement(name = "errorMessage")
    protected String errorMessage;
    @XmlElement(name = "estRuntime")
    protected double estRuntime;

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String[] getWarningMessages() {
        return (warningMessages != null) ? warningMessages.getString().toArray(new String[0]) : null;
    }

    public void setWarningMessages(String[] warningMessages) {
        if (warningMessages != null) {
            this.warningMessages = new ArrayOfString();
            this.warningMessages.getString().addAll(Arrays.asList(warningMessages));
        } else {
            this.warningMessages = null;
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public double getEstRuntime() {
        return estRuntime;
    }

    public void setEstRuntime(double estRuntime) {
        this.estRuntime = estRuntime;
    }

    public ValidationReport() {
        this.accepted = false;
    }

    public ValidationReport(boolean accepted, double estRuntime) {
        this.accepted = accepted;
        this.estRuntime = estRuntime;
    }

    public ValidationReport(String errorMessage) {
        this.accepted = false;
        this.estRuntime = -1.0;
        this.errorMessage = errorMessage;
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
