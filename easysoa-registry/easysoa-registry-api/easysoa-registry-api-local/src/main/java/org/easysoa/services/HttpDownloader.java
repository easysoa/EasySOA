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

package org.easysoa.services;

import java.io.File;
import java.io.IOException;

import org.nuxeo.ecm.core.api.Blob;

public interface HttpDownloader {

	/**
	 * Checks if the given URL is available.
	 * @return
	 */
	boolean isURLAvailable();

	/**
	 * Downloads the page at the given URL.
	 * @return
	 * @throws Exception
	 */
	HttpDownloader download() throws Exception;

	/**
	 * Checks if the page has been successfully downloaded.
	 * (note: you must explicitly call download() to fetch the page)
	 * @return
	 */
	boolean isDownloaded();


	/**
	 * Returns the downloaded page as a file, or null if it has not been downloaded, or the export to a file failed.
	 * @return
	 * @throws IOException
	 */
	File getFile() throws IOException;

	/**
	 * Returns the downloaded page as a blob (ready for storage in a Nuxeo document), or null if it has not been downloaded.
	 * @return
	 */
	Blob getBlob();
	
	/**
	 * Returns the downloaded page as an array of bytes, or null if it has not been downloaded.
	 * @return
	 */
	byte[] getBytes();
	
	/**
	 * Deletes all stored data (including the eventual file) if the page has been downloaded. 
	 */
	void delete();

}