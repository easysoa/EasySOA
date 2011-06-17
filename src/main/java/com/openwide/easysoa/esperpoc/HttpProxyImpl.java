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
	// In this case, we need a complate set of command to start, stop a run ....
	static {
		ProxyConfigurator.configure();
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
	    	forward(request, response);
    	    Message message = new Message(request);
    	    MonitorService.getMonitorService().listen(message);
    	    
    	    // TODO .monitoring.MessageHandler : isOKFor(Message ? Request ?) handle(Message)
    	    // TODO in all methods (doGet, doPost), for (mh in List<>Message Handler ) { boolean isOKFor(); handle() return stopHandling; }
    	    // TODO GetWSDLMessageHandler, SoapMessageHandler, RestMessageHandler

    	    // TODO MonitorService : mode, soaModel, listen() -> "for (mh..." called here
    	    // TODO refactor the test with MonitorService.listen()
    	    // TODO move doGet() code in forward() to finish 

    	    // TODO at the start (end ?!) of MonitorService.listen(), RunRecorder.record(Message)
    	    // TODO RunRecorder (NB. not a RunRepository, yet) : record(Message)
    	    // TODO Run : startDate, stopDate...
    	    // TODO RunManager : runs, start() (if not autostart), stop(), listRuns() / getLastRun()..., rerun(Run) -> for (run... MonitorService.listen(...
	    }
	    catch(Throwable ex){
	    	ex.printStackTrace();
	    	logger.error("An error occurs in doGet method.", ex);
	    	respOut.println("<html><body>httpProxy : An errror occurs :<br/>");
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
		
		PrintWriter respOut = response.getWriter();
		// re-route request to the provider and send the response to the consumer
	    try{
	    	forward(request, response);
    	    Message message = new Message(request);
    	    message.setBody(bodyContent.toString());
    	    MonitorService.getMonitorService().listen(message);
	    }
	    catch(Throwable ex){
	    	ex.printStackTrace();
	    	logger.error("A" , ex);
	    	respOut.println("<html><body>httpProxy : An errror occurs :<br/>");
	    	respOut.println(ex.getMessage() + "</body></html>");
	    }
	    finally {
	    	logger.debug("Closing response flow");
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
	private void forward(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter respOut = response.getWriter();
		// Header
		Enumeration<String> enum1 = request.getHeaderNames(); 
		logger.debug("Requests Headers");
		//Form form = new Form();
		while(enum1.hasMoreElements()){
			String headerName = enum1.nextElement();
			String headerValue = request.getHeader(headerName);
			logger.debug("Header name = " + headerName + ", Header value = " + headerValue);
			//form.add(headerName, headerValue);
		}
		// URL
		StringBuffer requestUrlBuffer = new StringBuffer();
		requestUrlBuffer.append(request.getRequestURL().toString());
	    if(request.getQueryString() != null){
	    	requestUrlBuffer.append("?");
	    	requestUrlBuffer.append(request.getQueryString());
	    }
	    String requestUrlString = requestUrlBuffer.toString();
	    // Body
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
        //Representation rep = form.getWebRepresentation();
        //Request restletRequest = new Request();
        //restletRequest.getAttributes().put("org.restlet.http.headers", rep);
        //resource.setRequest(restletRequest);
	    InputStream in = null;
	    if("GET".equalsIgnoreCase(request.getMethod())){
	    	in = resource.get().getStream();
	    } else {
	    	Representation representation = new org.restlet.representation.StringRepresentation(requestBodyString);
	    	in = resource.post(representation).getStream();
	    }
	    //TODO Take too much time when the received response is big ... Try to find an other method to optimize
	    byte[] byteArray = new byte[8192];
	    if(in != null){
	    	logger.debug("Sending response to original recipient ...");
    	   	while((in.read(byteArray)) != -1){
    	   		respOut.write(new String(byteArray));
    	   	}
    	}
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
