package com.openwide.easysoa.esperpoc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

import com.openwide.easysoa.monitoring.Message;
import com.openwide.easysoa.monitoring.Message.MessageType;
import com.openwide.easysoa.monitoring.MonitorService.MonitoringMode;
import com.openwide.easysoa.monitoring.MessageHandler;
import com.openwide.easysoa.monitoring.MonitorService;
import com.openwide.easysoa.monitoring.RestMessageHandler;
import com.openwide.easysoa.monitoring.SoapMessageHandler;
import com.openwide.easysoa.monitoring.WSDLMessageHandler;
import com.openwide.easysoa.monitoring.apidetector.UrlTree;
import com.openwide.easysoa.monitoring.apidetector.UrlTreeNode;

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
	 * Log system initialization
	 */
	//TODO add a way to specify dynamically the monitoring mode, default monitoring mode is stored in httpProxy.properties
	//TODO Add a way to finish the run in discovery mode to register all the applications / services / api stored in the urlTree
	//TODO Special command with url send to the proxy ?
	// In this case, we need a complate set of command to start, stop a run ....
	// Other solution : Use a Frascati sca property and update it with a hot update => needs an external GUI or Frascati explorer
	static {
		PropertyConfigurator.configure(HttpProxyImpl.class.getClassLoader().getResource("log4j.properties"));
		MonitorService.getMonitorService(MonitoringMode.valueOf(PropertyManager.getProperty("proxy.default.monitoring.mode").toUpperCase()));
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
	    	StringBuffer requestUrlBuf = new StringBuffer();
	    	requestUrlBuf.append(request.getRequestURL().toString());
	    	if(request.getQueryString() != null){
	    		requestUrlBuf.append("?");
	    		requestUrlBuf.append(request.getQueryString());
	    	}
	    	String requestUrlString = requestUrlBuf.toString();
	    	logger.debug("--- Complete request : " + requestUrlString);

	    	// send to actual server :
	    	ClientResource resource = new ClientResource(requestUrlString);
	    	// Send an authenticated request using the Basic authentication scheme.
	    	if(request.getRemoteUser() != null){
	    		String authHead=request.getHeader("Authorization");
	    		resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, request.getRemoteUser(), decodePassword(authHead));
	    	}
	    	// TODO get the response for monitoring purpose before sending it back
	    	InputStream in = resource.get().getStream();
    	    if(in != null){
    	    	int c;
    	    	while((c = in.read()) != -1){
    	    		respOut.write(c);
    	    	}
    	    }

	    	// TODO .monitoring.MessageHandler : isOKFor(Message ? Request ?) handle(Message)
    	    // TODO in all methods (doGet, doPost), for (mh in List<>Message Handler ) { boolean isOKFor(); handle() return stopHandling; }
    	    // TODO GetWSDLMessageHandler, SoapMessageHandler, RestMessageHandler
    	    
    	    // TODO MonitorService : mode, soaModel, listen() -> "for (mh..." called here
    	    // TODO refactor the test with MonitorService.listen()
    	    // TODO move doGet() code in forward() 
    	    
    	    // TODO at the start (end ?!) of MonitorService.listen(), RunRecorder.record(Message)
    	    // TODO RunRecorder (NB. not a RunRepository, yet) : record(Message)
    	    // TODO Run : startDate, stopDate...
    	    // TODO RunManager : runs, start() (if not autostart), stop(), listRuns() / getLastRun()..., rerun(Run) -> for (run... MonitorService.listen(...
    	    Message message = new Message(request);
    	    MonitorService.getMonitorService().listen(message);

	    	/*if(isWSDL(requestUrlString)){
	    		// Registering WSDL web service
	    		// Create a new message received object and send it to Esper
	    		logger.debug("--- ****** WSDL found, create Message !");
	    		Message msg = new Message(request.getRequestURL().toString(), request.getProtocol(), request.getServerName(), request.getServerPort(), request.getRequestURI(), request.getQueryString(), MessageType.WSDL);				
				EsperEngineSingleton.getEsperRuntime().sendEvent(msg);
	    	} else {
	    		//TODO
	    		// Not possible to make 2 different strategies : one for static url with parameters and one for dynamic url because it is possible to have dynamic url with parameters ...
    			Message msg = new Message(request.getRequestURL().toString(), request.getProtocol(), request.getServerName(), request.getServerPort(), request.getRequestURI(), request.getQueryString(), MessageType.REST);
    			// Add the url in the url tree structure
    			logger.debug("--- REST Service found, registering in URL tree !");
    			urlTree.addUrlNode(msg);
    			// Filtre pour ne pas prendre en compte les resources statiques : pas la meilleure solution
    			// Seule solution pour detection correcte : Analyse de la requete et de la reponse associée.
    			// Si reponse contient du JSON => webservice, si html simple ou image => pas webservice ...
    			// Ce qui implique de modifier le pojo message pour faire 2 parties distinctes : request / response
	    	}*/
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
		//TODO
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
		/*if(bodyContent.toString().toLowerCase().contains("schemas.xmlsoap.org") && bodyContent.toString().toLowerCase().startsWith("<?xml")){
			logger.debug("SOAP Message found, create Esper message");
			// Check if a WSDL exists
			if(checkWsdl(request.getRequestURL().toString())){
				logger.debug("WSDL found");
				logger.debug("Registering in nuxeo");
				Message msg = new Message(request.getProtocol(), request.getServerName(), request.getServerPort(), request.getRequestURI(), "wsdl", bodyContent.toString(), request.getMethod(), MessageType.WSDL);
				EsperEngineSingleton.getEsperRuntime().sendEvent(msg);				
			}
		}*/
		
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
	    		String authHead=request.getHeader("Authorization");
	    		resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, request.getRemoteUser(), decodePassword(authHead));
	    	}
	    	InputStream in = resource.post(representation).getStream();
    	    if(in != null){
    	    	int c;
    	    	while((c = in.read()) != -1){
    	    		respOut.write(c);
    	    	}
    	    }
    	    Message message = new Message(request);
    	    message.setBody(bodyContent.toString());
    	    MonitorService.getMonitorService().listen(message);
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
	 * 
	 */
	//TODO : Move this method or change the way to obtain urlTree !!
	@SuppressWarnings("unused")
	private void printUrlTree(){
		logger.debug("[printUrlTree()] Printing tree node index ***");
		logger.debug("[printUrlTree()] Total url count : " + MonitorService.getMonitorService().getUrlTree().getTotalUrlCount());
		String key;
		HashMap<String, UrlTreeNode> index = MonitorService.getMonitorService().getUrlTree().getNodeIndex();
		Iterator<String> iter2 = index.keySet().iterator();
		UrlTreeNode parentNode;
		float ratio;
		while(iter2.hasNext()){
			key = iter2.next();
			parentNode = (UrlTreeNode)(index.get(key).getParent());
			ratio = (float)index.get(key).getPartialUrlcallCount() / MonitorService.getMonitorService().getUrlTree().getTotalUrlCount();
			logger.debug("[printUrlTree()] " + key + " -- " + index.get(key).toString() + ", parent node => " + parentNode.getNodeName() + ", Depth => " + index.get(key).getDepth() + ", node childs => " + index.get(key).getChildCount() + ", ratio => " + ratio);
		}
	}

	/**
	 * Send back the request to the original recipient and get the response
	 * @throws IOException 
	 */
	//TODO Use this method in doGet and doPost methods
	public void forward(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter respOut = response.getWriter();
		//try{
		StringBuffer requestUrlBuffer = new StringBuffer();
		requestUrlBuffer.append(request.getRequestURL().toString());
	    if(request.getQueryString() != null){
	    	requestUrlBuffer.append("?");
	    	requestUrlBuffer.append(request.getQueryString());
	    }
	    String requestUrlString = requestUrlBuffer.toString();

	    BufferedReader requestBufferedReader = request.getReader();
	    StringBuffer bodyContent = new StringBuffer();
		if(requestBufferedReader != null){
		   	String line;
		   	while((line = requestBufferedReader.readLine()) != null){
		   		bodyContent.append(line);
		   	}
		}
		String requestBodyString = bodyContent.toString();
			
	    ClientResource resource = new ClientResource(requestUrlString);
	    Representation representation = new org.restlet.representation.StringRepresentation(requestBodyString);
	    InputStream in = resource.post(representation).getStream();
    	if(in != null){
    	   	int c;
    	   	while((c = in.read()) != -1){
    	   		respOut.write(c);
    	   	}
    	}
	    /*}
		catch(Throwable ex){
	    	ex.printStackTrace();
	    	//respOut.println("<html><body>httpProxy : An errror occurs.<br/>");
	    	//respOut.println(ex.getMessage() + "</body></html>");
	    }
	    finally {
	    	logger.debug("Closing response flow");
	    	//respOut.println("Finally block ..... Something goes wrong !!");
	    	respOut.close();
	    }*/
    	respOut.close();
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
