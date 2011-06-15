package com.openwide.easysoa.monitoring;

import org.apache.log4j.Logger;
import com.openwide.easysoa.esperpoc.EsperEngineSingleton;
import com.openwide.easysoa.monitoring.Message.MessageType;
import com.openwide.easysoa.monitoring.MonitorService.MonitoringMode;

public class RestMessageHandler implements MessageHandler {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(RestMessageHandler.class.getName());	

	@Override
	public boolean isOkFor(Message message) {
		// TODO : How to determine if a message is a pure rest message ....
		return true;
	}

	@Override
	public void handle(Message message) {
		// Add the url in the url tree structure
		logger.debug("REST message found");
		message.setType(MessageType.REST);
		if(MonitoringMode.DISCOVERY.compareTo(MonitorService.getMonitorService().getMode()) == 0){
			logger.debug("Discovery mode, message added in tree");
			MonitorService.getMonitorService().getUrlTree().addUrlNode(message);
		}
		else if(MonitoringMode.VALIDATED.compareTo(MonitorService.getMonitorService().getMode()) == 0){
			// validation mode
			// TODO match url in soaModel
			logger.debug("Validated mode, checking if message exists in urlSoaModel");
			MonitoringModel monitoringModel =  MonitorService.getMonitorService().getModel();
			logger.debug("searched key : " + message.getUrl());
			//TODO : the entire url is never stored in Nuxeo ....
			//TODO change this to match with partial url
			String urlSoaModelType =  monitoringModel.getSoaModelUrlToTypeMap().get(message.getUrl());
			// if none, maybe it is a resource :
			if (urlSoaModelType == null) {
				logger.debug("urlSoaModelType null .....");
				int lastSlashIndex = message.getUrl().lastIndexOf('/'); // TODO BETTER regexp or finite automat OR ESPER OR SHARED MODEL WITH TREE OR ABSTRACT TREE ??!!
				String serviceUrlOfResource = message.getUrl().substring(0, lastSlashIndex);
				message.setUrl(serviceUrlOfResource); // HACK TODO rather add a field
				urlSoaModelType = monitoringModel.getSoaModelUrlToTypeMap().get(serviceUrlOfResource);
			}
			if (urlSoaModelType != null) {
				logger.debug("Validated mode, message send to esper");
				// if there, feed it to esper
				// TODO put known serviceUrl in esper
				// TODO write listener that groups by serviceUrl and registers them to nuxeo every minute
				EsperEngineSingleton.getEsperRuntime().sendEvent(message);
			} else {
				//TODO else add it to unknownMessageStore (if service not there already) & remember to send an alert (also aggregated)
			}			
		}
	}

}
