/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.servlet.ServletContext;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.servicebroker.engine.ConfigProperties;
import uq.ilabs.servicebroker.engine.LabConsts;

/**
 *
 * @author uqlpayne
 */
@Singleton
public class ServiceBrokerBean {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokerBean.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_LoggingLevel_arg = "LoggingLevel: %s";
    private static final String STRLOG_ServiceBrokerGuid_arg = "ServiceBrokerGuid: %s";
    /*
     * String constants
     */
    public static final String STR_UserGroup = "DummyServiceBroker";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private ConfigProperties configProperties;
    private ServiceBrokerHandler serviceBrokerHandler;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private boolean initialised = false;

    public boolean isInitialised() {
        return initialised;
    }

    public ServiceBrokerHandler getServiceBrokerHandler() {
        return serviceBrokerHandler;
    }
    //</editor-fold>

    /**
     * Creates a new instance of ServiceBrokerBean
     */
    public ServiceBrokerBean() {
    }

    /**
     *
     * @param servletContext
     */
    public void Initialise(ServletContext servletContext) {
        final String methodName = "Initialise";

        try {
            /*
             * Get the path for the logfiles and logging level
             */
            String logFilesPath = servletContext.getInitParameter(LabConsts.STRPRM_LogFilesPath);
            logFilesPath = servletContext.getRealPath(logFilesPath);
            String strLogLevel = servletContext.getInitParameter(LabConsts.STRPRM_LogLevel);

            /*
             * Create an instance of the logger and set the logging level
             */
            Logger logger = Logfile.CreateLogger(logFilesPath);
            Level level = Level.INFO;
            try {
                level = Level.parse(strLogLevel);
            } catch (Exception ex) {
            }
            logger.setLevel(level);

            Logfile.WriteCalled(STR_ClassName, methodName,
                    String.format(STRLOG_LoggingLevel_arg, logger.getLevel().toString()));

            /*
             * Get configuration properties from the file
             */
            String xmlConfigPropertiesPath = servletContext.getInitParameter(LabConsts.STRPRM_XmlConfigPropertiesPath);
            this.configProperties = new ConfigProperties(servletContext.getRealPath(xmlConfigPropertiesPath));

            Logfile.Write(String.format(STRLOG_ServiceBrokerGuid_arg, this.configProperties.getServiceBrokerGuid()));

            /*
             * Create local variable instances
             */
            this.serviceBrokerHandler = new ServiceBrokerHandler(configProperties);

            /*
             * Initialisation complete
             */
            this.initialised = true;

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
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
             * Deregister the database driver
             */
            this.configProperties.getDbConnection().DeRegister();

            /*
             * Close the logfile
             */
            Logfile.CloseLogger();

            this.initialised = false;
        }

        Logfile.WriteCompleted(Level.INFO, STR_ClassName, methodName);
    }
}
