package org.easysoa.records;

import java.util.ArrayDeque;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class to store a collection of ExchangeRecords
 * @author jguillemotte
 *
 */
@SuppressWarnings("serial")
@XmlRootElement
public class ExchangeRecordStore extends ArrayDeque<ExchangeRecord> {
	
	// Store name
	private String storeName;
	// Environment
	private String environment;
	
	/**
	 * 
	 */
	public ExchangeRecordStore(){
		this("", "");
	}
	
	/**
	 * 
	 * @param storeName
	 */
	public ExchangeRecordStore(String storeName){
		this(storeName, "");
	}
	
	/**
	 * 
	 * @param storeName
	 * @param environment
	 */
	public ExchangeRecordStore(String storeName, String environment){
		super();
		this.storeName = storeName;
		this.environment = environment;		
	}
	
	/**
	 * 
	 * @return
	 */
	public String getStoreName() {
		return storeName;
	}

	/**
	 * 
	 * @param storeName
	 */
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	
    /**
     * 
     * @return
     */
	public String getEnvironment() {
		return environment;
	}

	/**
	 * 
	 * @param environment
	 */
	public void setEnvironment(String environment) {
		this.environment = environment;
	}	
	
}
