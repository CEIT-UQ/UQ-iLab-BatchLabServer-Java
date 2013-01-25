/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine;

import java.util.ArrayList;
import java.util.logging.Level;
import uq.ilabs.library.lab.database.DBConnection;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.library.labserver.database.ExperimentQueueDB;
import uq.ilabs.library.labserver.database.ExperimentResultsDB;
import uq.ilabs.library.labserver.database.ExperimentStatisticsDB;
import uq.ilabs.library.labserver.database.LabEquipmentDB;
import uq.ilabs.library.labserver.database.LabServerDB;
import uq.ilabs.library.labserver.database.ServiceBrokersDB;
import uq.ilabs.library.labserver.database.types.LabEquipmentInfo;
import uq.ilabs.library.labserver.database.types.LabServerInfo;
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
    /*
     * String constants for exception messages
     */
    private static final String STRERR_LabServerNotRegistered = "LabServer is not registered!";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private ConfigProperties configProperties;
    private LabConfiguration labConfiguration;
    private LabServerInfo labServerInfo;
    private ServiceBrokersDB serviceBrokersDB;
    private ExperimentQueueDB experimentQueueDB;
    private ExperimentResultsDB experimentResultsDB;
    private ExperimentStatisticsDB experimentStatisticsDB;
    private WaitNotify signalSubmitted;
    private ArrayList<LabEquipmentServiceInfo> labEquipmentServiceInfoList;

    public ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public LabConfiguration getLabConfiguration() {
        return labConfiguration;
    }

    public LabServerInfo getLabServerInfo() {
        return labServerInfo;
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
        return (labEquipmentServiceInfoList != null) ? labEquipmentServiceInfoList.size() : 0;
    }

    public ArrayList<LabEquipmentServiceInfo> getLabEquipmentServiceInfoList() {
        return labEquipmentServiceInfoList;
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

            /*
             * Create an instance of LabServerDB
             */
            DBConnection dbConnection = this.configProperties.getDbConnection();
            LabServerDB labServerDB = new LabServerDB(dbConnection);
            if (labServerDB == null) {
                throw new NullPointerException(LabServerDB.class.getSimpleName());
            }

            /*
             * Get the LabServer information
             */
            String[] names = labServerDB.GetListName();
            if (names == null || names.length == 0) {
                throw new RuntimeException(STRERR_LabServerNotRegistered);
            }
            this.labServerInfo = labServerDB.RetrieveByName(names[0]);
            if (this.labServerInfo == null || this.labServerInfo.getGuid() == null) {
                throw new RuntimeException(STRERR_LabServerNotRegistered);
            }

            this.configProperties.setAuthenticating(this.labServerInfo.isAuthenticate());

            Logfile.Write(logLevel, String.format(STRLOG_LabServerGuid_arg, this.labServerInfo.getGuid()));

            /*
             * Create an instance of ServiceBrokersDB for authentication and name access
             */
            this.serviceBrokersDB = new ServiceBrokersDB(dbConnection);
            if (this.serviceBrokersDB == null) {
                throw new NullPointerException(ServiceBrokersDB.class.getSimpleName());
            }

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
             * Get the LabEquipment service information
             */
            LabEquipmentDB labEquipmentDB = new LabEquipmentDB(dbConnection);
            ArrayList<LabEquipmentInfo> labEquipmentInfoList = labEquipmentDB.RetrieveAll();

            /*
             * Get the LabEquipmentServiceInfo for each of the LabEquipment units
             */
            this.labEquipmentServiceInfoList = new ArrayList<>();
            for (int i = 0; i < labEquipmentInfoList.size(); i++) {
                LabEquipmentServiceInfo labEquipmentServiceInfo = new LabEquipmentServiceInfo(this.labServerInfo.getGuid(), labEquipmentInfoList.get(i));
                if (labEquipmentServiceInfo == null) {
                    throw new NullPointerException(LabEquipmentServiceInfo.class.getSimpleName());
                }
                this.labEquipmentServiceInfoList.add(labEquipmentServiceInfo);
            }

        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(logLevel, STR_ClassName, methodName);
    }

    /**
     *
     * @param infoId
     * @return LabEquipmentServiceInfo
     */
    public LabEquipmentServiceInfo GetLabEquipmentServiceInfo(int infoId) {
        LabEquipmentServiceInfo labEquipmentServiceInfo = null;

        if (this.labEquipmentServiceInfoList != null) {
            for (int i = 0; i < this.labEquipmentServiceInfoList.size(); i++) {
                LabEquipmentServiceInfo info = this.labEquipmentServiceInfoList.get(i);
                if (info.getLabEquipmentInfo() != null && info.getLabEquipmentInfo().getId() == infoId) {
                    labEquipmentServiceInfo = info;
                    break;
                }
            }
        }
        return labEquipmentServiceInfo;
    }
}
