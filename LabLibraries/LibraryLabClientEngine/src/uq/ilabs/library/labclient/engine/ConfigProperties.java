/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labclient.engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import uq.ilabs.library.lab.utilities.Logfile;

/**
 *
 * @author uqlpayne
 */
public class ConfigProperties {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ConfigProperties.class.getName();
    private static final Level logLevel = Level.CONFIG;
    /*
     * String constants for configuration properties
     */
    private static final String STRCFG_ServiceUrl = "ServiceUrl";
    private static final String STRCFG_LabServerId = "LabServerId";
    private static final String STRCFG_MultiSubmit = "MultiSubmit";
    private static final String STRCFG_FeedbackEmail = "FeedbackEmail";
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_Filename_arg = "filename: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_Filename = "filename";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String serviceUrl;
    private String labServerId;
    private boolean multiSubmit;
    private String feedbackEmail;

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getLabServerId() {
        return labServerId;
    }

    public void setLabServerId(String labServerId) {
        this.labServerId = labServerId;
    }

    public boolean isMultiSubmit() {
        return multiSubmit;
    }

    public void setMultiSubmit(boolean multiSubmit) {
        this.multiSubmit = multiSubmit;
    }

    public String getFeedbackEmail() {
        return feedbackEmail;
    }
    //</editor-fold>

    /**
     *
     * @param filename
     * @throws Exception
     */
    public ConfigProperties(String filename) throws Exception {
        final String methodName = "ConfigProperties";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName,
                String.format(STRLOG_Filename_arg, filename));

        try {
            /*
             * Check that parameters are valid
             */
            if (filename == null) {
                throw new NullPointerException(STRERR_Filename);
            }
            if (filename.trim().isEmpty()) {
                throw new IllegalArgumentException(STRERR_Filename);
            }

            /*
             * Load the configuration properties from the specified file
             */
            InputStream inputStream = new FileInputStream(filename);
            if (inputStream == null) {
                throw new NullPointerException(InputStream.class.getSimpleName());
            }
            Properties configProperties = new Properties();
            configProperties.loadFromXML(inputStream);

            /*
             * Get configuration information
             */
            this.serviceUrl = configProperties.getProperty(STRCFG_ServiceUrl);
            this.labServerId = configProperties.getProperty(STRCFG_LabServerId);
            this.feedbackEmail = configProperties.getProperty(STRCFG_FeedbackEmail);
            if (configProperties.getProperty(STRCFG_MultiSubmit) != null) {
                this.multiSubmit = Boolean.parseBoolean(configProperties.getProperty(STRCFG_MultiSubmit));
            }

        } catch (NullPointerException | IllegalArgumentException | IOException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
