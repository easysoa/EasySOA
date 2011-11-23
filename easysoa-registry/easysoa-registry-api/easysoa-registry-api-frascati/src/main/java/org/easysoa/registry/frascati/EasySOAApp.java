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

import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.util.FrascatiException;

public interface EasySOAApp {

	/**
	 * Start the EasySOA app
	 */
	public FraSCAti start() throws FrascatiException;

	/**
	 * Stop the EasySOA app
	 */	
	public void stop() throws FrascatiException;
	
	/**
	 * Returns the FraSCAti instance
	 * @return The FraSCAti instance
	 */
	public FraSCAti getFrascati();
	
}
