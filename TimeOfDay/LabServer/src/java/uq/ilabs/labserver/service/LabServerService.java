/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.service;

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
import uq.ilabs.labserver.LabServerAppBean;
import uq.ilabs.library.lab.exceptions.UnauthorizedException;
import uq.ilabs.library.lab.types.AuthHeader;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.SubmissionReport;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
@WebService(serviceName = "LabServerWebService",
        portName = "LabServerWebServiceSoap",
        endpointInterface = "edu.mit.ilab.LabServerWebServiceSoap",
        targetNamespace = "http://ilab.mit.edu",
        wsdlLocation = "WEB-INF/wsdl/LabServerService/ILabServerWebService.asmx.wsdl")
@HandlerChain(file = "LabServerService_handler.xml")
public class LabServerService {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabServerService.class.getName();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @Resource
    private WebServiceContext wsContext;
    @EJB
    private LabServerAppBean labServerBean;
    //</editor-fold>

    /**
     *
     * @param experimentID
     * @return boolean
     */
    public boolean cancel(int experimentID) {
        boolean success = false;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            success = this.labServerBean.getLabServerHandler().cancel(authHeader, experimentID);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return success;
    }

    /**
     *
     * @param userGroup
     * @param priorityHint
     * @return edu.mit.ilab.WaitEstimate
     */
    public edu.mit.ilab.WaitEstimate getEffectiveQueueLength(java.lang.String userGroup, int priorityHint) {
        edu.mit.ilab.WaitEstimate proxyWaitEstimate = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            WaitEstimate waitEstimate = this.labServerBean.getLabServerHandler().getEffectiveQueueLength(authHeader, userGroup, priorityHint);
            proxyWaitEstimate = ConvertTypes.Convert(waitEstimate);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
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

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            LabExperimentStatus labExperimentStatus = this.labServerBean.getLabServerHandler().getExperimentStatus(authHeader, experimentID);
            proxyLabExperimentStatus = ConvertTypes.Convert(labExperimentStatus);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return proxyLabExperimentStatus;
    }

    /**
     *
     * @param userGroup
     * @return String
     */
    public java.lang.String getLabConfiguration(java.lang.String userGroup) {
        String labConfiguration = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            labConfiguration = this.labServerBean.getLabServerHandler().getLabConfiguration(authHeader, userGroup);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return labConfiguration;
    }

    /**
     *
     * @return String
     */
    public java.lang.String getLabInfo() {
        String labInfo = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            labInfo = this.labServerBean.getLabServerHandler().getLabInfo(authHeader);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return labInfo;
    }

    /**
     *
     * @return edu.mit.ilab.LabStatus
     */
    public edu.mit.ilab.LabStatus getLabStatus() {
        edu.mit.ilab.LabStatus proxyLabStatus = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            LabStatus labStatus = this.labServerBean.getLabServerHandler().getLabStatus(authHeader);
            proxyLabStatus = ConvertTypes.Convert(labStatus);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
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

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            ResultReport resultReport = this.labServerBean.getLabServerHandler().retrieveResult(authHeader, experimentID);
            proxyResultReport = ConvertTypes.Convert(resultReport);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return proxyResultReport;
    }

    /**
     *
     * @param experimentID
     * @param experimentSpecification
     * @param userGroup
     * @param priorityHint
     * @return edu.mit.ilab.SubmissionReport
     */
    public edu.mit.ilab.SubmissionReport submit(int experimentID, java.lang.String experimentSpecification, java.lang.String userGroup, int priorityHint) {
        edu.mit.ilab.SubmissionReport proxySubmissionReport = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            SubmissionReport submissionReport = this.labServerBean.getLabServerHandler().submit(authHeader, experimentID, experimentSpecification, userGroup, priorityHint);
            proxySubmissionReport = ConvertTypes.Convert(submissionReport);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return proxySubmissionReport;
    }

    /**
     *
     * @param experimentSpecification
     * @param userGroup
     * @return edu.mit.ilab.ValidationReport
     */
    public edu.mit.ilab.ValidationReport validate(java.lang.String experimentSpecification, java.lang.String userGroup) {
        edu.mit.ilab.ValidationReport proxyValidationReport = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            ValidationReport validationReport = this.labServerBean.getLabServerHandler().validate(authHeader, experimentSpecification, userGroup);
            proxyValidationReport = ConvertTypes.Convert(validationReport);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return proxyValidationReport;
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
             * Start the LabServer service if not done already
             */
            this.labServerBean.StartService((ServletContext) this.wsContext.getMessageContext().get(MessageContext.SERVLET_CONTEXT));

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
