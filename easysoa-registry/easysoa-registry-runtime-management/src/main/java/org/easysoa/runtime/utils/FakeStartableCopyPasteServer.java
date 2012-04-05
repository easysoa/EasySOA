package org.easysoa.runtime.utils;

import java.io.File;
import java.io.IOException;

import org.easysoa.runtime.api.RuntimeControlService;
import org.easysoa.runtime.copypaste.CopyPasteServer;

/**
 * Useless CopyPasteServer that can be "started" and "stopped".
 * For runtime managment testing purposes.
 * 
 * @author mkalam-alami
 *
 */
public class FakeStartableCopyPasteServer extends CopyPasteServer implements RuntimeControlService {
	
	private File startedFile;
	
	public FakeStartableCopyPasteServer(String deployablesDirectory) {
		this(new File(deployablesDirectory));
	}
	
	public FakeStartableCopyPasteServer(File deployablesDirectory) {
		super(deployablesDirectory);
		startedFile = new File(deployablesDirectory.getPath() + "/STARTED");
	}
	
	@Override
	public RuntimeControlService getControlService() {
		return this;
	}

	@Override
	public RuntimeState getState() {
		if (startedFile.exists()) {
			return RuntimeState.STARTED; 
		}
		else {
			return RuntimeState.STOPPED;
		}
	}

	@Override
	public boolean start() {
		try {
			return startedFile.createNewFile();
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean stop() {
		return startedFile.delete();
	}
	
}
