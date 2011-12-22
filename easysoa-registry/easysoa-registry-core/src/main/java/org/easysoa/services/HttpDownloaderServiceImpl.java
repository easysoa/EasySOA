package org.easysoa.services;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpDownloaderServiceImpl implements HttpDownloaderService {

	@Override
	public HttpDownloader createHttpDownloader(URL url) {
		return new HttpDownloaderImpl(url);
	}
	
	@Override
	public HttpDownloader createHttpDownloader(String url) throws MalformedURLException {
		return new HttpDownloaderImpl(url);
	}

}
