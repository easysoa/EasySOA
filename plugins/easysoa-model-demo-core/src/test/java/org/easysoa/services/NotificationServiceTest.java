package org.easysoa.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.test.EasySOAFeature;
import org.easysoa.test.EasySOARepositoryInit;
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
    
    @Inject NotificationService notifService;
    
    @Inject DocumentService docService;
    
    @Inject CoreSession session;

    @Before
    public void setUp() throws Exception {
  	  	assertNotNull("Cannot get notification service component", notifService);
  	  	assertNotNull("Cannot get document service component", docService);
    }
    
    @Test
    public void testAppliImplCreation() throws Exception {
    	
    	String appliUrl = "http://www.myapp.com/services", title = "myApp";
    	
    	// Create Appli Impl.
    	Map<String, String> properties = new HashMap<String, String>();
    	properties.put("title", title);
    	properties.put(AppliImpl.PROP_URL, appliUrl);
    	notifService.notifyAppliImpl(session, properties);
    	
    	// Check creation
    	DocumentModel doc = docService.findAppliImpl(session, appliUrl);
    	assertNotNull(doc);
    	assertEquals(title, (String) doc.getPropertyValue("dc:title"));
    	
    }

}