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

/**
 * @author jguillemotte
 *
 */
public class FraSCAtiCompositeApp extends AbstractEasySOAApp {

	// TODO : Problem here : used by FrascatiService et FrascatiServiceBase, all of them try to start a new Frascati instance
	private static FraSCAtiCompositeApp frascatiCompositeApp;
	
	private FraSCAtiCompositeApp(){}
	
	public static final FraSCAtiCompositeApp getInstance(){
		if(frascatiCompositeApp == null){
			frascatiCompositeApp = new FraSCAtiCompositeApp(); 
		}
		return frascatiCompositeApp;
	}
	
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
