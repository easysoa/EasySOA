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

package org.easysoa.monitoring;

import org.easysoa.esper.EsperEngine;
import org.easysoa.records.ExchangeRecord;


public interface MessageHandler {
	
	/**
	 * return if the message can be handled by this handler
	 * @param message The message to handle 
	 * @return True if the message can be handled, false otherwise
	 * @throws Exception 
	 */
	//public boolean isOkFor(Message message);
	public boolean isOkFor(ExchangeRecord exchangeRecord);
	
	/**
	 * Handle the message
	 * @param message The message to handle
	 */
	// TODO : Monitoring service as parameter here is not a good solution, find an other way ...
	//public boolean handle(Message message, MonitoringService monitoringService, EsperEngine esperEngine);
	public boolean handle(ExchangeRecord exchangeRecord, MonitoringService monitoringService, EsperEngine esperEngine);
	

}
