/**
 * 
 */
package org.easysoa.frascati.remote.test;

import org.easysoa.sca.frascati.RemoteFraSCAtiServiceProvider;
import org.easysoa.sca.frascati.test.RemoteFraSCAtiServiceProviderTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author munilla
 * 
 */
public class RemoteFraSCAtiTest extends RemoteFraSCAtiServiceProviderTest
{

    RemoteFraSCAtiServiceProvider provider = null;

    @Before
    public void setUp()
    {
        configure();

        String libDir = System
                .getProperty(RemoteFraSCAtiServiceProvider.REMOTE_FRASCATI_LIBRARIES_BASEDIR);

        log.info(RemoteFraSCAtiServiceProvider.REMOTE_FRASCATI_LIBRARIES_BASEDIR
                + " : " + libDir);

        assertNotNull(libDir);

        try
        {
            provider = new RemoteFraSCAtiServiceProvider(null);

        } catch (Exception e)
        {
            e.printStackTrace();
            log.severe("Enable to create a remote frascati provider");
        }
        assertNotNull(provider);
    }

    @Test
    public void testTheProvider()
    {
        assertNotNull(provider.getFraSCAtiService());
    }

}
