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
		log.debug("Closing components");
		for(Component component : components){
			frascati.close(component);
		}
	}

}
