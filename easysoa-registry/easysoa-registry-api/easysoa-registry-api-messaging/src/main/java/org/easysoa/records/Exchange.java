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

/**
 * 
 */
package org.easysoa.records;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jguillemotte
 *
 */
@XmlRootElement
public class Exchange {

	// Exchange ID
	private String exchangeID;
	// Exchange type
	private ExchangeType exchangeType;
	
	/**
	 *	Message types 
	 */
	public enum ExchangeType {
	    WSDL, REST, SOAP
	}		
	
	/**
	 * Default constructor
	 */
	public Exchange(){
		this.exchangeID = "";
	}
	
	/**
	 * Constructor with Exchange ID
	 * @param exchangeID
	 */
	public Exchange(String exchangeID){
		this.exchangeID = exchangeID;
	}
	
	/**
	 * Set the exchange ID
	 * @param exchangeID
	 */
	public void setExchangeID(String exchangeID){
		this.exchangeID = exchangeID;
	}
	
	/**
	 * Returns the exchange ID
	 * @return The Exchange ID
	 */
	public String getExchangeID(){
		return this.exchangeID;
	}	
	
	/**
	 * Get the exchange type
	 * @return The exchange type
	 */
	public ExchangeType getExchangeType() {
		return exchangeType;
	}

	/**
	 * Set the exchange type
	 * @param exchangeType The exchange type
	 */
	public void setExchangeType(ExchangeType exchangeType) {
		this.exchangeType = exchangeType;
	}
	
}
