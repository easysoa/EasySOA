/**
 * 
 */
package org.easysoa.proxy.registry;

import java.io.File;
import java.net.URL;

import org.easysoa.proxy.core.api.configuration.EasySOAGeneratedAppConfiguration;

/**
 * @author jguillemotte
 *
 */
public class Template {

    // Contains data to generate composites file for FraSCAti studio apps
    
    /**
     * Build a template object from a URL 
     * @param url
     */
    public Template(URL url){
        
    }
    
    /**
     * Build a template object from a file
     * @param file
     */
    public Template(File file){
        
    }
    
    /**
     * 
     * @return
     */
    public String generateComposite(EasySOAGeneratedAppConfiguration configuration) {
        
        // generate a composite file for the app, using the provided configuration to replace templatized parameters
        
        return "";
    }
    
}
