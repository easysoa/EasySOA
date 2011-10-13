/**
 * EasySOA Proxy
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
 * Contact : easysoa-dev@groups.google.com
 */

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
