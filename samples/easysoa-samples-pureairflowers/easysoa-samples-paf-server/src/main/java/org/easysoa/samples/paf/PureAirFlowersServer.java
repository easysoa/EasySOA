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
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.samples.paf;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.easysoa.discovery.classpath.EasySOAClasspathAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jguillemotte, mkalam-alami
 *
 */
public class PureAirFlowersServer {
    
    public final static String PAF_ENDPOINT = "http://localhost:9010/PureAirFlowers";
    
    private final static Logger logger = LoggerFactory.getLogger(PureAirFlowersServer.class);
    
    private Server server = null;
    
    public static void main(String args[]) throws Exception {
        PureAirFlowersServer pafServer = new PureAirFlowersServer();
        pafServer.start();
        
        logger.info("To stop the server, type 'Q' and press Enter!");
        char c;
        do {
            c = (char) System.in.read();
        } while (c != 'q' && c != 'Q');
        
        pafServer.stop();
        System.exit(0);
    }
    
    public void start() {
        if (server == null) {
            logger.info("Starting Server");
            
            JaxWsServerFactoryBean serverFactory = new JaxWsServerFactoryBean();
            serverFactory.setServiceClass(PureAirFlowersService.class);
            serverFactory.setAddress(PAF_ENDPOINT);
            serverFactory.setServiceBean(new PureAirFlowersServiceImpl());

            // EasySOA Classpath discovery entry point
            // TODO How can we allow better decoupling?
            EasySOAClasspathAnalysis.discover("http://localhost:9010");
            
            serverFactory.create();
            
            logger.info("Server started");
        }
        else {
            logger.info("Server was already started");
        }
    }

    public void stop() {
        if (server != null) {
            logger.info("Stopping server");
            server.stop();
            server = null;
            logger.info("Server stopped");
        }
        else {
            logger.info("Server was already stopped");
        }
    }

}
