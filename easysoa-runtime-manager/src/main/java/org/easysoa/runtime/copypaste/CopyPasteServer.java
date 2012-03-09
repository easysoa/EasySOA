package org.easysoa.runtime.copypaste;

import java.io.File;
import java.security.InvalidParameterException;

import org.easysoa.runtime.api.RuntimeControlService;
import org.easysoa.runtime.api.RuntimeDeploymentService;
import org.easysoa.runtime.api.RuntimeServer;

public class CopyPasteServer implements RuntimeServer<CopyPasteServerEventService> {

	private File deployablesDirectory;
	
	private CopyPasteServerEventService eventService;
	
	private CopyPasteDeploymentService deploymentService;
	
	
	public CopyPasteServer(File deployablesDirectory) {
		if (!deployablesDirectory.isDirectory()) {
			if (!deployablesDirectory.exists()) {
				boolean success = deployablesDirectory.mkdir();
				if (!success) {
					throw new InvalidParameterException("Specified directory doesn't exist and cannot be created");
				}
			}
			else {
				throw new InvalidParameterException("Specified file is not a directory");
			}
		}
		this.deployablesDirectory = deployablesDirectory;
		this.eventService = new CopyPasteServerEventService();
		this.deploymentService = new CopyPasteDeploymentService(eventService, deployablesDirectory);
	}
	
	public File getDeployablesDirectory() {
		return deployablesDirectory;
	}
	
	@Override
	public RuntimeControlService getControlService() {
		return null; // Unsupported
	}

	@Override
	public RuntimeDeploymentService getDeploymentService() {
		return this.deploymentService;
	}

	@Override
	public CopyPasteServerEventService getEventService() {
		return this.eventService;
	}

}
