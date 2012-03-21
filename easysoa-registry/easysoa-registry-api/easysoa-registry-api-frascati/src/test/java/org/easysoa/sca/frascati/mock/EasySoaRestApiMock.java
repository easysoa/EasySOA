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

package org.easysoa.sca.frascati.mock;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.easysoa.sca.frascati.RestApiFrascatiImportServiceTest;
import org.osoa.sca.annotations.Scope;

/**
 * Rest API mock server to work with local tests
 * @author jguillemotte
 *
 */
@Scope("COMPOSITE")
@SuppressWarnings("serial")
//public class EasySoaRestApiMock extends GenericServlet implements Servlet, TestMock<ApiFrascatiImportServiceTest> {
public class EasySoaRestApiMock extends HttpServlet implements Servlet, TestMock<RestApiFrascatiImportServiceTest> {

	// Test class
	private RestApiFrascatiImportServiceTest test;

	@Override
	public void setTest(RestApiFrascatiImportServiceTest test) {
		System.out.println("Setting test : " + test);
		this.test = test;
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
	    // wrapping request to cache content, because
	    // 1. otherwise in test.record(), new InMessage(request) won't be able to read its content (??!!)
	    // and 2. it eases debugging but could also allow test assertions such as :
        //String requestContent = new Scanner(req.getInputStream()).useDelimiter("\\A").next();
	    
	    final String reqContent = IOUtils.toString(request.getInputStream());
        ///System.err.println("\n\nreq content:\n" + reqContent);
	    
	    request = new HttpServletRequestWrapper(request) {
	        public ServletInputStream getInputStream() {
	            return new ServletInputStream() {
                    private ByteArrayInputStream bis = new ByteArrayInputStream(reqContent.getBytes());
                    @Override
                    public int read() throws IOException {
                        return bis.read();
                    }
                };
	        }
            public BufferedReader getReader() {
                return new BufferedReader(new StringReader(reqContent));
            }
	    };
	    

	    if (request.getPathInfo().startsWith("nuxeo/site/automation")) {

	        // providing nice responses to nuxeo automation client
	        
	        String resContent = "";
    	    if ("nuxeo/site/automation/".equals(request.getPathInfo())) {
    	        response.setContentType("application/json+nxautomation"); // because expected l108 in org.nuxeo.ecm.automation.client.jaxrs.impl.HttpConnector
    	        resContent = IOUtils.toString(new FileReader("src/test/resources/nuxeo/site/automation")); // automation registry 
    	        
    	    } else if ("nuxeo/site/automation/login".equals(request.getPathInfo())) {
                response.setContentType("application/json+nxentity"); // because expected l108 in org.nuxeo.ecm.automation.client.jaxrs.impl.HttpConnector
                resContent = "{\"entity-type\":\"login\",\"username\":\"Administrator\",\"isAdministrator\":true,\"groups\":[\"administrators\"]}";
                // {"entity-type":"login","username":"Administrator","isAdministrator":true,"groups":["administrators"]}    
    	    }

            //response.setContentLength(resContent.length()); // not required
            IOUtils.write(resContent.getBytes(), response.getOutputStream()); // AND NOT WRITER else character set ISO-8859-1 is added and Automation client's Jackson JSON parser loops endlessly
    	    
            
        } else if (request.getPathInfo().startsWith("nuxeo/site/easysoa")) {

            // providing nice responses to easysoa discovery client

            // TODO : Call a 'record' method with the request and response.
            // Record all messages and then call a method to check the recorded messages        
            test.recordExchange(request, response);

            // here, call methods on test containing asserts related to each use case of the mock, ex:
            // if isCaseOne(req) then test.checkCaseOne(req, res)...        
            /*try{
                test.checkCaseOne(req, res);                
            }
            catch(Exception ex){
                throw new ServletException(ex.getMessage());
            }*/    
        }
	}

}
