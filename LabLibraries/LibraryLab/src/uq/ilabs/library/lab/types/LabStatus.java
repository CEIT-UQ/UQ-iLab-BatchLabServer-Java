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
@XmlType(name = "LabStatus", propOrder = {
    "online",
    "labStatusMessage"
})
public class LabStatus {

    @XmlElement(name = "online")
    protected boolean online;
    @XmlElement(name = "labStatusMessage")
    protected String labStatusMessage;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getLabStatusMessage() {
        return labStatusMessage;
    }

    public void setLabStatusMessage(String labStatusMessage) {
        this.labStatusMessage = labStatusMessage;
    }

    public LabStatus() {
        this.online = false;
        this.labStatusMessage = null;
    }

    public LabStatus(boolean online, String message) {
        this.online = online;
        this.labStatusMessage = message;
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
            JAXBElement<LabStatus> jaxbElement = (new ObjectFactory()).createLabStatus(this);
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
     * @return LabStatus
     */
    public static LabStatus XmlParse(String xmlString) {
        LabStatus labStatus = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(LabStatus.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<LabStatus> jaxbElement = (JAXBElement<LabStatus>) unmarshaller.unmarshal(streamSource, LabStatus.class);
            labStatus = jaxbElement.getValue();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return labStatus;
    }
}
