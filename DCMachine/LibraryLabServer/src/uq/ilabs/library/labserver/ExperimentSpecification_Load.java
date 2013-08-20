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
    "loadMin",
    "loadMax",
    "loadStep"
})
public class ExperimentSpecification_Load extends ExperimentSpecification {

    private int loadMin;
    private int loadMax;
    private int loadStep;

    public int getLoadMin() {
        return loadMin;
    }

    public void setLoadMin(int loadMin) {
        this.loadMin = loadMin;
    }

    public int getLoadMax() {
        return loadMax;
    }

    public void setLoadMax(int loadMax) {
        this.loadMax = loadMax;
    }

    public int getLoadStep() {
        return loadStep;
    }

    public void setLoadStep(int loadStep) {
        this.loadStep = loadStep;
    }

    public ExperimentSpecification_Load() {
    }

    public ExperimentSpecification_Load(int loadMin, int loadMax, int loadStep) {
        this.loadMin = loadMin;
        this.loadMax = loadMax;
        this.loadStep = loadStep;
    }

    /**
     *
     * @param xmlString
     * @return
     */
    public static ExperimentSpecification_Load ToObject(String xmlString) {
        ExperimentSpecification_Load object = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(ExperimentSpecification_Load.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<ExperimentSpecification_Load> jaxbElement = (JAXBElement<ExperimentSpecification_Load>) unmarshaller.unmarshal(streamSource, ExperimentSpecification_Load.class);
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
            JAXBElement<ExperimentSpecification_Load> jaxbElement =
                    new JAXBElement<>(new QName(QNAME_ExperimentSpecification), ExperimentSpecification_Load.class, null, this);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(jaxbElement, stringWriter);
            xmlString = stringWriter.toString();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return xmlString;
    }
}
