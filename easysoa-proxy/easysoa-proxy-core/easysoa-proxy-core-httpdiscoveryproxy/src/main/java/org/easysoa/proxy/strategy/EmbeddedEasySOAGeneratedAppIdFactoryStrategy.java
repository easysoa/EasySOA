/**
 * 
 */
package org.easysoa.proxy.strategy;

/**
 * @author jguillemotte
 *
 */
public class EmbeddedEasySOAGeneratedAppIdFactoryStrategy {

    /**
     * Build an ID with the provided parameters 
     * @param user Easysoa user
     * @param projectId Project ID
     * @param componentIds Components ID
     * @return The generated ID
     */
    // TODO : complete this method
    public String getId(String user, String projectId, String componentIds){
        StringBuffer buffer = new StringBuffer(); 
        buffer.append(user);
        buffer.append("_");
        buffer.append(projectId);
        //buffer.append("_");
        //buffer.append(componentIds);
        return buffer.toString();
    }
    
}
