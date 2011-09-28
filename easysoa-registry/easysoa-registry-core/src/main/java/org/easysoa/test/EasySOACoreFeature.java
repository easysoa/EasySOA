package org.easysoa.test;

import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.SimpleFeature;


/**
 * Allows for easy testing of EasySOA Core non-UI code (outside of EasySOA Core tests)
 * see http://doc.nuxeo.com/display/CORG/Unit+Testing
 * 
 * @author mdutoo
 *
 */
@Deploy({
    "org.easysoa.registry.core",
    "org.nuxeo.ecm.actions", // required by easysoa-registry-core contrib
    "org.nuxeo.ecm.platform.forms.layout.client", // required by easysoa-registry-core contrib
    "org.nuxeo.ecm.platform.query.api" // required by usermanager's PageProvider
})
@Features(NuxeoFeatureBase.class)
public class EasySOACoreFeature extends SimpleFeature {
}
