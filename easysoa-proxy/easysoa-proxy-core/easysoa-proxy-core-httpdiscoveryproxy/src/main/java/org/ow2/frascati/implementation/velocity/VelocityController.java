/**
 * EasySOA Proxy Copyright 2011-2013 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */
package org.ow2.frascati.implementation.velocity;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.easysoa.proxy.core.api.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.proxy.core.api.template.AbstractTemplateField;
import org.easysoa.proxy.core.api.template.OutputTemplateField;
import org.easysoa.proxy.core.api.template.TemplateFieldSuggestions;

/**
 *
 * @author jguillemotte
 */
public class VelocityController {

    // TODO : inject velocity components to render store list or WSDL
    
    // TODO : ProxyFileStore as Reference, no proxyFileStore composite at this time
    // @Reference
    // protected ProxyFileStore proxyFileStore;
    
    public static void buildResponse(String requestedResource, VelocityEngine velocityEngine, VelocityContext velocityContext, HttpServletRequest request, HttpServletResponse response) throws IOException {

        if ("GET".equalsIgnoreCase(request.getMethod())) {

            // For the resource /target/ : for each record template, send back a html list of links on template.wsdl
            if (requestedResource.endsWith("/")) {
                // call the build store list method
                buildStoreListHTMLResponse(response, velocityEngine, velocityContext);
                // Generate a WSDl file
            } else if ("wsdl".equalsIgnoreCase(request.getQueryString())) {
                // call the build WSDL method
                buildWSDLResponse(requestedResource, velocityEngine, velocityContext, request, response);
            }
        } else if ("POST".equalsIgnoreCase(request.getMethod())) {
            System.out.println("POST request received !!");
            // POST requests :  a simple implementation of calling these WSDLs (extract service and operation name, 
            // then call the TemplateDefinitionService with them to find the template and its .fld, 
            // then extract field values as required in.fld from request XML content, 
            // then use TemplateExecutor to execute the templated request with these).        		
            response.getWriter().println("POST request : implementation in progress !");
        } 
        // Send a default response ...
        else {
            response.getWriter().println("/target resources called : only GET and POST request are supported !");
            //response.getWriter().println("Add code to get a list of HTML links on generated WSDL corresponding to the template records !");
        }
    }

    /**
     * Build a response containing the HTML formated store list
     *
     * @param response The http response to build
     * @throws IOException If a problem occurs
     */
    public static void buildStoreListHTMLResponse(HttpServletResponse response, VelocityEngine velocityEngine, VelocityContext velocityContext) throws IOException {
        // TODO : pass base address (host + port) as parameter (now hard coded localhost)

        // Get the list of record template stores
        ProxyFileStore excf = new ProxyFileStore();
        List<String> storeList = excf.getExchangeRecordStorelist();

        // Inject velocity template parameters
        velocityContext.put("storeList", storeList);
        //velocityContext.put("baseAddress", "localhost"); // TODO

        // Process the template
        renderTemplate(response, "templates/storeListTemplate.vm", velocityEngine, velocityContext);
    }

    /**
     * Build a response containing a WSDL file. The WSDL file is generated from
     * a FLD file processed by a velocity template
     *
     * @param requestedResource The requested ressource
     * @param wsdlTemplate velocity template to render the WSDL 1.1
     * @param context velocity context
     * @param request The http request
     * @param response The http response to build
     * @throws IOException If a problem occurs
     */
    public static void buildWSDLResponse(String requestedResource, VelocityEngine velocityEngine, VelocityContext velocityContext, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the FLD file and generate WSDL using XSLT transformation or by calling a velocity template
        // TODO : If run not found, returns an error message instead of an incomplete WSDL
        ProxyFileStore excf = new ProxyFileStore();
        try {
            // Get the store name (last token of requested resource)
            String storeName = requestedResource.substring(requestedResource.lastIndexOf("/") + 1);
            // Get the template files recorded in the store, each template => an operation
            List<String> templateListExt = excf.getTemplateList(storeName);
            // Removing template file extension
            // TODO : Do better .....
            List<String> templateList = new ArrayList<String>();
            for (String templateName : templateListExt) {
                // Only the requests are set in the WSDL
                if (templateName.startsWith("req")) {
                    templateList.add(templateName.substring(0, templateName.lastIndexOf(".")));
                }
            }
            HashMap<String, Map<String, List<AbstractTemplateField>>> operationParams = new HashMap<String, Map<String, List<AbstractTemplateField>>>();
            HashMap<String, List<AbstractTemplateField>> paramsList;
            List<AbstractTemplateField> requestOperationParams;
            List<AbstractTemplateField> responseOperationParams;

            for (String templateName : templateList) {
                //templateName = templateName.substring(0, templateName.lastIndexOf("."));
                //String templateIndex = templateName.substring(templateName.lastIndexOf("_")+1, templateName.lastIndexOf("."));
                String templateIndex = templateName.substring(templateName.lastIndexOf("_") + 1);
                // TODO : change the naming convention for the vm and fld files.

                paramsList = new HashMap<String, List<AbstractTemplateField>>();
                operationParams.put(templateName, paramsList);
                requestOperationParams = new ArrayList<AbstractTemplateField>();
                responseOperationParams = new ArrayList<AbstractTemplateField>();
                paramsList.put("inputParams", requestOperationParams);
                paramsList.put("outputParams", responseOperationParams);
                // TODO : Get suggestions for input fields => OK But about the other fields skipped by the correlation, what to do ????
                // But also for output fields => NOK => Need to add correlation method or other to generate a 'output field suggestion file' 
                TemplateFieldSuggestions templateSuggestion = excf.getTemplateFieldSuggestions(storeName, templateIndex);
                for (AbstractTemplateField field : templateSuggestion.getTemplateFields()) {
                    // TODO : do not set the field type here, the type must be set by the field suggester !!
                    // DOne like that temporary because type is not set in the suggester !!
                    field.setFieldType("string");
                    requestOperationParams.add(field);
                }
                // TODO : At the moment, the response is always returned as a string
                AbstractTemplateField responseField = new OutputTemplateField();
                responseField.setFieldName("response");
                responseField.setFieldType("string");
                responseOperationParams.add(responseField);
            }

            // Put the HTTP request and response into the Velocity context.
            velocityContext.put("request", request);
            velocityContext.put("response", response);

            // inject parameters as Velocity variables.
            velocityContext.put("storeName", storeName);
            velocityContext.put("operationList", templateList);

            // Inject operation params structure
            velocityContext.put("paramFields", operationParams);

            // Process the template
            renderTemplate(response, "templates/wsdlTemplate.vm", velocityEngine, velocityContext);

        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Render a velocity template
     *
     * @param response
     * @param template The template to render
     * @param velocityEngine The velocity engine
     * @param velocityContext The velocity context
     * @throws IOException If a problem occurs
     */
    private static void renderTemplate(HttpServletResponse response, String template, VelocityEngine velocityEngine, VelocityContext velocityContext) throws IOException {

        response.setContentType("text/xml");
        OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());

        // Get the velocity template
        Template wsdltemplate = velocityEngine.getTemplate(template);

        // Render the template
        wsdltemplate.merge(velocityContext, osw);
        osw.flush();
    }
}
