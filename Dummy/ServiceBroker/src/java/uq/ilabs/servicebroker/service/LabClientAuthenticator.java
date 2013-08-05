/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.service;

import edu.mit.ilab.ObjectFactory;
import edu.mit.ilab.SbAuthHeader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.servicebroker.ServiceBrokerBean;

/**
 *
 * @author uqlpayne
 */
public class LabClientAuthenticator implements SOAPHandler<SOAPMessageContext> {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    /*
     * String constants
     */
    private static final String STR_CouponId = "couponID";
    private static final String STR_CouponPasskey = "couponPassKey";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private static ObjectFactory objectFactory;
    private static String qnameSbAuthHeaderLocalPart;
    //</editor-fold>

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
            /*
             * Check if ServiceBrokerBean has been initialised
             */
            if (ServiceBrokerBean.isInitialised() == false) {
                ServiceBrokerBean.Initialise((ServletContext) messageContext.get(MessageContext.SERVLET_CONTEXT));
            }

            /*
             * Check if an instance of ObjectFactory has been created
             */
            if (objectFactory == null) {
                objectFactory = new ObjectFactory();
                
                /*
                 * Get the authentication header names
                 */
                JAXBElement<SbAuthHeader> jaxbElement = objectFactory.createSbAuthHeader(new SbAuthHeader());
                qnameSbAuthHeaderLocalPart = jaxbElement.getName().getLocalPart();
            }

            try {
                /*
                 * Write the SOAP message to system output
                 */
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                messageContext.getMessage().writeTo(outputStream);
//                System.out.println(outputStream.toString());

                /*
                 * Process the SOAP header to get the authentication information
                 */
                ProcessSoapHeader(messageContext);

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
                if (localName.equalsIgnoreCase(qnameSbAuthHeaderLocalPart) == true) {
                    /*
                     * SbAuthHeader
                     */
                    SbAuthHeader sbAuthHeader = ProcessSoapElementSbAuthHeader(soapElement);
                    messageContext.put(qnameSbAuthHeaderLocalPart, sbAuthHeader);
                    messageContext.setScope(qnameSbAuthHeaderLocalPart, MessageContext.Scope.APPLICATION);
                }
            }
        }
    }

    /**
     *
     * @param soapElement
     * @return SbAuthHeader
     */
    private SbAuthHeader ProcessSoapElementSbAuthHeader(SOAPElement soapElement) {
        SbAuthHeader sbAuthHeader = objectFactory.createSbAuthHeader();
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
                if (localName.equalsIgnoreCase(STR_CouponId) == true) {
                    sbAuthHeader.setCouponID(Long.parseLong(element.getValue()));
                } else if (localName.equalsIgnoreCase(STR_CouponPasskey) == true) {
                    sbAuthHeader.setCouponPassKey(element.getValue());
                }
            }
        }

        return sbAuthHeader;
    }
}
