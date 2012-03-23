/**
 * 
 */
package org.easysoa.records.filters.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.easysoa.records.filters.ExchangeRecordServletFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

/*
import org.easysoa.test.EasySOACoreTestFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.frascati.test.FraSCAtiFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
*/

/**
 * Class to test the Exchange record servlet filter
 * 
 * @author jguillemotte
 *
 */
//@RunWith(FeaturesRunner.class)
//@Features({EasySOACoreTestFeature.class,FraSCAtiFeature.class})
//@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class JettyServletFilterTest {

    // Add a dependency to Easysoa-registry-frascati ???
    
    // Start Nuxeo
    // Start embeded FraSCAti services
    // Plug the ExchangeRecordServletFilter
    // Send a test request
    // Check if a new run with exchange record is recorded
    
    /*
     * Using Jetty ServletTester is OK for simple ServletFilters tests
     * Here we have to tests more complicated filters using FraSCati/Nuxeo features
     */
    
    //
    private ServletTester tester;
    
    @Before
    public void setUp() throws Exception {
        tester = new ServletTester();
        tester.setContextPath("/webApp");
        tester.addFilter(ExchangeRecordServletFilter.class, "/*", 0);
        tester.addServlet(DefaultServlet.class, "/");
        tester.start();
    }
    
    @Test
    public void testDoFilter() throws Exception {
        HttpTester request = new HttpTester();
        HttpTester response = new HttpTester();
        request.setMethod("GET");
        request.setHeader("Host", "tester");
        //request.setURI("/app/Person");
        //request.setURI("/webApp");
        request.setURI("http://tester/webApp/");
        request.setVersion("HTTP/1.0");
        String generate = request.generate();
        String responses = tester.getResponses(generate);
        response.parse(responses);
        assertTrue(response.getMethod() == null);
        assertEquals(200, response.getStatus());
        System.out.println("response Content = " + response.getContent());
        System.out.println("response URI = " + response.getURI());
        //assertEquals("<h1>Hello Servlet</h1>", response.getContent());
    }
    
    @After
    public void tearDown() throws Exception {
        tester.stop();
    }
    
}
