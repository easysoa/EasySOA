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

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.easysoa.sca.frascati.ApiFrascatiImportServiceTest;
import org.osoa.sca.annotations.Scope;

/**
 * Rest API mock server to work with local tests
 * @author jguillemotte
 *
 */
@Scope("COMPOSITE")
@SuppressWarnings("serial")
public class EasySoaRestApiMock extends GenericServlet implements Servlet, TestMock<ApiFrascatiImportServiceTest> {

	// Test class
	private ApiFrascatiImportServiceTest test;

	@Override
	public void setTest(ApiFrascatiImportServiceTest test) {
		System.out.println("Setting test : " + test);
		this.test = test;
	}

	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
    	//String requestContent = new Scanner(req.getInputStream()).useDelimiter("\\A").next();
    	// TODO BUG if req.getInputStream() called above, makes the next one (in checkXXX()) explode
		// solution : either pass requestContent in addition to req, or a request wrapper using req
		// as delegate except for InputStream, which would return ex. a ByteArrayInputStream()
		// on the request content...

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
