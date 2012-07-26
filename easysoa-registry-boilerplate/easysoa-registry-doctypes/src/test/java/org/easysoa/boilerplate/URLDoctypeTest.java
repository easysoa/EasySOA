package org.easysoa.boilerplate;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(EasySOADoctypesFeature.class)
@RepositoryConfig(init=DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class URLDoctypeTest {
    
    private static final String URL_DOCTYPE = "URL";

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(URLDoctypeTest.class);
    
    @Inject CoreSession coreSession;
    
    @Test public void testURLDoctypeAvailability() {
        Assert.assertNotNull("URL doctype should exist", coreSession.getDocumentType(URL_DOCTYPE));
    }
    
    @Test public void testURLCreation() throws ClientException {
        // Create document
        String expectedUrl = "http://hello.world.com";
        DocumentModel urlModel = coreSession.createDocumentModel(URL_DOCTYPE);
        urlModel.setProperty("url", "url", expectedUrl);
        coreSession.createDocument(urlModel);
        coreSession.save();
        
        // Fetch it
        DocumentModelList urlModelList = coreSession.query("SELECT * FROM URL");
        Assert.assertEquals("Document should have been saved",
                1, urlModelList.size());
        urlModel = urlModelList.get(0);
        Assert.assertEquals("Document should store the expected URL",
                expectedUrl, urlModel.getProperty("url", "url"));
    }
    
}
