package com.openwide.easysoa.monitoring;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.openwide.easysoa.esperpoc.NuxeoRegistrationService;
import com.openwide.easysoa.esperpoc.RunManager;
import com.openwide.easysoa.esperpoc.RunRecorder;
import com.openwide.easysoa.monitoring.apidetector.UrlTree;
import com.openwide.easysoa.monitoring.apidetector.UrlTreeNode;
import com.openwide.easysoa.monitoring.soa.Api;
import com.openwide.easysoa.monitoring.soa.Appli;
import com.openwide.easysoa.monitoring.soa.Service;

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
	private static MonitoringMode mode = null;
	
	/**
	 * 
	 */
	private static MonitorService monitorService = null;
	
	/**
	 * 
	 */
	private MonitoringModel monitoringModel; // scope : global
	
	/**
	 * urlTree
	 */
	private UrlTree urlTree; // scope : run	
	
	/**
	 * Data structure to store unknown messages
	 */
	private ArrayDeque<Message> unknownMessagesList;
	
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
		//logger.debug("In MonitorService constructor !!");
		if(MonitoringMode.DISCOVERY.compareTo(mode) == 0){
			logger.debug("Mode = DISCOVERY !!");
			monitoringModel = null;
			urlTree = new UrlTree(new UrlTreeNode("root", ""));
			unknownMessagesList = null;
		} else if(MonitoringMode.VALIDATED.compareTo(mode) == 0) {
			// init & fill it from Nuxeo
			logger.debug("Mode = VALIDATED !!");
			unknownMessagesList = new ArrayDeque<Message>();
			monitoringModel = new MonitoringModel();
			monitoringModel.fetchFromNuxeo();
			logger.debug("Validated mode : Printing monitoring model keyset");
			Iterator<String> iter = monitoringModel.getSoaModelUrlToTypeMap().keySet().iterator();
			String key;
			while(iter.hasNext()){
				key = iter.next();
				logger.debug("key = " + key + ", value = " + monitoringModel.getSoaModelUrlToTypeMap().get(key));
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
		if(monitorService == null || monitoringMode.compareTo(mode) != 0){
			// If monitor Service != null => register before to create a new Monitoring service
			if(monitorService != null){
				logger.debug("Changing monitoring mode, processing messages data before switching");
				if(MonitoringMode.DISCOVERY.compareTo(monitorService.getMode())==0){
					monitorService.registerDetectedServicesToNuxeo();	
				} else {
					monitorService.registerUnknownMessagesToNuxeo();
				}
			}
			monitorService = new MonitorService(monitoringMode);
		}
		return monitorService;
	}

	/**
	 * Listen a message
	 * @param message The <code>Message</code> to listen
	 */
	public void listen(Message message){
	    logger.debug("Listenning message : " + message);
	    RunRecorder recorder = new RunRecorder();
	    recorder.record(message);
		for(MessageHandler mh : messageHandlers){
	    	// add code here to call each messageHandler
	    	// When the good message handler is found, stop the loop
	    	if(mh.isOkFor(message)){
	    		logger.debug("MessageHandler found : " + mh.getClass().getName());
	    		if(mh.handle(message)){
	    			break;
	    		}
	    	}
	    }
	}

	/**
	 * Return the monitoring mode
	 * @return <code>MonitoringMode</code>
	 */
	public static MonitoringMode getMode(){
		return mode;
	}

	/**
	 * 
	 * @return
	 */
	public MonitoringModel getModel(){
		return this.monitoringModel;
	}
	
	/**
	 * Returns the url tree
	 * @return
	 */
	public UrlTree getUrlTree(){
		return this.urlTree;
	}
	
	/**
	 * Returns the unknown messages list
	 * @return
	 */
	public ArrayDeque<Message> getUnknownMessagesList(){
		return this.unknownMessagesList;
	}
	
	public void registerUnknownMessagesToNuxeo(){
		// TODO => Proceed unknown message list to send a message / notification
	}
	
	/**
	 * Sends detected apis & services to nuxeo
	 * Analyse the urlTree
	 */
	public void registerDetectedServicesToNuxeo() {
		logger.debug("Analysing urlTree and registering in Nuxeo");
		if(urlTree != null){
			UrlTreeNode rootNode = (UrlTreeNode) urlTree.getRoot();
			registerChildren(rootNode);
		}
	}

	/**
	 * Register the children of the node
	 * @param node The node 
	 */
	//TODO Method used in class UrlTreeEventListener !!!
	private void registerChildren(UrlTreeNode node) {
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		for (int i = 0; i < node.getChildCount(); i++) {
			UrlTreeNode childNode = (UrlTreeNode) node.getChildAt(i);
			node = (UrlTreeNode) childNode.getParent();
			// APLLICATION detection
			if(childNode.getLevel() == 1){
				logger.debug("[registerChildren] --- new Appli to register !!!!");
				if(!childNode.isRegistered() && childNode.getPartialUrlcallCount() > 5 && childNode.getCompleteUrlcallCount() == 0){
					Appli appli = new Appli(childNode.getNodeName(), childNode.getPartialUrl());
					appli.setUiUrl(childNode.getPartialUrl());
					appli.setTitle(childNode.getNodeName());
					appli.setDescription(childNode.getNodeName());
					if(!"ok".equals(nrs.registerRestAppli(appli))){
						childNode.setRegistered();
					}
				}
			}
			// API detection
			//TODO CHange the parameters to detect API (Hardcoded level parameter !)
			else if(childNode.getChildCount() > 0 && childNode.getRatioComplete(urlTree) == 0 && childNode.getLevel() >= 2 && childNode.getLevel() < 4 
					&& childNode.getPartialUrlcallCount() > 5 && !childNode.isRegistered() && node.isRegistered()){
				logger.debug("[registerChildren] --- new Api to register !!!!");
				Api api = new Api(childNode.getPartialUrl(), node.getPartialUrl());
				api.setTitle(childNode.getNodeName());
				api.setDescription(childNode.getNodeName());
				if(!"ok".equals(nrs.registerRestApi(api))){
					childNode.setRegistered();
				}
			}
			// Service detection
			// How to make distinction between service and api ????
			// identification d'un service atomique : ratio du noeud aux messages vu > 1 à 10 (?) et ratio du noeud à ses enfants (s'il en a) > 1 à 10 (?)
			// TODO Change the parameters to detect service (Hardcoded level parameter !)
			else if(childNode.getLevel() == 4  && !childNode.isRegistered() && node.isRegistered()){
				// Message lastMessage = childNode.getMessages().getLast(); // Pas sur une feuille donc ArrayDeque message pas rempli ...
				// TODO Revoir le systeme de remplissage des messages, mise en place d'un système pour detecter des patterns dans les url
				logger.debug("[registerChildren] --- new Service to register !!!!");
				Service service = new Service(childNode.getPartialUrl(), node.getPartialUrl());
				logger.debug("Messages size : " + childNode.getMessages().size());
				service.setCallCount(childNode.getMessages().size());
				service.setTitle(childNode.getNodeName());
				service.setDescription(childNode.getNodeName());
				service.setHttpMethod(childNode.getMessages().getLast().getMethod());
				if(!"ok".equals(nrs.registerRestService(service))){
					childNode.setRegistered();
				}
			}
			registerChildren(childNode);
		}
	}	
	
}
