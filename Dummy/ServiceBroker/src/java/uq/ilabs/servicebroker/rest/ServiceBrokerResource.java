/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.rest;

import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.enterprise.context.RequestScoped;
import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import uq.ilabs.library.lab.exceptions.UnauthorizedException;
import uq.ilabs.library.lab.types.ClientSubmissionReport;
import uq.ilabs.library.lab.types.LabExperimentStatus;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.types.ResultReport;
import uq.ilabs.library.lab.types.SbAuthHeader;
import uq.ilabs.library.lab.types.ValidationReport;
import uq.ilabs.library.lab.types.WaitEstimate;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.servicebroker.ServiceBrokerAppBean;

/**
 * REST Web Service
 *
 * @author uqlpayne
 */
@Path("")
@RequestScoped
public class ServiceBrokerResource {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokerResource.class.getName();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @EJB
    private ServiceBrokerAppBean serviceBrokerBean;
    @Context
    private HttpHeaders httpHeaders;
    @Context
    private ServletContext servletContext;
    //</editor-fold>

    /**
     * Creates a new instance of ServiceBrokerResource
     */
    public ServiceBrokerResource() {
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

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            boolean success = this.serviceBrokerBean.getServiceBrokerHandler().cancel(sbAuthHeader, experimentId);
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
    @Path("EffectiveQueueLength/{labServerId}/{priorityHint}")
    @GET
    @Produces("application/xml")
    public String getEffectiveQueueLength(@PathParam("labServerId") String labServerId, @PathParam("priorityHint") int priorityHint) {
        String xmlString = null;

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            WaitEstimate waitEstimate = this.serviceBrokerBean.getServiceBrokerHandler().getEffectiveQueueLength(sbAuthHeader, labServerId, priorityHint);
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

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            LabExperimentStatus labExperimentStatus = this.serviceBrokerBean.getServiceBrokerHandler().getExperimentStatus(sbAuthHeader, experimentId);
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
    @Path("LabConfiguration/{labServerId}")
    @GET
    @Produces("application/xml")
    public String getLabConfiguration(@PathParam("labServerId") String labServerId) {
        String xmlString = null;

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            xmlString = this.serviceBrokerBean.getServiceBrokerHandler().getLabConfiguration(sbAuthHeader, labServerId);
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
    @Path("LabInfo/{labServerId}")
    @GET
    @Produces("text/plain")
    public String getLabInfo(@PathParam("labServerId") String labServerId) {
        String plainString = null;

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            plainString = this.serviceBrokerBean.getServiceBrokerHandler().getLabInfo(sbAuthHeader, labServerId);
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
    @Path("LabStatus/{labServerId}")
    @GET
    @Produces("application/xml")
    public String getLabStatus(@PathParam("labServerId") String labServerId) throws Exception {
        String xmlString = null;

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            LabStatus labStatus = this.serviceBrokerBean.getServiceBrokerHandler().getLabStatus(sbAuthHeader, labServerId);
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

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            ResultReport resultReport = this.serviceBrokerBean.getServiceBrokerHandler().retrieveResult(sbAuthHeader, experimentId);
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
    @Path("Submit/{labServerId}/{priorityHint}/{emailNotification}")
    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String postSubmit(@PathParam("labServerId") String labServerId, @PathParam("priorityHint") int priorityHint, @PathParam("emailNotification") boolean emailNotification, String xmlSpecification) {
        String xmlString = null;

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            ClientSubmissionReport clientSubmissionReport = this.serviceBrokerBean.getServiceBrokerHandler().submit(sbAuthHeader, labServerId, xmlSpecification, priorityHint, emailNotification);
            xmlString = clientSubmissionReport.ToXmlString();
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
    @Path("Validate/{labServerId}")
    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String postValidate(@PathParam("labServerId") String labServerId, String xmlSpecification) {
        String xmlString = null;

        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            ValidationReport validationReport = this.serviceBrokerBean.getServiceBrokerHandler().validate(sbAuthHeader, labServerId, xmlSpecification);
            xmlString = validationReport.ToXmlString();
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }

        return xmlString;
    }

    /**
     * PUT method for Notify
     *
     * @param experimentId
     */
    @Path("Notify/{experimentId}")
    @PUT
    public void putNotify(@PathParam("experimentId") int experimentId) {
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();

        try {
            this.serviceBrokerBean.getServiceBrokerHandler().notify(sbAuthHeader, experimentId);
        } catch (UnauthorizedException ex) {
            this.ThrowUnauthorizedException(ex);
        } catch (Exception ex) {
            this.ThrowException(ex);
        }
    }

    //================================================================================================================//
    /**
     *
     * @return SbAuthHeader
     */
    private SbAuthHeader GetSbAuthHeader() {
        final String methodName = "GetSbAuthHeader";

        SbAuthHeader sbAuthHeader = null;

        try {
            /*
             * Start the service if not done already
             */
            this.serviceBrokerBean.StartService(this.servletContext);

            /*
             * Create instance of SbAuthHeader
             */
            int couponId = Integer.parseInt(this.httpHeaders.getHeaderString(SbAuthHeader.STR_CouponId));
            sbAuthHeader = new SbAuthHeader();
            sbAuthHeader.setCouponId(couponId);
            sbAuthHeader.setCouponPasskey(this.httpHeaders.getHeaderString(SbAuthHeader.STR_CouponPasskey));
        } catch (Exception ex) {
            Logfile.WriteException(STR_ClassName, methodName, ex);
        }

        return sbAuthHeader;
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
