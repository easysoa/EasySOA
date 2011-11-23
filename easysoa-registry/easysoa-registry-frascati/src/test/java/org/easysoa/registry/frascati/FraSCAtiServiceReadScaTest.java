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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.stp.sca.Composite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.ow2.frascati.assembly.factory.api.ProcessingContext;
import org.ow2.frascati.assembly.factory.api.ProcessingMode;
import org.ow2.frascati.util.FrascatiException;
import com.google.inject.Inject;

/**
 * Tests SCA read with FraSCAti
 * @author mdutoo
 *
 */

@RunWith(FeaturesRunner.class)
public class FraSCAtiServiceReadScaTest {

    static final Log log = LogFactory.getLog(FraSCAtiServiceReadScaTest.class);
    
    @Inject NxFraSCAtiService frascatiService;
    
    @Before
    public void setUp() throws ClientException, MalformedURLException {
    	// FraSCAti
  	  	assertNotNull("Cannot get FraSCAti service component", frascatiService);
    }
    
    /** checking that FraSCAti parsing-based import of SCA ref'ing unknown class
     * fails without custom ProcessingContext.loadClass() */
    @Test
    public void testReadSCACompositeFailsOnClassNotFound() throws Exception {
    	// SCA composite file to import :
    	String scaFilePath = "src/test/resources/" + "org/easysoa/sca/RestSoapProxy.composite";
    	File scaFile = new File(scaFilePath);
	
    	// reading all services :
		Composite composite = frascatiService.readComposite(scaFile.toURI().toURL());
		int serviceNb = composite.getService().size();
		Assert.assertTrue(serviceNb > 0);
		
		// reading only until unfound class (then ParserException in AssemblyFactoryManager.processComposite())
		// therefore not all services read :
		Composite crippledComposite = this.readComposite(scaFile.toURI().toURL(), frascatiService.getFraSCAti().getCompositeManager().newProcessingContext());
		Assert.assertTrue(crippledComposite == null);
    }


    /** Rather here than in FraSCAtiService because only public for test purpose */
    public Composite readComposite(URL compositeUrl, ProcessingContext processingContext) throws Exception {
  	  if (processingContext.getRootComposite() != null) {
  		  throw new Exception("Unlawful reuse of processingContext : already has a root composite "
  				  + processingContext.getRootComposite().getName());
  	  }
      // Only parse and check the SCA composite, i.e., don't generate code for the SCA composite and don't instantiate it.
      processingContext.setProcessingMode(ProcessingMode.check); // else composite fails to start because ref'd WSDLs are unavailable
      
      try {
        // Process the SCA composite.
    	  frascatiService.getFraSCAti().processComposite(compositeUrl.toString(), processingContext);
        
        // Return the Eclipse STP SCA Composite.
        return processingContext.getRootComposite();
      } catch(FrascatiException fe) {
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
