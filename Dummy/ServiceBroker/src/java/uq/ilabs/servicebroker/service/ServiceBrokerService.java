/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.service;

import edu.mit.ilab.ObjectFactory;
import edu.mit.ilab.SbAuthHeader;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceContext;
import uq.ilabs.servicebroker.engine.ConfigProperties;

/**
 *
 * @author uqlpayne
 */
@WebService(serviceName = "ServiceBrokerService", portName = "ServiceBrokerServiceSoap", endpointInterface = "edu.mit.ilab.ServiceBrokerServiceSoap",
targetNamespace = "http://ilab.mit.edu", wsdlLocation = "WEB-INF/wsdl/ServiceBrokerService/IServiceBrokerService.asmx.wsdl")
@HandlerChain(file = "ServiceBrokerService_handler.xml")
public class ServiceBrokerService {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokerService.class.getName();
    /*
     * String constants
     */
    private static final String STR_AccessDenied = "ServiceBrokerService - Access Denied!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @Resource
    private WebServiceContext wsContext;
    @EJB
    private ServiceBrokerServiceBean serviceBrokerServiceBean;
    private static String qnameSbAuthHeaderLocalPart;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static boolean initialised = false;
    private static ConfigProperties configProperties;

    public static boolean isInitialised() {
        return initialised;
    }

    public static void setInitialised(boolean initialised) {
        ServiceBrokerService.initialised = initialised;
    }

    public static ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public static void setConfigProperties(ConfigProperties configProperties) {
        ServiceBrokerService.configProperties = configProperties;
    }
    //</editor-fold>

    public boolean cancel(int experimentID) {
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        return serviceBrokerServiceBean.cancel(sbAuthHeader, experimentID);
    }

    public edu.mit.ilab.WaitEstimate getEffectiveQueueLength(java.lang.String labServerID, int priorityHint) {
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        return serviceBrokerServiceBean.getEffectiveQueueLength(sbAuthHeader, labServerID, priorityHint);
    }

    public edu.mit.ilab.LabExperimentStatus getExperimentStatus(int experimentID) {
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        return serviceBrokerServiceBean.getExperimentStatus(sbAuthHeader, experimentID);
    }

    public java.lang.String getLabConfiguration(java.lang.String labServerID) {
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        return serviceBrokerServiceBean.getLabConfiguration(sbAuthHeader, labServerID);
    }

    public java.lang.String getLabInfo(java.lang.String labServerID) {
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        return serviceBrokerServiceBean.getLabInfo(sbAuthHeader, labServerID);
    }

    public edu.mit.ilab.LabStatus getLabStatus(java.lang.String labServerID) {
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        return serviceBrokerServiceBean.getLabStatus(sbAuthHeader, labServerID);
    }

    public edu.mit.ilab.ResultReport retrieveResult(int experimentID) {
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        return serviceBrokerServiceBean.retrieveResult(sbAuthHeader, experimentID);
    }

    public edu.mit.ilab.ClientSubmissionReport submit(java.lang.String labServerID, java.lang.String experimentSpecification, int priorityHint, boolean emailNotification) {
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        return serviceBrokerServiceBean.submit(sbAuthHeader, labServerID, experimentSpecification, priorityHint, emailNotification);
    }

    public edu.mit.ilab.ValidationReport validate(java.lang.String labServerID, java.lang.String experimentSpecification) {
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        return serviceBrokerServiceBean.validate(sbAuthHeader, labServerID, experimentSpecification);
    }

    public void notify(int experimentID) {
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        serviceBrokerServiceBean.notify(sbAuthHeader, experimentID);
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
            JAXBElement<SbAuthHeader> jaxbElement = objectFactory.createSbAuthHeader(new SbAuthHeader());
            qnameSbAuthHeaderLocalPart = jaxbElement.getName().getLocalPart();
        }
        Object object = wsContext.getMessageContext().get(qnameSbAuthHeaderLocalPart);

        /*
         * Check that it is an SbAuthHeader
         */
        if (object != null && object instanceof SbAuthHeader) {
            sbAuthHeader = (SbAuthHeader) object;
        }

        return sbAuthHeader;
    }
}
