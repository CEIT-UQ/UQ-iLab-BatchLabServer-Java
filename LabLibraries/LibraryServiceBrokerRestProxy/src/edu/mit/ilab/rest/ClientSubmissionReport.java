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
@XmlType(name = "ClientSubmissionReport", propOrder = {
    "vReport",
    "experimentID",
    "minTimeToLive",
    "wait"
})
public class ClientSubmissionReport {

    protected ValidationReport vReport;
    protected int experimentID;
    protected double minTimeToLive;
    protected WaitEstimate wait;

    public ValidationReport getVReport() {
        return vReport;
    }

    public void setVReport(ValidationReport value) {
        this.vReport = value;
    }

    public int getExperimentID() {
        return experimentID;
    }

    public void setExperimentID(int value) {
        this.experimentID = value;
    }

    public double getMinTimeToLive() {
        return minTimeToLive;
    }

    public void setMinTimeToLive(double value) {
        this.minTimeToLive = value;
    }

    public WaitEstimate getWait() {
        return wait;
    }

    public void setWait(WaitEstimate value) {
        this.wait = value;
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
