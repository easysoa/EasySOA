package org.easysoa.sca.frascati;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.Service;
import org.easysoa.registry.frascati.FraSCAtiService;
import org.easysoa.services.DocumentService;
import org.easysoa.test.EasySOACoreFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.easysoa.test.rest.RepositoryLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.services.resource.ResourceService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;


/**
 * Tests SCA import with FraSCAti
 * @author mdutoo
 *
 */

@RunWith(FeaturesRunner.class)
@Features(EasySOACoreFeature.class)
@Deploy({
	"org.easysoa.registry.frascati",
	//"org.easysoa.registry.core" // deployed auto by dep
	"org.nuxeo.runtime.datasource"
})
@LocalDeploy({
	"org.easysoa.registry.frascati:OSGI-INF/frascati-service.xml", // required else no frascatiService OUTSIDE TEST INJECTIONS
	"org.easysoa.registry.core:org/easysoa/tests/datasource-contrib.xml" // required because no jetty.naming in deps
})
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class FraSCAtiImportServiceTest
{

    static final Log log = LogFactory.getLog(FraSCAtiImportServiceTest.class);

    @Inject CoreSession session;

    @Inject DocumentService docService;
    
    @Inject ResourceService resourceService;
    
    DocumentModel parentAppliImplModel;
    
    @Inject FraSCAtiService frascatiService;
    
    @Before
    public void setUp() throws ClientException, MalformedURLException {
    	// FraSCAti
  	  	assertNotNull("Cannot get FraSCAti service component", frascatiService);

    	// Find or create appli
    	String appliUrl = "http://localhost";
		parentAppliImplModel = docService.findAppliImpl(session, appliUrl);
		if(parentAppliImplModel == null) {
			String title = "Test Appli Title";
			parentAppliImplModel = docService.createAppliImpl(session, appliUrl);
			parentAppliImplModel.setProperty("dublincore", "title", title);
			session.saveDocument(parentAppliImplModel);
			session.save();
			// NB. created documents are auto deleted at the end, so no need for :
			// session.removeDocument(parentAppliImplModel.getRef());
		}
    }
    
    @Test
    public void importSCAZipSimple() throws Exception {
    	// SCA composite file to import :
    	// to load a file, we use simply File, since user.dir is set relatively to the project
    	String scaFilePath = "src/test/resources/" + "proxy-simple-1.0-SNAPSHOT.jar";
    	File scaFile = new File(scaFilePath);
    	// NB. on the opposite, ResourceService does not work (or maybe with additional contributions ?)
    	//URL a = resourceService.getResource("org/easysoa/tests/RestSoapProxy.composite");
    	
    	FraSCAtiScaImporter importer = new FraSCAtiScaImporter(session, new FileBlob(scaFile)); // TODO put FileBlob back in orig test
		importer.setParentAppliImpl(session.getDocument(new IdRef(parentAppliImplModel.getId())));
		importer.setServiceStackType("FraSCAti");
		importer.setServiceStackUrl("/");
		importer.importSCAZip();

		DocumentModelList resDocList;
		DocumentModel resDoc;
		
		// Log repository
		new RepositoryLogger(session, "Repository state after import").logAllRepository();
		
		// services :
		
		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				Service.DOCTYPE + "' AND " + "dc:title" + " = '" +  "restInterface" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());
		resDoc = resDocList.get(0);
		assertEquals("/Proxy/restInterface", resDoc.getProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH));
		/*
		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				Service.DOCTYPE + "' AND " + "dc:title" + " = '" +  "ProxyService" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());
		resDoc = resDocList.get(0);
		assertEquals("/ProxyService", resDoc.getProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH));

		// references :
		
		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				ServiceReference.DOCTYPE + "' AND "
				+ EasySOADoctype.SCHEMA_COMMON_PREFIX + EasySOADoctype.PROP_ARCHIPATH
				+ " = '" +  "/Proxy/ws" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());

		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				ServiceReference.DOCTYPE + "' AND "
				+ EasySOADoctype.SCHEMA_COMMON_PREFIX + EasySOADoctype.PROP_ARCHIPATH
				+ " = '" +  "/ProxyUnused/ws" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());
		
		// api :
		
		DocumentModel apiModel = docService.findServiceApi(session, "http://127.0.0.1:9010");
		assertEquals("PureAirFlowers API", apiModel.getTitle());*/
		
    }
    
    
    /** The following FraSCAti parsing-based import would fail without custom
     * ProcessingContext.loadClass() because of unknown class in zip */
    @Test
    public void importSCAZip() throws Exception {
    	// SCA composite file to import :
    	// to load a file, we use simply File, since user.dir is set relatively to the project
    	String scaFilePath = "src/test/resources/" + "proxy-1.0-SNAPSHOT.jar";
    	File scaFile = new File(scaFilePath);
    	// NB. on the opposite, ResourceService does not work (or maybe with additional contributions ?)
    	//URL a = resourceService.getResource("org/easysoa/tests/RestSoapProxy.composite");
    	
    	FraSCAtiScaImporter importer = new FraSCAtiScaImporter(session, new FileBlob(scaFile)); // TODO put FileBlob back in orig test
		importer.setParentAppliImpl(session.getDocument(new IdRef(parentAppliImplModel.getId())));
		importer.setServiceStackType("FraSCAti");
		importer.setServiceStackUrl("/");
		importer.importSCAZip();

		DocumentModelList resDocList;
		DocumentModel resDoc;
		
		// Log repository
		new RepositoryLogger(session, "Repository state after import").logAllRepository();
		
		// services :
		
		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				Service.DOCTYPE + "' AND " + "dc:title" + " = '" +  "restInterface" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());
		resDoc = resDocList.get(0);
		assertEquals("/Proxy/restInterface", resDoc.getProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH));
		
    }
    
    @Test
    public void testSCAComposite() throws Exception {
    	// SCA composite file to import :
    	// to load a file, we use simply File, since user.dir is set relatively to the project
    	String scaFilePath = "src/test/resources/" + "org/easysoa/sca/RestSoapProxy.composite";
    	File scaFile = new File(scaFilePath);
    	// NB. on the opposite, ResourceService does not work (or maybe with additional contributions ?)
    	//URL a = resourceService.getResource("org/easysoa/tests/RestSoapProxy.composite");

    	FraSCAtiScaImporter importer = new FraSCAtiScaImporter(session, new FileBlob(scaFile));
		importer.setParentAppliImpl(session.getDocument(new IdRef(parentAppliImplModel.getId())));
		importer.setServiceStackType("FraSCAti");
		importer.setServiceStackUrl("/");
		importer.importSCAComposite();

		DocumentModelList resDocList;
		DocumentModel resDoc;
		
		// Log repository
		new RepositoryLogger(session, "Repository state after import").logAllRepository();
		
		// services :
		
		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				Service.DOCTYPE + "' AND " + "dc:title" + " = '" +  "restInterface" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());
		resDoc = resDocList.get(0);
		assertEquals("/Proxy/restInterface", resDoc.getProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH));
		
    }
    
}
