/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.core.api.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jguillemotte
 */
public interface GenericTemplateRendererItf {
    
    /**
     * The template result
     * @param templateName The path + name of the template to execute
     * @param storeName
     * @param argMap
     * @return 
     */
    public String execute_custom(String templateName, String storeName, Map<String, List<String>> operationList, HashMap<String, Map<String, List<AbstractTemplateField>>> templateFields);
    
}
