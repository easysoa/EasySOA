package org.easysoa.runtime.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

import org.easysoa.runtime.api.AbstractDeployable;

public class FileDeployable extends AbstractDeployable<URI> {

	public FileDeployable(File file) throws FileNotFoundException {
		super(file.toURI(), new FileInputStream(file));
	}
	
	public FileDeployable(URI id, InputStream is) {
		super(id, is);
	}

	@Override
	public String getFileName() {
		if (id.getPath().isEmpty()) {
			return id.getHost();
		}
		else {
			String[] splitPath = id.getPath().split("/");
			return splitPath[splitPath.length - 1];
		}
	}

}
