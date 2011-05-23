package com.openwide.easysoa.esperpoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import com.openwide.easysoa.esperpoc.esper.Message;

@SuppressWarnings("serial")
public class HttpProxyImpl extends HttpServlet {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(HttpProxyImpl.class.getName());

	/**
	 * Log system init
	 */
	static {
		PropertyConfigurator.configure(HttpProxyImpl.class.getClassLoader().getResource("log4j.properties"));
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	public final void doGet(HttpServletRequest request,	HttpServletResponse response) throws ServletException, IOException {
		logger.debug("------------------");
		logger.debug("--- Method: " + request.getMethod());
		logger.debug("--- RequestURI : " + request.getRequestURI());
		logger.debug("--- Query : " + request.getQueryString());
		logger.debug("--- server: " + request.getServerName());
		logger.debug("--- port: " + request.getServerPort());
		logger.debug("--- request URL: " + request.getRequestURL());
		PrintWriter respOut = response.getWriter();
		// re-route request to the provider and send the response to the consumer
	    try{
	    	// RESTLET
			// Create the client resource 
	    	StringBuffer sb = new StringBuffer();
	    	sb.append(request.getRequestURL().toString());
	    	if(request.getQueryString() != null){
	    		sb.append("?");
	    		sb.append(request.getQueryString());
	    	}
	    	logger.debug("--- Complete request : " + sb.toString());
	    	if(sb.toString().toLowerCase().matches(PropertyManager.getProperty("proxy.wsdl.request.detect"))){
		    	// Registering WSDL web service
	    		// Create a new message received object and send it to Esper
	    		logger.debug("--- ****** WSDL found, create Message !");
	    		Message msg = new Message(request.getProtocol(), request.getServerName(), request.getServerPort(), request.getRequestURI(), request.getQueryString());				
				EsperEngineSingleton.getEsperRuntime().sendEvent(msg);
	    	} else {
	    		logger.debug("--- ****** Processing regex to find rest web service !");
	    		if(sb.toString().toLowerCase().matches(PropertyManager.getProperty("proxy.rest.request.detect.parameters"))){
		    		// Registering a REST web service with parameters
		    		// eg : www.google.fr/search?q=test&lang=fr
	    			logger.debug("--- REST Service with parameters found, create Message !");
	    			Message msg = new Message(request.getProtocol(), request.getServerName(), request.getServerPort(), request.getRequestURI(), request.getQueryString());
	    			EsperEngineSingleton.getEsperRuntime().sendEvent(msg);
	    		}
	    		else {
	    			// Registering a 'dynamic' web service (with parameters directly in the url)
		    		// dynamic url
		    		// www.freebooks.org/library/getBook/7548669-874-98854
		    		// eg Pattern => www.freebooks.org/library/getBook/{id or isbn}
	    		}
	    	}
	    	ClientResource resource = new ClientResource(sb.toString());
	    	InputStream in = resource.get().getStream();
    	    if(in != null){
    	    	int c;
    	    	while((c = in.read()) != -1){
    	    		respOut.write(c);
    	    	}
    	    }
	    }
	    catch(Throwable ex){
	    	ex.printStackTrace();
	    	respOut.println("<html><body>httpProxy : An errror occurs.<br/>");
	    	respOut.println(ex.getMessage() + "</body></html>");
	    }
	    finally {
	    	logger.debug("--- Closing response flow");
	    	respOut.close();
	    }
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
	 */	
	@Override
	public final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//@TODO
		// OK : Detecter si body contient du xml -> pour setter dynamiquement le type RESTLET --> automatique
		// Detecter si body contient du SOAP -> recuperation wsdl, creation d'un message esper + enregistrement du service
		// Généraliser pour écouter également les requetes post RESTFull
		logger.debug("------------------");
		logger.debug("Method: " + request.getMethod());
		logger.debug("RequestURI : " + request.getRequestURI());
		logger.debug("Query : " + request.getQueryString());
		logger.debug("server: " + request.getServerName());
		logger.debug("port: " + request.getServerPort());
		logger.debug("request URL: " + request.getRequestURL());
		// List header info
		Enumeration<String> enum1 = request.getHeaderNames(); 
		while(enum1.hasMoreElements()){
			String headerName = enum1.nextElement();
			String headerValue = request.getHeader(headerName);
			logger.debug("Header name = " + headerName + ", Header value = " + headerValue);
		}
		// List attributes
		/*Enumeration<String> enum2 = request.getAttributeNames();
		while(enum2.hasMoreElements()){
			String attributeName = enum2.nextElement();
			Object attributeValue = request.getAttribute(attributeName);
			logger.debug("Header name = " + attributeName + ", Header value = " + attributeValue);
		}*/
		// List parameters
		/*Enumeration<String> enum3 = request.getParameterNames();
		while(enum3.hasMoreElements()){
			String parameterName = enum3.nextElement();
			String parameterValue = request.getParameter(parameterName);
			logger.debug("Header name = " + parameterName + ", Header value = " + parameterValue);
		}*/

		BufferedReader br = request.getReader();
    	StringBuffer bodyContent = new StringBuffer();
		if(br != null){
	    	logger.debug("Request body : ");
	    	String line;
	    	while((line = br.readLine()) != null){
	    		logger.debug(line);
	    		bodyContent.append(line);
	    	}
	    } else {
	    	logger.debug("Request body is empty ! ");
	    }
		
		if(bodyContent.toString().toLowerCase().contains("schemas.xmlsoap.org") && bodyContent.toString().toLowerCase().startsWith("<?xml")){
			logger.debug("SOAP Message found, create Esper message");
			// tester si existence d'un WSDL
			if(checkWsdl(request.getRequestURL().toString())){
				logger.debug("WSDL found");
				logger.debug("Registering in nuxeo");
				Message msg = new Message(request.getProtocol(), request.getServerName(), request.getServerPort(), request.getRequestURI(), "wsdl", bodyContent.toString());
				EsperEngineSingleton.getEsperRuntime().sendEvent(msg);				
			}
		}
		
		PrintWriter respOut = response.getWriter();
		// re-route request to the provider and send the response to the consumer
	    try{
			// Create the client resource  
	    	StringBuffer sb = new StringBuffer();
	    	sb.append(request.getRequestURL().toString());
	    	if(request.getQueryString() != null){
	    		sb.append("?");
	    		sb.append(request.getQueryString());
	    	}
	    	logger.debug("*** Complete request : " + sb.toString());
	    	//Representation representation = new org.restlet.representation.StringRepresentation(bodyContent.toString(),MediaType.APPLICATION_XML);
	    	Representation representation = new org.restlet.representation.StringRepresentation(bodyContent.toString());
	    	ClientResource resource = new ClientResource(sb.toString());
	    	InputStream in = resource.post(representation).getStream();
    	    if(in != null){
    	    	int c;
    	    	while((c = in.read()) != -1){
    	    		respOut.write(c);
    	    	}
    	    }
	    }
	    catch(Throwable ex){
	    	ex.printStackTrace();
	    	//respOut.println("<html><body>httpProxy : An errror occurs.<br/>");
	    	//respOut.println(ex.getMessage() + "</body></html>");
	    }
	    finally {
	    	logger.debug("Closing response flow");
	    	//respOut.println("Finally block ..... Something goes wrong !!");
	    	respOut.close();
	    }
	}
	
	/**
	 * Check if a WSDL service exists
	 * @param url The url to check
	 * @return true if the WSDL service send a response, false otherwise.
	 */
	private boolean checkWsdl(String url){
		boolean result = false;
		try{
			ClientResource resource = new ClientResource(url + "/?wsdl");
			resource.get();
			result = true;
		}
		catch(Exception ex){
			logger.debug("Unable to get a correct response from " + url + "/?wsdl");
		}
		return result;
	}
	
	
	
	
}

// Utilisation de Jersey client avec Frascati ...
/*Client client = Client.create();
//client.addFilter(new HTTPBasicAuthFilter("Administrator", "Administrator")); 
WebResource webResource = client.resource(request.getRequestURL().toString());
ClientResponse resp = webResource.get(ClientResponse.class);
int status = resp.getStatus();
	System.out.println("Registration request response status = " + status);
	//String textEntity = resp.getEntity(String.class);
//System.out.println("Registration request response = " + textEntity);		
System.out.println("resp.getEntityInputStream():" + resp.getEntityInputStream());
//respOut.println(textEntity);*/
