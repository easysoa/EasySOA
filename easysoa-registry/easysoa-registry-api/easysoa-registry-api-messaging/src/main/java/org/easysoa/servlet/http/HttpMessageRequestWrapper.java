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

import org.apache.commons.io.IOUtils;

/**
 * 
 * @author jguillemotte
 *
 */
public class HttpMessageRequestWrapper extends HttpServletRequestWrapper {

    // Request content
    private String reqContent;
    
    private Map<String, String[]> parameters;
    
    /**
     * 
     * @param request
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
	public HttpMessageRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        reqContent = IOUtils.toString(request.getInputStream());    
        this.parameters = request.getParameterMap(); // TODO or also clone ??
    }
    
    /**
     * 
     * @return
     */
    public ServletInputStream getInputStream() {
        return new ServletInputStream() {
            private ByteArrayInputStream bis = new ByteArrayInputStream(reqContent.getBytes());
            @Override
            public int read() throws IOException {
                return bis.read();
            }
        };
    }    
    
    /**
     * 
     * @return
     */
    public BufferedReader getReader() {
        return new BufferedReader(new StringReader(reqContent));
    }
  
    @Override    
    public String[] getParameterValues(String name) {
    	return this.parameters.get(name);
    }
    
    @SuppressWarnings("rawtypes")
	@Override
	public Map getParameterMap() {
		return this.parameters;
	}

	@Override
	public Enumeration<String> getParameterNames() {
	    Enumeration<String> e = Collections.enumeration(this.parameters.keySet());
		return e;
	}

	@Override
    public String getParameter(String name) {
    	return this.getParameterMap().get(name).toString();
    }
    
}
