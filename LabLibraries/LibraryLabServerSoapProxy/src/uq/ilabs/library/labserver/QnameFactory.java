/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import edu.mit.ilab.labserver.proxy.AuthHeader;
import edu.mit.ilab.labserver.proxy.ObjectFactory;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 *
 * @author uqlpayne
 */
public class QnameFactory {

    private static ObjectFactory objectFactory;
    private static QName authHeaderQName;

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
    public static QName getAuthHeaderQName() {
        if (authHeaderQName == null) {
            JAXBElement<AuthHeader> jaxbElement = getObjectFactory().createAuthHeader(new AuthHeader());
            authHeaderQName = jaxbElement.getName();
        }
        return authHeaderQName;
    }
}
