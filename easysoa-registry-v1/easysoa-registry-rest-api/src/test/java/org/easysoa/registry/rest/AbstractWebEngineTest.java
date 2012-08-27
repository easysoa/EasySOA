package org.easysoa.registry.rest;

import java.net.URL;
import java.net.URLConnection;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;
import org.easysoa.registry.test.EasySOAFeature;
import org.easysoa.registry.test.PathExtractor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

@RunWith(FeaturesRunner.class)
@Features({EasySOAFeature.class, WebEngineFeature.class})
@Jetty(port=AbstractWebEngineTest.PORT)
@RepositoryConfig(cleanup = Granularity.METHOD)
public class AbstractWebEngineTest {
    
    public static final int PORT = 8082;

    private static final String NUXEO_PATH = "http://localhost:" + PORT + "/";

    private static final Logger logger = Logger.getLogger(AbstractWebEngineTest.class);

    private final ClientConfig clientConfig;
    
    public AbstractWebEngineTest(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
    public AbstractWebEngineTest() {
        this(createClientConfig());
    }
    
    @Inject
    protected CoreSession documentManager;

    @Rule
    public TestName name = new TestName();

    @Before
    public void testAvailability() {
        try {
            URLConnection connection = new URL(NUXEO_PATH).openConnection();
            connection.connect();
        }
        catch (Exception e) {
            String message = "Testing environment issue: cannot reach test WebEngine URL";
            logger.error(message, e);
            Assert.fail(message);
        }
    }

    private static ClientConfig createClientConfig() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getSingletons().add(new JsonMessageReader());
        clientConfig.getSingletons().add(new JsonMessageWriter());
        return clientConfig;
    }

    public Client createAuthenticatedHTTPClient() {
        return createAuthenticatedHTTPClient("Administrator", "Administrator");
    }
    
    public Client createAuthenticatedHTTPClient(String username, String password) {
        Client client = Client.create(clientConfig);
        client.addFilter(new HTTPBasicAuthFilter("Administrator", "Administrator"));
        return client;
    }
    
    public String getURL(Class<?> c) {
        return NUXEO_PATH + PathExtractor.getPath(c);
    }

    public String getURL(Class<?> c, String methodName, Class<?>... parameterTypes) throws SecurityException, NoSuchMethodException {
        return NUXEO_PATH + PathExtractor.getPath(c, methodName, parameterTypes);
    }
    
    public void logTestName(Logger logger) {
        logger.debug("--------------------");
        logger.debug(name.getMethodName());
        logger.debug("--------------------");
    }
}
