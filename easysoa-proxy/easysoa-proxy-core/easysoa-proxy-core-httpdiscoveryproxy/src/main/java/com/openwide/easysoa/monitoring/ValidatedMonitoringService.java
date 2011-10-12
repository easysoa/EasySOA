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
 * Contact : easysoa-dev@groups.google.com
 */

package com.openwide.easysoa.monitoring;

import java.util.ArrayDeque;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import com.openwide.easysoa.esper.EsperEngine;

/**
 * Monitoring service for Validated mode
 * @author jguillemotte
 *
 */
@Scope("composite")
public class ValidatedMonitoringService extends AbstractMonitoringService {

	/**
	 * Logger
	 */
	private Logger logger = Logger.getLogger(ValidatedMonitoringService.class.getName());	
	
	/**
	 * Reference to Esper engine 
	 */
	@Reference
	EsperEngine esperEngine; 	
	
	/**
	 * 
	 */
	public ValidatedMonitoringService(){
		// init & fill it from Nuxeo
		logger.debug("Mode = VALIDATED !!");
		unknownMessagesList = new ArrayDeque<Message>();
		monitoringModel = new MonitoringModel();
		monitoringModel.fetchFromNuxeo();
		logger.debug("Validated mode : Printing monitoring model keyset");
		for (String url : monitoringModel.getSoaModelUrlToTypeMap().keySet()) {
			logger.debug("url = " + url + ", value = " + monitoringModel.getSoaModelUrlToTypeMap().get(url));
		}
		Iterator<String> urlIter = monitoringModel.getSoaModelUrlToTypeMap().keySet().iterator();
		String url;
		while(urlIter.hasNext()){
			url = urlIter.next();
			logger.debug("url = " + url + ", value = " + monitoringModel.getSoaModelUrlToTypeMap().get(url));
		}
		urlTree = null;
	}
	
	/**
	 * Return the monitoring mode
	 * @return <code>MonitoringMode</code>
	 */
	public MonitoringMode getMode(){
		return MonitoringMode.VALIDATED;
	}	

	@Override
	public void registerUnknownMessagesToNuxeo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerDetectedServicesToNuxeo() {
		// TODO Auto-generated method stub
	}

	@Override
	public void listen(Message message) {
		listen(message, esperEngine);
	}

}
