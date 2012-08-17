package org.easysoa.registry.test;

import java.net.URL;
import java.net.URLConnection;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({EasySOAFeature.class, WebEngineFeature.class})
@Jetty(port=AbstractWebEngineTest.PORT)
@Deploy("org.easysoa.registry.rest")
@RepositoryConfig(cleanup = Granularity.METHOD)
public class AbstractWebEngineTest {
    
    public static final int PORT = 8082;

    private final static Logger logger = Logger.getLogger(AbstractWebEngineTest.class);
    
    @Inject
    protected CoreSession documentManager;

    protected RepositoryLogger repositoryLogger;

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() {
        repositoryLogger = new RepositoryLogger(documentManager);
    }

    @Before
    public void testAvailability() {
        try {
            URLConnection connection = new URL("http://localhost:" + PORT).openConnection();
            connection.connect();
        }
        catch (Exception e) {
            String message = "Testing environment issue: cannot reach test WebEngine URL";
            logger.error(message, e);
            Assert.fail(message);
        }
    }
    
    @After
    public void logRepository() {
        repositoryLogger.setTitle(name.getMethodName());
        repositoryLogger.logAllRepository();
    }
    
}
