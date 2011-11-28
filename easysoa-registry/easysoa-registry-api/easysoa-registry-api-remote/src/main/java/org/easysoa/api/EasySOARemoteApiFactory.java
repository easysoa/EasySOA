package org.easysoa.api;

import java.io.IOException;

import org.easysoa.rest.EasySOARemoteApi;

/**
 * 
 * @author mkalam-alami, jguillemotte
 *
 */
public class EasySOARemoteApiFactory {

    public static EasySOAApiSession createRemoteApi(String user, String password) throws IOException {
        return new EasySOARemoteApi(user, password);
    }
    
    public static EasySOAApiSession createRemoteApi(String nuxeoApisUrl) throws IOException {
        return new EasySOARemoteApi(nuxeoApisUrl);
    }
	
}
