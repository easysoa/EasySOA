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

package org.openwide.easysoa.test;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openwide.easysoa.test.util.AbstractProxyTestStarter;

/**
 * HTTP Discovery Proxy Test Starter. Just launch the proxy in FraSCAti and wait
 * for a user action to stop.
 */
public class ProxyStarter extends AbstractProxyTestStarter
{

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(ProxyStarter.class
            .getClass());

    /**
     * Initialize one time the remote systems for the test FraSCAti and HTTP
     * discovery Proxy ...
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUp() throws Exception
    {

        logger.info("Launching FraSCAti and HTTP Discovery Proxy");
        startFraSCAti();
        startHttpDiscoveryProxy("src/main/resources/httpDiscoveryProxy.composite");
    }

    /**
     * Stop FraSCAti components
     * 
     * @throws FrascatiException
     */
    @AfterClass
    public static void cleanUp() throws Exception
    {

        logger.info("Stopping FraSCAti...");
        stopFraSCAti();

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name;
        try
        {
            name = new ObjectName("SCA domain:name0=*,*");
            Set<ObjectName> names = mbs.queryNames(name, name);
            for (ObjectName objectName : names)
            {
                mbs.unregisterMBean(objectName);
            }
            mbs.unregisterMBean(new ObjectName(
                    "org.ow2.frascati.jmx:name=FrascatiJmx"));
        } catch (MalformedObjectNameException e)
        {
            // e.printStackTrace();
        } catch (NullPointerException e)
        {
            // e.printStackTrace();
        } catch (MBeanRegistrationException e)
        {
            // e.printStackTrace();
        } catch (InstanceNotFoundException e)
        {
            // e.printStackTrace();
        }
    }

    /**
     * This test do nothing, just wait for a user action to stop the proxy.
     * 
     * @throws ClientException
     * @throws SOAPException
     * @throws IOException
     */
    @Test
    // @Ignore
    public final void testWaitUntilRead() throws Exception
    {

        logger.info("Http Discovery Proxy started, wait for user action to stop !");
        // Just push a key in the console window to stop the test
        System.in.read();
        logger.info("Http Discovery Proxy stopped !");
    }

}
