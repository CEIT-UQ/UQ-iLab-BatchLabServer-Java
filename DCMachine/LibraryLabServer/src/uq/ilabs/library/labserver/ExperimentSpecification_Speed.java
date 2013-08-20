/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author uqlpayne
 */
@XmlType(propOrder = {
    "speedMin",
    "speedMax",
    "speedStep"
})
public class ExperimentSpecification_Speed extends ExperimentSpecification {

    private int speedMin;
    private int speedMax;
    private int speedStep;

    public int getSpeedMin() {
        return speedMin;
    }

    public void setSpeedMin(int speedMin) {
        this.speedMin = speedMin;
    }

    public int getSpeedMax() {
        return speedMax;
    }

    public void setSpeedMax(int speedMax) {
        this.speedMax = speedMax;
    }

    public int getSpeedStep() {
        return speedStep;
    }

    public void setSpeedStep(int speedStep) {
        this.speedStep = speedStep;
    }

    public ExperimentSpecification_Speed() {
    }

    public ExperimentSpecification_Speed(int speedMin, int speedMax, int speedStep) {
        this.speedMin = speedMin;
        this.speedMax = speedMax;
        this.speedStep = speedStep;
    }

    /**
     *
     * @param xmlString
     * @return
     */
    public static ExperimentSpecification_Speed ToObject(String xmlString) {
        ExperimentSpecification_Speed object = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(ExperimentSpecification_Speed.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<ExperimentSpecification_Speed> jaxbElement = (JAXBElement<ExperimentSpecification_Speed>) unmarshaller.unmarshal(streamSource, ExperimentSpecification_Speed.class);
            object = jaxbElement.getValue();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return object;
    }

    /**
     *
     * @return String
     */
    @Override
    public String ToXmlString() {
        String xmlString = null;

        try {
            Marshaller marshaller = JAXBContext.newInstance(this.getClass()).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            JAXBElement<ExperimentSpecification_Speed> jaxbElement =
                    new JAXBElement<>(new QName(QNAME_ExperimentSpecification), ExperimentSpecification_Speed.class, null, this);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(jaxbElement, stringWriter);
            xmlString = stringWriter.toString();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return xmlString;
    }
}
