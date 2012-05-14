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

    @Override
    public String testMethod(UserManager userManager) throws Exception {
        userManager.authenticate("administrator", "administrator");
        return userManager.getDefaultGroup();
    }

}
