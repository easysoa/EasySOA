package org.openwide.easysoa.test;

import javax.xml.ws.Endpoint;
import de.sopera.airportsoap.AirportSoap;
import de.sopera.airportsoap.AirportSoapImpl;

public class TalendTutoServiceMockStarter {

	protected TalendTutoServiceMockStarter() throws Exception {
        // START SNIPPET: publish
        System.out.println("Starting Server");
        AirportSoap implementor = new AirportSoapImpl();
        String address = "http://localhost:8200/esb/AirportService";
        Endpoint.publish(address, implementor);
        System.out.println("Server started");        
        // END SNIPPET: publish
    }

    public static void main(String args[]) throws Exception {
    	new TalendTutoServiceMockStarter();
        System.out.println("Server ready...");
        System.out.println("To stop the server, push the 'Q' key !");
        while(System.in.read()!='q' && System.in.read()!='Q'){
        	Thread.sleep(100);
        	// Continue
        }
        System.out.println("Server exiting");
        System.exit(0);        
    }
}

