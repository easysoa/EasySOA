package org.easysoa.runtime.copypaste;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.easysoa.runtime.api.Deployable;
import org.easysoa.runtime.api.RuntimeDeploymentService;

public class CopyPasteDeploymentService implements RuntimeDeploymentService {

	private File deployablesFolder;
	
	private CopyPasteServerEventService eventService;
	
	public CopyPasteDeploymentService(CopyPasteServerEventService eventService, File deployablesFolder) {
		this.eventService = eventService;
		this.deployablesFolder = deployablesFolder;
	}

	@Override
	public boolean deploy(Deployable<?> deployable) throws IOException {
		File targetFile = getTargetFile(deployable);
		if (!targetFile.exists()) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(deployable.getInputStream()));
			FileWriter writer = new FileWriter(targetFile);
			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
			}
			deployable.closeInputStream();
			eventService.onDeploy(deployable);
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean undeploy(Deployable<?> deployable) {
		File targetFile = getTargetFile(deployable);
		if (targetFile.exists()) {
			boolean success = targetFile.delete();
			if (success) {
				eventService.onUndeploy(deployable);
			}
			return success;
		}
		else {
			return false;
		}
			
	}
	
	private File getTargetFile(Deployable<?> deployable) {
		return new File(deployablesFolder.toString() + File.separator + deployable.getFileName());
	}

}
