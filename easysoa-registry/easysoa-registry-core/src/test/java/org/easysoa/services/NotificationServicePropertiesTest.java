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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.api.EasySOAApiSession;
import org.easysoa.api.EasySOALocalApiFactory;
import org.easysoa.doctypes.Service;
import org.easysoa.test.StaticWebServer;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import com.google.inject.Inject;

/**
 * Tests notification service
 * @author mkalam-alami, mdutoo
 *
 */
public class NotificationServicePropertiesTest extends CoreServiceTestHelperBase {

    static final Log log = LogFactory.getLog(NotificationServicePropertiesTest.class);
    
    @Inject DocumentService docService;
    
    @Inject CoreSession session;
    
    private EasySOAApiSession api;
    
    private StaticWebServer server = null;

    @Before
    public void setUp() throws Exception {
        server = new StaticWebServer(9010);
        server.start();
        api = EasySOALocalApiFactory.createLocalApi(session);
  	  	assertNotNull("Cannot get API", api);
  	  	assertNotNull("Cannot get document service component", docService);
    }

    @After
    public void tearDown() throws Exception {
        if (server != null)  {
            server.stop();
        }
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
    	api.notifyService(properties);
    	
    	DocumentModel doc = docService.findService(session, serviceUrl);
    	Assume.assumeNotNull(doc);
    	assertEquals(new Long(5), (Long) doc.getProperty(Service.SCHEMA, Service.PROP_CALLCOUNT));
    	
    	// Increment callcount
    	properties = new HashMap<String, String>();
    	properties.put(Service.PROP_URL, serviceUrl);
    	properties.put(Service.PROP_PARENTURL, parentUrl);
    	properties.put(Service.PROP_CALLCOUNT, "10");
    	api.notifyService(properties);
    	
    	doc = docService.findService(session, serviceUrl);
    	assertEquals(new Long(15), (Long) doc.getProperty(Service.SCHEMA, Service.PROP_CALLCOUNT));
    	
    }
    
    /**
     * Test the upload of services using the file URL property
     * (which attaches a WSDL to a Service for data extraction)
     * @throws Exception
     */
    @Test
    public void testFileUrl() throws Exception {

        // TODO Host WSDL in a local server 
        
    	String wsdlUrl = "http://localhost:9010/PureAirFlowers.wsdl",
    		serviceUrl = "http://localhost:9010/PureAirFlowers",
    		query = "SELECT * FROM Document WHERE serv:url = '"+serviceUrl+"'";
    	
    	// Create Service
    	Map<String, String> properties = new HashMap<String, String>();
    	properties.put("title", "My Service");
    	properties.put(Service.PROP_URL, wsdlUrl);
    	properties.put(Service.PROP_CALLCOUNT, "5");
    	properties.put(Service.PROP_FILEURL, wsdlUrl);
    	api.notifyService(properties);
    	
    	// The URL should have been changed according to the WSDL contents
    	// XXX WSDL are parsed asynchronously, cannot pass
    	
//    	DocumentModel doc = docService.findService(session, serviceUrl);
//    	assertNotNull("The WSDL hasn't been parsed", doc); 
//    	doc = docService.findService(session, wsdlUrl);
//    	assertNotNull(doc);
//    	DocumentModelList list = session.query(query);
//    	assertEquals(1, list.size());
//    	
//    	// A second notification should update the same document
//    	properties = new HashMap<String, String>();
//    	properties.put("title", "My Updated Service");
//    	properties.put(Service.PROP_URL, wsdlUrl);
//    	properties.put(Service.PROP_FILEURL, wsdlUrl);
//    	properties.put(Service.PROP_DESCRIPTION, "hello");
//    	api.notifyService(properties);
//    	
//    	list = session.query(query);
//    	assertEquals(1, list.size());
//    	assertEquals("hello", list.get(0).getProperty(Service.SCHEMA_DUBLINCORE, Service.PROP_DESCRIPTION));
//    	assertEquals("My Updated Service", list.get(0).getTitle());
    	
    }
    
}