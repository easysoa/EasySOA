package org.openwide.easysoa.test;

import org.easysoa.EasySOAConstants;
import org.easysoa.test.EasySOACoreFeature;
import org.easysoa.test.rest.AbstractRestTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

import com.google.inject.Inject;
import com.openwide.easysoa.monitoring.soa.Service;
import com.openwide.easysoa.nuxeo.registration.NuxeoRegistrationService;

/**
 * Tests the NuxeoRegistrationService
 * 
 * @author mkalam-alami
 *
 */
@RunWith(FeaturesRunner.class)
@Features({EasySOACoreFeature.class, WebEngineFeature.class})
@Deploy({
    "org.easysoa.registry.rest"
})
@Jetty(config="src/test/resources/jetty.xml", port=EasySOAConstants.NUXEO_TEST_PORT)
public class NuxeoRegistrationServiceTest extends AbstractRestTest {

    @Inject CoreSession session;
    
    // TODO Fix the test
    @Test @Ignore
    public void testNuxeoRegistrationService() throws Exception {
        
        String serviceUrl = "http://www.test-services.com/service";
        
        NuxeoRegistrationService nxrs = new NuxeoRegistrationService();
        Service newService = new Service("http://www.test-services.com/service?wsdl");
        nxrs.registerWSDLService(newService);
        
        assertDocumentExists(session, "Service", serviceUrl);
        
    }
    
}
