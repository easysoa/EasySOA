package org.easysoa.registry.frascati;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.model.DefaultComponent;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * FraSCAti Web Explorer service.
 * This class doesn't works yet, just to keep a trace of the research made to integrate FraSCAti web explorer to Nuxeo
 *
 */
public class FraSCAtiWebExplorerService extends DefaultComponent {

	private static Log log = LogFactory.getLog(FraSCAtiWebExplorerService.class);

	private FraSCAti frascati;

	public FraSCAtiWebExplorerService() throws FrascatiException {
		// Instantiate OW2 FraSCAti.
		frascati = FraSCAti.newFraSCAti();
		/*
		URL resource = getClass().getClassLoader().getResource("WebExplorer.composite");
		System.out.println("Resource : " + resource);
		resource = InvokerImpl.class.getClassLoader().getResource("WebExplorer.composite");
		System.out.println("Resource : " + resource);
		resource = getClass().getResource("/WebExplorer.composite");
		System.out.println("Resource : " + resource);
		resource = InvokerImpl.class.getResource("/WebExplorer.composite");
		System.out.println("Resource : " + resource);
		*/
		URL compositeUrl = ClassLoader.getSystemResource("WebExplorer.composite");			
		System.out.println("CompositeUrl : " + compositeUrl);
		try{
			// Loading Web explorer composite
			//frascati.getComposite("WebExplorer.composite");
			frascati.processComposite(compositeUrl.toString(), new ProcessingContextImpl());
		}
		catch(Exception ex){
			ex.printStackTrace();
			log.debug(ex);
		}
		
	}
}
