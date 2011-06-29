package org.easysoa.test.rest.tools.notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class RestNotificationImpl implements RestNotificationRequest {

	private static final Log log = LogFactory.getLog(RestNotificationImpl.class);
	
	private URL requestUrl;
	private Map<String, String> requestProperties; 
	
	public RestNotificationImpl(String requestUrlString) throws MalformedURLException {
		requestUrl = new URL(requestUrlString);
		requestProperties = new HashMap<String, String>();
	}
	
	public RestNotificationRequest setProperty(String property, String value) {
		requestProperties.put(property, value);
		return this;
	}
	
	public void send() throws IOException, ProtocolException {
		
		// Prepare request
		String body = computeRequestBody();
		String logString = "url= "+requestUrl+", body: "+body;
		
		// Send
		JSONObject result = send(body);
		
		try {
			// Check result, throw error if necessary
			if (!result.getString("result").equals("ok")) {
				log.warn("Failure: "+logString);
				throw new ProtocolException(result.getString("result"));
			}
			log.info("OK: "+logString);
		} catch (JSONException e) {
			log.warn("Failure: "+logString);
			throw new IOException("Response is not formatted as expected", e);
		}
	}
	
	private String computeRequestBody() {
		String body = "";
		for (String key : requestProperties.keySet()) {
			body += key + "=" + requestProperties.get(key) + "&";
		}
		return body;
	}

	private JSONObject send(String requestBody) throws IOException {
		
		// Open connection
		HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);
        
        // Write request
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(requestBody);
        writer.flush();
        
        // Read response
        StringBuffer answer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            answer.append(line);
        }
        writer.close();
        reader.close();
        
		try {
			return new JSONObject(answer.toString());
		} catch (JSONException e) {
			log.error("Failed to convert response to expected JSON object", e);
			return null;
		}
	}

}
