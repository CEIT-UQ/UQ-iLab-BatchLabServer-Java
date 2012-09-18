/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver.client;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.client.Consts;
import uq.ilabs.library.labserver.client.LabServerSession;

/**
 *
 * @author uqlpayne
 */
public class LogoutServlet extends HttpServlet {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LogoutServlet.class.getName();
    //</editor-fold>

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String methodName = "doGet";
        Logfile.WriteCalled(STR_ClassName, methodName);

        /*
         * Get the ServiceBrokerSession information from the session
         */
        HttpSession httpSession = request.getSession();
        LabServerSession labServerSession = (LabServerSession) httpSession.getAttribute(Consts.STRSSN_LabServer);

        /*
         * Remove the user's session
         */
        if (labServerSession != null) {
            labServerSession.setUserSession(null);
        }

        /*
         * Go to the ServiceBroker's home page
         */
        response.sendRedirect(Consts.STRURL_Home);

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }
}
