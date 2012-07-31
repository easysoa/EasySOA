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

package org.easysoa.test.mock.nuxeo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.easysoa.message.InMessage;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.servlet.http.HttpMessageRequestWrapper;
import org.osoa.sca.annotations.Scope;


/**
 * TODO COPIED FROM easysoa-registry-api-frascati, REFACTOR & SHARE IT
 * 
 * Base mock of the EasySOA Core registry (actually of Nuxeo Content Automation).
 * Override it to customize responses.
 * @author mdutoo
 *
 */

@Scope("COMPOSITE")
@SuppressWarnings("serial")
public class EasySoaRestApiMock extends HttpServlet implements Servlet, RecordsProvider {
	
    protected static final Logger log = Logger.getLogger(EasySoaRestApiMock.class);

    // List to record the messages exchanged between client and mock rest api server
    private ArrayList<ExchangeRecord> records = new ArrayList<ExchangeRecord>();

	@Override
	public List<ExchangeRecord> getRecords() {
		return this.records;
	}
	
	/**
	 * Override to set up more specific response, if not an empty OK one.
	 * NB. ca't do request-specific test assertions here, because failures wouldn't
	 * go back up to the test execution thread because it is not the same
	 * as this server thread.
	 * @param request
	 * @param response
	 */
	protected void handleExchange(HttpServletRequest request, HttpServletResponse response) {
		// doing request-specific test assertions if any
    	// setting up response
    	// (nothing to do, default status is 200 - OK
		//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
	    

	    /*if (request.getPathInfo().startsWith("nuxeo/site/automation")) {

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
    	    
            */
        //} else if (request.getPathInfo().startsWith("nuxeo/site/easysoa")) {

            // Record all messages for later check & assertions using getRecords()
            this.recordExchange(request);

        	// callback to let the test do any request-specific assertions and specify custom responses
            this.handleExchange(request, response);

        //}
	}

    /**
     * Record a REST exchange to be checked later by the test using getRecords()
     * @param request The <code>ServletRequest</code> request
     * @param response The <code>ServletResponse</code> response
     * @throws IOException
     */
    private void recordExchange(HttpServletRequest request) throws IOException {
    	// recording exchange
        ExchangeRecord record = new ExchangeRecord("" + new Date().getTime(), new InMessage(new HttpMessageRequestWrapper(request)));
        log.debug("request content : " + record.getInMessage());
        this.records.add(record);
    }

}
