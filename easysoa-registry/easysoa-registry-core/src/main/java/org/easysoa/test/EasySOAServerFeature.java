package org.easysoa.test;

import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.SimpleFeature;


/**
 * Allows for easy testing of EasySOA Core non-UI code
 * see http://doc.nuxeo.com/display/CORG/Unit+Testing
 * 
 * Use it by annotating a junit test class, ex. :
 * 
 * @RunWith(FeaturesRunner.class)
 * @Features(EasySOAServerFeature.class)
 * @Jetty(port=EasySOAConstants.NUXEO_TEST_PORT)
 * //@Jetty(config="/home/mdutoo/dev/easysoa/nuxeo-dm-5.3.2-jetty/config/jetty.xml") //,port=9980
 * 
 * @author mdutoo
 *
 */
@Deploy({
	"org.nuxeo.runtime.jetty"
})
@Features(/*webenginefeature TODO move in -rest*/ EasySOAFeatureBase.class)
public class EasySOAServerFeature extends SimpleFeature {
}
