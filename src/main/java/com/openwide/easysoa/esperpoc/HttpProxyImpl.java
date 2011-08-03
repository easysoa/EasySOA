package com.openwide.easysoa.esperpoc;

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

import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import com.openwide.easysoa.esperpoc.run.RunManager;
import com.openwide.easysoa.monitoring.Message;
//import com.openwide.easysoa.monitoring.DiscoveryMonitoringService;
//import com.openwide.easysoa.monitoring.DiscoveryMonitoringService.MonitoringMode;
import com.openwide.easysoa.monitoring.MonitoringService;
import com.openwide.easysoa.monitoring.apidetector.UrlTreeNode;

/**
 * HTTP Proxy 
 * 
 * Work on the top of FraSCAti
 * 
 * Does : 
 * - Detect GET WSDL request messages, then register the WSDL in Nuxeo Easysoa model
 * - Detect GET REST request messages with parameters but no dynamic path parts, then register the REST service in Nuxeo Easysoa model
 * - Detect GET REST request messages with dynamic URL (in development)
 * - Detect POST SOAP request messages, check if a WSDL is associated with a message and register it in Nuxeo Easysoa model 
 * @author jguillemotte
 */
@SuppressWarnings("serial")
@Scope("composite")
public class HttpProxyImpl extends HttpServlet {
	
	// Reference on monitoring service
	@Reference
	MonitoringService monitoringService;
	
	//TODO : remove this constant and find a way to get the proxy port configured in frascati composite file
	private final static int PROXY_PORT = 8082; 
	private final static int HTTP_CONNEXION_TIMEOUT_MS = 10000; 
	private final static int HTTP_SOCKET_TIMEOUT_MS = 10000;
	
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
		/*if(DiscoveryMonitoringService.getMode() == null){
			DiscoveryMonitoringService.getMonitorService(MonitoringMode.valueOf(PropertyManager.getProperty("proxy.default.monitoring.mode").toUpperCase()));
		}*/
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	public final void doGet(HttpServletRequest request,	HttpServletResponse response) throws ServletException, IOException {
		doHttpMethod(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
	 */	
	@Override
	public final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHttpMethod(request, response);
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHttpMethod(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHttpMethod(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHttpMethod(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doOptions(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHttpMethod(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doTrace(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doTrace(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHttpMethod(request, response);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public final void doHttpMethod(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
	    	// Remove that if block
	    	/*if(monitoringService.getRunManager() == null){
	    		monitoringService.setRunManager(runManager);
	    	}*/
	    	Message message = forward(request, response);
	    	monitoringService.listen(message);
	    	//DiscoveryMonitoringService.getMonitorService().listen(message);
	    }
	    catch (HttpResponseException rex) {
			// error in the actual server : return it back to the client

			// attempting to reset response
			response.reset();
			response.setStatus(rex.getStatusCode());

			// filling response :
			// TODO get expected content type from request
			// TODO also handle bad result content ex. "Internal Server Error" from demo MS WS
			response.setContentType("text/xml");
			respOut.write(rex.getMessage());
			respOut.close();

			// debugging
			logger.debug("httpProxy : error in proxied server : "
					+ rex.getStatusCode() + " - " + rex.getMessage());
		}
	    catch(Throwable ex){
	    	// error in the internals of the httpProxy : building & returning it
	    	ex.printStackTrace();
	    	logger.error("An error occurs in doPost method.", ex);

	    	// attempting to reset response
	    	response.reset();
	    	response.setStatus(500);
            
	    	// filling response
	    	HashMap<String, Object> headers = new HashMap<String, Object>();
	    	if(headers.containsKey("SOAPAction")){
	    		response.setContentType("text/xml");
	    		// Returns SOAP Fault
	    		logger.debug("Returns a SOAP Fault");
	    		respOut.print(this.buildSoapFault(ex.getMessage()));
	    	} else {
	    		// Returns HTML response
				response.setContentType("text/html");
				logger.debug("returns an html response");
	    		respOut.println("<html><body>httpProxy : unknown error in proxy, doPost :<br/>");
	    		respOut.println(ex.getMessage());
	    		ex.printStackTrace(respOut);
	    		respOut.println("</body></html>");
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
	//@SuppressWarnings("unused")
	//@Deprecated
	/*private void printUrlTree(){
		logger.debug("[printUrlTree()] Printing tree node index ***");
		logger.debug("[printUrlTree()] Total url count : " + DiscoveryMonitoringService.getMonitorService().getUrlTree().getTotalUrlCount());
		String key;
		HashMap<String, UrlTreeNode> index = DiscoveryMonitoringService.getMonitorService().getUrlTree().getNodeIndex();
		Iterator<String> iter2 = index.keySet().iterator();
		UrlTreeNode parentNode;
		float ratio;
		while(iter2.hasNext()){
			key = iter2.next();
			parentNode = (UrlTreeNode)(index.get(key).getParent());
			ratio = (float)index.get(key).getPartialUrlcallCount() / DiscoveryMonitoringService.getMonitorService().getUrlTree().getTotalUrlCount();
			logger.debug("[printUrlTree()] " + key + " -- " + index.get(key).toString() + ", parent node => " + parentNode.getNodeName() + ", Depth => " + index.get(key).getDepth() + ", node childs => " + index.get(key).getChildCount() + ", ratio => " + ratio);
		}
	}*/

	/**
	 * Send back the request to the original recipient and get the response
	 * @throws Exception 
	 */
	private Message forward(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter respOut = response.getWriter();
		DefaultHttpClient httpClient = new DefaultHttpClient();
        
		// set the retry handler
		httpClient.setHttpRequestRetryHandler(new HttpRetryHandler());
		// set the connection timeout
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_CONNEXION_TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(httpParams, HTTP_SOCKET_TIMEOUT_MS);
		
		// TODO Enable infinite loop detection
		//infiniteLoopDetection(request);
		
		// URL
		StringBuffer requestUrlBuffer = new StringBuffer();
		requestUrlBuffer.append(request.getRequestURL().toString());
	    if(request.getQueryString() != null){
	    	requestUrlBuffer.append("?");
	    	requestUrlBuffer.append(request.getQueryString());
	    }
	    String requestUrlString = requestUrlBuffer.toString();
	    
	    // Body
		Message message = new Message(request);
    	HttpEntity httpEntity = new StringEntity(message.getBody());
		logger.debug("Request URL String : " +  requestUrlString);
		logger.debug("Request Body String : " + message.getBody());
		
		ResponseHandler<String> responseHandler = new BasicResponseHandler(); 
		HttpUriRequest httpUriRequest;
		// TODO later use a pattern to create them (builder found in a map method -> builder...)
		if("GET".equalsIgnoreCase(request.getMethod())){
	    	httpUriRequest = new HttpGet(requestUrlString);
	    } else if("PUT".equalsIgnoreCase(request.getMethod())){
	    	HttpPut httpPut = new HttpPut(requestUrlString);
	    	httpPut.setEntity(httpEntity);
	    	httpUriRequest = httpPut;
	    } else if("DELETE".equalsIgnoreCase(request.getMethod())){
	    	httpUriRequest = new HttpDelete(requestUrlString);
	    } else if("OPTIONS".equalsIgnoreCase(request.getMethod())){
	    	httpUriRequest = new HttpOptions(requestUrlString);
	    } else if("HEAD".equalsIgnoreCase(request.getMethod())){
	    	httpUriRequest = new HttpOptions(requestUrlString);
	    } else if("TRACE".equalsIgnoreCase(request.getMethod())){
	    	httpUriRequest = new HttpOptions(requestUrlString);
	    } else { // POST
	    	HttpPost httpPost = new HttpPost(requestUrlString);
	    	httpPost.setEntity(httpEntity);
	    	httpUriRequest = httpPost;
	    }
    	setHeaders(request, httpUriRequest);
	    
    	// TODO proxy for outgoing messages : allow to conf it, with a ProxyStrategy interface & a few impl (proxy all, by host, LATER or possibly driven by easysoa ui...) injected in frascati
	    /*if (requestUrlString.contains("microsoft")) {
	        HttpHost myProxy = new HttpHost("localhost", 8084, "http");
	        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, myProxy);
	    }*/
    	String resp = httpClient.execute(httpUriRequest, responseHandler);
	    
	    respOut.write(resp);
    	respOut.close();
    	return message;
	}

	/**
	 * Set headers in the httpMessage
	 * @param request The request where to get headers
	 * @param httpMessage The http message to set
	 */
	private void setHeaders(HttpServletRequest request, HttpMessage httpMessage){
		Enumeration<String> enum1 = request.getHeaderNames();
		logger.debug("Requests Headers :");
		while(enum1.hasMoreElements()){
			String headerName = enum1.nextElement();
			String headerValue = request.getHeader(headerName);
			// to avoid an exception when the Content-length header is set twice
			//if("Host".equals(headerName) && headerValue.contains("microsoft")){////
			//	httpMessage.setHeader("Host", "localhost:8084");////
			//} else/////
			if(!"Content-Length".equals(headerName) && !"Transfer-Encoding".equals(headerName)){
				httpMessage.setHeader(headerName, headerValue);
			}
			//logger.debug("Header name = " + headerName + ", Header value = " + headerValue);
			logger.debug(headerName + ": " + headerValue);
		}
	}
	
	/**
	 * 
	 * @param authhead
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@Deprecated
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

	/**
	 * Detect if the request call directly the proxy, if true, an exception is throw
	 */
	private void infiniteLoopDetection(HttpServletRequest request) throws Exception{
		if(PROXY_PORT == request.getServerPort()){
			throw new Exception("Request on proxy itself detected on port " + PROXY_PORT + " !");
		}
		/*Enumeration<String> enum1 = this.getServletContext().getInitParameterNames();
		while(enum1.hasMoreElements()){
			logger.debug("InitParametersName : " + enum1.nextElement());
		}*/
		//throw new Exception("TEST");
		// Get the localhost and port to detect the loop
	}
	
}

