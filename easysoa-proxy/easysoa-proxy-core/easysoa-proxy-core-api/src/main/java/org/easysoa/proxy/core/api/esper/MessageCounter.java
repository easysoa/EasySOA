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

package org.easysoa.proxy.core.api.esper;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

@Deprecated
public class MessageCounter implements UpdateListener {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(MessageCounter.class.getName());
	
	@Override
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
