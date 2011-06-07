package com.openwide.easysoa.esperpoc.esper;

import org.apache.log4j.Logger;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.openwide.easysoa.esperpoc.NuxeoRegistrationService;
import com.openwide.easysoa.esperpoc.UrlTreeNode;
import com.openwide.easysoa.esperpoc.UrlTreeNodeEvent;

public class UrlTreeEventListener implements UpdateListener {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(UrlTreeEventListener.class.getName());
	
	/**
	 * Triggered all 5 minutes :
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
			logger.debug("[MessageListener] --- " + newData[i].getUnderlying().getClass().getName());
			NuxeoRegistrationService nrs = new NuxeoRegistrationService();
			//Service service;
			UrlTreeNodeEvent event = (UrlTreeNodeEvent)(newData[i].getUnderlying());
			UrlTreeNode node = event.getEventSource();
			//UrlTreeNode parentNode = (UrlTreeNode)(node.getParent());
			logger.debug("[MessageListener] --- node name " + node.getNodeName());
			
			// Application dectection
			// Ratio complete = 0%
			//if(parentNode != null && parentNode.isRoot()){
			UrlTreeNode parentNode = (UrlTreeNode)(node.getParent());					
			logger.debug("[MessageListener] --- node level " + node.getLevel());
			logger.debug("[MessageListener] --- node registered " + node.isRegistered());
			logger.debug("[MessageListener] --- node child count " + node.getChildCount());
			logger.debug("[MessageListener] --- node ratio complete " + event.getRatioComplete());
			logger.debug("[MessageListener] --- node partial url count " + node.getPartialUrlcallCount());
			logger.debug("[MessageListener] --- node parent registered "  + parentNode.isRegistered());

			if(node.getLevel() == 1){
				// Application detected
				logger.debug("[MessageListener] --- new Appli to register !!!!");
				if(!node.isRegistered() && node.getPartialUrlcallCount() > 5 && node.getCompleteUrlcallCount() == 0){
					Appli appli = new Appli(node.getNodeName(), node.getNodeName());
					appli.setUiUrl(node.getNodeName());
					if(!"ok".equals(nrs.registerRestAppli(appli))){
						node.setRegistered();
					}
				}
			}
			// API detection, racine commune => assemblage de plusieurs noeuds
			// Ratio complete = 0%
			// Root qui n'est pas un appli root
			// child qui n'est pas une feuille
			// www.imedia.com/shop -- [node name : shop, node partial url call count : 24000, node complete url call count : 0], parent node => www.imedia.com, Depth => 2, node childs => 6, ratioPartial => 100.0%, ratioComplete => 0.0%, Ratio childs = 0.025%
			// www.imedia.com/shop/addBookToBasket -- [node name : addBookToBasket, node partial url call count : 2000, node complete url call count : 0], parent node => shop, Depth => 1, node childs => 851, ratioPartial => 8.333333%, ratioComplete => 0.0%, Ratio childs = 42.55%
			// www.imedia.com/shop/getBook -- [node name : getBook, node partial url call count : 8000, node complete url call count : 0], parent node => shop, Depth => 1, node childs => 1001, ratioPartial => 33.333332%, ratioComplete => 0.0%, Ratio childs = 12.5125%
			else if(node.getChildCount() > 0 && event.getRatioComplete() == 0 && node.getLevel() >= 2  && node.getPartialUrlcallCount() > 5 && !node.isRegistered() && parentNode.isRegistered()){
				logger.debug("[MessageListener] --- new Api to register !!!!");
				Api api = new Api(node.getNodeName(), parentNode.getNodeName());
				api.setTitle(node.getNodeName());
				if(!"ok".equals(nrs.registerRestApi(api))){
					node.setRegistered();
				}
			}
			
			// Service detection
			//Comment faire la distinction entre service et api ????
			// identification d'un service atomique : ratio du noeud aux messages vu > 
			// 1 à 10 (?) et ratio du noeud à ses enfants (s'il en a) > 1 à 10 (?)
			/*else if(){
				
			}*/
			// Detecting API / Application / Services 
			// Analyse ratio, Depth ....
			// Esper request or Java algorithm ????  
			
			// Application => direct childs of root node
			// API => level 2 childs to level n childs 
			// Atomic service => level n+1 to level n+n
		}
	}

}
