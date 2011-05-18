package com.openwide.easysoa.esperpoc.esper;

import java.util.HashMap;
//import java.util.Iterator;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.bean.BeanEventBean;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class MessageListener implements UpdateListener {

	public void update(EventBean[] newData, EventBean[] oldData) {
		System.out.println("--- Event received: " + newData[0].getUnderlying());
		System.out.println("--- " + newData[0].getUnderlying().getClass().getName());
		//Message msg = (Message)(newData[0].getUnderlying());
		@SuppressWarnings("unchecked")
		HashMap<String,Object> hm = (HashMap<String,Object>)(newData[0].getUnderlying());
		/*Iterator<String> iter = hm.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			System.out.println("Key : " + key);
			System.out.println("Value : " + hm.get(key));
			System.out.println("Clazz value : " + hm.get(key).getClass().getName());
		}*/
		//Message msg =  (Message)(hm.get("s"));
		BeanEventBean beb = (BeanEventBean)(hm.get("s"));
		Message msg = (Message)(beb.getUnderlying());
		
		// Construction d'un service + send service event
		// TODO STATIC variable for Nuxeo address
		String serviceName = msg.getPathName();
		if(serviceName.startsWith("/")){
			serviceName = serviceName.substring(1);
		}
		serviceName = serviceName.replace('/', '_');
		Service service = new Service(serviceName, msg.getHost(), msg.getCompleteMessage());
		StringBuffer sb = new StringBuffer("http://localhost:8080/nuxeo/restAPI/wsdlupload/");
	    sb.append(service.getAppName()); //App Name
	    sb.append("/");
	    sb.append(service.getServiceName()); //Service name
	    sb.append("/");
	    //sb.append("http://localhost:9010/PureAirFlowers?wsdl");
	    sb.append(service.getUrl());
		System.out.println("Request URL = " + sb.toString());
		// Send request to register the service
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("Administrator", "Administrator")); 
		WebResource webResource = client.resource(sb.toString());
		ClientResponse response = webResource.accept("text/plain").get(ClientResponse.class);
	   	int status = response.getStatus();
	   	System.out.println("Registration request response status = " + status);
		String textEntity = response.getEntity(String.class);
		System.out.println("Registration request response = " + textEntity);
    }
	
}
