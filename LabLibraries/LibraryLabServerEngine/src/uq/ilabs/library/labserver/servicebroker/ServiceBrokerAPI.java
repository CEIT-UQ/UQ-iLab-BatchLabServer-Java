/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.servicebroker;

import javax.xml.ws.BindingProvider;
import uq.ilabs.library.lab.utilities.Logfile;
import uq.ilabs.servicebroker.ServiceBrokerService;
import uq.ilabs.servicebroker.ServiceBrokerServiceSoap;

/**
 *
 * @author uqlpayne
 */
public class ServiceBrokerAPI {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = ServiceBrokerAPI.class.getName();
    /*
     * String constants for logfile messages
     */
    private static final String STRLOG_ServiceUrl_arg = "ServiceUrl: '%s'";
    private static final String STRLOG_ExperimentId_arg = " experimentId: %d";
    private static final String STRLOG_Success_arg = "success: %s";
    /*
     * String constants for exception messages
     */
    private static final String STRERR_ServiceUrl = "serviceUrl";
    private static final String STRERR_ServiceBrokerService = "serviceBrokerService";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Variables">
    /*
     * Local variables
     */
    private ServiceBrokerServiceSoap proxyServiceBroker;
    //</editor-fold>

    /**
     *
     * @param serviceUrl
     */
    public ServiceBrokerAPI(String serviceUrl) throws Exception {
        final String methodName = "ServiceBrokerAPI";
        Logfile.WriteCalled(STR_ClassName, methodName,
                String.format(STRLOG_ServiceUrl_arg, serviceUrl));

        try {
            /*
             * Check that parameters are valid
             */
            if (serviceUrl == null) {
                throw new NullPointerException(STRERR_ServiceUrl);
            }
            serviceUrl = serviceUrl.trim();
            if (serviceUrl.isEmpty()) {
                throw new IllegalArgumentException(STRERR_ServiceUrl);
            }

            /*
             * Create a proxy for the ServiceBroker's web service and set the web service URL
             */
            ServiceBrokerService serviceBrokerService = new ServiceBrokerService();
            if (serviceBrokerService == null) {
                throw new NullPointerException(STRERR_ServiceBrokerService);
            }
            this.proxyServiceBroker = serviceBrokerService.getServiceBrokerServiceSoap();
            BindingProvider bp = (BindingProvider) this.proxyServiceBroker;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUrl);
        } catch (NullPointerException | IllegalArgumentException ex) {
            Logfile.WriteError(ex.toString());
            throw ex;
        }

        Logfile.WriteCompleted(STR_ClassName, methodName);
    }

    /**
     *
     * @param experimentID
     */
    public boolean Notify(int experimentId) {
        final String methodName = "Notify";
        Logfile.WriteCalled(STR_ClassName, methodName,
                String.format(STRLOG_ExperimentId_arg, experimentId));

        boolean success = false;

        try {
            this.proxyServiceBroker.notify(experimentId);

            success = true;
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        Logfile.WriteCompleted(STR_ClassName, methodName,
                String.format(STRLOG_Success_arg, success));

        return success;
    }
}
