package org.easysoa.proxy.core.api.run;

import org.apache.log4j.Logger;

/**
 * Run name checker
 * @author jguillemotte
 *
 */
public class RunNameChecker {

    // Logger
    private Logger logger = Logger.getLogger(RunNameChecker.class.getName());
    
    // Singleton
    private static RunNameChecker checkerInstance = null;    
    
    /**
     * Returns the singleton instance of NumberGeneratorSingleton
     * @return The singleton instance
     */
    public static RunNameChecker getInstance() {
        if(checkerInstance == null){
            checkerInstance = new RunNameChecker();
        }
        return checkerInstance;
    }
    
    /**
     * 
     * @param runName
     * @return
     */
    public boolean checkUniqueRunName(String runName) {

        // Get the registered runs name in store folder
        
        // how to have access to the run names of all run manager instances ???? (get a list at startup ?)
        // add a nameRegister method ??
        // in the case the run is deleted but not saved => how to remove the unused run name ??
        
        return false;
    }
    
}
