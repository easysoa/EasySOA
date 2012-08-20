package org.easysoa.registry.rest;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

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

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.JsonDocumentMarshalling;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeMarshalling;
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
@Path("easysoa/discovery")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegistryApi {
    
	@POST
	@Path("{doctype}")
    public Object doPost(@Context HttpServletRequest request,
            @PathParam("doctype") String doctype, String body) throws Exception {
        SoaNodeMarshalling marshalling = new JsonDocumentMarshalling();
        try {
            // Initialization
            CoreSession documentManager = SessionFactory.getSession(request);
            DocumentService documentService = Framework.getService(DocumentService.class);

            // Create SoaNode
            SoaNodeInformation soaNodeInfo = marshalling.unmarshall(body);
            DocumentModel createdModel = documentService.create(documentManager, soaNodeInfo.getId(),
                    soaNodeInfo.getId().getName());
            for (Entry<String, Object> entry : soaNodeInfo.getProperties().entrySet()) {
                createdModel.setPropertyValue(entry.getKey(), (Serializable) entry.getValue());
            }
            documentManager.saveDocument(createdModel);
            documentManager.save();
            
            return marshalling.marshall(new SoaNodeInformation(documentManager, createdModel));
        } catch (Exception e) {
            return marshalling.marshallError("Failed to create document", e);
        }
    }

	@GET
    @Path("{doctype}")
    public Object doGet(@Context HttpServletRequest request,
            @PathParam("doctype") String doctype) throws ClientException {
        SoaNodeMarshalling marshalling = new JsonDocumentMarshalling();
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
                modelsToMarshall.add(new SoaNodeInformation(SoaNodeId.fromModel(soaNodeModel),
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
        SoaNodeMarshalling marshalling = new JsonDocumentMarshalling();
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
            @PathParam("doctype") String doctype, @PathParam("name") String name) throws ClientException {
        // Initialization
        CoreSession documentManager = SessionFactory.getSession(request);
        SoaNodeId id = new SoaNodeId(doctype, name);
        
        // SoaNode update
        // TODO
        return id;
    }

    @DELETE
    @Path("{doctype}/{name}")
    public Object doDelete(@Context HttpServletRequest request,
            @PathParam("doctype") String doctype, @PathParam("name") String name) throws ClientException {
        // Initialization
        CoreSession documentManager = SessionFactory.getSession(request);
        SoaNodeId id = new SoaNodeId(doctype, name);
        
        // Document deletion
        // TODO
        return id;
    }
}
