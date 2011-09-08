package org.easysoa.test;

import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.SimpleFeature;


/**
 * To be used as a Nuxeo-deploying basis to build features 
 * allowing for easy testing of EasySOA
 * see http://doc.nuxeo.com/display/CORG/Unit+Testing
 * 
 * @author mdutoo
 *
 */
@Deploy({
    "org.nuxeo.ecm.directory.types.contrib", // required, else no vocabulary schema in database
    "org.nuxeo.ecm.directory",
    "org.nuxeo.ecm.directory.sql",
    "org.nuxeo.ecm.directory.api", // all required, else no dirService
    "org.nuxeo.ecm.platform.types.core",
    "org.nuxeo.ecm.core.convert.plugins"
})
@Features(CoreFeature.class)
public class NuxeoFeatureBase extends SimpleFeature {
}
