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
    "org.easysoa.demo.core",
    "org.nuxeo.ecm.directory.types.contrib:OSGI-INF/DirectoryTypes.xml", // required, else no vocabulary schema in database
    "org.nuxeo.ecm.directory",
    "org.nuxeo.ecm.directory.api",
    "org.nuxeo.ecm.directory.sql", // all required, else no dirService
    "org.easysoa.demo.core:OSGI-INF/nxdirectories-contrib.xml", // required, else no custom easysoa vocabularies
    "org.easysoa.demo.core:OSGI-INF/core-type-contrib.xml", // required, else no custom types
	"org.easysoa.demo.core:OSGI-INF/DocumentServiceComponent.xml", // required to find the service through the Framework class
	"org.easysoa.demo.core:OSGI-INF/NotificationServiceComponent.xml", // idem
	"org.easysoa.demo.core:OSGI-INF/VocabularyHelperComponent.xml", // idem
	"org.easysoa.demo.core:OSGI-INF/EasySOAInitComponent.xml", // required by the contribution below
	"org.easysoa.demo.core:OSGI-INF/eventlistener-contrib.xml" // required to enable the specific doctype listeners
})
@Features(CoreFeature.class)
public class EasySOAFeatureBase extends SimpleFeature {
}
