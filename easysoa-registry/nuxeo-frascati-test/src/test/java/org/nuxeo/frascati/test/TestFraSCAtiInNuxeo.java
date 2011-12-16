package org.nuxeo.frascati.test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.frascati.api.AbstractProcessingContext;
import org.nuxeo.frascati.api.FraSCAtiServiceItf;
import org.nuxeo.frascati.api.FraSCAtiCompositeItf;
import org.nuxeo.frascati.NuxeoFraSCAtiException;
import org.ow2.frascati.util.reflect.ReflectionHelper;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(FraSCAtiFeature.class)
public class TestFraSCAtiInNuxeo {

	private final String scaFilePath = "src/test/resources/" + "helloworld-pojo.composite";
	
	protected static final Logger log = Logger.getLogger(TestFraSCAtiInNuxeo.class.getCanonicalName());
	
	private final String STARTED = "STARTED";
	private final String STOPPED = "STOPPED";
	private FraSCAtiCompositeItf fcomponent = null;

	FraSCAtiServiceItf frascatiService = null;
	
	@Before
	public void setUp() throws MalformedURLException, ClientException, Exception{
		
		frascatiService = Framework.getLocalService(FraSCAtiServiceItf.class);
		assertNotNull(frascatiService);
		
		File scaFile = new File(scaFilePath);   
		
		frascatiService.processComposite(scaFile.getAbsolutePath(),
					new ProcessingContextTest(
							frascatiService.newProcessingContext()));			
		fcomponent = frascatiService.getComposite("helloworld-pojo");	
	}
	
	@After
	public void tearDown() throws NuxeoFraSCAtiException{		
		frascatiService.remove("helloworld-pojo");
		fcomponent = null;
	}
	
	/**
     * Test if an existing service can be retrieved 
     * 
     * @throws NuxeoFraSCAtiException
     */
    @Test
    public void testGetRunnableServiceAndExecuteIt() throws NuxeoFraSCAtiException {  
    	Runnable r = frascatiService.getService(fcomponent.getComponent(), "r", Runnable.class);
	    assertNotNull(r);
	    r.run();   	
    }
    
    /**
     * Test NuxeoFraSCAtiException is thrown if a service is unknown
     * 
	 * @throws NuxeoFraSCAtiException
     */
    @Test(expected = NuxeoFraSCAtiException.class)
    public void testThrowExceptionIfGetUnexistingService() throws NuxeoFraSCAtiException {      	
    	Runnable r = frascatiService.getService(fcomponent.getComponent(), "unknown", Runnable.class);    	
    }
   
    @Test
    public void testSCACompositeLifeCycle(){    	    	
    	String state = fcomponent.getState();
    	if(STARTED.equals(state)){    		
    		fcomponent.stop();
    		log.info( fcomponent.getState()); 
    		assertEquals(STOPPED,fcomponent.getState());
    		fcomponent.start();
    		log.info(fcomponent.getState()); 
    		assertEquals(STARTED,fcomponent.getState());
    	} else {    		
    		fcomponent.start();
    		log.info(fcomponent.getState()); 
    		assertEquals(STARTED,fcomponent.getState());
    		fcomponent.stop();
    		log.info(fcomponent.getState()); 
    		assertEquals(STOPPED,fcomponent.getState());
    	}
    }
	
    @Test
    public void testWait() throws IOException{
		log.info("Frascati in Nuxeo started, wait for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
		log.info("Frascati in Nuxeo stopped !");    	
    }
    
    private class ProcessingContextTest extends AbstractProcessingContext {

    	public ProcessingContextTest(ReflectionHelper delegate) throws NuxeoFraSCAtiException {
    		super(delegate);
    	}

    }
	
}
