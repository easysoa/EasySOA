package org.easysoa.test;

import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.runtime.test.runner.SimpleFeature;


/**
 * Allows for easy testing of EasySOA Core non-UI code
 * see http://doc.nuxeo.com/display/CORG/Unit+Testing
 * 
 * @author mdutoo
 *
 */
@Deploy({
    "org.nuxeo.runtime.datasource",
	"org.easysoa.demo.core:OSGI-INF/nxdirectories-contrib.xml", // required, else no custom easysoa vocabularies
    "org.easysoa.demo.core:OSGI-INF/core-type-contrib.xml", // required, else no custom types
	"org.easysoa.demo.core:OSGI-INF/DocumentServiceComponent.xml", // required to find the service through the Framework class
	"org.easysoa.demo.core:OSGI-INF/NotificationServiceComponent.xml", // idem
	"org.easysoa.demo.core:OSGI-INF/VocabularyHelperComponent.xml", // idem
	"org.easysoa.demo.core:OSGI-INF/EasySOAInitComponent.xml", // required by the contribution below
	"org.easysoa.demo.core:OSGI-INF/eventlistener-contrib.xml" // required to enable the specific doctype listeners
})
@Features(EasySOAFeatureBase.class)
@LocalDeploy("org.easysoa.demo.core:org/easysoa/tests/datasource-contrib.xml")
public class EasySOAFeature extends SimpleFeature {
}
