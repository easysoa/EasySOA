package org.easysoa.api;

import java.util.Map;

import org.easysoa.api.exceptions.RepositoryAccessException;
import org.easysoa.api.exceptions.PropertyNotFoundException;
import org.easysoa.api.exceptions.SchemaNotFoundException;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface EasySOADocument {

    String getId() throws RepositoryAccessException;

    String getTitle() throws PropertyNotFoundException, RepositoryAccessException;

    String getType() throws RepositoryAccessException;

    String getPath() throws RepositoryAccessException;

    Map<String, Object> getAllProperties() throws SchemaNotFoundException, RepositoryAccessException;

    Object getProperty(String key) throws PropertyNotFoundException, RepositoryAccessException;
    
    String getPropertyAsString(String key) throws PropertyNotFoundException, RepositoryAccessException, ClassCastException;

    Long getPropertyAsLong(String key) throws PropertyNotFoundException, RepositoryAccessException, ClassCastException;

}
