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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.stp.sca.Composite;
import org.nuxeo.frascati.NuxeoFraSCAtiException;
import org.nuxeo.frascati.api.FraSCAtiServiceItf;
import org.nuxeo.frascati.api.ProcessingModeProxy;

/**
 * TODO pb : now wrongly depends on Nuxeo through FraSCAtiServiceItf
 * 
 * @author jguillemotte
 *
 */
public abstract class FraSCAtiRegistryServiceBase implements FraSCAtiRegistryServiceItf {

	private static Log log = LogFactory.getLog(FraSCAtiRegistryServiceBase.class);
	
	protected FraSCAtiServiceItf frascati; // TODO make it independent from nuxeo by reimplementing it also directly on top of FraSCAti

	public FraSCAtiRegistryServiceBase() {
		// Instantiate OW2 FraSCAti.
		/*easySOAApp = new FraSCAtiBootstrapApp();
		easySOAApp.start();*/

		// For test only
		// Start the HttpDiscoveryProxy in Nuxeo with embedded FraSCAti
		// does not works because NuxeoFrascati is not yet instanced ....
		/*
		log.debug("Trying to load Http discovery proxy !");
		System.out.println("Trying to load Http discovery proxy !");
		try {
			if(easySOAApp.getFrascati() != null){
				//easySOAApp.getFrascati().processComposite("httpDiscoveryProxy");
				easySOAApp.getFrascati().processComposite("scaffoldingProxy");
			} else {
				log.debug("Unable to get FraSCAti, null returned !");
				System.out.println("Unable to get FraSCAti, null returned !");
			}
		} catch (NuxeoFraSCAtiException ex) {
			// TODO Auto-generated catch block
			log.debug("Error catched when trying to load Http discovery proxy !", ex);
			System.out.println("Error catched when trying to load Http discovery proxy : " + ex.getMessage());
		}*/
	}

	/**
	 * Get an SCA composite.
	 * 
	 * @param composite the composite to get.
	 * @return the composite.
	 */
	public Object getComposite(String composite) throws NuxeoFraSCAtiException {
		return frascati.getComposite(composite);
	}

	/**
	 * Added for convenience in a first time. TODO To improve usability and
	 * modularity, calls to its methods should be replaced by calls to
	 * FraSCAtiService methods calling them.
	 * 
	 * @return
	 */
	public FraSCAtiServiceItf getFraSCAti() {
		return frascati;
	}

	// //////////////////////////////////////////////
	// parsing methods
	// TODO rather move them in a parsing service, and extract calls to FraSCAti
	// as new methods of FraSCAti service ?

	/**
	 * 
	 */
	public ParsingProcessingContext newParsingProcessingContext(URL... urls) throws NuxeoFraSCAtiException {
		return new ParsingProcessingContext(frascati.newProcessingContext(urls));
	}

	/**
	 * 
	 * @param urls
	 * @return
	 * @throws Exception 
	 */
	public DiscoveryProcessingContext newDiscoveryProcessingContext(URL... urls) throws Exception  {
		// add a parameter to pass the importer
		FraSCAtiRuntimeScaImporterItf runtimeScaImporter = newRuntimeScaImporter();
		return new DiscoveryProcessingContext(this, runtimeScaImporter, urls);
	}
	
	/**
	 * 
	 * @param compositeUrl
	 * @param scaZipUrls
	 * @return
	 * @throws Exception
	 */
	public Composite readComposite(URL compositeUrl, URL... scaZipUrls)	throws Exception {
		// Create a processing context with where to find ref'd classes
		log.debug("composite URL = " + compositeUrl);
		log.debug("scaZipUrls = " + scaZipUrls);
		// TODO : if we have a standalone composite file, do not instanciate and start the composite
		// TODO : change the processing context to discovery processing context
		
		//ParsingProcessingContext processingContext = this.newParsingProcessingContext(scaZipUrls);
		DiscoveryProcessingContext processingContext = this.newDiscoveryProcessingContext(scaZipUrls);
		
		// Only parse and check the SCA composite, i.e., don't generate code for
		// the SCA composite and don't instantiate it.
		processingContext.setProcessingMode(ProcessingModeProxy.check); // else composite fails to start because ref'd WSDLs are unavailable
		//processingContext.setProcessingMode(ProcessingModeProxy.parse);

		// TODO : Solve problem here ...
		// Problem with this mode : class not found exceptions when a single composite is loaded
		//processingContext.setProcessingMode(ProcessingMode.compile);
		
		// Generate Juliac class generation and compilation errors
		//processingContext.setProcessingMode(ProcessingMode.instantiate);
		//processingContext.setProcessingMode(ProcessingMode.start);
		
		try {
			// Register first => registering fails
			// Process next
			// Process the SCA composite.
			frascati.processComposite(compositeUrl.toString(), processingContext);
		} 
		catch (NuxeoFraSCAtiException fe) {
			log.error("The number of checking errors is equals to " + processingContext.getErrors());
			log.error("The number of checking warnings is equals to " + processingContext.getWarnings());
			log.error(fe);	
		}
		// TODO feed parsing errors / warnings up to UI ?!
		log.warn("\nErrors while parsing " + compositeUrl + ":\n" + processingContext.getErrorMessages());
		log.info("\nWarnings while parsing " + compositeUrl + ":\n" + processingContext.getWarningMessages());
		
		Composite composite = processingContext.getRootComposite();
		
		if(composite == null){
			throw new FraSCAtiRegistryException("Composite '" + compositeUrl + "' can not be loaded");
		}
		return composite;
	}

	/**
	 * Reads a zip (or jar), parses and returns the composites within. Classes
	 * they references are resolved within the zip. Only known (i.e. in the
	 * classpath / maven) extensions can be parsed (impls, bindings...).
	 * 
	 * TODO pbs : processContribution() and processComposite() return null in
	 * check mode, so has to use a separate ProcessingContext for each read (and
	 * alter each one's classloader)
	 * 
	 * @param scaZipFile
	 * @return
	 */
	public Set<Composite> readScaZip(File scaZipFile) throws Exception {
		// NB. can't use processContribution() because in check mode returns
		// nothing
		// and puts in the context only the single root composite

		URL scaZipFileUrl = scaZipFile.toURI().toURL();

		// if sca zip : (still fails to load sub composite, i.e. intent, because
		// looks for it at .)
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
