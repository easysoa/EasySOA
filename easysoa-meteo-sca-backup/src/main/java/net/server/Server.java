package net.server;

import javax.xml.ws.Endpoint;

import net.webservicex.GlobalWeatherSoapImpl1;

public class Server {

    protected Server(String address) throws Exception {
        System.out.println("Starting Server");
        GlobalWeatherSoapImpl1 implementor = new GlobalWeatherSoapImpl1();
        //String address = "http://localhost:9020/WeatherService";
        Endpoint.publish(address, implementor);
    }

    public static void main(String args[]) throws Exception {

    	new Server(args[0]);
    	if(args == null || args[0] == null || "".equals(args[0])){
    		throw new IllegalArgumentException("The deployment address must be specified in arg0 !");
    	}
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
