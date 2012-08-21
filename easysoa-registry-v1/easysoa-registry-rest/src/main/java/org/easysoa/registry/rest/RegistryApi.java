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

import net.sf.json.JSONObject;

import org.easysoa.registry.DiscoveryService;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.JsonRegistryApiMarshalling;
import org.easysoa.registry.rest.marshalling.RegistryApiMarshalling;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 * 
 */
@Path("easysoa/registry")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegistryApi {
    
	@POST
	@Path("{doctype}")
    public Object doPost(@Context HttpServletRequest request,
            @PathParam("doctype") String doctype, String body) throws Exception {
        RegistryApiMarshalling marshalling = new JsonRegistryApiMarshalling();
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);

            // Create SoaNode
            SoaNodeInformation soaNodeInfo = marshalling.unmarshall(body);
            DiscoveryService discoveryService = Framework.getService(DiscoveryService.class);
            discoveryService.runDiscovery(documentManager, soaNodeInfo.getId(),
                    soaNodeInfo.getProperties(), soaNodeInfo.getCorrelatedDocuments());
            documentManager.save();
            
            // Return created document
            DocumentService documentService = Framework.getService(DocumentService.class);
            DocumentModel createdModel = documentService.find(documentManager, soaNodeInfo.getId());
            return marshalling.marshall(new SoaNodeInformation(documentManager, createdModel));
        } catch (Exception e) {
            return marshalling.marshallError("Failed to update or create document", e);
        }
    }

	@GET
    @Path("{doctype}")
    public Object doGet(@Context HttpServletRequest request,
            @PathParam("doctype") String doctype) throws ClientException {
        RegistryApiMarshalling marshalling = new JsonRegistryApiMarshalling();
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);

            // Fetch SoaNode list
            String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE "
                    + "ecm:currentLifeCycleState <> 'deleted' AND "
                    + "ecm:isCheckedInVersion = 0 AND " + "ecm:isProxy = 0",
                    new Object[] { doctype }, false, true);
            DocumentModelList soaNodeModelList = documentManager.query(query);

            // Convert data for marshalling
            List<SoaNodeInformation> modelsToMarshall = new LinkedList<SoaNodeInformation>();
            for (DocumentModel soaNodeModel : soaNodeModelList) {
                modelsToMarshall.add(new SoaNodeInformation(new SoaNodeId(soaNodeModel),
                        null, null));
            }

            // Write response
            return marshalling.marshall(modelsToMarshall);
        } catch (Exception e) {
            return marshalling.marshallError("Failed to fetch " + doctype + " list", e);
        }
    }

    @GET
    @Path("{doctype}/{name}")
    public Object doGet(@Context HttpServletRequest request,
            @PathParam("doctype") String doctype, @PathParam("name") String name) {
        SoaNodeId id = new SoaNodeId(doctype, name);
        RegistryApiMarshalling marshalling = new JsonRegistryApiMarshalling();
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DocumentService documentService = Framework.getService(DocumentService.class);
    
            // Fetch SoaNode
            DocumentModel foundDocument = documentService.find(documentManager, id);
            if (foundDocument == null) {
                return new JSONObject().toString();
            }
            else {
                return marshalling.marshall(new SoaNodeInformation(documentManager, foundDocument));
            }
        }
        catch (Exception e) {
            return marshalling.marshallError("Failed to fetch document " + id.toString(), e);
        }
    }

    @PUT
    @Path("{doctype}/{name}")
    public Object doPut(@Context HttpServletRequest request,
            @PathParam("doctype") String doctype, @PathParam("name") String name, String body) throws ClientException {
        RegistryApiMarshalling marshalling = new JsonRegistryApiMarshalling();
        SoaNodeId soaNodeId = new SoaNodeId(doctype, name);
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DocumentService documentService = Framework.getService(DocumentService.class);

            // Check that the target document exists
            DocumentModel modelToUpdate = documentService.find(documentManager, soaNodeId);
            if (modelToUpdate != null) {
                // Create SoaNode
                SoaNodeInformation soaNodeInfo = marshalling.unmarshall(body);
                DiscoveryService discoveryService = Framework.getService(DiscoveryService.class);
                discoveryService.runDiscovery(documentManager, soaNodeId,
                        soaNodeInfo.getProperties(), soaNodeInfo.getCorrelatedDocuments());
                documentManager.save();
                
                // Return created document
                DocumentModel updatedModel = documentService.find(documentManager, soaNodeId);
                return marshalling.marshall(new SoaNodeInformation(documentManager, updatedModel));
            }
            else {
                throw new Exception("Document to update " + soaNodeId.toString() + " doesn't exist");
            }
        } catch (Exception e) {
            return marshalling.marshallError("Failed to update or create document", e);
        }
    }

    @DELETE
    @Path("{doctype}/{name}")
    public Object doDelete(@Context HttpServletRequest request,
            @PathParam("doctype") String doctype, @PathParam("name") String name) throws ClientException {
        RegistryApiMarshalling marshalling = new JsonRegistryApiMarshalling();
        SoaNodeId soaNodeId = new SoaNodeId(doctype, name);
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DocumentService documentService = Framework.getService(DocumentService.class);

            // Delete SoaNode
            documentService.delete(documentManager, soaNodeId);

            return marshalling.marshallSuccess();
        } catch (Exception e) {
            return marshalling.marshallError("Failed to delete document " + soaNodeId.toString(), e);
        }
    }
    
    @DELETE
    @Path("{doctype}/{name}/{correlatedDoctype}/{correlatedName}")
    public Object doDelete(@Context HttpServletRequest request,
            @PathParam("doctype") String doctype, @PathParam("name") String name,
            @PathParam("correlatedDoctype") String correlatedDoctype,
            @PathParam("correlatedName") String correlatedName) throws ClientException {
        RegistryApiMarshalling marshalling = new JsonRegistryApiMarshalling();
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

            return marshalling.marshallSuccess();
        } catch (Exception e) {
            return marshalling.marshallError("Failed to delete document " + soaNodeId.toString(), e);
        }
    }
}
