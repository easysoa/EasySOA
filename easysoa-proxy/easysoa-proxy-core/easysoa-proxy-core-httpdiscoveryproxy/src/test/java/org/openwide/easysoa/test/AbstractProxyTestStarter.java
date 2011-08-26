package org.openwide.easysoa.test;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

import com.openwide.easysoa.nuxeo.registration.NuxeoRegistrationService;

/**
 * Abstract Proxy Test Starter. Launch FraSCAti and the HTTP Discovery Proxy.
 */
public abstract class AbstractProxyTestStarter {
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(getInvokingClassName());    
    
	/** The FraSCAti platform */
    protected static FraSCAti frascati;
   
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
	
	/**
	 * Start FraSCAti
	 * @throws FrascatiException 
	 */
	protected static void startFraSCAti() throws FrascatiException {
		logger.info("FraSCATI Starting");
		frascati = FraSCAti.newFraSCAti();
	}
	
	/**
	 * Start HTTP Proxy
	 * @throws FrascatiException
	 */
	protected static void startHttpDiscoveryProxy() throws FrascatiException {
		logger.info("HTTP Discovery Proxy Starting");
		frascati.processComposite("src/main/resources/httpDiscoveryProxy.composite", new ProcessingContextImpl());		
	}
	
	/**
	 * Start the services mock for tests
	 * @throws FrascatiException
	 */
	protected static void startMockServices() throws FrascatiException {
		logger.info("Services Mock Starting");
		frascati.processComposite("src/test/resources/twitterMockRest.composite", new ProcessingContextImpl());
		frascati.processComposite("src/test/resources/meteoMockSoap.composite", new ProcessingContextImpl());
	}
 
	/**
	 * Clean Nuxeo registery before to launch the tests
	 * TODO Find an other method to clean because it is not possible with using only NXQL 
	 * @throws JSONException 
	 */
	public final static String cleanNuxeoRegistery() throws JSONException  {
		// Not possible NXQL to select only one field, only select * is available ... Strange
		String nuxeoQuery = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted' AND (ecm:primaryType = 'Service' OR ecm:primaryType = 'ServiceAPI' OR ecm:primaryType = 'Workspace')";
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		String nuxeoResponse = nrs.sendQuery(nuxeoQuery);
		// For each documents returned by the query, call the delete method
		// Need to parse the complete JSON response to find all the document uid to delete
		String entries = new JSONObject(nuxeoResponse).getString("entries");
		JSONArray entriesArray = new JSONArray(entries);
		for(int i =0; i < entriesArray.length(); i++){
			JSONObject entry = entriesArray.getJSONObject(i);
			String uid = entry.getString("uid");
			logger.info("Deleting document with uid = " + uid);
			if(nrs.deleteQuery(uid)){
				logger.info("Document doc:" + uid + " deleted successfully");
			} else {
				logger.info("Unable to delete document doc:" + uid);
			}
		}
		
		// check that docs are well deleted
		return nuxeoResponse = nrs.sendQuery(nuxeoQuery);
		//assertEquals("{\n  \"entity-type\": \"documents\",\n  \"entries\": []\n}", nuxeoResponse);		
	}	
	
}
