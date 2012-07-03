/**
 * 
 */
package org.easysoa.test;

import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * @author jguillemotte
 *
 */
public class ScaTestComponentImpl implements ScaTestComponent {

    public String testMethod(UserManager userManager) throws Exception {
        return String.valueOf(userManager.getUserIds().size());
    }

}
