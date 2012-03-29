package org.easysoa.runtime.utils;

import java.io.IOException;

import org.easysoa.runtime.api.DeployableProvider;

public class FileProvider implements DeployableProvider<FileDeployable, String> {

	String fileRepositoryFolder;
	
	public FileProvider(String fileRepositoryFolder) {
		this.fileRepositoryFolder = fileRepositoryFolder;
	}

	@Override
	public FileDeployable fetchDeployable(String path) throws IOException {
		return new FileDeployable(fileRepositoryFolder + path);
	}

}
