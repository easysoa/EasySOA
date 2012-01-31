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

package org.easysoa.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.ws.rs.core.UriInfo;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.rest.servicefinder.ServiceFinderRest;
import org.easysoa.services.HttpDownloader;
import org.easysoa.services.HttpDownloaderService;
import org.easysoa.test.EasySOACoreFeature;
import org.easysoa.test.StaticWebServer;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

@RunWith(FeaturesRunner.class)
@Features({EasySOACoreFeature.class, WebEngineFeature.class})
@Jetty(config="src/test/resources/jetty.xml", port=EasySOAConstants.NUXEO_TEST_PORT)
@Deploy({"org.easysoa.registry.rest"})
@Ignore
public class ServiceFinderTest {

	private static final String ONLINE_SERVICE_URL = "http://localhost:8222/trip.html";

    private static Logger logger = Logger.getLogger(ServiceFinderTest.class);
    
    private static StaticWebServer webServer;
    
    @BeforeClass
    public static void setUp() throws Exception {
        webServer = new StaticWebServer(8222, "src/test/resources/www");
        webServer.start();
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        if (webServer != null) {
            webServer.stop();
        }
    }
    
    @Test
    public void testServiceFinder() throws Exception {
        
        // Check that the service is available
    	HttpDownloaderService httpDownloaderService = Framework.getService(HttpDownloaderService.class);
        HttpDownloader onlineServiceFile = httpDownloaderService.createHttpDownloader(ONLINE_SERVICE_URL);
        Assume.assumeTrue(onlineServiceFile.isURLAvailable());
        
        // Make request
        ServiceFinderRest serviceFinder = new ServiceFinderRest();
        Object obj = serviceFinder.doGet(mockUriInfo(ONLINE_SERVICE_URL));
        
        // Check result data
        Assert.assertNotNull(obj);
        JSONObject json = new JSONObject(obj.toString());
        logger.info("Service finder response: "+json.toString(2));
        
        JSONObject foundLinks = (JSONObject) json.get("foundLinks");
        Assert.assertNotNull(foundLinks);
        Iterator<?> it = foundLinks.keys();
        
        // Output found links
        while (it.hasNext()) {
            String linkName = (String) it.next();
            logger.info("Found service: "+linkName);
            logger.info(foundLinks.getString(linkName));
        }
    }
    
    @Test
    public void testServiceFinderJSONP() throws Exception {

        // Check that the service is available
    	HttpDownloaderService httpDownloaderService = Framework.getService(HttpDownloaderService.class);
        HttpDownloader onlineServiceFile = httpDownloaderService.createHttpDownloader(ONLINE_SERVICE_URL);
        Assume.assumeTrue(onlineServiceFile.isURLAvailable());

        // Make request
        ServiceFinderRest serviceFinder = new ServiceFinderRest();
        Object obj = serviceFinder.doGet(mockUriInfo(ONLINE_SERVICE_URL + "?callback=mycallback"));
    	
        // Check result data
        Assert.assertNotNull(obj);
        String response = obj.toString();
        logger.info("Service finder response: " + response);
        Assert.assertTrue("Malformated JSONP response", response.startsWith("mycallback(") && response.endsWith(")"));
    	
    }
    
    private UriInfo mockUriInfo(String uri) throws URISyntaxException {
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getRequestUri()).thenReturn(new URI("http://127.0.0.1:8080/nuxeo/site/easysoa/servicefinder/"+uri));
        Mockito.when(uriInfo.getBaseUri()).thenReturn(new URI("http://127.0.0.1:8080/nuxeo/site/"));
        return uriInfo;
    }

}
