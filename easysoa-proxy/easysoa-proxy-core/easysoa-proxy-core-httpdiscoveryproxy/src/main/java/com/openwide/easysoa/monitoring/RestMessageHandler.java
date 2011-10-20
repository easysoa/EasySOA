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

package com.openwide.easysoa.monitoring;

import org.apache.log4j.Logger;
import com.openwide.easysoa.esper.EsperEngine;
import com.openwide.easysoa.monitoring.Message.MessageType;
import com.openwide.easysoa.monitoring.soa.Node;

public class RestMessageHandler implements MessageHandler {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(RestMessageHandler.class.getName());	

	@Override
	public boolean isOkFor(Message message) {
		// TODO : How to determine if a message is a pure rest message ....
		// TODO : Use a Data Mining framework to discover URI patterns 
		return true;
	}

	@Override
	public boolean handle(Message message, MonitoringService monitoringService, EsperEngine esperEngine) {
		// Add the url in the url tree structure
		logger.debug("REST message found");
		message.setType(MessageType.REST);
		//if(MonitoringMode.DISCOVERY.compareTo(DiscoveryMonitoringService.getMonitorService().getMode()) == 0){
		if(monitoringService instanceof DiscoveryMonitoringService){
			logger.debug("Discovery mode, message added in tree");
			monitoringService.getUrlTree().addUrlNode(message);
		}
		//else if(MonitoringMode.VALIDATED.compareTo(DiscoveryMonitoringService.getMonitorService().getMode()) == 0){
		else if(monitoringService instanceof ValidatedMonitoringService){
			logger.debug("Validated mode, checking if message exists in urlSoaModel");
			//MonitoringModel monitoringModel =  DiscoveryMonitoringService.getMonitorService().getModel();
			MonitoringModel monitoringModel = monitoringService.getModel();
			logger.debug("searched key : " + message.getUrl());
			//TODO change this to match with partial url
			String urlSoaModelType =  monitoringModel.getSoaModelUrlToTypeMap().get(message.getUrl());
			// if none, maybe it is a resource :
			if (urlSoaModelType == null) {
				logger.debug("urlSoaModelType is null, searched key not found ..");
				int lastSlashIndex = message.getUrl().lastIndexOf('/'); // TODO BETTER regexp or finite automat OR ESPER OR SHARED MODEL WITH TREE OR ABSTRACT TREE ??!!
				String serviceUrlOfResource = message.getUrl().substring(0, lastSlashIndex);
				message.setUrl(serviceUrlOfResource); // HACK TODO rather add a field
				urlSoaModelType = monitoringModel.getSoaModelUrlToTypeMap().get(serviceUrlOfResource);
			}
			if (urlSoaModelType != null) {
				logger.debug("Validated mode, message send to esper");
				// if there, feed it to esper
				// TODO write listener that group by serviceUrl and register to nuxeo every minute
				Node soaNode = null;
				for(Node node : monitoringService.getModel().getSoaNodes()){
					if(node.getUrl().equals(message.getUrl())){
						soaNode = node;
						logger.debug("Node found ! " + soaNode.getTitle());
						break;
					}
				}
				esperEngine.sendEvent(soaNode);
			} else {
				logger.debug("Validated mode, Adding message to unknwown message list");
				// Unknown message
				monitoringService.getUnknownMessagesList().add(message);
			}			
		}
		return true;
	}

}
