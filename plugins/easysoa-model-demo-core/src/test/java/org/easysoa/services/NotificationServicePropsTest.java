package org.easysoa.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.Service;
import org.easysoa.test.EasySOAFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * Tests notification service
 * @author mkalam-alami, mdutoo
 *
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOAFeature.class)
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class NotificationServicePropsTest {

    static final Log log = LogFactory.getLog(NotificationServicePropsTest.class);
    
    @Inject NotificationService notifService;
    
    @Inject DocumentService docService;
    
    @Inject CoreSession session;

    @Before
    public void setUp() throws Exception {
  	  	assertNotNull("Cannot get notification service component", notifService);
  	  	assertNotNull("Cannot get document service component", docService);
    }
    
    /**
     * Test callcount incrementation
     * @throws Exception
     */
    @Test
    public void testCallcount() throws Exception {

    	String parentUrl ="http://www.myapp.com/api";
    	String serviceUrl ="http://www.myapp.com/api/service";
    	
    	// Create Service
    	Map<String, String> properties = new HashMap<String, String>();
    	properties.put("title", "My Service");
    	properties.put(Service.PROP_URL, serviceUrl);
    	properties.put(Service.PROP_PARENTURL, parentUrl);
    	properties.put(Service.PROP_CALLCOUNT, "5");
    	notifService.notifyService(session, properties);
    	
    	DocumentModel doc = docService.findService(session, serviceUrl);
    	Assume.assumeNotNull(doc);
    	assertEquals(new Long(5), (Long) doc.getProperty(Service.SCHEMA, Service.PROP_CALLCOUNT));
    	
    	// Increment callcount
    	properties = new HashMap<String, String>();
    	properties.put(Service.PROP_URL, serviceUrl);
    	properties.put(Service.PROP_PARENTURL, parentUrl);
    	properties.put(Service.PROP_CALLCOUNT, "10");
    	notifService.notifyService(session, properties);
    	
    	doc = docService.findService(session, serviceUrl);
    	assertEquals(new Long(15), (Long) doc.getProperty(Service.SCHEMA, Service.PROP_CALLCOUNT));
    	
    }
}