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
		return false;
	}

	@Override
	public boolean handle(Message message) {
		// Add the url in the url tree structure
		//TODO remove the test isOkFor !! => how to send back a boolean ???
		if(isOkFor(message)){
			logger.debug("REST found");
			message.setType(MessageType.REST);
			//TODO What to do here ?? Fill urlTree or not, mode dependency
			//EsperEngineSingleton.getEsperRuntime().sendEvent(message);
			if(MonitorService.getMonitorService().getMode().compareTo(MonitoringMode.DISCOVERY)==0){
				MonitorService.getMonitorService().getUrlTree().addUrlNode(message);
			}
			return true;
		} else {
			return false;
		}
	}

}
