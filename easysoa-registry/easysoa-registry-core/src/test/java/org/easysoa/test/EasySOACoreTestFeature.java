package org.easysoa.test;

import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.runtime.test.runner.SimpleFeature;


/**
 * Allows for easy testing of EasySOA Core non-UI code within EasySOA Core tests
 * see http://doc.nuxeo.com/display/CORG/Unit+Testing
 * 
 * @author mdutoo
 *
 */
@Deploy({
    "org.easysoa.registry.core",
    "org.easysoa.registry.core:OSGI-INF/vocabularies-contrib.xml", // required, else no custom easysoa vocabularies,
    "org.easysoa.registry.core:OSGI-INF/DocumentServiceComponent.xml", // required to find the service through the Framework class
    "org.easysoa.registry.core:OSGI-INF/NotificationServiceComponent.xml", // idem
    "org.easysoa.registry.core:OSGI-INF/VocabularyHelperComponent.xml", // idem
    "org.easysoa.registry.core:OSGI-INF/core-type-contrib.xml", // required, else no custom types
    "org.easysoa.registry.core:OSGI-INF/EasySOAInitComponent.xml", // required by the contribution below
    "org.easysoa.registry.core:OSGI-INF/eventlistener-contrib.xml", // required to enable the specific doctype listeners
    "org.nuxeo.runtime.datasource"
})
@Features(NuxeoFeatureBase.class)
@LocalDeploy("org.easysoa.registry.core:org/easysoa/tests/datasource-contrib.xml")
public class EasySOACoreTestFeature extends SimpleFeature {
}
