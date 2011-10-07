package org.easysoa.proxy.common;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class ProxyUtil {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(ProxyUtil.class);	
	
	
	public static URL getUrlOrFile(String wsdlSource) throws Exception {
		try {
			return new URL(wsdlSource);
		} catch (MalformedURLException e) {
			File wsdlFile = new File(wsdlSource);
			if (wsdlFile.isFile()) {
				try {
					return wsdlFile.toURI().toURL();
				} catch (MalformedURLException e1) {
					// should not happen because is a valid file
					logger.error("File produces a malformed URL?! " + wsdlFile.getAbsolutePath(), e1);
					throw e1;
				}
			} else {
				throw new Exception("wsdlSource should be either a URL or a file but is " + wsdlSource);
			}
		}
	}
	
}
