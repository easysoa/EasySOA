package org.openwide.easysoa.test.monitoring.apidetector;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.Assume;

import com.openwide.easysoa.esperpoc.run.RunManagerImpl;
import com.openwide.easysoa.monitoring.Message;
import com.openwide.easysoa.monitoring.Message.MessageType;
import com.openwide.easysoa.monitoring.MonitorService.MonitoringMode;
import com.openwide.easysoa.monitoring.apidetector.UrlTree;
import com.openwide.easysoa.monitoring.apidetector.UrlTreeNode;
import com.openwide.easysoa.monitoring.MonitorService;
import com.sun.jersey.api.client.ClientHandlerException;

/**
 * Unit test for simple App.
 */
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
		MonitorService.getMonitorService(MonitoringMode.DISCOVERY);
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
			MonitorService.getMonitorService(MonitoringMode.VALIDATED);
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
			RunManagerImpl.getInstance().start("Test run");
			//for(int i=0; i<1000;i++){
			for(int i=0; i<1;i++){
				while(iter.hasNext()){
					urlString = iter.next();
					logger.debug("**** New URL :" + urlString);
					try{
						URL url = new URL(urlString);
						Message msg = new Message(url, MessageType.REST);
						// listen message
						MonitorService.getMonitorService().listen(msg);
					}
					catch(Exception ex){
						logger.error("**** problem spotted ! ", ex);
					}
				}
			}
			// Stop the RunManager
			RunManagerImpl.getInstance().stop();			
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
		if(MonitoringMode.DISCOVERY.compareTo(MonitorService.getMonitorService().getMode()) == 0){
			MonitorService.getMonitorService().registerDetectedServicesToNuxeo();
		} else {
			MonitorService.getMonitorService().registerUnknownMessagesToNuxeo();
		}
	}

	/**
	 *
	 */
	//TODO Stay here ? Remove this method ?
	public void urlDetectionDebugResults(){
		// debug / print them only in discovery mode :
		UrlTree urlTree = MonitorService.getMonitorService().getUrlTree();
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
		}
	}

}
