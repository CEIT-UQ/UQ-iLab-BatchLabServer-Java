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
@XmlType(name = "ResultReport", propOrder = {
    "statusCode",
    "xmlExperimentResults",
    "xmlResultExtension",
    "xmlBlobExtension",
    "warningMessages",
    "errorMessage"
})
public class ResultReport {

    @XmlElement(name = "statusCode")
    protected int statusCode;
    @XmlElement(name = "experimentResults")
    protected String xmlExperimentResults;
    @XmlElement(name = "xmlResultExtension")
    protected String xmlResultExtension;
    @XmlElement(name = "xmlBlobExtension")
    protected String xmlBlobExtension;
    @XmlElement(name = "warningMessages")
    protected ArrayOfString warningMessages;
    @XmlElement(name = "errorMessage")
    protected String errorMessage;

    public StatusCodes getStatusCode() {
        return StatusCodes.ToStatusCode(statusCode);
    }

    public void setStatusCode(StatusCodes statusCode) {
        this.statusCode = statusCode.getValue();
    }

    public String getXmlExperimentResults() {
        return xmlExperimentResults;
    }

    public void setXmlExperimentResults(String xmlExperimentResults) {
        this.xmlExperimentResults = xmlExperimentResults;
    }

    public String getXmlResultExtension() {
        return xmlResultExtension;
    }

    public void setXmlResultExtension(String xmlResultExtension) {
        this.xmlResultExtension = xmlResultExtension;
    }

    public String getXmlBlobExtension() {
        return xmlBlobExtension;
    }

    public void setXmlBlobExtension(String xmlBlobExtension) {
        this.xmlBlobExtension = xmlBlobExtension;
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

    public ResultReport() {
        this(StatusCodes.Unknown);
    }

    public ResultReport(StatusCodes statusCode) {
        this.statusCode = statusCode.getValue();
    }

    public ResultReport(StatusCodes statusCode, String errorMessage) {
        this.statusCode = statusCode.getValue();
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
