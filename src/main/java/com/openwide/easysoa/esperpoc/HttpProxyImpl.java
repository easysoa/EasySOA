package com.openwide.easysoa.esperpoc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.restlet.data.ChallengeScheme;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import com.openwide.easysoa.esperpoc.esper.Message;
import com.openwide.easysoa.esperpoc.UrlTree;
import com.openwide.easysoa.esperpoc.UrlTreeNode;

/**
 * HTTP Proxy 
 * 
 * Work on the top of Frascati
 * 
 * Does : 
 * - Detect GET WSDL request messages, then register the WSDL in Nuxeo Easysoa model
 * - Detect GET REST request messages with parameters but no dynamic path parts, then register the REST service in Nuxeo Easysoa model
 * - Detect GET REST request messages with dynamic URL (in development)
 * 
 * - Detect POST SOAP request messages, check if a WSDL is associated with a message and register it in Nuxeo Easysoa model 
 *
 * @author jguillemotte
 *
 */
@SuppressWarnings("serial")
public class HttpProxyImpl extends HttpServlet {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(HttpProxyImpl.class.getName());

	/**
	 * 
	 */
	private static UrlTree urlTree;
	
	/**
	 * Log system init
	 */
	static {
		PropertyConfigurator.configure(HttpProxyImpl.class.getClassLoader().getResource("log4j.properties"));
		urlTree = new UrlTree(new UrlTreeNode("root"));
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
	    		Message msg = new Message(request.getProtocol(), request.getServerName(), request.getServerPort(), request.getRequestURI(), request.getQueryString(), "WSDL");				
				EsperEngineSingleton.getEsperRuntime().sendEvent(msg);
	    	} else {
	    		//logger.debug("--- ****** Processing regex to find rest web service !");
	    		//TODO
	    		// Not possible to make 2 different strategies : one for static url with parameters and one for dynamic url because it is possible to have dynamic url with parameters ...
	    		//if(sb.toString().toLowerCase().matches(PropertyManager.getProperty("proxy.rest.request.detect.parameters"))){
		    		// Registering a REST web service with parameters
		    		// eg : www.google.fr/search?q=test&lang=fr
	    			logger.debug("--- REST Service with parameters found, create Message !");
	    			Message msg = new Message(request.getProtocol(), request.getServerName(), request.getServerPort(), request.getRequestURI(), request.getQueryString(), "REST");
	    			//EsperEngineSingleton.getEsperRuntime().sendEvent(msg);
	    		//}
	    		//else {
		    		// Registering a REST web service with parameters
		    		// eg : www.imedia.com/shop/getBook/{bookId}    			
	    			// Add the url in the url tree structure
	    			logger.debug("--- REST Service with dynamic path found, registering in URL tree !");
	    			urlTree.addUrlNode(request.getRequestURL().toString().substring(6), msg);
	    			
	    			// TODO : Add authentification
	    			// Make links between url tree and esper to analyse tree data and to produce api and service to register in nuxeo
	    			
	    			// Mock avec un hashset contenant des exemples d'URL
	    			// treeset avec un compteur pour chacun des noeuds/feuilles => incrementation en temps réel

	    			// Registering a 'dynamic' web service (with parameters directly in the url)
		    		// dynamic url
		    		// www.freebooks.org/library/getBook/7548669-874-98854
		    		// eg Pattern => www.freebooks.org/library/getBook/{id or isbn}

	    			// Filtre pour ne pas prendre en compte les resources statiques
	    			// Verifier existance d'un WADL

	    			// Seule solution pour detection correcte : Analyse de la requete et de la reponse associée.
	    			// Si reponse contient du JSON => webservice, si html simple => pas webservice
	    			// Ce qui implique de modifier le pojo message pour faire 2 parties disctinctes : request / response
	    			//
	    		//}
	    	}
	    	ClientResource resource = new ClientResource(sb.toString());
	    	// Send an authenticated request using the Basic authentication scheme.
	    	if(request.getRemoteUser() != null){
	    		String authhead=request.getHeader("Authorization");
	    		resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, request.getRemoteUser(), decodePassword(authhead));
	    	}
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
	    //printUrlTree();
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
	 */	
	@Override
	public final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//@TODO
		// Add code to listen REST post requests
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
		// Check if the request body is a soap message
		if(bodyContent.toString().toLowerCase().contains("schemas.xmlsoap.org") && bodyContent.toString().toLowerCase().startsWith("<?xml")){
			logger.debug("SOAP Message found, create Esper message");
			// Check if a WSDL exists
			if(checkWsdl(request.getRequestURL().toString())){
				logger.debug("WSDL found");
				logger.debug("Registering in nuxeo");
				Message msg = new Message(request.getProtocol(), request.getServerName(), request.getServerPort(), request.getRequestURI(), "wsdl", bodyContent.toString(), request.getMethod(), "WSDL");
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
	    	// Send an authenticated request using the Basic authentication scheme.
	    	if(request.getRemoteUser() != null){
	    		String authhead=request.getHeader("Authorization");
	    		resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, request.getRemoteUser(), decodePassword(authhead));
	    	}	    	
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
	
	/**
	 * 
	 */
	private void printUrlTree(){
		logger.debug("[printUrlTree()] Printing tree node index ***");
		logger.debug("[printUrlTree()] Total url count : " + urlTree.getTotalUrlCount());
		String key;
		HashMap<String, UrlTreeNode> index = urlTree.getNodeIndex();
		Iterator<String> iter2 = index.keySet().iterator();
		UrlTreeNode parentNode;
		float ratio;
		while(iter2.hasNext()){
			key = iter2.next();
			parentNode = (UrlTreeNode)(index.get(key).getParent());
			ratio = (float)index.get(key).getPartialUrlcallCount() / urlTree.getTotalUrlCount();
			logger.debug("[printUrlTree()] " + key + " -- " + index.get(key).toString() + ", parent node => " + parentNode.getNodeName() + ", Depth => " + index.get(key).getDepth() + ", node childs => " + index.get(key).getChildCount() + ", ratio => " + ratio);
		}
	}
	
	/**
	 * 
	 * @param authhead
	 * @return
	 * @throws Exception
	 */
	private String decodePassword(String authhead) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(authhead.getBytes());
        InputStream b64is = MimeUtility.decode(bais, "base64");
        byte[] tmp = new byte[authhead.getBytes().length];
        int n = b64is.read(tmp);
        byte[] res = new byte[n];
        System.arraycopy(tmp, 0, res, 0, n);
        return new String(res);
    } 

}
