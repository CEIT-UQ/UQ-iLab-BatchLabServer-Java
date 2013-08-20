/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.service;

import edu.mit.ilab.AuthHeader;
import edu.mit.ilab.ObjectFactory;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author uqlpayne
 */
public class QnameFactory {

    private static ObjectFactory objectFactory;
    private static String authHeaderLocalPart;

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
    public static String getAuthHeaderLocalPart() {
        if (authHeaderLocalPart == null) {
            JAXBElement<AuthHeader> jaxbElement = getObjectFactory().createAuthHeader(new AuthHeader());
            authHeaderLocalPart = jaxbElement.getName().getLocalPart();
        }
        return authHeaderLocalPart;
    }
}
