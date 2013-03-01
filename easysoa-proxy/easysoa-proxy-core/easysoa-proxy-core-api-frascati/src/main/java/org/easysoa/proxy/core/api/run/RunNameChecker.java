/**
 * EasySOA Proxy Copyright 2011 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */
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
