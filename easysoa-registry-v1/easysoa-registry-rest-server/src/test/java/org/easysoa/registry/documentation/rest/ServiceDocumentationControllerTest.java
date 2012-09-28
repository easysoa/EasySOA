package org.easysoa.registry.documentation.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.AbstractRestApiTest;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.Service;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoftwareComponent;
import org.easysoa.registry.types.SystemTreeRoot;
import org.easysoa.registry.types.TaggingFolder;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.model.impl.ListProperty;
import org.nuxeo.ecm.core.api.model.impl.MapProperty;
import org.nuxeo.ecm.core.api.model.impl.primitives.StringProperty;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource.Builder;

@Deploy("org.easysoa.registry.rest.server")
@RepositoryConfig(cleanup = Granularity.CLASS)
public class ServiceDocumentationControllerTest extends AbstractRestApiTest {

    public ServiceDocumentationControllerTest() {
        super();
        setLogRepositoryAfterEachTest(true);
    }

    private static Logger logger = Logger.getLogger(ServiceDocumentationControllerTest.class);
    
    @Inject
    DiscoveryService discoveryService;

    @Inject
    DocumentService documentService;

    private final int SERVICE_COUNT = 5;

    @Test
    public void testServiceDocumentation() throws Exception {
        
        // Fill repository for all tests :
        
        // services
        for (int i = 0; i < SERVICE_COUNT; i++) {
            List<SoaNodeId> parentDocuments = new ArrayList<SoaNodeId>();
            if (i < SERVICE_COUNT - 1) {
                parentDocuments.add(new SoaNodeId(TaggingFolder.DOCTYPE, "Tag" + i));
            }
            discoveryService.runDiscovery(documentManager, new SoaNodeId(Service.DOCTYPE,
                    "MyService" + i), null, parentDocuments );
        }
        
        // endpoints
        SoaNodeId service0Id = new SoaNodeId(Service.DOCTYPE, "MyService0");
        SoaNodeId endpointId = new SoaNodeId(Endpoint.DOCTYPE, "MyEndpoint");
        discoveryService.runDiscovery(documentManager, endpointId, null, Arrays.asList(service0Id));
        discoveryService.runDiscovery(documentManager, new SoaNodeId(Endpoint.DOCTYPE, "MyEndpoint1"),
                null, Arrays.asList(new SoaNodeId(Service.DOCTYPE, "MyService1")));
        discoveryService.runDiscovery(documentManager, new SoaNodeId(Endpoint.DOCTYPE, "MyEndpoint2"),
                null, Arrays.asList(new SoaNodeId(Service.DOCTYPE, "MyService2")));
        
		// service impls
        SoaNodeId serviceImplId = new SoaNodeId(ServiceImplementation.DOCTYPE, "MyServiceImpl");
        Map<String, Object> properties = new HashMap<String, Object>();
        /*ListProperty operations = new org.nuxeo.ecm.core.api.model.impl.ListProperty(null, null);
        MapProperty operation1 = new MapProperty(operations, null).setValue(value);
        //operations.add(operations1);
        operation1.put("operationParameters", new StringProperty(operation1, null, 0));
        StringProperty operationName = new StringProperty(operation1, null, 0);
        operationName.setValue(value);
        operation1.put("operationName",  "getOrdersNumber");
        operation1.put("operationDocumentation", "Method: GET, Path: \"/orders/{clientName}\", Description: Returns the orders number for the specified client name.");*/
        ArrayList<Object> operations = new ArrayList<Object>();
        Map<String, Object> operation1 = new HashMap<String, Object>();
        operation1.put("operationParameters", null);
        operation1.put("operationName", "getOrdersNumber");
        operation1.put("operationDocumentation", "Method: GET, Path: \"/orders/{clientName}\", Description: Returns the orders number for the specified client name.");
        operations.add(operation1);
        properties.put(ServiceImplementation.XPATH_OPERATIONS, operations);
        properties.put(ServiceImplementation.XPATH_DOCUMENTATION,
        		"Blah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah\nBlah");
        properties.put(ServiceImplementation.XPATH_TESTS,
        		Arrays.asList("org.easysoa.MyServiceImplTest"));
        properties.put(ServiceImplementation.XPATH_ISMOCK, "true");
        discoveryService.runDiscovery(documentManager, serviceImplId, properties, Arrays.asList(service0Id));
        
        properties.clear();
        properties.put(ServiceImplementation.XPATH_TESTS,
        		Arrays.asList("org.easysoa.MyServiceImplTest"));
        discoveryService.runDiscovery(documentManager, 
        		new SoaNodeId(ServiceImplementation.DOCTYPE, "MyServiceImplNotMock"), properties, Arrays.asList(service0Id));
        
        discoveryService.runDiscovery(documentManager, 
        		new SoaNodeId(ServiceImplementation.DOCTYPE, "MyNotMockedImpl"), null,
        		Arrays.asList(new SoaNodeId(Service.DOCTYPE, "MyNotMockedService")));
        documentManager.save();
        
        // development project (as folder, or could be in model TODO, especially since can be discovered from root pom (though it is not only that))

        // (technical component (same ?!?))

        
        // business component (as folder, or could be in model TODO)
        SoaNodeId businessProcessSystem1Id = new SoaNodeId(TaggingFolder.DOCTYPE, "BusinessProcessSystem1");
        discoveryService.runDiscovery(documentManager, businessProcessSystem1Id, null, null);
        SoaNodeId businessProcess1SoftwareComponent1Id = new SoaNodeId(SoftwareComponent.DOCTYPE, "BusinessProcess1SoftwareComponent1");
        discoveryService.runDiscovery(documentManager, businessProcess1SoftwareComponent1Id, null, Arrays.asList(businessProcessSystem1Id)); // consists in
        //discoveryService.runDiscovery(documentManager, service0Id, null, Arrays.asList(businessProcess1SoftwareComponent1Id)); // consumes NO rather deliverables
        SoaNodeId deliverable0id = new SoaNodeId(Deliverable.DOCTYPE, "Deliverable0");
        discoveryService.runDiscovery(documentManager, deliverable0id, null, Arrays.asList(businessProcess1SoftwareComponent1Id));
        SoaNodeId serviceImplementation0id = new SoaNodeId(ServiceImplementation.DOCTYPE, "ServiceImplementation0");
        discoveryService.runDiscovery(documentManager, serviceImplementation0id, null, Arrays.asList(deliverable0id));
        discoveryService.runDiscovery(documentManager, serviceImplementation0id, null, Arrays.asList(service0Id));
        SoaNodeId deliverable1id = new SoaNodeId(Deliverable.DOCTYPE, "Deliverable1");
        discoveryService.runDiscovery(documentManager, deliverable1id, null, null); // deliverable in no business process
        discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "ServiceImplementation1"),
                null, Arrays.asList(new SoaNodeId(Service.DOCTYPE, "MyService1")));
        discoveryService.runDiscovery(documentManager, new SoaNodeId(ServiceImplementation.DOCTYPE, "ServiceImplementation1"),
                null, Arrays.asList(deliverable1id));
        
        SoaNodeId noBusinessProcessSoftwareComponentId = new SoaNodeId(SoftwareComponent.DOCTYPE, "NoBusinessProcessSoftwareComponent");
        discoveryService.runDiscovery(documentManager, noBusinessProcessSoftwareComponentId, null, null);
        discoveryService.runDiscovery(documentManager, new SoaNodeId(Deliverable.DOCTYPE, "noBusinessProcessDeliverable"), null, Arrays.asList(noBusinessProcessSoftwareComponentId));

        // test software component

        // user classified business component
        /*DocumentModel documentModel = documentManager.createDocumentModel(doctype);
        documentModel.setPathInfo(parentPath, name);
        documentModel.setProperty("dublincore", "title", title);
        documentModel = documentManager.createDocument(documentModel);*/
        documentService.createDocument(documentManager, "Workspace", "Business", "/default-domain/workspaces", "Business");
        DocumentModel business1Folder = documentService.createDocument(documentManager, SystemTreeRoot.DOCTYPE, "Business1", "/default-domain/workspaces/Business", "Business1");
        // first BP (user created) and its service :
        DocumentModel b1p2 = documentService.create(documentManager, new SoaNodeId(TaggingFolder.DOCTYPE, "Business1Process2"), "/default-domain/workspaces/Business/Business1"); // will be auto reclassified
        b1p2.setPropertyValue("dc:title", "Business1Process2");
        documentManager.save();
        SoaNodeId b1p2Id = documentService.createSoaNodeId(b1p2);
        discoveryService.runDiscovery(documentManager, new SoaNodeId(Service.DOCTYPE, "Business1Process2Service1"),null, Arrays.asList(b1p2Id));
        // 2nd BP (reused) and its service :
        documentManager.createProxy(new PathRef("/default-domain/repository/TaggingFolder/BusinessProcessSystem1"), business1Folder.getRef());
        discoveryService.runDiscovery(documentManager, new SoaNodeId(Service.DOCTYPE, "BusinessProcessSystem1Service1"), null, Arrays.asList(businessProcessSystem1Id));

        // tag without service : 
        SoaNodeId tagWithoutService = new SoaNodeId(TaggingFolder.DOCTYPE, "tagWithoutService");
        discoveryService.runDiscovery(documentManager, tagWithoutService, null, null);
        
        documentManager.save();
        logRepository();

        Client client = createAuthenticatedHTTPClient();
        
        // Fetch services page :
        Builder servicesReq = client.resource(this.getURL(ServiceDocumentationController.class)).accept(MediaType.TEXT_HTML);
        String res = servicesReq.get(String.class);
        logger.info(res);
        // Check result
        Assert.assertTrue(res.contains("MyService0"));

        // Fetch service doc page :
        Builder serviceDocRef = client.resource(this.getURL(ServiceDocumentationController.class))
                .path("path/" + "default-domain/repository/Service/MyService0").accept(MediaType.TEXT_HTML); // impl case
        //Builder serviceDocRef = client.resource(this.getURL(ServiceDocumentationController.class))
        //        .path("default-domain/repository/Service/MyService1").accept(MediaType.TEXT_HTML);
        //Builder serviceDocRef = client.resource(this.getURL(ServiceDocumentationController.class))
        //        .path("default-domain/repository/TaggingFolder/Tag0/MyService0").accept(MediaType.TEXT_HTML); // proxy case
        res = serviceDocRef.get(String.class);
        logger.info(res);
        Assert.assertTrue(res.contains("MyService0"));

        serviceDocRef = client.resource(this.getURL(ServiceDocumentationController.class))
                .path("tag/default-domain/repository/TaggingFolder/BusinessProcessSystem1").accept(MediaType.TEXT_HTML);
        res = serviceDocRef.get(String.class);
        logger.info(res);
        Assert.assertTrue(res.contains("/default-domain/repository/Service/BusinessProcessSystem1Service1"));

        serviceDocRef = client.resource(this.getURL(ServiceDocumentationController.class))
                .path("tag/default-domain/workspaces/Business/Business1/Business1Process2").accept(MediaType.TEXT_HTML);
        res = serviceDocRef.get(String.class);
        logger.info(res);
        Assert.assertTrue(res.contains("/default-domain/repository/Service/Business1Process2Service1"));

        serviceDocRef = client.resource(this.getURL(ServiceDocumentationController.class))
                .path("default-domain/repository/Service/BusinessProcessSystem1Service1/tags").accept(MediaType.TEXT_HTML);
        res = serviceDocRef.get(String.class);
        logger.info(res);
        Assert.assertTrue(res.contains("/default-domain/repository/TaggingFolder/Business1Process2"));
        
        // validation - internal services (consumption) :
        // for (service : getInternalServices(context))
        
        // validation - services promoted from outside :
        // for (service : application/proxiedServices) if !isCompliantTo(service.itf, application.getReqItf(service) alert("inconsistent!")
        // for (businessService : application/businessServices) if !isCompliantTo(businessService/proxiedTechnicalService.itf, businessService.itf alert("inconsistent!")

        // validation - services promoted to outside vs reqs :
        // for (publishedService : application/publishedServices) if !isCompliantTo(publishedService.itf, application.getPublishedReqItf(publishedService) alert("inconsistent!")
    }
    
}
