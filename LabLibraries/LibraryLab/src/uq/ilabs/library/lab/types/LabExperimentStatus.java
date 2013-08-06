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
@XmlType(name = "LabExperimentStatus", propOrder = {
    "experimentStatus",
    "minTimetoLive"
})
public class LabExperimentStatus {

    @XmlElement(name = "statusReport")
    protected ExperimentStatus experimentStatus;
    @XmlElement(name = "minTimetoLive")
    protected double minTimetoLive;

    public ExperimentStatus getExperimentStatus() {
        return experimentStatus;
    }

    public void setExperimentStatus(ExperimentStatus experimentStatus) {
        this.experimentStatus = experimentStatus;
    }

    public double getMinTimetoLive() {
        return minTimetoLive;
    }

    public void setMinTimetoLive(double minTimetoLive) {
        this.minTimetoLive = minTimetoLive;
    }

    public LabExperimentStatus() {
        this.experimentStatus = new ExperimentStatus();
        this.minTimetoLive = 0.0;
    }

    public LabExperimentStatus(ExperimentStatus experimentStatus) {
        this.experimentStatus = experimentStatus;
        this.minTimetoLive = 0.0;
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
            JAXBElement<LabExperimentStatus> jaxbElement = (new ObjectFactory()).createLabExperimentStatus(this);
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
     * @return LabExperimentStatus
     */
    public static LabExperimentStatus XmlParse(String xmlString) {
        LabExperimentStatus labExperimentStatus = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(LabExperimentStatus.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<LabExperimentStatus> jaxbElement = (JAXBElement<LabExperimentStatus>) unmarshaller.unmarshal(streamSource, LabExperimentStatus.class);
            labExperimentStatus = jaxbElement.getValue();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return labExperimentStatus;
    }
}
