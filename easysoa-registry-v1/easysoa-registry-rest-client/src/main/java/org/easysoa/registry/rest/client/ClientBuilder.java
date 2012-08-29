package org.easysoa.registry.rest.client;

import java.util.HashSet;
import java.util.Set;

import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class ClientBuilder {
    
    private Set<Object> singletons;
    private String nuxeoUrl = "http://localhost:8080/nuxeo/site/";
    private String username = "Administrator";
    private String password = "Administrator";

    public ClientBuilder() {
        this.singletons = new HashSet<Object>();
        this.singletons.add(new JsonMessageReader());
        this.singletons.add(new JsonMessageWriter());
    }

    public void addSingleton(Object singleton) {
        this.singletons.add(singleton);
    }

    public void setNuxeoUrl(String nuxeoUrl) {
        this.nuxeoUrl = nuxeoUrl;
    }
    
    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public Client constructEasySOAClient() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getSingletons().addAll(this.singletons);
        Client client = Client.create(clientConfig);
        client.addFilter(new HTTPBasicAuthFilter(username, password));
        return client;
    }
    
    public RegistryApi constructRegistryApi() {
        Client client = constructEasySOAClient();
        WebResource registryApiResource = client.resource(this.nuxeoUrl).path("easysoa/registry");
        return WebResourceFactory.newResource(RegistryApi.class, registryApiResource);
    }

}
