/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.servicebroker;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.servlet.ServletContext;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.LabServerAPI;
import uq.ilabs.servicebroker.database.ExperimentsDB;
import uq.ilabs.servicebroker.engine.ConfigProperties;
import uq.ilabs.servicebroker.engine.LabConsts;
import uq.ilabs.servicebroker.engine.types.LabServerInfo;

/**
 *
 * @author uqlpayne
 */
@Stateless
public class ServiceBrokerBean {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokerBean.class.getName();
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_LoggingLevel_arg = "LoggingLevel: %s";
    private static final String STRLOG_ServiceBrokerGuid_arg = "ServiceBrokerGuid: %s";
    private static final String STRLOG_NextExperimentId_arg = "Next ExperimentId: %d";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_LabServerUnknown_arg = "LabServer Unknown: %s";
    /*
     * String constants
     */
    public static final String STR_UserGroup = "DummyServiceBroker";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    private static HashMap<String, LabServerAPI> mapLabServerAPI;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static boolean initialised = false;
    private static ConfigProperties configProperties;
    private static ExperimentsDB experimentsDB;

    public static boolean isInitialised() {
        return initialised;
    }

    public static ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public static ExperimentsDB getExperimentsDB() {
        return experimentsDB;
    }
    //</editor-fold>

    /**
     *
     * @param servletContext
     */
    public static void Initialise(ServletContext servletContext) {
        final String methodName = "Initialise";

        try {
            /*
             * Get the path for the logfiles and logging level
             */
            String logFilesPath = servletContext.getInitParameter(LabConsts.STRPRM_LogFilesPath);
            logFilesPath = servletContext.getRealPath(logFilesPath);
            String logLevel = servletContext.getInitParameter(LabConsts.STRPRM_LogLevel);

            /*
             * Create an instance of the logger and set the logging level
             */
            Logger logger = Logfile.CreateLogger(logFilesPath);
            Level level = Level.INFO;
            try {
                level = Level.parse(logLevel);
            } catch (Exception ex) {
            }
            logger.setLevel(level);

            Logfile.WriteCalled(STR_ClassName, methodName,
                    String.format(STRLOG_LoggingLevel_arg, logger.getLevel().toString()));

            /*
             * Get configuration properties from the file
             */
            String xmlConfigPropertiesPath = servletContext.getInitParameter(LabConsts.STRPRM_XmlConfigPropertiesPath);
            ServiceBrokerBean.configProperties = new ConfigProperties(servletContext.getRealPath(xmlConfigPropertiesPath));

            Logfile.Write(String.format(STRLOG_ServiceBrokerGuid_arg, ServiceBrokerBean.configProperties.getServiceBrokerGuid()));

            /*
             * Create instance of Experiments database API
             */
            DBConnection dbConnection = ServiceBrokerBean.configProperties.getDbConnection();
            ServiceBrokerBean.experimentsDB = new ExperimentsDB(dbConnection);

            /*
             * Get the next experiment Id from the experiment database
             */
            int nextExperimentId = ServiceBrokerBean.experimentsDB.GetNextExperimentId();
            Logfile.Write(String.format(STRLOG_NextExperimentId_arg, nextExperimentId));

            /*
             * Initialisation complete
             */
            ServiceBrokerBean.initialised = true;

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }

    /**
     *
     * @param labServerGuid
     * @return LabServerAPI
     * @throws Exception
     */
    public static LabServerAPI GetLabServerAPI(String labServerGuid) throws Exception {
        LabServerAPI labServerAPI;

        /*
         * Check if instance of LabServerAPI HashMap has been created
         */
        if (ServiceBrokerBean.mapLabServerAPI == null) {
            ServiceBrokerBean.mapLabServerAPI = new HashMap<>();
        }

        /*
         * Check if the BatchLabServerAPI for this labServerGuid already exists
         */
        if ((labServerAPI = ServiceBrokerBean.mapLabServerAPI.get(labServerGuid)) == null) {
            /*
             * Get LabServer information
             */
            LabServerInfo labServerInfo = ServiceBrokerBean.configProperties.GetLabServerInfo(labServerGuid);
            if (labServerInfo == null) {
                throw new RuntimeException(String.format(STRERR_LabServerUnknown_arg, labServerGuid));
            }

            /*
             * Create an instance of LabServerAPI for this LabServer
             */
            labServerAPI = new LabServerAPI(labServerInfo.getServiceUrl());
            labServerAPI.setIdentifier(ServiceBrokerBean.configProperties.getServiceBrokerGuid());
            labServerAPI.setPasskey(labServerInfo.getOutgoingPasskey());

            /*
             * Add the LabServerAPI to the map for next time
             */
            ServiceBrokerBean.mapLabServerAPI.put(labServerGuid, labServerAPI);
        }

        return labServerAPI;
    }

    /**
     *
     */
    public static void Close() {
        final String methodName = "Close";
        Logfile.WriteCalled(Level.INFO, STR_ClassName, methodName);

        if (ServiceBrokerBean.initialised == true) {
            /*
             * Close the logfile
             */
            Logfile.CloseLogger();

            ServiceBrokerBean.initialised = false;
        }

        Logfile.WriteCompleted(Level.INFO, STR_ClassName, methodName);
    }
}
