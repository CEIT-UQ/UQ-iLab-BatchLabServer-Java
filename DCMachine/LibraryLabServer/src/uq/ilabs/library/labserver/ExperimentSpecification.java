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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author uqlpayne
 */
@XmlRootElement(name = "experimentSpecification")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "setupName",
    "setupId"
})
public class ExperimentSpecification {

    protected static final String QNAME_ExperimentSpecification = "experimentSpecification";
    //
    protected String setupName;
    protected String setupId;

    public String getSetupName() {
        return setupName;
    }

    public void setSetupName(String setupName) {
        this.setupName = setupName;
    }

    public String getSetupId() {
        return setupId;
    }

    public void setSetupId(String setupId) {
        this.setupId = setupId;
    }

    /**
     *
     * @param xmlString
     * @return
     */
    public static ExperimentSpecification ToObject(String xmlString) {
        ExperimentSpecification object = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(ExperimentSpecification.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<ExperimentSpecification> jaxbElement = (JAXBElement<ExperimentSpecification>) unmarshaller.unmarshal(streamSource, ExperimentSpecification.class);
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
    public String ToXmlString() {
        String xmlString = null;

        try {
            Marshaller marshaller = JAXBContext.newInstance(this.getClass()).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            JAXBElement<ExperimentSpecification> jaxbElement =
                    new JAXBElement<>(new QName(QNAME_ExperimentSpecification), ExperimentSpecification.class, null, this);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(jaxbElement, stringWriter);
            xmlString = stringWriter.toString();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return xmlString;
    }
}
