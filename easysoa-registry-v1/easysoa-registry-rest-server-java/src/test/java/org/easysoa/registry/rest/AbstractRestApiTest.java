package org.easysoa.registry.rest;

import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;
import org.easysoa.registry.test.AbstractWebEngineTest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public abstract class AbstractRestApiTest extends AbstractWebEngineTest {

    private final ClientConfig clientConfig;
    
    public AbstractRestApiTest() {
        this.clientConfig = new DefaultClientConfig();
        clientConfig.getSingletons().add(new JsonMessageReader());
        clientConfig.getSingletons().add(new JsonMessageWriter());
    }
    
    public Client createAuthenticatedHTTPClient() {
        return createAuthenticatedHTTPClient("Administrator", "Administrator");
    }
    
    public Client createAuthenticatedHTTPClient(String username, String password) {
        Client client = Client.create(this.clientConfig);
        client.addFilter(new HTTPBasicAuthFilter(username, password));
        return client;
    }
    
    
}
