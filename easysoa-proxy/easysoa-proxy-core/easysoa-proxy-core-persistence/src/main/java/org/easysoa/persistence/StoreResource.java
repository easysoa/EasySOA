/**
 * EasySOA Proxy
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
package org.easysoa.persistence;

/**
 * Generic store resource
 * 
 * @author jguillemotte
 *
 */
public class StoreResource {

    // TODO use a String directly or an object with calling the toString method ??
    private String content;
    
    // the store where to save or load the resource
    private String storeName;
    
    // Resource name
    private String resourceName;
    
    /**
     *
     * @param resourceName The name of the resource. In case of file system persistence, the name must be the name of the physical file (including extension)
     */
    public StoreResource(String resourceName){
        this(resourceName, "", "");
    }

    /**
     * 
     * @param resourceName The name of the resource. In case of file system persistence, the name must be the name of the physical file (including extension)
     * @param storeName The store where to load or save the resource. 
     * In case of file system persistence, the store must be a relative or absolute path using the common '/' separator.
     * The store name will be the token after the last '/' separator 
     */
    public StoreResource(String resourceName, String storeName){
        this(resourceName, storeName, "");
    }    
    
    /**
     * 
     * @param resourceName The name of the resource. In case of file system persistence, the name must be the name of the physical file (including extension)
     * @param storeName The store where to load or save the resource. 
     * In case of file system persistence, the store must be a relative or absolute path using the common '/' separator.
     * The store name will be the token after the last '/' separator
     * @param content The resource content
     */
    public StoreResource(String resourceName, String storeName, String content){
        this.resourceName = resourceName;
        this.storeName = storeName;
        this.content = content;
    }
    
    /**
     * 
     * @return
     */
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
    
    /**
     * Returns a string representation of StoreResource object
     */
    public String toString(){
       StringBuffer buffer = new StringBuffer();
       buffer.append("StoreResource(name:");
       buffer.append(this.resourceName);
       buffer.append(", storename:");
       buffer.append(this.storeName);
       buffer.append(", content:");
       buffer.append(this.content);
       return buffer.toString();
    }
    
}
