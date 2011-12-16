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

package org.easysoa.sca.frascati;

import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.easysoa.test.EasySOACoreFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.services.resource.ResourceService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

/**
 * Tests EasySOA Core as required for further testing with FraSCAti
 * @author mdutoo
 *
 */

@RunWith(FeaturesRunner.class)
@Features(EasySOACoreFeature.class)
@Deploy({
	"org.nuxeo.runtime.datasource",
	"org.easysoa.registry.frascati", // deployed auto by dep
	"org.easysoa.registry.core",//, // deployed auto by dep
    "org.easysoa.registry.core:OSGI-INF/vocabularies-contrib.xml", // required, else no custom easysoa vocabularies,
    "org.easysoa.registry.core:OSGI-INF/DocumentServiceComponent.xml", // required to find the service through the Framework class
    "org.easysoa.registry.core:OSGI-INF/core-type-contrib.xml", // required, else no custom types
//    "org.easysoa.registry.core:OSGI-INF/EasySOAInitComponent.xml", // required by the contribution below
    "org.easysoa.registry.core:OSGI-INF/eventlistener-contrib.xml" // required to enable the specific doctype listeners
})
@LocalDeploy({
	"org.easysoa.registry.core:org/easysoa/tests/datasource-contrib.xml" // required because no jetty.naming in deps
})
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class EasySoaCoreTest
{

    static final Log log = LogFactory.getLog(EasySoaCoreTest.class);

    @Inject CoreSession session;

    @Inject DocumentService docService;
    
    @Inject ResourceService resourceService;
    
    DocumentModel parentAppliImplModel;
        
    @Test
    public void setUp() throws ClientException, MalformedURLException {
    	// Find or create appli
    	String appliUrl = "http://localhost";
		parentAppliImplModel = docService.findAppliImpl(session, appliUrl);
		if(parentAppliImplModel == null) {
			String title = "Test Appli Title";
			parentAppliImplModel = docService.createAppliImpl(session, appliUrl);
			parentAppliImplModel.setProperty("dublincore", "title", title);
			session.saveDocument(parentAppliImplModel);
			session.save();
			// NB. created documents are auto deleted at the end, so no need for :
			// session.removeDocument(parentAppliImplModel.getRef());
		}
    }
    
}
