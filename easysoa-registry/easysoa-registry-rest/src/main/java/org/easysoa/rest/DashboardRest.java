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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.Workspace;
import org.easysoa.services.DocumentService;
import org.easysoa.validation.ServiceValidationService;
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

    
    @GET
    @Path("/linkservices")
    public Object linkServices(@Context HttpServletRequest request, 
            @QueryParam("fromid") String fromId, 
            @QueryParam("toid") String toId) throws Exception {

        CoreSession session = SessionFactory.getSession(request);
        
        DocumentModel localServiceModel = session.getDocument(new IdRef(fromId));
        if (localServiceModel != null) {
            if (session.exists(new IdRef(toId))) {
                localServiceModel.setProperty(Service.SCHEMA, Service.PROP_REFERENCESERVICE, toId);
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
    @Path("/servicesstate")
    public Object getServicesState(@Context HttpServletRequest request, @QueryParam("username") String username) throws Exception {

        // Init
        CoreSession session = SessionFactory.getSession(request);
        DocumentService docService = Framework.getService(DocumentService.class);
        JSONArray serviceEntries = new JSONArray();
        
        try {
            
            // Find workspace
            DocumentModel workspaceModel = docService.findWorkspace(session, username);
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
                        DocumentModel workspaceServiceModel = popMatchingService(environmentServiceModel, workspaceServiceModels);
                        if (workspaceServiceModel != null) {
                            addServiceEntry(serviceEntries, workspaceServiceModel, environmentServiceModel);
                        }
                        else {
                            environmentServiceModelsWithoutMatch.add(environmentServiceModel);
                        }
                    }
                    for (DocumentModel environmentServiceModel : environmentServiceModelsWithoutMatch) {
                        addServiceEntry(serviceEntries, null, environmentServiceModel);
                    }
                    
                    for (DocumentModel workspaceServiceModel : workspaceServiceModels) {
                        addServiceEntry(serviceEntries, workspaceServiceModel, null);
                    }
                    
                }
                else {
                    return formatError("Workspace '" + username + "' has no reference environment");
                }
            
            }
            else {
                return formatError("No such workspace: '" + username + "'");
            }
        
        }
        catch (Exception e) {
            return formatError("Failed to query services state", e);
        }
        
        return serviceEntries.toString();
        
    }
    
    private void addServiceEntry(JSONArray serviceEntries, DocumentModel workspaceServiceModel, DocumentModel referencedServiceModel) throws JSONException, ClientException {
        JSONObject serviceEntry = new JSONObject();
        
        // Local service
        JSONObject localService = new JSONObject();
        if (workspaceServiceModel != null) {
            localService.put("id", workspaceServiceModel.getId());
            localService.put("name", workspaceServiceModel.getTitle());
            localService.put("url", workspaceServiceModel.getProperty(Service.SCHEMA, Service.PROP_URL));
            localService.put("isValidated", workspaceServiceModel.getProperty(Service.SCHEMA, Service.PROP_ISVALIDATED));
            localService.put("validationState", getValidationStateAsJSON(workspaceServiceModel));
        }
        serviceEntry.put("localService", localService);
        
        // Referenced service
        JSONObject referencedService = new JSONObject();
        if (referencedServiceModel != null) {
            referencedService.put("id", referencedServiceModel.getId());
            referencedService.put("name", referencedServiceModel.getTitle());
            referencedService.put("url", referencedServiceModel.getProperty(Service.SCHEMA, Service.PROP_URL));
        }
        serviceEntry.put("referencedService", referencedService);
        
        // Add to service entry list
        serviceEntries.put(serviceEntry);
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

    public DocumentModel popMatchingService(DocumentModel environmentServiceModel, DocumentModelList workspaceServiceModels) throws ClientException {
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
    

    public String formatError(String error) throws JSONException {
        return formatError(error, null);
    }

    public String formatError(String error, Exception e) throws JSONException {
        JSONObject errorObject = new JSONObject();
        errorObject.put("error", error);
        if (e != null) {
            errorObject.put("stacktrace", e);
        }
        return errorObject.toString();
    }
    
}