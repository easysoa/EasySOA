package org.easysoa.registry.frascati;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.stp.sca.Composite;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.api.ProcessingMode;
import org.ow2.frascati.util.FrascatiException;

public class FraSCAtiServiceBase implements FraSCAtiServiceItf {

	private static Log log = LogFactory.getLog(FraSCAtiServiceBase.class);

	private EasySOAApp easySOAApp;

	public FraSCAtiServiceBase() throws FrascatiException {
		// Instantiate OW2 FraSCAti.
		//easySOAApp = new FraSCAtiBootstrapApp();
		easySOAApp = new FraSCAtiCompositeApp();
		easySOAApp.start();
	}
	
	/* (non-Javadoc)
	 * @see org.easysoa.registry.frascati.FraSCAtiServiceItf#startScaApp(java.net.URL)
	 */
	@Override
	public Component[] startScaApp(URL scaAppUrl) throws FrascatiException {
		// TODO : change the processing context to discovery processing context
		ParsingProcessingContext processingContext = this.newParsingProcessingContext(scaAppUrl);
		//DiscoveryProcessingContext processingContext = this.newDiscoveryProcessingContext(scaAppUrl);
		return easySOAApp.getFrascati().getCompositeManager().processContribution(scaAppUrl.toString(), processingContext);
	}

	/* (non-Javadoc)
	 * @see org.easysoa.registry.frascati.FraSCAtiServiceItf#getComposite(java.lang.String)
	 */
	@Override
	public Object getComposite(String composite) throws FrascatiException {
		return easySOAApp.getFrascati().getComposite(composite);
	}

	/* (non-Javadoc)
	 * @see org.easysoa.registry.frascati.FraSCAtiServiceItf#getFraSCAti()
	 */
	@Override
	public FraSCAti getFraSCAti() {
		return easySOAApp.getFrascati();
	}

	// //////////////////////////////////////////////
	// parsing methods
	// TODO rather move them in a parsing service, and extract calls to FraSCAti
	// as new methods of FraSCAti service ?

	/* (non-Javadoc)
	 * @see org.easysoa.registry.frascati.FraSCAtiServiceItf#newParsingProcessingContext(java.net.URL)
	 */
	@Override
	public ParsingProcessingContext newParsingProcessingContext(URL... urls) throws FrascatiException {
		return new ParsingProcessingContext(easySOAApp.getFrascati().getCompositeManager().newProcessingContext(urls));
	}

	/* (non-Javadoc)
	 * @see org.easysoa.registry.frascati.FraSCAtiServiceItf#newDiscoveryProcessingContext(java.net.URL)
	 */
	@Override
	public DiscoveryProcessingContext newDiscoveryProcessingContext(URL... urls) throws FrascatiException {
		// add a parameter to pass the importer
		return new DiscoveryProcessingContext(easySOAApp.getFrascati().getCompositeManager().newProcessingContext(urls));
	}
	
	/* (non-Javadoc)
	 * @see org.easysoa.registry.frascati.FraSCAtiServiceItf#readComposite(java.net.URL, java.net.URL)
	 */
	@Override
	public Composite readComposite(URL compositeUrl, URL... scaZipUrls)	throws Exception {
		// Create a processing context with where to find ref'd classes
		//ProcessingContext processingContext = frascati.getCompositeManager().newProcessingContext(scaZipUrls);
		log.debug("composite URL = " + compositeUrl);
		log.debug("scaZipUrls = " + scaZipUrls);
		// TODO : if we have a standalone composite file, do not instanciate and start the composite
		// TODO : change the processing context to discovery processing context
		
		ParsingProcessingContext processingContext = this.newParsingProcessingContext(scaZipUrls);
		//DiscoveryProcessingContext processingContext = this.newDiscoveryProcessingContext(scaZipUrls);
		
		// Only parse and check the SCA composite, i.e., don't generate code for
		// the SCA composite and don't instantiate it.
		//processingContext.setProcessingMode(ProcessingMode.check); // else composite fails to start because ref'd WSDLs are unavailable
		processingContext.setProcessingMode(ProcessingMode.generate);

		// TODO : Solve problem here ...
		// Problem with this mode : class not found exceptions when a single composite is loaded
		//processingContext.setProcessingMode(ProcessingMode.compile);
		
		// Generate Juliac class generation and compilation errors 
		//processingContext.setProcessingMode(ProcessingMode.instantiate);
		//processingContext.setProcessingMode(ProcessingMode.start);
		
		try {
			// Process the SCA composite.
			easySOAApp.getFrascati().processComposite(compositeUrl.toString(), processingContext);

			// Return the Eclipse STP SCA Composite.
			return processingContext.getRootComposite();
		} catch (FrascatiException fe) {
			//System.err.println("The number of checking errors is equals to " + processingContext.getErrors());
			log.error("The number of checking errors is equals to " + processingContext.getErrors());
			// for (error : processingContext.getData(key, type)) {
			//
			// }
			//System.err.println("The number of checking warnings is equals to " + processingContext.getWarnings());
			log.error("The number of checking warnings is equals to " + processingContext.getWarnings());
			log.error(fe);			
		}

		// TODO feed parsing errors / warnings up to UI ?!
		log.warn("\nErrors while parsing " + compositeUrl + ":\n" + processingContext.getErrorMessages());
		log.info("\nWarnings while parsing " + compositeUrl + ":\n" + processingContext.getWarningMessages());
		return null;
	}

	/* (non-Javadoc)
	 * @see org.easysoa.registry.frascati.FraSCAtiServiceItf#readScaZip(java.io.File)
	 */
	@Override
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
