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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author jguillemotte
 */
public class ReplayHttpControllerServlet extends HttpServlet {

    @Reference
    protected Servlet templateReplayWSDLHTMLListRenderer;
    
    @Reference
    //protected Servlet templateReplayWSDLRenderer;
    protected GenericTemplateRendererItf templateReplayWSDLRenderer;
    
    @Reference
    //protected Servlet templateResponseWSDLRenderer;
    protected GenericTemplateRendererItf templateResponseWSDLRenderer;
    
    @Reference
    protected WsdlDataStoreProvider wsdlDataStoreProvider;
    
    @Reference
    protected TemplateEngine templateEngine;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        if(req.getRequestURI().endsWith("/") && req.getQueryString() == null){
            
            wsdlDataStoreProvider.buildStoreListHTMLResponse(req, resp, templateReplayWSDLHTMLListRenderer);
            
        } else if(req.getQueryString().endsWith("wsdl")) {
            
            String result;
            
            // TODO : A problem remains. The velocity servlet implementation try to load the storeName composite instead of the default conposite
            
            //wsdlDataStoreProvider.buildWSDLResponse(req, resp, templateReplayWSDLRenderer);
            
            ProxyFileStore excf = new ProxyFileStore();
            try {
                String storeName = "";
                // Get the store name (last token of requested resource)
                if(!req.getRequestURL().toString().endsWith("/")){
                    storeName = req.getRequestURL().toString().substring(req.getRequestURL().toString().lastIndexOf("/") + 1);
                } else {
                    storeName = req.getRequestURL().toString().substring(req.getRequestURL().toString().lastIndexOf("target/") +7, req.getRequestURL().toString().lastIndexOf("/"));
                }
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
                //templateReplayWSDLRenderer.service(req, resp);
                Map<String, List<String>> fields = new HashMap<String, List<String>>();
                //List<String> arg0FieldList = new ArrayList<String>();
                //arg0FieldList.add(operationParams);
                //arg0FieldList.add("");
                //fields.put("paramFields)", arg0FieldList);
                fields.put("operationList", templateList);
                //List<String> arg2FieldList = new ArrayList<String>();
                //arg2FieldList.add(storeName);
                //fields.put("storeName", arg2FieldList);
                   
                result = templateReplayWSDLRenderer.execute_custom("wsdlTemplate.xml", storeName, fields, operationParams);

                resp.getWriter().print(result);
            }
            catch(Exception ex){
                super.doGet(req, resp);    
            }
        }
        else {
            super.doGet(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    
        String result = "An error occurs when completing the request, see the error log";
        String storeName;
        String recordId;
        String operation;
        Map<String, List<String>> params = new HashMap<String, List<String>>();        
        
        // Get the XML message content
        Document doc = null;
        try {
            InputStream xml = req.getInputStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(xml);
        } catch (Exception ex) {
            Logger.getLogger(ReplayHttpControllerServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }

        /*
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:twit="http://easysoa.org/Twitter_test_run.xsd">
           <soapenv:Header/>
           <soapenv:Body>
              <twit:reqTemplateRecord_2Request>
                 <!--You may enter the following 2 items in any order-->
                 <tweetNumber>5</tweetNumber>
                 <user>toto</user>
              </twit:reqTemplateRecord_2Request>
           </soapenv:Body>
        </soapenv:Envelope>
        */
        
        if(doc != null){
            // Extract service and operation
            // Get the store name
            Element rootElement = doc.getDocumentElement();
            // TODO : already the same prefix twe ??? Works only with SOAPUI ????
            // No : how to find the good attribute to get the store name ....
            // get the prefix xmlns:twit="http://easysoa.org/Twitter_test_run.xsd
            // with substring between xmlns: and ="http://...
            // change the twe prefix by xsd
            storeName = rootElement.getAttribute("xmlns:twit").substring(rootElement.getAttribute("xmlns:twit").lastIndexOf("/")+1, rootElement.getAttribute("xmlns:twit").lastIndexOf("."));
            
            // Get record ID
            Node body = rootElement.getChildNodes().item(3);
            Node record = body.getChildNodes().item(1);
            recordId = record.getNodeName().substring(record.getNodeName().indexOf("_")+1, record.getNodeName().lastIndexOf("Request"));
            operation = record.getNodeName().substring(record.getNodeName().indexOf(":")+1, record.getNodeName().lastIndexOf("Request"));
            // Get the fields
            NodeList fields = record.getChildNodes();
            for(int i=0; i<fields.getLength(); i++){
                Node field = fields.item(i);
                if(!field.getNodeName().startsWith("#")){
                    List paramList = new ArrayList<String>();
                    paramList.add(field.getNodeValue());
                    params.put(field.getNodeName(), paramList);
                }
            }
        }
        else {
            throw new ServletException("Unable to read XML message content");
        }
        
        ProxyFileStore excf = new ProxyFileStore();
        try {
            //excf.getTemplateFieldSuggestions(storeName, null);
            
            // TODO : not exposed as web service ....
            // use a reference or expose a web service
            //OutMessage message = replayWithTemplate(params, storeName, recordId);
            
            ExchangeRecord record = excf.loadExchangeRecord(storeName, recordId, true);
            // last param set to true = simulation mode
            OutMessage message = templateEngine.renderTemplateAndReplay(storeName, record, params, true);
            
            // Put the out message in the xml response
            req.setAttribute("xmlResponse", message.getMessageContent());
            req.setAttribute("storeName", storeName);
            req.setAttribute("operation", operation);
            
            // Call the rendering template
            //templateResponseWSDLRenderer.service(req, resp);
            Map<String, List<String>> fields = new HashMap<String, List<String>>();
            List<String> arg0FieldList = new ArrayList<String>();
            arg0FieldList.add(message.getMessageContent().getRawContent());
            fields.put("result", arg0FieldList);
            List<String> arg1FieldList = new ArrayList<String>();
            arg1FieldList.add(operation);
            fields.put("operation", arg1FieldList);
            List<String> arg2FieldList = new ArrayList<String>();
            arg2FieldList.add(storeName);
            fields.put("storeName", arg2FieldList);
            result = templateResponseWSDLRenderer.execute_custom("wsdlResponseTemplate.xml", "", fields, null);
        }
        catch(Exception ex){
            throw new ServletException("Unable to get template field suggestions",ex);
            //result = result + "";
        }
        
        // POST requests : a simple implementation of calling these WSDLs (extract service and operation name,
        // then call the TemplateDefinitionService with them to find the template and its .fld,
        // then extract field values as required in.fld from request XML content,
        // then use TemplateExecutor to execute the templated request with these).
        // then format the response to return a SOAP response
        resp.getWriter().print(result);

    }
    
}
