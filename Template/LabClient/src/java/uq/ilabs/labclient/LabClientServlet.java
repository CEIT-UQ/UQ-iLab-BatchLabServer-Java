/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labclient;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import uq.ilabs.library.labclient.engine.LabConsts;
import uq.ilabs.library.servicebroker.ServiceBrokerAPI;

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
    private static final String STRLOG_LoggingLevel_arg = "LoggingLevel: %s";
    private static final String STRLOG_UserHost_arg2 = "UserHost - IP Address: %s  Host Name: %s";
    private static final String STRLOG_RequestParams_arg5 = "CouponId: %d  Passkey: %s  ServiceUrl: %s  LabServerId: %s  MultiSubmit: %s";
    private static final String STRLOG_GettingLabStatus = "Getting Lab Status...";
    private static final String STRLOG_LabStatus_arg2 = "LabStatus - Online: %s  Message: '%s'";
    //</editor-fold>

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String methodName = "doGet";

        /*
         * Get the LabClientSession information from the session
         */
        HttpSession httpSession = request.getSession();
        LabClientSession labClientSession = (LabClientSession) httpSession.getAttribute(Consts.STRSSN_LabClient);

        /*
         * Check if the LabClient session doesn't exists
         */
        if (labClientSession == null) {
            try {
                /*
                 * Get the path for the logfiles and logging level
                 */
                String logFilesPath = getServletContext().getInitParameter(LabConsts.STRPRM_LogFilesPath);
                logFilesPath = getServletContext().getRealPath(logFilesPath);
                String initLogLevel = getServletContext().getInitParameter(LabConsts.STRPRM_LogLevel);

                /*
                 * Create an instance of the logger and set the logging level
                 */
                Logger logger = Logfile.CreateLogger(logFilesPath);
                Level level = Level.INFO;
                try {
                    level = Level.parse(initLogLevel);
                } catch (Exception ex) {
                }
                logger.setLevel(level);

                Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                        String.format(STRLOG_LoggingLevel_arg, logger.getLevel().toString()));


                /*
                 * Log the caller's IP address and hostname
                 */
                Logfile.Write(logLevel, String.format(STRLOG_UserHost_arg2, request.getRemoteAddr(), request.getRemoteHost()));

                /*
                 * Get configuration properties from the file
                 */
                String xmlConfigPropertiesPath = getServletContext().getInitParameter(Consts.STRPRM_XmlConfigPropertiesPath);
                ConfigProperties configProperties = new ConfigProperties(getServletContext().getRealPath(xmlConfigPropertiesPath));

                /*
                 * Get request parameters
                 */
                int couponId = 0;
                String passkey = null;
                Map<String, String[]> parameterMap = request.getParameterMap();
                Iterator iterator = parameterMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    if (key.equalsIgnoreCase(Consts.STRREQ_CouponId)) {
                        couponId = Integer.parseInt(request.getParameter(key));
                    } else if (key.equalsIgnoreCase(Consts.STRREQ_Coupon_Id)) {
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
                ServiceBrokerAPI serviceBrokerAPI = new ServiceBrokerAPI(configProperties.getServiceUrl());
                serviceBrokerAPI.setLabServerId(configProperties.getLabServerId());
                serviceBrokerAPI.setCouponId(couponId);
                serviceBrokerAPI.setCouponPasskey(passkey);

                /*
                 * Create an instance of the LabClientSession and fill in
                 */
                labClientSession = new LabClientSession();
                labClientSession.setServiceBrokerAPI(serviceBrokerAPI);
                labClientSession.setMultiSubmit(configProperties.isMultiSubmit());
                labClientSession.setFeedbackEmailUrl(configProperties.getFeedbackEmail());

                /*
                 * Set LabClientSession information in the session for access by the web pages
                 */
                httpSession.setAttribute(Consts.STRSSN_LabClient, labClientSession);

                Logfile.Write(Level.CONFIG, String.format(STRLOG_RequestParams_arg5,
                        serviceBrokerAPI.getCouponId(), serviceBrokerAPI.getCouponPasskey(),
                        configProperties.getServiceUrl(), configProperties.getLabServerId(), configProperties.isMultiSubmit()));

                /*
                 * Get the lab status
                 */
                Logfile.Write(logLevel, STRLOG_GettingLabStatus);
                LabStatus labStatus = serviceBrokerAPI.GetLabStatus();
                Logfile.Write(logLevel, String.format(STRLOG_LabStatus_arg2, labStatus.isOnline(), labStatus.getLabStatusMessage()));

                /*
                 * Get information from the lab configuration xml file
                 */
                String xmlLabConfiguration = serviceBrokerAPI.GetLabConfiguration();
                labClientSession.ParseLabConfiguration(xmlLabConfiguration);

            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
            }
        }

        /*
         * Go to the LabClient's home page
         */
        response.sendRedirect(getServletContext().getContextPath() + Consts.STRURL_Faces + Consts.STRURL_Home);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
