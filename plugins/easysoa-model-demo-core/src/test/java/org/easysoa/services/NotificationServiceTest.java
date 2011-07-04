package org.easysoa.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.test.EasySOAFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.easysoa.test.RepositoryLogger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
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
@Deploy({
	"org.easysoa.demo.core:OSGI-INF/DocumentServiceComponent.xml"
})
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class NotificationServiceTest {

    static final Log log = LogFactory.getLog(NotificationServiceTest.class);
    
    private static final String APPLIIMPL_URL = "http://www.myapp.com/services";
    private static final String APPLIIMPL_TITLE= "My App";
    
    private static final String API_URL = APPLIIMPL_URL+"/crm";
    private static final String API_TITLE = "CRM API";
    private static final String API2_URL = "http://www.external-api.com";
    private static final String API2_TITLE = "External API";
    
    private static final String SERVICE_URL = API_URL+"/clients";
    private static final String SERVICE_TITLE = "Clients service";

    private static CoreSession sessionStatic;
    
    @Inject NotificationService notifService;
    
    @Inject DocumentService docService;
    
    @Inject CoreSession session;

    @Before
    public void setUp() throws Exception {
  	  	assertNotNull("Cannot get notification service component", notifService);
  	  	assertNotNull("Cannot get document service component", docService);
  	  	sessionStatic = session;
    }
    
    /**
     * Test Appli Impl. creation
     * @throws Exception
     */
    @Test
    public void testAppliImplCreation() throws Exception {
    	
    	// Create Appli Impl.
    	Map<String, String> properties = new HashMap<String, String>();
    	properties.put("title", APPLIIMPL_TITLE);
    	properties.put(AppliImpl.PROP_URL, APPLIIMPL_URL);
    	notifService.notifyAppliImpl(session, properties);
    	
    	// Check creation
    	DocumentModel doc = docService.findAppliImpl(session, APPLIIMPL_URL);
    	assertNotNull(doc);
    	assertEquals(APPLIIMPL_TITLE, (String) doc.getPropertyValue("dc:title"));
    	
    }
    
    /**
     * Test Service API creation, both in the newly created application
     * and in a new unknown one.
     * @throws Exception
     */
    @Test
    public void testServiceAPICreation() throws Exception {
    	
    	//// In newly created application
    	
    	// Create API
    	Map<String, String> properties = new HashMap<String, String>();
    	properties.put("title", API_TITLE);
    	properties.put(ServiceAPI.PROP_URL, API_URL);
    	properties.put(ServiceAPI.PROP_PARENTURL, APPLIIMPL_URL);
    	notifService.notifyServiceApi(session, properties);
    	
    	// Check creation
    	DocumentModel doc = docService.findServiceApi(session, API_URL);
    	assertNotNull(doc);
    	assertEquals(API_TITLE, (String) doc.getPropertyValue("dc:title"));
    	
    	// Check parent
    	assertEquals(APPLIIMPL_TITLE, (String) session.getDocument(doc.getParentRef())
    			.getPropertyValue("dc:title"));
    	
    	//// Default behaviour: a new application is created with given URLs

    	String newAppliImplUrl = "http://www.i-dont-exist.com";
    	
    	// Create API
    	properties = new HashMap<String, String>();
    	properties.put("title", API2_TITLE);
    	properties.put(ServiceAPI.PROP_URL, API2_URL);
    	properties.put(ServiceAPI.PROP_PARENTURL, newAppliImplUrl);
    	notifService.notifyServiceApi(session, properties);
    	
    	// Check creation
    	doc = docService.findServiceApi(session, API2_URL);
    	assertNotNull(doc);
    	assertEquals(API2_TITLE, (String) doc.getPropertyValue("dc:title"));
    	
    	// Check parent
    	assertEquals(newAppliImplUrl,
    			(String) session.getDocument(doc.getParentRef()).getPropertyValue("dc:title"));
    	assertEquals(newAppliImplUrl,
    			(String) session.getDocument(doc.getParentRef()).getProperty(
    					AppliImpl.SCHEMA, AppliImpl.PROP_URL));
    }

    @Test
    public void testServiceCreation() throws Exception {
    	
    	// Create service
    	Map<String, String> properties = new HashMap<String, String>();
    	properties.put("title", SERVICE_TITLE);
    	properties.put(Service.PROP_URL, SERVICE_URL);
    	properties.put(Service.PROP_PARENTURL, API_URL);
    	notifService.notifyService(session, properties);
    	
    	// Check creation
    	DocumentModel doc = docService.findService(session, SERVICE_URL);
    	assertNotNull(doc);
    	assertEquals(SERVICE_TITLE, (String) doc.getPropertyValue("dc:title"));
    	
    	// Check parent
    	assertEquals(API_TITLE, (String) session.getDocument(doc.getParentRef())
    			.getPropertyValue("dc:title"));
    	
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
    	new RepositoryLogger(sessionStatic).logAllRepository();
    }
}