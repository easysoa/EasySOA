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

import java.util.ArrayDeque;

import org.easysoa.records.ExchangeRecord;

import com.openwide.easysoa.monitoring.apidetector.UrlTree;

public interface MonitoringService {

	/**
	 * Modes
	 */
	public enum MonitoringMode {
		DISCOVERY, VALIDATED
	}	
	
	/**
	 * Listen a message
	 * @param message The <code>Message</code> to listen
	 */
	//public void listen(Message message);
	public void listen(ExchangeRecord exchangeRecord);

	/**
	 * 
	 * @return
	 */
	public MonitoringModel getModel();

	/**
	 * Returns the url tree
	 * @return
	 */
	public UrlTree getUrlTree();

	/**
	 * Returns the unknown messages list
	 * @return
	 */
	//public ArrayDeque<Message> getUnknownMessagesList();
	public ArrayDeque<ExchangeRecord> getUnknownExchangeRecordList();

	/**
	 */
	public void registerUnknownMessagesToNuxeo();

	/**
	 * Sends detected apis & services to nuxeo
	 * Analyse the urlTree
	 */
	public void registerDetectedServicesToNuxeo();

}