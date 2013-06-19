/**
 *
 */
package org.easysoa.proxy.strategy;

/**
 * @author jguillemotte
 *
 */
public class PerUserEasySOAGeneratedAppIdFactoryStrategy implements  EasySOAGeneratedAppIdFactoryStrategy {

    /**
     * Build an ID with the provided parameters
     * @param user Easysoa user
     * @param projectId Project ID
     * @param componentIds Components ID
     * @return The generated ID
     */
    // TODO : complete this method
    public String getId(String user, String projectId, String componentIds){
        StringBuilder builder = new StringBuilder();
        builder.append(user);
        builder.append("_");
        builder.append(projectId);
        //buffer.append("_");
        //buffer.append(componentIds);
        return builder.toString();
    }

}
