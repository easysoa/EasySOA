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

import org.easysoa.api.exceptions.PropertyNotFoundException;
import org.easysoa.api.exceptions.RepositoryAccessException;
import org.easysoa.api.exceptions.SchemaNotFoundException;
import org.nuxeo.ecm.automation.client.model.Document;

/**
 *
 * @author mkalam-alami
 *
 */
public class EasySOARemoteDocument implements EasySOADocument {

    private Document document;

    public EasySOARemoteDocument(Document document) {
        this.document = document;
    }

    @Override
    public String getId() throws RepositoryAccessException {
        return document.getId();
    }

    @Override
    public String getTitle() throws PropertyNotFoundException, RepositoryAccessException {
        return document.getTitle();
    }

    @Override
    public String getType() throws RepositoryAccessException {
        return document.getType();
    }

    @Override
    public String getPath() throws RepositoryAccessException {
        return document.getPath();
    }

    @Override
    public Map<String, Object> getAllProperties() throws SchemaNotFoundException, RepositoryAccessException {
        return document.getProperties().map();
    }

    @Override
    public Object getProperty(String key) throws PropertyNotFoundException, RepositoryAccessException {
        Object value = document.getString(key);
        if (value == null) {
            throw new PropertyNotFoundException("XPath '" + key +"' don't match a property");
        }
        else {
            return value;
        }
    }

    @Override
    public String getPropertyAsString(String key) throws PropertyNotFoundException, RepositoryAccessException, ClassCastException {
        String value = document.getString(key);
        if (value == null) {
            throw new PropertyNotFoundException("XPath '" + key +"' don't match a property");
        }
        else {
            return value;
        }
    }

    @Override
    public Long getPropertyAsLong(String key) throws PropertyNotFoundException, RepositoryAccessException, ClassCastException {
        Long value = document.getLong(key);
        if (value == null) {
            throw new PropertyNotFoundException("XPath '" + key +"' don't match a property");
        }
        else {
            return value;
        }
    }

}
