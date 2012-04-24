/**
 * 
 */
package org.openwide.easysoa.test.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.junit.Ignore;
import org.junit.Test;
import org.openwide.easysoa.test.mock.meteomock.client.MeteoMock;
import org.openwide.easysoa.test.mock.meteomock.client.MeteoMockPortType;

import com.openwide.easysoa.util.ContentReader;

/**
 * This class can be used to create a permanent exchange test set
 * based on the mocks or based on the smartravel sample
 * @author jguillemotte
 *
 */
public class ExchangeTestSetCreator extends AbstractProxyTestStarter {

    // Logger
    private static Logger logger = Logger.getLogger(ExchangeTestSetCreator.class.getName());
    
    /**
     * Generate the test set
     * @throws Exception If a problem occurs
     */
    @Test
    @Ignore // Ignored : this test is used only to generate permanent test set to feed other tests
    public void generateExchangeTestSet() throws Exception {
        // Start FraSCAti
        startFraSCAti();
        // Start HTTP proxy
        startHttpDiscoveryProxy("httpDiscoveryProxy.composite");
        // Start mock services
        startMockServices(false, true, true);
        // Create first test set based on REST twitter mock 
        createTwitterExchangeTestSet();
        // Create second test set based on WSDL Meteo mock
        createMeteoExchangeTestSet();
        // Stop FraSCAti
        stopFraSCAti();
    }    
    
    /**
     * Create a twitter mock based exchange test set
     * @throws Exception If a problem occurs
     */
    public void createTwitterExchangeTestSet() throws Exception {
        String testSetStoreName = "Twitter_Test_Set";
        
        DefaultHttpClient httpClient = new DefaultHttpClient();     
        
        // Start a new Run
        HttpPost newRunPostRequest = new HttpPost("http://localhost:8084/run/start/" + testSetStoreName);
        assertEquals("Run '" + testSetStoreName + "' started !", httpClient.execute(newRunPostRequest, new BasicResponseHandler()));
        
        // Get the twitter mock set and send requests to the mock through the HTTP proxy
        DefaultHttpClient httpProxyClient = new DefaultHttpClient();

        // Set client to use the HTTP Discovery Proxy
        HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
        httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        UrlMock urlMock = new UrlMock();
        HttpResponse response;
        HttpUriRequest httpUriRequest;
        for(String url : urlMock.getTwitterUrlData("localhost:" + EasySOAConstants.TWITTER_MOCK_PORT)){
            logger.info("Request send : " + url);           
            httpUriRequest = new HttpGet(url);
            response = httpProxyClient.execute(httpUriRequest);
            // Need to read the response body entierely to be able to send another request
            ContentReader.read(response.getEntity().getContent());           
        }
        
        // Stop and save the run
        HttpPost stopRunPostRequest = new HttpPost("http://localhost:8084/run/stop");
        assertEquals("Current run stopped !", httpClient.execute(stopRunPostRequest, new BasicResponseHandler()));
        // delete the run
        HttpPost deleteRunPostRequest = new HttpPost("http://localhost:8084/run/delete");
        assertEquals("Run deleted !", httpClient.execute(deleteRunPostRequest, new BasicResponseHandler()));        

        // Copy the test set in src/test/resources/exchangeTestSets
        copyTestSet(testSetStoreName);
    }

    /**
     * @throws IOException 
     * @throws  
     * 
     */
    private void createMeteoExchangeTestSet() throws Exception {
        
        String testSetStoreName = "Meteo_Test_Set";

        DefaultHttpClient httpClient = new DefaultHttpClient();
        
        // Start a new Run
        HttpPost newRunPostRequest = new HttpPost("http://localhost:8084/run/start/" + testSetStoreName);
        assertEquals("Run '" + testSetStoreName + "' started !", httpClient.execute(newRunPostRequest, new BasicResponseHandler()));       
        
        // Set the discovery proxy
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "8082");        
        
        // Send the test requests
        UrlMock urlMock = new UrlMock();
        for(String cityName : urlMock.getMeteoMockCities()){
            MeteoMock meteoService = new MeteoMock();
            MeteoMockPortType port = meteoService.getMeteoMockPort();
            String response = port.getTomorrowForecast(cityName);
            logger.info("Response for " + cityName + " : " + response);
        }
        
        // Remove the discovery proxy
        System.setProperty("http.proxyHost", "");
        System.setProperty("http.proxyPort", "");
        
        // Stop and save the run
        HttpPost stopRunPostRequest = new HttpPost("http://localhost:8084/run/stop");
        assertEquals("Current run stopped !", httpClient.execute(stopRunPostRequest, new BasicResponseHandler()));
        // delete the run
        HttpPost deleteRunPostRequest = new HttpPost("http://localhost:8084/run/delete");
        assertEquals("Run deleted !", httpClient.execute(deleteRunPostRequest, new BasicResponseHandler()));        
        
        // Copy test set to src/test/resources ...
        copyTestSet(testSetStoreName);
    }

    /**
     * Copy the test set from a source folder to a destination folder
     * @param testSetStoreName
     * @throws IOException
     */
    private void copyTestSet(String testSetStoreName) throws IOException {
        // Copy the test set in src/test/resources/exchangeTestSets
        File sourceFolder = new File("target/stores/" + testSetStoreName);
        File destinationFolder = new File("src/test/resources/exchangeTestSets/" + testSetStoreName);
        FileUtils.copyDirectory(sourceFolder, destinationFolder);        
    }
    
}
