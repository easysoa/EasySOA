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

import java.util.ArrayDeque;
import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.esper.EsperEngine;
import org.easysoa.proxy.core.api.monitoring.AbstractMonitoringService;
import org.easysoa.proxy.core.api.monitoring.MonitoringModel;
import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * Monitoring service for Validated mode
 * 
 * Validated mode get already discovered services from Nuxeo, then listen for exchanges.
 * If a listened service is already in the list form Nuxeo, the call counter is increased
 * otherwise the service is stored in an unknow service list (TODO later)
 * 
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
	 * @throws Exception 
	 * 
	 */
	public ValidatedMonitoringService() throws Exception{
		// init & fill it from Nuxeo
		logger.debug("Mode = VALIDATED !!");
		monitoringMode = MonitoringMode.VALIDATED;
		unknownExchangeRecordList = new ArrayDeque<ExchangeRecord>();
		monitoringModel = new MonitoringModel();
		monitoringModel.fetchFromNuxeo();
		urlTree = null;
	}

	@Override
	public void registerUnknownMessagesToNuxeo() {
		// Nothing to do, unknow messages are just ignored
	    // TODO later, implements this method to register unknow services
	}

	@Override
	public void registerDetectedServicesToNuxeo() {
        // Nothing to do, services are already registered in nuxeo
	    // Method used only in discovery mode
	}

	@Override
	public void listen(ExchangeRecord exchangeRecord){
		listen(exchangeRecord, esperEngine);
	}
	
	

}
