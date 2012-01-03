package org.easysoa.services;

import java.net.MalformedURLException;
import java.net.URL;

public interface HttpDownloaderService {

	HttpDownloader createHttpDownloader(URL url);

	HttpDownloader createHttpDownloader(String url) throws MalformedURLException;
	
}
