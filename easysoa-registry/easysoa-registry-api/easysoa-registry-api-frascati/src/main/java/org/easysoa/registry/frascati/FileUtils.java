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

package org.easysoa.registry.frascati;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ow2.frascati.assembly.factory.api.ManagerException;

public class FileUtils {

	private static Log log = LogFactory.getLog(FileUtils.class);

	/**
	 * Copied from AssemblyFactoryManager
	 * Stream copy - used to extract entries from contribution ZIP.
	 * @param in input stream
	 * @param out output stream
	 * @throws IOException
	 */
	public static final void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024 * 64];
		int len;
		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}
		in.close();
		out.close();
	}

	/**
	 * Unzips the given jar in a temp file and returns its files' URLs Inspired
	 * from AssemblyFactoryManager.processContribution() (though doesn't add it
	 * to the classloader or parse composites)
	 * TODO move to util TODO better : delete temp files afterwards (or mark them so) OR rather use jar:url ?
	 * @param SCA zip or jar
	 * @return unzipped composites URLs
	 * @throws ManagerException
	 */
	public static final Set<URL> unzipAndGetFileUrls(File file) {
		try {
			// Load contribution zip file
			ZipFile zipFile = new ZipFile(file);

			// Get folder name for output
			final String folder = zipFile.getName().substring(zipFile.getName().lastIndexOf(File.separator), zipFile.getName().length() - ".zip".length());

			Set<URL> fileURLSet = new HashSet<URL>();

			// Set directory for extracted files

			// TODO : use system temp directory but should use output folder
			// given by
			// runtime component. Will be possible once Assembly Factory modules
			// will
			// be merged

			final String tempDir = System.getProperty("java.io.tmpdir") + File.separator + folder + File.separator;

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			// Iterate over zip entries
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				log.info("ZIP entry: " + entry.getName());

				// create directories
				if (entry.isDirectory()) {
					log.info("create directory : " + tempDir + entry.getName());
					new File(tempDir, entry.getName()).mkdirs();
				} else {
					File f = new File(tempDir, File.separator + entry.getName());
					// register jar files
					int idx = entry.getName().lastIndexOf(File.separator);
					if (idx != -1) {
						String tmp = entry.getName().substring(0, idx);
						log.info("create directory : " + tempDir + tmp);
						new File(tempDir, tmp).mkdirs();
					}
					// extract entry from zip to tempDir
					copy(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(f)));
					// add to res set
					fileURLSet.add(f.toURI().toURL());
				}
			}

			return fileURLSet;

		} catch (IOException e) {
			log.error(e);
			return new HashSet<URL>(0);
		}
	}

	/**
	 * Copy a file in an other file
	 * 
	 * @param source The source file
	 * @param target The target file
	 * @throws Exception If a problem occurs
	 */
	public static final void copyTo(File source, File target) throws Exception {
		if(source == null || target == null ){
			throw new IllegalArgumentException("Source and target files must not be null");
		}
		// Input and outputs channels
		log.debug("source file = " + source);
		log.debug("target file = " + target);
		FileChannel in = null;
		FileChannel out = null;
		try {
			// Init
			in = new FileInputStream(source).getChannel();
			out = new FileOutputStream(target).getChannel();
			// File copy
			in.transferTo(0, in.size(), out);
		} catch (Exception ex) {
			throw ex;
		} finally {
			// finally close all streams
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {}
			}
		}
	}

}
