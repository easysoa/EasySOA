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

package org.easysoa.sca.frascati;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.stp.sca.Composite;
import org.junit.After;
import org.junit.runner.RunWith;
import org.easysoa.frascati.FraSCAtiServiceException;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.sca.frascati.test.RemoteFraSCAtiServiceProviderTest;
import org.ow2.frascati.util.FrascatiException;

/**
 * 
 * @author jguillemotte
 * 
 */
public class ApiTestHelperBase extends RemoteFraSCAtiServiceProviderTest {

    static final Log log = LogFactory.getLog(ApiTestHelperBase.class);

    /** The FraSCAti platform */
    protected static FraSCAtiServiceItf frascati;

    protected static RemoteFraSCAtiServiceProvider serviceProvider = null;    
    
    protected static ArrayList<String> componentList;

    protected static void startMock() {
        log.info("Services Mock Starting");
        log.info("frascati = " + frascati);
        String compositeName = null;
        try {
            compositeName = frascati.processComposite("src/test/resources/RestApiMock.composite", FraSCAtiServiceItf.all);
            componentList.add(compositeName);
        } catch (FraSCAtiServiceException e) {
            
            e.printStackTrace();
        }
    }

    /**
     * Start FraSCAti
     * 
     * @throws FrascatiException
     */
    /*protected static void startFraSCAti() {
        log.info("FraSCATI Starting");
        componentCompositeNameList = new ArrayList<String>();
        // This test doesn't works, unbale to start FraSCAti in remote mode (not
        // in Nuxeo)
        // Need to make a FraSCAtiServiceProvider for remote mode
        // TODO : do not use Nuxeo Framework in this test to start Frascati
        // TODO : Remove all nuxeo stuff !!
        // frascati =
        // Framework.getLocalService(FraSCAtiServiceProviderItf.class).getFraSCAtiService();
        //log.info("frascati = " + frascati);
        // Use this code instead. PB FraSCAti is not a FraSCAtiServiceItf ....
        // FraSCAti frascati = FraSCAti.newFraSCAti();
         RemoteFraSCAtiServiceProvider remoteProvider = new RemoteFraSCAtiServiceProvider();
         frascati = remoteProvider.getFraSCAtiService();
     }*/
    
    /**
     * Start FraSCAti
     * 
     * @throws Exception
     */
    protected void startFraSCAti() throws Exception {
        if(frascati == null) {
            //char sep = File.separatorChar;
            configure();
            /*          
            StringBuilder srcBuilder = new StringBuilder("target").append(
                    sep).append("test-classes").append(sep).append(
                            "easysoa-proxy-core-httpdiscoveryproxy.jar");
            
            File srcFile = new File(srcBuilder.toString());
            FileUtils.copyFileToDirectory(srcFile.getAbsoluteFile(), remoteFrascatiLibDir);
            
            StringBuilder libBuilder = new StringBuilder(
                    remoteFrascatiLibDir.getAbsolutePath()).append(sep).append(
                         "easysoa-proxy-core-httpdiscoveryproxy.jar");
                        
            lib = new File(libBuilder.toString());
            */
            log.info("FraSCATI Starting");
            componentList = new ArrayList<String>();
            serviceProvider = new RemoteFraSCAtiServiceProvider();
            frascati = serviceProvider.getFraSCAtiService();
        }
    }    
    
     public void stopFraSCAti() {
        for (String componentCompositeName : componentList) {
            try {
                frascati.stop(componentCompositeName);
            } catch (Exception e) {
                log.warn("Error stopping composite " + componentCompositeName);
            }
            try {
                frascati.remove(componentCompositeName);
            } catch (Exception e) {
                log.warn("Error removing composite " + componentCompositeName);
            }
        }
        frascati = null;
    }

}
