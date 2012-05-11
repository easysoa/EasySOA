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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.app.AppComponent;
import org.easysoa.registry.frascati.EasySOAApp;
import org.easysoa.test.EasySOACoreTestFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.frascati.test.FraSCAtiFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Tests SCA import with FraSCAti
 * @author mdutoo
 *
 */

@RunWith(FeaturesRunner.class)
@Features({EasySOACoreTestFeature.class,FraSCAtiFeature.class})
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
@Ignore // TODO Update with new API
public class FraSCAtiAppTest{

    static final Log log = LogFactory.getLog(FraSCAtiAppTest.class);

    //@Inject CoreSession session;

    //@Inject DocumentService docService;
    
    //@Inject ResourceService resourceService;
    
    //DocumentModel parentAppliImplModel;
    
    //@Inject ScaImporterComponent scaImporterComponent;

   // @Inject NxFraSCAtiRegistryService frascatiRegistryService;
    
	@Before
	public void setUp() throws Exception {
		// FraSCAti
	//	assertNotNull("Cannot get FraSCAti service component", frascatiRegistryService);
	}

    @Test
    public void checkEasySOAApps() throws Exception {
    	// get the Appcomponent with Framework.getCompoenent method
    	AppComponent appComponent = Framework.getService(AppComponent.class);
    	// Check that the number of Easy soa app is equals to the number of declared apps in the contrib file
    	List<EasySOAApp> EasySoaAppList = appComponent.getApps();
    	log.debug("App list " + EasySoaAppList.size());
    	// Check that the appId is correct
    	for(EasySOAApp app : EasySoaAppList){
    		log.debug("AppId = " + app.getAppId());
    	}
    	assertEquals(1, EasySoaAppList.size());    	
    }

}
