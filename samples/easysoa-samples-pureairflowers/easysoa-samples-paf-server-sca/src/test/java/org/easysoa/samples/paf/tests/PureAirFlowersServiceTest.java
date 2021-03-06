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

package org.easysoa.samples.paf.tests;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.easysoa.samples.paf.PureAirFlowersServer;
import org.easysoa.samples.paf.PureAirFlowersService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author mkalam-alami
 *
 */
public class PureAirFlowersServiceTest {

    private final static Logger logger = LoggerFactory.getLogger(PureAirFlowersServiceTest.class);

    private final static boolean USE_CLASSPATH_DISCOVERY = true;
    
    private static PureAirFlowersServer server;
    
    private static PureAirFlowersService client;
    
    @BeforeClass
    public static void setUp() {
        server = new PureAirFlowersServer(USE_CLASSPATH_DISCOVERY);
        server.start();
        
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(PureAirFlowersService.class);
        factory.setAddress(PureAirFlowersServer.PAF_ENDPOINT);
        client = (PureAirFlowersService) factory.create();
        
        logger.info("---------------- BEGIN TESTS");
    }
    
    @AfterClass
    public static void tearDown() {
        logger.info("---------------- END TESTS");
        server.stop();
    }
    
    
    @Test
    public void testPafService() {
        logger.info("Orders count for Bob: " + client.getOrdersNumber("Bob"));
    }
    
    
}
