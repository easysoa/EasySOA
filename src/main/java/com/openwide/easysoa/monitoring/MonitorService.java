package com.openwide.easysoa.monitoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.openwide.easysoa.esperpoc.NuxeoRegistrationService;
import com.openwide.easysoa.monitoring.apidetector.UrlTree;
import com.openwide.easysoa.monitoring.apidetector.UrlTreeNode;
import com.openwide.easysoa.monitoring.soa.Api;
import com.openwide.easysoa.monitoring.soa.Appli;

public class MonitorService {

	/**
	 * Singleton to store all global project stuff (temporarily)
	 * Modes : discovery, validated ....
	 * soaModel
	 * urltree
	 */
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(getInvokingClassName());	
	
	/**
	 * Modes
	 */
	public enum MonitoringMode {
		DISCOVERY, VALIDATED
	}
	
	/**
	 * Monitoring mode
	 */
	private static MonitoringMode mode;
	
	/**
	 * 
	 */
	private static MonitorService monitorService = null;
	
	/**
	 * 
	 */
	private MonitoringModel monitoringModel; // scope : global
	
	/**
	 * 
	 */
	private UrlTree urlTree; // scope : run	
	
	/**
	 * List of message handlers
	 */
	private static List<MessageHandler> messageHandlers;
	
    /**
     * 
     * @return
     */
    public static String getInvokingClassName() {
    	return Thread.currentThread().getStackTrace()[1].getClassName();
    }	
	
    /**
     * Message handler list initialization
     * Order is important, more specific first 
     */
    static{
    	messageHandlers = new ArrayList<MessageHandler>();
    	messageHandlers.add(new WSDLMessageHandler());
    	messageHandlers.add(new SoapMessageHandler());    	
    	messageHandlers.add(new RestMessageHandler());    	
    }
    
	/**
	 * 
	 * @param mode
	 */
	private MonitorService(MonitoringMode monitoringMode){
		mode = monitoringMode;
		logger.debug("In MonitorService constructor !!");
		if(MonitoringMode.DISCOVERY.compareTo(mode) == 0){
			logger.debug("Mode = DISCOVERY !!");
			monitoringModel = null;
			urlTree = new UrlTree(new UrlTreeNode("root"));
		} else if(MonitoringMode.VALIDATED.compareTo(mode) == 0) {
			// init & fill it from Nuxeo
			logger.debug("Mode = VALIDATED !!");
			monitoringModel = new MonitoringModel();
			monitoringModel.fetchFromNuxeo();
			//logger.debug("allNodes:\n" + monitoringModel.getSoaNodes());
			logger.debug("Validated mode : Printing monitoring model keyset");
			Iterator<String> iter = monitoringModel.getSoaModelUrlToTypeMap().keySet().iterator();
			while(iter.hasNext()){
				logger.debug("key = " + iter.next());
			}
			urlTree = null;
		}
	}
	
	/**
	 * Returns an instance of MonitorService. If this method is called first, the monitoring mode is set with default value 'VALIDATED' !
	 * @return An instance of <code>MonitorService</code>
	 */
	public static MonitorService getMonitorService(){
		if(monitorService == null){
			return getMonitorService(MonitoringMode.VALIDATED);
		} else {
			return monitorService;
		}
	}
	
	
	/**
	 * Returns an instance of MonitorService. If the mode is changed since the last call to this method, a new instance of MonitorService is returned !
	 * @param mode Monitoring mode 
	 * @return An instance of <code>MonitorService</code> 
	 */
	public static MonitorService getMonitorService(MonitoringMode monitoringMode){
		if(monitorService == null && !monitoringMode.equals(mode)){
			monitorService = new MonitorService(monitoringMode);
		}
		return monitorService;
	}
	
	/**
	 * 
	 */
	public void listen(Message message){
	    logger.debug("Listenning message : " + message);
		for(MessageHandler mh : messageHandlers){
	    	// add code here to call each messageHandler
	    	// When the good message handler is found, stop the loop
	    	if(mh.isOkFor(message)){
	    		logger.debug("MessageHandler found : " + mh.getClass().getName());
	    		mh.handle(message);
	    	}
	    }		
	}
	
	/**
	 * Return the monitoring mode
	 * @return <code>MonitoringMode</code>
	 */
	@SuppressWarnings("static-access")
	public MonitoringMode getMode(){
		return this.mode;
	}

	/**
	 * 
	 * @return
	 */
	public MonitoringModel getModel(){
		return this.monitoringModel;
	}
	
	/**
	 * 
	 * @return
	 */
	public UrlTree getUrlTree(){
		return this.urlTree;
	}
	
	/**
	 * Sends detected apis & services to nuxeo
	 */
	public void registerDetectedServicesToNuxeo() {
		UrlTreeNode rootNode = (UrlTreeNode) urlTree.getRoot();
		registerChildren(rootNode);
	}
	
	/**
	 * 
	 * @param node
	 */
	//TODO Method used in class UrlTreeEventListener !!! Factorisation !
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
					appli.setTitle(childNode.getNodeName());
					appli.setDescription(childNode.getNodeName());
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
				api.setDescription(childNode.getNodeName());
				if(!"ok".equals(new NuxeoRegistrationService().registerRestApi(api))){
					childNode.setRegistered();
				}
			}
			
			registerChildren(childNode);
		}
	}	
	
	/**
	 * Handle a message
	 * @param msg
	 */
	//TODO Move this method in Each Message Handler
	// Problem : How to deal with the modes .....
	/*private void handleMessage(Message msg) {
		if (MonitoringMode.DISCOVERY.equals(mode)) {
			// detection mode
			// put in the tree, and compute local indicators :
			urlTree.addUrlNode(msg);
			
		} else if(MonitoringMode.VALIDATED.equals(mode)){
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
	}*/	
	
	
	
}
