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

package org.easysoa.api;

import java.util.Map;

import org.easysoa.api.exceptions.RepositoryAccessException;
import org.easysoa.api.exceptions.PropertyNotFoundException;
import org.easysoa.api.exceptions.SchemaNotFoundException;

/**
 * Read-only representation of a document.
 * 
 * @author mkalam-alami
 *
 */
public interface EasySOADocument {

    /**
     * 
     * @return The UUID
     * @throws RepositoryAccessException
     */
    String getId() throws RepositoryAccessException;

    /**
     * 
     * @return The title
     * @throws PropertyNotFoundException
     * @throws RepositoryAccessException
     */
    String getTitle() throws PropertyNotFoundException, RepositoryAccessException;

    /**
     * 
     * @return The document type
     * @throws RepositoryAccessException
     */
    String getType() throws RepositoryAccessException;

    /**
     * 
     * @return The full path as a string
     * @throws RepositoryAccessException
     */
    String getPath() throws RepositoryAccessException;

    /**
     * 
     * @return All properties as a set of keys (in XPath format) and their values
     * @throws SchemaNotFoundException
     * @throws RepositoryAccessException
     */
    Map<String, Object> getAllProperties() throws SchemaNotFoundException, RepositoryAccessException;

    /**
     * Returns a specific property value given an XPath key (ex: "dc:title")
     * @param key
     * @return
     * @throws PropertyNotFoundException
     * @throws RepositoryAccessException
     */
    Object getProperty(String key) throws PropertyNotFoundException, RepositoryAccessException;

    /**
     * Returns a specific property value given an XPath key (ex: "dc:title"), as a String
     * @param key
     * @return
     * @throws PropertyNotFoundException
     * @throws RepositoryAccessException
     */
    String getPropertyAsString(String key) throws PropertyNotFoundException, RepositoryAccessException, ClassCastException;

    /**
     * Returns a specific property value given an XPath key (ex: "dc:title"), as a Long
     * @param key
     * @return
     * @throws PropertyNotFoundException
     * @throws RepositoryAccessException
     */
    Long getPropertyAsLong(String key) throws PropertyNotFoundException, RepositoryAccessException, ClassCastException;

}
