package org.easysoa.registry;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.ServiceImplementation;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;

@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
public class InheritedFacetsTest extends AbstractRegistryTest {

	private static final SoaNodeId MYIMPL_ID = new SoaNodeId(ServiceImplementation.DOCTYPE, "MyServiceImpl");
	
	private static final SoaNodeId MYIMPL2_ID = new SoaNodeId(ServiceImplementation.DOCTYPE, "MyServiceImpl2");

	private static final SoaNodeId MYENDPOINT_ID = new SoaNodeId(Endpoint.DOCTYPE, "MyEndpoint");

	@Inject
	private CoreSession documentManager;
	
	@Inject
	private DocumentService documentService;

	private static DocumentModel myServiceImplModel;

	private static ServiceImplementation myServiceImpl;

	private static DocumentModel myEndpointModel;

	private static ServiceImplementation myEndpointServiceImpl;

	private static DocumentModel endpointProxyModel;
	
	@Test
	public void testInheritanceOnCreation() throws Exception {
		myServiceImplModel = documentService.create(documentManager, MYIMPL_ID);
		myServiceImpl = myServiceImplModel.getAdapter(ServiceImplementation.class);
		myServiceImpl.setTests(Arrays.asList("org.easysoa.Test1"));
		documentManager.saveDocument(myServiceImplModel);
		documentManager.save();
		
		myEndpointModel = documentService.create(documentManager,
				MYENDPOINT_ID, myServiceImplModel.getPathAsString());
		documentManager.save();
		myEndpointServiceImpl = myEndpointModel.getAdapter(ServiceImplementation.class);
		Assert.assertTrue("Inherited facets' metadata must be copied when child is created",
				myEndpointServiceImpl.getTests() != null &&
				myEndpointServiceImpl.getTests().contains("org.easysoa.Test1"));
	}

	@Test
	public void testInheritanceOnUpdate() throws Exception {
		// ...when impl is updated...
		myServiceImplModel = documentService.find(documentManager, MYIMPL_ID);
		myServiceImpl = myServiceImplModel.getAdapter(ServiceImplementation.class);
		List<String> implTests = myServiceImpl.getTests();
		implTests.add("org.easysoa.Test2");
		myServiceImpl.setTests(implTests);
		documentManager.saveDocument(myServiceImplModel);
		documentManager.save();
		myEndpointModel = documentService.find(documentManager, MYENDPOINT_ID);
		myEndpointServiceImpl = myEndpointModel.getAdapter(ServiceImplementation.class);
		Assert.assertTrue("Inherited facets' metadata must be maintained on updates",
				myEndpointServiceImpl.getTests() != null &&
				myEndpointServiceImpl.getTests().contains("org.easysoa.Test2"));
	}
	

	@Test
	public void testInheritanceOnMove() throws Exception {
		// ...when endpoint is moved to another impl...
		DocumentModel myServiceImpl2Model = documentService.create(documentManager, MYIMPL2_ID);
		ServiceImplementation myServiceImpl2 = myServiceImpl2Model.getAdapter(ServiceImplementation.class);
		myServiceImpl2.setTests(Arrays.asList("org.easysoa.Test3"));
		documentManager.saveDocument(myServiceImpl2Model);
		endpointProxyModel = documentService.findProxies(documentManager, MYENDPOINT_ID).get(0);
		documentManager.move(endpointProxyModel.getRef(), myServiceImpl2Model.getRef(), endpointProxyModel.getName());
		documentManager.save();

		myEndpointModel = documentService.find(documentManager, MYENDPOINT_ID);
		myEndpointServiceImpl = myEndpointModel.getAdapter(ServiceImplementation.class);
		Assert.assertTrue("Inherited facets' metadata must be updated when child is moved",
				myEndpointServiceImpl.getTests() != null &&
				myEndpointServiceImpl.getTests().contains("org.easysoa.Test3"));
	}

	@Test
	public void testQuery() throws Exception {
		// Intermission: take advantage of this feature,
		// by looking for "the endpoints that are covered by a certain test"
		DocumentModelList results = documentManager.query("SELECT * FROM " + Endpoint.DOCTYPE
				+ " WHERE " + NXQL.ECM_ISPROXY + " = 0 AND "
				+ ServiceImplementation.XPATH_TESTS + "/* = 'org.easysoa.Test3'");
		Assert.assertEquals("Query of ServiceImpl metadata on Endpoints must work", 1, results.size());
	}
	
	@Test
	public void testInheritanceOnDeletion() throws Exception {
		// ...and when endpoint is removed from impl
		documentManager.removeDocument(endpointProxyModel.getRef());
		documentManager.save();
		myEndpointModel = documentService.find(documentManager, MYENDPOINT_ID);
		myEndpointServiceImpl = myEndpointModel.getAdapter(ServiceImplementation.class);
		Assert.assertEquals("Inherited facets' metadata must be reset when child is deleted",
				0, myEndpointServiceImpl.getTests().size());
		
	}
}
