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

package org.easysoa.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.test.rest.RepositoryLogger;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import com.google.inject.Inject;

/**
 * Tests notification service.
 * All unit tests have to be launched together.
 * @author mkalam-alami, mdutoo
 *
 */
public class NotificationServiceTreeTest extends CoreServiceTestHelperBase {

    static final Log log = LogFactory.getLog(NotificationServiceTreeTest.class);
    
    private static final String APPLIIMPL_URL = "http://www.myapp.com/services";
    private static final String APPLIIMPL_TITLE= "My App";
    
    private static final String API_URL = APPLIIMPL_URL+"/crm";
    private static final String API_TITLE = "CRM API";
    private static final String API2_URL = "http://www.external-api.com";
    private static final String API2_TITLE = "External API";
    
    private static final String SERVICE_URL = API_URL+"/clients";
    private static final String SERVICE_TITLE = "Clients service";
    private static final String SERVICE2_URL = API2_URL+"/helloWorld";
    private static final String SERVICE2_TITLE = "Hello world";

    private static CoreSession sessionStatic;
    
    @Inject DiscoveryService notifService;
    
    @Inject DocumentService docService;
    
    @Inject CoreSession session;

    @Before
    public void setUp() {
  	  	assertNotNull("Cannot get notification service component", notifService);
  	  	assertNotNull("Cannot get document service component", docService);
  	  	sessionStatic = session;
    }
    
    /**
     * Test Appli Impl. creation
     * @throws Exception
     */
    @Test
    public void testAppliImplCreation() throws ClientException, MalformedURLException {
    	
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
    public void testServiceAPICreation() throws ClientException, MalformedURLException {

    	Assume.assumeNotNull(docService.findAppliImpl(session, APPLIIMPL_URL));
    	
    	//// In the newly created application
    	
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
    public void testServiceCreation() throws ClientException, MalformedURLException {

    	Assume.assumeNotNull(docService.findServiceApi(session, API_URL));
    	
    	//// In the newly created API
    	
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
    	
    	
    	//// Default behaviour: a new application is created with given URLs
    	String newApiUrl = "http://www.i-dont-exist-too.com/api";
    	
    	// Create service
    	properties = new HashMap<String, String>();
    	properties.put("title", SERVICE2_TITLE);
    	properties.put(Service.PROP_URL, SERVICE2_URL);
    	properties.put(Service.PROP_PARENTURL, newApiUrl);
    	notifService.notifyService(session, properties);

    	// Check creation
    	doc = docService.findService(session, SERVICE2_URL);
    	assertNotNull(doc);
    	assertEquals(SERVICE2_TITLE, (String) doc.getPropertyValue("dc:title"));
    	
    	// Check parent
    	doc = session.getDocument(doc.getParentRef());
    	assertEquals(newApiUrl, (String) doc.getProperty(ServiceAPI.SCHEMA, ServiceAPI.PROP_URL));

    	// Check parent's parent
    	doc = session.getDocument(doc.getParentRef());
    	assertEquals(AppliImpl.DEFAULT_APPLIIMPL_TITLE, (String) doc.getPropertyValue("dc:title"));
    	
    }
    

    @Test
    public void testServiceReferenceCreation() throws ClientException, MalformedURLException {

    	URL referenceUrl = new URL("http://www.webservicex.net/globalweather.asmx?WSDL");
    	String generatedServiceTitle = "globalweather.asmx";

    	// Test connection
    	HttpConnection connection = new HttpConnection(referenceUrl.getHost(), 80);
    	try {
	    	connection.open();
    	}
    	catch (Exception e) {
    		log.error("Cannot execute test, referenced service unreachable", e);
    		Assume.assumeNoException(e);
    	}
    	finally {
    		connection.close();
    	}
    	
    	// Create service reference
    	DocumentModel parentModel = docService.findAppliImpl(session, APPLIIMPL_URL);
    	Assume.assumeNotNull(parentModel);
    	Map<String, String> properties = new HashMap<String, String>();
    	properties.put(ServiceReference.PROP_REFURL, referenceUrl.toString());
    	properties.put(ServiceReference.PROP_ARCHIPATH, referenceUrl.toString());
    	properties.put(ServiceReference.PROP_PARENTURL, 
    			(String) parentModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL));
    	notifService.notifyServiceReference(session, properties);

    	// Check creation
    	DocumentModel doc = docService.findServiceReference(session, referenceUrl.toString());
    	assertNotNull(doc);
    	
    	// Check service generated by the reference (extracted from the WSDL)
    	doc = docService.findService(session, referenceUrl.toString());
    	assertNotNull(doc);
    	assertEquals(generatedServiceTitle, doc.getTitle());
    	
    }
    
    @AfterClass
    public static void tearDown() {
    	new RepositoryLogger(sessionStatic).logAllRepository();
    }
}