package com.openwide.easysoa.esperpoc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.restlet.Client;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import com.openwide.easysoa.monitoring.Message;
import com.openwide.easysoa.monitoring.MonitorService.MonitoringMode;
import com.openwide.easysoa.monitoring.MonitorService;
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
 * - Detect POST SOAP request messages, check if a WSDL is associated with a message and register it in Nuxeo Easysoa model 
 * @author jguillemotte
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
	static {
		ProxyConfigurator.configure();
		// If MonitorService is not set, set with the default monitoring mode
		if(MonitorService.getMode() == null){
			MonitorService.getMonitorService(MonitoringMode.valueOf(PropertyManager.getProperty("proxy.default.monitoring.mode").toUpperCase()));
		}
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
	    	Message message = forward(request, response);
    	    MonitorService.getMonitorService().listen(message);
	    }
	    catch(Throwable ex){
	    	ex.printStackTrace();
	    	logger.error("An error occurs in doGet method.", ex);
	    	respOut.println("<html><body>httpProxy, doGet : An errror occurs :<br/>");
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
		logger.debug("------------------");
		logger.debug("Method: " + request.getMethod());
		logger.debug("RequestURI : " + request.getRequestURI());
		logger.debug("Query : " + request.getQueryString());
		logger.debug("server: " + request.getServerName());
		logger.debug("port: " + request.getServerPort());
		logger.debug("request URL: " + request.getRequestURL());
		PrintWriter respOut = response.getWriter();
		// re-route request to the provider and send the response to the consumer
	    try{
	    	Message message = forward(request, response);
    	    MonitorService.getMonitorService().listen(message);
	    }
	    catch(Throwable ex){
	    	ex.printStackTrace();
	    	logger.error("An error occurs in doPost method." , ex);
	    	HashMap<String, Object> headers = new HashMap<String, Object>();
	    	if(headers.containsKey("SOAPAction")){
	    		// Returns SOAP Fault
	    		respOut.print(this.buildSoapFault(ex.getMessage()));
	    	} else {
	    		// Returns HTML response
	    		respOut.println("<html><body>httpProxy, doPost : An errror occurs :<br/>");
	    		respOut.println(ex.getMessage() + "</body></html>");
	    	}
	    }
	    finally {
	    	logger.debug("--- Closing response flow");
	    	respOut.close();
	    }
	}

	/**
	 * 
	 */
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
	private Message forward(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter respOut = response.getWriter();
		
		// Header
		Enumeration<String> enum1 = request.getHeaderNames();
		HashMap<String, Object> headers = new HashMap<String, Object>();
		Form headersForm = new Form();
		logger.debug("Requests Headers");
		while(enum1.hasMoreElements()){
			String headerName = enum1.nextElement();
			String headerValue = request.getHeader(headerName);
			headersForm.add(headerName, headerValue);
			logger.debug("Header name = " + headerName + ", Header value = " + headerValue);
		}
		headers.put("org.restlet.http.headers", headersForm);
		
		// URL
		StringBuffer requestUrlBuffer = new StringBuffer();
		requestUrlBuffer.append(request.getRequestURL().toString());
	    if(request.getQueryString() != null){
	    	requestUrlBuffer.append("?");
	    	requestUrlBuffer.append(request.getQueryString());
	    }
	    String requestUrlString = requestUrlBuffer.toString();
	    
	    // Body
	    /*BufferedReader requestBufferedReader = request.getReader();
	    StringBuffer bodyContent = new StringBuffer();
		if(requestBufferedReader != null){
		   	String line;
		   	while((line = requestBufferedReader.readLine()) != null){
		   		bodyContent.append(line);
		   	}
		}
		String requestBodyString = bodyContent.toString();*/
		Message message = new Message(request);
		logger.debug("Request URL String : " +  requestUrlString);
		logger.debug("Request Body String : " + message.getBody());
	    ClientResource resource = new ClientResource(requestUrlString);
	    InputStream in = null;
		    if("GET".equalsIgnoreCase(request.getMethod())){
		    	in = resource.get().getStream();
		    } else {
		    	Representation representation = new org.restlet.representation.StringRepresentation(message.getBody());
		    	if(message.getBody().contains("soap:Envelope")){
		    		logger.debug("Setting mediatype to text/xml");		
		    		representation.setMediaType(MediaType.TEXT_XML);
		    		
		    	}
		    	resource.getRequest().setAttributes(headers);
		    	resource.setRetryOnError(true);
		    	resource.setRetryAttempts(3);
		    	in = resource.post(representation).getStream();
		    }
		    byte[] byteArray = new byte[8192];
		    String responseBuffer;
		    if(in != null){
		    	logger.debug("Sending response to original recipient ...");
	    	   	while((in.read(byteArray)) != -1){
	    	   		responseBuffer = new String(byteArray);
	    	   		logger.debug("ResponseBuffer : " + responseBuffer);
	    	   		respOut.write(responseBuffer);
	    	   	}
	    	}
    	respOut.close();
	    return message;
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
	
	/**
	 * 
	 * @param error
	 * @return
	 */
	private String buildSoapFault(String error){
		StringBuffer fault = new StringBuffer();
		fault.append("<?xml version=\"1.0\"?>");
		fault.append("<SOAP-ENV:Envelope SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\">");
		fault.append("<SOAP-ENV:Body><SOAP-ENV:Fault>");		
		fault.append("<faultcode>SOAP-ENV:Client</faultcode>"); 
		fault.append("<faultstring>");
		fault.append(error.replace("\"", "\\\""));
		fault.append("</faultstring>");
		fault.append("</SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>");
		return fault.toString();
	}

}

