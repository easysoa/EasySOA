package com.openwide.easysoa.galaxydemotest;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.easysoa.test.EasySOAFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.services.resource.ResourceService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * Unit test for Galaxy Demo.
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOAFeature.class)
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class NuxeoEasySOATest {

    @Inject CoreSession session;
    @Inject ResourceService resourceService;
    
    /**
     * @throws ClientException 
     * 
     */
	@Test
	public final void testNuxeo() throws ClientException{
	
		DocumentModelList resDocList;
		//DocumentModel resDoc;		
		
		// Checks that service informations are registered in Nuxeo
		/*resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + Service.DOCTYPE + "' AND " + "dc:title" + " = '" +  "restInterface" + "' AND ecm:currentLifeCycleState <> 'deleted'");*/
		//resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = 'Service'");
		resDocList = session.query("SELECT * FROM Document");
		Iterator<DocumentModel> iter = resDocList.iterator();
		while(iter.hasNext()){
			DocumentModel doc = iter.next();
			System.out.println("Doc name : " + doc.getName());
		}
		assertEquals(resDocList.size(), 3);		
		
		//resDocList = session.query("SELECT * FROM Document WHERE ");
		//assertEquals(resDocList.size(), 1);
	}
	
}