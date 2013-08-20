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
@XmlType(name = "LabEquipmentStatus", propOrder = {
    "online",
    "statusMessage"
})
public class LabEquipmentStatus {

    @XmlElement(name = "Online")
    protected boolean online;
    @XmlElement(name = "StatusMessage")
    protected String statusMessage;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public LabEquipmentStatus() {
    }

    public LabEquipmentStatus(boolean online, String statusMessage) {
        this.online = online;
        this.statusMessage = statusMessage;
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
            JAXBElement<LabEquipmentStatus> jaxbElement = (new ObjectFactory()).createLabEquipmentStatus(this);
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
    public static LabEquipmentStatus XmlParse(String xmlString) {
        LabEquipmentStatus labEquipmentStatus = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(LabEquipmentStatus.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<LabEquipmentStatus> jaxbElement = (JAXBElement<LabEquipmentStatus>) unmarshaller.unmarshal(streamSource, LabEquipmentStatus.class);
            labEquipmentStatus = jaxbElement.getValue();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return labEquipmentStatus;
    }
}
