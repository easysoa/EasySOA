package org.easysoa.test;

import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.SimpleFeature;


/**
 * Allows for easy testing of EasySOA Core non-UI code
 * see http://doc.nuxeo.com/display/CORG/Unit+Testing
 * 
 * @author mdutoo
 *
 */
@Deploy({
    "org.easysoa.registry.core",
	"org.easysoa.registry.core:OSGI-INF/nxdirectories-contrib.xml", // required, else no custom easysoa vocabularies,
	"org.easysoa.registry.core:OSGI-INF/DocumentServiceComponent.xml", // required to find the service through the Framework class
	"org.easysoa.registry.core:OSGI-INF/NotificationServiceComponent.xml", // idem
	"org.easysoa.registry.core:OSGI-INF/VocabularyHelperComponent.xml", // idem
    "org.easysoa.registry.core:OSGI-INF/core-type-contrib.xml", // required, else no custom types
	"org.easysoa.registry.core:OSGI-INF/EasySOAInitComponent.xml", // required by the contribution below
	"org.easysoa.registry.core:OSGI-INF/eventlistener-contrib.xml", // required to enable the specific doctype listeners
    "org.nuxeo.ecm.directory.types.contrib", // required, else no vocabulary schema in database
    "org.nuxeo.ecm.directory",
    "org.nuxeo.ecm.directory.sql",
    "org.nuxeo.ecm.directory.api", // all required, else no dirService
    "org.nuxeo.ecm.platform.types.core"
})
@Features(CoreFeature.class)
public class EasySOAFeatureBase extends SimpleFeature {
}
