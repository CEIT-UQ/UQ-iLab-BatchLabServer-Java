/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.service;

import edu.mit.ilab.AuthHeader;
import edu.mit.ilab.ObjectFactory;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceContext;
import uq.ilabs.library.labserver.database.ServiceBrokersDB;
import uq.ilabs.library.labserver.engine.ConfigProperties;

/**
 *
 * @author uqlpayne
 */
@WebService(serviceName = "LabServerWebService", portName = "LabServerWebServiceSoap", endpointInterface = "edu.mit.ilab.LabServerWebServiceSoap",
targetNamespace = "http://ilab.mit.edu", wsdlLocation = "WEB-INF/wsdl/LabServerService/ILabServerWebService.asmx.wsdl")
@HandlerChain(file = "LabServerService_handler.xml")
public class LabServerService {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabServerService.class.getName();
    /*
     * String constants
     */
    private static final String STR_AccessDenied = "LabServerService - Access Denied!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @Resource
    private WebServiceContext wsContext;
    @EJB
    private LabServerServiceBean labServerServiceBean;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static boolean initialised = false;
    private static boolean loggerCreated = false;
    private static ConfigProperties configProperties;
    private static ServiceBrokersDB serviceBrokers;

    public static boolean isInitialised() {
        return initialised;
    }

    public static void setInitialised(boolean initialised) {
        LabServerService.initialised = initialised;
    }

    public static boolean isLoggerCreated() {
        return loggerCreated;
    }

    public static void setLoggerCreated(boolean loggerCreated) {
        LabServerService.loggerCreated = loggerCreated;
    }

    public static ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public static void setConfigProperties(ConfigProperties configProperties) {
        LabServerService.configProperties = configProperties;
    }

    public static ServiceBrokersDB getServiceBrokers() {
        return serviceBrokers;
    }

    public static void setServiceBrokers(ServiceBrokersDB serviceBrokers) {
        LabServerService.serviceBrokers = serviceBrokers;
    }
    //</editor-fold>

    /**
     *
     * @param experimentID
     * @return
     */
    public boolean cancel(int experimentID) {
        String identifier = this.ProcessAuthHeader();
        String sbName = serviceBrokers.GetNameByGuid(identifier);
        return labServerServiceBean.cancel(experimentID, sbName);
    }

    /**
     *
     * @param userGroup
     * @param priorityHint
     * @return
     */
    public edu.mit.ilab.WaitEstimate getEffectiveQueueLength(java.lang.String userGroup, int priorityHint) {
        this.ProcessAuthHeader();
        return labServerServiceBean.getEffectiveQueueLength(userGroup, priorityHint);
    }

    /**
     *
     * @param experimentID
     * @return
     */
    public edu.mit.ilab.LabExperimentStatus getExperimentStatus(int experimentID) {
        String identifier = this.ProcessAuthHeader();
        String sbName = serviceBrokers.GetNameByGuid(identifier);
        return labServerServiceBean.getExperimentStatus(experimentID, sbName);
    }

    /**
     *
     * @param userGroup
     * @return
     */
    public java.lang.String getLabConfiguration(java.lang.String userGroup) {
        this.ProcessAuthHeader();
        return labServerServiceBean.getLabConfiguration(userGroup);
    }

    /**
     *
     * @return
     */
    public java.lang.String getLabInfo() throws Exception {
        this.ProcessAuthHeader();
        return labServerServiceBean.getLabInfo();
    }

    /**
     *
     * @return
     */
    public edu.mit.ilab.LabStatus getLabStatus() {
        this.ProcessAuthHeader();
        return labServerServiceBean.getLabStatus();
    }

    /**
     *
     * @param experimentID
     * @return
     */
    public edu.mit.ilab.ResultReport retrieveResult(int experimentID) {
        String identifier = this.ProcessAuthHeader();
        String sbName = serviceBrokers.GetNameByGuid(identifier);
        return labServerServiceBean.retrieveResult(experimentID, sbName);
    }

    /**
     *
     * @param experimentID
     * @param experimentSpecification
     * @param userGroup
     * @param priorityHint
     * @return
     */
    public edu.mit.ilab.SubmissionReport submit(int experimentID, java.lang.String experimentSpecification, java.lang.String userGroup, int priorityHint) {
        String identifier = this.ProcessAuthHeader();
        String sbName = serviceBrokers.GetNameByGuid(identifier);
        return labServerServiceBean.submit(experimentID, sbName, experimentSpecification, userGroup, priorityHint);
    }

    /**
     *
     * @param experimentSpecification
     * @param userGroup
     * @return
     */
    public edu.mit.ilab.ValidationReport validate(java.lang.String experimentSpecification, java.lang.String userGroup) {
        this.ProcessAuthHeader();
        return labServerServiceBean.validate(experimentSpecification, userGroup);
    }

    /**
     *
     * @return String
     */
    private String ProcessAuthHeader() {
        /*
         * Get the authentication header from the message context
         */
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<AuthHeader> jaxbElement = objectFactory.createAuthHeader(new AuthHeader());
        Object object = wsContext.getMessageContext().get(jaxbElement.getName().getLocalPart());

        /*
         * Check that it is an AuthHeader
         */
        String identifier = null;
        String passkey = null;
        if (object != null && object instanceof AuthHeader) {
            /*
             * Get the ServiceBroker's guid and outgoing passkey
             */
            AuthHeader authHeader = (AuthHeader) object;
            identifier = authHeader.getIdentifier();
            passkey = authHeader.getPassKey();
        }

        /*
         * Authenticate - Do we just return a boolean or do we throw an exception?
         */
        boolean success = labServerServiceBean.Authenticate(identifier, passkey);
        if (success == false) {
            throw new ProtocolException(STR_AccessDenied);
        }

        return (success == true) ? identifier : null;
    }
}
