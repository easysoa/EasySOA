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


package org.easysoa.sca.frascati;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

public class ApiTestHelperBase {

    static final Log log = LogFactory.getLog(ApiTestHelperBase.class);
	
	/** The FraSCAti platform */
    protected static FraSCAti frascati;

    protected static ArrayList<Component> componentList;    
    
	protected static void startMock() throws FrascatiException {
		log.info("Services Mock Starting");
		componentList.add(frascati.processComposite("src/test/resources/RestApiMock.composite", new ProcessingContextImpl()));
	}
	
	/**
	 * Start FraSCAti
	 * @throws FrascatiException 
	 */
	protected static void startFraSCAti() throws FrascatiException {
		log.info("FraSCATI Starting");
		componentList =  new ArrayList<Component>();
		frascati = FraSCAti.newFraSCAti();
	}
	
}
