/**
 * 
 */
package org.openwide.easysoa.test.messaging;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.openwide.easysoa.test.util.AbstractProxyTestStarter;
import org.ow2.frascati.util.FrascatiException;

/**
 * To test replay templates
 * 
 * @author jguillemotte
 *
 */
public class TemplateTest extends AbstractProxyTestStarter {

	// Logger
	private static Logger logger = Logger.getLogger(TemplateTest.class.getName());

	@Before
	public void setUp() throws FrascatiException{
		// Start fraSCAti
		startFraSCAti();
		// Start Http Discovery proxy
		startHttpDiscoveryProxy("httpDiscoveryProxy.composite");
		// Start mock services
		startMockServices(false);
	}	
	
	@Test
	public void replayTemplateWithDefaultValue() throws ClientProtocolException, IOException{
		// TODO : Complete this test with 4 kinds of parameters : formParams, pathParams, QueryParams, WSDLParams
		
		// Read template file with a GET HTTP request
		// returns a request for the replay system
		DefaultHttpClient httpClient = new DefaultHttpClient();		
		HttpGet getRequest = new HttpGet("http://localhost:8090/runManager/exchangeRecordStore/replayTemplate.html");
		String response = httpClient.execute(getRequest, new BasicResponseHandler());
		logger.debug("GetTemplate response = " + response);
		assertTrue(response.contains("comment (String) : <input type=\"text\" name=\"comment\" value=\"test\" />"));
		// Specify the exchangeRecord to use => run/exchangeRecord to get the request 
		
		HttpPost postRequest = new HttpPost("http://localhost:8085/replayWithTemplate/Test_Run/1/testTemplate");
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("user", "FR3Bourgogne.xml")); // tests HTTP Form-like params (others being HTTP path, query and SOAP)
		formparams.add(new BasicNameValuePair("param2", "value2"));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		postRequest.setEntity(entity);
		//HttpParams httpParams = new BasicHttpParams();
		//httpParams.setParameter("", "multipart/form-data");
		//postRequest.setParams(httpParams);
		response = httpClient.execute(postRequest, new BasicResponseHandler());
		logger.debug("replayWithTemplate response = " + response);
		
		// Set the custom values :
		
		// For WSDL : re-use the the existing code working in the scaffolder proxy
		// For Rest : 3 differents cases :
		// Form params : get the inMessage content and change the values
		// Query params : get the complete url and change the values
		// Path Params : This case is harder, need to know the position of each param in the url ... must be specified in the template OR use the discovery mechanism from HTTP discovery proxy 
		
		
		
		// replay the request with specified values (can be default values)  
		
	}
	
}
