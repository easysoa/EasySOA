/**
 * EasySOA Proxy
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

import org.easysoa.monitoring.soa.Service;
import org.easysoa.nuxeo.registration.NuxeoRegistrationService;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Tests the NuxeoRegistrationService
 * 
 * @author mkalam-alami
 *
 */
//@RunWith(FeaturesRunner.class)
//@Features({EasySOACoreFeature.class, WebEngineFeature.class})
/*@Deploy({
    "org.easysoa.registry.rest"
})*/
//@Jetty(config="src/test/resources/jetty.xml", port=EasySOAConstants.NUXEO_TEST_PORT)
public class NuxeoRegistrationServiceTest /*extends AbstractRestTest*/ {

    /*
     * Disabled test because there is a class isolation problem between remote FraSCAti and Nuxeo embeded FraSCAti.
     * 
     * The easysoa-proxy-core-httpdiscoveryproxy works with a remote FraSCAti engine and this test needs some classes in 
     * the easysoa-frascati-service and easysoa-frascati-service -api projects. 
     * If we enable these dependencies in the pom file, we have a cast problem with generated classes and the main proxy composite doesn't start. 
     */
    
    ///@Inject CoreSession session;
    
    // TODO Fix the test
    @Test @Ignore
    public void testNuxeoRegistrationService() throws Exception {
        
        String serviceUrl = "http://www.test-services.com/service";
        
        NuxeoRegistrationService nxrs = new NuxeoRegistrationService();
        Service newService = new Service("http://www.test-services.com/service?wsdl");
        nxrs.registerWSDLService(newService);
        
        ///assertDocumentExists(session, "Service", serviceUrl);
        
    }
    
}
