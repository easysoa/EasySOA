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