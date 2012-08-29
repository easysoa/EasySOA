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

	/**
	 * Returns the store list from the specified path
	 * @param path The path where to get the store list
	 * @return A list containing the store names
	 */
    public List<String> getStoreList(String path);

    /**
     * Returns a list of resources contained in the specified store
     * @return A list containing <code>StoreResource</code> objects
     * @throws Exception 
     */
    public List<StoreResource> getResourceList(String store) throws Exception;
	
    /**
     * Creates a recording lock
     * @param storeName The store name where to create the lock
     * @throws Exception
     */
    public void createRecordingLock(String storeName) throws Exception;
    
    /**
     * Remove a recording lock
     * @param storeName The store name where to delete the lock
     * @throws Exception
     */
    public void removeRecordingLock(String storeName) throws Exception;
    
    /**
     * Check if there is a recording lock
     * @param storeName The store name where to check for a lock
     * @return true if there is a recording lock, false otherwise
     */
    public boolean checkRecordingLock(String storeName);
    
    /**
     * Waits for the recording store is deleted. If the recording store is not deleted before the timeout is finished, the method throws an exception
     * @param storeName The store name to wait for the recording lock is deleted
     * @param timeout in ms
     * @throws Exception if the timeout is reached before the lock is deleted
     */
    public void waitForRecordingLock(String storeName, long timeout) throws Exception;
    
}
