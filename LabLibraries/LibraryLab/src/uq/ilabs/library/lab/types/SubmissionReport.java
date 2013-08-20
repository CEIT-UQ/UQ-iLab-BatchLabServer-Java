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
@XmlType(name = "SubmissionReport", propOrder = {
    "validationReport",
    "experimentId",
    "minTimeToLive",
    "waitEstimate"
})
public class SubmissionReport {

    @XmlElement(name = "vReport")
    protected ValidationReport validationReport;
    @XmlElement(name = "experimentID")
    protected int experimentId;
    @XmlElement(name = "minTimeToLive")
    protected double minTimeToLive;
    @XmlElement(name = "wait")
    protected WaitEstimate waitEstimate;

    public ValidationReport getValidationReport() {
        return validationReport;
    }

    public void setValidationReport(ValidationReport validationReport) {
        this.validationReport = validationReport;
    }

    public int getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    public double getMinTimeToLive() {
        return minTimeToLive;
    }

    public void setMinTimeToLive(double minTimeToLive) {
        this.minTimeToLive = minTimeToLive;
    }

    public WaitEstimate getWaitEstimate() {
        return waitEstimate;
    }

    public void setWaitEstimate(WaitEstimate waitEstimate) {
        this.waitEstimate = waitEstimate;
    }

    public SubmissionReport() {
        this.validationReport = new ValidationReport();
        this.experimentId = -1;
        this.minTimeToLive = 0.0;
        this.waitEstimate = new WaitEstimate();
    }

    public SubmissionReport(int experimentId) {
        this.validationReport = new ValidationReport();
        this.experimentId = experimentId;
        this.minTimeToLive = 0.0;
        this.waitEstimate = new WaitEstimate();
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
            JAXBElement<SubmissionReport> jaxbElement = (new ObjectFactory()).createSubmissionReport(this);
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
     * @return ClientSubmissionReport
     */
    public static SubmissionReport XmlParse(String xmlString) {
        SubmissionReport submissionReport = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(SubmissionReport.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<SubmissionReport> jaxbElement = (JAXBElement<SubmissionReport>) unmarshaller.unmarshal(streamSource, SubmissionReport.class);
            submissionReport = jaxbElement.getValue();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return submissionReport;
    }
}
