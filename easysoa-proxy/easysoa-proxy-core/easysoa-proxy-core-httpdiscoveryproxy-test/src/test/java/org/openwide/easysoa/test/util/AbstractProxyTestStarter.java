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
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.sca.frascati.RemoteFraSCAtiServiceProvider;

/**
 * Abstract Proxy Test Starter. Launch FraSCAti and the HTTP Discovery Proxy.
 */
public abstract class AbstractProxyTestStarter
{

    /**
     * Logger
     */
    private static final Logger logger = Logger
            .getLogger(getInvokingClassName());

    /** The FraSCAti platform */
    // protected static FraSCAti frascati;
    protected static FraSCAtiServiceItf frascati;

    // protected static ArrayList<Component> componentList;
    protected static ArrayList<String> componentList;

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
     * 
     * @throws Exception
     */
    protected static void startFraSCAti() throws Exception
    {

        logger.info("FraSCATI Starting");
        // componentList = new ArrayList<Component>();
        componentList = new ArrayList<String>();
        // TODO replace this code by new RemoteFrascatiServiceProvider
        // frascati = FraSCAti.newFraSCAti();
        File frascatiLibFolder = new File(
                "../../../easysoa-distribution/easysoa/frascati/lib");
        RemoteFraSCAtiServiceProvider serviceProvider = new RemoteFraSCAtiServiceProvider(
                frascatiLibFolder);
        frascati = serviceProvider.getFraSCAtiService();
    }

    /**
     * 
     * @throws FrascatiException
     */
    protected static void stopFraSCAti() throws Exception
    {

        logger.info("FraSCATI Stopping");
        if (componentList != null)
        {
            // for(Component component : componentList){
            for (String component : componentList)
            {
                logger.debug("Closing component : " + component);
                frascati.stop(component);
                // frascati.close(component);
            }
        }

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
        frascati = null;
    }

    /**
     * Start HTTP Proxy
     * 
     * @throws FrascatiException
     * @throws FraSCAtiServiceException
     */
    protected static void startHttpDiscoveryProxy(String composite, URL... urls)
            throws Exception
    {

        logger.info("HTTP Discovery Proxy Starting");
        // Component component = frascati.processComposite(composite,new
        // ProcessingContextImpl(new FrascatiClassLoader(urls)));
        String component = frascati.processComposite(composite,
                FraSCAtiServiceItf.all, urls);
        componentList.add(component);
    }

    /**
     * Start the services mock for tests (Meteo mock, twitter mock ...)
     * 
     * @param withNuxeoMock
     *            If true, the Nuxeo mock is started
     * @throws FrascatiException
     *             if a problem occurs during the start of composites
     * @throws FraSCAtiServiceException
     */
    protected static void startMockServices(boolean withNuxeoMock)
            throws Exception
    {

        logger.info("Services Mock Starting");
        // componentList.add(frascati.processComposite("twitterMockRest.composite",
        // new ProcessingContextImpl()));
        componentList.add(frascati
                .processComposite("twitterMockRest.composite"));
        // start Nuxeo mock
        if (withNuxeoMock)
        {
            // componentList.add(frascati.processComposite("nuxeoMockRest.composite",
            // new ProcessingContextImpl()));
            componentList.add(frascati
                    .processComposite("nuxeoMockRest.composite"));
        }
        // componentList.add(frascati.processComposite("meteoMockSoap.composite",
        // new ProcessingContextImpl()));
        componentList.add(frascati.processComposite("meteoMockSoap.composite"));
    }

    /**
     * 
     * @throws Exception
     */
    public void startNewRun(String runName) throws Exception
    {

        DefaultHttpClient httpClient = new DefaultHttpClient();
        // Start a new Run
        HttpPost newRunPostRequest = new HttpPost("http://localhost:"
                + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT
                + "/run/start/" + runName);
        httpClient.execute(newRunPostRequest, new BasicResponseHandler());
    }

    /**
     * 
     * @throws Exception
     */
    public void stopAndSaveRun() throws Exception
    {

        DefaultHttpClient httpClient = new DefaultHttpClient();
        // Stop and save the run
        HttpPost stopRunPostRequest = new HttpPost("http://localhost:"
                + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT
                + "/run/stop");
        httpClient.execute(stopRunPostRequest, new BasicResponseHandler());

    }

    /**
     * 
     * @throws Exception
     */
    public void deleteRun() throws Exception
    {

        DefaultHttpClient httpClient = new DefaultHttpClient();
        // delete the run
        HttpPost deleteRunPostRequest = new HttpPost("http://localhost:"
                + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT
                + "/run/delete");
        httpClient.execute(deleteRunPostRequest, new BasicResponseHandler());
    }

}
