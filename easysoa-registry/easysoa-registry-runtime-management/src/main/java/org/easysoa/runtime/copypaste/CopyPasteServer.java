package org.easysoa.runtime.copypaste;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

import org.easysoa.runtime.api.RuntimeServer;
import org.apache.log4j.Logger;
import org.easysoa.runtime.api.Deployable;
import org.easysoa.runtime.api.RuntimeControlService;
import org.easysoa.runtime.api.RuntimeDeployableService;
import org.easysoa.runtime.utils.FileDeployable;

public class CopyPasteServer extends CopyPasteServerEventService implements 
	RuntimeServer<Deployable<?>, CopyPasteServerEventService>,
	RuntimeDeployableService<Deployable<?>> {
	
	private static final int BUFFER_SIZE = 4096;

	private static Logger logger = Logger.getLogger(CopyPasteServer.class);
	
	private File deployablesDirectory;

	public CopyPasteServer(String deployablesDirectory) {
		this(new File(deployablesDirectory));
	}
	
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

	@Override
	public List<Deployable<?>> getDeployedDeployables() {
		File[] files = deployablesDirectory.listFiles();
		List<Deployable<?>> result = new LinkedList<Deployable<?>>();
		for (File file : files) {
			try {
				result.add(new FileDeployable(file));
			} catch (FileNotFoundException e) {
				logger.warn("Could not access deployable " + file.getName() + ": " + e.getMessage());
			}
		}
		return result;
	}
	
	private File getTargetFile(Deployable<?> deployable) {
		return new File(this.deployablesDirectory.toString() + File.separator + deployable.getFileName());
	}

	@Override
	public boolean start(Deployable<?> deployable) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't start copy/paste server");
	}

	@Override
	public boolean stop(Deployable<?> deployable) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't stop copy/paste server");
	}

	@Override
	public RuntimeControlService getControlService() {
		return null; // Unsupported
	}

	@Override
	public RuntimeDeployableService<Deployable<?>> getDeployableService() {
		return this;
	}

	@Override
	public CopyPasteServerEventService getEventService() {
		return this;
	}

	@Override
	public String getName() {
		return "Folder " + deployablesDirectory.getPath();
	}

}
