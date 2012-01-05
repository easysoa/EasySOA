/**
 * 
 */
package org.easysoa.records;

import java.util.ArrayDeque;

/**
 * This classe store exchange records without persistence.
 * @author jguillemotte
 */
public class ExchangeRecordStoreArray extends ArrayDeque<ExchangeRecord>  {

	private static final long serialVersionUID = 7771363932618846658L;
	
	private String environment;
	
	// List to record Exchange Records
	// TODO : record an object which contains an ID and the ExchangeRecord
    //private ArrayDeque<ExchangeRecord> recordArray;
    
	/**
	 * 
	 * @param environment Execution environment
	 */
    public ExchangeRecordStoreArray(String environment){
    	super();
    	this.setEnvironment(environment);
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
