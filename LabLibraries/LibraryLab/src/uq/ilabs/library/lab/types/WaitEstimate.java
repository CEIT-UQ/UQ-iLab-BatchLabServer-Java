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
@XmlType(name = "WaitEstimate", propOrder = {
    "effectiveQueueLength",
    "estWait"
})
public class WaitEstimate {

    @XmlElement(name = "effectiveQueueLength")
    protected int effectiveQueueLength;
    @XmlElement(name = "estWait")
    protected double estWait;

    public int getEffectiveQueueLength() {
        return effectiveQueueLength;
    }

    public void setEffectiveQueueLength(int effectiveQueueLength) {
        this.effectiveQueueLength = effectiveQueueLength;
    }

    public double getEstWait() {
        return estWait;
    }

    public void setEstWait(double estWait) {
        this.estWait = estWait;
    }

    public WaitEstimate() {
        this.effectiveQueueLength = 0;
        this.estWait = 0;
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
            JAXBElement<WaitEstimate> jaxbElement = (new ObjectFactory()).createWaitEstimate(this);
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
     * @return WaitEstimate
     */
    public static WaitEstimate XmlParse(String xmlString) {
        WaitEstimate waitEstimate = null;

        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(WaitEstimate.class).createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xmlString));
            JAXBElement<WaitEstimate> jaxbElement = (JAXBElement<WaitEstimate>) unmarshaller.unmarshal(streamSource, WaitEstimate.class);
            waitEstimate = jaxbElement.getValue();
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }

        return waitEstimate;
    }
}
