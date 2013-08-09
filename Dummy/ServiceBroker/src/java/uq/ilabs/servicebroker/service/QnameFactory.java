/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.service;

import edu.mit.ilab.ObjectFactory;
import edu.mit.ilab.SbAuthHeader;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author uqlpayne
 */
public class QnameFactory {

    private static ObjectFactory objectFactory;
    private static String sbAuthHeaderLocalPart;

    /**
     *
     * @return edu.mit.ilab.ObjectFactory
     */
    public static ObjectFactory getObjectFactory() {
        if (objectFactory == null) {
            objectFactory = new ObjectFactory();
        }
        return objectFactory;
    }

    /**
     *
     * @return String
     */
    public static String getSbAuthHeaderLocalPart() {
        if (sbAuthHeaderLocalPart == null) {
            JAXBElement<SbAuthHeader> jaxbElement = getObjectFactory().createSbAuthHeader(new SbAuthHeader());
            sbAuthHeaderLocalPart = jaxbElement.getName().getLocalPart();
        }
        return sbAuthHeaderLocalPart;
    }
}
