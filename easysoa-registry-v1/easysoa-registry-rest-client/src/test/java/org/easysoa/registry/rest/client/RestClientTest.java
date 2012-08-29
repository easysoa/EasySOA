package org.easysoa.registry.rest.client;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.test.AbstractWebEngineTest;
import org.easysoa.registry.types.Service;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

@Deploy("org.easysoa.registry.rest.server")
@RepositoryConfig(cleanup = Granularity.METHOD)
public class RestClientTest extends AbstractWebEngineTest {

    private RegistryApi registryApi;
    
    public RestClientTest() {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.setNuxeoUrl(NUXEO_URL);
        registryApi = clientBuilder.constructRegistryApi();
    }
    
    @Test
    public void testClientCreation() throws Exception {
        Assert.assertNotNull("RegistryApi instanciation must be successful", registryApi);
        
        // Create some document
        SoaNodeId myServiceId = new SoaNodeId(Service.DOCTYPE, "MyService");
        OperationResult result = registryApi.post(new SoaNodeInformation(myServiceId, null, null));
        Assert.assertTrue("Creation must be successful", result.isSuccessful());
        
        // Fetch it
        SoaNodeInformation foundSoaNode = registryApi.get(myServiceId.getType(), myServiceId.getName());
        Assert.assertNotNull("Created SoaNode must have been found by the client", foundSoaNode);
        Assert.assertEquals("Found document must be the expected Service", myServiceId, foundSoaNode.getId());
    }

}
