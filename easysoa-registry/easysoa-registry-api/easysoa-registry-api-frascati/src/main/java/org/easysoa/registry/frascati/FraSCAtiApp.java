package org.easysoa.registry.frascati;

import org.nuxeo.frascati.api.FraSCAtiCompositeItf;

public class FraSCAtiApp implements EasySOAApp {
	
	private final static String STARTING_METHOD = "NxFraSCAti";
	
	private String appPath;

	private FraSCAtiRegistryServiceBase fraSCAtiService; // TODO make it independent from nuxeo by reimplementing it also directly on top of FraSCAti
	
	private FraSCAtiCompositeItf composite;
	
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
