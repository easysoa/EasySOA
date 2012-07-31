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

package org.easysoa.test;

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

