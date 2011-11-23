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

package org.easysoa.sca.frascati;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.registry.frascati.EasySOAApiFraSCAti;
import org.easysoa.sca.frascati.mock.TestMock;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.easysoa.sca.visitors.RemoteBindingVisitorFactory;
import org.junit.Before;
import org.junit.Test;
import org.ow2.frascati.util.FrascatiException;

/**
 * Test class for the FraSCAti SCA importer working with the REST API
 * @author jguillemotte
 *
 */
public class ApiFrascatiImportServiceTest extends ApiTestHelperBase {

    static final Log log = LogFactory.getLog(ApiFrascatiImportServiceTest.class);
    
    private ArrayList<ExchangeRecord> recordList;
    
    @Before
    public void setUp() throws FrascatiException {
    	recordList = new ArrayList<ExchangeRecord>();
    	// Start fraSCAti
 	    startFraSCAti();
    	// Maybe need to add a mock REST API server to simulate the exchange
    	startMock();
    	setTest(this);
    }
    
    /**
     * Set this object in the RestApiMock. When the EasySoaRestApiMock receive a request, it calls the check method corresponding to the received request
     * @param test
     * @throws FrascatiException
     */
    protected void setTest(ApiFrascatiImportServiceTest test) throws FrascatiException{
    	System.out.println("composite restApiMock : " + frascati.getComposite("RestApiMock"));
    	frascati.getService(frascati.getComposite("RestApiMock"), "restApiMockServiceJava", TestMock.class).setTest(test);    	    	
    }
    
    @Test
    public void testSCAComposite() throws Exception {
    	// SCA composite file to import :
    	// to load a file, we use simply File, since user.dir is set relatively to the project
    	String scaFilePath = "src/test/resources/" + "org/easysoa/sca/RestSoapProxy.composite";
    	File scaFile = new File(scaFilePath);
    	BindingVisitorFactory bindingVisitorFactory = new RemoteBindingVisitorFactory();
    	ApiFraSCAtiScaImporter importer = new ApiFraSCAtiScaImporter(bindingVisitorFactory, scaFile,  EasySOAApiFraSCAti.getInstance());
		importer.setServiceStackType("FraSCAti");
		importer.setServiceStackUrl("/");
		importer.importSCAComposite();
		checkExchanges();
		
		checkTestSCAComposite(/*...*/);
    }
    
    public void checkExchanges() throws IOException{
    	for(ExchangeRecord record : recordList){
        	assertTrue(record.getRequestContent().contains("RestSoapProxy.composite"));    		
    	}
    }
    
    public void checkTestSCAComposite(/*...*/) throws Exception {
    	// abstract above, impl'd using nuxeo queries when not mocked, when mocked checks that checkCaseOne==true
    	// OR use mock libraries ex. mockito, rmock, easymock, jmock...
    }
    
    /**
     * Assert method for the case one
     * @param req <code>ServletRequest</code>
     * @param res <code>Servletresponse</code>
     */
    // TODO : add more check methods
    public void checkCaseOne(ServletRequest req, ServletResponse res) throws Exception {
    	String requestContent;
		requestContent = new Scanner(req.getInputStream()).useDelimiter("\\A").next();
    	assertTrue(requestContent.contains("RestSoapPoxy.composite"));
    	res.getOutputStream().println("OK");
    	//checkCaseOne==true;
    }
    
    /**
     * Record a REST exchange to be checked
     * @param request The <code>ServletRequest</code> request
     * @param response The <code>ServletResponse</code> response
     * @throws IOException 
     */
    public void recordExchange(ServletRequest request, ServletResponse response) throws IOException {
    	recordList.add(new ExchangeRecord(request, response));
    }
    
    
}
