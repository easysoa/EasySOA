/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.strategy;

/**
 *
 * @author jguillemotte
 */
public interface EasySOAGeneratedAppIdFactoryStrategy {

    /**
     *
     * @param user
     * @param projectId
     * @param componentIds
     * @return
     */
    public String getId(String user, String projectId, String componentIds);

}
