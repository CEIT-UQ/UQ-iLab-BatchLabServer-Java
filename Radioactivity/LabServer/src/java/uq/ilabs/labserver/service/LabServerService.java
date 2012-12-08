/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.service;

import edu.mit.ilab.AuthHeader;
import edu.mit.ilab.ObjectFactory;
import java.util.HashMap;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.WebServiceContext;
import uq.ilabs.library.labserver.engine.ConfigProperties;
import uq.ilabs.library.labserver.engine.types.ServiceBrokerInfo;

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
    private static String qnameAuthHeaderLocalPart;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static boolean initialised = false;
    private static boolean loggerCreated = false;
    private static ConfigProperties configProperties;
    private static HashMap<String, ServiceBrokerInfo> mapServiceBrokerInfo;

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

    public static HashMap<String, ServiceBrokerInfo> getMapServiceBrokerInfo() {
        return mapServiceBrokerInfo;
    }

    public static void setMapServiceBrokerInfo(HashMap<String, ServiceBrokerInfo> mapServiceBrokerInfo) {
        LabServerService.mapServiceBrokerInfo = mapServiceBrokerInfo;
    }
    //</editor-fold>

    public boolean cancel(int experimentID) {
        AuthHeader authHeader = this.GetAuthHeader();
        return labServerServiceBean.cancel(authHeader, experimentID);
    }

    public edu.mit.ilab.WaitEstimate getEffectiveQueueLength(java.lang.String userGroup, int priorityHint) {
        AuthHeader authHeader = this.GetAuthHeader();
        return labServerServiceBean.getEffectiveQueueLength(authHeader, userGroup, priorityHint);
    }

    public edu.mit.ilab.LabExperimentStatus getExperimentStatus(int experimentID) {
        AuthHeader authHeader = this.GetAuthHeader();
        return labServerServiceBean.getExperimentStatus(authHeader, experimentID);
    }

    public java.lang.String getLabConfiguration(java.lang.String userGroup) {
        AuthHeader authHeader = this.GetAuthHeader();
        return labServerServiceBean.getLabConfiguration(authHeader, userGroup);
    }

    public java.lang.String getLabInfo() {
        AuthHeader authHeader = this.GetAuthHeader();
        return labServerServiceBean.getLabInfo(authHeader);
    }

    public edu.mit.ilab.LabStatus getLabStatus() {
        AuthHeader authHeader = this.GetAuthHeader();
        return labServerServiceBean.getLabStatus(authHeader);
    }

    public edu.mit.ilab.ResultReport retrieveResult(int experimentID) {
        AuthHeader authHeader = this.GetAuthHeader();
        return labServerServiceBean.retrieveResult(authHeader, experimentID);
    }

    public edu.mit.ilab.SubmissionReport submit(int experimentID, java.lang.String experimentSpecification, java.lang.String userGroup, int priorityHint) {
        AuthHeader authHeader = this.GetAuthHeader();
        return labServerServiceBean.submit(authHeader, experimentID, experimentSpecification, userGroup, priorityHint);
    }

    public edu.mit.ilab.ValidationReport validate(java.lang.String experimentSpecification, java.lang.String userGroup) {
        AuthHeader authHeader = this.GetAuthHeader();
        return labServerServiceBean.validate(authHeader, experimentSpecification, userGroup);
    }

    //================================================================================================================//
    /**
     *
     * @return AuthHeader
     */
    private AuthHeader GetAuthHeader() {
        AuthHeader authHeader = null;

        /*
         * Get the authentication header from the message context
         */
        if (qnameAuthHeaderLocalPart == null) {
            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<AuthHeader> jaxbElement = objectFactory.createAuthHeader(new AuthHeader());
            qnameAuthHeaderLocalPart = jaxbElement.getName().getLocalPart();
        }
        Object object = wsContext.getMessageContext().get(qnameAuthHeaderLocalPart);

        /*
         * Check that it is an AuthHeader
         */
        if (object != null && object instanceof AuthHeader) {
            authHeader = (AuthHeader) object;
        }

        return authHeader;
    }
}
