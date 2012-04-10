/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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

	public FileDeployable(String filePath) throws FileNotFoundException {
		this(new File(filePath));
	}
	
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
