/**
 * 
 */
package org.easysoa.registry.frascati;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.util.FrascatiException;

/**
 * @author jguillemotte
 *
 */
public abstract class AbstractEasySOAApp implements EasySOAApp {

	private static Log log = LogFactory.getLog(AbstractEasySOAApp.class);	
	
	protected FraSCAti frascati;
	
	@Override
	public FraSCAti getFrascati(){
		return frascati;
	}
	
	/**
	 * 
	 * @param components
	 * @throws FrascatiException
	 */
	public void stopComponents(Component... components) throws FrascatiException {
		for(Component component : components){
			frascati.close(component);
		}
	}

}
