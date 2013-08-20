/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labequipment.service;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import uq.ilabs.labequipment.LabEquipmentAppBean;
import uq.ilabs.library.lab.exceptions.UnauthorizedException;
import uq.ilabs.library.lab.types.AuthHeader;
import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.LabEquipmentStatus;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
@WebService(serviceName = "LabEquipmentService",
        portName = "LabEquipmentServiceSoap",
        endpointInterface = "au.edu.uq.ilab.LabEquipmentServiceSoap",
        targetNamespace = "http://ilab.uq.edu.au/",
        wsdlLocation = "WEB-INF/wsdl/LabEquipmentService/ILabEquipmentService.asmx.wsdl")
@HandlerChain(file = "LabEquipmentService_handler.xml")
public class LabEquipmentService {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentService.class.getName();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @Resource
    private WebServiceContext wsContext;
    @EJB
    private LabEquipmentAppBean labEquipmentBean;
    //</editor-fold>

    /**
     *
     * @return au.edu.uq.ilab.LabEquipmentStatus
     */
    public au.edu.uq.ilab.LabEquipmentStatus getLabEquipmentStatus() {
        au.edu.uq.ilab.LabEquipmentStatus proxyLabEquipmentStatus = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            LabEquipmentStatus labEquipmentStatus = this.labEquipmentBean.getLabEquipmentHandler().GetLabEquipmentStatus(authHeader);
            proxyLabEquipmentStatus = ConvertTypes.Convert(labEquipmentStatus);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return proxyLabEquipmentStatus;
    }

    /**
     *
     * @return int
     */
    public int getTimeUntilReady() {
        int timeUntilReady = -1;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            timeUntilReady = this.labEquipmentBean.getLabEquipmentHandler().GetTimeUntilReady(authHeader);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return timeUntilReady;
    }

    /**
     *
     * @param xmlSpecification
     * @return au.edu.uq.ilab.Validation
     */
    public au.edu.uq.ilab.Validation validate(java.lang.String xmlSpecification) {
        au.edu.uq.ilab.Validation proxyValidation = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            Validation validation = this.labEquipmentBean.getLabEquipmentHandler().Validate(authHeader, xmlSpecification);
            proxyValidation = ConvertTypes.Convert(validation);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return proxyValidation;
    }

    /**
     *
     * @param xmlSpecification
     * @return au.edu.uq.ilab.ExecutionStatus
     */
    public au.edu.uq.ilab.ExecutionStatus startLabExecution(java.lang.String xmlSpecification) {
        au.edu.uq.ilab.ExecutionStatus proxyExecutionStatus = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            ExecutionStatus executionStatus = this.labEquipmentBean.getLabEquipmentHandler().StartLabExecution(authHeader, xmlSpecification);
            proxyExecutionStatus = ConvertTypes.Convert(executionStatus);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return proxyExecutionStatus;
    }

    /**
     *
     * @param executionId
     * @return au.edu.uq.ilab.ExecutionStatus
     */
    public au.edu.uq.ilab.ExecutionStatus getLabExecutionStatus(int executionId) {
        au.edu.uq.ilab.ExecutionStatus proxyExecutionStatus = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            ExecutionStatus executionStatus = this.labEquipmentBean.getLabEquipmentHandler().GetLabExecutionStatus(authHeader, executionId);
            proxyExecutionStatus = ConvertTypes.Convert(executionStatus);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return proxyExecutionStatus;
    }

    /**
     *
     * @param executionId
     * @return java.lang.String
     */
    public java.lang.String getLabExecutionResults(int executionId) {
        String labExecutionResults = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            labExecutionResults = this.labEquipmentBean.getLabEquipmentHandler().GetLabExecutionResults(authHeader, executionId);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return labExecutionResults;
    }

    /**
     *
     * @param executionId
     * @return boolean
     */
    public boolean cancelLabExecution(int executionId) {
        boolean success = false;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            success = this.labEquipmentBean.getLabEquipmentHandler().CancelLabExecution(authHeader, executionId);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return success;
    }

    //================================================================================================================//
    /**
     *
     * @return AuthHeader
     */
    private AuthHeader GetAuthHeader() {
        final String methodName = "GetAuthHeader";

        AuthHeader authHeader = null;

        try {
            /*
             * Start the LabEquipment service if not done already
             */
            this.labEquipmentBean.StartService((ServletContext) this.wsContext.getMessageContext().get(MessageContext.SERVLET_CONTEXT));


            /*
             * Get the authentication header from the message context
             */
            Object object = this.wsContext.getMessageContext().get(AuthHeader.class.getSimpleName());

            /*
             * Check that it is an AuthHeader
             */
            if (object != null && object instanceof AuthHeader) {
                authHeader = (AuthHeader) object;
            }
        } catch (Exception ex) {
            Logfile.WriteException(STR_ClassName, methodName, ex);
        }

        return authHeader;
    }

    /**
     *
     * @param ex
     */
    private void ThrowUnauthorizedException(Exception ex) {
        this.ThrowSOAPFault(ex.getMessage());
    }

    /**
     *
     * @param ex
     */
    private void ThrowException(Exception ex) {
        this.ThrowSOAPFault(ex.getMessage());
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
