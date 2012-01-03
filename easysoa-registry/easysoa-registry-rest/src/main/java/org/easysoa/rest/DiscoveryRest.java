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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.api.EasySOAApiSession;
import org.easysoa.api.EasySOADocument;
import org.easysoa.api.EasySOALocalApiFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.doctypes.ServiceReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.view.TemplateView;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.spi.container.ContainerRequest;

@Path("easysoa/discovery")
public class DiscoveryRest {

    private static final Log log = LogFactory.getLog(DiscoveryRest.class);
    
    private static final String ERROR = "[ERROR] ";
    
    private JSONObject result = new JSONObject();
    private Map<String, String> commonPropertiesDocumentation;
    
    public DiscoveryRest() {
        try {
            result.put("result", "ok");
        } catch (JSONException e) {
            log.error("JSON init failure", e);
        }
    }

    @GET
    @Path("/")
    public Object doPostDiscoveryRoot() {
        return new TemplateView(new EasySOAAppRoot(), "index.html");
    }

    @GET
    @Path("/environments")
    public Object doGetEnvironmentNames(@Context HttpServletRequest request) throws ClientException {
        CoreSession session = SessionFactory.getSession(request);
        DocumentModelList models = session.query("SELECT * FROM Workspace WHERE ecm:currentLifeCycleState <> 'deleted'");
        JSONArray result = new JSONArray();
        for (DocumentModel model : models) {
            result.put(model.getTitle());
        }
        return result.toString();
    }
    
    @POST
    @Path("/appliimpl")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Object doPostAppliImpl(@Context HttpContext httpContext, @Context HttpServletRequest request) throws Exception {
        EasySOAApiSession api = EasySOALocalApiFactory.createLocalApi(SessionFactory.getSession(request));
        Map<String, String> params = getFormValues(httpContext);
        try {
            EasySOADocument doc = api.notifyAppliImpl(params);
            result.put("documentId", doc.getId());
        }
        catch (Exception e) {
            appendError(e.getMessage());
        }
        return getFormattedResult();
    }

    @GET
    @Path("/appliimpl")
    @Produces(MediaType.APPLICATION_JSON)
    public Object doGetAppliImpl() throws JSONException {
        result = new JSONObject();
        JSONObject params = new JSONObject();
        Map<String, String> commonDef = getCommonPropertiesDocumentation();
        for (Entry<String, String> entry : commonDef.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        Map<String, String> appliImplDef = AppliImpl.getPropertyList();
        for (Entry<String, String> entry : appliImplDef.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        Map<String, String> featureDef = AppliImpl.getFeaturePropertyList();
        for (Entry<String, String> entry : featureDef.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        result.put("parameters", params);
        result.put("description", "Notification concerning an application implementation.");
    
        return getFormattedResult();
    }

    @POST
    @Path("/api")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Object doPostApi(@Context HttpContext httpContext, @Context HttpServletRequest request) throws Exception {
        EasySOAApiSession api = EasySOALocalApiFactory.createLocalApi(SessionFactory.getSession(request));
        Map<String, String> params = getFormValues(httpContext);
        try {
            EasySOADocument doc = api.notifyServiceApi(params);
            result.put("documentId", doc.getId());
        }
        catch (Exception e) {
            appendError(e.getMessage());
        }
        return getFormattedResult();
    }
    
    @GET
    @Path("/api")
    @Produces(MediaType.APPLICATION_JSON)
    public Object doGetApi() throws JSONException {
        result = new JSONObject();
        JSONObject params = new JSONObject();
        Map<String, String> commonDef = getCommonPropertiesDocumentation();
        for (Entry<String, String> entry : commonDef.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        Map<String, String> apiDef = ServiceAPI.getPropertyList();
        for (Entry<String, String> entry : apiDef.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        result.put("parameters", params);
        result.put("description", "API-level notification.");
        return getFormattedResult();
    }

    @POST
    @Path("/service")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Object doPostService(@Context HttpContext httpContext, @Context HttpServletRequest request) throws Exception {
        EasySOAApiSession api = EasySOALocalApiFactory.createLocalApi(SessionFactory.getSession(request));
        Map<String, String> params = getFormValues(httpContext);
        try {
            EasySOADocument doc = api.notifyService(params);
            result.put("documentId", doc.getId());
        }
        catch (Exception e) {
            appendError(e.getMessage());
        }
        return getFormattedResult();
    }

    @GET
    @Path("/service")
    @Produces(MediaType.APPLICATION_JSON)
    public Object doGetService() throws JSONException {
        result = new JSONObject();
        JSONObject params = new JSONObject();
        Map<String, String> commonDef = getCommonPropertiesDocumentation();
        for (Entry<String, String> entry : commonDef.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        Map<String, String> serviceDef = Service.getPropertyList();
        for (Entry<String, String> entry : serviceDef.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        result.put("parameters", params);
        result.put("description", "Service-level notification.");
        return getFormattedResult();
    }
    
    @GET
    @Path("/service/jsonp")
    @Produces("application/javascript")
    public Object doGetServiceJSONP(@Context HttpContext httpContext, @Context HttpServletRequest request,
            @QueryParam("callback") String callback) throws Exception {
        EasySOAApiSession api = EasySOALocalApiFactory.createLocalApi(SessionFactory.getSession(request));
        Map<String, List<String>> multiValuedParams = httpContext.getRequest().getQueryParameters();
        Map<String, String> params = new HashMap<String, String>();
        for (Entry<String, List<String>> entry : multiValuedParams.entrySet()) {
            List<String> value = entry.getValue();
            if (value != null) {
                params.put(entry.getKey(), value.get(value.size() - 1));
            }
        }
        try {
            EasySOADocument doc = api.notifyService(params);
            result.put("documentId", doc.getId());
        }
        catch (Exception e) {
            appendError(e.getMessage());
        }
        return getFormattedResult(callback);
    }
    
    @POST
    @Path("/servicereference")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Object doPostServiceReference(@Context HttpContext httpContext, @Context HttpServletRequest request) throws Exception {
        EasySOAApiSession api = EasySOALocalApiFactory.createLocalApi(SessionFactory.getSession(request));
        Map<String, String> params = getFormValues(httpContext);
        try {
            EasySOADocument doc = api.notifyServiceReference(params);
            result.put("documentId", doc.getId());
        }
        catch (Exception e) {
            appendError(e.getMessage());
        }
        return getFormattedResult();
    }

    @GET
    @Path("/servicereference")
    @Produces(MediaType.APPLICATION_JSON)
    public Object doGetServiceReference() throws JSONException {
        result = new JSONObject();
        JSONObject params = new JSONObject();
        Map<String, String> commonDef = getCommonPropertiesDocumentation();
        for (Entry<String, String> entry : commonDef.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        Map<String, String> serviceReferenceDef = ServiceReference.getPropertyList();
        for (Entry<String, String> entry : serviceReferenceDef.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        result.put("parameters", params);
        result.put("description", "Service-level notification.");
        return getFormattedResult();
    }

    @POST
    @Path("/{all:.*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object doPostInvalid() throws JSONException, LoginException {
        appendError("Content type should be 'application/x-www-form-urlencoded'");
        return getFormattedResult();
    }

    /**
     * Appends an error to a JSON object (in the "result" item)
     * @param json
     * @param msg
     * @throws JSONException
     */
    private void appendError(String msg) {
        try {
            String formattedMsg = ERROR+msg;
            Object existingResult;
                existingResult = result.get("result");
            if (existingResult.equals("ok")) {
                result.put("result", formattedMsg);
            }
            else {
                result.accumulate("result", formattedMsg);
            }
        } catch (JSONException e) {
            log.error("Failed to append error '"+msg+"' in response", e);
        }
    }

    // TODO Refactoring
    public static Map<String, String> getFormValues(HttpContext httpContext) {
        /*
         * When accessing the form the usual way, the returned Form is empty,
         * and the following Warning is logged. This hack avoids the problem.
         * 
         * "ATTENTION: A servlet POST request, to the URI ###, contains form
         * parameters in the request body but the request body has been 
         * consumed by the servlet or a servlet filter accessing the request parameters.
         * Only resource methods using @FormParam will work as expected. Resource methods
         * consuming the request body by other means will not work as expected."
         */
        Form params = (Form) ((ContainerRequest) httpContext.getRequest()).
                getProperties().get("com.sun.jersey.api.representation.form");
        
        // Keep only the first value for each key (instead of a list of values) 
        Map<String, String> map = new HashMap<String, String>();
        for (Entry<String, List<String>> entry : params.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                map.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        
        return map;
    }

    private Map<String, String> getCommonPropertiesDocumentation() throws JSONException {
        if (commonPropertiesDocumentation == null) {
            commonPropertiesDocumentation = new HashMap<String, String>();
            Map<String, String> dcPropertyList = EasySOADoctype.getDublinCorePropertyList();
            for (Entry<String, String> entry : dcPropertyList.entrySet()) {
                commonPropertiesDocumentation.put(entry.getKey(), entry.getValue());
            }
            Map<String, String> commonPropertyList = EasySOADoctype.getCommonPropertyList();
            for (Entry<String, String> entry : commonPropertyList.entrySet()) {
                commonPropertiesDocumentation.put(entry.getKey(), entry.getValue());
            }
        }
        return commonPropertiesDocumentation;
    }
    
    /**
     * Formats the JSONObject into a string
     * @param result
     * @return
     */
    private String getFormattedResult() {
        try {
            return result.toString(2);
        }
        catch (JSONException e) {
            return "{ result: \""+ERROR+"Could not format results to JSON.\"}";
        }
    }
    
    private String getFormattedResult(String callback) {
        String result = getFormattedResult();
        if (callback != null) {
            result = callback + '(' + result + ')';
        }
        return result;
    }
    
}