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
    "org.nuxeo.runtime.datasource", // required, else errors : [DataSourceHelper] Unknown JNDI Context class: null , [SQLDirectory] dataSource lookup failed , "javax.naming.NoInitialContextException: Need to specify class name...
})
@Features(EasySOAFeatureBase.class)
@LocalDeploy("org.easysoa.demo.core:org/easysoa/tests/datasource-contrib.xml")
public class EasySOAFeature extends SimpleFeature {
}
