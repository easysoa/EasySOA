/**
 * EasySOA Proxy
 * Copyright 2011-2013 Open Wide
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

package org.easysoa.proxy.core.api.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.easysoa.proxy.core.api.records.persistence.filesystem.ProxyFileStore;
import org.osoa.sca.annotations.Reference;

/**
 *
 * @author jguillemotte
 */
public class ReplayHttpControllerServlet extends HttpServlet {

    @Reference
    protected Servlet templateReplayWSDLHTMLListRenderer;
    
    @Reference
    protected Servlet templateReplayWSDLRenderer;
    
    @Reference
    protected WsdlDataStoreProvider wsdlDataStoreProvider;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        if(req.getRequestURI().endsWith("/") && req.getQueryString() == null){
            
            wsdlDataStoreProvider.buildStoreListHTMLResponse(req, resp, templateReplayWSDLHTMLListRenderer);
            
        } else if(req.getQueryString().endsWith("wsdl")) {
            
            // TODO : A problem remains. The velocity servlet implementation try to load the storeName composite instead of the default conposite
            
            //wsdlDataStoreProvider.buildWSDLResponse(req, resp, templateReplayWSDLRenderer);
            
            ProxyFileStore excf = new ProxyFileStore();
            try {
                // Get the store name (last token of requested resource)
                String storeName = req.getRequestURL().toString().substring(req.getRequestURL().toString().lastIndexOf("/") + 1);
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
                req.setAttribute("request", req);
                req.setAttribute("response", resp);

                // inject parameters as Velocity variables.
                req.setAttribute("storeName", storeName);
                req.setAttribute("operationList", templateList);

                // Inject operation params structure
                req.setAttribute("paramFields", operationParams);            

                // problem in this case, the servletTemplateEngine try to get the runanme template ....
                // Wrapper ???
                
                templateReplayWSDLRenderer.service(req, resp);
            }
            catch(Exception ex){
                super.doGet(req, resp);    
            }
        }
        else {
            super.doGet(req, resp);
        }
    }
    
    /*@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    
        // Extract service and operation
        
        //ProxyFileStore excf = new ProxyFileStore();
        //excf.getTemplateFieldSuggestions(null, null)
        
        // POST requests : a simple implementation of calling these WSDLs (extract service and operation name,
        // then call the TemplateDefinitionService with them to find the template and its .fld,
        // then extract field values as required in.fld from request XML content,
        // then use TemplateExecutor to execute the templated request with these).
        resp.getWriter().println("POST request : implementation in progress !");

    }*/
    
}
