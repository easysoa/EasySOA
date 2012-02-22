package org.easysoa.nxql;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.api.EasySOAApiSession;
import org.easysoa.api.EasySOADocument;
import org.easysoa.api.EasySOALocalApiFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.services.DocumentService;
import org.easysoa.test.EasySOACoreTestFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * Tests the use of NXQL requests against the SOA model.
 * 
 * @author mkalam-alami
 *
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOACoreTestFeature.class)
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class NXQLTest {
    
    private static final String APP_URL = "http://www.myservices.com";
    private static final String API_URL = "http://www.myservices.com/api";
    private static final String SERVICE_URL = "http://www.myservices.com/service";
    
    static final Log log = LogFactory.getLog(NXQLTest.class);

    @Inject DocumentService docService;
    
    @Inject CoreSession session;
    
    private EasySOAApiSession api = null;
    
    private DocumentModel appliImplModel = null;

    /* NXQL Reference: http://doc.nuxeo.com/display/NXDOC/NXQL} */
    
    /**
     * Fills the registry
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        if (api == null) {
        
            api = EasySOALocalApiFactory.createLocalApi(session);
            EasySOADocument doc = null;
            
            // Application
            
            Map<String, String> appProps = new HashMap<String, String>();
            appProps.put(AppliImpl.PROP_URL, APP_URL);
            appProps.put(AppliImpl.PROP_TITLE, "My App");
            appProps.put(AppliImpl.PROP_DOMAIN, "CRM");
            doc = api.notifyAppliImpl(appProps);
            appliImplModel = session.getDocument(new IdRef(doc.getId()));
            
            // API in which the services will be contained
            Map<String, String> apiProps = new HashMap<String, String>();
            apiProps.put(ServiceAPI.PROP_PARENTURL, APP_URL); // Ensures in which app this will be stored
            apiProps.put(ServiceAPI.PROP_URL, API_URL);
            apiProps.put(ServiceAPI.PROP_TITLE, "My API");
            api.notifyServiceApi(apiProps);
    
            // A few services
            Map<String, String> serviceProps = new HashMap<String, String>();
            for (int i = 1; i < 10; i++) {
                serviceProps.put(Service.PROP_PARENTURL, API_URL); // Ensures in which API this will be stored
                serviceProps.put(Service.PROP_URL, SERVICE_URL + i);
                serviceProps.put(Service.PROP_TITLE, "My Service #" + i);
                serviceProps.put(Service.PROP_PARTICIPANTS, "My company"); // The service participants
                api.notifyService(serviceProps);
            }
        
        }
    }
    
    @Test
    public void findServicesFromSameApplication() throws ClientException {
        String appliImplPath = appliImplModel.getPathAsString();
        DocumentModelList list = session.query("SELECT * FROM Service WHERE ecm:path STARTSWITH '" + appliImplPath + "'");
        Assert.assertEquals(list.size(), 9);
        traceList(list);
    }
    
    @Test
    public void findServicesByName() throws ClientException {
        String name = "Service #1";
        DocumentModelList list = session.query("SELECT * FROM Service WHERE dc:title LIKE '%" + name + "%'");
        Assert.assertEquals(list.size(), 1);
        traceList(list);
    }
    
    private void traceList(DocumentModelList list) throws ClientException {
        log.info("Found " + list.size() + " document(s):");
        for (DocumentModel model : list) {
            log.info(" * " + model.getTitle());
        }
    }

}
