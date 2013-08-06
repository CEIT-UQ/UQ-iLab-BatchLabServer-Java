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
@XmlType(name = "ClientSubmissionReport", propOrder = {
    "validationReport",
    "experimentId",
    "minTimeToLive",
    "waitEstimate"
})
public class ClientSubmissionReport {

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

    public ClientSubmissionReport() {
        this.validationReport = new ValidationReport();
        this.experimentId = -1;
        this.minTimeToLive = 0.0;
        this.waitEstimate = new WaitEstimate();
    }

    public ClientSubmissionReport(int experimentId) {
        this.validationReport = new ValidationReport();
        this.experimentId = experimentId;
        this.minTimeToLive = 0.0;
        this.waitEstimate = new WaitEstimate();
    }

    /**
     *
     * @param submissionReport
     */
    public ClientSubmissionReport(SubmissionReport submissionReport) {
        if (submissionReport != null) {
            this.experimentId = (int) submissionReport.getExperimentId();
            this.minTimeToLive = submissionReport.getMinTimeToLive();
            if (submissionReport.getValidationReport() != null) {
                this.validationReport = new ValidationReport();
                this.validationReport.setAccepted(submissionReport.getValidationReport().isAccepted());
                this.validationReport.setErrorMessage(submissionReport.getValidationReport().getErrorMessage());
                this.validationReport.setEstRuntime(submissionReport.getValidationReport().getEstRuntime());
                this.validationReport.setWarningMessages(submissionReport.getValidationReport().getWarningMessages());
            }
            if (submissionReport.getWaitEstimate() != null) {
                this.waitEstimate = new WaitEstimate();
                this.waitEstimate.setEffectiveQueueLength(submissionReport.getWaitEstimate().getEffectiveQueueLength());
                this.waitEstimate.setEstWait(submissionReport.getWaitEstimate().getEstWait());
            }
        }
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
            JAXBElement<ClientSubmissionReport> jaxbElement = (new ObjectFactory()).createClientSubmissionReport(this);
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
    public static ClientSubmissionReport XmlParse(String xmlString) {
        ClientSubmissionReport clientSubmissionReport = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(ClientSubmissionReport.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<ClientSubmissionReport> jaxbElement = (JAXBElement<ClientSubmissionReport>) unmarshaller.unmarshal(streamSource, ClientSubmissionReport.class);
            clientSubmissionReport = jaxbElement.getValue();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return clientSubmissionReport;
    }
}
