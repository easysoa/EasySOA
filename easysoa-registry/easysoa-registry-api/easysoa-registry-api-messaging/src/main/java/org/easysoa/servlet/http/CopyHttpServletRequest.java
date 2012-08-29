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

/**
 * 
 */
package org.easysoa.servlet.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Tries to solve getParameters() vs getInputStream() problems, but not required for now (?!!)
 * 
 * @author jguillemotte
 *
 */
public class CopyHttpServletRequest extends HttpServletRequestWrapper {

    // Request content
    private byte[] reqContentBytes = null;

    private Map<String, String[]> parameters = null;
    
    /**
     * 
     * @param request
     * @throws IOException
     */
	public CopyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        /*
        if ("POST".equals(request.getMethod()) && request.getContentType().startsWith("application/x-www-form-urlencoded")) {
        	// handling specific case of form params, see http://markmail.org/message/xstjwbgz5r2ko2oe
        	// TODO doesn't work with DiscoverRest (because of Nuxeo reflection access to request ??) so rather use this code in DiscoverRest ; or possible opposite code ???
            this.reqContentBytes = IOUtils.toByteArray(request.getInputStream());
            String[] valueKeyStrings = new String(reqContentBytes).split("&");
            this.parameters = new java.util.HashMap<String, String[]>(valueKeyStrings.length);
            for (String valueKeyString : valueKeyStrings) {
            	String[] valueKeyTable = valueKeyString.split("=");
            	if (valueKeyTable.length < 2) {
            		continue; // TODO or accept = 1 as "false" ?
            	}
            	this.parameters.put(URLDecoder.decode(valueKeyTable[0], "UTF-8"),
            			new String[] { URLDecoder.decode(valueKeyTable[1], "UTF-8") }); 
            }
        }*/
        
        // NOT REQUIRED FOR NOW else see http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2
        /*try {
            MultipartStream multipartStream = new MultipartStream(new ByteArrayInputStream(reqContentBytes), get it from content type.getBytes());
            boolean nextPart = multipartStream.skipPreamble();
            OutputStream output = null;
            while(nextPart) {
              String header = multipartStream.readHeaders();
              // process headers
              // create some output stream
              multipartStream.readBodyData(output);
              nextPart = multipartStream.readBoundary();
            }
          } catch(MultipartStream.MalformedStreamException e) {
            // the stream failed to follow required syntax
        	  throw e;
          } catch(IOException e) {
            // a read or write error occurred
        	  throw e;
          }*/
    }
    
    /**
     * 
     * @return
     * @throws IOException 
     */
    public ServletInputStream getInputStream() throws IOException {
    	if (this.reqContentBytes == null) {
    		return super.getInputStream();
    	}
        return new ServletInputStream() {
            private ByteArrayInputStream bis = new ByteArrayInputStream(CopyHttpServletRequest.this.reqContentBytes);
            @Override
            public int read() throws IOException {
                return bis.read();
            }
        };
    }    
    
    /**
     * 
     * @return
     * @throws IOException 
     */
    public BufferedReader getReader() throws IOException {
    	if (this.reqContentBytes == null) {
    		return super.getReader();
    	}
        return new BufferedReader(new StringReader(new String(reqContentBytes)));
    }
  
    @Override    
    public String[] getParameterValues(String name) {
    	if (this.parameters == null) {
    		return super.getParameterValues(name);
    	}
    	return this.parameters.get(name);
    }
    
    @SuppressWarnings("rawtypes")
	@Override
	public Map getParameterMap() {
    	if (this.parameters == null) {
    		return super.getParameterMap();
    	}
		return this.parameters;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getParameterNames() {
    	if (this.parameters == null) {
    		return super.getParameterNames();
    	}
	    Enumeration<String> e = Collections.enumeration(this.parameters.keySet());
		return e;
	}

	@Override
    public String getParameter(String name) {
    	if (this.parameters == null) {
    		return super.getParameter(name);
    	}
    	return this.parameters.get(name)[0];
    }
    
}
