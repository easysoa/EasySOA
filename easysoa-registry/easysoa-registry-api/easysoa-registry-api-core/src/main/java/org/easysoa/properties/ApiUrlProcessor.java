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
    public final static String computeApiUrl(String appliImplUrl, String apiUrlPath,
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
    
    public final static String computeApiUrl(String serviceUrlPath) throws MalformedURLException {
        return PropertyNormalizer.normalizeUrl(
                serviceUrlPath.substring(0, serviceUrlPath.lastIndexOf('/')),
                ERROR_API_URL_API); 
    }
    
    public final static String computeAppliImplUrl(String apiUrlPath) throws MalformedURLException {
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

    public final static String computeServiceTitle(String serviceUrlPath) throws MalformedURLException {
        String lastUrlPart = serviceUrlPath.substring(serviceUrlPath.lastIndexOf('/')+1);
        return lastUrlPart.replaceAll("(wsdl|WSDL|[^\\w.-])", "");
    }    
    
}
