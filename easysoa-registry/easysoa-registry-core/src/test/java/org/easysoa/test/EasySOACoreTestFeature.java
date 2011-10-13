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
 * Contact : easysoa-dev@groups.google.com
 */

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
