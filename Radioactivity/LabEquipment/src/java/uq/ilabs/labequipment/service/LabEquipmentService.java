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
import javax.xml.ws.ProtocolException;
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
        this.ProcessAuthHeader();
        return labEquipmentServiceBean.GetLabEquipmentStatus();
    }

    public int getTimeUntilReady() {
        this.ProcessAuthHeader();
        return labEquipmentServiceBean.GetTimeUntilReady();
    }

    public au.edu.uq.ilab.Validation validate(java.lang.String xmlSpecification) {
        this.ProcessAuthHeader();
        return labEquipmentServiceBean.Validate(xmlSpecification);
    }

    public au.edu.uq.ilab.ExecutionStatus startLabExecution(java.lang.String xmlSpecification) {
        this.ProcessAuthHeader();
        return labEquipmentServiceBean.StartLabExecution(xmlSpecification);
    }

    public au.edu.uq.ilab.ExecutionStatus getLabExecutionStatus(int executionId) {
        this.ProcessAuthHeader();
        return labEquipmentServiceBean.GetLabExecutionStatus(executionId);
    }

    public java.lang.String getLabExecutionResults(int executionId) {
        this.ProcessAuthHeader();
        return labEquipmentServiceBean.GetLabExecutionResults(executionId);
    }

    public boolean cancelLabExecution(int executionId) {
        this.ProcessAuthHeader();
        return labEquipmentServiceBean.CancelLabExecution(executionId);
    }

    /**
     *
     * @return boolean
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
             * Get the LabServer's guid and passkey
             */
            AuthHeader authHeader = (AuthHeader) object;
            identifier = authHeader.getIdentifier();
            passkey = authHeader.getPassKey();
        }

        /*
         * Authenticate - Do we just return a boolean or do we throw an exception?
         */
        boolean success = labEquipmentServiceBean.Authenticate(identifier, passkey);
        if (success == false) {
            throw new ProtocolException(STR_AccessDenied);
        }

        return (success == true) ? identifier : null;
    }
}
