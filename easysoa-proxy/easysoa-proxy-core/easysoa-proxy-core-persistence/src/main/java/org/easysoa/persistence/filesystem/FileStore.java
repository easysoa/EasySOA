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

package org.easysoa.persistence.filesystem;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

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

    // Recording lock file name
    public final static String RECORDING_LOCK_FILE_NAME = "recording.lock";
    
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
    public List<String> getStoreList(String path){
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        List<String> storeList = new ArrayList<String>();
        if(listOfFiles != null){
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    storeList.add(file.getName());
                }
            }
        }
        return storeList;
    }
    
    @Override
    public List<StoreResource> getResourceList(String storeName) throws Exception{
        File folder = new File(storeName);
        File[] listOfFiles = folder.listFiles();
        ArrayList<StoreResource> resourceList = new ArrayList<StoreResource>();
        if(listOfFiles != null){
            for (File file : listOfFiles) {
                if (file.isFile()) {        
                    resourceList.add(this.load(file.getName(), storeName));
                }
            }
        }
        return resourceList;
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

    @Override
    public void createRecordingLock(String storeName) throws Exception {
        File recordingLock = new File(storeName + SEPARATOR + RECORDING_LOCK_FILE_NAME);
        FileWriter resourceFileWriter = new FileWriter(recordingLock);
        try{
            resourceFileWriter.write("The store " + storeName + " is currently locked, a writing operation is in process. This lock file will be removed automatically when the writing process will be terminated.");
        }
        finally{
            resourceFileWriter.close();        
        }
    }

    @Override
    public void removeRecordingLock(String storeName) throws Exception {
        File recordingLock = new File(storeName + SEPARATOR + RECORDING_LOCK_FILE_NAME);
        if(recordingLock.exists()){
            recordingLock.delete();
        }
    }

    @Override
    public boolean checkRecordingLock(String storeName) {
        File recordingLock = new File(storeName + SEPARATOR + RECORDING_LOCK_FILE_NAME);
        return recordingLock.exists();
    }

    @Override
    public void waitForRecordingLock(String storeName, long timeout) throws Exception {
        File recordingLock = new File(storeName + SEPARATOR + RECORDING_LOCK_FILE_NAME);
        long currentTime = System.currentTimeMillis();
        while(recordingLock.exists()){
            long actualTime = System.currentTimeMillis();
            // CHeck the timeout
            if(actualTime - currentTime >= timeout){
                throw new TimeoutException("Timeout reached while waiting for recording lock been deleted");
            } else {
                Thread.sleep(500);
            }
        }
    }
    
}
