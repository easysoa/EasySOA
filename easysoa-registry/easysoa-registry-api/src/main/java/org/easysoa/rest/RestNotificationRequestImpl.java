/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.utils.Base64;
import org.json.JSONException;
import org.json.JSONObject;

public class RestNotificationRequestImpl implements RestNotificationRequest {
    
    private static final String LOGIN = "Administrator";
    private static final String PASSWORD = "Administrator";

    private static Log log = LogFactory.getLog(RestNotificationRequestImpl.class);
    
    private URL requestUrl;
    private Map<String, String> requestProperties; 
    private String method;
    
    public RestNotificationRequestImpl(URL requestUrl, String method) throws MalformedURLException {
        this.requestUrl = requestUrl;
        this.requestProperties = new HashMap<String, String>();
        this.method = method;
    }
    
    public RestNotificationRequest setProperty(String property, String value) {
        requestProperties.put(property, value);
        return this;
    }
    
    public JSONObject send() throws Exception {
        
        // Prepare request
        String body = method.equals("POST") ? computeRequestBody() : null;
        String logString = "url= "+requestUrl+", body: "+body;
        
        // Send
        JSONObject result = null;
        try {
            result = send(body);
            if (result == null)
                throw new Exception();
        }
        catch (Exception e) {
            log.warn("Failed to send the notification due to an external problem (Nuxeo not started?)");
            return null;
        }
        
        try {
            // Check result, throw error if necessary
            if (!result.has("parameters") // Notification doc 
                    && (!result.has("result") || !result.getString("result").equals("ok"))) { // Notification result
                log.warn("Failure: "+logString);
                throw new Exception("Request result is not as expected: '"+result.getString("result")+"'");
            }
            log.info("OK: "+logString);
            return result;
            
        } catch (JSONException e) {
            log.warn("Failure: "+logString);
            throw new IOException("Response is not formatted as expected", e);
        }
        
    }
    
    private String computeRequestBody() {
        StringBuffer body = new StringBuffer();
        for (Entry<String, String> entry : requestProperties.entrySet()) {
            body.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        return body.toString();
    }

    private JSONObject send(String requestBody) throws IOException {
        
        // Open connection
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Authorization", "Basic " +
                Base64.encodeBytes((LOGIN + ":" + PASSWORD).getBytes()));
        connection.setDoOutput(true);
        
        // Write request
        OutputStreamWriter writer = null;
        if (requestBody != null) {
            try {
                writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(requestBody);
                writer.flush();
            }
            finally {
                writer.close();
            }
        }
        
        // Read response
        StringBuffer answer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        try  {
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
        }
        finally {
            reader.close();
        }
        
        try {
            return new JSONObject(answer.toString());
        } catch (JSONException e) {
            log.error("Failed to convert response to expected JSON object", e);
            return null;
        }
    }

}
