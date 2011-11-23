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

package org.easysoa.sca.frascati;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.registry.frascati.NxFraSCAtiService;
import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.extension.ScaImporterComponent;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.easysoa.sca.visitors.NxBindingVisitorFactory;
import org.easysoa.services.DocumentService;
import org.easysoa.test.EasySOACoreFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.easysoa.test.rest.RepositoryLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
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
	//"org.easysoa.registry.core", // deployed auto by dep
	"org.nuxeo.runtime.datasource",

	// BUG should but does not work without deploying the following deps
	// taken from easysoa-registry-core's EasySOACoreTestFeature
    //"org.easysoa.registry.core",
    "org.easysoa.registry.core:OSGI-INF/vocabularies-contrib.xml", // required, else no custom easysoa vocabularies,
    "org.easysoa.registry.core:OSGI-INF/DocumentServiceComponent.xml", // required to find the service through the Framework class
    "org.easysoa.registry.core:OSGI-INF/DiscoveryServiceComponent.xml", // idem
    "org.easysoa.registry.core:OSGI-INF/VocabularyHelperComponent.xml", // idem
    "org.easysoa.registry.core:OSGI-INF/core-type-contrib.xml", // required, else no custom types
    "org.easysoa.registry.core:OSGI-INF/EasySOAInitComponent.xml", // required by the contribution below
    "org.easysoa.registry.core:OSGI-INF/eventlistener-contrib.xml" // required to enable the specific doctype listeners
    //"org.nuxeo.runtime.datasource"
})
@LocalDeploy({
	///"org.easysoa.registry.frascati:OSGI-INF/frascati-service.xml", // required else no frascatiService OUTSIDE TEST INJECTIONS
	"org.easysoa.registry.core:OSGI-INF/ScaImporterComponent.xml",
	///"org.easysoa.registry.core:OSGI-INF/sca-importer-xml-contrib.xml", // would override frascati so no
	///"org.easysoa.registry.frascati:OSGI-INF/sca-importer-frascati-contrib.xml",
	"org.easysoa.registry.core:test/datasource-contrib.xml" // required because no jetty.naming in deps
})
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class FraSCAtiImportServiceTest {

    static final Log log = LogFactory.getLog(FraSCAtiImportServiceTest.class);

    @Inject CoreSession session;

    @Inject DocumentService docService;
    
    @Inject ResourceService resourceService;
    
    DocumentModel parentAppliImplModel;
    
    @Inject NxFraSCAtiService frascatiService;
    
    @Inject ScaImporterComponent scaImporterComponent;
    
    @Before
    public void setUp() throws Exception {
        
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
    //@Ignore    
    public void importSCAZipSimple() throws Exception {
    	// SCA composite file to import :
    	// to load a file, we use simply File, since user.dir is set relatively to the project
    	String scaFilePath = "src/test/resources/" + "proxy-simple-1.0-SNAPSHOT.jar";
    	File scaFile = new File(scaFilePath);
    	// NB. on the opposite, ResourceService does not work (or maybe with additional contributions ?)
    	//URL a = resourceService.getResource("org/easysoa/tests/RestSoapProxy.composite");
    	
    	BindingVisitorFactory visitorFactory = new NxBindingVisitorFactory(session);
		FraSCAtiScaImporter importer = new FraSCAtiScaImporter(visitorFactory, scaFile); // TODO put FileBlob back in orig test
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
		
		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				Service.DOCTYPE + "' AND " + "dc:title" + " = '" +  "ProxyService" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());
		resDoc = resDocList.get(0);
		assertEquals("/ProxyService", resDoc.getProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH));

		// references :
		/*resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				Service.DOCTYPE + "' AND " 
				+ EasySOADoctype.SCHEMA_COMMON_PREFIX + EasySOADoctype.PROP_ARCHIPATH
				+ " = '" +  "ws" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());

		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				Service.DOCTYPE + "' AND "
				+ EasySOADoctype.SCHEMA_COMMON_PREFIX + EasySOADoctype.PROP_ARCHIPATH
				+ " = '" +  "/ProxyUnused/ws" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());*/
		
		// api :
		/*
		DocumentModel apiModel = docService.findServiceApi(session, "http://127.0.0.1:9010");
		assertEquals("PureAirFlowers API", apiModel.getTitle());*/
		
    }
    
    
    /** The following FraSCAti parsing-based import would fail without custom
     * ProcessingContext.loadClass() because of unknown class in zip */
    @Test
    //@Ignore
    public void importSCAZip() throws Exception {
    	// SCA composite file to import :
    	// to load a file, we use simply File, since user.dir is set relatively to the project
    	String scaFilePath = "src/test/resources/" + "proxy-1.0-SNAPSHOT.jar";
    	File scaFile = new File(scaFilePath);
    	// NB. on the opposite, ResourceService does not work (or maybe with additional contributions ?)
    	//URL a = resourceService.getResource("org/easysoa/tests/RestSoapProxy.composite");
    	BindingVisitorFactory visitorFactory = new NxBindingVisitorFactory(session);
    	FraSCAtiScaImporter importer = new FraSCAtiScaImporter(visitorFactory, scaFile);
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
    //@Ignore
    public void testSCAComposite() throws Exception {
    	// SCA composite file to import :
    	// to load a file, we use simply File, since user.dir is set relatively to the project
    	String scaFilePath = "src/test/resources/" + "org/easysoa/sca/RestSoapProxy.composite";
    	File scaFile = new File(scaFilePath);
    	// NB. on the opposite, ResourceService does not work (or maybe with additional contributions ?)
    	//URL a = resourceService.getResource("org/easysoa/tests/RestSoapProxy.composite");
    	BindingVisitorFactory visitorFactory = new NxBindingVisitorFactory(session);
    	FraSCAtiScaImporter importer = new FraSCAtiScaImporter(visitorFactory, scaFile);
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
    
    /**
     * Test the frascati SCA importer deployed as a Nuxeo extension point
     */
    @Test
    public void testFrascatiScaImporter() throws Exception {
    	// SCA composite file to import :
    	// to load a file, we use simply File, since user.dir is set relatively to the project
    	//String scaFilePath = "src/test/resources/" + "org/easysoa/sca/RestSoapProxy.composite";
    	// With this sample, no problem, all the required (specified in the composite file) classes are in a single jar
    	String scaFilePath = "src/test/resources/" + "proxy-1.0-SNAPSHOT.jar";

    	File scaFile = new File(scaFilePath);    	
   	
    	// Getting the importer
    	BindingVisitorFactory visitorFactory = new NxBindingVisitorFactory(session);
    	IScaImporter importer = scaImporterComponent.createScaImporter(visitorFactory, scaFile);
    	//IScaImporter importer = scaImporterComponent.createScaImporter(session, scaFile);
    	// If importer is null, we have a problem
    	assertNotNull(importer);
    	
		importer.setParentAppliImpl(session.getDocument(new IdRef(parentAppliImplModel.getId())));
		importer.setServiceStackType("FraSCAti");
		importer.setServiceStackUrl("/");
    	importer.importSCA();
    	
    	// Check import results
    	DocumentModelList resDocList;
		DocumentModel resDoc;
		
		// Log repository
		new RepositoryLogger(session, "Repository state after import").logAllRepository();
		
		// services :
		// No corresponding data in the imported sample jar
		/*resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				Service.DOCTYPE + "' AND " + "dc:title" + " = '" +  "restInterface" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());
		resDoc = resDocList.get(0);
		assertEquals("/Proxy/restInterface", resDoc.getProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH));;*/
		
		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				Service.DOCTYPE + "' AND " + "dc:title" + " = '" +  "ProxyService" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());
		resDoc = resDocList.get(0);
		assertEquals("/ProxyService", resDoc.getProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH));;

		// references :
		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				ServiceReference.DOCTYPE + "' AND "
				+ EasySOADoctype.SCHEMA_COMMON_PREFIX + EasySOADoctype.PROP_ARCHIPATH
				+ " = '" +  "/Proxy/ws" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());

		// No corresponding data in the imported sample jar
		/*resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				ServiceReference.DOCTYPE + "' AND "
				+ EasySOADoctype.SCHEMA_COMMON_PREFIX + EasySOADoctype.PROP_ARCHIPATH
				+ " = '" +  "/ProxyUnused/ws" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());*/
		
		// api :
		DocumentModel apiModel = docService.findServiceApi(session, "http://127.0.0.1:9010");
		assertEquals("PureAirFlowers API", apiModel.getTitle());
    }    
    
    @Test
    //@Ignore
    public void testFrascatiClassNotFoundException() throws Exception {
    	// With this sample, frascati throws a ClassNotFoundException because required classes are in an other jar
    	String scaFilePath = "src/test/resources/" + "easysoa-samples-smarttravel-trip-0.4-SNAPSHOT.jar";
    	File scaFile = new File(scaFilePath);    	
   	
    	// Getting the importer
    	BindingVisitorFactory visitorFactory = new NxBindingVisitorFactory(session);
    	IScaImporter importer = scaImporterComponent.createScaImporter(visitorFactory, scaFile);
    	//IScaImporter importer = scaImporterComponent.createScaImporter(session, scaFile);
    	// If importer is null, we have a problem
    	assertNotNull(importer);

		importer.setParentAppliImpl(session.getDocument(new IdRef(parentAppliImplModel.getId())));
		importer.setServiceStackType("FraSCAti");
		importer.setServiceStackUrl("/");
		try{
			importer.importSCA();
			// if not exception is throws, fail the test
			fail();
		}
		catch(Exception ex){
			assertEquals("org.ow2.frascati.tinfi.TinfiRuntimeException", ex.getClass().getName());
			assertEquals("java.lang.NoClassDefFoundError: Lnet/webservicex/GlobalWeatherSoap;", ex.getMessage());
		}
    }
    
}
