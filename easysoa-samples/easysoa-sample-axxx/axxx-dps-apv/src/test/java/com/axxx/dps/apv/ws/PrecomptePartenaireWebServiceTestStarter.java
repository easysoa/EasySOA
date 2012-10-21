package com.axxx.dps.apv.ws;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PrecomptePartenaireWebServiceTestStarter {
    
    private ClassPathXmlApplicationContext context;

    protected PrecomptePartenaireWebServiceTestStarter() {
        System.out.println("Starting Server");
        
        this.context = new ClassPathXmlApplicationContext("axxx-dps-apv-test-context.xml");
    }
    
    public static void main(String args[]) throws java.lang.Exception { 
        new PrecomptePartenaireWebServiceTestStarter();
        System.out.println("Server ready..."); 
        
        Thread.sleep(5 * 60 * 1000); 
        System.out.println("Server exiting");
        System.exit(0);
    }
}
