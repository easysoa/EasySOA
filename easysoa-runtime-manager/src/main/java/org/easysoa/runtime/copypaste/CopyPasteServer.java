package org.easysoa.runtime.copypaste;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

import org.easysoa.runtime.api.Deployable;
import org.easysoa.runtime.api.RuntimeDeploymentService;

public class CopyPasteServer extends CopyPasteServerEventService implements RuntimeDeploymentService {

	private File deployablesDirectory;
	
	private static final int BUFFER_SIZE = 4096;
	
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
	}
	
	public File getDeployablesDirectory() {
		return deployablesDirectory;
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
			this.onDeploy(deployable);
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
				this.onUndeploy(deployable);
			}
			return success;
		}
		else {
			return false;
		}
			
	}
	
	private File getTargetFile(Deployable<?> deployable) {
		return new File(this.deployablesDirectory.toString() + File.separator + deployable.getFileName());
	}

}
