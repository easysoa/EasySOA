/**
 * EasySOA Proxy
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
 * Contact : easysoa-dev@groups.google.com
 */

package com.openwide.easysoa.proxy;

import org.apache.log4j.PropertyConfigurator;

/**
 * Singleton for logger configuration
 * @author jguillemotte
 *
 */
public class ProxyConfigurator {
	
	private static boolean configuratorAlreadyCalled = false;

	/**
	 * Call only one time
	 * Configuration of logger
	 */
	public static void configure(){
		if(!configuratorAlreadyCalled){
			PropertyConfigurator.configure(HttpDiscoveryProxy.class.getClassLoader().getResource("log4j.properties"));
			configuratorAlreadyCalled = true;
		}
	}

}
