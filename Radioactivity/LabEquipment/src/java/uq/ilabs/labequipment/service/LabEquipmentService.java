/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labequipment.service;

import au.edu.uq.ilab.AuthHeader;
import au.edu.uq.ilab.ObjectFactory;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.WebServiceContext;
import uq.ilabs.library.labequipment.engine.ConfigProperties;

/**
 *
 * @author uqlpayne
 */
@WebService(serviceName = "LabEquipmentService", portName = "LabEquipmentServiceSoap", endpointInterface = "au.edu.uq.ilab.LabEquipmentServiceSoap",
targetNamespace = "http://ilab.uq.edu.au/", wsdlLocation = "WEB-INF/wsdl/LabEquipmentService/ILabEquipmentService.asmx.wsdl")
@HandlerChain(file = "LabEquipmentService_handler.xml")
public class LabEquipmentService {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentService.class.getName();
    /*
     * String constants
     */
    private static final String STR_AccessDenied = "LabEquipmentService - Access Denied!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @Resource
    private WebServiceContext wsContext;
    @EJB
    private LabEquipmentServiceBean labEquipmentServiceBean;
    private static String qnameAuthHeaderLocalPart;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static boolean initialised = false;
    private static ConfigProperties configProperties;

    public static boolean isInitialised() {
        return initialised;
    }

    public static void setInitialised(boolean initialised) {
        LabEquipmentService.initialised = initialised;
    }

    public static ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public static void setConfigProperties(ConfigProperties configProperties) {
        LabEquipmentService.configProperties = configProperties;
    }
    //</editor-fold>

    public au.edu.uq.ilab.LabEquipmentStatus getLabEquipmentStatus() {
        AuthHeader authHeader = this.GetAuthHeader();
        return labEquipmentServiceBean.GetLabEquipmentStatus(authHeader);
    }

    public int getTimeUntilReady() {
        AuthHeader authHeader = this.GetAuthHeader();
        return labEquipmentServiceBean.GetTimeUntilReady(authHeader);
    }

    public au.edu.uq.ilab.Validation validate(java.lang.String xmlSpecification) {
        AuthHeader authHeader = this.GetAuthHeader();
        return labEquipmentServiceBean.Validate(authHeader, xmlSpecification);
    }

    public au.edu.uq.ilab.ExecutionStatus startLabExecution(java.lang.String xmlSpecification) {
        AuthHeader authHeader = this.GetAuthHeader();
        return labEquipmentServiceBean.StartLabExecution(authHeader, xmlSpecification);
    }

    public au.edu.uq.ilab.ExecutionStatus getLabExecutionStatus(int executionId) {
        AuthHeader authHeader = this.GetAuthHeader();
        return labEquipmentServiceBean.GetLabExecutionStatus(authHeader, executionId);
    }

    public java.lang.String getLabExecutionResults(int executionId) {
        AuthHeader authHeader = this.GetAuthHeader();
        return labEquipmentServiceBean.GetLabExecutionResults(authHeader, executionId);
    }

    public boolean cancelLabExecution(int executionId) {
        AuthHeader authHeader = this.GetAuthHeader();
        return labEquipmentServiceBean.CancelLabExecution(authHeader, executionId);
    }

    //================================================================================================================//
    /**
     *
     * @return SbAuthHeader
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
