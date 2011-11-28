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
