package org.easysoa.sca;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;

import org.easysoa.doctypes.Service;
import org.easysoa.services.DocumentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.blob.InputStreamBlob;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.services.resource.ResourceService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;


/**
 * Tests SCA import.
 * Done in the manner of SCAImportBean.
 * 
 * @author mdutoo
 *
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOAFeature.class)
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=DefaultRepositoryInit.class)
public class SCAImportTest {
    @Inject CoreSession session;
    @Inject ResourceService resourceService;
    
    DocumentModel parentAppliImplModel;
    
    
    @Before
    public void setUp() throws ClientException {
    	// Find or create appli
    	String appliUrl = "http://test/appli/";
		parentAppliImplModel = DocumentService.findAppliImpl(session, appliUrl);
		assertEquals(parentAppliImplModel, null);
		
		String title = "Test Appli Title";
		parentAppliImplModel = DocumentService.createAppliImpl(session, appliUrl);
		parentAppliImplModel.setProperty("dublincore", "title", title);
		session.saveDocument(parentAppliImplModel);
		session.save();
		// Update optional properties
		//setPropertiesIfNotNull(parentAppliImplModel, SCHEMA, AppliImpl.getPropertyList(), params);
		// Save
		//if (!errorFound) {
		//	session.saveDocument(parentAppliImplModel);
		//	session.save();
		//}
		
		// NB. created documents are auto deleted at the end, so no need for :
		// session.removeDocument(parentAppliImplModel.getRef());
    }
    
    @Test
    public void testSCA() throws Exception {
    	// SCA composite file to import :
    	// to load a file, we use simply File, since user.dir is set relatively to the project
    	String scaFilePath = "src/test/resources/" + "org/easysoa/tests/RestSoapProxy.composite";
    	File scaFile = new File(scaFilePath);
    	// NB. on the opposite, ResourceService does not work (or maybe with additional contributions ?)
    	//URL a = resourceService.getResource("org/easysoa/tests/RestSoapProxy.composite");
    	
		ScaImporter importer = new ScaImporter(session, new InputStreamBlob(new FileInputStream(scaFile)));
		importer.setParentAppliImpl(session.getDocument(new IdRef(parentAppliImplModel.getId())));
		importer.setServiceStackType("FraSCAti");
		importer.setServiceStackUrl("/");
		importer.importSCA();

		DocumentModelList resDocList;
		//DocumentModel resDoc;
		
		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				Service.DOCTYPE + "' AND " + "dc:title" + " = '" +  "restInterface" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(resDocList.size(), 1);
		//resDoc = resDocList.get(0);
		
		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				Service.DOCTYPE + "' AND " + "dc:title" + " = '" +  "ProxyService" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(resDocList.size(), 1);
    }
}
