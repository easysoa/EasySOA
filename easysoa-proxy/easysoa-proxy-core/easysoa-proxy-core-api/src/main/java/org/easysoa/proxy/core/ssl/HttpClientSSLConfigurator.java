package org.easysoa.proxy.core.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * HttpClient usage in Easysoa
 * 
 *  - org.easysoa.proxy.core.api.monitoring.SoapMessageHandler, method checkWsdl : to check if a wsdl file exist for a soap exchange
 *  - org.easysoa.proxy.core.api.nuxeo.registration.NuxeoRegistrationService, methods sendQuery, deleteQuery : query nuxeo docs
 *  - org.easysoa.proxy.core.api.util.RequestForwarder, method sendRestRequest : send request to original server
 *  
 *  - Several other test classes : 
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy-test/src/test/java/org/easysoa/proxy/handler/event/EventExchangeHandlerTest.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy-test/src/test/java/org/easysoa/records/simulation/SimulationEngineTest.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy-test/src/test/java/org/easysoa/test/FraSCAtiBindingHttpCloseBugTest.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy-test/src/test/java/org/easysoa/test/helpers/DiscoveryModeProxyTestBase.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy-test/src/test/java/org/easysoa/test/messaging/ExchangeRecordProxyReplayTest.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy-test/src/test/java/org/easysoa/test/messaging/ExchangeRecordStoreTest.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy-test/src/test/java/org/easysoa/test/messaging/TemplateTest.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy-test/src/test/java/org/easysoa/test/mode/validated/FullMockedValidatedModeProxyTest.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy-test/src/test/java/org/easysoa/test/mode/validated/PartiallyMockedValidatedModeProxyTest.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy-test/src/test/java/org/easysoa/test/util/AbstractProxyTestStarter.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy-test/src/test/java/org/easysoa/test/util/ExchangeTestSetCreator.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy/src/main/java/org/easysoa/proxy/test/HttpUtils.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-scaffolderproxy/src/test/java/org/easysoa/test/FormGeneratorTest.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-scaffolderproxy/src/test/java/org/easysoa/test/WsdlFileTester.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-scaffolderproxy/src/test/java/org/easysoa/test/XsltFormGeneratorTest.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-tests/src/test/java/org/easysoa/tests/helpers/AbstractTestHelper.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-tests/src/test/java/org/easysoa/tests/ScenarioTest.java
 * easysoa/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-cxf-interceptor/src/test/java/org/easysoa/cxf/ProxyInterceptorTest.java
 * easysoa/easysoa-registry/easysoa-registry-frascati/src/test/java/org/easysoa/sca/proxy/DiscoveryProxyAppTest.java
 * easysoa/easysoa-registry/easysoa-registry-rest-miner/src/test/java/org/easysoa/records/filters/test/NuxeoFrascatiServletFilterTest.java
 */

/**
 * 
 * @author jguillemotte
 *
 */
public class HttpClientSSLConfigurator {

    // Default SSL Port
    public final static int SSL_PORT = 443;
    
    private String keyStorePassword;
    private String keyStoreFile;
    private String trustStorePassword;    
    private String trustStoreFile;
    
    /**
     * 
     * @param keyStoreFile
     * @param keyStorePassword
     */
    public HttpClientSSLConfigurator(String keyStoreFile, String keyStorePassword, String trustStoreFile, String trustStorePassword){
        /*if(checkParams(keyStoreFile, keyStorePassword, trustStoreFile)){*/
            this.keyStoreFile = keyStoreFile;
            this.keyStorePassword = keyStorePassword;
            this.trustStoreFile = trustStoreFile;
            this.trustStorePassword = trustStorePassword;
        /*}
        else {
            throw new IllegalArgumentException();
        }*/
    }
    
    /**
     * Check if params are not nul and not empty
     * @param keyStoreFile2
     * @param keyStorePassword2
     * @return True if not null and not empty, false otherwise
     */
    // TODO: check input parameters to vaoid null pointerException 
    private boolean checkParams(String keyStoreFile2, String keyStorePassword2, String trustStoreFile2) {
        if(keyStoreFile2 != null && !"".equals(keyStoreFile2) 
                && keyStorePassword2 != null && !"".equals(keyStorePassword2)
                && trustStoreFile2 != null && !"".equals(trustStoreFile2)
                ){
            return true;
        }
        else return false;
    }

    /**
     * Configure an HttpClient to use SSL security protocol with default parameters
     * @param httpClient The http client to configure
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException
     */
    public void configureHttpClientSSL(HttpClient httpClient) 
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, UnrecoverableKeyException {

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream ksInstream = new FileInputStream(new File(keyStoreFile));
        FileInputStream tsInstream = new FileInputStream(new File(trustStoreFile));
        try {
            keyStore.load(ksInstream, keyStorePassword.toCharArray());
            trustStore.load(tsInstream, trustStorePassword.toCharArray());
        } finally {
            try { 
                ksInstream.close();
                tsInstream.close();
            } 
            catch (Exception ignore) {}
        }

        SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore, keyStorePassword, trustStore);
        
        // TODO : Required in the test to avoid a hostname problem
        socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        
        Scheme sch = new Scheme("https", SSL_PORT, socketFactory);
        httpClient.getConnectionManager().getSchemeRegistry().register(sch);        
    }
    
}
