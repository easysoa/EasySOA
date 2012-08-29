package org.easysoa.registry.indicators.rest;

import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.AbstractRestApiTest;
import org.easysoa.registry.test.RepositoryLogger;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.Service;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource.Builder;

@Deploy("org.easysoa.registry.rest.server")
@RepositoryConfig(cleanup = Granularity.CLASS)
public class IndicatorsControllerTest extends AbstractRestApiTest {

    private static Logger logger = Logger.getLogger(IndicatorsControllerTest.class);
    
    @Inject
    DiscoveryService discoveryService;

    @Inject
    DocumentService documentService;

    private final int SERVICE_COUNT = 5;

    @Test
    public void testIndicators() throws Exception {
        logTestName(logger);
        
        // Fill repository for all tests
        for (int i = 0; i < SERVICE_COUNT; i++) {
            discoveryService.runDiscovery(documentManager, new SoaNodeId(Service.DOCTYPE,
                    "MyService" + i), null, null);
        }
        SoaNodeId service0Id = new SoaNodeId(Service.DOCTYPE, "MyService0");
        SoaNodeId endpointId = new SoaNodeId(Endpoint.DOCTYPE, "MyEndpoint");
        discoveryService.runDiscovery(documentManager, endpointId, null, Arrays.asList(service0Id));
        documentManager.save();
        
        logRepository();

        // Fetch indicators page
        Client client = createAuthenticatedHTTPClient();
        Builder indicatorsReq = client.resource(this.getURL(IndicatorsController.class))
                .type(MediaType.TEXT_HTML);
        String res = indicatorsReq.get(String.class);

        logger.info(res);

        // Check result
    }
    

    // from AbstractRegistryTest

    protected RepositoryLogger repositoryLogger;
    
    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() {
        repositoryLogger = new RepositoryLogger(documentManager);
    }
    
    //@After
    public void logRepository() {
        repositoryLogger.setTitle(name.getMethodName());
        repositoryLogger.logAllRepository();
    }
        
}
