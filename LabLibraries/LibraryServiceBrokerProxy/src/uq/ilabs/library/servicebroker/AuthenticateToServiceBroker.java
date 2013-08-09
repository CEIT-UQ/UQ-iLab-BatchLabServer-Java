/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.servicebroker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.servicebroker.SbAuthHeader;

/**
 *
 * @author uqlpayne
 */
public class AuthenticateToServiceBroker implements SOAPHandler<SOAPMessageContext> {

    //<editor-fold defaultstate="collapsed" desc="Variables">
    private static SOAPFactory soapFactory;
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
                 * Check if SOAPFactory instance have been created
                 */
                if (soapFactory == null) {
                    soapFactory = SOAPFactory.newInstance();
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
         * Get the SOAP envelope and add a SOAP header
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
        Object object = messageContext.get(QnameFactory.getSbAuthHeaderName().getLocalPart());
        if (object != null && object instanceof SbAuthHeader) {
            /*
             * SbAuthHeader
             */
            this.ProcessSbAuthHeader((SbAuthHeader) object, QnameFactory.getSbAuthHeaderName(), soapHeader);
        }
    }

    /**
     *
     * @param sbAuthHeader
     * @param qnameAuthHeader
     * @param soapHeader
     * @return SOAPHeaderElement
     */
    private SOAPHeaderElement ProcessSbAuthHeader(SbAuthHeader sbAuthHeader, QName qnameAuthHeader, SOAPHeader soapHeader) {

        SOAPHeaderElement headerElement;

        try {
            /*
             * Create the authentication header element
             */
            headerElement = soapHeader.addHeaderElement(qnameAuthHeader);

            /*
             * Create couponId element and add to the coupon element
             */
            QName qName = new QName(qnameAuthHeader.getNamespaceURI(), uq.ilabs.library.lab.types.SbAuthHeader.STR_CouponId, qnameAuthHeader.getPrefix());
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
                qName = new QName(qnameAuthHeader.getNamespaceURI(), uq.ilabs.library.lab.types.SbAuthHeader.STR_CouponPasskey, qnameAuthHeader.getPrefix());
                element = soapFactory.createElement(qName);
                element.addTextNode(sbAuthHeader.getCouponPassKey());
                headerElement.addChildElement(element);
            }
        } catch (SOAPException ex) {
            headerElement = null;
        }

        return headerElement;
    }
}
