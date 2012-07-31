/**
 * 
 */
package org.easysoa.cxf;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.util.ContentReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for CXF proxy interceptor
 * 
 * @author jguillemotte
 *
 */
public class ProxyInterceptorTest {

    // Logger
    private static Logger logger = Logger.getLogger(ProxyInterceptorTest.class.getName());
    
    //
    private Server server;
    
    @Before
    public void setUp(){
        // start a server, create a proxy interceptor and attach it
        ServerFactoryBean serverFactoryBean = new ServerFactoryBean();
        serverFactoryBean.setServiceClass(ServerTest.class);
        serverFactoryBean.setAddress("http://localhost:9910/");
        serverFactoryBean.setServiceBean(new ServerTestImpl());        
        server = serverFactoryBean.create();

        // Creating interceptor
        CXFProxyInterceptor proxyInterceptor = new CXFProxyInterceptor();
        // attaching interceptor
        server.getEndpoint().getInInterceptors().add(proxyInterceptor);
    }
    
    @Test
    public void InterceptorTest() throws IllegalStateException, Exception {
        // send a request to trigger the interceptor
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpUriRequest httpUriRequest;
        httpUriRequest = new HttpGet("http://localhost:9910/?wsdl");
        HttpResponse response = httpClient.execute(httpUriRequest);
        // Need to read the response body entierely to be able to send another request
        String entityResponseString = ContentReader.read(response.getEntity().getContent());
        logger.debug(entityResponseString);
    }
    
    @After
    public void tearDown(){
        // Destroy the server
        server.destroy();
    }
}
