package com.openwide.easysoa.monitoring.apidetector;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import com.openwide.easysoa.esperpoc.EsperEngineSingleton;
import com.openwide.easysoa.esperpoc.NuxeoRegistrationService;
import com.openwide.easysoa.monitoring.Message;
import com.openwide.easysoa.monitoring.MonitoringModel;
import com.openwide.easysoa.monitoring.soa.Api;
import com.openwide.easysoa.monitoring.soa.Appli;

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
	
	private MonitoringModel monitoringModel; // scope : global
	private UrlTree urlTree; // scope : run
	
    /**
     * Create the test case
     *
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
	public void testUrlDetection(){
		// init
		monitoringModel = null; // none

		// pre run
		// init apidetector tree :
		urlTree = new UrlTree(new UrlTreeNode("root"));

		urlDetectionSimulate(new UrlMock().getTwitterUrlData());
		urlDetectionCompute();
		urlDetectionDebugResults();
		
		// post run (detection mode)
		// let esper send detected apis & services to nuxeo
		registerDetectedServicesToNuxeo();

		// load nuxeo model and display it
		MonitoringModel testSoaModel = new MonitoringModel();
		testSoaModel.fetchFromNuxeo();
		logger.debug("allNodes:\n" + testSoaModel.getSoaNodes());
		
		urlTree = null;
	}
	
	private void registerDetectedServicesToNuxeo() {
		UrlTreeNode rootNode = (UrlTreeNode) urlTree.getRoot();
		registerChildren(rootNode);
	}

	private void registerChildren(UrlTreeNode node) {
		
		for (int i = 0; i < node.getChildCount(); i++) {
			
			UrlTreeNode childNode = (UrlTreeNode) node.getChildAt(i);
			
			node = (UrlTreeNode) childNode.getParent();
			if(childNode.getLevel() == 1){
				// Application detected
				logger.debug("[MessageListener] --- new Appli to register !!!!");
				if(!childNode.isRegistered() && childNode.getPartialUrlcallCount() > 5 && childNode.getCompleteUrlcallCount() == 0){
					Appli appli = new Appli(childNode.getNodeName(), childNode.getNodeName());
					appli.setUiUrl(childNode.getNodeName());
					if(!"ok".equals(new NuxeoRegistrationService().registerRestAppli(appli))){
						childNode.setRegistered();
					}
				}
			}
			// API detection
			else if(childNode.getChildCount() > 0 && childNode.getRatioComplete(urlTree) == 0 && childNode.getLevel() >= 2 
					&& childNode.getPartialUrlcallCount() > 5 && !childNode.isRegistered() && node.isRegistered()){
				logger.debug("[MessageListener] --- new Api to register !!!!");
				Api api = new Api(childNode.getNodeName(), node.getNodeName());
				api.setTitle(childNode.getNodeName());
				if(!"ok".equals(new NuxeoRegistrationService().registerRestApi(api))){
					childNode.setRegistered();
				}
			}
			
			registerChildren(childNode);
		}
	}

	/**
	 * 
	 */
	public void testUrlValidated(){
		// NB. in validation mode, no concept or pre or post run

		// init
		monitoringModel = new MonitoringModel();
		// fill it from nuxeo
		monitoringModel.fetchFromNuxeo();
		logger.debug("allNodes:\n" + monitoringModel.getSoaNodes());
		/*soaModelUrlToTypeMap = new HashMap<String, String>();
		soaModelUrlToTypeMap.put("http://api.twitter.com/1/users/show", SOA_MODEL_TYPE_SERVICE);*/
		// TODO LATER cache it

		//urlDetectionSimulate(new UrlMock().getTwitterUrlData());
		urlDetectionSimulate(new UrlMock().getTwitterUrlData());
		// TODO LATER mixed mode : do compute and debugResults BUT ONLY on unknown messages ?!
		//urlDetectionCompute();
		//urlDetectionDebugResults();
		
		// load nuxeo model and display it
		//MonitoringModel testSoaModel = new MonitoringModel();
		//testSoaModel.fetchFromNuxeo();
		//logger.debug("allNodes:\n" + testSoaModel.getSoaNodes());
	}
	
	/**
	 * @param arrayList 
	 * 
	 */
	public void urlDetectionSimulate(ArrayList<String> arrayList){
		// simulate rest exchanges
		logger.debug( "Test URL detection start");
		Iterator<String> iter = arrayList.iterator();;
		String urlString;
		//for(int i=0; i<1000;i++){
		for(int i=0; i<1;i++){
			while(iter.hasNext()){
				urlString = iter.next();
				logger.debug("**** New URL :" + urlString);
				try{
					URL url = new URL(urlString);
					Message msg = new Message(url, "REST");
					
					// actual handling :
					handleMessage(msg);
				}
				catch(Exception ex){
					logger.error("**** problem spotted ! ", ex);
				}
			}
		}
		logger.debug("Test URL detection stop");
	}
		
	public void urlDetectionCompute(){
		// compute additional, non-local indicators :
		// TODO compute them ; for now, only computed on demand at the end
	}

	/**
	 * 
	 */
	public void urlDetectionDebugResults(){
		// debug / print them :
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
	
	/**
	 * 
	 * @param msg
	 */
	private void handleMessage(Message msg) {
		if (monitoringModel == null) {
			// detection mode
			
			// put in the tree, and compute local indicators :
			urlTree.addUrlNode(msg);
			
		} else {
			// validation mode
			
			// TODO match url in soaModel
			String urlSoaModelType = monitoringModel.getSoaModelUrlToTypeMap().get(msg.getUrl());
			
			// if none, maybe it is a resource :
			if (urlSoaModelType == null) {
				int lastSlashIndex = msg.getUrl().lastIndexOf('/'); // TODO BETTER regexp or finite automat OR ESPER OR SHARED MODEL WITH TREE OR ABSTRACT TREE ??!!
				String serviceUrlOfResource = msg.getUrl().substring(0, lastSlashIndex);
				msg.setUrl(serviceUrlOfResource); // HACK TODO rather add a field
				urlSoaModelType = monitoringModel.getSoaModelUrlToTypeMap().get(serviceUrlOfResource);
			}

			if (urlSoaModelType != null) {
				// if there, feed it to esper
				// TODO put known serviceUrl in esper
				// TODO write listener that groups by serviceUrl and registers them to nuxeo every minute
				EsperEngineSingleton.getEsperRuntime().sendEvent(msg);
			
			} else {
				// else add it to unknownMessageStore (if service not there already) & remember to send an alert (also aggregated)
				//TODO
			}
		}
	}
}
