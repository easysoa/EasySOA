package com.openwide.easysoa.galaxydemotest;

import org.apache.log4j.Logger;
import org.easysoa.test.EasySOAServerFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.services.resource.ResourceService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

import com.google.inject.Inject;

/**
 * Unit test for Galaxy Demo.
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOAServerFeature.class)
@Jetty(port=8080)
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class NuxeoTestStarter {

    @Inject CoreSession session;
    @Inject ResourceService resourceService;

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(getInvokingClassName());    
    
    /**
     * 
     * @return
     */
    public static String getInvokingClassName() {
    	return Thread.currentThread().getStackTrace()[1].getClassName();
    }		
	
    /**
     * 
     */
	static {
		System.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
	}    

	/**
	 * @throws Exception
	 * 
	 */
	@Test
	public final void testWaitUntilRead() throws Exception{
		logger.debug("Nuxeo started, wait for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
	}	
}