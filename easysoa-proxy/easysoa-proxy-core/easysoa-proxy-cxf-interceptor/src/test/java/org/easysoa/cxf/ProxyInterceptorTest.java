/**
 * 
 */
package org.easysoa.cxf;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for CXF proxy interceptor
 * 
 * @author jguillemotte
 *
 */
public class ProxyInterceptorTest {

    @Before
    public void setUp(){
        // start a server, create a proxy interceptor and attach it
        ServerFactoryBean serverFactoryBean = new ServerFactoryBean();
        serverFactoryBean.setServiceClass(ServerTest.class);
        serverFactoryBean.setAddress("http://localhost:9910/");
        serverFactoryBean.setServiceBean(new ServerTestImpl());        
        Server server = serverFactoryBean.create();

        // Creating interceptor
        CXFProxyInterceptor proxyInterceptor = new CXFProxyInterceptor();
        // attaching interceptor
        server.getEndpoint().getInInterceptors().add(proxyInterceptor);
        
    }
    
    @Test
    public void InterceptorTest(){
        
    }
    
}
