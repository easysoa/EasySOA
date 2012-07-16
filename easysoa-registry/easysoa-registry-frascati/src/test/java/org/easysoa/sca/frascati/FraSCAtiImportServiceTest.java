/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.sca.frascati;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.extension.ScaImporterComponent;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.easysoa.sca.visitors.LocalBindingVisitorFactory;
import org.easysoa.services.DocumentService;
import org.easysoa.test.EasySOACoreTestFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.easysoa.test.RepositoryLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.frascati.test.FraSCAtiFeature;
import org.nuxeo.runtime.services.resource.ResourceService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * Tests SCA import with FraSCAti
 * 
 * @author mdutoo
 */
//TODO Update with new API
@RunWith(FeaturesRunner.class) 
@Features({ EasySOACoreTestFeature.class, FraSCAtiFeature.class }) 
@Deploy({ "org.easysoa.registry.frascati" })
@RepositoryConfig(type = BackendType.H2, user = "Administrator", init = EasySOARepositoryInit.class) 
public class FraSCAtiImportServiceTest
{
    static final Log log = LogFactory.getLog(FraSCAtiImportServiceTest.class);
    @Inject CoreSession session;
    @Inject DocumentService docService;
    @Inject ResourceService resourceService;
    DocumentModel parentAppliImplModel;
    @Inject ScaImporterComponent scaImporterComponent;
   // @Inject NxFraSCAtiRegistryService frascatiRegistryService;

    @Before 
    public void setUp() throws Exception
    {
        // FraSCAti
        //assertNotNull("Cannot get FraSCAti service component", frascatiRegistryService);
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
        }
    }
    
    @Test 
    //@Ignore 
    public void importSCAZipSimple() throws Exception
    {
        // SCA composite file to import :
        // to load a file, we use simply File, since user.dir is set relatively to the
        // project
        String scaFilePath =
                "src/test/resources/" + "proxy-simple-1.0-SNAPSHOT.jar";
        File scaFile = new File(scaFilePath);
        // NB. on the opposite, ResourceService does not work (or maybe with additional
        // contributions ?)
        // URL a =
        // resourceService.getResource("org/easysoa/tests/RestSoapProxy.composite");
        BindingVisitorFactory visitorFactory =
                new LocalBindingVisitorFactory(session);
        FraSCAtiScaImporter importer =
                new FraSCAtiScaImporter(visitorFactory, scaFile); // TODO put FileBlob
                                                                  // back in orig test
        // importer.setParentAppliImpl(session.getDocument(new
        // IdRef(parentAppliImplModel.getId())));
        importer.setAppliImplURL((String) parentAppliImplModel.getProperty(
                AppliImpl.SCHEMA, AppliImpl.PROP_URL));
        importer.setServiceStackType("FraSCAti");
        importer.setServiceStackUrl("/");
        importer.importSCAZip();
        
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
                        + "' AND ecm:currentLifeCycleState <> 'deleted'");
        assertEquals(1, resDocList.size());
        resDoc = resDocList.get(0);
        assertEquals("/Proxy/restInterface", resDoc.getProperty(
                EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH));
        resDocList =
                session.query("SELECT * FROM Document WHERE ecm:primaryType = '"
                        + Service.DOCTYPE
                        + "' AND "
                        + "dc:title"
                        + " = '"
                        + "ProxyService"
                        + "' AND ecm:currentLifeCycleState <> 'deleted'");
        assertEquals(1, resDocList.size());
        resDoc = resDocList.get(0);
        assertEquals("/ProxyService", resDoc.getProperty(
                EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH));
        // references :
        /*
         * resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" +
         * Service.DOCTYPE + "' AND " + EasySOADoctype.SCHEMA_COMMON_PREFIX +
         * EasySOADoctype.PROP_ARCHIPATH + " = '" + "ws" +
         * "' AND ecm:currentLifeCycleState <> 'deleted'"); assertEquals(1,
         * resDocList.size()); resDocList =
         * session.query("SELECT * FROM Document WHERE ecm:primaryType = '" +
         * Service.DOCTYPE + "' AND " + EasySOADoctype.SCHEMA_COMMON_PREFIX +
         * EasySOADoctype.PROP_ARCHIPATH + " = '" + "/ProxyUnused/ws" +
         * "' AND ecm:currentLifeCycleState <> 'deleted'"); assertEquals(1,
         * resDocList.size());
         */
        // api :
        /*
         * DocumentModel apiModel = docService.findServiceApi(session,
         * "http://127.0.0.1:9010"); assertEquals("PureAirFlowers API",
         * apiModel.getTitle());
         */
    }

    /**
     * The following FraSCAti parsing-based import would fail without custom
     * ProcessingContext.loadClass() because of unknown class in zip
     */
    @Test 
    //@Ignore 
    public void importSCAZip() throws Exception
    {
        // SCA composite file to import :
        // to load a file, we use simply File, since user.dir is set relatively to the
        // project
        String scaFilePath = "src/test/resources/" + "proxy-1.0-SNAPSHOT.jar";
        File scaFile = new File(scaFilePath);
        // NB. on the opposite, ResourceService does not work (or maybe with additional
        // contributions ?)
        // URL a =
        // resourceService.getResource("org/easysoa/tests/RestSoapProxy.composite");
        BindingVisitorFactory visitorFactory =
                new LocalBindingVisitorFactory(session);
        FraSCAtiScaImporter importer =
                new FraSCAtiScaImporter(visitorFactory, scaFile);
        // importer.setParentAppliImpl(session.getDocument(new
        // IdRef(parentAppliImplModel.getId())));
        importer.setAppliImplURL((String) parentAppliImplModel.getProperty(
                AppliImpl.SCHEMA, AppliImpl.PROP_URL));
        importer.setServiceStackType("FraSCAti");
        importer.setServiceStackUrl("/");
        importer.importSCAZip();
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

    @Test 
    //@Ignore 
    public void testSCAComposite() throws Exception
    {
        // SCA composite file to import :
        // to load a file, we use simply File, since user.dir is set relatively to the
        // project
        String scaFilePath =
                "src/test/resources/"
                        + "org/easysoa/sca/RestSoapProxy.composite";
        File scaFile = new File(scaFilePath);
        // NB. on the opposite, ResourceService does not work (or maybe with additional
        // contributions ?)
        // URL a =
        // resourceService.getResource("org/easysoa/tests/RestSoapProxy.composite");
        BindingVisitorFactory visitorFactory =
                new LocalBindingVisitorFactory(session);
        FraSCAtiScaImporter importer =
                new FraSCAtiScaImporter(visitorFactory, scaFile);
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
     * Test the frascati SCA importer deployed as a Nuxeo extension point
     */
    @Test 
    //@Ignore 
    public void testFrascatiScaImporter() throws Exception
    {
        // SCA composite file to import :
        // to load a file, we use simply File, since user.dir is set relatively to the
        // project
        // String scaFilePath = "src/test/resources/" +
        // "org/easysoa/sca/RestSoapProxy.composite";
        // With this sample, no problem, all the required (specified in the composite
        // file) classes are in a single jar
        String scaFilePath = "src/test/resources/" + "proxy-1.0-SNAPSHOT.jar";
        File scaFile = new File(scaFilePath);
        // Getting the importer
        BindingVisitorFactory visitorFactory =
                new LocalBindingVisitorFactory(session);
        IScaImporter importer =
                scaImporterComponent.createScaImporter(visitorFactory, scaFile);
        // IScaImporter importer = scaImporterComponent.createScaImporter(session,
        // scaFile);
        // If importer is null, we have a problem
        assertNotNull(importer);
        // importer.setParentAppliImpl(session.getDocument(new
        // IdRef(parentAppliImplModel.getId())));
        importer.setAppliImplURL((String) parentAppliImplModel.getProperty(
                AppliImpl.SCHEMA, AppliImpl.PROP_URL));
        importer.setServiceStackType("FraSCAti");
        importer.setServiceStackUrl("/");
        importer.importSCA();
        // Check import results
        DocumentModelList resDocList;
        DocumentModel resDoc;
        // Log repository
        new RepositoryLogger(session, "Repository state after import")
                .logAllRepository();
        // services :
        // No corresponding data in the imported sample jar
        /*
         * resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" +
         * Service.DOCTYPE + "' AND " + "dc:title" + " = '" + "restInterface" +
         * "' AND ecm:currentLifeCycleState <> 'deleted'"); assertEquals(1,
         * resDocList.size()); resDoc = resDocList.get(0);
         * assertEquals("/Proxy/restInterface",
         * resDoc.getProperty(EasySOADoctype.SCHEMA_COMMON,
         * EasySOADoctype.PROP_ARCHIPATH));;
         */
        resDocList =
                session.query("SELECT * FROM Document WHERE ecm:primaryType = '"
                        + Service.DOCTYPE
                        + "' AND "
                        + "dc:title"
                        + " = '"
                        + "ProxyService"
                        + "' AND ecm:currentLifeCycleState <> 'deleted' AND ecm:isProxy = 0");
        assertTrue(resDocList.size()>0);
        resDoc = resDocList.get(0);
        assertEquals("/ProxyService", resDoc.getProperty(
                EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH));;
        // references :
        resDocList =
                session.query("SELECT * FROM Document WHERE ecm:primaryType = '"
                        + ServiceReference.DOCTYPE
                        + "' AND "
                        + EasySOADoctype.SCHEMA_COMMON_PREFIX
                        + EasySOADoctype.PROP_ARCHIPATH
                        + " = '"
                        + "/Proxy/ws"
                        + "' AND ecm:currentLifeCycleState <> 'deleted' AND ecm:isProxy = 0");
        assertTrue(resDocList.size()>0);
        // No corresponding data in the imported sample jar
        /*
         * resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" +
         * ServiceReference.DOCTYPE + "' AND " + EasySOADoctype.SCHEMA_COMMON_PREFIX +
         * EasySOADoctype.PROP_ARCHIPATH + " = '" + "/ProxyUnused/ws" +
         * "' AND ecm:currentLifeCycleState <> 'deleted'"); assertEquals(1,
         * resDocList.size());
         */
        // api :
        DocumentModel apiModel =
                docService.findServiceApi(session, "http://127.0.0.1:9010");
        assertEquals("PureAirFlowers API", apiModel.getTitle());
    }

    @Test 
    public void testFrascatiClassNotFoundException() throws Exception
    {
        // With this sample, frascati throws a ClassNotFoundException because required
        // classes are in an other jar
        String scaFilePath = "src/test/resources/" + "easysoa-samples-smarttravel-trip-0.4.jar";
        File scaFile = new File(scaFilePath);
        boolean classNotFoundExceptionThrown = false;
        // Getting the importer
        BindingVisitorFactory visitorFactory = new LocalBindingVisitorFactory(session);
        IScaImporter importer =
                scaImporterComponent.createScaImporter(visitorFactory, scaFile);
        // If importer is null, we have a problem
        assertNotNull(importer);
        importer.setAppliImplURL((String) parentAppliImplModel.getProperty(
                AppliImpl.SCHEMA, AppliImpl.PROP_URL));
        importer.setServiceStackType("FraSCAti");
        importer.setServiceStackUrl("/");
        try
        {
            importer.importSCA();
        } catch (Exception e)
        {
            classNotFoundExceptionThrown = true;
            // e.printStackTrace();
        }
        assertTrue(classNotFoundExceptionThrown);
    }
}
