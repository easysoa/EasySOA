package org.easysoa.registry.rest;

import org.easysoa.registry.test.AbstractWebEngineTest;
import org.junit.Test;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;

@RepositoryConfig(cleanup = Granularity.CLASS)
public class RestApiAvailabilityTest extends AbstractWebEngineTest {

    @Test
    public void helloWorld() {
        // Nothing
    }
    
}
