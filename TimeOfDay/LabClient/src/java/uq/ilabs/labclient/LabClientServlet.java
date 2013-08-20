/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labclient;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uq.ilabs.library.lab.types.LabStatus;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labclient.Consts;
import uq.ilabs.library.labclient.engine.ConfigProperties;
import uq.ilabs.library.labclient.engine.LabClientSession;
import uq.ilabs.library.labclient.ServiceBrokerAPI;

/**
 *
 * @author uqlpayne
 */
public class LabClientServlet extends HttpServlet {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabClientServlet.class.getName();
    private static final Level logLevel = Level.INFO;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_UserHost_arg2 = "UserHost - IP Address: %s  Host Name: %s";
    private static final String STRLOG_RequestParams_arg5 = "CouponId: %d  Passkey: %s  ServiceUrl: %s  LabServerId: %s  MultiSubmit: %s";
    private static final String STRLOG_GettingLabStatus = "Getting Lab Status...";
    private static final String STRLOG_LabStatus_arg2 = "LabStatus - Online: %s  Message: '%s'";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @EJB
    private LabClientAppBean labClientBean;
    //</editor-fold>

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String methodName = "doGet";

        /*
         * Initialise the LabClient if not done already
         */
        this.labClientBean.Initialise(request.getServletContext());

        /*
         * Get the LabClientSession information from the session
         */
        HttpSession httpSession = request.getSession();
        LabClientSession labClientSession = (LabClientSession) httpSession.getAttribute(Consts.STRSSN_LabClient);

        /*
         * Check if the LabClient session doesn't yet exist
         */
        if (labClientSession == null) {
            try {
                /*
                 * Log the caller's IP address and hostname
                 */
                Logfile.Write(logLevel, String.format(STRLOG_UserHost_arg2, request.getRemoteAddr(), request.getRemoteHost()));

                /*
                 * Create an instance of the LabClientSession ready to fill in
                 */
                labClientSession = new LabClientSession();

                /*
                 * Get ConfigProperties information
                 */
                ConfigProperties configProperties = this.labClientBean.getConfigProperties();

                /*
                 * Get request parameters
                 */
                int couponId = 0;
                String passkey = null;
                Map<String, String[]> parameterMap = request.getParameterMap();
                for (String key : parameterMap.keySet()) {
                    if (key.equalsIgnoreCase(Consts.STRREQ_CouponId)) {
                        /* Used with Batch ServiceBroker */
                        couponId = Integer.parseInt(request.getParameter(key));
                    } else if (key.equalsIgnoreCase(Consts.STRREQ_Coupon_Id)) {
                        /* Used with Merged ServiceBroker */
                        couponId = Integer.parseInt(request.getParameter(key));
                    } else if (key.equalsIgnoreCase(Consts.STRREQ_Passkey)) {
                        passkey = request.getParameter(key);
                    } else if (key.equalsIgnoreCase(Consts.STRREQ_ServiceUrl)) {
                        configProperties.setServiceUrl(request.getParameter(key));
                    } else if (key.equalsIgnoreCase(Consts.STRREQ_LabServerId)) {
                        configProperties.setLabServerId(request.getParameter(key));
                    } else if (key.equalsIgnoreCase(Consts.STRREQ_MultiSubmit)) {
                        configProperties.setMultiSubmit(Boolean.parseBoolean(request.getParameter(key)));
                    }
                }

                /*
                 * Create a ServiceBroker proxy and add authorisation information
                 */
                ServiceBrokerAPI serviceBrokerAPI = new ServiceBrokerAPI(configProperties.getServiceType(), configProperties.getServiceUrl());
                serviceBrokerAPI.setLabServerId(configProperties.getLabServerId());
                serviceBrokerAPI.setCouponId(couponId);
                serviceBrokerAPI.setCouponPasskey(passkey);

                /*
                 * Create an instance of the LabClientSession and fill in
                 */
                labClientSession.setServiceBrokerAPI(serviceBrokerAPI);
                labClientSession.setMultiSubmit(configProperties.isMultiSubmit());
                labClientSession.setFeedbackEmailUrl(configProperties.getFeedbackEmail());

                Logfile.Write(Level.CONFIG, String.format(STRLOG_RequestParams_arg5,
                        serviceBrokerAPI.getCouponId(), serviceBrokerAPI.getCouponPasskey(),
                        configProperties.getServiceUrl(), configProperties.getLabServerId(), configProperties.isMultiSubmit()));

                /*
                 * Get the lab status. It may not be online yet until web services have started
                 */
                Logfile.Write(logLevel, STRLOG_GettingLabStatus);
                LabStatus labStatus = serviceBrokerAPI.GetLabStatus();
                Logfile.Write(logLevel, String.format(STRLOG_LabStatus_arg2, labStatus.isOnline(), labStatus.getLabStatusMessage()));

                /*
                 * Get information from the lab configuration xml file
                 */
                String xmlLabConfiguration = serviceBrokerAPI.GetLabConfiguration();
                labClientSession.ParseLabConfiguration(xmlLabConfiguration);

                /*
                 * Set LabClientSession information in the session for access by the web pages
                 */
                httpSession.setAttribute(Consts.STRSSN_LabClient, labClientSession);

                /*
                 * Go to home page
                 */
                response.sendRedirect(request.getContextPath() + Consts.STRURL_Faces + Consts.STRURL_Home);

            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
                throw new ServletException(ex.toString());
            }
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
