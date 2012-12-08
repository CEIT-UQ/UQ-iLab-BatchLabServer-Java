/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine;

import java.util.logging.Level;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.database.ExperimentQueueDB;
import uq.ilabs.library.labserver.database.ExperimentResultsDB;
import uq.ilabs.library.labserver.database.ExperimentStatisticsDB;
import uq.ilabs.library.labserver.database.ServiceBrokersDB;
import uq.ilabs.library.labserver.engine.types.LabEquipmentServiceInfo;

/**
 *
 * @author uqlpayne
 */
public class LabManagement {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = LabManagement.class.getName();
    private static final Level logLevel = Level.CONFIG;
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_LabServerGuid_arg = "LabServerGuid: %s";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private ConfigProperties configProperties;
    private LabConfiguration labConfiguration;
    private ServiceBrokersDB serviceBrokersDB;
    private ExperimentQueueDB experimentQueueDB;
    private ExperimentResultsDB experimentResultsDB;
    private ExperimentStatisticsDB experimentStatisticsDB;
    private WaitNotify signalSubmitted;
    private int farmSize;
    private LabEquipmentServiceInfo[] labEquipmentServiceInfo;

    public ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public LabConfiguration getLabConfiguration() {
        return labConfiguration;
    }

    public ServiceBrokersDB getServiceBrokersDB() {
        return serviceBrokersDB;
    }

    public ExperimentQueueDB getExperimentQueueDB() {
        return experimentQueueDB;
    }

    public ExperimentResultsDB getExperimentResultsDB() {
        return experimentResultsDB;
    }

    public ExperimentStatisticsDB getExperimentStatisticsDB() {
        return experimentStatisticsDB;
    }

    public WaitNotify getSignalSubmitted() {
        return signalSubmitted;
    }

    public int getFarmSize() {
        return farmSize;
    }

    public LabEquipmentServiceInfo[] getLabEquipmentServiceInfo() {
        return labEquipmentServiceInfo;
    }
    //</editor-fold>

    /**
     *
     * @param labConfiguration
     */
    public LabManagement(ConfigProperties configProperties, LabConfiguration labConfiguration) throws Exception {
        final String methodName = "LabManagement";
        Logfile.WriteCalled(logLevel, STR_ClassName, methodName);

        try {
            /*
             * Check that parameters are valid
             */
            if (configProperties == null) {
                throw new NullPointerException(ConfigProperties.class.getSimpleName());
            }
            if (labConfiguration == null) {
                throw new NullPointerException(LabConfiguration.class.getSimpleName());
            }

            /*
             * Save to local variables
             */
            this.configProperties = configProperties;
            this.labConfiguration = labConfiguration;

            Logfile.Write(logLevel, String.format(STRLOG_LabServerGuid_arg, this.configProperties.getLabServerGuid()));

            /*
             * Create an instance of the database connection
             */
            DBConnection dbConnection = new DBConnection(this.configProperties.getDbDatabase());
            if (dbConnection == null) {
                throw new NullPointerException(DBConnection.class.getSimpleName());
            }
            dbConnection.setHost(this.configProperties.getDbHost());
            dbConnection.setUser(this.configProperties.getDbUser());
            dbConnection.setPassword(this.configProperties.getDbPassword());

            /*
             * Create an instance of ServiceBrokers for authentication and name access
             */
            this.serviceBrokersDB = new ServiceBrokersDB(dbConnection);
            if (this.serviceBrokersDB == null) {
                throw new NullPointerException(ServiceBrokersDB.class.getSimpleName());
            }
            this.serviceBrokersDB.setAuthenticating(this.configProperties.isAuthenticating());
            this.serviceBrokersDB.setLogAuthentication(this.configProperties.isLogAuthentication());

            /*
             * Initialise local variables
             */
            this.experimentQueueDB = new ExperimentQueueDB(dbConnection);
            if (this.experimentQueueDB == null) {
                throw new NullPointerException(ExperimentQueueDB.class.getSimpleName());
            }

            this.experimentResultsDB = new ExperimentResultsDB(dbConnection);
            if (this.experimentResultsDB == null) {
                throw new NullPointerException(ExperimentResultsDB.class.getSimpleName());
            }

            this.experimentStatisticsDB = new ExperimentStatisticsDB(dbConnection);
            if (this.experimentStatisticsDB == null) {
                throw new NullPointerException(ExperimentStatisticsDB.class.getSimpleName());
            }

            this.signalSubmitted = new WaitNotify();
            if (this.signalSubmitted == null) {
                throw new NullPointerException(WaitNotify.class.getSimpleName());
            }

            /*
             * Get the lab equipment service information for the farm units
             */
            this.farmSize = configProperties.getFarmSize();
            this.labEquipmentServiceInfo = configProperties.getLabEquipmentServiceInfo();
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }
}
