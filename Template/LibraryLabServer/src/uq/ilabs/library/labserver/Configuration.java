/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver;

import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.engine.LabConfiguration;

/**
 *
 * @author uqlpayne
 */
public class Configuration extends LabConfiguration {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = Configuration.class.getName();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    //</editor-fold>

    /**
     * 
     * @param xmlLabConfiguration
     * @throws Exception 
     */
    public Configuration(String xmlLabConfiguration) throws Exception {
        this(null, null, xmlLabConfiguration);
    }

    /**
     * 
     * @param filepath
     * @param filename
     * @throws Exception 
     */
    public Configuration(String filepath, String filename) throws Exception {
        this(filepath, filename, null);
    }

    /**
     * 
     * @param filepath
     * @param filename
     * @param xmlLabConfiguration
     * @throws Exception 
     */
    private Configuration(String filepath, String filename, String xmlLabConfiguration) throws Exception {
        super(filepath, filename, xmlLabConfiguration);

        final String methodName = "Configuration";
        Logfile.WriteCalled(STR_ClassName, methodName);

        try {
            /*
             * YOUR CODE HERE
             */
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }
}
