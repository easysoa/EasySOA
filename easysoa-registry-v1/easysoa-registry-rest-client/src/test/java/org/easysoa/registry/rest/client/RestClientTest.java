package org.easysoa.registry.rest.client;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.test.AbstractWebEngineTest;
import org.easysoa.registry.types.Service;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.google.inject.Inject;
import com.sun.jersey.api.client.WebResource;

@Deploy("org.easysoa.registry.rest.server")
@RepositoryConfig(cleanup = Granularity.METHOD)
public class RestClientTest extends AbstractWebEngineTest {
    
    @Inject
    private CoreSession documentManager;
    
    @Inject
    private DocumentService documentService;

    private WebResource registryApiClient;
    
    public RestClientTest() {
        registryApiClient = new ClientBuilder().constructClient().resource(NUXEO_URL).path("easysoa/registry");
    }
    
    @Test
    public void testClientCreation() throws Exception {
        // Create some document
        SoaNodeId myServiceId = new SoaNodeId(Service.DOCTYPE, "MyService");
        documentService.create(documentManager, myServiceId, myServiceId.getName());
        documentManager.save();
        
        // Fetch it through the REST client
        
        RegistryApi registryApi = WebResourceFactory.newResource(RegistryApi.class, registryApiClient
                .path(myServiceId.getType())
                .path(myServiceId.getName()));
        Assert.assertNotNull("RegistryApi instanciation must be successful", registryApi);
        SoaNodeInformation foundSoaNode = registryApi.get();
        Assert.assertNotNull("Created SoaNode must have been found by the client", foundSoaNode);
        Assert.assertEquals("Found document must be the expected Service", myServiceId, foundSoaNode.getId());
    }

}
