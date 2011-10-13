/**
 * EasySOA Samples - PureAirFlowers
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
 * Contact : easysoa-dev@groups.google.com
 */

package pureairflowers.clients;

import javax.xml.ws.Endpoint;

public class PureAirFlowersServer {

    protected PureAirFlowersServer() throws Exception {
        System.out.println("Starting Server");
        PureAirFlowersClientsImpl implementor = new PureAirFlowersClientsImpl();
        String address = "http://localhost:9010/PureAirFlowers";
        Endpoint.publish(address, implementor);
        System.out.println("Server started");        
    	// ALTERNATIVE CODE
    	//HelloWorldImpl implementor = new HelloWorldImpl();
    	//JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
    	//svrFactory.setServiceClass(HelloWorld.class);
    	//svrFactory.setAddress("http://localhost:9000/helloWorld");
    	//svrFactory.setServiceBean(implementor);
    	//svrFactory.getInInterceptors().add(new LoggingInInterceptor());
    	//svrFactory.getOutInterceptors().add(new LoggingOutInterceptor());
    	//svrFactory.create();
    }

    public static void main(String args[]) throws Exception {
    	new PureAirFlowersServer();
    	// Problem using this code in a executable JAR.
    	// Spring can't find the XML http://cxf.apache.org/jaxws  
    	//SpringBusFactory bf = new SpringBusFactory();
        //Bus bus = bf.createBus();
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
