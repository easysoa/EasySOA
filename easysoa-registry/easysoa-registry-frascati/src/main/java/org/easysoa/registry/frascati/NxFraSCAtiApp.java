package org.easysoa.registry.frascati;

//import org.easysoa.app.AppDescriptor;
import org.nuxeo.frascati.NuxeoFraSCAtiException;
import org.nuxeo.frascati.api.FraSCAtiCompositeItf;
import org.nuxeo.runtime.api.Framework;

public class NxFraSCAtiApp implements EasySOAApp {
	
	private final static String STARTING_METHOD = "NxFraSCAti";
	
	private String appPath;

	//private AppDescriptor appDescriptor;

	private FraSCAtiRegistryServiceBase fraSCAtiService; // TODO make it independent from nuxeo by reimplementing it also directly on top of FraSCAti
	
	private FraSCAtiCompositeItf composite;
	
	/*public NxFraSCAtiApp (AppDescriptor appDescriptor) {
		this.appDescriptor = appDescriptor;
	}*/
	
	public NxFraSCAtiApp (String appPath) {
		this.appPath = appPath;
	}
	
	@Override
	public void start() {
		NxFraSCAtiRegistryService nxFraSCAtiRegistryService = Framework.getLocalService(NxFraSCAtiRegistryService.class);
		// TODO start with disco
		try {
			DiscoveryProcessingContext pctx = nxFraSCAtiRegistryService.newDiscoveryProcessingContext();
			nxFraSCAtiRegistryService.getFraSCAti().processComposite(appPath, pctx);
		} catch (NuxeoFraSCAtiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void stop() {
		// TODO stop compos
	}

	// init
	/*public void setAppPath(String appPath) {
		this.appPath = appPath;
	}*/

	public String getAppPath() {
		return appPath;
	}

	public FraSCAtiRegistryServiceBase getFraSCAtiService() {
		return fraSCAtiService;
	}

	// init
	/*public void setFraSCAtiService(FraSCAtiRegistryServiceBase fraSCAtiService) {
		this.fraSCAtiService = fraSCAtiService;
	}*/

	public FraSCAtiCompositeItf getComposite() {
		return composite;
	}
	
	public void SetCompositeItf(FraSCAtiCompositeItf composite){
		this.composite = composite;
	}

	@Override
	public String getAppId() {
		return STARTING_METHOD + ":" + appPath; 
	}
	
}
