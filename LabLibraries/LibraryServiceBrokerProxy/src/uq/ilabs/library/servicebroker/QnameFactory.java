/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.servicebroker;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import uq.ilabs.servicebroker.ObjectFactory;
import uq.ilabs.servicebroker.SbAuthHeader;

/**
 *
 * @author uqlpayne
 */
public class QnameFactory {

    private static ObjectFactory objectFactory;
    private static QName sbAuthHeaderName;

    /**
     *
     * @return uq.ilabs.servicebroker.ObjectFactory
     */
    public static ObjectFactory getObjectFactory() {
        if (objectFactory == null) {
            objectFactory = new ObjectFactory();
        }
        return objectFactory;
    }

    /**
     *
     * @return QName
     */
    public static QName getSbAuthHeaderName() {
        if (sbAuthHeaderName == null) {
            JAXBElement<SbAuthHeader> jaxbElement = getObjectFactory().createSbAuthHeader(new SbAuthHeader());
            sbAuthHeaderName = jaxbElement.getName();
        }
        return sbAuthHeaderName;
    }
}
