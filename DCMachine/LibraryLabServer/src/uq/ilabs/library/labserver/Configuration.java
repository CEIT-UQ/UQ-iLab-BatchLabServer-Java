/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.engine.LabConfiguration;

/**
 *
 * @author uqlpayne
 */
public class Configuration extends LabConfiguration {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = Configuration.class.getName();
    private static final Level logLevel = Level.FINE;
    //</editor-fold>

    /**
     *
     * @param filepath
     * @param filename
     * @throws Exception
     */
    public Configuration(String filepath, String filename) throws Exception {
        super(filepath, filename);

        final String methodName = "Configuration";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Nothing to do here
             */
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
