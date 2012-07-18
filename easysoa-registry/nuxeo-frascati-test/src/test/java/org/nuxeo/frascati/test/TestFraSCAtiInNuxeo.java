/**
 * EasySOA Registry
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

package org.nuxeo.frascati.test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import org.easysoa.frascati.FraSCAtiServiceException;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.frascati.NuxeoFraSCAtiException;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(FraSCAtiFeature.class)
public class TestFraSCAtiInNuxeo
{

    private final String resourcePath = "src/test/resources/";
    private final String pojoPath = resourcePath  + "helloworld-pojo-1.5-SNAPSHOT.jar";
    private final String wsPath = resourcePath  + "helloworld-ws-server-1.5-SNAPSHOT.jar";
    private final String servletPath = resourcePath + "frascati-helloworld-servlet-1.5-SNAPSHOT.jar";

    protected static final Logger log = Logger
            .getLogger(TestFraSCAtiInNuxeo.class.getCanonicalName());

    private final String STARTED = "STARTED";
    private final String STOPPED = "STOPPED";

    FraSCAtiServiceItf frascatiService = null;
    String fcomponent;

    public TestFraSCAtiInNuxeo()
    {
    }

    @Before
    public void setUp() throws MalformedURLException, ClientException,
            Exception
    {
        try
        {
            frascatiService = (FraSCAtiServiceItf) Framework.getLocalService(
                    FraSCAtiServiceProviderItf.class).getFraSCAtiService();
            assertNotNull(frascatiService);
            File scaFile = new File(pojoPath);
            scaFile = scaFile.getAbsoluteFile();
            fcomponent = frascatiService.processComposite(
                    "helloworld-pojo.composite", FraSCAtiServiceItf.all,
                    scaFile.toURI().toURL()).getName();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws NuxeoFraSCAtiException
    {
        try
        {
            frascatiService.remove("helloworld-pojo");
        } catch (FraSCAtiServiceException e)
        {
            e.printStackTrace();
        }
        fcomponent = null;
    }

    /**
     * Test if an existing service can be retrieved
     * 
     * @throws NuxeoFraSCAtiException
     */
    @Test
    public void testGetRunnableServiceAndExecuteIt()
            throws NuxeoFraSCAtiException
    {
        Runnable r = null;
        try
        {
            r = (Runnable) frascatiService.getService(fcomponent, "r",
                    Runnable.class);
        } catch (FraSCAtiServiceException e)
        {
            e.printStackTrace();
        }
        assertNotNull(r);
        r.run();
    }

    /**
     * Test NuxeoFraSCAtiException is thrown if a service is unknown
     * 
     * @throws NuxeoFraSCAtiException
     */
    @Test(expected = FraSCAtiServiceException.class)
    public void testThrowExceptionIfGetUnexistingService()
            throws FraSCAtiServiceException
    {
        frascatiService.getService(fcomponent, "unknown", Runnable.class);
    }

    @Test
    public void testSCACompositeLifeCycle()
    {
        assertTrue(STARTED.equals(frascatiService.state(fcomponent)));
        frascatiService.stop(fcomponent);
        assertTrue(STOPPED.equals(frascatiService.state(fcomponent)));
        frascatiService.start(fcomponent);
        assertTrue(STARTED.equals(frascatiService.state(fcomponent)));
    }

    @Test
    public void testUnregisterServlet()
    {
        try
        {
            File servletFile = new File(servletPath);
            int n = 0;
            for (; n <= 1; n++)
            {
                String componentServletName = frascatiService.processComposite(
                        "helloworld-servlet.composite", FraSCAtiServiceItf.all,
                        servletFile.toURI().toURL()).getName();
                frascatiService.remove(componentServletName);
            }
        } catch (FraSCAtiServiceException e)
        {
            e.printStackTrace();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testUnregisterWS()
    {
        try
        {
            File wsFile = new File(wsPath);
            int n = 0;
            for (; n <= 1; n++)
            {
                String componentWSName = frascatiService.processComposite(
                        "helloworld-ws-server.composite",
                        FraSCAtiServiceItf.all, wsFile.toURI().toURL()).getName();
                frascatiService.remove(componentWSName);
            }
        } catch (FraSCAtiServiceException e)
        {
            e.printStackTrace();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }


}