/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labequipment.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import uq.ilabs.library.lab.types.AuthHeader;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class LabServerAuthenticator implements SOAPHandler<SOAPMessageContext> {

    @Override
    public boolean handleMessage(SOAPMessageContext messageContext) {
        /*
         * Assume this will fail
         */
        boolean success = false;

        /*
         * Process the header info for an inbound message if authentication is required
         */
        if ((Boolean) messageContext.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY) == false) {
            try {
                /*
                 * Write the SOAP message to system output
                 */
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                messageContext.getMessage().writeTo(outputStream);
//                System.out.println(LabServerAuthenticator.class.getSimpleName());
//                System.out.println(outputStream.toString());

                /*
                 * Process the SOAP header to get the authentication information
                 */
                ProcessSoapHeader(messageContext);

                success = true;

            } catch (SOAPException | IOException ex) {
                /*
                 * Create a SOAPFaultException to be thrown back to the caller
                 */
                try {
                    SOAPFault fault = SOAPFactory.newInstance().createFault();
                    fault.setFaultString(ex.getMessage());
                    throw new SOAPFaultException(fault);
                } catch (SOAPException e) {
                    Logfile.WriteError(e.getMessage());
                }
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

        /*
         * Scan through the header's child elements looking for the authentication header
         */
        Iterator iterator = soapHeader.getChildElements();
        while (iterator.hasNext()) {
            /*
             * Get the next child looking for a SOAPElement type
             */
            Object object = iterator.next();
            if (object instanceof SOAPElement) {
                /*
                 * Get the SOAPElement looking for the authentication header
                 */
                SOAPElement soapElement = (SOAPElement) object;
                Name elementName = soapElement.getElementName();
                String localName = elementName.getLocalName();

                /*
                 * Process the authentication header and pass to the web service through the
                 * message context. The scope has to be changed from HANDLER to APPLICATION so
                 * that the web service can see the message context map
                 */
                if (localName.equalsIgnoreCase(QnameFactory.getAuthHeaderLocalPart()) == true) {
                    /*
                     * AuthHeader
                     */
                    AuthHeader authHeader = ProcessSoapElementAuthHeader(soapElement);
                    String authHeaderName = uq.ilabs.library.lab.types.AuthHeader.class.getSimpleName();
                    messageContext.put(authHeaderName, authHeader);
                    messageContext.setScope(authHeaderName, MessageContext.Scope.APPLICATION);
                }
            }
        }
    }

    /**
     *
     * @param soapElement
     * @return AuthHeader
     */
    private AuthHeader ProcessSoapElementAuthHeader(SOAPElement soapElement) {
        AuthHeader authHeader = new AuthHeader();
        Iterator iterator = soapElement.getChildElements();
        while (iterator.hasNext()) {
            /*
             * Get the next child looking for a SOAPElement type
             */
            Object object = iterator.next();
            if (object instanceof SOAPElement) {
                /*
                 * Get the SOAPElement looking for the authentication information
                 */
                SOAPElement element = (SOAPElement) object;
                Name elementName = element.getElementName();
                String localName = elementName.getLocalName();

                /*
                 * Check if localName matches a specified string
                 */
                if (localName.equalsIgnoreCase(AuthHeader.STR_Identifier) == true) {
                    authHeader.setIdentifier(element.getValue());
                } else if (localName.equalsIgnoreCase(AuthHeader.STR_Passkey) == true) {
                    authHeader.setPasskey(element.getValue());
                }
            }
        }

        return authHeader;
    }
}
