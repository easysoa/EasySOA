package org.easysoa.registry.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.nuxeo.ecm.core.api.CoreSession;

import com.google.inject.Inject;

public class AbstractRepositoryTest {
    
    @Inject
    protected CoreSession documentManager;

    protected RepositoryLogger repositoryLogger;

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() {
        repositoryLogger = new RepositoryLogger(documentManager);
    }
    
    @After
    public void logRepository() {
        repositoryLogger.setTitle(name.getMethodName());
        repositoryLogger.logAllRepository();
    }
    
}
