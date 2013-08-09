/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.service;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.SOAPFaultException;
import uq.ilabs.library.lab.types.ClientSubmissionReport;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.SbAuthHeader;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;
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
            success = this.serviceBrokerBean.getServiceBrokerHandler().cancel(sbAuthHeader, experimentID);
        } catch (Exception ex) {
            this.ThrowSOAPFault(ex.getMessage());
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
        edu.mit.ilab.WaitEstimate proxyWaitEstimate = null;

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            WaitEstimate waitEstimate = this.serviceBrokerBean.getServiceBrokerHandler().getEffectiveQueueLength(sbAuthHeader, labServerID, priorityHint);
            proxyWaitEstimate = ConvertTypes.Convert(waitEstimate);
        } catch (Exception ex) {
            this.ThrowSOAPFault(ex.getMessage());
        }

        return proxyWaitEstimate;
    }

    /**
     *
     * @param experimentID
     * @return edu.mit.ilab.LabExperimentStatus
     */
    public edu.mit.ilab.LabExperimentStatus getExperimentStatus(int experimentID) {
        edu.mit.ilab.LabExperimentStatus proxyLabExperimentStatus = null;

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            LabExperimentStatus labExperimentStatus = this.serviceBrokerBean.getServiceBrokerHandler().getExperimentStatus(sbAuthHeader, experimentID);
            proxyLabExperimentStatus = ConvertTypes.Convert(labExperimentStatus);
        } catch (Exception ex) {
            this.ThrowSOAPFault(ex.getMessage());
        }

        return proxyLabExperimentStatus;
    }

    /**
     *
     * @param labServerID
     * @return java.lang.String
     */
    public java.lang.String getLabConfiguration(java.lang.String labServerID) {
        String labConfiguration = null;

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            labConfiguration = this.serviceBrokerBean.getServiceBrokerHandler().getLabConfiguration(sbAuthHeader, labServerID);
        } catch (Exception ex) {
            this.ThrowSOAPFault(ex.getMessage());
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

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            labInfo = this.serviceBrokerBean.getServiceBrokerHandler().getLabInfo(sbAuthHeader, labServerID);
        } catch (Exception ex) {
            this.ThrowSOAPFault(ex.getMessage());
        }

        return labInfo;
    }

    /**
     *
     * @param labServerID
     * @return edu.mit.ilab.LabStatus
     */
    public edu.mit.ilab.LabStatus getLabStatus(java.lang.String labServerID) {
        edu.mit.ilab.LabStatus proxyLabStatus = null;

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            LabStatus labStatus = this.serviceBrokerBean.getServiceBrokerHandler().getLabStatus(sbAuthHeader, labServerID);
            proxyLabStatus = ConvertTypes.Convert(labStatus);
        } catch (Exception ex) {
            this.ThrowSOAPFault(ex.getMessage());
        }

        return proxyLabStatus;
    }

    /**
     *
     * @param experimentID
     * @return edu.mit.ilab.ResultReport
     */
    public edu.mit.ilab.ResultReport retrieveResult(int experimentID) {
        edu.mit.ilab.ResultReport proxyResultReport = null;

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            ResultReport resultReport = this.serviceBrokerBean.getServiceBrokerHandler().retrieveResult(sbAuthHeader, experimentID);
            proxyResultReport = ConvertTypes.Convert(resultReport);
        } catch (Exception ex) {
            this.ThrowSOAPFault(ex.getMessage());
        }

        return proxyResultReport;
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
        edu.mit.ilab.ClientSubmissionReport proxyClientSubmissionReport = null;

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            ClientSubmissionReport clientSubmissionReport = this.serviceBrokerBean.getServiceBrokerHandler().submit(sbAuthHeader, labServerID, experimentSpecification, priorityHint, emailNotification);
            proxyClientSubmissionReport = ConvertTypes.Convert(clientSubmissionReport);
        } catch (Exception ex) {
            this.ThrowSOAPFault(ex.getMessage());
        }

        return proxyClientSubmissionReport;
    }

    /**
     *
     * @param labServerID
     * @param experimentSpecification
     * @return edu.mit.ilab.ValidationReport
     */
    public edu.mit.ilab.ValidationReport validate(java.lang.String labServerID, java.lang.String experimentSpecification) {
        edu.mit.ilab.ValidationReport proxyValidationReport = null;

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            ValidationReport validationReport = this.serviceBrokerBean.getServiceBrokerHandler().validate(sbAuthHeader, labServerID, experimentSpecification);
            proxyValidationReport = ConvertTypes.Convert(validationReport);
        } catch (Exception ex) {
            this.ThrowSOAPFault(ex.getMessage());
        }

        return proxyValidationReport;
    }

    /**
     *
     * @param experimentID
     */
    public void notify(int experimentID) {
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            this.serviceBrokerBean.getServiceBrokerHandler().notify(sbAuthHeader, experimentID);
        } catch (Exception ex) {
            this.ThrowSOAPFault(ex.getMessage());
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
        Object object = wsContext.getMessageContext().get(QnameFactory.getSbAuthHeaderLocalPart());

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
