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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;
import org.apache.log4j.Logger;
import org.easysoa.frascati.FraSCAtiServiceException;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.registry.frascati.EasySOAApiFraSCAti;
import org.easysoa.sca.frascati.mock.TestMock;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.easysoa.sca.visitors.RemoteBindingVisitorFactory;
import org.easysoa.servlet.http.HttpMessageRequestWrapper;
import org.eclipse.jetty.server.Handler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;

import com.openwide.easysoa.message.InMessage;

/**
 * Tests FraSCAti SCA "import" & "(runtime startup) discovery" working with the
 * REST EasySOA API, done from a remote FraSCAti.
 * 
 * PROBLEM : injected FraSCAti (gotten from ApiTestHelperBase) is the wrapper
 * and not an actual "remoted" FraSCAti.
 * 
 * @author jguillemotte
 * 
 */
//@RunWith(FeaturesRunner.class)
public class RestApiFrascatiImportServiceTest extends ApiTestHelperBase {

    static final Logger logger = Logger.getLogger(RestApiFrascatiImportServiceTest.class);
    
    private static final String AUTOMATION_URL = "http://localhost:8080/nuxeo/site/automation";
    
    // List to record the messages exchanged between client and mock rest api server
    private ArrayList<ExchangeRecord> recordList;

    // Boolean to indicate if the test is mocked or not
    boolean mockedTest = true;

    @Before
    public void setUp() throws Exception {
        // init record list
        recordList = new ArrayList<ExchangeRecord>();
        // Start fraSCAti
        startFraSCAti();
        // Start the mock service composite
        startMock();
        // Set the test class : Mockito
        setTest(this);
    }

    @After
    public void tearDown() throws Exception {
              
        stopFraSCAti();     
        
        JettyHTTPServerEngineFactory jettyFactory =
            BusFactory.getDefaultBus().getExtension(JettyHTTPServerEngineFactory.class);
        
        JettyHTTPServerEngine jettyServer = jettyFactory.retrieveJettyHTTPServerEngine(8080);
        Collection<Object> beans = jettyServer.getServer().getBeans();
        
        if(beans != null)
        {
            for(Object bean : beans)
            {
                jettyServer.getServer().removeBean(bean);
            }
        }
        jettyFactory.destroyForPort(8080);
    }

    /**
     * Set the ApiFrascatiImportServiceTest in the RestApiMock. When the
     * EasySoaRestApiMock receive a request, it calls the check method
     * corresponding to the received request
     * 
     * @param test
     *            ApiFrascatiImportServiceTest
     * @throws NuxeoFraSCAtiException
     *             If a problem occurs when the set is done
     */
    @SuppressWarnings("unchecked")
    protected void setTest(RestApiFrascatiImportServiceTest test) throws FraSCAtiServiceException {
        logger.debug("composite restApiMock : " + frascati.getComposite("RestApiMock"));
        ((TestMock<RestApiFrascatiImportServiceTest>) frascati.getService("RestApiMock", "restApiMockServiceJava", TestMock.class)).setTest(test);
    }

    /**
     * Tests import of SCA composite file, done from remote FraSCAti. NOT
     * IMPORTANT since "import" is more of a Nuxeo-side functionality. PROBLEM :
     * injected FraSCAti is the wrapper and not an actual "remoted" FraSCAti.
     * 
     * Within the composite, referenced classes are missing so there should be
     * (non-fatal) warnings.
     * 
     * NB. Use Mockito to check that the service method are well called
     * 
     * @throws Exception
     */
    @Test
    //@Ignore
    public void testSCACompositeImport() throws Exception {

        // SCA composite file to import :
        // to load a file, we use simply File, since user.dir is set relatively
        // to the project
        String scaFilePath = "src/test/resources/" + "org/easysoa/sca/RestSoapProxy.composite";
        File scaFile = new File(scaFilePath);
        logger.debug("Creating instance of ApiFracSCAtiScaImporter");
        BindingVisitorFactory bindingVisitorFactory = new RemoteBindingVisitorFactory();
        logger.debug("Creating instance of ApiFracSCAtiScaImporter");
        ApiFraSCAtiScaImporter importer = new ApiFraSCAtiScaImporter(bindingVisitorFactory, scaFile, 
                EasySOAApiFraSCAti.getInstance());

        importer.setAppliImplURL("http://localhost"); // choose appli to import in
        importer.setServiceStackType("FraSCAti");
        importer.setServiceStackUrl("/");
        // Create a spy importer for Mockito
        logger.debug("Creating mockito spy importer");
        ApiFraSCAtiScaImporter spyImporter = spy(importer);
        try {
            // Import the SCA composite
            logger.debug("Importing composite");
            spyImporter.importSCAComposite();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Check the recorded exchanges
        checkExchanges("RestSoapProxy.composite");

        // Check with Mockito TODO better (mockito mock of client api...), otherwise useless
        verify(spyImporter).importSCAComposite();
        //
        // checkTestSCAComposite(/*...*/);
    }

    /**
     * Tests import of SCA zip file, done from remote FraSCAti. NOT IMPORTANT
     * since "import" is more of a Nuxeo-side functionality. PROBLEM : injected
     * FraSCAti is the wrapper and not an actual "remoted" FraSCAti.
     * 
     * All referenced classes are provided within the SCA zip file, so there
     * should be few (none ?) (non-fatal) warnings.
     * 
     * Use Mockito to check that the service method are well called
     * 
     * @throws Exception
     */
    @Test
    //@Ignore
    public void testSCAZipImport() throws Exception {

        // SCA composite file to import :
        // to load a file, we use simply File, since user.dir is set relatively
        // to the project
        String scaZipFilePath = "src/test/resources/" + "proxy-1.0-SNAPSHOT.jar";
        File scaZipFile = new File(scaZipFilePath);
        BindingVisitorFactory bindingVisitorFactory = new RemoteBindingVisitorFactory();
        ApiFraSCAtiScaImporter importer = new ApiFraSCAtiScaImporter(bindingVisitorFactory, scaZipFile, 
                EasySOAApiFraSCAti.getInstance());

        importer.setAppliImplURL("http://localhost"); // choose appli to import in
        importer.setServiceStackType("FraSCAti");
        importer.setServiceStackUrl("/");
        // Create a spy importer for Mockito
        ApiFraSCAtiScaImporter spyImporter = spy(importer);
        try {
            // Import the SCA composite
            spyImporter.importSCAZip();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Check the recorded exchanges
        checkExchanges("proxy-1.0-SNAPSHOT.jar");
        
        // Check with Mockito TODO better (mockito mock of client api...), otherwise useless
        verify(spyImporter).importSCAZip();
        //
        // checkTestSCAComposite(/*...*/);
    }

    /**
     * Tests runtime discovery of services when FraSCAti starts an application.
     * IMPORTANT since it should work in a "remoted" FraSCAti. PROBLEM :
     * injected FraSCAti is the wrapper and not an actual "remoted" FraSCAti.
     * 
     * @throws FrascatiException
     * @throws Exception
     */
    @Test
    //@Ignore
    public void testFraSCAtiRuntimeDiscovery() throws Exception {

        String scaZipFilePath = "src/test/resources/" + "proxy-1.0-SNAPSHOT.jar";
        File scaZipFile = new File(scaZipFilePath);

        ApiRuntimeFraSCAtiScaImporter importer = (ApiRuntimeFraSCAtiScaImporter) 
        EasySOAApiFraSCAti.getInstance().newRuntimeScaImporter();
        
        importer.setAppliImplURL("http://localhost"); // choose appli to import in
        importer.setServiceStackType("FraSCAti");
        importer.setServiceStackUrl("/");
        importer.compositeFile = new File("RestSoapProxy.composite"){
            
            public String getName()
            {
                return "RestSoapProxy.composite";
            }
        };
        
        frascati.setScaImporterRecipient(importer);
        frascati.processComposite("RestSoapProxy.composite", FraSCAtiServiceItf.check, scaZipFile.toURI().toURL());

        // Check the recorded exchanges
        checkExchanges("RestSoapProxy.composite");
    }

    /**
     * Check the recorded exchanges
     * 
     * @throws IOException
     */
    public void checkExchanges(String toLookForInContent) {
        boolean atLeastOne = false;
        for (ExchangeRecord record : recordList) {
            atLeastOne = true;
            assertTrue("'" + toLookForInContent + "' not found in request", record.getInMessage().getMessageContent().getRawContent().contains(toLookForInContent));
        }
        assertTrue("There should be at least one exchange after SCA service discovery", atLeastOne);
    }

    // add a method to check the recorded exchanges in the order :
    // - eg check record one contains "blabla", record 2 contains
    // "an other blabla" ...
    /**
     * 
     * @throws Exception
     */
    public void checkTestSCAComposite(/* ... */) throws Exception {

        // abstract above, impl'd using nuxeo queries when not mocked, when
        // mocked checks that checkCaseOne==true
        // OR use mock libraries ex. mockito, rmock, easymock, jmock...
        if (mockedTest) {

        } else {

        }
    }

    /**
     * Assert method for the case one
     * 
     * @param req
     *            <code>ServletRequest</code>
     * @param res
     *            <code>Servletresponse</code>
     */
    /*
     * public void checkCaseOne(ServletRequest req, ServletResponse res) throws
     * Exception { String requestContent; requestContent = new
     * Scanner(req.getInputStream()).useDelimiter("\\A").next();
     * assertTrue(requestContent.contains("RestSoapPoxy.composite"));
     * res.getOutputStream().println("OK"); //checkCaseOne==true; }
     */

    /**
     * Record a REST exchange to be checked
     * @param request The <code>ServletRequest</code> request
     * @param response The <code>ServletResponse</code> response
     * @throws IOException
     */
    public void recordExchange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExchangeRecord record = new ExchangeRecord("" + new Date().getTime(), new InMessage(new HttpMessageRequestWrapper(request)));
        log.debug("request content : " + record.getInMessage());
        recordList.add(record);
    }

}
