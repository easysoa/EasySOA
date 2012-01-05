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
