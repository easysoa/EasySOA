package com.openwide.easysoa.esperpoc.esper;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class MessageCounter implements UpdateListener {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(MessageCounter.class.getName());
	
	/**
	 * 
	 */
	public void update(EventBean[] newData, EventBean[] oldData) {
		logger.debug("[MessageCounter] ****");
		if(newData != null){
			logger.debug("[MessageCounter] --- newData Event received with : " + newData.length + " elements");
			
			for(int i = 0; i < newData.length; i++){
				logger.debug("[MessageCounter] --- newData Event " + i + " received: " + newData[i].getUnderlying());
				logger.debug("[MessageCounter] --- " + newData[i].getUnderlying().getClass().getName());
				@SuppressWarnings("unchecked")
				HashMap<String,Object> hm = (HashMap<String,Object>)(newData[i].getUnderlying());		
				Iterator<String> iter = hm.keySet().iterator();
				while(iter.hasNext()){
					String key = iter.next();
					logger.debug("[MessageCounter] Key : " + key);
					logger.debug("[MessageCounter] Value : " + hm.get(key));
					logger.debug("[MessageCounter] Clazz value : " + hm.get(key).getClass().getName());
				}
			}
		}
		if(oldData != null){
			logger.debug("[MessageCounter] --- oldData Event received with : " + oldData.length + " elements");
			logger.debug("[MessageCounter] --- oldData Event received: " + oldData[0].getUnderlying());
			logger.debug("[MessageCounter] --- " + oldData[0].getUnderlying().getClass().getName());
			@SuppressWarnings("unchecked")
			HashMap<String,Object> hm = (HashMap<String,Object>)(oldData[0].getUnderlying());		
			Iterator<String> iter = hm.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				logger.debug("[MessageCounter] Key : " + key);
				logger.debug("[MessageCounter] Value : " + hm.get(key));
				logger.debug("[MessageCounter] Clazz value : " + hm.get(key).getClass().getName());
			}
		}		
		
		
	}
	
}
