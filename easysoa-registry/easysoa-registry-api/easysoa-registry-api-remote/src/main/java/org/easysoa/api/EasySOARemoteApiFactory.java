package org.easysoa.api;

import java.io.IOException;

import org.easysoa.rest.EasySOARemoteApi;

/**
 * 
 * @author mkalam-alami, jguillemotte
 *
 */
public class EasySOARemoteApiFactory {

    public static EasySOAApiSession createRemoteApi() throws IOException {
        return new EasySOARemoteApi();
    }
    
    public static EasySOAApiSession createRemoteApi(String nuxeoApisUrl) throws IOException {
        return new EasySOARemoteApi(nuxeoApisUrl);
    }
	
}
