/**
 * 
 */
package org.easysoa.registry.frascati;

import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.util.FrascatiException;

/**
 * @author jguillemotte
 *
 */
public class FraSCAtiCompositeApp extends AbstractEasySOAApp {

	/* (non-Javadoc)
	 * @see org.easysoa.registry.frascati.EasySOAApp#start()
	 */
	@Override
	public FraSCAti start() throws FrascatiException {
		frascati = FraSCAti.newFraSCAti();
		return frascati;
	}

	/* (non-Javadoc)
	 * @see org.easysoa.registry.frascati.EasySOAApp#stop()
	 */	
	@Override
	public void stop() throws FrascatiException {
		// TODO Auto-generated method stub
	}
}
