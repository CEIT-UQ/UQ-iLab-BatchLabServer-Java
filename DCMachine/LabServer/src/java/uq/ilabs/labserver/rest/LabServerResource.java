/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.rest;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
 * REST Web Service
 *
 * @author uqlpayne
 */
@Path("")
public class LabServerResource {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabServerResource.class.getName();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @EJB
    private LabServerAppBean labServerBean;
    @Context
    private HttpHeaders httpHeaders;
    @Context
    private ServletContext servletContext;
    //</editor-fold>

    /**
     * Creates a new instance of LabServerResource
     */
    public LabServerResource() {
    }

    /**
     * PUT method for Cancel
     *
     * @param experimentId
     * @return
     */
    @Path("Cancel/{experimentId}")
    @PUT
    @Produces("text/plain")
    public String putCancel(@PathParam("experimentId") int experimentId) {
        String plainString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            boolean success = this.labServerBean.getLabServerHandler().cancel(authHeader, experimentId);
            plainString = Boolean.toString(success);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return plainString;
    }

    /**
     * GET method for EffectiveQueueLength
     *
     * @param labServerId
     * @param priorityHint
     * @return
     */
    @Path("EffectiveQueueLength/{userGroup}/{priorityHint}")
    @GET
    @Produces("application/xml")
    public String getEffectiveQueueLength(@PathParam("userGroup") String userGroup, @PathParam("priorityHint") int priorityHint) {
        String xmlString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            WaitEstimate waitEstimate = this.labServerBean.getLabServerHandler().getEffectiveQueueLength(authHeader, userGroup, priorityHint);
            xmlString = waitEstimate.ToXmlString();
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return xmlString;
    }

    /**
     * GET method for ExperimentStatus
     *
     * @param experimentId
     * @return
     */
    @Path("ExperimentStatus/{experimentId}")
    @GET
    @Produces("application/xml")
    public String getExperimentStatus(@PathParam("experimentId") int experimentId) {
        String xmlString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            LabExperimentStatus labExperimentStatus = this.labServerBean.getLabServerHandler().getExperimentStatus(authHeader, experimentId);
            xmlString = labExperimentStatus.ToXmlString();
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return xmlString;
    }

    /**
     * GET method for LabConfiguration
     *
     * @param labServerId
     * @return
     */
    @Path("LabConfiguration/{userGroup}")
    @GET
    @Produces("application/xml")
    public String getLabConfiguration(@PathParam("userGroup") String userGroup) {
        String xmlString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            xmlString = this.labServerBean.getLabServerHandler().getLabConfiguration(authHeader, userGroup);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return xmlString;
    }

    /**
     * GET method for LabInfo
     *
     * @param labServerId
     * @return
     */
    @Path("LabInfo")
    @GET
    @Produces("text/plain")
    public String getLabInfo() {
        String plainString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            plainString = this.labServerBean.getLabServerHandler().getLabInfo(authHeader);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return plainString;
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
            LabStatus labStatus = this.labServerBean.getLabServerHandler().getLabStatus(authHeader);
            xmlString = labStatus.ToXmlString();
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return xmlString;
    }

    /**
     * GET method for RetrieveResult
     *
     * @param experimentId
     * @return
     */
    @Path("RetrieveResult/{experimentId}")
    @GET
    @Produces("application/xml")
    public String getRetrieveResult(@PathParam("experimentId") int experimentId) {
        String xmlString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            ResultReport resultReport = this.labServerBean.getLabServerHandler().retrieveResult(authHeader, experimentId);
            xmlString = resultReport.ToXmlString();
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return xmlString;
    }

    /**
     * POST method for Submit
     *
     * @param labServerId
     * @param priorityHint
     * @param emailNotification
     * @param xmlSpecification
     * @return
     */
    @Path("Submit/{experimentId}/{userGroup}/{priorityHint}")
    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String postSubmit(@PathParam("experimentId") int experimentId, @PathParam("userGroup") String userGroup, @PathParam("priorityHint") int priorityHint, String xmlSpecification) {
        String xmlString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            SubmissionReport submissionReport = this.labServerBean.getLabServerHandler().submit(authHeader, experimentId, xmlSpecification, userGroup, priorityHint);
            xmlString = submissionReport.ToXmlString();
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return xmlString;
    }

    /**
     * POST method for Validate
     *
     * @param labServerId
     * @param xmlSpecification
     * @return
     */
    @Path("Validate/{userGroup}")
    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String postValidate(@PathParam("userGroup") String userGroup, String xmlSpecification) {
        String xmlString = null;

        AuthHeader authHeader = this.GetAuthHeader();

        try {
            ValidationReport validationReport = this.labServerBean.getLabServerHandler().validate(authHeader, xmlSpecification, userGroup);
            xmlString = validationReport.ToXmlString();
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return xmlString;
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
             * Start the service if not done already
             */
            this.labServerBean.StartService(this.servletContext);

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
