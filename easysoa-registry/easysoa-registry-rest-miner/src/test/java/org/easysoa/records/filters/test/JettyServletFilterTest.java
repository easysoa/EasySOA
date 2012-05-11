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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.easysoa.records.filters.ExchangeRecordServletFilterImpl;
import org.easysoa.test.TestServlet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

/**
 * Class to test the Exchange record servlet filter
 * 
 * @author jguillemotte, mkalam-alami
 *
 */
public class JettyServletFilterTest {

    /*
     * Using Jetty ServletTester is OK for simple ServletFilters tests
     * Here we have to tests more complicated filters using FraSCati/Nuxeo features
     */
    
    private ServletTester tester;

    private FilterHolder exchangeFilter;
    
    @Before
    public void setUp() throws Exception {
        tester = new ServletTester();
        tester.setContextPath("/webApp");
        exchangeFilter = tester.addFilter(ExchangeRecordServletFilterImpl.class, "/*", 0);
        tester.addServlet(TestServlet.class, "/*");
        tester.start();
        exchangeFilter.setFilter(spy(exchangeFilter.getFilter())); // Spy the filter
    }
    
    @Test
    public void testDoFilter() throws Exception {
        HttpTester request = new HttpTester();
        HttpTester response = new HttpTester();
        request.setMethod("GET");
        request.setHeader("Host", "tester");
        request.setURI("http://tester/webApp/");
        request.setVersion("HTTP/1.0");
        String generate = request.generate();
        String responses = tester.getResponses(generate);
        response.parse(responses);
        assertTrue(response.getMethod() == null);
        assertEquals(200, response.getStatus());
        System.out.println("response Content = " + response.getContent());
        System.out.println("response URI = " + response.getURI());
        assertEquals("Test", response.getContent());
        verify(exchangeFilter.getFilter(), times(1)).doFilter(
        		any(ServletRequest.class),
        		any(ServletResponse.class),
        		any(FilterChain.class));
    }
    
    @After
    public void tearDown() throws Exception {
        tester.stop();
    }
    
}
