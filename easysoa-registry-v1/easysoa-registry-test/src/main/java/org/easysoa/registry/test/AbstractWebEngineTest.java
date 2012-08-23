package org.easysoa.registry.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.Assert;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
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

@RunWith(FeaturesRunner.class)
@Features({EasySOAFeature.class, WebEngineFeature.class})
@Jetty(port=AbstractWebEngineTest.PORT)
@RepositoryConfig(cleanup = Granularity.METHOD)
public class AbstractWebEngineTest {
    
    public static final int PORT = 8082;

    private static final String NUXEO_PATH = "http://localhost:" + PORT + "/";

    private static final Logger logger = Logger.getLogger(AbstractWebEngineTest.class);
    
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
    
    public HttpClient createAuthenticatedHTTPClient() {
        return createAuthenticatedHTTPClient("Administrator", "Administrator");
    }
    
    public HttpClient createAuthenticatedHTTPClient(String username, String password) {
        HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);
        client.getState().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        return client;
    }

    public JSONArray getResultBodyAsJSONArray(HttpMethodBase method) throws IOException {
        return (JSONArray) getResultBodyAsJSON(method);
    }

    public JSONObject getResultBodyAsJSONObject(HttpMethodBase method) throws IOException {
        return (JSONObject) getResultBodyAsJSON(method);
    }

    private JSON getResultBodyAsJSON(HttpMethodBase method) throws IOException {
        InputStream is = null;
        try {
            is = method.getResponseBodyAsStream();
            String string = IOUtils.toString(is);
            return JSONSerializer.toJSON(string);
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
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
