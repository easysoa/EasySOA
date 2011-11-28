package org.easysoa.api;

import java.util.Map;

import org.easysoa.api.exceptions.RepositoryAccessException;
import org.easysoa.api.exceptions.PropertyNotFoundException;
import org.easysoa.api.exceptions.SchemaNotFoundException;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

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
