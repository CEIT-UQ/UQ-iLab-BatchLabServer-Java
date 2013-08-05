/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.servicebroker;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

/**
 * Jersey REST client generated for REST resource:ServiceBrokerRestResource [ServiceBrokerRest]<br>
 * USAGE:
 * <pre>
 *        ServiceBrokerRestClient client = new ServiceBrokerRestClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author uqlpayne
 */
public class ServiceBrokerRestClient {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8080/DummyServiceBrokerRest";

    public ServiceBrokerRestClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("ServiceBrokerRest");
    }

    public void putNotify(String experimentId) throws ClientErrorException {
        webTarget.path(java.text.MessageFormat.format("Notify/{0}", new Object[]{experimentId})).request().put(null);
    }

    public String getLabInfo(String labServerId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("LabInfo/{0}", new Object[]{labServerId}));
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    public String getEffectiveQueueLength(String labServerId, String priorityHint) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("EffectiveQueueLength/{0}/{1}", new Object[]{labServerId, priorityHint}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(String.class);
    }

    public String postValidate(Object requestEntity, String labServerId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("Validate/{0}", new Object[]{labServerId})).request(javax.ws.rs.core.MediaType.APPLICATION_XML).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), String.class);
    }

    public String getLabStatus(String labServerId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("LabStatus/{0}", new Object[]{labServerId}));
        Builder builder = resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML);
        builder = builder.header("couponID", 12345);
        builder = builder.header("couponPassKey", "qwerty");
        return builder.get(String.class);
    }

    public String getExperimentStatus(String experimentId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("ExperimentStatus/{0}", new Object[]{experimentId}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(String.class);
    }

    public String postSubmit(Object requestEntity, String labServerId, String priorityHint, String emailNotification) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("Submit/{0}/{1}/{2}", new Object[]{labServerId, priorityHint, emailNotification})).request(javax.ws.rs.core.MediaType.APPLICATION_XML).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), String.class);
    }

    public String getRetrieveResult(String experimentId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("RetrieveResult/{0}", new Object[]{experimentId}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(String.class);
    }

    public String getLabConfiguration(String labServerId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("LabConfiguration/{0}", new Object[]{labServerId}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(String.class);
    }

    public String putCancel(String experimentId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("Cancel/{0}", new Object[]{experimentId})).request().put(null, String.class);
    }

    public void close() {
        client.close();
    }
}
