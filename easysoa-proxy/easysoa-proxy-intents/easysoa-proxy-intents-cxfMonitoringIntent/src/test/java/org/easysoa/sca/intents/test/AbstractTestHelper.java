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

package org.easysoa.sca.intents.test;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * Abstract Test Helper. Launch FraSCAti, the HTTP Discovery Proxy and the
 * scaffolder proxy.
 */
public abstract class AbstractTestHelper {

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(AbstractTestHelper.class.getName());;

    /** The FraSCAti platform */
    protected static FraSCAti frascati;

    protected static ArrayList<Component> componentList;

    static {
        System.setProperty("org.ow2.frascati.bootstrap", "org.ow2.frascati.bootstrap.FraSCAti");
    }

    /**
     * Start FraSCAti
     * 
     * @throws FrascatiException
     */
    protected static void startFraSCAti() throws FrascatiException {
        logger.info("FraSCATI Starting");
        componentList = new ArrayList<Component>();
        frascati = FraSCAti.newFraSCAti();
    }

    /**
     * 
     * @throws FrascatiException
     */
    protected static void stopFraSCAti() throws FrascatiException {
        logger.info("FraSCATI Stopping");
        if (componentList != null) {
            for (Component component : componentList) {
                frascati.close(component);
            }
        }
    }

    /**
     * Start the smart travel services mock for tests
     * @throws Exception
     */
    protected static void startSoapMockServices() throws Exception {
        logger.info("SOAP Services Mock Starting");
        componentList.add(frascati.processComposite("services-backup-sca.composite", new ProcessingContextImpl()));
    }
    
    /**
     * 
     * @throws Exception
     */
    protected static void startRestMockServices() throws Exception {
        logger.info("REST Services Mock Starting");
        componentList.add(frascati.processComposite("twitterMockRest.composite", new ProcessingContextImpl()));        
    }

}
