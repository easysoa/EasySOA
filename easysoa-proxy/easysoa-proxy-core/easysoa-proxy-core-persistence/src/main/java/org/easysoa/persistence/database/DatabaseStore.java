/**
 * 
 */
package org.easysoa.persistence.database;

import java.util.List;

import org.easysoa.persistence.StoreItf;
import org.easysoa.persistence.StoreResource;

/**
 * Not used ate the moment.
 * 
 * @author jguillemotte
 *
 */
public class DatabaseStore implements StoreItf {

    @Override
    public void createStore(String storeName) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String save(StoreResource resource) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StoreResource load(String resourceName, String store) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getStoreList(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<StoreResource> getResourceList(String store) {
        // TODO Auto-generated method stub
        return null;
    }

}
