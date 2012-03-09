package org.easysoa.runtime.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

import org.easysoa.runtime.api.AbstractDeployable;

/**
 * Thin wrapping of files as deployables.
 * 
 * @author mkalam-alami
 *
 */
public class FileDeployable extends AbstractDeployable<URI> {

	private File file;
	
	public FileDeployable(File file) throws FileNotFoundException {
		super(file.toURI(), new FileInputStream(file));
		this.file = file;
	}
	
	public FileDeployable(URI id, InputStream is) {
		super(id, is);
	}

	@Override
	public String getFileName() {
		return file.getName();
	}
	
	public File getFile() {
		return file;
	}

}
