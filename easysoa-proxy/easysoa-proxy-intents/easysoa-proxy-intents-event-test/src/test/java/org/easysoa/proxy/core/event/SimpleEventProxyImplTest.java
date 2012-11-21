package org.easysoa.proxy.core.event;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.easysoa.proxy.core.event.implementations.ActionInterface;
import org.easysoa.samples.paf.PureAirFlowersServer;
import org.easysoa.samples.paf.PureAirFlowersService;
import org.junit.AfterClass;	
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

public class SimpleEventProxyImplTest {

  private static PureAirFlowersServer server;
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(getInvokingClassName());    

	/** The FraSCAti platform */
	protected static FraSCAti frascati;

        protected static ArrayList<Component> componentList;
    
	static {
		System.setProperty("org.ow2.frascati.bootstrap", "org.ow2.frascati.bootstrap.FraSCAti");
	}

    /**
     * Return the invoking class name
     * @return The class name
     */
    public static String getInvokingClassName() {
    	return Thread.currentThread().getStackTrace()[1].getClassName();
    }


	@BeforeClass
    public static void setUp() throws Exception {
		System.out.println("\nEtape 1 du test: lancement des serveur pureair flowers et frascati");
           // server = new PureAirFlowersServer();
         //     server.start();
        
        startFraSCAti();

		System.out.println("\nFrascati lancé...");
    }
	
	
	/**
	 * This test do nothing, just wait for a user action to stop the proxy. 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@org.junit.Test
	@Ignore
	public final void testWaitUntilRead() throws Exception {
		logger.info("Http Discovery Proxy started, wait for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
		logger.info("Http Discovery Proxy stopped !");
	}
	
	@org.junit.Test
	public final void testIt() throws Exception {

		System.out.println("\nEtape 3 du test: tentative de chargement du fichier composite");
                 Component component = frascati.processComposite("src/main/resources/eventproxy_simple.composite",new ProcessingContextImpl());
                 
                // Component componentintent = frascati.processComposite("src/main/resources/monintent.composite",new ProcessingContextImpl());
                // ActionInterface act = (ActionInterface) component.getFcInterface("ActionInterface");
                Runnable client = frascati.getService(component, "r",Runnable.class);
                client.run();
                //System.in.read();
         //      Object dz = component.getFcInterface("client");
          //      System.out.println("c est ici ".concat(dz.toString()));
                //System.out.println("\nlongueur de dz ".concat(String.valueOf(dz.length)));
		try {
			//intentService.getOrdersNumber("452");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
                    

		System.out.println("\n Chargement échoué");
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void teardown() throws Exception{
		stopFraSCAti();
		//server.stop();
	}
	
	/**
	 * 
	 * @throws FrascatiException
	 */
	protected static void stopFraSCAti() throws FrascatiException{

		logger.info("FraSCATI Stopping");
		if(componentList != null){
			//for(Component component : componentList){
		    for(Component component : componentList){
				logger.debug("Closing component : " + component);
				//frascati.stop(component);
			    frascati.close(component);
			}
		}
		frascati = null;

	}
	/**
	 * Start FraSCAti
	 * @throws Exception 
	 */
	protected static void startFraSCAti() throws Exception {
		logger.info("FraSCATI Starting");
		componentList =  new ArrayList<Component>();
		frascati = FraSCAti.newFraSCAti();
	}
        
}
