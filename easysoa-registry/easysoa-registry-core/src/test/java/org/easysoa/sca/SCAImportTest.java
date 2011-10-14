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
 * Contact : easysoa-dev@groups.google.com
 */

package org.easysoa.sca;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;

import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.services.DocumentService;
import org.easysoa.test.EasySOACoreTestFeature;
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
import org.nuxeo.ecm.core.api.impl.blob.InputStreamBlob;
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
@Features(EasySOACoreTestFeature.class)
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class SCAImportTest {

    @Inject CoreSession session;

    @Inject DocumentService docService;
    
    @Inject ResourceService resourceService;
    
    DocumentModel parentAppliImplModel;
    
    @Before
    public void setUp() throws ClientException, MalformedURLException {

    	// Find or create appli
    	String appliUrl = "http://localhost";
		parentAppliImplModel = docService.findAppliImpl(session, appliUrl);
		assertEquals(parentAppliImplModel, null);
		
		String title = "Test Appli Title";
		parentAppliImplModel = docService.createAppliImpl(session, appliUrl);
		parentAppliImplModel.setProperty("dublincore", "title", title);
		session.saveDocument(parentAppliImplModel);
		session.save();
		// NB. created documents are auto deleted at the end, so no need for :
		// session.removeDocument(parentAppliImplModel.getRef());
    }
    
    @Test
    public void testSCA() throws Exception {
    	// SCA composite file to import :
    	// to load a file, we use simply File, since user.dir is set relatively to the project
    	String scaFilePath = "src/test/resources/" + "org/easysoa/sca/RestSoapProxy.composite";
    	File scaFile = new File(scaFilePath);
    	// NB. on the opposite, ResourceService does not work (or maybe with additional contributions ?)
    	//URL a = resourceService.getResource("org/easysoa/tests/RestSoapProxy.composite");
    	
		XMLScaImporter importer = new XMLScaImporter(session, new InputStreamBlob(new FileInputStream(scaFile)));
		importer.setParentAppliImpl(session.getDocument(new IdRef(parentAppliImplModel.getId())));
		importer.setServiceStackType("FraSCAti");
		importer.setServiceStackUrl("/");
		importer.importSCA();

		DocumentModelList resDocList;
		DocumentModel resDoc;
		
		// Log repository
		new RepositoryLogger(session, "Repository state after import").logAllRepository();
		
		// services :
		
		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				Service.DOCTYPE + "' AND " + "dc:title" + " = '" +  "restInterface" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());
		resDoc = resDocList.get(0);
		assertEquals("/Proxy/restInterface", resDoc.getProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH));;
		
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

		resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				ServiceReference.DOCTYPE + "' AND "
				+ EasySOADoctype.SCHEMA_COMMON_PREFIX + EasySOADoctype.PROP_ARCHIPATH
				+ " = '" +  "/ProxyUnused/ws" + "' AND ecm:currentLifeCycleState <> 'deleted'");
		assertEquals(1, resDocList.size());
		
		// api :
		
		DocumentModel apiModel = docService.findServiceApi(session, "http://127.0.0.1:9010");
		assertEquals("PureAirFlowers API", apiModel.getTitle());
		
    }
}
