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
    "org.nuxeo.ecm.directory.types.contrib", // required, else no vocabulary schema in database
    "org.nuxeo.ecm.directory",
    "org.nuxeo.ecm.directory.sql",
    "org.nuxeo.ecm.directory.api", // all required, else no dirService
    "org.nuxeo.ecm.platform.types.core"
})
@Features(CoreFeature.class)
public class EasySOAFeatureBase extends SimpleFeature {
}
