/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.servicebroker;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import edu.mit.ilab.servicebroker.proxy.ObjectFactory;
import edu.mit.ilab.servicebroker.proxy.SbAuthHeader;

/**
 *
 * @author uqlpayne
 */
public class QnameFactory {

    private static ObjectFactory objectFactory;
    private static QName sbAuthHeaderQName;

    /**
     *
     * @return edu.mit.ilab.servicebroker.proxy.ObjectFactory
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
    public static QName getSbAuthHeaderQName() {
        if (sbAuthHeaderQName == null) {
            JAXBElement<SbAuthHeader> jaxbElement = getObjectFactory().createSbAuthHeader(new SbAuthHeader());
            sbAuthHeaderQName = jaxbElement.getName();
        }
        return sbAuthHeaderQName;
    }
}
