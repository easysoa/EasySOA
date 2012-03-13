package org.easysoa.registry.frascati;


public class FraSCAtiApp implements EasySOAApp {
	
	private final static String STARTING_METHOD = "NxFraSCAti";
	
	private String appPath;

	private FraSCAtiRegistryServiceBase fraSCAtiService; // TODO make it independent from nuxeo by reimplementing it also directly on top of FraSCAti
	
	
	@Override
	public void start() {
		// TODO start with disco
	}

	@Override
	public void stop() {
		// TODO stop compos
	}

	// init
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}

	public String getAppPath() {
		return appPath;
	}

	public FraSCAtiRegistryServiceBase getFraSCAtiService() {
		return fraSCAtiService;
	}

	// init
	public void setFraSCAtiService(FraSCAtiRegistryServiceBase fraSCAtiService) {
		this.fraSCAtiService = fraSCAtiService;
	}

	@Override
	public String getAppId() {
		return STARTING_METHOD + ":" + appPath; 
	}
	
}
