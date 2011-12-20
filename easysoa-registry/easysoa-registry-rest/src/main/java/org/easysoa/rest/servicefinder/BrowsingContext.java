package org.easysoa.rest.servicefinder;

import java.io.FileInputStream;
import java.net.URL;

import org.easysoa.impl.HttpFile;

public class BrowsingContext {

	private URL url = null;
	
	private String data = null;

	/**
	 * 
	 * @param data The response produced when the user browsed to the URL.
	 */
	public BrowsingContext(URL url, String data) throws Exception {
		this.url = url;
		this.data = data;
	}
	
	public BrowsingContext(URL url) throws Exception {
		if (url != null) {
			// Download the file at the given URL
	        HttpFile file = new HttpFile(url);
	        try {
		        file.download();
		        FileInputStream fis = new FileInputStream(file.getFile());
		        StringBuffer dataBuffer = new StringBuffer();
		        char c;
		        while ((c = (char) fis.read()) != -1) {
		        	dataBuffer.append(c);
		        }
		        data = dataBuffer.toString();
	        }
	        finally {
	        	file.delete();
	        }
		}
	}
	
	/**
	 * @return The browsed URL
	 */
	public URL getURL() {
		return url;
	}
	
	/**
	 * @return The response produced when the user browsed to the URL.
	 */
	public String getData() {
		return data;
	}
	
}
