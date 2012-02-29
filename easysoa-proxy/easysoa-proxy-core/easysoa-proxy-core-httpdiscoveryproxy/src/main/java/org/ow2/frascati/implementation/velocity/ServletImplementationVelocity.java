/**
 * OW2 FraSCAti: SCA Implementation Velocity
 * Copyright (C) 2011 Inria, University Lille 1
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * Contact: frascati@ow2.org
 *
 * Author: Philippe Merle
 *
 * Contributor: Romain Rouvoy
 *
 */
package org.ow2.frascati.implementation.velocity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.easysoa.EasySOAConstants;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.template.TemplateField;
import org.easysoa.template.TemplateFieldSuggestions;
import org.eclipse.stp.sca.Component;
import org.eclipse.stp.sca.ComponentReference;
import org.eclipse.stp.sca.PropertyValue;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;
import org.ow2.frascati.assembly.factory.api.ProcessingContext;
import org.ow2.frascati.util.Stream;

import com.openwide.easysoa.proxy.PropertyManager;

/**
 * OW2 FraSCAti implementation template component class for Servlet.
 * 
 * @author Philippe Merle - Inria
 * @version 1.5
 */
@Service(Servlet.class)
public class ServletImplementationVelocity extends ImplementationVelocity {
    // ---------------------------------------------------------------------------
    // Internal state.
    // --------------------------------------------------------------------------

    /**
     * Mapping between file extensions and MIME types.
     */
    private static Properties extensions2mimeTypes = new Properties();
    static {
        // Load mapping between file extensions and MIME types.
        try {
            extensions2mimeTypes.load(ServletImplementationVelocity.class
                    .getClassLoader().getResourceAsStream(
                            ServletImplementationVelocity.class.getPackage()
                                    .getName().replace('.', '/')
                                    + "/extensions2mimeTypes.properties"));
        } catch (IOException ioe) {
            throw new Error(ioe);
        }
    }

    // TODO: Allow to configure this list.
    static final List<String> templatables = Arrays.asList(new String[] { "",
            ".html", ".txt", ".xml" });

    // ---------------------------------------------------------------------------
    // Internal methods.
    // --------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // The requested resource.
        String requestedResource = request.getPathInfo();

        System.out.println("Requested " + requestedResource);

        // If no requested resource then redirect to '/'.
        if (requestedResource == null || requestedResource.equals("")) {
            response.sendRedirect(request.getRequestURL().append('/')
                    .toString());
            return;
        }

        // If the requested resource is '/' then use the default resource.
        if (requestedResource.equals("/")) {
            requestedResource = '/' + this.defaultResource;
        }

        /**
		 * EASYSOA HACK BEGIN
		 * The hack code is triggered when a request is done on /target
		 * Not the best solution but it works 
         */
        // TODO : find an other way to trigger custom code
        if(requestedResource.contains("/target")){
        	if("GET".equalsIgnoreCase(request.getMethod())){
        		System.out.println("GET request received !!");
	        	// Get the list of record template
	        	// TODO : remove the ExchangeRecordFileStore and use TemplateDefinitionService
	        	ProxyFileStore excf = new ProxyFileStore();
	        	//excf.setStorePath(PropertyManager.getProperty("path.template.store"));
	        	//List<String> templateFileList = excf.getTemplateList();
	        	List<ExchangeRecordStore> storeList = excf.getExchangeRecordStorelist();
	        	
	        	// For the resource /target/ : for each record template, send back a html list of links on template.wsdl
	        	if(requestedResource.endsWith("/")){
	        		if(storeList.size()>0){
	        			response.getWriter().println("<html><body><ul>");
		        		for(ExchangeRecordStore recordStore : storeList){
		        			response.getWriter().println("<li><a href=\"http://localhost:" + EasySOAConstants.HTML_FORM_GENERATOR_PORT + "/runManager/target/" + recordStore.getStoreName() + "?wsdl\">" + recordStore.getStoreName() + "</a> </li>");
		        		}
		        		response.getWriter().println("</ul></body></html>");
	        		}
	        	}
	        	else if("wsdl".equalsIgnoreCase(request.getQueryString())){
	        		// Get the FLD file and generate WSDL using XSLT transformation or by calling a velocity template
	        		// TODO : If run not found, returns an error message instead of an incomplete WSDL
	        		try {
	        			// Get the store name (last token of requested resource)
	        			String storeName = requestedResource.substring(requestedResource.lastIndexOf("/")+1);
	        			System.out.println("StoreName = " + storeName);
	        			// Get the template files recorded in the store, each template => an operation
	        			List<String> templateListExt = excf.getTemplateList(storeName);
        				// Removing template file extension
	        			// TODO : Do better .....
	        			List<String> templateList = new ArrayList<String>();
	        			for(String templateName : templateListExt){
	        				// Only the requests are set in the WSDL
	        				if(templateName.startsWith("req")){
	        					templateList.add(templateName.substring(0, templateName.lastIndexOf(".")));
	        				}
	        			}
	        			HashMap<String, Map<String, List<TemplateField>>> operationParams = new HashMap<String, Map<String, List<TemplateField>>>();
	        			HashMap<String, List<TemplateField>> paramsList;
	        			List<TemplateField> requestOperationParams;
	        			List<TemplateField> responseOperationParams;
	        			
	        			for(String templateName : templateList){
	        				//templateName = templateName.substring(0, templateName.lastIndexOf("."));
	        				//System.out.println("templateName = " + templateName);	        				
	        				//String templateIndex = templateName.substring(templateName.lastIndexOf("_")+1, templateName.lastIndexOf("."));
	        				String templateIndex = templateName.substring(templateName.lastIndexOf("_")+1);
	        				System.out.println("template index = " + templateIndex);
	        				// TODO : change the naming convention for the vm and fld files.
	        				
	        				paramsList = new HashMap<String, List<TemplateField>>();
	        				operationParams.put(templateName, paramsList);
	        				requestOperationParams = new ArrayList<TemplateField>();
	        				responseOperationParams = new ArrayList<TemplateField>();
	        				paramsList.put("inputParams", requestOperationParams);
	        				paramsList.put("outputParams", responseOperationParams);
	        				// TODO : Get suggestions for input fields => OK But about the other fields skipped by the correlation, what to do ????
	        				// But also for output fields => NOK => Need to add correlation method or other to generate a 'output field suggestion file' 
	        				TemplateFieldSuggestions templateSuggestion = excf.getTemplateFieldSuggestions(storeName, templateIndex);
	       	            	for(TemplateField field : templateSuggestion.getTemplateFields()){
	       	            		// TODO : do not set the field type here, the type must be set by the field suggester !!
	       	            		// DOne like that temporary because type is not set in the suggester !!
	       	            		field.setFieldType("string");
	       	            		requestOperationParams.add(field);
	       	            	}
	       	            	// TODO : At the moment, the response is always returned as a string
	       	            	TemplateField responseField = new TemplateField();
	       	            	responseField.setFieldName("response");
	       	            	responseField.setFieldType("string");
	       	            	responseOperationParams.add(responseField);
	        			}
	        			// Call the velocity template to render the WSDL 1.1
	        			Template wsdltemplate = this.velocityEngine.getTemplate("templates/wsdlTemplate.vm");
	        			
	    	            VelocityContext context = new VelocityContext(this.velocityContext);
	    	            // Put the HTTP request and response into the Velocity context.
	    	            context.put("request", request);
	    	            context.put("response", response);
	    	            
	    	            // inject parameters as Velocity variables.
	   	            	context.put("storeName", storeName);
	   	            	context.put("operationList", templateList);
	
	   	            	// Inject operation params structure
	   	            	context.put("paramFields", operationParams);
	        			
	    	            // Process the template.
	   	            	response.setContentType("text/xml");
	    	            OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
	    	            wsdltemplate.merge(context, osw);
	    	            osw.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
	        	}
        	} else if("POST".equalsIgnoreCase(request.getMethod())) {
        		System.out.println("POST request received !!");
        		// POST requests :  a simple implementation of calling these WSDLs (extract service and operation name, 
        		// then call the TemplateDefinitionService with them to find the template and its .fld, 
        		// then extract field values as required in.fld from request XML content, 
        		// then use TemplateExecutor to execute the templated request with these).        		
        		response.getWriter().println("POST request : implementation in progress !");
        	}
        	// Send a default response ...
        	// TODO : change this response
        	else {
            	response.getWriter().println("/target resources called : HACK code triggered !");
            	response.getWriter().println("Add code to get a list of HTML links on generated WSDL corresponding to the template records !");        		
        	}
        }
        /**
		 * EASYSOA HACK END
         */   
        else {
	        // Compute extension of the requested resource.
	        int idx = requestedResource.lastIndexOf('.');
	        String extension = (idx != -1) ? requestedResource.substring(idx)
	                : ".txt";
	
	        // Set response status to OK.
	        response.setStatus(HttpServletResponse.SC_OK);
	        // Set response content type.
	        response.setContentType(extensions2mimeTypes.getProperty(extension));
	
	        // Is a templatable requested resource?
	        if (templatables.contains(extension)) {
	            // Get the requested resource as a Velocity template.
	            Template template = null;
	            try {
	                template = this.velocityEngine.getTemplate(requestedResource
	                        .substring(1));
	            } catch (Exception exc) {
	                exc.printStackTrace(System.err);
	                // Requested resource not found.
	                super.service(request, response);
	                return;
	            }
	
	            // Create a Velocity context connected to the component's Velocity
	            // context.
	            VelocityContext context = new VelocityContext(this.velocityContext);
	            // Put the HTTP request and response into the Velocity context.
	            context.put("request", request);
	            context.put("response", response);
	
	            // inject HTTP parameters as Velocity variables.
	            Enumeration<?> parameterNames = request.getParameterNames();
	            while (parameterNames.hasMoreElements()) {
	                String parameterName = (String) parameterNames.nextElement();
	                context.put(parameterName, request.getParameter(parameterName));
	            }
	
	            // TODO: should not be called but @Lifecycle does not work as
	            // expected.
	            registerScaProperties();
	
	            // Process the template.
	            OutputStreamWriter osw = new OutputStreamWriter(
	                    response.getOutputStream());
	            template.merge(context, osw);
	            osw.flush();
	
	        } else {
	            // Search the requested resource into the class loader.
	            InputStream is = this.classLoader.getResourceAsStream(this.location
	                    + requestedResource);
	            if (is == null) {
	                // Requested resource not found.
	                super.service(request, response);
	                return;
	            }
	            // Copy the requested resource to the HTTP response output stream.
	            Stream.copy(is, response.getOutputStream());
	            is.close();
	        }
        }
    }

    // ---------------------------------------------------------------------------
    // Public methods.
    // --------------------------------------------------------------------------

    public static void generateContent(Component component,
            ProcessingContext processingContext, String outputDirectory,
            String packageGeneration, String contentClassName)
            throws FileNotFoundException {
        // TODO: Certainly not required with the next Tinfi release.
        File packageDirectory = new File(outputDirectory + '/'
                + packageGeneration.replace('.', '/'));
        packageDirectory.mkdirs();

        PrintStream file = new PrintStream(new FileOutputStream(new File(
                packageDirectory, contentClassName + ".java")));

        file.println("package " + packageGeneration + ";\n");
        file.println("public class " + contentClassName + " extends "
                + ServletImplementationVelocity.class.getName());
        file.println("{");
        int index = 0;
        for (PropertyValue propertyValue : component.getProperty()) {
            // Get the property value and class.
            Object propertyValueObject = processingContext.getData(
                    propertyValue, Object.class);
            Class<?> propertyValueClass = (propertyValueObject != null) ? propertyValueObject
                    .getClass() : String.class;
            file.println("  @" + Property.class.getName() + "(name = \""
                    + propertyValue.getName() + "\")");
            file.println("  protected " + propertyValueClass.getName()
                    + " property" + index + ";");
            index++;
        }
        index = 0;
        for (ComponentReference componentReference : component.getReference()) {
            file.println("  @" + Reference.class.getName() + "(name = \""
                    + componentReference.getName() + "\")");
            file.println("  protected Object reference" + index + ";");
            index++;
        }
        file.println("}");
        file.flush();
        file.close();
    }
    
}