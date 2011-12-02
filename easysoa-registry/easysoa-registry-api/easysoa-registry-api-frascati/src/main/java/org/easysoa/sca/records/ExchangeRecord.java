/**
 * EasySOA Registry
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

package org.easysoa.sca.records;

@Deprecated
public class ExchangeRecord {

	// TODO : remove this class and use ExchangeRecord form the easysoa.messaging.api instead !!!
	
	// in / out messages
	private String inMessage;
	private String outMessage;
	// Exchange ID
	private String exchangeId;

	/**
	 * Build a new ExchangeRecord object
	 * @param request The request content
	 * @param response The response content
	 * @throws IOException
	 */
	public ExchangeRecord(String inMessage, String outMessage) {
		//this.requestContent = new Scanner(request.getInputStream()).useDelimiter("\\A").next();
		this.inMessage = inMessage;
		this.outMessage = outMessage;
	}
	
	/**
	 * 
	 * @param inMessage
	 */
	public ExchangeRecord(String inMessage){
		this.inMessage = inMessage;
	}
	
	/**
	 * 
	 * @param outMessage
	 */
	public void setOutMessage(String outMessage){
		this.outMessage = outMessage;
	}
	
	/**
	 * returns the exchange request
	 * @return <code>ServletRequest</code>
	 */
	public String getInMessage(){
		return this.inMessage;
	}
	
	/**
	 * Returns the exchange response
	 * @return <code>ServletResponse</code>
	 */
	public String getOutMessage(){
		return this.outMessage;
	}
	
}
