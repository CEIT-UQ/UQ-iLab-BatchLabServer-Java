/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labequipment;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import uq.ilabs.labequipment.proxy.AuthHeader;
import uq.ilabs.labequipment.proxy.ObjectFactory;

/**
 *
 * @author uqlpayne
 */
public class QnameFactory {

    private static ObjectFactory objectFactory;
    private static QName authHeaderQName;

    /**
     *
     * @return uq.ilabs.labequipment.proxy.ObjectFactory
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
    public static QName getAuthHeaderQName() {
        if (authHeaderQName == null) {
            JAXBElement<AuthHeader> jaxbElement = getObjectFactory().createAuthHeader(new AuthHeader());
            authHeaderQName = jaxbElement.getName();
        }
        return authHeaderQName;
    }
}
