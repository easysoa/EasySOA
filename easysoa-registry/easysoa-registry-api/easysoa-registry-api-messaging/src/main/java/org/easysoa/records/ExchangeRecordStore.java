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
	
	//
	private String storeName;
	
	public ExchangeRecordStore(){
		this.storeName = "";
	}
	
	/**
	 * 
	 * @param storeName
	 */
	public ExchangeRecordStore(String storeName){
		this.storeName = storeName;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	
	
}
