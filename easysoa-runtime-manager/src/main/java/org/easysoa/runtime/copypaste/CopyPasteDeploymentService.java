package org.easysoa.runtime.copypaste;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.easysoa.runtime.api.Deployable;
import org.easysoa.runtime.api.RuntimeDeploymentService;

public class CopyPasteDeploymentService implements RuntimeDeploymentService {

	private static final int BUFFER_SIZE = 4096;
	
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
			BufferedInputStream bis = new BufferedInputStream(deployable.getInputStream());
			FileOutputStream fos = new FileOutputStream(targetFile);
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytes;
			while ((bytes = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, bytes);
			}
			bis.close();
			fos.close();
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
