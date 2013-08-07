/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.service;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.bind.JAXBElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.SOAPFaultException;
import uq.ilabs.library.lab.types.SbAuthHeader;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.servicebroker.ConvertTypes;
import uq.ilabs.servicebroker.ServiceBrokerBean;

/**
 *
 * @author uqlpayne
 */
@WebService(serviceName = "ServiceBrokerService",
        portName = "ServiceBrokerServiceSoap",
        endpointInterface = "edu.mit.ilab.ServiceBrokerServiceSoap",
        targetNamespace = "http://ilab.mit.edu",
        wsdlLocation = "WEB-INF/wsdl/ServiceBrokerService/IServiceBrokerService.asmx.wsdl")
@HandlerChain(file = "ServiceBrokerService_handler.xml")
public class ServiceBrokerService {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokerService.class.getName();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @Resource
    private WebServiceContext wsContext;
    @EJB
    private ServiceBrokerBean serviceBrokerBean;
    private static String qnameSbAuthHeaderLocalPart;
    //</editor-fold>

    /**
     *
     * @param experimentID
     * @return boolean
     */
    public boolean cancel(int experimentID) {
        boolean success = false;

        try {
            SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
            success = this.serviceBrokerBean.cancel(sbAuthHeader, experimentID);
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
        }

        return success;
    }

    /**
     *
     * @param labServerID
     * @param priorityHint
     * @return edu.mit.ilab.WaitEstimate
     */
    public edu.mit.ilab.WaitEstimate getEffectiveQueueLength(java.lang.String labServerID, int priorityHint) {
        edu.mit.ilab.WaitEstimate waitEstimate = null;

        try {
            SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
            waitEstimate = ConvertTypes.Convert(this.serviceBrokerBean.getEffectiveQueueLength(sbAuthHeader, labServerID, priorityHint));
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
        }

        return waitEstimate;
    }

    /**
     *
     * @param experimentID
     * @return edu.mit.ilab.LabExperimentStatus
     */
    public edu.mit.ilab.LabExperimentStatus getExperimentStatus(int experimentID) {
        edu.mit.ilab.LabExperimentStatus labExperimentStatus = null;

        try {
            SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
            labExperimentStatus = ConvertTypes.Convert(this.serviceBrokerBean.getExperimentStatus(sbAuthHeader, experimentID));
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
        }

        return labExperimentStatus;
    }

    /**
     *
     * @param labServerID
     * @return java.lang.String
     */
    public java.lang.String getLabConfiguration(java.lang.String labServerID) {
        String labConfiguration = null;

        try {
            SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
            labConfiguration = this.serviceBrokerBean.getLabConfiguration(sbAuthHeader, labServerID);
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
        }

        return labConfiguration;
    }

    /**
     *
     * @param labServerID
     * @return java.lang.String
     */
    public java.lang.String getLabInfo(java.lang.String labServerID) {
        String labInfo = null;

        try {
            SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
            labInfo = this.serviceBrokerBean.getLabInfo(sbAuthHeader, labServerID);
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
        }

        return labInfo;
    }

    /**
     *
     * @param labServerID
     * @return edu.mit.ilab.LabStatus
     */
    public edu.mit.ilab.LabStatus getLabStatus(java.lang.String labServerID) {
        edu.mit.ilab.LabStatus labStatus = null;

        try {
            SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
            labStatus = ConvertTypes.Convert(this.serviceBrokerBean.getLabStatus(sbAuthHeader, labServerID));
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
        }

        return labStatus;
    }

    /**
     *
     * @param experimentID
     * @return edu.mit.ilab.ResultReport
     */
    public edu.mit.ilab.ResultReport retrieveResult(int experimentID) {
        edu.mit.ilab.ResultReport resultReport = null;

        try {
            SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
            resultReport = ConvertTypes.Convert(this.serviceBrokerBean.retrieveResult(sbAuthHeader, experimentID));
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
        }

        return resultReport;
    }

    /**
     *
     * @param labServerID
     * @param experimentSpecification
     * @param priorityHint
     * @param emailNotification
     * @return edu.mit.ilab.ClientSubmissionReport
     */
    public edu.mit.ilab.ClientSubmissionReport submit(java.lang.String labServerID, java.lang.String experimentSpecification, int priorityHint, boolean emailNotification) {
        edu.mit.ilab.ClientSubmissionReport clientSubmissionReport = null;

        try {
            SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
            clientSubmissionReport = ConvertTypes.Convert(this.serviceBrokerBean.submit(sbAuthHeader, labServerID, experimentSpecification, priorityHint, emailNotification));
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
        }

        return clientSubmissionReport;
    }

    /**
     *
     * @param labServerID
     * @param experimentSpecification
     * @return edu.mit.ilab.ValidationReport
     */
    public edu.mit.ilab.ValidationReport validate(java.lang.String labServerID, java.lang.String experimentSpecification) {
        edu.mit.ilab.ValidationReport validationReport = null;

        try {
            SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
            validationReport = ConvertTypes.Convert(this.serviceBrokerBean.validate(sbAuthHeader, labServerID, experimentSpecification));
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
        }

        return validationReport;
    }

    /**
     *
     * @param experimentID
     */
    public void notify(int experimentID) {
        try {
            SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
            this.serviceBrokerBean.notify(sbAuthHeader, experimentID);
        } catch (ProtocolException ex) {
            this.ThrowSOAPFault(ex.getMessage());
        } catch (Exception ex) {
        }
    }

    //================================================================================================================//
    /**
     *
     * @return SbAuthHeader
     */
    private SbAuthHeader GetSbAuthHeader() {
        SbAuthHeader sbAuthHeader = null;

        /*
         * Get the authentication header from the message context
         */
        if (qnameSbAuthHeaderLocalPart == null) {
            edu.mit.ilab.ObjectFactory objectFactory = new edu.mit.ilab.ObjectFactory();
            JAXBElement<edu.mit.ilab.SbAuthHeader> jaxbElement = objectFactory.createSbAuthHeader(new edu.mit.ilab.SbAuthHeader());
            qnameSbAuthHeaderLocalPart = jaxbElement.getName().getLocalPart();
        }
        Object object = wsContext.getMessageContext().get(qnameSbAuthHeaderLocalPart);

        /*
         * Check that it is an SbAuthHeader
         */
        if (object != null && object instanceof edu.mit.ilab.SbAuthHeader) {
            edu.mit.ilab.SbAuthHeader proxySbAuthHeader = (edu.mit.ilab.SbAuthHeader) object;
            sbAuthHeader = new SbAuthHeader();
            sbAuthHeader.setCouponId(proxySbAuthHeader.getCouponID());
            sbAuthHeader.setCouponPasskey(proxySbAuthHeader.getCouponPassKey());
        }

        return sbAuthHeader;
    }

    /**
     *
     * @param message
     */
    private void ThrowSOAPFault(String message) {
        /*
         * Create a SOAPFaultException to be thrown back to the caller
         */
        try {
            SOAPFault fault = SOAPFactory.newInstance().createFault();
            fault.setFaultString(message);
            throw new SOAPFaultException(fault);
        } catch (SOAPException e) {
            Logfile.WriteError(e.getMessage());
        }
    }
}
