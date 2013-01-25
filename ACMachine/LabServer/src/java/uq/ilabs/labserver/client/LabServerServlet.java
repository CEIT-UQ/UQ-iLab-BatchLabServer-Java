/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uq.ilabs.labserver.service.LabServerService;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.client.Consts;
import uq.ilabs.library.labserver.client.LabServerSession;
import uq.ilabs.library.labserver.database.LabServerDB;
import uq.ilabs.library.labserver.database.types.LabServerInfo;
import uq.ilabs.library.labserver.engine.ConfigProperties;
import uq.ilabs.library.labserver.engine.LabConfiguration;

/**
 *
 * @author uqlpayne
 */
public class LabServerServlet extends HttpServlet {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabServerServlet.class.getName();
    private static final Level logLevel = Level.INFO;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_LoggingLevel_arg = "LoggingLevel: %s";
    private static final String STRLOG_UserHost_arg2 = "UserHost - IP Address: %s  Host Name: %s";
    private static final String STRLOG_TitleVersion_arg2 = "Title: '%s'  Version: '%s'";
    //</editor-fold>

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String methodName = "doGet";

        /*
         * Get the LabServerSession information from the session
         */
        HttpSession httpSession = request.getSession();
        LabServerSession labServerSession = (LabServerSession) httpSession.getAttribute(Consts.STRSSN_LabServer);

        /*
         * Check if the LabServer session doesn't yet exist
         */
        if (labServerSession == null) {
            try {
                /*
                 * Check if the logger has already been created by the LabServer service
                 */
                if (LabServerService.isLoggerCreated() == false) {
                    /*
                     * Get the path for the logfiles and logging level
                     */
                    String logFilesPath = getServletContext().getInitParameter(Consts.STRPRM_LogFilesPath);
                    logFilesPath = getServletContext().getRealPath(logFilesPath);
                    String initLogLevel = getServletContext().getInitParameter(Consts.STRPRM_LogLevel);

                    /*
                     * Create an instance of the logger and set the logging level
                     */
                    Logger logger = Logfile.CreateLogger(logFilesPath);
                    LabServerService.setLoggerCreated(true);
                    Level level = Level.INFO;
                    try {
                        level = Level.parse(initLogLevel);
                    } catch (Exception ex) {
                    }
                    logger.setLevel(level);

                    Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                            String.format(STRLOG_LoggingLevel_arg, logger.getLevel().toString()));
                } else {
                    Logfile.WriteCalled(STR_ClassName, methodName);
                }

                /*
                 * Log the caller's IP address and hostname
                 */
                Logfile.Write(logLevel, String.format(STRLOG_UserHost_arg2, request.getRemoteAddr(), request.getRemoteHost()));

                /*
                 * Get configuration properties from the file
                 */
                ServletContext servletContext = getServletContext();
                String xmlConfigPropertiesPath = servletContext.getInitParameter(Consts.STRPRM_XmlConfigPropertiesPath);
                ConfigProperties configProperties = new ConfigProperties(servletContext.getRealPath(xmlConfigPropertiesPath));

                /*
                 * Get the path to the XML LabConfiguration file
                 */
                String xmlLabConfigurationPath = servletContext.getInitParameter(Consts.STRPRM_XmlLabConfigurationPath);
                configProperties.setXmlLabConfigurationPath(servletContext.getRealPath(xmlLabConfigurationPath));

                /*
                 * Create an instance of the LabServerSession ready to fill in
                 */
                labServerSession = new LabServerSession();

                /*
                 * Get information from the LabConfiguration file and save to the session
                 */
                LabConfiguration labConfiguration = new LabConfiguration(null, configProperties.getXmlLabConfigurationPath());
                labServerSession.setTitle(labConfiguration.getTitle());
                labServerSession.setVersion(labConfiguration.getVersion());
                labServerSession.setNavmenuPhotoUrl(labConfiguration.getNavmenuPhotoUrl());
                labServerSession.setLabCameraUrl(labConfiguration.getLabCameraUrl());
                labServerSession.setLabInfoUrl(labConfiguration.getLabInfoUrl());
                Logfile.Write(String.format(STRLOG_TitleVersion_arg2, labServerSession.getTitle(), labServerSession.getVersion()));

                /*
                 * Get information from ConfigProperties and save to the session
                 */
                DBConnection dbConnection = configProperties.getDbConnection();
                labServerSession.setDbConnection(dbConnection);

                /*
                 * Get specific LabServer information and save to the session
                 */
                LabServerDB labServerDB = new LabServerDB(dbConnection);
                LabServerInfo labServerInfo = labServerDB.Retrieve();
                labServerSession.setLabServerInfo(labServerInfo);

                /*
                 * Set LabServerSession information in the session for access by the web pages
                 */
                httpSession.setAttribute(Consts.STRSSN_LabServer, labServerSession);

            } catch (Exception ex) {
                Logfile.WriteError(ex.toString());
            }
        }

        /*
         * Go to the LabServer's home page
         */
        response.sendRedirect(getServletContext().getContextPath() + Consts.STRURL_Faces + Consts.STRURL_Home);

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
