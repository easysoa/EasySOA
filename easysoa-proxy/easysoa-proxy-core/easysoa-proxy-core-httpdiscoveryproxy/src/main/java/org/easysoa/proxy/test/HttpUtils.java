package org.easysoa.proxy.test;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.easysoa.EasySOAConstants;


/**
 * For run easily some http calls 
 * @author fntangke
 *
 */


public class HttpUtils {
    
    
    public HttpUtils(){}
    
    /**
     * A simple GET Http call to the url
     * @param url
     * @return 
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String doGet(String url) throws ClientProtocolException, IOException {
        // TODO Auto-generated method stub
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        // HTTP proxy Client
        DefaultHttpClient httpProxyClient = new DefaultHttpClient();
        // Set client to use the HTTP Discovery Proxy
        HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
        httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        // Send a request to 
        return httpProxyClient.execute(new HttpGet(url), responseHandler);
    }
    
    
    /**
     * A simple POST Http call to the url
     * @param url
     * @param jsonContent
     * @return 
     * @throws ClientProtocolException
     * @throws IOException
     */
    
    public static String doPostJson(String url, String jsonContent) throws ClientProtocolException, IOException {
        // TODO Auto-generated method stub
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        
        // HTTP proxy Client
        DefaultHttpClient httpProxyClient = new DefaultHttpClient();

        // Set client to use the HTTP Discovery Proxy
        HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
        httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        // Send a request to 
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setEntity(new StringEntity(jsonContent));
        return httpProxyClient.execute(httpPost, responseHandler);
    }
    
    /**
     * A simple POST Http call to the url
     * @param url
     * @param soapContent
     * @return 
     * @throws ClientProtocolException
     * @throws IOException
     */
    
    public static String doPostSoap(String url, String soapContent) throws ClientProtocolException, IOException {
        // TODO Auto-generated method stub
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        
        // HTTP proxy Client
        DefaultHttpClient httpProxyClient = new DefaultHttpClient();

        // Set client to use the HTTP Discovery Proxy
        HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
        httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        // Send a request to 
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/xml; charset=UTF-8");
        httpPost.setEntity(new StringEntity(soapContent));
        return httpProxyClient.execute(httpPost, responseHandler);
    }

    
    
}
