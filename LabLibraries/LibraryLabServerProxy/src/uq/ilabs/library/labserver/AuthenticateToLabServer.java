/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import uq.ilabs.labserver.AuthHeader;
import uq.ilabs.labserver.ObjectFactory;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class AuthenticateToLabServer implements SOAPHandler<SOAPMessageContext> {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    /*
     * String constants
     */
    private static final String STR_Identifier = "identifier";
    private static final String STR_Passkey = "passKey";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private static boolean initialised = false;
    private static SOAPFactory soapFactory;
    private static ObjectFactory objectFactory;
    private static QName qnameAuthHeader;
    //</editor-fold>

    @Override
    public boolean handleMessage(SOAPMessageContext messageContext) {
        boolean success = false;

        /*
         * Process the SOAP header for an outbound message
         */
        if ((Boolean) messageContext.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY) == true) {
            try {
                /*
                 * Check if local static variables have been initialised
                 */
                if (initialised == false) {
                    soapFactory = SOAPFactory.newInstance();
                    objectFactory = new ObjectFactory();
                    JAXBElement<AuthHeader> jaxbElementAuthHeader = objectFactory.createAuthHeader(new AuthHeader());
                    qnameAuthHeader = jaxbElementAuthHeader.getName();

                    initialised = true;
                }

                /*
                 * Process the SOAP header to add the authentication information
                 */
                this.ProcessSoapHeader(messageContext);

                /*
                 * Write the finished SOAP message to system output
                 */
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                messageContext.getMessage().writeTo(outputStream);
//                System.out.println(outputStream.toString());

                success = true;
            } catch (SOAPException | IOException ex) {
                Logfile.WriteError(ex.toString());
            }
        }

        return success;
    }

    @Override
    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }

    @Override
    public boolean handleFault(SOAPMessageContext messageContext) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }

    /**
     *
     * @param messageContext
     * @throws SOAPException
     */
    private void ProcessSoapHeader(SOAPMessageContext messageContext) throws SOAPException {
        /*
         * Get the SOAP header
         */
        SOAPMessage soapMessage = messageContext.getMessage();
        SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();
        if (soapHeader == null) {
            soapHeader = soapEnvelope.addHeader();
        }

        /*
         * Get authentication header information from the context and process
         */
        Object object = messageContext.get(qnameAuthHeader.getLocalPart());
        if (object != null && object instanceof AuthHeader) {
            /*
             * AuthHeader
             */
            this.ProcessAuthHeader((AuthHeader) object, qnameAuthHeader, soapHeader);
        }
    }

    /**
     *
     * @param authHeader
     * @param qnameAuthHeader
     * @param soapHeader
     * @return SOAPHeaderElement
     */
    private SOAPHeaderElement ProcessAuthHeader(AuthHeader authHeader, QName qnameAuthHeader, SOAPHeader soapHeader) {

        SOAPHeaderElement headerElement;

        try {
            /*
             * Create the authentication header element
             */
            headerElement = soapHeader.addHeaderElement(qnameAuthHeader);

            /*
             * Check if Identifier is specified
             */
            if (authHeader.getIdentifier() != null) {
                /*
                 * Create Identifier element
                 */
                QName qName = new QName(qnameAuthHeader.getNamespaceURI(), STR_Identifier, qnameAuthHeader.getPrefix());
                SOAPElement element = soapFactory.createElement(qName);
                element.addTextNode(authHeader.getIdentifier());
                headerElement.addChildElement(element);
            }

            /*
             * Check if PassKey is specified
             */
            if (authHeader.getPassKey() != null) {
                /*
                 * Create PassKey element
                 */
                QName qName = new QName(qnameAuthHeader.getNamespaceURI(), STR_Passkey, qnameAuthHeader.getPrefix());
                SOAPElement element = soapFactory.createElement(qName);
                element.addTextNode(authHeader.getPassKey());
                headerElement.addChildElement(element);
            }
        } catch (SOAPException ex) {
            headerElement = null;
        }

        return headerElement;
    }
}
