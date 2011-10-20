/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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
 * //@Jetty(config="/home/mdutoo/dev/easysoa/nuxeo-dm-5.3.2-jetty/config/jetty.xml") //,port=6088 // TODO OR EasySOAConstants.NUXEO_TEST_PORT
 * 
 * @author mdutoo
 *
 */
@Deploy({
    "org.easysoa.registry.core",
    "org.nuxeo.runtime.jetty"
})
@Features(/*webenginefeature TODO move in -rest and rename *Rest* */ NuxeoFeatureBase.class)
public class EasySOAServerFeature extends SimpleFeature {
}
