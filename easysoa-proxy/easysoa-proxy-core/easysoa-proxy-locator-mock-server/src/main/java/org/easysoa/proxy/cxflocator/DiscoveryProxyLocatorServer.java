package org.easysoa.proxy.cxflocator;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DiscoveryProxyLocatorServer {
    
    private ClassPathXmlApplicationContext context;

    protected DiscoveryProxyLocatorServer() throws java.lang.Exception {
        System.out.println("Starting Server");
        
        this.context = new ClassPathXmlApplicationContext("cxf.xml");
    }
    
    public static void main(String args[]) throws java.lang.Exception { 
        new DiscoveryProxyLocatorServer();
        System.out.println("Server ready..."); 
        
        Thread.sleep(5 * 60 * 1000); 
        System.out.println("Server exiting");
        System.exit(0);
    }
}
