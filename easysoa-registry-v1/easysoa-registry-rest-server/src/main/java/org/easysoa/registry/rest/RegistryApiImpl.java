package org.easysoa.registry.rest;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.runtime.api.Framework;

/**
 * REST discovery API
 * 
 * For now try to put info discovered in source code the simplest way
 * 
 * later (once indicators are computed...) soanodeinformation may need to be be detailed (add correlation direction, full model...)
 * 
 * @author mkalam-alami
 * 
 */
@Path("easysoa/registry")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegistryApiImpl implements RegistryApi {
    
    @Context HttpServletRequest request;
    
    @POST
    public OperationResult post(SoaNodeInformation soaNodeInfo) throws Exception {
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DiscoveryService discoveryService = Framework.getService(DiscoveryService.class);

            // Create SoaNode
            discoveryService.runDiscovery(documentManager, soaNodeInfo.getId(),
                    soaNodeInfo.getProperties(), soaNodeInfo.getParentDocuments());
            documentManager.save();
            return new OperationResult(true);
        } catch (Exception e) {
            return new OperationResult(false, "Failed to update or create document", e);
        }
    }

    @GET
    public SoaNodeInformation get() throws Exception {
        CoreSession documentManager = SessionFactory.getSession(request);
        DocumentModel document = documentManager.getDocument(new PathRef("/default-domain/workspaces"));
        return SoaNodeInformationFactory.create(documentManager, document);
    }

    @GET
    @Path("{doctype}")
    public List<SoaNodeInformation> get(
            @PathParam("doctype") String doctype) throws Exception {
        // Initialization
        CoreSession documentManager = SessionFactory.getSession(request);
        DocumentService documentService = Framework.getService(DocumentService.class);

        // Fetch SoaNode list
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE "
                + "ecm:currentLifeCycleState <> 'deleted' AND "
                + "ecm:isCheckedInVersion = 0 AND " + "ecm:isProxy = 0",
                new Object[] { doctype }, false, true);
        DocumentModelList soaNodeModelList = documentManager.query(query);

        // Convert data for marshalling
        List<SoaNodeInformation> modelsToMarshall = new LinkedList<SoaNodeInformation>();
        for (DocumentModel soaNodeModel : soaNodeModelList) {
            modelsToMarshall.add(new SoaNodeInformation(documentService.createSoaNodeId(soaNodeModel),
                    null, null));
        }

        // Write response
        return modelsToMarshall;
    }

    @GET
    @Path("{doctype}/{name}")
    public SoaNodeInformation get(
            @PathParam("doctype") String doctype, @PathParam("name") String name) throws Exception {
        SoaNodeId id = new SoaNodeId(doctype, name);
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DocumentService documentService = Framework.getService(DocumentService.class);
    
            // Fetch SoaNode
            DocumentModel foundDocument = documentService.find(documentManager, id);
            if (foundDocument == null) {
                throw new Exception("Document doesnt exist"); // TODO 404
            }
            else {
                return SoaNodeInformationFactory.create(documentManager, foundDocument);
            }
        }
        catch (Exception e) {
            throw new Exception("Failed to fetch document " + id.toString(), e);
        }
    }

    @PUT
    public OperationResult put(
            SoaNodeInformation soaNodeInfo) throws ClientException {
        SoaNodeId soaNodeId = soaNodeInfo.getId();
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DocumentService documentService = Framework.getService(DocumentService.class);

            // Check that the target document exists
            DocumentModel modelToUpdate = documentService.find(documentManager, soaNodeId);
            if (modelToUpdate != null) {
                // Update SoaNode
                DiscoveryService discoveryService = Framework.getService(DiscoveryService.class);
                discoveryService.runDiscovery(documentManager, soaNodeId,
                        soaNodeInfo.getProperties(), soaNodeInfo.getParentDocuments());
                documentManager.save();
                
                return new OperationResult(true);
            }
            else {
                throw new Exception("Document to update " + soaNodeId.toString() + " doesn't exist");
            }
        } catch (Exception e) {
            return new OperationResult(false, "Failed to update or create document", e);
        }
    }
    
    @DELETE
    @Path("{doctype}/{name}")
    public OperationResult delete(
            @PathParam("doctype") String doctype, @PathParam("name") String name) throws ClientException {
        SoaNodeId soaNodeId = new SoaNodeId(doctype, name);
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DocumentService documentService = Framework.getService(DocumentService.class);

            // Delete SoaNode
            documentService.delete(documentManager, soaNodeId);

            return new OperationResult(true);
        } catch (Exception e) {
            return new OperationResult(false, "Failed to delete document " + soaNodeId.toString(), e);
        }
    }
    
    @DELETE
    @Path("{doctype}/{name}/{correlatedDoctype}/{correlatedName}")
    public OperationResult delete(
            @PathParam("doctype") String doctype, @PathParam("name") String name,
            @PathParam("correlatedDoctype") String correlatedDoctype,
            @PathParam("correlatedName") String correlatedName) throws ClientException {
        SoaNodeId soaNodeId = new SoaNodeId(doctype, name),
                correlatedSoaNodeId = new SoaNodeId(correlatedDoctype, correlatedName);
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DocumentService documentService = Framework.getService(DocumentService.class);

            // Delete proxy of SoaNode
            DocumentModel correlatedSoaNodeModel = documentService.find(documentManager, correlatedSoaNodeId);
            if (correlatedSoaNodeModel != null) {
                documentService.deleteProxy(documentManager, soaNodeId, correlatedSoaNodeModel.getPathAsString());
            }
            else {
                throw new Exception("Correlated SoaNode does not exist");
            }

            return new OperationResult(true);
        } catch (Exception e) {
            return new OperationResult(false, "Failed to delete document " + soaNodeId.toString(), e);
        }
    }
}
