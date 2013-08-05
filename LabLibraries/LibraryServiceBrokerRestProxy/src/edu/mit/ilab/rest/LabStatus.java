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
@XmlType(name = "LabStatus", propOrder = {
    "online",
    "labStatusMessage"
})
public class LabStatus {

    protected boolean online;
    protected String labStatusMessage;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean value) {
        this.online = value;
    }

    public String getLabStatusMessage() {
        return labStatusMessage;
    }

    public void setLabStatusMessage(String value) {
        this.labStatusMessage = value;
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
