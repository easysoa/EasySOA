/**
 * EasySOA: OW2 FraSCAti in Nuxeo
 * Copyright (C) 2011 INRIA, University of Lille 1
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * Contact: frascati@ow2.org
 *
 * Author: Philippe Merle
 *
 * Contributor(s):
 *
 */
package org.easysoa.frascati.nuxeo;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.stp.sca.Composite;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.DefaultComponent;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.api.ProcessingMode;
import org.ow2.frascati.util.FrascatiException;


/**
 * 
 * @author pmerle, mdutoo
 * 
 * TODO solve maven deps :
 			<groupId>org.eclipse.jdt</groupId>
			<artifactId>core</artifactId>
			<version>3.3.0.771</version>
 			<!-- TODO Eclipse m2e says : Overriding managed version 3.1.1-NXP-4284 for core ?!! -->
 *
 */
public class FraSCAtiService extends DefaultComponent
{
    public static final ComponentName NAME = new ComponentName(
    		"org.easysoa.registry.frascati.FraSCAtiServiceComponent");

	private static Log log = LogFactory.getLog(FraSCAtiService.class);

	private FraSCAti frascati;

	public FraSCAtiService() throws FrascatiException
	{
		// Instantiate OW2 FraSCAti.
		frascati = FraSCAti.newFraSCAti();
	}

	/**
	 * Get an SCA composite.
	 * @param composite the composite to get.
	 * @return the composite.
	 */
	public Object getComposite(String composite) throws FrascatiException
	{
		return frascati.getComposite(composite);
	}
	
	/**
	 * Added for convenience in a first time.
	 * TODO To improve usability and modularity, calls to its methods should be replaced
	 * by calls to FraSCAtiService methods calling them.
	 * @return
	 * @throws FrascatiException
	 */
	public FraSCAti getFraSCAti()
	{
		return frascati;
	}

	

	////////////////////////////////////////////////
	// parsing methods
	// TODO rather move them in a parsing service, and extract calls to FraSCAti
	// as new methods of FraSCAti service ?

	  
	  public ParsingProcessingContext newParsingProcessingContext(URL ... urls) throws FrascatiException {
		  return new ParsingProcessingContext(frascati.getCompositeManager().newProcessingContext(urls));
	  }

  public Composite readComposite(URL compositeUrl, URL ... scaZipUrls) throws Exception
  {
    // Create a processing context with where to find ref'd classes
	///ProcessingContext processingContext = frascati.getCompositeManager().newProcessingContext(scaZipUrls);
	  ParsingProcessingContext processingContext = this.newParsingProcessingContext(scaZipUrls);
	    
    // Only parse and check the SCA composite, i.e., don't generate code for the SCA composite and don't instantiate it.
    processingContext.setProcessingMode(ProcessingMode.check); // else composite fails to start because ref'd WSDLs are unavailable
    
    try {
      // Process the SCA composite.
      frascati.processComposite(compositeUrl.toString(), processingContext);
      
      // Return the Eclipse STP SCA Composite.
      return processingContext.getRootComposite();
    } catch(FrascatiException fe) {
    	System.err.println("The number of checking errors is equals to " + processingContext.getErrors());
    	//for (error : processingContext.getData(key, type)) {
    	//	
    	//}
  	System.err.println("The number of checking warnings is equals to " + processingContext.getWarnings());
    }
    
    // TODO feed parsing errors / warnings up to UI ?!
    log.warn("\nErrors while parsing " + compositeUrl + ":\n" + processingContext.getErrorMessages());
    log.info("\nWarnings while parsing " + compositeUrl + ":\n" + processingContext.getWarningMessages());
    return null;
  }
  
  /**
   * Reads a zip (or jar), parses and returns the composites within.
   * Classes they references are resolved within the zip.
   * Only known (i.e. in the classpath / maven) extensions can be parsed (impls, bindings...).
   * 
   * TODO pbs : processContribution() and processComposite() return null in check mode,
   * so has to use a separate ProcessingContext for each read (and alter each one's classloader)
   * @param scaZipFile
   * @return
   */
  public Set<Composite> readScaZip(File scaZipFile) throws Exception {
	  // NB. can't use processContribution() because in check mode returns nothing
	  // and puts in the context  only the single root composite
	  
		URL scaZipFileUrl = scaZipFile.toURI().toURL();

	    // if sca zip : (still fails to load sub composite, i.e. intent, because looks for it at .)
		Set<URL> scaZipCompositeURLSet = FileUtils.unzipAndGetFileUrls(scaZipFile);
		Set<Composite> scaZipCompositeSet = new HashSet<Composite>();
		for (URL scaZipCompositeURL : scaZipCompositeURLSet) {
	          if (scaZipCompositeURL.getPath().endsWith(".composite")) {
		            log.info("Found composite to parse " + scaZipCompositeURL);
					Composite scaComposite = readComposite(scaZipCompositeURL, scaZipFileUrl);
					if (scaComposite != null) {
						scaZipCompositeSet.add(scaComposite);
					}
		      }
		}
		return scaZipCompositeSet;
  }
}
