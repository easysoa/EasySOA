package org.easysoa.records;

import java.util.ArrayDeque;

/**
 * Class to store a collection of ExchangeRecords
 * @author jguillemotte
 *
 */
@SuppressWarnings("serial")
public class ExchangeRecordStore extends ArrayDeque<ExchangeRecord> {
	
	//
	private String storeName;
	
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
