/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import uq.ilabs.labserver.AuthHeader;
import uq.ilabs.labserver.ObjectFactory;

/**
 *
 * @author uqlpayne
 */
public class QnameFactory {

    private static ObjectFactory objectFactory;
    private static QName authHeaderName;

    /**
     *
     * @return uq.ilabs.labserver.ObjectFactory
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
    public static QName getAuthHeaderName() {
        if (authHeaderName == null) {
            JAXBElement<AuthHeader> jaxbElement = getObjectFactory().createAuthHeader(new AuthHeader());
            authHeaderName = jaxbElement.getName();
        }
        return authHeaderName;
    }
}
