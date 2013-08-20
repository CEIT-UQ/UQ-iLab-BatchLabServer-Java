/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labequipment.rest;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import uq.ilabs.labequipment.LabEquipmentAppBean;
import uq.ilabs.library.lab.exceptions.UnauthorizedException;
import uq.ilabs.library.lab.types.AuthHeader;
import uq.ilabs.library.lab.types.ExecutionStatus;
import uq.ilabs.library.lab.types.LabEquipmentStatus;
import uq.ilabs.library.lab.types.Validation;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 * REST Web Service
 *
 * @author uqlpayne
 */
@Path("")
public class LabEquipmentResource {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentResource.class.getName();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @EJB
    private LabEquipmentAppBean labEquipmentBean;
    @Context
    private HttpHeaders httpHeaders;
    @Context
    private ServletContext servletContext;
    //</editor-fold>

    /**
     * Creates a new instance of LabEquipmentResource
     */
    public LabEquipmentResource() {
    }

    /**
     * GET method for LabStatus
     *
     * @param labServerId
     * @return
     */
    @Path("LabStatus")
    @GET
    @Produces("application/xml")
    public String getLabStatus() {
        String xmlString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            LabEquipmentStatus labEquipmentStatus = this.labEquipmentBean.getLabEquipmentHandler().GetLabEquipmentStatus(authHeader);
            xmlString = labEquipmentStatus.ToXmlString();
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return xmlString;
    }

    /**
     * GET method for TimeUntilReady
     *
     * @param labServerId
     * @return
     */
    @Path("TimeUntilReady")
    @GET
    @Produces("text/plain")
    public String getTimeUntilReady() {
        String plainString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            int value = this.labEquipmentBean.getLabEquipmentHandler().GetTimeUntilReady(authHeader);
            plainString = Integer.toString(value);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return plainString;
    }

    /**
     * POST method for Validate
     *
     * @param labServerId
     * @param xmlSpecification
     * @return
     */
    @Path("Validate")
    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String postValidate(String xmlSpecification) {
        String xmlString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            Validation validation = this.labEquipmentBean.getLabEquipmentHandler().Validate(authHeader, xmlSpecification);
            xmlString = validation.ToXmlString();
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return xmlString;
    }

    /**
     * POST method for StartLabExecution
     *
     * @param labServerId
     * @param xmlSpecification
     * @return
     */
    @Path("StartLabExecution")
    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String postStartLabExecution(String xmlSpecification) {
        String xmlString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            ExecutionStatus executionStatus = this.labEquipmentBean.getLabEquipmentHandler().StartLabExecution(authHeader, xmlSpecification);
            xmlString = executionStatus.ToXmlString();
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return xmlString;
    }

    /**
     * GET method for LabExecutionStatus
     *
     * @param executionId
     * @return
     */
    @Path("LabExecutionStatus/{executionId}")
    @GET
    @Produces("application/xml")
    public String getLabExecutionStatus(@PathParam("executionId") int executionId) {
        String xmlString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            ExecutionStatus executionStatus = this.labEquipmentBean.getLabEquipmentHandler().GetLabExecutionStatus(authHeader, executionId);
            xmlString = executionStatus.ToXmlString();
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return xmlString;
    }

    /**
     * GET method for LabExecutionResults
     *
     * @param executionId
     * @return
     */
    @Path("LabExecutionResults/{executionId}")
    @GET
    @Produces("application/xml")
    public String getLabExecutionResults(@PathParam("executionId") int executionId) {
        String xmlString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            xmlString = this.labEquipmentBean.getLabEquipmentHandler().GetLabExecutionResults(authHeader, executionId);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return xmlString;
    }

    /**
     * PUT method for Cancel
     *
     * @param experimentId
     * @return
     */
    @Path("CancelLabExecution/{experimentId}")
    @PUT
    @Produces("text/plain")
    public String putCancel(@PathParam("experimentId") int experimentId) {
        String plainString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            boolean success = this.labEquipmentBean.getLabEquipmentHandler().CancelLabExecution(authHeader, experimentId);
            plainString = Boolean.toString(success);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return plainString;
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
            this.labEquipmentBean.StartService(this.servletContext);

            /*
             * Create instance of AuthHeader
             */
            authHeader = new AuthHeader();
            authHeader.setIdentifier(this.httpHeaders.getHeaderString(AuthHeader.STR_Identifier));
            authHeader.setPasskey(this.httpHeaders.getHeaderString(AuthHeader.STR_Passkey));
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
        throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).type(MediaType.TEXT_PLAIN_TYPE).entity(ex.getMessage()).build());
    }

    /**
     *
     * @param ex
     */
    private void ThrowException(Exception ex) {
        throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(ex.getMessage()).build());
    }
}
