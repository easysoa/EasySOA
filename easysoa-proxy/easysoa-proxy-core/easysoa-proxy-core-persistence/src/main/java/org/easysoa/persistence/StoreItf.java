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
package org.easysoa.persistence;

import java.util.List;

// TODO : Modify the methods to have generic in/out parameters 

/**
 * @author jguillemotte
 */
public interface StoreItf {

	/**
	 * Save an exchange record
	 * @param exchangeRecord The <code>ExchangeRecord</code> to save
	 * @return The id of the stored <code>ExchangeRecord</code>
	 * @throws Exception if a problem occurs
	 */
	//public String save(ExchangeRecord exchangeRecord) throws Exception;
	
	/**
	 * Create a store
	 * @param storeName The store name
	 */
	public void createStore(String storeName) throws Exception;
	
	/**
	 * Save a resource and returns the result 
	 * @param the resource to save containing the file name, the store where the resource must be saved, and the content to save (in file, database ...)
	 * @return
	 */
	public String save(StoreResource resource) throws Exception;

	/**
	 * Load a ressource and returns the result
	 * @return The EasySOAResource filled with content, file name, store .... 
	 */
	public StoreResource load(String resourceName, String store) throws Exception;
	
	//public List<String> getFile
	
}
