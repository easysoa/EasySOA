package org.easysoa.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.Service;
import org.easysoa.test.EasySOAFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
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
public class DocumentServiceTest {

    static final Log log = LogFactory.getLog(DocumentServiceTest.class);
    
    @Inject DocumentService docService;
    
    @Inject CoreSession session;

    @Before
    public void setUp() throws Exception {
  	  	assertNotNull("Cannot get document service component", docService);
    }
    
    /**
     * Test document merge
     * @throws Exception
     */
    @Test
    public void testMerge() throws Exception {

    	String url = "http://www.easysoa.com/services/coffee",
    		apiUrl = "http://www.easysoa.com/services",
    		lightUrl = "http://www.easysoa.com/web/coffee",
    		description = "Free coffee for everyone";

    	// Create API
    	DocumentModel appliImplModel = docService.getDefaultAppliImpl(session);
    	DocumentModel apiModel = docService.createServiceAPI(session, appliImplModel.getPathAsString(), apiUrl);
    	session.save();
    	
    	// Create services
    	DocumentModel model1 = docService.createService(session, apiModel.getPathAsString(), url);
    	model1.setProperty(Service.SCHEMA, Service.PROP_LIGHTURL, lightUrl);
    	session.saveDocument(model1);
    	session.save();
    	DocumentModel model2 = docService.createService(session, apiModel.getPathAsString(), url);
    	model2.setProperty(Service.SCHEMA_DUBLINCORE, Service.PROP_DESCRIPTION, description);
    	session.saveDocument(model2);
    	session.save();

    	// Fetch service
    	DocumentModelList list = session.query("SELECT * FROM Service");
    	assertEquals(1, list.size());
    	DocumentModel mergedModel = list.get(0);
    	assertEquals(lightUrl, mergedModel.getProperty(Service.SCHEMA, Service.PROP_LIGHTURL));
    	assertEquals(description, mergedModel.getProperty(Service.SCHEMA_DUBLINCORE, Service.PROP_DESCRIPTION));
    	
    }
    
    /**
     * Test the file download and parsing when the URL matches a WSDL
     * @throws Exception
     */
    @Test
    public void testWSDL() throws Exception {

    	String wsdlUrl = "http://www.ebi.ac.uk/Tools/webservices/wsdl/WSCensor.wsdl",
    		apiUrl = "http://www.ebi.ac.uk/Tools/es/ws-servers",
    		serviceUrl = "http://www.ebi.ac.uk/Tools/es/ws-servers/WSCensor";
    	
    	// Create API
    	DocumentModel appliImplModel = docService.getDefaultAppliImpl(session);
    	DocumentModel apiModel = docService.createServiceAPI(session, appliImplModel.getPathAsString(), apiUrl);

    	// Create Service with WSDL adress only
    	DocumentModel model = docService.createService(session, apiModel.getPathAsString(), wsdlUrl);
    	session.saveDocument(model);
    	session.save();
    	
    	// Check service contents
    	model = docService.findService(session, serviceUrl);
    	assertNotNull("Failed to fetch document by fileUrl", model);
    	assertEquals("Service didn't recognize URL as a WSDL file", wsdlUrl, 
    			model.getProperty(Service.SCHEMA, Service.PROP_FILEURL));
    	assertEquals("Failed to extract service URL from WSDL", serviceUrl, 
    			model.getProperty(Service.SCHEMA, Service.PROP_URL));
    	assertNotNull("Failed to save WSDL in 'file' schema", model.getProperty("file", "content"));
    	
    }
    
    /**
     * Test the service fetch by its fileUrl value
     * @throws Exception
     */
    @Test
    public void testFindByWSDL() throws Exception {

    	String wsdlUrl = "http://soatest.parasoft.com/calculator.wsdl",
    		apiUrl = "http://soatest.parasoft.com/glue",
    		serviceUrl = "http://ws1.parasoft.com/glue/calculator";

    	// Create API (if not done by previous test)
    	DocumentModel apiModel = docService.findServiceApi(session, apiUrl);
    	if (apiModel == null) {
        	DocumentModel appliImplModel = docService.getDefaultAppliImpl(session);
    		apiModel = docService.createServiceAPI(session, appliImplModel.getPathAsString(), apiUrl);
    	}

    	// Create Service with WSDL adress only
    	DocumentModel model = docService.createService(session, apiModel.getPathAsString(), serviceUrl);
    	model.setProperty(Service.SCHEMA, Service.PROP_FILEURL, wsdlUrl);
    	session.saveDocument(model);
    	session.save();
    	
    	// Find Service
    	model = docService.findService(session, wsdlUrl);
    	assertNotNull("Failed to fetch document by fileUrl", model);
    	
    }
    
}