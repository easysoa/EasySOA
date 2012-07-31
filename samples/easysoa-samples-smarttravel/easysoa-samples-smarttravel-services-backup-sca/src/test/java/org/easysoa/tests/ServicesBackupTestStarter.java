/**
 * EasySOA Samples - Smart Travel
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

package org.easysoa.tests;

import java.io.IOException;

import javax.xml.soap.SOAPException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;
import org.ow2.frascati.FraSCAti;

/**
 * Used to test issue #23 FraSCAti can't mock a specific WSDL (because no JAXWS annotations support)
 * https://github.com/easysoa/easysoa-model-demo/issues/23 .
 * 
 * @author mdutoo
 *
 */
public class ServicesBackupTestStarter {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(ServicesBackupTestStarter.class.getClass());	

	/** The FraSCAti platform */
    private static FraSCAti frascati;
    
	/**
	 * Init the remote systems for the test
	 * Frascati and HTTP Proxy
	 * Instantiate FraSCAti and retrieve services.
	 * @throws InterruptedException 
	 */
	@BeforeClass
	public static void setUp() throws FrascatiException, InterruptedException {
		// Start fraSCAti
		startFraSCAti();
		// Start Scaffolding Proxy test
		startServicesMockComposite();
	}
	
	/**
	 * Wait for an user action to stop the test 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@Test
	public final void testWaitUntilRead() throws Exception{
		logger.info("Services mock started, wait for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
		logger.info("Services mock test stopped !");
	}	
	
	/**
	 * Start FraSCAti
	 * @throws FrascatiException 
	 */
	private static void startFraSCAti() throws FrascatiException{
		frascati = FraSCAti.newFraSCAti();
	}
	
	/**
	 * Start services mock
	 * @throws FrascatiException
	 */
	private static void startServicesMockComposite() throws FrascatiException{
		frascati.processComposite("src/main/resources/services-backup-sca.composite", new ProcessingContextImpl());
	}	

}
