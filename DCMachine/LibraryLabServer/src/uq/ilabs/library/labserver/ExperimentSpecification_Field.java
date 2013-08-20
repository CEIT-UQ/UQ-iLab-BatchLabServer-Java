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
    "fieldMin",
    "fieldMax",
    "fieldStep"
})
public class ExperimentSpecification_Field extends ExperimentSpecification {

    private int fieldMin;
    private int fieldMax;
    private int fieldStep;

    public int getFieldMin() {
        return fieldMin;
    }

    public void setFieldMin(int fieldMin) {
        this.fieldMin = fieldMin;
    }

    public int getFieldMax() {
        return fieldMax;
    }

    public void setFieldMax(int fieldMax) {
        this.fieldMax = fieldMax;
    }

    public int getFieldStep() {
        return fieldStep;
    }

    public void setFieldStep(int fieldStep) {
        this.fieldStep = fieldStep;
    }

    public ExperimentSpecification_Field() {
    }

    public ExperimentSpecification_Field(int fieldMin, int fieldMax, int fieldStep) {
        this.fieldMin = fieldMin;
        this.fieldMax = fieldMax;
        this.fieldStep = fieldStep;
    }

    /**
     *
     * @param xmlString
     * @return
     */
    public static ExperimentSpecification_Field ToObject(String xmlString) {
        ExperimentSpecification_Field object = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(ExperimentSpecification_Field.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<ExperimentSpecification_Field> jaxbElement = (JAXBElement<ExperimentSpecification_Field>) unmarshaller.unmarshal(streamSource, ExperimentSpecification_Field.class);
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
            JAXBElement<ExperimentSpecification_Field> jaxbElement =
                    new JAXBElement<>(new QName(QNAME_ExperimentSpecification), ExperimentSpecification_Field.class, null, this);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(jaxbElement, stringWriter);
            xmlString = stringWriter.toString();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return xmlString;
    }
}
