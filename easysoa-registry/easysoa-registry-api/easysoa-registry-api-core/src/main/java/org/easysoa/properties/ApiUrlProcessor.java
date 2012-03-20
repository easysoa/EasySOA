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

package org.easysoa.properties;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class ApiUrlProcessor {

	private static Logger log = Logger.getLogger(ApiUrlProcessor.class.getName());	
	
    private static final String ERROR_API_URL_BASE = "Can't get service API url because ";
    private static final String ERROR_API_URL_APPLIIMPL = ERROR_API_URL_BASE + "bad appliimpl URL";
    private static final String ERROR_API_URL_API = ERROR_API_URL_BASE + "bad api URL";
    private static final String ERROR_API_URL_SERVICE = ERROR_API_URL_BASE + "bad service URL";
    
    /**
     * Guesses an API url given the service URL and others.
     * Normalizes URLs first.
     * @param appliImplUrl has to be empty (means no default root url for this appliimpl)
     * or a well-formed URL.
     * @param apiUrlPath
     * @param serviceUrlPath
     * @return
     * @throws MalformedURLException 
     */
    public static final String computeApiUrl(String appliImplUrl, String apiUrlPath,
            String serviceUrlPath) throws MalformedURLException {
        
        apiUrlPath = PropertyNormalizer.normalizeUrl(apiUrlPath, ERROR_API_URL_API);
        serviceUrlPath = PropertyNormalizer.normalizeUrl(serviceUrlPath, ERROR_API_URL_SERVICE);
        
        int apiPathEndIndex = -1;
        
        try {
            if (appliImplUrl.length() != 0) {
                // appliImplUrl has to be well-formed
                appliImplUrl = PropertyNormalizer.normalizeUrl(appliImplUrl, ERROR_API_URL_APPLIIMPL);
                String defaultApiUrl = PropertyNormalizer.concatUrlPath(appliImplUrl, apiUrlPath);
                if (serviceUrlPath.contains(defaultApiUrl)) {
                    apiPathEndIndex = serviceUrlPath.indexOf(defaultApiUrl) + defaultApiUrl.length();
                } // else default appliImplUrl does not apply
            } // else empty appliImplUrl means no default appliImplUrl for apis
        }
        catch (Exception e) {
            log.warning("Failed to compute API url from appli URL & API URL path, using default ("+e.getMessage()+")");
        }
        
        if (apiPathEndIndex == -1) {
            return computeApiUrl(serviceUrlPath);
        }
        
        return PropertyNormalizer.normalizeUrl(
                serviceUrlPath.substring(0, apiPathEndIndex), ERROR_API_URL_API); // TODO http://localhost:9000/hrestSoapProxyWSIntern
    }
    
    public static final String computeApiUrl(String serviceUrlPath) throws MalformedURLException {
        int lastSlashIndex = serviceUrlPath.lastIndexOf('/');
        int lastProtocolSlashIndex = serviceUrlPath.indexOf('/') + 1;
        if (lastSlashIndex == lastProtocolSlashIndex) {
            // if no slash besides protocol, api url is service url
            return PropertyNormalizer.normalizeUrl(serviceUrlPath, ERROR_API_URL_API); 
        }
        // otherwise api url is service url until last slash TODO better for REST (but OK for WS)
        return PropertyNormalizer.normalizeUrl(
                serviceUrlPath.substring(0, lastSlashIndex),
                ERROR_API_URL_API); 
    }
    
    public static final String computeAppliImplUrl(String apiUrlPath) throws MalformedURLException {
        if (apiUrlPath.replace("://", "").lastIndexOf('/') != -1) {
            return PropertyNormalizer.normalizeUrl(
                    apiUrlPath.substring(0, apiUrlPath.lastIndexOf('/')),
                    ERROR_API_URL_APPLIIMPL); 
        }
        else {
            URL url = new URL(apiUrlPath);
            return PropertyNormalizer.normalizeUrl(url.getProtocol()+"://"+url.getHost());
        }
    }

    public static final String computeServiceTitle(String serviceUrlPath) throws MalformedURLException {
        String lastUrlPart = serviceUrlPath.substring(serviceUrlPath.lastIndexOf('/')+1);
        return lastUrlPart.replaceAll("(wsdl|WSDL|[^\\w.-])", "");
    }    
    
}
