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

package com.openwide.easysoa.esper;

import org.easysoa.records.ExchangeRecord;
import com.openwide.easysoa.monitoring.soa.Node;

public interface EsperEngine {

	/**
	 * Send a event to the Esper engine 
	 * @param soaNode The <code>Node</code> contained in the event
	 */
	public void sendEvent(Node soaNode);

	/**
	 * Send a event to the Esper engine 
	 * @param soaNode The <code>Message</code> contained in the event
	 */
	//public void sendEvent(Message message);
	public void sendEvent(ExchangeRecord exchangeRecord);
	
}
