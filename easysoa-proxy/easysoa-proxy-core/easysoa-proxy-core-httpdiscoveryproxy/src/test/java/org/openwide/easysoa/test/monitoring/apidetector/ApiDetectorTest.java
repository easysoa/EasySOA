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

package org.openwide.easysoa.test.monitoring.apidetector;

import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Assume;

import com.sun.jersey.api.client.ClientHandlerException;

/**
 * Unit test for simple App.
 * @Deprecated : Use MockedHttpDiscoveryProxyTest instead
 */
@Deprecated
public class ApiDetectorTest extends TestCase {
	
	public final static String SOA_MODEL_TYPE_APPLIIMPL = "appliimpl";
	public final static String SOA_MODEL_TYPE_API = "api";
	public final static String SOA_MODEL_TYPE_SERVICE = "service";
	//public final static String SOA_MODEL_TYPE_RESOURCE = "resource";
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(getInvokingClassName());
	
    /**
     * Create the test case
     * @param testName name of the test case
     */
    public ApiDetectorTest(String testName){
        super(testName);
    }
    
    /**
     * 
     * @return
     */
    public static String getInvokingClassName() {
    	return Thread.currentThread().getStackTrace()[1].getClassName();
    }

    /**
     * 
     */
    //TODO : add assert to automatically check the result of the test
	public void testUrlDiscoveryMode(){
		// Not a singleton ....
		//DiscoveryMonitoringService.getMonitorService(MonitoringMode.DISCOVERY);
		urlDetectionSimulate(new UrlMock().getTwitterUrlData("api.twitter.com"));
		//urlDetectionSimulate(new UrlMock().getIMediaUrlData());
		try {
			urlDetectionCompute();
			urlDetectionDebugResults();
		}
		catch (ClientHandlerException e) {
			logger.warn("Failed to connect to Nuxeo, aborting test: "+e.getMessage());
			Assume.assumeNoException(e); 
		}

		// JUnit automatic tests
		// Get regsitered data form Nuxeo and compare them to expected data's		
		// Build tree ? or use the MonitoringModel datastructure ?
		//Object registeredInNuxeo = getDataFromNuxeo();
		//Object expectedResult = buildExpectedResult(); 
		//assertEquals(expectedResult, registeredInNuxeo);
	}

	/**
	 * 
	 */
    //TODO : add assert to automatically check the result of the test	
	public void testUrlValidatedMode(){
		// NB. in validation mode, no concept or pre or post run
		// TODO LATER cache it
		try {
			// Not a singleton ...
			//DiscoveryMonitoringService.getMonitorService(MonitoringMode.VALIDATED);
			urlDetectionSimulate(new UrlMock().getTwitterUrlData("api.twitter.com"));
		}
		catch (ClientHandlerException e) {
			logger.warn("Failed to connect to Nuxeo, aborting test: "+e.getMessage());
			Assume.assumeNoException(e);
		}
		
		// TODO LATER mixed mode : do compute and debugResults BUT ONLY on unknown messages ?!
	}
	
	/**
	 * @param arrayList 
	 * 
	 */
	//TODO Stay here ????
	public void urlDetectionSimulate(ArrayList<String> arrayList){
		// simulate rest exchanges
		logger.debug( "Test URL detection start");
		Iterator<String> iter = arrayList.iterator();;
		String urlString;
		// Start the RunManager
		try{
			// Not a singleton ...			
			//RunManagerImpl.getInstance().start("Test run");
			//for(int i=0; i<1000;i++){
			for(int i=0; i<1;i++){
				while(iter.hasNext()){
					urlString = iter.next();
					logger.debug("**** New URL :" + urlString);
					try{
						//URL url = new URL(urlString);
						//Message msg = new Message(url, MessageType.REST);
						// listen message
						// Not a singleton ...
						//DiscoveryMonitoringService.getMonitorService().listen(msg);
					}
					catch(Exception ex){
						logger.error("**** problem spotted ! ", ex);
					}
				}
			}
			// Stop the RunManager
			// Not a singleton ...
			//RunManagerImpl.getInstance().stop();			
		}
		catch(Exception ex){
			logger.error("An error occurs when trying to start a new run", ex);
		}
		logger.debug("Test URL detection stop");
	}
		
	/**
	 * 
	 */
	//TODO Stay here ? Remove this method ?	
	public void urlDetectionCompute(){
		// compute additional, non-local indicators :
		// Not a singleton ...
		/*if(MonitoringMode.DISCOVERY.compareTo(DiscoveryMonitoringService.getMonitorService().getMode()) == 0){
			DiscoveryMonitoringService.getMonitorService().registerDetectedServicesToNuxeo();
		} else {
			DiscoveryMonitoringService.getMonitorService().registerUnknownMessagesToNuxeo();
		}*/
	}

	/**
	 *
	 */
	//TODO Stay here ? Remove this method ?
	public void urlDetectionDebugResults(){
		// debug / print them only in discovery mode :
		// Not a singleton ...
		//UrlTree urlTree = DiscoveryMonitoringService.getMonitorService().getUrlTree();
	    
	    /*//UrlTree urlTree = null;
		if(urlTree != null){
			logger.debug("Printing tree node index ***");
			logger.debug("Total url count : " + urlTree.getTotalUrlCount());
			String key;
			HashMap<String, UrlTreeNode> index = urlTree.getNodeIndex();
			Iterator<String> iter2 = index.keySet().iterator();
			UrlTreeNode node;
			UrlTreeNode parentNode;
			while(iter2.hasNext()){
				key = iter2.next();
				node = index.get(key);
				if(node.getLevel() <= 3){
					parentNode = (UrlTreeNode)(node.getParent());
					logger.debug("complete = " + node.getCompleteUrlcallCount() + ",partial = " + node.getPartialUrlcallCount() + ",total = " + urlTree.getTotalUrlCount());
					logger.debug(key + " -- " + node.toString() + ", parent node => " + parentNode.getNodeName() + ", Depth => " + node.getDepth() + ", Level => " + node.getLevel()); 
					logger.debug(", direct node childs => " + node.getChildCount() + "Total childs number => " + node.getTotalChildsNumber());
					logger.debug(", ratioPartial => " + node.getRatioPartial(urlTree) + "%, ratioComplete => " + node.getRatioComplete(urlTree) + "%" + ", Ratio childs => " + node.getRatioChilds() + "%" + ", Ratio childs to ancestor => " + node.getRatioChildsToAncestor() + "%");
				}
			}
		}*/
	}

}
