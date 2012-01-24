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
import org.eclipse.stp.sca.Composite;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.frascati.NuxeoFraSCAtiException;
import org.nuxeo.frascati.api.AbstractProcessingContext;
import org.nuxeo.frascati.api.ProcessingModeProxy;
import org.nuxeo.frascati.test.FraSCAtiFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * Tests SCA read with FraSCAti
 * @author mdutoo
 *
 */
@RunWith(FeaturesRunner.class)
@Features({FraSCAtiFeature.class})
public class FraSCAtiServiceReadScaTest {
	
    static final Log log = LogFactory.getLog(FraSCAtiServiceReadScaTest.class);
       
    @Inject NxFraSCAtiRegistryService frascatiRegistryService;
        
    /** checking that FraSCAti parsing-based import of SCA ref'ing unknown class
     * fails without custom ProcessingContext.loadClass() */
    @Test
    @Ignore
    public void testReadSCACompositeFailsOnClassNotFound() throws Exception {
    	// SCA composite file to import :
    	String scaFilePath = "src/test/resources/" + "org/easysoa/sca/RestSoapProxy.composite";
    	File scaFile = new File(scaFilePath);
    	Composite composite = null;
    	try{
	    	composite = frascatiRegistryService.readComposite(scaFile.toURI().toURL());	
			int serviceNb = composite.getService().size();			
			Assert.assertTrue(serviceNb > 0);	
    	} catch (Exception e){
    		e.printStackTrace();
    	}		
		// reading only until unfound class (then ParserException in AssemblyFactoryManager.processComposite())
		// therefore not all services read :
		Composite crippledComposite = readComposite(scaFile.toURI().toURL(),
				frascatiRegistryService.newParsingProcessingContext());
		
		Assert.assertTrue(crippledComposite == null);
    }


    /** Rather here than in FraSCAtiService because only public for test purpose */
    public Composite readComposite(URL compositeUrl, AbstractProcessingContext processingContext) 
    		throws Exception {
  	  if (processingContext.getRootComposite() != null) {
  		  throw new Exception("Unlawful reuse of processingContext : already has a root composite "
  				  + processingContext.getRootComposite().getName());
  	  }
      // Only parse and check the SCA composite, i.e., don't generate code for the SCA composite and don't instantiate it.
      processingContext.setProcessingMode(ProcessingModeProxy.check); // else composite fails to start because ref'd WSDLs are unavailable      
      try {
    	  
        // Process the SCA composite.
    	frascatiRegistryService.getFraSCAti().processComposite(compositeUrl.toString(),
    			processingContext);   
    	
      } catch(NuxeoFraSCAtiException fe) {
      	System.err.println("The number of checking errors is equals to " + processingContext.getErrors());
      	//for (error : processingContext.getData(key, type)) {
      	//	
      	//}
    	System.err.println("The number of checking warnings is equals to " + processingContext.getWarnings());
      }
      Composite parsedComposite = processingContext.getRootComposite();
      // resetting for next parsing :
  	  processingContext.setRootComposite(null);
  	  
      return parsedComposite;
    }
    
}
