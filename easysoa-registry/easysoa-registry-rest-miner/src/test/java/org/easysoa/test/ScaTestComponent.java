/**
 * 
 */
package org.easysoa.test;

import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * @author jguillemotte
 *
 */
public interface ScaTestComponent {

    public String testMethod(UserManager userManager) throws Exception;
    
}
