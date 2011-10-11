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

package org.openwide.easysoa.test;

import java.util.ArrayList;

import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

public abstract class AbstractTest {

	/** The FraSCAti platform */
    protected static FraSCAti frascati;
    
    protected static ArrayList<Component> componentList;
	
    /** Set system properties for FraSCAti */
	static {
		System.setProperty("org.ow2.frascati.bootstrap", "org.ow2.frascati.bootstrap.FraSCAti");
	}    
    
	/**
	 * Start FraSCAti
	 * @throws FrascatiException 
	 */
	protected static void startFraSCAti() throws FrascatiException{
		componentList =  new ArrayList<Component>();
		frascati = FraSCAti.newFraSCAti();
	}
	
	/**
	 * 
	 * @throws FrascatiException
	 */
	protected static void stopFraSCAti() throws FrascatiException{
		if(componentList != null){
			for(Component component : componentList){
				frascati.close(component);
			}
		}
	}
	
	/**
	 * Start Velocity Test
	 * @throws FrascatiException
	 */
	protected static void startScaffoldingProxyComposite() throws FrascatiException{
		componentList.add(frascati.processComposite("src/test/resources/scaffoldingProxy.composite", new ProcessingContextImpl()));
	}

	/**
	 * Start Soap service mock
	 * @throws FrascatiException
	 */
	protected static void startSoapServiceMockComposite() throws FrascatiException{
		componentList.add(frascati.processComposite("src/test/resources/soapServiceMock.composite", new ProcessingContextImpl()));
	}
	
}
