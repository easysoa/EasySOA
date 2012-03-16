package org.easysoa.sca.frascati;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.Service;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.registry.frascati.EasySOAApiFraSCAti;
import org.easysoa.registry.frascati.NxFraSCAtiRegistryService;
import org.easysoa.sca.IScaRuntimeImporter;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.easysoa.sca.visitors.LocalBindingVisitorFactory;
import org.easysoa.services.DocumentService;
import org.easysoa.test.EasySOACoreTestFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.easysoa.test.RepositoryLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.frascati.test.FraSCAtiFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.ow2.frascati.util.FrascatiException;

import com.google.inject.Inject;

/**
 * Tests FraSCAti SCA "import" & "(runtime startup) discovery" working with the local
 * EasySOA API, done from within Nuxeo.
 * 
 * @author jguillemotte
 */
@RunWith(FeaturesRunner.class) @Features(
{ EasySOACoreTestFeature.class, FraSCAtiFeature.class }) @Deploy(
{
        "org.easysoa.registry.frascati",
        "org.nuxeo.ecm.platform.jbpm.core",
        "org.nuxeo.ecm.platform.audit", 
        "org.nuxeo.ecm.platform.preview"
})// @Jetty(config="src/test/resources/jetty.xml", port=EasySOAConstants.NUXEO_TEST_PORT)
@RepositoryConfig(type = BackendType.H2, user = "Administrator",
        init = EasySOARepositoryInit.class) public class LocalApiFrascatiImportServiceTest
{
    static final Log log = LogFactory
            .getLog(LocalApiFrascatiImportServiceTest.class);
    @Inject CoreSession session;
    @Inject DocumentService docService;
    // @Inject ResourceService resourceService;
    DocumentModel parentAppliImplModel;
    // @Inject ScaImporterComponent scaImporterComponent;
    @Inject NxFraSCAtiRegistryService frascatiRegistryService;

    @Before public void setUp() throws ClientException, MalformedURLException
    {
        log.debug("service  = " + frascatiRegistryService);
        // FraSCAti
        assertNotNull("Cannot get FraSCAti service component",
                frascatiRegistryService);
        // Find or create appli
        String appliUrl = "http://localhost";
        parentAppliImplModel = docService.findAppliImpl(session, appliUrl);
        if (parentAppliImplModel == null)
        {
            String title = "Test Appli Title";
            parentAppliImplModel =
                    docService.createAppliImpl(session, appliUrl);
            parentAppliImplModel.setProperty("dublincore", "title", title);
            session.saveDocument(parentAppliImplModel);
            session.save();
            // NB. created documents are auto deleted at the end, so no need for :
            // session.removeDocument(parentAppliImplModel.getRef());
        }
    }

    /**
     * Tests import of SCA composite file, done from within Nuxeo. IMPORTANT since
     * "import" is a Nuxeo-side functionality. However, it could be refactored to be
     * triggered in "discovery" fashion, i.e. from Intent within FraSCAti. Within the
     * composite, referenced classes are missing so there should be (non-fatal) warnings.
     * 
     * @throws Exception
     *             If a problem occurs
     */
    @Test 
    public void testSCACompositeImport() throws Exception
    {
        // SCA composite file to import :
        // to load a file, we use simply File, since user.dir is set relatively to the
        // project
        log.debug("FrascatiService = " + frascatiRegistryService);
        String scaFilePath =
                "src/test/resources/"
                        + "org/easysoa/sca/RestSoapProxy.composite";
        File scaFile = new File(scaFilePath);
        BindingVisitorFactory bindingVisitorFactory =
                new LocalBindingVisitorFactory(session);
        ApiFraSCAtiScaImporter importer =
                new ApiFraSCAtiScaImporter(bindingVisitorFactory, scaFile,
                        frascatiRegistryService);
        // importer.setParentAppliImpl(session.getDocument(new
        // IdRef(parentAppliImplModel.getId())));
        importer.setAppliImplURL((String) parentAppliImplModel.getProperty(
                AppliImpl.SCHEMA, AppliImpl.PROP_URL));
        importer.setServiceStackType("FraSCAti");
        importer.setServiceStackUrl("/");
        try
        {
            importer.importSCAComposite();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        DocumentModelList resDocList;
        DocumentModel resDoc;
        // Log repository
        new RepositoryLogger(session, "Repository state after import")
                .logAllRepository();
        // services :
        resDocList =
                session.query("SELECT * FROM Document WHERE ecm:primaryType = '"
                        + Service.DOCTYPE
                        + "' AND "
                        + "dc:title"
                        + " = '"
                        + "restInterface"
                        + "' AND ecm:currentLifeCycleState <> 'deleted' AND ecm:isProxy = 0");
        assertEquals(1, resDocList.size());
        resDoc = resDocList.get(0);
        assertEquals("/Proxy/restInterface", resDoc.getProperty(
                EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH));
    }

    /**
     * Tests runtime discovery of services when FraSCAti starts an application. SOMEWHAT
     * IMPORTANT for the "runtime discovery within FraSCAti embedded in Nuxeo" use case,
     * however it could be hidden behind a "business door" like the FraSCAti Studio API to
     * create on-demand proxies (see #100)
     * 
     * @throws FrascatiException
     * @throws Exception
     */
    @Test
    public void testFraSCAtiRuntimeDiscovery() throws FrascatiException,
            Exception
    {
        String scaZipFilePath =
                "src/test/resources/" + "proxy-1.0-SNAPSHOT.jar";
        File scaZipFile = new File(scaZipFilePath);
        IScaRuntimeImporter runtimeScaImporter =
                EasySOAApiFraSCAti.getInstance().newRuntimeScaImporter();
        frascatiRegistryService.getFraSCAti().setScaImporterRecipient(
                runtimeScaImporter);

        String myCompositeApplication =
                frascatiRegistryService.getFraSCAti().processComposite(
                        "RestSoapProxy.composite", FraSCAtiServiceItf.check,
                        scaZipFile.toURI().toURL());
        // services :
        DocumentModelList resDocList =
                session.query("SELECT * FROM Document WHERE ecm:primaryType = '"
                        + Service.DOCTYPE
                        + "' AND "
                        + "dc:title"
                        + " = '"
                        + "restInterface"
                        + "' AND ecm:currentLifeCycleState <> 'deleted' AND ecm:isProxy = 0");
        assertTrue(1 <= resDocList.size()); // TODO more precise assertion
    }
}
