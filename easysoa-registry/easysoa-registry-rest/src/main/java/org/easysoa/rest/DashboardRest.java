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

package org.easysoa.rest;

import java.util.List;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.Workspace;
import org.easysoa.services.DeletedDocumentFilter;
import org.easysoa.services.DocumentService;
import org.easysoa.services.EventsHelper;
import org.easysoa.services.ServiceValidationService;
import org.easysoa.validation.CorrelationMatch;
import org.easysoa.validation.ServiceValidator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.api.model.impl.ListProperty;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.runtime.api.Framework;

@Path("easysoa/dashboard")
public class DashboardRest {

    @GET
    @Path("/services/{workspace}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getServicesByWorkspace(@Context HttpServletRequest request, 
            @PathParam("workspace") String workspace) throws Exception {
    
        // Init
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);
        JSONArray serviceEntries = new JSONArray();
        
        try {
            
            // Find workspace
            DocumentModel workspaceModel = docService.findWorkspace(session, workspace);
            if (workspaceModel != null) {
    
                // Gather workspace services
                DocumentModelList workspaceServiceModels = session.query("SELECT * FROM " + Service.DOCTYPE +
                        " WHERE ecm:path STARTSWITH '" + workspaceModel.getPathAsString() + "'" +
                        " AND ecm:currentLifeCycleState <> 'deleted' AND ecm:currentLifeCycleState <> 'obsolete'");
                
                // Find environment
                DocumentModel environmentModel = docService.findEnvironment(session, 
                        (String) workspaceModel.getProperty(Workspace.SCHEMA, Workspace.PROP_REFERENCEDENVIRONMENT));
                if (environmentModel != null) {
    
                    // Gather reference services
                    DocumentModelList environmentServiceModels = session.query("SELECT * FROM " + Service.DOCTYPE +
                            " WHERE ecm:path STARTSWITH '" + environmentModel.getPathAsString() + "'" +
                            " AND ecm:currentLifeCycleState <> 'deleted'");
                    
                    // Build response, with services in order:
                    // 1. Matching services
                    // 2. Reference services without match (= in environment only)
                    // 3. Workspace services without match
                    DocumentModelList matchedEnvironmentServiceModels = new DocumentModelListImpl();
                    for (DocumentModel environmentServiceModel : environmentServiceModels) {
                        DocumentModelList matchingWorkspaceServiceModels = getLocalServices(environmentServiceModel, workspaceServiceModels);
                        if (!workspaceServiceModels.isEmpty()) {
                            for (DocumentModel workspaceServiceModel : matchingWorkspaceServiceModels) {
                                serviceEntries.put(getServiceEntry(workspaceServiceModel, environmentServiceModel));
                                matchedEnvironmentServiceModels.add(environmentServiceModel);
                                workspaceServiceModels.remove(workspaceServiceModel);
                            }
                        }
                    }
                    for (DocumentModel environmentServiceModel : environmentServiceModels) {
                        if (!matchedEnvironmentServiceModels.contains(environmentServiceModel)) {
                            serviceEntries.put(getServiceEntry(null, environmentServiceModel));
                        }
                    }
                    for (DocumentModel workspaceServiceModel : workspaceServiceModels) {
                        serviceEntries.put(getServiceEntry(workspaceServiceModel, null));
                    }
                    
                }
                else {
                    return formatError("Workspace '" + workspace + "' has no reference environment");
                }
            
            }
            else {
                return formatError("No such workspace: '" + workspace + "'");
            }
        
        }
        catch (Exception e) {
            return formatError("Failed to query services state", e);
        }
        
        return serviceEntries.toString();
        
    }
    
    
    @GET
    @Path("/service/{serviceid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getServiceById(@Context HttpServletRequest request, 
            @PathParam("serviceid") String serviceid) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentModel worskspaceServiceModel = session.getDocument(new IdRef(serviceid));
        String referencePath = (String) worskspaceServiceModel.getProperty(Service.SCHEMA, Service.PROP_REFERENCESERVICE);
        DocumentModel referencedServiceModel = (referencePath != null) ? session.getDocument(new PathRef(referencePath)) : null;
        return getServiceEntry(session.getDocument(new IdRef(serviceid)), referencedServiceModel).toString();
    }

    
    @GET
    @Path("/service/{serviceid}/matches")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getServiceMatches(@Context HttpServletRequest request,
            @PathParam("serviceid") String serviceId) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        ServiceValidationService validationService = Framework.getService(ServiceValidationService.class);
        SortedSet<CorrelationMatch> findCorrelatedServices = validationService.findCorrelatedServices(session, session.getDocument(new IdRef(serviceId)));
        JSONArray result = new JSONArray();
        for (CorrelationMatch match : findCorrelatedServices) {
            JSONObject matchJSON = new JSONObject();
            matchJSON.put("id", match.getDocumentModel().getId());
            matchJSON.put("title", match.getDocumentModel().getTitle());
            matchJSON.put("correlationRate", match.getCorrelationRateAsPercentage());
            result.put(matchJSON);
        }
        return result.toString();
    }
    

    @GET
    @Path("/validators")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getValidators(@Context HttpServletRequest request) throws Exception {
        ServiceValidationService validationService = Framework.getService(ServiceValidationService.class);
        JSONObject result = new JSONObject();
        List<ServiceValidator> validators = validationService.getValidators();
        for (ServiceValidator validator : validators) {
            result.put(validator.getName(), validator.getLabel());
        }
        return result.toString();
    }


    @POST
    @Path("/document/{docId}/validate")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getValidators(@Context HttpServletRequest request, @PathParam("docId") String docId) throws Exception {
        
        // XXX Untested
        
        CoreSession session = SessionFactory.getSession(request);
        DocumentModel doc = session.getDocument(new IdRef(docId));
        JSONObject result = new JSONObject();
        
        if (doc != null) {
            try {
                EventsHelper.fireDocumentEvent(session, EventsHelper.EVENTTYPE_VALIDATIONREQUEST, doc);
                result.put("result", "ok");
            }
            catch (Exception e) {
                return formatError("Couldn't run validation", e);
            }
        }
        else {
            return formatError("Document not found");
        }
        
        return result.toString();
    }
    
    
    @GET
    @Path("/deployables/{workspace}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getDeployablesByWorkspace(@Context HttpServletRequest request,
            @PathParam("workspace") String workspace) throws Exception {
        // Init
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);
        JSONArray applications = new JSONArray();
        
        // Fetch applications
        DocumentModel workspaceModel = docService.findWorkspace(session, workspace);
        DocumentModelList appliImplModels = session.getChildren(workspaceModel.getRef(),
                AppliImpl.DOCTYPE, new DeletedDocumentFilter(), null);
        for (DocumentModel appliImplModel : appliImplModels) {
            JSONObject application = new JSONObject();
            application.put("title", appliImplModel.getTitle());
            
            // Extract application deployables
            JSONArray deployables = new JSONArray();
            ListProperty deployablesProp = (ListProperty) appliImplModel.getProperty(AppliImpl.SCHEMA_PREFIX + AppliImpl.PROP_DEPLOYABLES);
            for (Property deployableProp : deployablesProp.getChildren()) {
                JSONObject deployable = new JSONObject();
                deployable.put("deployableName", deployableProp.get(AppliImpl.SUBPROP_DEPLOYABLENAME).getValue());
                deployable.put("deployableVersion", deployableProp.get(AppliImpl.SUBPROP_DEPLOYABLEVERSION).getValue());
                deployables.put(deployable);
            }
            application.put("deployables", deployables);
            
            applications.put(application);
        }
        
        return applications.toString();
    }


    @POST
    @Path("/service/{serviceid}/linkto/{referenceid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object createServiceReference(@Context HttpServletRequest request, 
            @PathParam("serviceid") String serviceid, 
            @PathParam("referenceid") String referenceid) throws Exception {
        
        CoreSession session = SessionFactory.getSession(request);
        DocumentModel localServiceModel = session.getDocument(new IdRef(serviceid));
        if (localServiceModel != null) {
            boolean referenceidIsNull = "null".equals(referenceid);
            DocumentRef referenceRef = new IdRef(referenceid);
            if (referenceidIsNull || session.exists(new IdRef(referenceid))) {
                DocumentModel referenceModel = session.getDocument(referenceRef);
                String newReferencePath = null, newReferenceOrigin = null;
                if (!referenceidIsNull) {
                    newReferencePath = referenceModel.getPathAsString();
                    newReferenceOrigin = "Manually set";
                    if (localServiceModel.getAllowedStateTransitions().contains("approve")) {
                        localServiceModel.followTransition("approve");
                    }
                }
                else if (localServiceModel.getAllowedStateTransitions().contains("backToProject")) {
                    localServiceModel.followTransition("backToProject");
                }
                localServiceModel.setProperty(Service.SCHEMA, Service.PROP_REFERENCESERVICE, newReferencePath);
                localServiceModel.setProperty(Service.SCHEMA, Service.PROP_REFERENCESERVICEORIGIN, newReferenceOrigin);
                session.saveDocument(localServiceModel);
                session.save();
            }
            else {
                return formatError("Referenced service doesn't exist anymore");
            }
        }
        else {
            return formatError("Local service doesn't exist anymore");
        }
        return new JSONObject("{result: 'ok'}").toString();
    }
    
    @POST
    @Path("/service/{serviceid}/lifecycle/{transition}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object followLifeCycleTransition(@Context HttpServletRequest request, 
            @PathParam("serviceid") String serviceid, 
            @PathParam("transition") String transition) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentModel localServiceModel = session.getDocument(new IdRef(serviceid));
        if (localServiceModel != null) {
            try {
                localServiceModel.followTransition(transition);
                session.saveDocument(localServiceModel);
                session.save();
            }
            catch (ClientException e) {
                return formatError("Failed to follow transition '" + transition + "': " + e.getMessage());
            }
        }
        else {
            return formatError("Specified service doesn't exist anymore");
        }
        return new JSONObject("{result: 'ok'}").toString();
    }

    private JSONObject getServiceEntry(DocumentModel workspaceServiceModel, DocumentModel referencedServiceModel) throws JSONException, ClientException {
        JSONObject serviceEntry = getDocumentModelAsJSON(workspaceServiceModel);
        serviceEntry.put("referencedService", getDocumentModelAsJSON(referencedServiceModel));
        return serviceEntry;
    }
    
    private JSONObject getDocumentModelAsJSON(DocumentModel model) throws JSONException, ClientException {
        JSONObject modelJSON = new JSONObject();
        if (model != null) {
            modelJSON.put("id", model.getId());
            modelJSON.put("name", model.getTitle());
            modelJSON.put("url", model.getProperty(Service.SCHEMA, Service.PROP_URL));
            modelJSON.put("isValidated", model.getProperty(Service.SCHEMA, Service.PROP_ISVALIDATED));
            modelJSON.put("lifeCycleState", model.getCurrentLifeCycleState());
            modelJSON.put("validationState", getValidationStateAsJSON(model));
        }
        return modelJSON;
    }
    
    
    private JSONObject getValidationStateAsJSON(DocumentModel serviceModel) throws PropertyException, ClientException, JSONException {
        JSONObject validationStateJSON = new JSONObject();
        ListProperty validationStateProperty = (ListProperty) serviceModel.getProperty(Service.SCHEMA_PREFIX + Service.PROP_VALIDATIONSTATE);
        for (Property validatorResult : validationStateProperty.getChildren()) {
            JSONObject validatorResultJSON = new JSONObject();
            validatorResultJSON.put(Service.SUBPROP_ISVALIDATED, validatorResult.get(Service.SUBPROP_ISVALIDATED).getValue());
            validatorResultJSON.put(Service.SUBPROP_VALIDATIONLOG, validatorResult.get(Service.SUBPROP_VALIDATIONLOG).getValue());
            validationStateJSON.put((String) validatorResult.get(Service.SUBPROP_VALIDATORNAME).getValue(), validatorResultJSON);
        }
        return validationStateJSON;
    }

    
    private DocumentModelList getLocalServices(DocumentModel environmentServiceModel, DocumentModelList workspaceServiceModels) throws ClientException {
        String pathToMatch = environmentServiceModel.getPathAsString();
        DocumentModelList matchingServices = new DocumentModelListImpl();
        for (DocumentModel worskpaceServiceModel : workspaceServiceModels) {
            if (pathToMatch.equals(worskpaceServiceModel.getProperty(Service.SCHEMA, Service.PROP_REFERENCESERVICE))) {
                matchingServices.add(worskpaceServiceModel);
            }
        }
        return matchingServices;
    }
    
    
    private String formatError(String error) throws JSONException {
        return formatError(error, null);
    }

    
    private String formatError(String error, Exception e) throws JSONException {
        JSONObject errorObject = new JSONObject();
        errorObject.put("result", error);
        if (e != null) {
            errorObject.put("stacktrace", e);
        }
        return errorObject.toString();
    }
    
}