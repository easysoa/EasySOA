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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.easysoa.records.persistence.filesystem.ExchangeRecordFileStore;
import org.eclipse.stp.sca.Component;
import org.eclipse.stp.sca.ComponentReference;
import org.eclipse.stp.sca.PropertyValue;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;
import org.ow2.frascati.assembly.factory.api.ProcessingContext;
import org.ow2.frascati.util.Stream;

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
        if(requestedResource.startsWith("/target")){
        	
        	// Get the list of record template
        	// TODO : remove the ExchangeRecordFileStore and use TemplateDefinitionService
        	ExchangeRecordFileStore excf = new ExchangeRecordFileStore();
        	List<String> templateFileList = excf.getTemplateList();
        	
        	// For the resource /target/ : for each record template, send back a html list of links on template.wsdl
        	if(requestedResource.endsWith("/")){
        		if(templateFileList.size()>0){
        			response.getWriter().println("<html><body><ul>");
	        		for(String templateFile : templateFileList){
	        			response.getWriter().println("<li><a href=\"http://localhost:8090/runManager/target/" + templateFile + "?wsdl\">" + templateFile + "</a> </li>");		
	        		}
	        		response.getWriter().println("</ul></body></html>");
        		}
        	}
        	else if(requestedResource.endsWith("?wsdl")){
        		// Get the FLD file and generate WSDL using XSLT transformation or by calling a velocity template 
        		/*try {
        			//String fileName = 
					//excf.getTemplateFieldSuggestions(requestedResource.substring(requestedResource.lastIndexOf("/"), requestedResource.indexOf("?")));
				} catch (Exception e) {
					e.printStackTrace();
				}*/
        		response.getWriter().println("Not yet implemented. Will return the required WSDL ...");
        	}
        	// Send a default response ...
        	// TODO : change this response
        	else {
            	response.getWriter().println("/target resources called : HACK code triggered !");
            	response.getWriter().println("Add code to get a list of HTML links on generated WSDL corresponding to the template records !");        		
        	}
        	// For the resource /target/templateX.wsdl : returns the corresponding WSDL file with default values generated from the FLD and template files associated to the record.
        	// ... Next : see issue #73 
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