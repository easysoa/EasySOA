package org.easysoa.services;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.nuxeo.ecm.core.api.Blob;

public interface HttpDownloader {

	boolean isURLAvailable();

	HttpDownloader download() throws IOException, URISyntaxException;

	void delete();

	File getFile();

	Blob getBlob();

	boolean isDownloaded();

}