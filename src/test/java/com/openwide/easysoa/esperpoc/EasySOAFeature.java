package com.openwide.easysoa.esperpoc;

import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.SimpleFeature;

/**
 * Allows for easy testing of EasySOA Core non-UI code
 * see http://doc.nuxeo.com/display/CORG/Unit+Testing
 */
@Deploy({
    "org.easysoa.demo.core"
})
@Features(CoreFeature.class)
public class EasySOAFeature extends SimpleFeature {

}
