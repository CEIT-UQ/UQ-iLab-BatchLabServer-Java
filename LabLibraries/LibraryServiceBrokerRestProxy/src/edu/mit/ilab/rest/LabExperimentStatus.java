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
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "LabExperimentStatus", propOrder = {
    "statusReport",
    "minTimetoLive"
})
public class LabExperimentStatus {

    protected ExperimentStatus statusReport;
    protected double minTimetoLive;

    public ExperimentStatus getStatusReport() {
        return statusReport;
    }

    public void setStatusReport(ExperimentStatus value) {
        this.statusReport = value;
    }

    public double getMinTimetoLive() {
        return minTimetoLive;
    }

    public void setMinTimetoLive(double value) {
        this.minTimetoLive = value;
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
