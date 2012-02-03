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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.frascati.NuxeoFraSCAtiException;
import org.nuxeo.frascati.api.FraSCAtiCompositeItf;
import org.nuxeo.frascati.api.FraSCAtiServiceItf;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(FraSCAtiFeature.class)
public class TestFraSCAtiInNuxeo {

	private final String scaFilePath = "src/test/resources/" + "helloworld-pojo.composite";
	private final String scaWSFilePath = "src/test/resources/" + "helloworld-ws-server.composite";
	private final String scaServletFilePath = "src/test/resources/" + "helloworld-servlet.composite";	
	private URL servlet1;
	private URL servlet2;
	private URL servlet3;
	private URL webservice;
		
	protected static final Logger log = Logger.getLogger(TestFraSCAtiInNuxeo.class.getCanonicalName());
	
	private final String STARTED = "STARTED";
	private final String STOPPED = "STOPPED";
	private FraSCAtiCompositeItf fcomponent = null;

	FraSCAtiServiceItf frascatiService = null;
	
	public TestFraSCAtiInNuxeo(){
  		
		try {	
			servlet1 = new URL("http://localhost:8081/Hello");
			servlet2 = new URL("http://localhost:8082/Hello");
			servlet3 = new URL("http://localhost:8083/Hello");
			webservice = new URL("http://localhost:9000/HelloService");
		} catch(MalformedURLException e){
			e.printStackTrace();
		}
	}
	
	@Before
	public void setUp() throws MalformedURLException, ClientException, Exception{
		
		frascatiService = Framework.getLocalService(FraSCAtiServiceItf.class);
		assertNotNull(frascatiService);		
		File scaFile = new File(scaFilePath);   		
		frascatiService.processComposite(scaFile.getAbsolutePath());			
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
    	frascatiService.getService(fcomponent.getComponent(), "unknown", Runnable.class);    	
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
    public void testParserIntentObserver(){
    	
    	ParserIntentObserverTest testObserver = new ParserIntentObserverTest();
    	frascatiService.addParserIntentObserver(testObserver);
    	try {
			tearDown();
	    	File scaFile = new File(scaFilePath); 
	    	frascatiService.processComposite(scaFile.getAbsolutePath());	
	    	
		} catch (NuxeoFraSCAtiException e) {
			e.printStackTrace();
		}
	    assertEquals(1,testObserver.size());
	    log.info("ParserIntentObserver has one composite left ...");
	    assertEquals("helloworld-pojo",testObserver.last().getName());
	    log.info("... and its name is helloworld-pojo");
    }
        
    @Test 
    public void testUnregisterServlet(){    	  		
		try {	
			int n =0;
			for(;n<=1;n++){
				frascatiService.processComposite(new File(
						scaServletFilePath).getAbsolutePath());			
				frascatiService.remove("helloworld-servlet");
				fcomponent = null;
				frascatiService.unregisterServlet(servlet1);
				frascatiService.unregisterServlet(servlet2);
				frascatiService.unregisterServlet(servlet3);
			}
		} catch (NuxeoFraSCAtiException e) {
			e.printStackTrace();
		}		
    }
    
    @Test 
    public void testUnregisterWS(){    	  		
		try {	
			int n =0;
			for(;n<=1;n++){
				frascatiService.processComposite(
						new File(scaWSFilePath).getAbsolutePath());			
				frascatiService.remove("helloworld-ws-server");
				fcomponent = null;
				frascatiService.unregisterServlet(webservice);
			}
		} catch (NuxeoFraSCAtiException e) {
			e.printStackTrace();
		} 			
    }
    
    @Test
    @Ignore
    public void testWait() throws IOException{
		log.info("Frascati in Nuxeo started, wait for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
		log.info("Frascati in Nuxeo stopped !");    	
    }
    	
}
