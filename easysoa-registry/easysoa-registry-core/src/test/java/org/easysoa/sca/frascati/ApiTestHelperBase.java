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

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.frascati.FraSCAtiServiceException;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.registry.frascati.EasySOAApiFraSCAti;
import org.easysoa.sca.frascati.test.RemoteFraSCAtiServiceProviderTest;

/**
 * 
 * @author jguillemotte
 * 
 */
public class ApiTestHelperBase extends RemoteFraSCAtiServiceProviderTest {

    static final Log log = LogFactory.getLog(ApiTestHelperBase.class);

    /** The FraSCAti platform */
    protected static FraSCAtiServiceItf frascati = null;   
    protected static EasySOAApiFraSCAti easy = null;
    protected static ArrayList<String> componentList = null;

    protected static void startMock() {
        
        log.info("Services Mock Starting");
        log.info("frascati = " + frascati);
        
        String compositeName = null;
        
        try {
            
            compositeName = frascati.processComposite(
                    "src/test/resources/RestApiMock.composite", FraSCAtiServiceItf.all);
            componentList.add(compositeName);
            
        } catch (FraSCAtiServiceException e) {
            
            e.printStackTrace();
        }
    }
    
    /**
     * Start FraSCAti
     * 
     * @throws Exception
     */
    protected void startFraSCAti() throws Exception {
        
        if(easy == null) {
            //char sep = File.separatorChar;
            configure();
            log.info("FraSCATI Starting");
            componentList = new ArrayList<String>();
            easy = EasySOAApiFraSCAti.getInstance();            
            frascati = easy.getFraSCAti();
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
        
        EasySOAApiFraSCAti.killInstance();
        easy = null;
        frascati = null;
    }

}
