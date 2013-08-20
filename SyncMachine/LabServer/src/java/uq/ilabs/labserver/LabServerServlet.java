/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver;

import java.io.IOException;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.client.Consts;
import uq.ilabs.library.labserver.client.LabServerSession;
import uq.ilabs.library.labserver.engine.LabConfiguration;
import uq.ilabs.library.labserver.engine.LabManagement;

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
    private static final String STRLOG_UserHost_arg2 = "UserHost - IP Address: %s  Host Name: %s";
    private static final String STRLOG_TitleVersion_arg2 = "Title: '%s'  Version: '%s'";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    @EJB
    private LabServerAppBean labServerBean;
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

        try {
            /*
             * Initialise the LabServer if not done already
             */
            this.labServerBean.Initialise(request.getServletContext());

            /*
             * Get the LabServerSession information from the session
             */
            HttpSession httpSession = request.getSession();
            LabServerSession labServerSession = (LabServerSession) httpSession.getAttribute(Consts.STRSSN_LabServer);

            /*
             * Check if the LabServer session doesn't yet exist
             */
            if (labServerSession == null) {
                /*
                 * Log the caller's IP address and hostname
                 */
                Logfile.Write(logLevel, String.format(STRLOG_UserHost_arg2, request.getRemoteAddr(), request.getRemoteHost()));

                /*
                 * Create an instance of the LabServerSession ready to fill in
                 */
                labServerSession = new LabServerSession();

                /*
                 * Get LabManagement information and save to the session
                 */
                LabManagement labManagement = this.labServerBean.getLabManagement();
                labServerSession.setLabManagement(labManagement);

                /*
                 * Get information from the LabConfiguration and save to the session
                 */
                LabConfiguration labConfiguration = labManagement.getLabConfiguration();
                labServerSession.setTitle(labConfiguration.getTitle());
                labServerSession.setVersion(labConfiguration.getVersion());
                labServerSession.setNavmenuPhotoUrl(labConfiguration.getNavmenuPhotoUrl());
                labServerSession.setLabCameraUrl(labConfiguration.getLabCameraUrl());
                labServerSession.setLabInfoUrl(labConfiguration.getLabInfoUrl());
                Logfile.Write(String.format(STRLOG_TitleVersion_arg2, labServerSession.getTitle(), labServerSession.getVersion()));

                /*
                 * Set LabServerSession information in the session for access by the web pages
                 */
                httpSession.setAttribute(Consts.STRSSN_LabServer, labServerSession);
            }

            /*
             * Go to home page
             */
            response.sendRedirect(request.getContextPath() + Consts.STRURL_Faces + Consts.STRURL_Home);

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw new ServletException(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
