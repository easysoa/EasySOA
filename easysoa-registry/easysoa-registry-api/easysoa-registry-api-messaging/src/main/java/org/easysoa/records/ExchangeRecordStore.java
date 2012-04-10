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
