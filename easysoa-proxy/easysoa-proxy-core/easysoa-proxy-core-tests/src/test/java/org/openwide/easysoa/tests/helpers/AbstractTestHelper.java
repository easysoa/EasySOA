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
 * Contact : easysoa-dev@googlegroups.com
 */

package org.openwide.easysoa.tests.helpers;

import java.util.ArrayList;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * Abstract Test Helper. Launch FraSCAti, the HTTP Discovery Proxy and the scaffolder proxy.
 */
public abstract class AbstractTestHelper {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(AbstractTestHelper.class.getName());;   

	/** The FraSCAti platform */
    protected static FraSCAti frascati;

    protected static ArrayList<Component> componentList;
    
	static {
		System.setProperty("org.ow2.frascati.bootstrap", "org.ow2.frascati.bootstrap.FraSCAti");
	}
	
	/**
	 * Start FraSCAti
	 * @throws FrascatiException 
	 */
	protected static void startFraSCAti() throws FrascatiException {
		logger.info("FraSCATI Starting");
		componentList =  new ArrayList<Component>();
		frascati = FraSCAti.newFraSCAti();
	}
	
	/**
	 * 
	 * @throws FrascatiException
	 */
	protected static void stopFraSCAti() throws FrascatiException{
		logger.info("FraSCATI Stopping");
		if(componentList != null){
			for(Component component : componentList){
				frascati.close(component);
			}
		}
	}
	
	/**
	 * Start HTTP Proxy
	 * @param composite The Http Discovery proxy composite to load 
	 * @throws FrascatiException
	 */
	protected static void startHttpDiscoveryProxy(String composite) throws FrascatiException {
		logger.info("HTTP Discovery Proxy Starting");
		componentList.add(frascati.processComposite(composite,new ProcessingContextImpl()));
	}
	
	/**
	 * Start Scaffolder proxy
	 * @param composite The Scaffolder proxy composite to load
	 * @throws FrascatiException
	 */
	protected static void startScaffolderProxy(String composite) throws FrascatiException {
		logger.info("Scaffolder Proxy Starting");
		componentList.add(frascati.processComposite(composite, new ProcessingContextImpl()));
	}
	
	/**
	 * Start the services mock for tests (Meteo mock, twitter mock ...)
	 * @param withNuxeoMock If true, the Nuxeo mock is started
	 * @throws Exception 
	 */
	protected static void startMockServices(boolean withNuxeoMock) throws Exception {
		logger.info("Services Mock Starting");
		componentList.add(frascati.processComposite("twitterMockRest.composite", new ProcessingContextImpl()));
		componentList.add(frascati.processComposite("meteoMockSoap.composite", new ProcessingContextImpl()));
		// start Nuxeo mock
		if(withNuxeoMock){
			componentList.add(frascati.processComposite("nuxeoMockRest.composite", new ProcessingContextImpl()));
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void startNewRun(String runName) throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		// Start a new Run
		HttpPost newRunPostRequest = new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/start/" + runName);
		httpClient.execute(newRunPostRequest, new BasicResponseHandler());		
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void stopAndSaveRun() throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		// Stop and save the run
		HttpPost stopRunPostRequest = new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/stop");
		httpClient.execute(stopRunPostRequest, new BasicResponseHandler());

	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void deleteRun() throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		// delete the run
		HttpPost deleteRunPostRequest = new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/delete");
		httpClient.execute(deleteRunPostRequest, new BasicResponseHandler());		
	}	
	
}
