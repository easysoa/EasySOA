package org.easysoa.sca.frascati;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.Service;
import org.easysoa.registry.frascati.NxFraSCAtiService;
import org.easysoa.sca.extension.ScaImporterComponent;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.easysoa.sca.visitors.LocalBindingVisitorFactory;
import org.easysoa.services.DocumentService;
import org.easysoa.test.EasySOACoreFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.easysoa.test.rest.RepositoryLogger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.services.resource.ResourceService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

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
//@Jetty(config="src/test/resources/jetty.xml", port=EasySOAConstants.NUXEO_TEST_PORT)
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class ApiFrascatiImportServiceTest {

    static final Log log = LogFactory.getLog(ApiFrascatiImportServiceTest.class);

    @Inject CoreSession session;

    @Inject DocumentService docService;
    
    @Inject ResourceService resourceService;
    
    DocumentModel parentAppliImplModel;
    
    @Inject NxFraSCAtiService frascatiService;
    
    @Inject ScaImporterComponent scaImporterComponent;
    
    @Before
    public void setUp() throws ClientException, MalformedURLException {
    	
    	log.debug("service  = " + frascatiService);
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
	
    // TODO : How to make this test works ?
    // The REST server is not started
    @Test
    @Ignore
    public void testSCAComposite() throws Exception {
    	// SCA composite file to import :
    	// to load a file, we use simply File, since user.dir is set relatively to the project
    	log.debug("FrascatiService = " + frascatiService);
    	String scaFilePath = "src/test/resources/" + "org/easysoa/sca/RestSoapProxy.composite";
    	File scaFile = new File(scaFilePath);
    	// NB. on the opposite, ResourceService does not work (or maybe with additional contributions ?)
    	//URL a = resourceService.getResource("org/easysoa/tests/RestSoapProxy.composite");
    	
    	BindingVisitorFactory bindingVisitorFactory = new LocalBindingVisitorFactory(session);
    	ApiFraSCAtiScaImporter importer = new ApiFraSCAtiScaImporter(bindingVisitorFactory, scaFile, frascatiService);
		//importer.setParentAppliImpl(session.getDocument(new IdRef(parentAppliImplModel.getId())));
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
