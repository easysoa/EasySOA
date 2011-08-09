package com.openwide.easysoa.monitoring;

import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Scope;
import com.openwide.easysoa.monitoring.apidetector.UrlTree;
import com.openwide.easysoa.monitoring.apidetector.UrlTreeNode;
import com.openwide.easysoa.monitoring.soa.Api;
import com.openwide.easysoa.monitoring.soa.Appli;
import com.openwide.easysoa.monitoring.soa.Service;
import com.openwide.easysoa.nuxeo.registration.NuxeoRegistrationService;

/**
 * Monitoring service for Discovery mode
 * @author jguillemotte
 *
 */
@Scope("composite")
public class DiscoveryMonitoringService extends AbstractMonitoringService {
	
	/**
	 * Logger
	 */
	private Logger logger = Logger.getLogger(DiscoveryMonitoringService.class.getName());	

	/**
	 * Constructor
	 */
	public DiscoveryMonitoringService(){
		logger.debug("Mode = DISCOVERY !!");
		monitoringModel = null;
		urlTree = new UrlTree(new UrlTreeNode("root", ""));
		unknownMessagesList = null;
	}

	/**
	 * Return the monitoring mode
	 * @return <code>MonitoringMode</code>
	 */
	public MonitoringMode getMode(){
		return MonitoringMode.DISCOVERY;
	}
	
	/* (non-Javadoc)
	 * @see com.openwide.easysoa.monitoring.MonitoringService#registerUnknownMessagesToNuxeo()
	 */
	@Override
	public void registerUnknownMessagesToNuxeo(){
		// Nothing to do here, all messages are considered as unknown
	}
	
	/* (non-Javadoc)
	 * @see com.openwide.easysoa.monitoring.MonitoringService#registerDetectedServicesToNuxeo()
	 */
	@Override
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
				int callcount = childNode.getMessages().size();
				logger.debug("Messages size : " + callcount);
				Service service = new Service(childNode.getPartialUrl());
				service.setCallCount(callcount);
				service.setParentUrl(node.getPartialUrl());
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
