package org.easysoa.registry;

import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;

@Deploy({ "org.easysoa.registry.core.doctypes",
    "org.nuxeo.ecm.platform.types.api" })
public class EasySOADoctypesFeature extends CoreFeature {

}
