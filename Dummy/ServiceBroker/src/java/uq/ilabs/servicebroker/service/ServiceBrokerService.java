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

    /**
     *
     * @param experimentID
     * @return
     */
    public boolean cancel(int experimentID) {
        this.ProcessSbAuthHeader();
        return serviceBrokerServiceBean.cancel(experimentID);
    }

    /**
     *
     * @param labServerID
     * @param priorityHint
     * @return
     */
    public edu.mit.ilab.WaitEstimate getEffectiveQueueLength(java.lang.String labServerID, int priorityHint) {
        this.ProcessSbAuthHeader();
        return serviceBrokerServiceBean.getEffectiveQueueLength(labServerID, priorityHint);
    }

    /**
     *
     * @param experimentID
     * @return
     */
    public edu.mit.ilab.LabExperimentStatus getExperimentStatus(int experimentID) {
        this.ProcessSbAuthHeader();
        return serviceBrokerServiceBean.getExperimentStatus(experimentID);
    }

    /**
     *
     * @param labServerID
     * @return
     */
    public java.lang.String getLabConfiguration(java.lang.String labServerID) {
        this.ProcessSbAuthHeader();
        return serviceBrokerServiceBean.getLabConfiguration(labServerID);
    }

    /**
     *
     * @param labServerID
     * @return
     */
    public java.lang.String getLabInfo(java.lang.String labServerID) {
        this.ProcessSbAuthHeader();
        return serviceBrokerServiceBean.getLabInfo(labServerID);
    }

    /**
     *
     * @param labServerID
     * @return
     */
    public edu.mit.ilab.LabStatus getLabStatus(java.lang.String labServerID) {
        this.ProcessSbAuthHeader();
        return serviceBrokerServiceBean.getLabStatus(labServerID);
    }

    /**
     *
     * @param experimentID
     * @return
     */
    public edu.mit.ilab.ResultReport retrieveResult(int experimentID) {
        this.ProcessSbAuthHeader();
        return serviceBrokerServiceBean.retrieveResult(experimentID);
    }

    /**
     *
     * @param labServerID
     * @param experimentSpecification
     * @param priorityHint
     * @param emailNotification
     * @return
     */
    public edu.mit.ilab.ClientSubmissionReport submit(java.lang.String labServerID, java.lang.String experimentSpecification, int priorityHint, boolean emailNotification) {
        this.ProcessSbAuthHeader();
        return serviceBrokerServiceBean.submit(labServerID, experimentSpecification, priorityHint, emailNotification);
    }

    /**
     *
     * @param labServerID
     * @param experimentSpecification
     * @return
     */
    public edu.mit.ilab.ValidationReport validate(java.lang.String labServerID, java.lang.String experimentSpecification) {
        this.ProcessSbAuthHeader();
        return serviceBrokerServiceBean.validate(labServerID, experimentSpecification);
    }

    /**
     *
     * @param experimentID
     */
    public void notify(int experimentID) {
//        this.ProcessSbAuthHeader();
        serviceBrokerServiceBean.notify(experimentID);
    }

    /**
     *
     * @return boolean
     */
    private boolean ProcessSbAuthHeader() {
        /*
         * Get the authentication header from the message context
         */
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<SbAuthHeader> jaxbElement = objectFactory.createSbAuthHeader(new SbAuthHeader());
        Object object = wsContext.getMessageContext().get(jaxbElement.getName().getLocalPart());

        /*
         * Check that it is an SbAuthHeader
         */
        long couponId = 0;
        String passkey = null;
        if (object != null && object instanceof SbAuthHeader) {
            /*
             * Get the coupon Id and passkey
             */
            SbAuthHeader sbAuthHeader = (SbAuthHeader) object;
            couponId = sbAuthHeader.getCouponID();
            passkey = sbAuthHeader.getCouponPassKey();
        }

        /*
         * Authenticate - Do we just return a boolean or do we throw an exception?
         */
        boolean success = serviceBrokerServiceBean.Authenticate(couponId, passkey);
        if (success == false) {
            throw new ProtocolException(STR_AccessDenied);
        }

        return success;
    }
}
