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


package org.easysoa.wsdl.twitter_test_run_wsdl;

import javax.xml.ws.Endpoint;
import org.apache.log4j.Logger;

/**
 * This class was generated by Apache CXF 2.4.1
 * 2012-01-25T10:02:41.943+01:00
 * Generated source version: 2.4.1
 * 
 */
 
public class TwitterTestRunPortType_TwitterTestRunPort_Server{

	// Logger
	private static Logger logger = Logger.getLogger(TwitterTestRunPortType_TwitterTestRunPort_Server.class.getName());
	
	/**
	 * Publish the SOAP service
	 * @throws java.lang.Exception
	 */
    protected TwitterTestRunPortType_TwitterTestRunPort_Server() throws java.lang.Exception {
        logger.debug("Starting Server");
        Object implementor = new TwitterTestRunPortTypeImpl();
        String address = "http://localhost:8092/runManager/target/processOperation";
        Endpoint.publish(address, implementor);
    }
  
    /**
     * Start the server
     * @throws java.lang.Exception
     */
    public static void start() throws java.lang.Exception {
        new TwitterTestRunPortType_TwitterTestRunPort_Server();
        logger.debug("Server ready...");
    }
    
    /**
     * Main method, start the server and shut it down automatically after 30 sec
     * @param args
     * @throws java.lang.Exception
     */
    public static void main(String args[]) throws java.lang.Exception { 
        new TwitterTestRunPortType_TwitterTestRunPort_Server();
        logger.debug("Server ready..."); 
        Thread.sleep(5 * 60 * 1000); 
        logger.debug("Server exiting");
        System.exit(0);
    }
}
