/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labserver;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.servlet.ServletContext;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.Configuration;
import uq.ilabs.library.labserver.ExperimentManager;
import uq.ilabs.library.labserver.engine.ConfigProperties;
import uq.ilabs.library.labserver.engine.LabConsts;
import uq.ilabs.library.labserver.engine.LabManagement;

/**
 *
 * @author uqlpayne
 */
@Singleton
public class LabServerAppBean {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabServerAppBean.class.getName();
    private static final Level logLevel = Level.INFO;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_LoggingLevel_arg = "LoggingLevel: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ExperimentManagerCreateFailed = "ExperimentManager.Create() Failed!";
    private static final String STRERR_ExperimentManagerStartFailed = "ExperimentManager.Start() Failed!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private boolean initialised = false;
    private boolean serviceStarted = false;
    private LabManagement labManagement;
    private ExperimentManager experimentManager;
    private LabServerHandler labServerHandler;

    public boolean isInitialised() {
        return initialised;
    }

    public boolean isServiceStarted() {
        return serviceStarted;
    }

    public LabManagement getLabManagement() {
        return labManagement;
    }

    public ExperimentManager getExperimentManager() {
        return experimentManager;
    }

    public LabServerHandler getLabServerHandler() {
        return labServerHandler;
    }
    //</editor-fold>

    /**
     * Creates a new instance of LabServerAppBean
     */
    public LabServerAppBean() {
    }

    /**
     * Check if the service has been initialised and if not, initialise the service using the servlet context to
     * retrieve configuration information from the web.xml file.
     *
     * @param servletContext
     */
    public synchronized void Initialise(ServletContext servletContext) throws Exception {
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
                ConfigProperties configProperties = new ConfigProperties(servletContext.getRealPath(xmlConfigPropertiesPath));

                /*
                 * Get the path to the XML LabConfiguration file
                 */
                String xmlLabConfigurationPath = servletContext.getInitParameter(LabConsts.STRPRM_XmlLabConfigurationPath);
                configProperties.setXmlLabConfigurationPath(servletContext.getRealPath(xmlLabConfigurationPath));

                /*
                 * Create local variable instances
                 */
                Configuration configuration = new Configuration(null, configProperties.getXmlLabConfigurationPath());
                this.labManagement = new LabManagement(configProperties, configuration);
                this.labServerHandler = new LabServerHandler(this);

                /*
                 * Initialisation complete
                 */
                this.initialised = true;

                Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
            }
        } catch (Exception ex) {
            Logfile.WriteException(STR_ClassName, methodName, ex);
            throw ex;
        }
    }

    /**
     * Check if the service has been started. If not, initialise the service using the servlet context to retrieve
     * configuration information from the web.xml file and then start the service running.
     *
     * @param servletContext
     */
    public synchronized void StartService(ServletContext servletContext) throws Exception {
        final String methodName = "StartService";

        try {
            /*
             * Check if service has been started
             */
            if (this.serviceStarted == false) {
                Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

                /*
                 * Initialise if not done already
                 */
                this.Initialise(servletContext);

                /*
                 * Create an instance of the the experiment manager
                 */
                this.experimentManager = new ExperimentManager(this.labManagement);
                if (this.experimentManager == null) {
                    throw new NullPointerException(ExperimentManager.class.getSimpleName());
                }
                if (this.experimentManager.Create() == false) {
                    throw new RuntimeException(STRERR_ExperimentManagerCreateFailed);
                }

                /*
                 * Start the ExperimentManager
                 */
                if (this.experimentManager.Start() == false) {
                    throw new RuntimeException(STRERR_ExperimentManagerStartFailed);
                }

                /*
                 * Service started
                 */
                this.serviceStarted = true;

                Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
            }
        } catch (Exception ex) {
            Logfile.WriteException(STR_ClassName, methodName, ex);
            throw ex;
        }
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
             * Close the LabExperiment Manager
             */
            if (this.experimentManager != null) {
                this.experimentManager.Close();
            }

            /*
             * Deregister the database driver
             */
            this.labManagement.getDbConnection().DeRegister();

            /*
             * Close the logfile
             */
            Logfile.CloseLogger();

            this.initialised = false;
        }

        Logfile.WriteCompleted(Level.INFO, STR_ClassName, methodName);
    }
}
