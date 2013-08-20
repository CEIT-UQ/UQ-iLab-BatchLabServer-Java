/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import uq.ilabs.library.labserver.database.types.ServiceBrokerInfo;
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
    private ExperimentQueueDB experimentQueueDB;
    private ExperimentResultsDB experimentResultsDB;
    private ExperimentStatisticsDB experimentStatisticsDB;
    private LabServerDB labServerDB;
    private ServiceBrokersDB serviceBrokersDB;
    private LabEquipmentDB labEquipmentDB;
    private WaitNotify signalSubmitted;
    private ArrayList<LabEquipmentServiceInfo> labEquipmentServiceInfoList;
    private LabServerInfo labServerInfo;
    private HashMap<String, ServiceBrokerInfo> mapServiceBrokerInfo;

    public ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public DBConnection getDbConnection() {
        return (configProperties != null) ? configProperties.getDbConnection() : null;
    }

    public boolean isAuthenticating() {
        return (configProperties != null) ? configProperties.isAuthenticating() : null;
    }

    public boolean isLogAuthentication() {
        return (configProperties != null) ? configProperties.isLogAuthentication() : null;
    }

    public LabConfiguration getLabConfiguration() {
        return labConfiguration;
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

    public LabServerDB getLabServerDB() {
        return labServerDB;
    }

    public ServiceBrokersDB getServiceBrokersDB() {
        return serviceBrokersDB;
    }

    public LabEquipmentDB getLabEquipmentDB() {
        return labEquipmentDB;
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

    public LabServerInfo getLabServerInfo() {
        return labServerInfo;
    }

    public void setLabServerInfo(LabServerInfo labServerInfo) {
        this.labServerInfo = labServerInfo;
    }

    public HashMap<String, ServiceBrokerInfo> getMapServiceBrokerInfo() {
        return this.GetServiceBrokerInfoMap();
    }

    public void setMapServiceBrokerInfo(HashMap<String, ServiceBrokerInfo> mapServiceBrokerInfo) {
        this.mapServiceBrokerInfo = mapServiceBrokerInfo;
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
             * Create database API class instances
             */
            DBConnection dbConnection = this.configProperties.getDbConnection();
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
            this.labServerDB = new LabServerDB(dbConnection);
            if (this.labServerDB == null) {
                throw new NullPointerException(LabServerDB.class.getSimpleName());
            }
            this.serviceBrokersDB = new ServiceBrokersDB(dbConnection);
            if (this.serviceBrokersDB == null) {
                throw new NullPointerException(ServiceBrokersDB.class.getSimpleName());
            }
            this.labEquipmentDB = new LabEquipmentDB(dbConnection);
            if (this.labEquipmentDB == null) {
                throw new NullPointerException(LabEquipmentDB.class.getSimpleName());
            }

            /*
             * Get the LabServer information
             */
            String[] names = this.labServerDB.GetListOfNames();
            if (names == null || names.length == 0) {
                throw new RuntimeException(STRERR_LabServerNotRegistered);
            }
            this.labServerInfo = this.labServerDB.RetrieveByName(names[0]);
            if (this.labServerInfo == null || this.labServerInfo.getGuid() == null) {
                throw new RuntimeException(STRERR_LabServerNotRegistered);
            }

            this.configProperties.setAuthenticating(this.labServerInfo.isAuthenticate());

            Logfile.Write(logLevel, String.format(STRLOG_LabServerGuid_arg, this.labServerInfo.getGuid()));

            /*
             * Initialise local variables
             */
            this.signalSubmitted = new WaitNotify();
            if (this.signalSubmitted == null) {
                throw new NullPointerException(WaitNotify.class.getSimpleName());
            }

            /*
             * Get the LabEquipment service information
             */
            ArrayList<LabEquipmentInfo> labEquipmentInfoList = this.labEquipmentDB.RetrieveAll();

            /*
             * Get the LabEquipmentServiceInfo for each of the LabEquipment units
             */
            this.labEquipmentServiceInfoList = new ArrayList<>();
            for (int i = 0; i < labEquipmentInfoList.size(); i++) {
                LabEquipmentServiceInfo labEquipmentServiceInfo =
                        new LabEquipmentServiceInfo(this.labServerInfo.getGuid(), labEquipmentInfoList.get(i));
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

    /**
     *
     * @return HashMap of [String, ServiceBrokerInfo>]
     */
    public synchronized HashMap<String, ServiceBrokerInfo> GetServiceBrokerInfoMap() {
        /*
         * Check if the ServiceBrokerInfo map exists yet
         */
        if (this.mapServiceBrokerInfo == null) {
            /*
             * Create and populate ServiceBrokerInfo map
             */
            this.mapServiceBrokerInfo = new HashMap<>();
            ArrayList serviceBrokerInfoList = this.getServiceBrokersDB().RetrieveAll();
            Iterator iterator = serviceBrokerInfoList.iterator();
            while (iterator.hasNext()) {
                ServiceBrokerInfo serviceBrokerInfo = (ServiceBrokerInfo) iterator.next();
                this.mapServiceBrokerInfo.put(serviceBrokerInfo.getGuid(), serviceBrokerInfo);
            }
        }

        return mapServiceBrokerInfo;
    }
}
