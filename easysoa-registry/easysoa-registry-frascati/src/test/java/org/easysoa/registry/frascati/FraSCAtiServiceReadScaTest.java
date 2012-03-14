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

package org.easysoa.registry.frascati;

import java.io.File;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.frascati.FraSCAtiServiceException;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.eclipse.stp.sca.Composite;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.frascati.test.FraSCAtiFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * Tests SCA read with FraSCAti
 * 
 * @author mdutoo
 * 
 */
@RunWith(FeaturesRunner.class)
@Features({ FraSCAtiFeature.class })
public class FraSCAtiServiceReadScaTest
{

    static final Log log = LogFactory.getLog(FraSCAtiServiceReadScaTest.class);

    @Inject
    NxFraSCAtiRegistryService frascatiRegistryService;

    /**
     * checking that FraSCAti parsing-based import of SCA ref'ing unknown class
     * fails without custom ProcessingContext.loadClass()
     */
    @Test
    public void testReadSCACompositeFailsOnClassNotFound() throws Exception
    {

        // SCA composite file to import :
        String scaFilePath = "src/test/resources/"
                + "org/easysoa/sca/RestSoapProxy.composite";
        File scaFile = new File(scaFilePath);
        String compositeName = null;
        try
        {
            compositeName = frascatiRegistryService.getFraSCAti()
                    .processComposite("RestSoapProxy.composite",
                            FraSCAtiServiceItf.check, scaFile.toURI().toURL());

            Composite composite = frascatiRegistryService.getFraSCAti()
                    .getComposite(compositeName);
            Assert.assertTrue(composite != null);
            int serviceNb = composite.getService().size();
            Assert.assertTrue(serviceNb > 0);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        // reading only until unfound class (then ParserException in
        // AssemblyFactoryManager.processComposite())
        // therefore not all services read :
        Composite crippledComposite = readComposite(scaFile.toURI().toURL(),
                    FraSCAtiServiceItf.all);
        Assert.assertTrue(crippledComposite == null);
    }

    @Test
    public void testReadSCACompositeOK() throws Exception
    {
        // SCA composite file to import :
        String scaFilePath = "src/test/resources/" + "proxy-1.0-SNAPSHOT.jar";
        // File scaFile = new File(scaFilePath);

        String composite = frascatiRegistryService.getFraSCAti()
                .processComposite("RestSoapProxy.composite",
                        FraSCAtiServiceItf.check,
                        new File(scaFilePath).toURI().toURL());

        Assert.assertTrue(composite != null);
        Assert.assertTrue(frascatiRegistryService.getFraSCAti().getComposite(
                composite) != null);
    }

    /** Rather here than in FraSCAtiService because only public for test purpose */
    private Composite readComposite(URL compositeUrl, int mode)
            throws Exception
    {
        String compositeName = null;
        Composite parsedComposite = null;
        try
        {
        // Process the SCA composite.
        compositeName = frascatiRegistryService.getFraSCAti().processComposite(
                compositeUrl.toString(), mode);
        
        parsedComposite = frascatiRegistryService.getFraSCAti()
                    .getComposite(compositeName);

        } catch (FraSCAtiServiceException e)
        {
            
        }
        return parsedComposite;
    }

}
