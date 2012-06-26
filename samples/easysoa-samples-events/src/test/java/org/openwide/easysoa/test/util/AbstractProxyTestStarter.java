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

package org.openwide.easysoa.test.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;
import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * Abstract Proxy Test Starter. Launch FraSCAti and the HTTP Discovery Proxy.
 */
public abstract class AbstractProxyTestStarter /*extends RemoteFraSCAtiServiceProviderTest*/
{
    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(
            getInvokingClassName());


    protected static FraSCAti frascati;   

    protected static List<Component> componentList; // TODO move in abstract class
    
    protected File lib;

    static
    {
        System.setProperty("org.ow2.frascati.bootstrap",
                "org.ow2.frascati.bootstrap.FraSCAti");
                //"org.ow2.frascati.bootstrap.FraSCAtiWebExplorer");
    }

    /**
     * Return the invoking class name
     * 
     * @return The class name
     */
    public static String getInvokingClassName()
    {
        return Thread.currentThread().getStackTrace()[1].getClassName();
    }

    
    /**
     * Start FraSCAti
     * @throws FrascatiException 
     */
    protected static void startFraSCAti() throws FrascatiException{
        frascati = FraSCAti.newFraSCAti();
        componentList = new ArrayList<Component>();
    }
    
    
    /**
     * 
     * @throws FrascatiException
     */
    protected static void stopFraSCAti() throws Exception {
        if(frascati != null) {
            logger.info("FraSCATI Stopping");
            if (componentList != null)  {
                  for(Component component : componentList) {
                      logger.debug("Closing component : " + component);
                      frascati.close(component);
               }
            }
            frascati = null;
            componentList = null;
        }
    }
    
    
    /**
     * Start The Galaxy Demo 
     * @throws FrascatiException 
     */
    protected static void startHttpDiscoveryProxy(String composite) throws Exception {
        logger.info("HTTP Discovery Proxy Starting");
        URL compositeUrl = ClassLoader.getSystemResource(composite);
        Component component = frascati.processComposite(compositeUrl.toString(), new ProcessingContextImpl());
        componentList.add(component);
    }

    /**
     * Remove the Jetty deployed apps to avoid blocking tests
     * @param port The port where the Jetty application is deployed 
     */
    protected void cleanJetty(int port){
        JettyHTTPServerEngineFactory jettyFactory = BusFactory.getDefaultBus().getExtension(JettyHTTPServerEngineFactory.class);
        JettyHTTPServerEngine jettyServer = jettyFactory.retrieveJettyHTTPServerEngine(port);
        try {
            Collection<Object> beans = jettyServer.getServer().getBeans();
            if(beans != null){
                for(Object bean : beans){
                    logger.debug("Removing Jetty bean for port " + port);
                    jettyServer.getServer().removeBean(bean);
                }
            }
            jettyFactory.destroyForPort(port);
        }
        catch(Exception ex){
            logger.warn("No beans found for app deployed on Jetty port " + port);
        }
    }


}
