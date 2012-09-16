/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient.servicebroker;

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
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.servicebroker.ObjectFactory;
import uq.ilabs.servicebroker.SbAuthHeader;

/**
 *
 * @author uqlpayne
 */
public class AuthenticateToServiceBroker implements SOAPHandler<SOAPMessageContext> {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    /*
     * String constants
     */
    private static final String STR_CouponId = "couponID";
    private static final String STR_CouponPasskey = "couponPassKey";
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
         * Get the SOAP envelope and add a SOAP header
         */
        SOAPMessage soapMessage = messageContext.getMessage();
        SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.addHeader();

        /*
         * Get the authentication header information
         */
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<SbAuthHeader> jaxbElementSbAuthHeader = objectFactory.createSbAuthHeader(new SbAuthHeader());
        String qnameLocalPart = jaxbElementSbAuthHeader.getName().getLocalPart();
        Object object = messageContext.get(qnameLocalPart);
        if (object != null && object instanceof SbAuthHeader) {
            SbAuthHeader sbAuthHeader = (SbAuthHeader) object;
            SOAPFactory soapFactory = SOAPFactory.newInstance();

            /*
             * Create the authentication header element
             */
            QName qNameSbAuthHeader = jaxbElementSbAuthHeader.getName();
            SOAPHeaderElement headerElement = soapHeader.addHeaderElement(qNameSbAuthHeader);

            /*
             * Create couponId element and add to the coupon element
             */
            QName qName = new QName(qNameSbAuthHeader.getNamespaceURI(), STR_CouponId, qNameSbAuthHeader.getPrefix());
            SOAPElement element = soapFactory.createElement(qName);
            element.addTextNode(Long.toString(sbAuthHeader.getCouponID()));
            headerElement.addChildElement(element);

            /*
             * Check if coupon passkey is specified
             */
            if (sbAuthHeader.getCouponPassKey() != null) {
                /*
                 * Create the coupon passkey element and add its value
                 */
                qName = new QName(qNameSbAuthHeader.getNamespaceURI(), STR_CouponPasskey, qNameSbAuthHeader.getPrefix());
                element = soapFactory.createElement(qName);
                element.addTextNode(sbAuthHeader.getCouponPassKey());
                headerElement.addChildElement(element);
            }
        }
    }
}
