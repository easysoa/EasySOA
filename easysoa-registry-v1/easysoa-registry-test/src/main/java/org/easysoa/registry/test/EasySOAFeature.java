package org.easysoa.registry.test;

import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.runtime.test.runner.SimpleFeature;

/**
 * 
 * @author mkalam-alami
 * 
 */
@Deploy({
    "org.easysoa.registry.test",
    
    // Relations support
    "org.nuxeo.ecm.relations.api",
    "org.nuxeo.ecm.relations",
    "org.nuxeo.ecm.relations.jena",
    
    // Needed by the DocumentModelHelper to fetch doctype UI names
    "org.nuxeo.ecm.platform.types.api",
    "org.nuxeo.ecm.platform.types.core",
    
    // Minimal EasySOA requirements
    "org.easysoa.registry.doctypes.api",
    "org.easysoa.registry.doctypes.core"
})
@LocalDeploy(
    // Default graph contribution
    "org.easysoa.registry.test:OSGI-INF/test-relation-jena-contrib.xml"
)
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
