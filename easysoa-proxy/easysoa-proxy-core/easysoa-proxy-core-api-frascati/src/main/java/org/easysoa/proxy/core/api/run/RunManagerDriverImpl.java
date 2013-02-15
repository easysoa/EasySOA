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

package org.easysoa.proxy.core.api.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.configurator.ProxyConfigurator;
import org.easysoa.proxy.core.api.properties.PropertyManager;
import org.easysoa.proxy.core.api.run.RunManager;
import org.easysoa.proxy.core.api.run.RunManagerDriver;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * HttpProxyDriver implementation
 * @author jguillemotte
 *
 */
@Scope("composite")
public class RunManagerDriverImpl implements RunManagerDriver {
    
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(RunManagerDriverImpl.class.getName());
	
    // Property manager
    public static PropertyManager propertyManager;	
	
	/**
	 * 	Reference on monitoring service 
	 */
	@Reference
	RunManager runManager;
	
	@Reference
	HTMLProcessorItf html;
	
	static {
		ProxyConfigurator.configure(RunManagerDriverImpl.class);
	}
	
    /**
     * Constructor
     */
    public RunManagerDriverImpl(){
        try {
            propertyManager = new PropertyManager("");
        } catch (Exception ex) {
            logger.warn("Error when loading the property manager, trying another method");
            try{
                propertyManager = new PropertyManager(PropertyManager.DEFAULT_PROPERTY_FILE_NAME, this.getClass().getResourceAsStream("/" + PropertyManager.DEFAULT_PROPERTY_FILE_NAME));                
            } catch(Exception exc){
                logger.error("Error when loading the property manager", exc);                
            }
        }
    }
	
    /**
     * return usage informations as HTML data
     * @return HTML formated usage informations
     */
	public String returnUseInformations(UriInfo ui) {
		logger.debug("Returning help informations");
		//StringBuffer help = new StringBuffer();
		// Work when proxy is executed in Frascati
		// But not when deployed as an App on frascati studio : not the same class loader
		//InputStream is = this.getClass().getResourceAsStream("/webContent/replayManagerIndex.html");
		// 
		//BufferedReader br = new BufferedReader(new InputStreamReader(is));
		/*try {
            while(br.ready()) {
                help.append(br.readLine() + '\n');
            }
        } catch (IOException e) {
            logger.error("Failed to read main page HTML data", e);
        }
		finally {
		    try {
                br.close();
            } catch (IOException e) {
                logger.error("Failed to close main page input stream", e);
            }
		}*/
		//return help.toString();
		return html.getRunManagerIndex();
	}

	/**
	 * Start a new run
	 * @param runName The run name
	 */
	public String startNewRun(String runName) {
		logger.debug("Starting a new run !");
		try{
			runManager.start(runName);
	        return "Run '" + runName + "' started !";
		} catch(Exception ex){
			logger.error("Unable to start a new run", ex);
			return ex.getMessage();
		}
	}

	/**
	 * Stop the current run
	 */
	public String stopCurrentRun() {
		logger.debug("Stopping the current run !");
		try {
			return runManager.stop();
			// Registering is now done in save method
			//runManager.getMonitoringService().registerDetectedServicesToNuxeo();
                        //return "Current run stopped !";
		}
		catch(Exception ex){
			logger.error("Unable to stop the current run", ex);
			return ex.getMessage();			
		}
	}

	
	@Deprecated
	// TODO Modify this method to change the monitoring mode
	// Method not used at the current time.
	public String setMonitoringMode(String mode) {
		logger.debug("Set monitoring mode !");
		try {
			if(mode == null || "".equals(mode)){
				throw new IllegalArgumentException("Monitoring mode must not be null or empty");
			}
			// Best method is to stop the http Proxy with FraSCAti, and then restart with the good composite file
	        return "Monitoring mode set";
		} catch(Exception ex){
			logger.error("Unable to set monitoring mode", ex);
			return ex.getMessage();
		}
	}

	/*@Override
	public String getOrderedRunNames() {
		StringBuffer result = new StringBuffer();
		int i = 1;
		for(String runName: runManager.getOrderedRunNames()){
			result.append("Run number " + i + ": ");
			result.append(runName);
			result.append("; ");
			i++;
		}
		return result.toString();
	}*/

	/*@Override
	public String reRun(String runName) {
		try{
			runManager.reRun(runName);
        return "Re-run done";
		}
		catch(Exception ex){
			logger.error("Unable to re-run the run '" + runName + "'", ex);
			return ex.getMessage();	
		}
	}*/

	/**
	 * Delete the current run
	 */
	public String delete() {
		try {
			runManager.delete();
	        return "Run deleted !";
		}
		catch(Exception ex){
			logger.error("Unable to delete the run", ex);
			return ex.getMessage();
		}
	}
	
	/**
	 * Save the current run
	 */
	public String save() throws Exception {
	    // Register and save at the same time, maybe best to have 2 separated methods ...
        try {
            String result = runManager.save();
            // Monitoring service is now an independent service
            // TODO : Have a dedicated web driver for monitoring service
            //runManager.getMonitoringService().registerDetectedServicesToNuxeo();
            return result;
        }
        catch(Exception ex){
            logger.error("Unable to save the run", ex);
            return ex.getMessage();
        }
	}

}
