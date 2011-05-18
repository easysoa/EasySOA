package com.openwide.easysoa.esperpoc;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.restlet.resource.ClientResource;
import com.openwide.easysoa.esperpoc.esper.Message;

@SuppressWarnings("serial")
public class HttpProxyImpl extends HttpServlet {

	/**
	 * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	public final void doGet(HttpServletRequest request,	HttpServletResponse response) throws ServletException, IOException {
		System.out.println("------------------");
		System.out.println("Method: " + request.getMethod());
		System.out.println("RequestURI : " + request.getRequestURI());
		System.out.println("Query : " + request.getQueryString());
		System.out.println("server: " + request.getServerName());
		System.out.println("port: " + request.getServerPort());
		System.out.println("request URL: " + request.getRequestURL());
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
	    	System.out.println("*** Complete request : " + sb.toString());
	    	// registering WSDL web service
	    	if(sb.toString().toLowerCase().endsWith("?wsdl")){
	    		// Create a new message received object and sand it to Esper
	    		System.out.println("****** WSDL found, create Message !");
	    		Message msg = new Message(request.getProtocol(), request.getServerName(), request.getServerPort(), request.getRequestURI(), request.getQueryString());				
				EsperEngineSingleton.getEsperRuntime().sendEvent(msg);
	    	} /*else {*/
	    		/*System.out.println("****** Processing regex to find rest web service !");
	    		String regex = "\\w*?\\w*=\\w*";
	    		if(sb.toString().toLowerCase().matches(regex)){
	    			System.out.println("Service with parameters found !");
	    		}*/
	    		// Other checks to verify if the request is a call to a web service
	    		//
	    		// url with parameters
	    		// www.google.fr/search?q=test&lang=fr
	    		// 	    		//
	    		// dynamic url
	    		// www.freebooks.org/library/getBook/7548669-874-98854
	    		//
	    		// Pattern => www.freebooks.org/library/getBook/{id or isbn}
	    	/*}*/
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
	    	System.out.println("Closing response flow");
	    	//respOut.println("Finally block ..... Something goes wrong !!");
	    	respOut.close();
	    }
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
	 */	
	/*@Override
	public final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("------------------");
		System.out.println("Method: " + request.getMethod());
		System.out.println("RequestURI : " + request.getRequestURI());
		System.out.println("Query : " + request.getQueryString());
		System.out.println("server: " + request.getServerName());
		System.out.println("port: " + request.getServerPort());
		System.out.println("request URL: " + request.getRequestURL());
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
	    	System.out.println("*** Complete request : " + sb.toString());
	    	ClientResource resource = new ClientResource(sb.toString());
	    	InputStream in = resource.get().getStream();
    	    int c;
		    while((c = in.read()) != -1){
		    	respOut.write(c);
		    }
	    }
	    catch(Throwable ex){
	    	ex.printStackTrace();
	    	//respOut.println("<html><body>httpProxy : An errror occurs.<br/>");
	    	//respOut.println(ex.getMessage() + "</body></html>");
	    }
	    finally {
	    	System.out.println("Closing response flow");
	    	//respOut.println("Finally block ..... Something goes wrong !!");
	    	respOut.close();
	    }
	}*/
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
