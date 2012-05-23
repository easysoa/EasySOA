package org.easysoa.registry.frascati;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.easysoa.frascati.FraSCAtiServiceException;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * 
 * Component that allows the deployment of FraSCAti apps on startup
 * through simple XML contributions.
 * 
 * @author mkalam-alami
 *
 */
public class FraSCAtiAppComponent extends DefaultComponent {
	
	private static final String APP_EXTENSION_POINT = "apps"; 

    static final Logger logger = Logger.getLogger(FraSCAtiAppComponent.class);
    
	private FraSCAtiServiceItf frascatiService;
	
	/**
	 * Stores composite instance keys in order to be able to stop them.
	 */
	private Map<FraSCAtiAppDescriptor, String> compositeInstances = 
			new HashMap<FraSCAtiAppDescriptor, String>();

	@Override
	public void activate(ComponentContext context) throws Exception {
	    // TODO : there is a problem here, this method is called to early, frascati service is not yet initialized
		try {
	        frascatiService = (FraSCAtiServiceItf) Framework.getLocalService(
	        		FraSCAtiServiceProviderItf.class).getFraSCAtiService();
	        if (frascatiService == null) {
	        	throw new NullPointerException("FraSCAti service is null");
	        }
		}
		catch (Exception e) {
			throw new Exception("Failed to initialize FraSCAti apps component, it won't load any composite", e);
		}
		
	}
	
	@Override
	public void deactivate(ComponentContext context) throws Exception {
		for (String value : compositeInstances.values()) {
			frascatiService.remove(value);
		}
	}
	
	@Override
	public void registerContribution(Object contribution,
			String extensionPoint, ComponentInstance contributor)
			throws Exception {
		if (APP_EXTENSION_POINT.equals(extensionPoint)
				&& contribution instanceof FraSCAtiAppDescriptor) {
			FraSCAtiAppDescriptor app = (FraSCAtiAppDescriptor) contribution;
	        File appJar = new File(app.jarPath).getAbsoluteFile();
	        try {
	        	String newCompositeName = frascatiService.processComposite(app.compositeName,
	        			FraSCAtiServiceItf.all, appJar.toURI().toURL());
	        	compositeInstances.put(app, newCompositeName);
	        }
	        catch (FraSCAtiServiceException e) {
	        	logger.error("Failed to load app " + app.name + " (composite " + app.compositeName
	        			+ " from jar '" + appJar.getAbsolutePath() + "')", e);
	        }
		}
	}
	
	@Override
	public void unregisterContribution(Object contribution,
			String extensionPoint, ComponentInstance contributor)
			throws Exception {
		if (APP_EXTENSION_POINT.equals(extensionPoint)
				&& contribution instanceof FraSCAtiAppDescriptor) {
			FraSCAtiAppDescriptor app = (FraSCAtiAppDescriptor) contribution;
			String compositeToRemove = compositeInstances.remove(app);
			if (compositeToRemove != null) {
				try {
					frascatiService.remove(compositeToRemove);
		        }
		        catch (FraSCAtiServiceException e) {
		        	logger.error("Failed to stop and remove application " + app.name, e);
		        }
			}
		}
	}
	
}
