/**
 * 
 */
package org.easysoa.registry.frascati;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.util.FrascatiException;

/**
 * @author jguillemotte
 *
 */
public class FraSCAtiBootstrapApp extends AbstractEasySOAApp {

	@SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(FraSCAtiBootstrapApp.class);
	
	/* (non-Javadoc)
	 * @see org.easysoa.registry.frascati.EasySOAApp#start()
	 */
	@Override
	public FraSCAti start() throws FrascatiException {
		// Test to launch Web explorer : DOESN'T WORK
		// There is a problem with duplicate frascati.composite file in both web explorer module and runtime factory module
		/*URL compositeUrl = ClassLoader.getSystemResource("WebExplorer.composite");			
		try{
			// Loading Web explorer composite
			frascati.processComposite(compositeUrl.toString(), new ProcessingContextImpl());
		}
		catch(Exception ex){
			ex.printStackTrace();
			log.debug(ex);
		}*/
		
		// save normal bootstrap
		String normalBootstrap = System.getProperty("org.ow2.frascati.bootstrap");
		
    	// Set system property for Frascati web explorer, only this line is necessary to start the web explorer
		System.setProperty("org.ow2.frascati.bootstrap", "org.ow2.frascati.bootstrap.FraSCAtiWebExplorer");		
		
		// Instantiate web explorer-specific OW2 FraSCAti
		frascati = FraSCAti.newFraSCAti();
		
		// set it back
		if(normalBootstrap != null){
			System.setProperty("org.ow2.frascati.bootstrap", normalBootstrap);
		}	
		return frascati;
	}

	/* (non-Javadoc)
	 * @see org.easysoa.registry.frascati.EasySOAApp#stop()
	 */
	@Override
	public void stop() throws FrascatiException {
		//webExplorerFrascati.setClassLoader(classloader) // TODO try for tests
		stopComponents(frascati.getCompositeManager().getComposites());
		stopComponents(frascati.getCompositeManager().getTopLevelDomainComposite()); // would be enough on itself ??
	}

}
