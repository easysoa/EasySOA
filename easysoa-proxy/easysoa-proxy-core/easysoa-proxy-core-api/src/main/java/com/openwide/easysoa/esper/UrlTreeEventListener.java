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

package com.openwide.easysoa.esper;

import org.apache.log4j.Logger;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.openwide.easysoa.monitoring.apidetector.UrlTreeNode;
import com.openwide.easysoa.monitoring.apidetector.UrlTreeNodeEvent;
import com.openwide.easysoa.monitoring.soa.Api;
import com.openwide.easysoa.monitoring.soa.Appli;
import com.openwide.easysoa.nuxeo.registration.NuxeoRegistrationService;

/**
 * Triggered on every node update (LATER could be triggered on run end event)
 * Receives one event per url(TreeNode) each time
 * and registers the corresponding service to nuxeo
 * 
 * @author jguillemotte
 *
 */
public class UrlTreeEventListener implements UpdateListener {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(UrlTreeEventListener.class.getName());
	
	/**
	 * newData contains all the events received from the app start !!!!
	 */
	@Override
	public void update(EventBean[] newData, EventBean[] oldData) {
		logger.debug("[update()] new event received !");
		// Each time an event is received
		// An analysis must be done to determine if a new service can be registered in Nuxeo ....
		logger.debug("[MessageListener] --- NewData length : " + newData.length);
		for(int i=0; i<newData.length; i++){
			logger.debug("[MessageListener] --- Event received : " + newData[i].getUnderlying());
			UrlTreeNodeEvent event = (UrlTreeNodeEvent)(newData[i].getUnderlying());
			UrlTreeNode node = event.getEventSource();
			
			// Application detection
			UrlTreeNode parentNode = (UrlTreeNode)(node.getParent());
			logger.debug("[MessageListener] --- node name " + node.getNodeName());
			logger.debug("[MessageListener] --- node level " + node.getLevel());
			logger.debug("[MessageListener] --- node registered " + node.isRegistered());
			logger.debug("[MessageListener] --- node child count " + node.getChildCount());
			logger.debug("[MessageListener] --- node ratio complete " + node.getRatioComplete(event.getUrlTree()));
			logger.debug("[MessageListener] --- node partial url count " + node.getPartialUrlcallCount());
			logger.debug("[MessageListener] --- node parent registered "  + parentNode.isRegistered());

            try {
                NuxeoRegistrationService nrs = new NuxeoRegistrationService();
                
                if(node.getLevel() == 1){
                    // Application detected
                    logger.debug("[MessageListener] --- new Appli to register !!!!");
                    if(!node.isRegistered() && node.getPartialUrlcallCount() > 5 && node.getCompleteUrlcallCount() == 0){
                        Appli appli = new Appli(node.getNodeName(), node.getNodeName());
                        appli.setUiUrl(node.getNodeName());
                        appli.setDescription(node.getNodeName());
                        appli.setTitle(node.getNodeName());
                        if(!"ok".equals(nrs.registerRestAppli(appli))){
                            node.setRegistered();
                        }
                    }
                }
                // API detection
                else if(node.getChildCount() > 0 && node.getRatioComplete(event.getUrlTree()) == 0 && node.getLevel() >= 2  && node.getPartialUrlcallCount() > 5 && !node.isRegistered() && parentNode.isRegistered()){
                    logger.debug("[MessageListener] --- new Api to register !!!!");
                    Api api = new Api(node.getNodeName(), parentNode.getNodeName());
                    api.setTitle(node.getNodeName());
                    api.setDescription(node.getNodeName());
                    if(!"ok".equals(nrs.registerRestApi(api))){
                        node.setRegistered();
                    }
                }
            } catch (Exception ex) {
                logger.error("Failed to register node", ex);
            }
            
			// Service detection
			// TODO Add an efficient service detection system
			//Comment faire la distinction entre service et api ????
			// identification d'un service atomique : ratio du noeud aux messages vu > 
			// 1 à 10 (?) et ratio du noeud à ses enfants (s'il en a) > 1 à 10 (?)
			/*else if(event.getRatioAllChilds()){
				Message lastMessage = node.getMessages().getLast();
				Service service = new Service(node.getNodeName(), parentNode.getNodeName());
				service.setCallCount(service.getCallCount() + node.getMessages().size());
				service.setHttpMethod(lastMessage.getMethod());
				if(!"ok".equals(nrs.registerRestService(service))){
					node.setRegistered();
				}
			}*/
			// Application => direct childs of root node
			// API => level 2 childs to level n childs 
			// Atomic service => level n+1 to level n+n
		}
	}

}
