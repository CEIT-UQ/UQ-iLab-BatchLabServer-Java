/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.labequipment;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.servlet.ServletContext;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labequipment.EquipmentManager;
import uq.ilabs.library.labequipment.engine.ConfigProperties;
import uq.ilabs.library.labequipment.engine.LabConsts;

/**
 *
 * @author uqlpayne
 */
@Singleton
public class LabEquipmentAppBean {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabEquipmentAppBean.class.getName();
    private static final Level logLevel = Level.FINE;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_LoggingLevel_arg = "LoggingLevel: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_EquipmentManagerCreateFailed = "EquipmentManager.Create() failed!";
    private static final String STRERR_EquipmentManagerStartFailed = "EquipmentManager.Start() failed!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private boolean initialised = false;
    private boolean serviceStarted = false;
    private ConfigProperties configProperties;
    private EquipmentManager equipmentManager;
    private LabEquipmentHandler labEquipmentHandler;

    public boolean isInitialised() {
        return initialised;
    }

    public boolean isServiceStarted() {
        return serviceStarted;
    }

    public ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public EquipmentManager getEquipmentManager() {
        return equipmentManager;
    }

    public LabEquipmentHandler getLabEquipmentHandler() {
        return labEquipmentHandler;
    }
    //</editor-fold>

    /**
     * Creates a new instance of LabEquipmentAppBean
     */
    public LabEquipmentAppBean() {
    }

    /**
     * Check if the LabServer has been initialised and if not, initialise the LabServer using the servlet context to
     * retrieve configuration information from the web.xml file.
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

                /*
                 * Get the path to the XML EquipmentConfig file
                 */
                String xmlEquipmentConfigPath = servletContext.getInitParameter(LabConsts.STRPRM_XmlEquipmentConfigPath);
                this.configProperties.setXmlEquipmentConfigPath(servletContext.getRealPath(xmlEquipmentConfigPath));

                /*
                 * Create local variable instances
                 */
                this.labEquipmentHandler = new LabEquipmentHandler(this);

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
     * Check if the LabServer has been started. If not, initialise the LabServer using the servlet context to retrieve
     * configuration information from the web.xml file and then start the service running.
     *
     * @param servletContext
     */
    public synchronized void StartService(ServletContext servletContext) throws Exception {
        final String methodName = "StartService";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check if service has been started
             */
            if (this.serviceStarted == false) {

                /*
                 * Initialise if not done already
                 */
                this.Initialise(servletContext);

                /*
                 * Create an instance of the the EquipmentManager
                 */
                this.equipmentManager = new EquipmentManager(this.configProperties.getXmlEquipmentConfigPath());
                if (this.equipmentManager == null) {
                    throw new NullPointerException(EquipmentManager.class.getSimpleName());
                }
                if (this.equipmentManager.Create() == false) {
                    throw new RuntimeException(STRERR_EquipmentManagerCreateFailed);
                }

                /*
                 * Start the EquipmentManager
                 */
                if (this.equipmentManager.Start() == false) {
                    throw new RuntimeException(STRERR_EquipmentManagerStartFailed);
                }

                /*
                 * Service started
                 */
                this.serviceStarted = true;
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
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
             * Close the Equipment Manager
             */
            if (this.equipmentManager != null) {
                this.equipmentManager.Close();
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
