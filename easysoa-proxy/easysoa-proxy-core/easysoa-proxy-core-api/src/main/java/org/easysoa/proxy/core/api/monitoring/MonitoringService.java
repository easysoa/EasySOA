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

import org.easysoa.proxy.core.api.monitoring.apidetector.UrlTree;
import org.easysoa.records.ExchangeRecord;

/**
 * Monitoring service interface
 * @author jguillemotte
 *
 */
public interface MonitoringService {

	/**
	 * Monitoring modes
	 * - Discovery : register all the listened messages in Nuxeo, all messages are considered as new services
	 * - Validated : listen only the services already registered in Nuxeo, the unknown services are stored in a unknown message list and then, can be optionally registered.
	 */
	public enum MonitoringMode {
		DISCOVERY, VALIDATED
	}	
	
	/**
	 * Return the Monitoring mode
	 * @return The monitoring mode
	 */
	public MonitoringMode getMode();
	
	/**
	 * Listen a message
	 * @param message The <code>Message</code> to listen
	 */
	public void listen(ExchangeRecord exchangeRecord);

	/**
	 * Returns the monitoring model, the monitoring model is only loaded from nuxeo for Validated mode
	 * @return The monitoring model (validated mode) or null (discovery mode)
	 */
	public MonitoringModel getModel();

	/**
	 * Returns the url tree, used in Discovery mode to determine the services, api's and apply's
	 * @return UrlTree
	 */
	public UrlTree getUrlTree();

	/**
	 * Returns the unknown messages list
	 * @return ArrayDeque<ExchangeRecord> containing the unknow messages
	 */
	public ArrayDeque<ExchangeRecord> getUnknownExchangeRecordList();

	/**
	 * Registrer unknow message in Nuxeo
	 */
	public void registerUnknownMessagesToNuxeo();

	/**
	 * Sends detected apis & services to nuxeo
	 * Analyse the urlTree
	 */
	public void registerDetectedServicesToNuxeo();

}