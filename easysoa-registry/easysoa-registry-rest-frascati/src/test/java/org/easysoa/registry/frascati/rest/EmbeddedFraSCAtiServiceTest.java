/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.registry.frascati.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.easysoa.EasySOAConstants;
import org.easysoa.registry.frascati.NxFraSCAtiRegistryService;
import org.easysoa.test.EasySOACoreFeature;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.server.AutomationServer;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.app.WebEngineModule;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;
import com.openwide.easysoa.nuxeo.registration.NuxeoRegistrationService;

/**
 * Tests easysoa-registry-rest (on top of REST Nuxeo) with HTTP monitoring (on Nuxeo-embedded FraSCAti)
 * @author Marc Dutoo
 *
 * TODO MDU jetty, binding
 *
 */

@RunWith(FeaturesRunner.class)
@Features({EasySOACoreFeature.class, WebEngineFeature.class})
@Deploy({
	"org.easysoa.registry.rest.frascati",
	"org.easysoa.registry.frascati", // deployed auto by dep
	"org.easysoa.registry.rest", // deployed auto by dep NOO ?!!
	///"org.nuxeo.ecm.webengine.jaxrs:OSGI-INF/servlet-registry.xml",
	"org.nuxeo.ecm.automation.core",
	///"org.nuxeo.ecm.automation.core:OSGI-INF/AutomationService.xml",
	"org.nuxeo.ecm.automation.features",
	///"org.nuxeo.ecm.automation.features:OSGI-INF/operations-contrib.xml",
	"org.nuxeo.ecm.automation.jsf",
	"org.nuxeo.ecm.automation.server",
	"org.nuxeo.ecm.platform.webengine.sites",
	"org.nuxeo.ecm.platform.webengine.sites.core.contrib",
	"org.nuxeo.theme.core",
	"org.nuxeo.theme.fragments",
	"org.nuxeo.theme.webengine",
	///"org.nuxeo.theme.webengine:OSGI-INF/nxthemes-webengine-contrib.xml",
	//"org.nuxeo.ecm.actions",
	////"org.nuxeo.ecm.actions:OSGI-INF/actions-framework.xml",
	/////"org.nuxeo.ecm.platform.actions",
    /////"org.nuxeo.ecm.platform.actions.relations.web",
	"org.nuxeo.ecm.webapp.ui", // not required, gets the ui anyway ?!
	"org.nuxeo.ecm.platform.search",
	"org.nuxeo.ecm.relations.jena",
	"org.nuxeo.ecm.relations",
	//"org.nuxeo.ecm.platform.comment",
	"org.nuxeo.ecm.platform.comment.core",
	"org.nuxeo.ecm.platform.ui",
	"org.nuxeo.ecm.platform.login.default",
	"org.nuxeo.ecm.platform.login",
	"org.nuxeo.ecm.webapp.ui", // requires rights
	"org.nuxeo.ecm.platform.ui.api", // requires rights
	"org.nuxeo.ecm.platform"
	////"org.nuxeo.ecm.actions:OSGI-INF/org.nuxeo.ecm.platform.actions.ActionService.xml"
	//"org.nuxeo.ecm.automation.server:OSGI-INF/auth-contrib.xml"
	//"org.nuxeo.ecm.automation.server:OSGI-INF/AutomationServer.xml"
	//"org.nuxeo.ecm.automation.features:OSGI-INF/batchmanager-framework.xml"
})
@LocalDeploy({
	/////"org.nuxeo.ecm.platform.webengine.sites.core.contrib:OSGI-INF/webengine-sites-relation-contrib.xml",
	"org.nuxeo.ecm.relations.jena:OSGI-INF/nxrelations-test-jena-bundle.xml",
	"org.easysoa.registry.rest:OSGI-INF/login-contrib.xml"
})
//@Jetty(config="test_jetty.xml", port=EasySOAConstants.NUXEO_TEST_PORT)
@Jetty(config="src/test/resources/jetty.xml", port=EasySOAConstants.NUXEO_TEST_PORT)
//@RepositoryConfig(type=BackendType.H2)
public class EmbeddedFraSCAtiServiceTest
{

    static final Log log = LogFactory.getLog(EmbeddedFraSCAtiServiceTest.class);
    
    @Inject NxFraSCAtiRegistryService frascatiService;
    @Inject AutomationService automationService;
    @Inject AutomationServer automationServer;
    @Inject WebEngine webEngine;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        //assertNotNull(frascatiService.getComposite("httpDiscoveryProxy"));
    }
    
    @Before
    public void setUp() throws Exception
    {
  	  	//assertNotNull("FraSCAti service component unavailable", frascatiService);
        //assertNotNull(frascatiService.getComposite("httpDiscoveryProxy"));
    }
    
    /**
     * Test
     * @throws Exception
     */
    @Test
    @Ignore
    public void test() throws Exception
    {
        testNuxeo(); // tests REST nuxeo
        testHttpMonitoring(); // starts monitoring
  	  	testInfiniteLoopDetection(); // monitoring basic test
    }
    
    /**
     * Test
     * @throws Exception
     */
    //@Test
    public void testNuxeo() throws Exception
    {
        assertNotNull(automationService);
        assertNotNull(automationServer);
        assertNotNull(Framework.getService(AutomationService.class));
        assertNotNull(webEngine);
        WebEngineModule[] modules = webEngine.getApplications();
        for (WebEngineModule module : modules) {
            System.out.println(" module " + module.getId());
        }
        webEngine.reload();
        webEngine.reloadModules();
        //webEngine.addApplication(app);

        starter();
        cleanNuxeoRegistery(); // tests REST nuxeo
    }
    
    /**
     * Test
     * @throws Exception
     */
    @Test
    @Ignore
    public void starter() throws Exception
    {
    	System.in.read();
    }
    
    /**
     * Test start HTTP monitoring
     * WARNING don't forget to use the right registry properties in httpDiscoveryProxy.properties !
     * @throws Exception
     */
    //@Test
    public void testHttpMonitoring() throws Exception
    {
        assertNotNull(frascatiService.getComposite("httpDiscoveryProxy"));
    }
  
    
    /**
     * Test the infinite loop detection feature
     * @throws Exception
     */
    //@Test
    public final void testInfiniteLoopDetection() throws Exception {
		log.info("Test Infinite loop detection started !");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		
		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);				
		
		// Send a request to the proxy itself 
		try{
			httpProxyClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT + "/"), responseHandler);
		} 
		catch(HttpResponseException ex){
			assertEquals(500, ex.getStatusCode());
			log.debug(ex);
		}
		log.info("Test Infinite loop detection end !");
    }
    
   	/**
   	 * Clean Nuxeo registery before to launch the tests
   	 * @throws JSONException 
   	 */
    //@Test
   	public static String cleanNuxeoRegistery() throws IOException, JSONException  {
		log.info("cleanNuxeoRegistery...");
   		// Not possible NXQL to select only one field, only select * is available ..
   		String nuxeoQuery = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted' AND (ecm:primaryType = 'Service' OR ecm:primaryType = 'ServiceAPI' OR ecm:primaryType = 'Workspace')";
   		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
   		String nuxeoResponse = nrs.sendQuery(nuxeoQuery);
   		// For each documents returned by the query, call the delete method
   		// Need to parse the complete JSON response to find all the document uid to delete
   		String entries = new JSONObject(nuxeoResponse).getString("entries");
   		JSONArray entriesArray = new JSONArray(entries);
   		for(int i =0; i < entriesArray.length(); i++){
   			JSONObject entry = entriesArray.getJSONObject(i);
   			String uid = entry.getString("uid");
   			log.info("Deleting document with uid = " + uid);
   			if(nrs.deleteQuery(uid)){
   				log.info("Document doc:" + uid + " deleted successfully");
   			} else {
   				log.info("Unable to delete document doc:" + uid);
   			}
   		}
   		
   		// check that docs are well deleted
   		String res = nrs.sendQuery(nuxeoQuery);
		log.info("cleanNuxeoRegistery :\n" + res);
   		return res;
   	}

}
