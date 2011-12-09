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
import javax.ws.rs.core.Context;

import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.Workspace;
import org.easysoa.services.DocumentService;
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
import org.nuxeo.ecm.core.api.IdRef;
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
                        " AND ecm:currentLifeCycleState <> 'deleted'");
                
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
                    DocumentModelList environmentServiceModelsWithoutMatch = new DocumentModelListImpl();
                    for (DocumentModel environmentServiceModel : environmentServiceModels) {
                        DocumentModel workspaceServiceModel = popReferencedService(environmentServiceModel, workspaceServiceModels);
                        if (workspaceServiceModel != null) {
                            serviceEntries.put(getServiceEntry(workspaceServiceModel, environmentServiceModel));
                        }
                        else {
                            environmentServiceModelsWithoutMatch.add(environmentServiceModel);
                        }
                    }
                    for (DocumentModel environmentServiceModel : environmentServiceModelsWithoutMatch) {
                        serviceEntries.put(getServiceEntry(null, environmentServiceModel));
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
    public Object getServiceById(@Context HttpServletRequest request, 
            @PathParam("serviceid") String serviceid) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        DocumentModel worskspaceServiceModel = session.getDocument(new IdRef(serviceid));
        String referenceId = (String) worskspaceServiceModel.getProperty(Service.SCHEMA, Service.PROP_REFERENCESERVICE);
        DocumentModel referencedServiceModel = (referenceId != null) ? session.getDocument(new IdRef(referenceId)) : null;
        return getServiceEntry(session.getDocument(new IdRef(serviceid)), referencedServiceModel);
    }

    
    @POST
    @Path("/service/{serviceid}/linkto/{referenceid}")
    public Object createServiceReference(@Context HttpServletRequest request, 
            @PathParam("serviceid") String serviceid, 
            @PathParam("referenceid") String referenceid) throws Exception {
    
        CoreSession session = SessionFactory.getSession(request);
        
        DocumentModel localServiceModel = session.getDocument(new IdRef(serviceid));
        if (localServiceModel != null) {
            if (session.exists(new IdRef(referenceid))) {
                localServiceModel.setProperty(Service.SCHEMA, Service.PROP_REFERENCESERVICE, referenceid);
                localServiceModel.setProperty(Service.SCHEMA, Service.PROP_REFERENCESERVICEORIGIN, "Manually set");
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
        return new JSONObject().toString();
    }

    
    @GET
    @Path("/service/{serviceid}/matches")
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
        }
        return result.toString();
    }
    

    @GET
    @Path("/validators")
    public Object getValidators(@Context HttpServletRequest request) throws Exception {
        ServiceValidationService validationService = Framework.getService(ServiceValidationService.class);
        JSONObject result = new JSONObject();
        List<ServiceValidator> validators = validationService.getValidators();
        for (ServiceValidator validator : validators) {
            result.put(validator.getName(), validator.getLabel());
        }
        return result.toString();
    }
    
    
    private JSONObject getServiceEntry(DocumentModel workspaceServiceModel, DocumentModel referencedServiceModel) throws JSONException, ClientException {
        JSONObject serviceEntry = new JSONObject();
        serviceEntry.put("localService", getDocumentModelAsJSON(workspaceServiceModel));
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

    
    private DocumentModel popReferencedService(DocumentModel environmentServiceModel, DocumentModelList workspaceServiceModels) throws ClientException {
        // Init
        String idToMatch = environmentServiceModel.getId();
        DocumentModel matchingService = null;
        
        // Find
        for (DocumentModel worskpaceServiceModel : workspaceServiceModels) {
            if (idToMatch.equals(worskpaceServiceModel.getProperty(Service.SCHEMA, Service.PROP_REFERENCESERVICE))) {
                matchingService = worskpaceServiceModel;
                break;
            }
        }
        
        // Remove from list
        if (matchingService != null) {
            workspaceServiceModels.remove(matchingService);
        }
        
        return matchingService;
    }
    
    
    private String formatError(String error) throws JSONException {
        return formatError(error, null);
    }

    
    private String formatError(String error, Exception e) throws JSONException {
        JSONObject errorObject = new JSONObject();
        errorObject.put("error", error);
        if (e != null) {
            errorObject.put("stacktrace", e);
        }
        return errorObject.toString();
    }
    
}