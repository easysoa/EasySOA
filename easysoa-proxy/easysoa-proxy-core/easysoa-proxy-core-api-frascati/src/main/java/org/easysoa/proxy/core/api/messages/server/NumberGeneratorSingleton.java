package org.easysoa.proxy.core.api.messages.server;

import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.records.persistence.filesystem.ProxyFileStore;

/**
 * Number generator singleton
 * @author jguillemotte
 *
 */
public class NumberGeneratorSingleton {

    private Logger logger = Logger.getLogger(NumberGeneratorSingleton.class.getName());
    
    private static NumberGeneratorSingleton generatorInstance = null;
    
    private long currentMessageNumber;
    
    private ProxyFileStore fileStore;
    
    /**
     * Constructor
     */
    private NumberGeneratorSingleton(){
        fileStore = new ProxyFileStore();
        try {
            this.currentMessageNumber = fileStore.getExchangeNumber();
        }
        catch(Exception ex){
            logger.warn("Unable to get the Exchange number counter from data file, counter value is set to 0", ex);
            currentMessageNumber = 0;
        }
    }
    
    /**
     * Returns the singleton instance of NumberGeneratorSingleton
     * @return The singleton instance
     */
    public static NumberGeneratorSingleton getInstance(){
        if(generatorInstance == null){
            generatorInstance = new NumberGeneratorSingleton();
        }
        return generatorInstance;
    }
    
    /**
     * Returns the next exchange number
     * @return the next exchange number
     */
    public synchronized long getNextNumber(){
        this.currentMessageNumber++;
        try {
            fileStore.saveExchangeNumber(this.currentMessageNumber);
        }
        catch(Exception ex){
            logger.warn("Unable to save the Exchange number counter in data file, counter value will be set to 0 at next startup", ex);
        }
        return this.currentMessageNumber;
    }
    
}
