package org.easysoa.registry.rest;

import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.utils.DiscoveryApiHelper;
import org.easysoa.registry.test.AbstractWebEngineTest;
import org.easysoa.registry.types.Service;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;

import com.google.inject.Inject;

@RepositoryConfig(cleanup = Granularity.CLASS)
public class RegistryApiTest extends AbstractWebEngineTest {

    private static Logger logger = Logger.getLogger(RegistryApiTest.class);
    
    private DiscoveryApiHelper discoveryApi = new DiscoveryApiHelper(this);  
    
    @Inject
    DiscoveryService discoveryService;

    private final int SERVICE_COUNT = 5;

    @Test
    public void getOne() throws Exception {
        // Fill repository for all tests
        for (int i = 0; i < SERVICE_COUNT; i++) {
            discoveryService.importDiscovery(documentManager,
                    new SoaNodeId(Service.DOCTYPE, "MyService" + i), 
                    new HashMap<String, String>(),
                    null);
        }
        documentManager.save();
        
        // Fetch one service
        HttpClient client = createAuthenticatedHTTPClient();
        GetMethod discoveryRequest = new GetMethod(discoveryApi.getServiceURL(Service.DOCTYPE, "MyService3"));
        client.executeMethod(discoveryRequest);
        JSONObject result = getResultBodyAsJSONObject(discoveryRequest);
        
        logger.info(result.toString(2));
        
        // Check result
        Assert.assertEquals("'doctype' property must be provided for the document", result.get("doctype"), "Service");
        Assert.assertEquals("'name' property must be provided for the document", result.get("name"), "MyService3");
        Assert.assertTrue("Properties must be provided for the document", result.has("properties"));
        Assert.assertEquals("Properties must be listed as a JSON object", result.get("properties").getClass(), JSONObject.class);
        JSONObject properties = (JSONObject) result.get("properties");
        Assert.assertEquals("Valid properties must be returned", properties.get("dc:title"), "MyService3");
    }
    
    @Test
    public void getList() throws Exception {
        
        // Run request
        HttpClient client = createAuthenticatedHTTPClient();
        GetMethod discoveryRequest = new GetMethod(discoveryApi.getServiceURL(Service.DOCTYPE));
        client.executeMethod(discoveryRequest);
        JSONArray result = getResultBodyAsJSONArray(discoveryRequest);
        
        logger.info(result.toString(2));
        
        // Check result
        Assert.assertEquals("All registered services must be found", result.size(), SERVICE_COUNT);
        Assert.assertEquals("Services must be listed as JSON objects", result.get(0).getClass(), JSONObject.class);
        JSONObject firstService = (JSONObject) result.get(0);
        Assert.assertEquals("'doctype' property must be provided for each document", firstService.get("doctype"), "Service");
        Assert.assertEquals("'name' property must be provided for each document", firstService.get("name"), "MyService0");
    }
    
}
