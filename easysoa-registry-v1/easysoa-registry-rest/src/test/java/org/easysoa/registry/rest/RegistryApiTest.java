package org.easysoa.registry.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.utils.DiscoveryApiHelper;
import org.easysoa.registry.test.AbstractWebEngineTest;
import org.easysoa.registry.types.Deliverable;
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

    @Inject
    DocumentService documentService;

    private final int SERVICE_COUNT = 5;

    @Test
    public void getOne() throws Exception {
        // Fill repository for all tests
        for (int i = 0; i < SERVICE_COUNT; i++) {
            discoveryService.importDiscovery(documentManager, new SoaNodeId(Service.DOCTYPE,
                    "MyService" + i), new HashMap<String, String>(), null);
        }
        documentManager.save();

        // Fetch one service
        HttpClient client = createAuthenticatedHTTPClient();
        GetMethod discoveryRequest = new GetMethod(discoveryApi.getServiceURL(Service.DOCTYPE,
                "MyService3"));
        client.executeMethod(discoveryRequest);
        JSONObject result = getResultBodyAsJSONObject(discoveryRequest);

        logger.info(result.toString(2));

        // Check result
        Assert.assertEquals("'doctype' property must be provided for the document",
                "Service", result.get("doctype"));
        Assert.assertEquals("'name' property must be provided for the document",
                "MyService3", result.get("name"));
        Assert.assertTrue("Properties must be provided for the document", result.has("properties"));
        Assert.assertEquals("Properties must be listed as a JSON object", 
                JSONObject.class, result.get("properties").getClass() );
        JSONObject properties = (JSONObject) result.get("properties");
        Assert.assertEquals("Valid properties must be returned", 
                "MyService3", properties.get("dc:title"));
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
        Assert.assertEquals("All registered services must be found", SERVICE_COUNT, result.size());
        Assert.assertEquals("Services must be listed as JSON objects", JSONObject.class, result.get(0).getClass());
        JSONObject firstService = (JSONObject) result.get(0);
        Assert.assertEquals("'doctype' property must be provided for each document",
                "Service", firstService.get("doctype"));
        Assert.assertEquals("'name' property must be provided for each document",
                "MyService0", firstService.get("name"));
    }

    @Test
    public void create() throws Exception {
        final SoaNodeId deliverableId = new SoaNodeId(Deliverable.DOCTYPE, "org.easysoa:deliverable");
        
        // Create service
        JSONObject requestBody = new JSONObject();
        requestBody.put("doctype", deliverableId.getType());
        requestBody.put("name", deliverableId.getName());
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("dc:title", "My Deliverable");
        properties.put("del:application", "myapp");
        requestBody.put("properties", properties);
        String requestBodyAsString = requestBody.toString(2);
        logger.info("Request: " + requestBodyAsString);
        
        HttpClient client = createAuthenticatedHTTPClient();
        PostMethod discoveryRequest = new PostMethod(discoveryApi.getServiceURL(deliverableId.getType()));
        discoveryRequest.setRequestEntity(new StringRequestEntity(requestBodyAsString,
                MediaType.APPLICATION_JSON, "UTF-8"));
        client.executeMethod(discoveryRequest);
        JSONObject result = getResultBodyAsJSONObject(discoveryRequest);
        
        logger.info("Response: " + result.toString(2));
        
        // Check result
        documentService.find(documentManager, deliverableId);
        Assert.assertEquals("'doctype' property must be provided for the document", deliverableId.getType(), result.get("doctype"));
        Assert.assertEquals("'name' property must be provided for the document", deliverableId.getName(), result.get("name"));
        Assert.assertTrue("Properties must be provided for the document", result.has("properties"));
        Assert.assertEquals("Properties must be listed as a JSON object", JSONObject.class, result.get("properties").getClass());
        JSONObject resultProperties = (JSONObject) result.get("properties");
        Assert.assertEquals("Valid properties must be returned", properties.get("dc:title"), resultProperties.get("dc:title"));
        Assert.assertEquals("Valid properties must be returned", properties.get("del:application"), resultProperties.get("del:application"));
        
    }
}
