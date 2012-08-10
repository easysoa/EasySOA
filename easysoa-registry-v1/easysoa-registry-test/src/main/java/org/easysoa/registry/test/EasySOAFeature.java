package org.easysoa.registry.test;

import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;

/**
 * 
 * @author mkalam-alami
 * 
 */
@Deploy({
    // Needed by the DocumentModelHelper
    "org.nuxeo.ecm.platform.types.api",
    "org.nuxeo.ecm.platform.types.core",
    
    // Minimal EasySOA requirements
    "org.easysoa.registry.doctypes.core"
})
public class EasySOAFeature extends CoreFeature {

}
