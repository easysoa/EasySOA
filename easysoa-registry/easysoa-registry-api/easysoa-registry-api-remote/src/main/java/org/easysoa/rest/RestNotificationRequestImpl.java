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
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.common.utils.Base64;

public class RestNotificationRequestImpl implements RestNotificationRequest {
    
    private static Log log = LogFactory.getLog(RestNotificationRequestImpl.class);
    
    private static final int TIMEOUT = 3000;
    
    private URL requestUrl;
    private Map<String, String> requestProperties; 
    private String method;
    private String username;
    private String password;
    
    public RestNotificationRequestImpl(URL requestUrl, String username, String password,
            String method) throws MalformedURLException {
        this.requestUrl = requestUrl;
        this.requestProperties = new HashMap<String, String>();
        this.method = method;
        this.username = username;
        this.password = password;
    }
    
    public RestNotificationRequest setProperty(String property, String value) {
        requestProperties.put(property, value);
        return this;
    }

    public RestNotificationRequest setProperties(Map<String, String> entries) {
        requestProperties.putAll(entries);
        return this;
    }
    
    /**
     * Sends the request and returns its result as a JSONObject
     * (useless for POST requests, but useful to GET documentation)
     */
    public JSONObject send() throws Exception {
        
        // Prepare request
        String body = method.equals("POST") ? computeRequestBody() : null;
        log.error(body);
        String logString = "url= "+requestUrl+", body: "+body;
        
        // Send
        JSONObject result = null;
        try {
            result = send(body);
            if (result == null)
                throw new Exception();
        }
        catch (Exception e) {
            log.warn("Failed to send the notification, is Nuxeo started? (" + e.getMessage() + ")");
            return null;
        }
        
        try {
            // Check result, throw error if necessary
            if (!result.has("parameters") // Notification doc 
                    && (!result.has("result") || !result.getString("result").equals("ok"))) { // Notification result
                log.warn("Notification failure: " + logString);
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
                Base64.encodeBytes((username + ":" + password).getBytes()));
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
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
                if (writer  != null) {
                    writer.close();
                }
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
        
        String answerString = answer.toString();
        try {
            return new JSONObject(answerString);
        } catch (JSONException e) {
            if (answer.toString().startsWith("<!DOCTYPE")) {
                log.warn("Failed to parse response a JSON, credentials are probably invalid.");
            }
            else {
                log.warn("Failed to parse response: " + answerString);
            }
            return null;
        }
    }

}
