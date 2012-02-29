package org.easysoa.persistence.filesystem;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.CharBuffer;
import org.apache.log4j.Logger;
import org.easysoa.persistence.StoreItf;
import org.easysoa.persistence.StoreResource;

/**
 * Implementation of StoreItf for file system persistence
 * 
 * @author jguillemotte
 *
 */
public class FileStore implements StoreItf {

    // Path separator
    public static final String SEPARATOR = "/";
    
    // Logger
    private static Logger logger = Logger.getLogger(FileStore.class.getName());    
    
    @Override
    public void createStore(String storeName) throws Exception {
        logger.debug("Creating store in file system : " + storeName);
        File storeFolder = new File(storeName);
        storeFolder.mkdirs();
    }

    @Override
    public String save(StoreResource resource) throws Exception {
        // check if store exists
        if(!storeExists(resource.getStoreName())){
            createStore(resource.getStoreName());
        }
        File resourceFile = new File(resource.getStoreName() + SEPARATOR + resource.getResourceName());
        FileWriter resourceFileWriter = new FileWriter(resourceFile);
        try{
            resourceFileWriter.write(resource.getContent());
        }
        finally{
            resourceFileWriter.close();        
        }
        return null;
    }

    @Override
    public StoreResource load(String resourceName, String storeName) throws Exception {
        /*if(!storeExists(storeName)){
            throw new Exception("the resource cannot be loaded because the store " + storeName + " does not exist");
        }*/
        StoreResource resource = new StoreResource(resourceName);
        resource.setStoreName(storeName);
        File file = new File(storeName + SEPARATOR + resourceName);
        FileReader fr = new FileReader(file);
        try {
            CharBuffer buffer = CharBuffer.allocate(512);
            StringBuffer stringBuffer = new StringBuffer();
            while( fr.read(buffer) >= 0 ) {
                stringBuffer.append(buffer.flip());
                buffer.clear();         
            }
            stringBuffer.trimToSize();
            resource.setContent(stringBuffer.toString());
        }
        finally {
            if(fr != null){
                fr.close();
            }
        }
        return resource;
    }

    /**
     * Check if a store exists in the file system
     * @param storeName The store name to check
     * @return true if the store exists, false otherwise
     */
    private boolean storeExists(String storeName){
        logger.debug("checking if store exists : " + storeName);
        if(storeName == null){
            return false;
        }
        File storeFile = new File(storeName);
        if(storeFile.exists()){
            return true;
        } else {
            return false;
        }
    }
    
}
