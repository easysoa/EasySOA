package org.easysoa.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.ws.rs.core.UriInfo;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.EasySOAConstants;
import org.easysoa.rest.servicefinder.ServiceFinderRest;
import org.easysoa.test.EasySOACoreFeature;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.runtime.test.runner.LocalDeploy;

@RunWith(FeaturesRunner.class)
@Features({EasySOACoreFeature.class, WebEngineFeature.class})
@Deploy({
    "org.easysoa.registry.rest"
})
@Jetty(config="src/test/resources/jetty.xml", port=EasySOAConstants.NUXEO_TEST_PORT)
@LocalDeploy({"org.easysoa.registry.rest:OSGI-INF/login-contrib.xml",
    "org.easysoa.registry.rest:OSGI-INF/ServiceFinderComponent.xml",
    "org.easysoa.registry.rest:OSGI-INF/serviceFinders-contrib.xml"})
public class ServiceFindersTest {

    static final Log log = LogFactory.getLog(ServiceFindersTest.class);
    
    @Test
    public void testServiceFinder() throws Exception {
        
        // Make request
        ServiceFinderRest serviceFinder = new ServiceFinderRest();
        Object obj = serviceFinder.doGet(mockUriInfo("http://ec2-79-125-45-33.eu-west-1.compute.amazonaws.com:8080/services/"));
        
        // Check result data
        Assert.assertNotNull(obj);
        JSONObject json = new JSONObject(obj.toString());
        log.info("Service finder response: "+json.toString(2));
        
        JSONObject foundLinks = (JSONObject) json.get("foundLinks");
        Assert.assertNotNull(foundLinks);
        Iterator<?> it = foundLinks.keys();
        
        // Output found links
        while (it.hasNext()) {
            String linkName = (String) it.next();
            log.info("Found service: "+linkName);
            log.info(foundLinks.getString(linkName));
        }
        
    }
    
    private UriInfo mockUriInfo(String uri) throws URISyntaxException {
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getRequestUri()).thenReturn(new URI("http://127.0.0.1:8080/nuxeo/site/easysoa/servicefinder/"+uri));
        Mockito.when(uriInfo.getBaseUri()).thenReturn(new URI("http://127.0.0.1:8080/nuxeo/site/"));
        return uriInfo;
    }

}
