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
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;

/**
 * 
 * @author mkalam-alami
 *
 */
public class EasySOALocalDocument implements EasySOADocument {
    
    private DocumentModel model;
    
    public EasySOALocalDocument(DocumentModel model) throws RepositoryAccessException {
        if (model == null) {
            throw new RepositoryAccessException("Model must not be null");
        }
        this.model = model;
    }
    
    @Override
    public String getId() throws RepositoryAccessException {
        return model.getId();
    }

    @Override
    public String getTitle() throws PropertyNotFoundException, RepositoryAccessException {
        try {
            return model.getTitle();
        } catch (PropertyException e) {
            throw new PropertyNotFoundException("Cannot get document title from model", e);
        } catch (Exception e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public String getType() throws RepositoryAccessException {
        return model.getType();
    }

    @Override
    public String getPath() throws RepositoryAccessException {
        return model.getPathAsString();
    }

    @Override
    public Map<String, Object> getAllProperties() throws SchemaNotFoundException, RepositoryAccessException {
        throw new UnsupportedOperationException(); // TODO Implement
        /*try {
            Map<String, Object> properties = model.getProperties(XXXX);
        } catch (ClientException e) {
            throw new DocumentAccessException(e);
        }*/
    }

    @Override
    public Object getProperty(String key) throws PropertyNotFoundException, RepositoryAccessException {
        try {
            Object value = model.getProperty(key);
            if (value == null) {
                throw new PropertyNotFoundException("Property '" + key + "' cannot be found");
            }
            else {
                return value;
            }
        } catch (ClientException e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public Long getPropertyAsLong(String key) throws PropertyNotFoundException, RepositoryAccessException, ClassCastException {
        return (Long) getProperty(key);
    }

    @Override
    public String getPropertyAsString(String key) throws PropertyNotFoundException, RepositoryAccessException {
        return (String) getProperty(key);
    }
    

}
