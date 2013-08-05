/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker.rest;

import edu.mit.ilab.rest.ClientSubmissionReport;
import edu.mit.ilab.rest.LabExperimentStatus;
import edu.mit.ilab.rest.LabStatus;
import edu.mit.ilab.rest.ResultReport;
import edu.mit.ilab.rest.SbAuthHeader;
import edu.mit.ilab.rest.ValidationReport;
import edu.mit.ilab.rest.WaitEstimate;
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
import javax.ws.rs.core.HttpHeaders;
import uq.ilabs.servicebroker.ServiceBrokerBean;

/**
 * REST Web Service
 *
 * @author uqlpayne
 */
@Path("")
@RequestScoped
public class ServiceBrokerRestResource {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokerRestResource.class.getName();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @EJB
    ServiceBrokerRestBean serviceBrokerRestBean;
    @Context
    private HttpHeaders httpHeaders;
    @Context
    private ServletContext servletContext;
    //</editor-fold>

    /**
     * Creates a new instance of ServiceBrokerRestResource
     */
    public ServiceBrokerRestResource() {
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
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        boolean success = this.serviceBrokerRestBean.cancel(sbAuthHeader, experimentId);
        return Boolean.toString(success);
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
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        WaitEstimate waitEstimate = this.serviceBrokerRestBean.getEffectiveQueueLength(sbAuthHeader, labServerId, priorityHint);
        return waitEstimate.ToXmlString();
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
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        LabExperimentStatus labExperimentStatus = this.serviceBrokerRestBean.getExperimentStatus(sbAuthHeader, experimentId);
        return labExperimentStatus.ToXmlString();
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
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        return this.serviceBrokerRestBean.getLabConfiguration(sbAuthHeader, labServerId);
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
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        return this.serviceBrokerRestBean.getLabInfo(sbAuthHeader, labServerId);
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
    public String getLabStatus(@PathParam("labServerId") String labServerId) {
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        LabStatus labStatus = this.serviceBrokerRestBean.getLabStatus(sbAuthHeader, labServerId);
        return labStatus.ToXmlString();
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
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        ResultReport resultReport = this.serviceBrokerRestBean.retrieveResult(sbAuthHeader, experimentId);
        return resultReport.ToXmlString();
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
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        ClientSubmissionReport clientSubmissionReport = this.serviceBrokerRestBean.submit(sbAuthHeader, labServerId, xmlSpecification, priorityHint, emailNotification);
        return clientSubmissionReport.ToXmlString();
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
        SbAuthHeader sbAuthHeader = this.GetSbAuthHeader();
        ValidationReport validationReport = this.serviceBrokerRestBean.validate(sbAuthHeader, labServerId, xmlSpecification);
        return validationReport.ToXmlString();
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
        this.serviceBrokerRestBean.notify(sbAuthHeader, experimentId);
    }

    //================================================================================================================//
    /**
     *
     * @return SbAuthHeader
     */
    private SbAuthHeader GetSbAuthHeader() {
        /*
         * Check if ServiceBrokerBean has been initialised
         */
        if (ServiceBrokerBean.isInitialised() == false) {
            ServiceBrokerBean.Initialise(this.servletContext);
        }

        /*
         * Create instance of SbAuthHeader
         */
        SbAuthHeader sbAuthHeader = null;
        try {
            int couponId = Integer.parseInt(this.httpHeaders.getHeaderString(SbAuthHeader.STR_CouponId));
            sbAuthHeader = new SbAuthHeader();
            sbAuthHeader.setCouponID(couponId);
            sbAuthHeader.setCouponPassKey(this.httpHeaders.getHeaderString(SbAuthHeader.STR_CouponPasskey));
        } catch (NumberFormatException ex) {
        }

        return sbAuthHeader;
    }
}
