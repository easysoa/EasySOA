/**
 * EasySOA Proxy
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
package org.easysoa.records.filters.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.easysoa.records.filters.ExchangeRecordServletFilterImpl;
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
        tester.addFilter(ExchangeRecordServletFilterImpl.class, "/*", 0);
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
