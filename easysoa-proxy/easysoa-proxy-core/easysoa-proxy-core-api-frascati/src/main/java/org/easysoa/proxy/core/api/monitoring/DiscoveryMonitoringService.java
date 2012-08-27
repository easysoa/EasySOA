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

package org.easysoa.proxy.core.api.monitoring;

import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.esper.EsperEngine;
import org.easysoa.proxy.core.api.monitoring.AbstractMonitoringService;
import org.easysoa.proxy.core.api.monitoring.apidetector.UrlTree;
import org.easysoa.proxy.core.api.monitoring.apidetector.UrlTreeNode;
import org.easysoa.proxy.core.api.monitoring.soa.Api;
import org.easysoa.proxy.core.api.monitoring.soa.Appli;
import org.easysoa.proxy.core.api.monitoring.soa.Service;
import org.easysoa.proxy.core.api.nuxeo.registration.NuxeoRegistrationService;
import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

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
	 * Reference to Esper engine 
	 */
	@Reference
	EsperEngine esperEngine;  	
	
	/**
	 * Constructor
	 */
	public DiscoveryMonitoringService(){
		logger.debug("Mode = DISCOVERY !!");
		monitoringModel = null;
		urlTree = new UrlTree(new UrlTreeNode("root", ""));
		//unknownMessagesList = null;
		unknownExchangeRecordList = null;
	}

	/**
	 * Return the monitoring mode
	 * @return <code>MonitoringMode</code>
	 */
	public MonitoringMode getMode(){
		return MonitoringMode.DISCOVERY;
	}
	
	/* (non-Javadoc)
	 * @see org.easysoa.monitoring.MonitoringService#registerUnknownMessagesToNuxeo()
	 */
	@Override
	public void registerUnknownMessagesToNuxeo(){
		// Nothing to do here, all messages are considered as unknown
	}
	
	/* (non-Javadoc)
	 * @see org.easysoa.monitoring.MonitoringService#registerDetectedServicesToNuxeo()
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
	    
		NuxeoRegistrationService nrs = null;
        try {
            nrs = new NuxeoRegistrationService();
        } catch (Exception ex) {
            logger.error("Failed to create Nuxeo registration service", ex);
            return;
        }
		
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
				//service.setHttpMethod(childNode.getMessages().getLast().getMethod());
				service.setHttpMethod(childNode.getMessages().getLast().getInMessage().getMethod());
				if(!"ok".equals(nrs.registerRestService(service))){
					childNode.setRegistered();
				}
			}
			registerChildren(childNode);
		}
	}

	@Override
	/*public void listen(Message message) {
		listen(message, esperEngine);
	}*/
	public void listen(ExchangeRecord exchangeRecord) {
		listen(exchangeRecord, esperEngine);
	}
}
