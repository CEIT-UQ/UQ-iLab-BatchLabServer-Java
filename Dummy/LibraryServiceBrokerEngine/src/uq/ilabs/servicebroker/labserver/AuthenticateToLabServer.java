/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.labserver;

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

    @Override
    public boolean handleMessage(SOAPMessageContext messageContext) {
        boolean success = false;

        /*
         * Process the SOAP header for an outbound message
         */
        if ((Boolean) messageContext.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY) == true) {
            try {
                this.ProcessSoapHeader(messageContext);

                /*
                 * Write the finished SOAP message to system output
                 */
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                messageContext.getMessage().writeTo(outputStream);
//                System.out.println(outputStream.toString());

                success = true;
            } catch (SOAPException | IOException ex) {
                Logfile.WriteError(ex.getMessage());
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
         * Get the SOAP envelope and add a SOAP header
         */
        SOAPMessage soapMessage = messageContext.getMessage();
        SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.addHeader();
        SOAPFactory soapFactory = SOAPFactory.newInstance();

        /*
         * Create an instance of an authentication header ready to fill in
         */
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<AuthHeader> jaxbElement = objectFactory.createAuthHeader(new AuthHeader());

        /*
         * Get authentication header information from the context and process
         */
        Object object = messageContext.get(jaxbElement.getName().getLocalPart());
        if (object instanceof AuthHeader) {
            AuthHeader authHeader = (AuthHeader) object;

            /*
             * Create the authentication header element
             */
            QName qNameAuthHeader = jaxbElement.getName();
            SOAPHeaderElement headerElement = soapHeader.addHeaderElement(qNameAuthHeader);

            /*
             * Check if identifier is specified
             */
            if (authHeader.getIdentifier() != null) {
                /*
                 * Create the identifier element and add its value
                 */
                QName qName = new QName(qNameAuthHeader.getNamespaceURI(), STR_Identifier, qNameAuthHeader.getPrefix());
                SOAPElement element = soapFactory.createElement(qName);
                element.addTextNode(authHeader.getIdentifier());
                headerElement.addChildElement(element);
            }

            /*
             * Check if passkey is specified
             */
            if (authHeader.getPassKey() != null) {
                /*
                 * Create passkey element and add its value
                 */
                QName qName = new QName(qNameAuthHeader.getNamespaceURI(), STR_Passkey, qNameAuthHeader.getPrefix());
                SOAPElement element = soapFactory.createElement(qName);
                element.addTextNode(authHeader.getPassKey());
                headerElement.addChildElement(element);
            }
        }
    }
}
