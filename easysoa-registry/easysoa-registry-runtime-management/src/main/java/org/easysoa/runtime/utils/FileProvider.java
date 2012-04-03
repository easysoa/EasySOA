package org.easysoa.runtime.utils;

import java.io.FileNotFoundException;

import org.easysoa.runtime.api.DeployableProvider;

public class FileProvider implements DeployableProvider<FileDeployable> {

	String fileRepositoryFolder;
	
	public FileProvider(String fileRepositoryFolder) {
		this.fileRepositoryFolder = fileRepositoryFolder;
	}

	@Override
	public FileDeployable fetchDeployable(Object path) {
		try {
			return new FileDeployable(fileRepositoryFolder + path.toString());
		} catch (FileNotFoundException e) {
			return null;
		}
	}

}
