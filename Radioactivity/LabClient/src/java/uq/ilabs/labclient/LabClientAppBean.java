/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labclient;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.servlet.ServletContext;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labclient.Consts;
import uq.ilabs.library.labclient.engine.ConfigProperties;
import uq.ilabs.library.labclient.engine.LabConsts;
import uq.ilabs.library.labclient.ServiceBrokerAPI;

/**
 *
 * @author uqlpayne
 */
@Singleton
public class LabClientAppBean {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabClientAppBean.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_LoggingLevel_arg = "LoggingLevel: %s";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private boolean initialised = false;
    private ConfigProperties configProperties;
    private ServiceBrokerAPI serviceBrokerAPI;

    public boolean isInitialised() {
        return initialised;
    }

    public ConfigProperties getConfigProperties() {
        return configProperties;
    }
    //</editor-fold>

    public LabClientAppBean() {
    }

    /**
     *
     * @param servletContext
     */
    public synchronized void Initialise(ServletContext servletContext) {
        final String methodName = "Initialise";

        try {
            /*
             * Check if initialisation has been done
             */
            if (this.initialised == false) {
                /*
                 * Get the path for the logfiles and logging level
                 */
                String logFilesPath = servletContext.getInitParameter(LabConsts.STRPRM_LogFilesPath);
                logFilesPath = servletContext.getRealPath(logFilesPath);
                String initLogLevel = servletContext.getInitParameter(LabConsts.STRPRM_LogLevel);

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
                 * Get configuration properties from the file
                 */
                String xmlConfigPropertiesPath = servletContext.getInitParameter(Consts.STRPRM_XmlConfigPropertiesPath);
                this.configProperties = new ConfigProperties(servletContext.getRealPath(xmlConfigPropertiesPath));

                /*
                 * Initialisation complete
                 */
                this.initialised = true;
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     */
    @PreDestroy
    private void preDestroy() {
        final String methodName = "preDestroy";

        /*
         * Prevent from being called more than once
         */
        if (this.initialised == true) {
            Logfile.WriteCalled(Level.INFO, STR_ClassName, methodName);

            /*
             * Close the ServiceBrokerAPI
             */
            if (this.serviceBrokerAPI != null) {
                this.serviceBrokerAPI.Close();
            }

            /*
             * Close the logfile
             */
            Logfile.CloseLogger();

            this.initialised = false;
        }

        Logfile.WriteCompleted(Level.INFO, STR_ClassName, methodName);
    }
}
