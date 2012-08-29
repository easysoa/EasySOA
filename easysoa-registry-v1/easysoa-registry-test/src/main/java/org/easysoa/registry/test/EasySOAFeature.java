package org.easysoa.registry.test;

import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.SimpleFeature;

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
    "org.easysoa.registry.doctypes.api",
    "org.easysoa.registry.doctypes.core"
})
public class EasySOAFeature extends SimpleFeature {

//    private static Logger logger = Logger.getLogger(EasySOAFeature.class);
//
//    @Override
//    public void configure(FeaturesRunner runner, Binder binder) {
//        super.configure(runner, binder);
//        
//        // Check manifest (invalid characters - like tabs? - can cause injection issues & missing contributions) 
//        RuntimeFeature runtimeFeature = runner.getFeature(RuntimeFeature.class);
//        for (String deployment : runtimeFeature.getDeployments()) {
//            if (deployment.contains("easysoa")) {
//                Bundle easysoaBundle = runtimeFeature.getHarness().getOSGiAdapter().getBundle(deployment);
//                if (easysoaBundle != null) {
//                    Object nuxeoComponentField = easysoaBundle.getHeaders().get("Nuxeo-Component");
//                    if (nuxeoComponentField == null) {
//                        logger.warn("No Nuxeo-Component entry has been found in the manifest of '" + deployment  + "'. " +
//                        		"Unless this is intended, there must be some invalid characters in your Manifest.");
//                    }
//                }
//                else {
//                    logger.error("Bundle " + deployment + " has not been deployed");
//                }
//            }
//        }
//    }
    
}
