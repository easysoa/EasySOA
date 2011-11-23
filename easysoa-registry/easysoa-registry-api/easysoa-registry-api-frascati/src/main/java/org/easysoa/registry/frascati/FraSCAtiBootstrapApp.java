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
