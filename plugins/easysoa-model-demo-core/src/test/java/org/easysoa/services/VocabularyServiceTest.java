package org.easysoa.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.test.EasySOAFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

/**
 * Tests vocabulary services
 * @author mkalam-alami, mdutoo
 *
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOAFeature.class)
@Deploy({
	"org.nuxeo.runtime.datasource", // required, else errors : [DataSourceHelper] Unknown JNDI Context class: null , [SQLDirectory] dataSource lookup failed , "javax.naming.NoInitialContextException: Need to specify class name...
    "org.nuxeo.ecm.directory.types.contrib:OSGI-INF/DirectoryTypes.xml", // required, else no vocabulary schema in database
    "org.nuxeo.ecm.directory",
    "org.nuxeo.ecm.directory.api",
    "org.nuxeo.ecm.directory.sql", // all required, else no dirService
    "org.easysoa.demo.core:OSGI-INF/nxdirectories-contrib.xml" // required, else no custom easysoa vocabularies
})
@LocalDeploy("org.easysoa.demo.core:org/easysoa/tests/datasource-contrib.xml")
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class VocabularyServiceTest {

    static final Log log = LogFactory.getLog(VocabularyServiceTest.class);
    
    @Inject CoreSession session;

    @Inject VocabularyHelper vocService;
    
    @Inject DirectoryService dirService;

    @Before
    public void setUp() throws Exception {
  	  	assertNotNull("Cannot get directory service component", dirService);
    }
    
    @Test
    public void testVocabulary() throws Exception {
    	List<Directory> dirList = dirService.getDirectories();
  	  	assertNotNull(dirList);
  	  	assertEquals(dirList.size(), 3);

		String environment = "test test environment";
		
		// none yet :
		vocService.removeEntry(session,
				VocabularyHelper.VOCABULARY_ENVIRONMENT,
				environment); // in case previous tests let crap be there
		assertTrue(!vocService.entryExists(session,
				VocabularyHelper.VOCABULARY_ENVIRONMENT, environment));

		// adding one :
		vocService.addEntry(session,
				VocabularyHelper.VOCABULARY_ENVIRONMENT,
				environment, environment);
		assertTrue(vocService.entryExists(session,
				VocabularyHelper.VOCABULARY_ENVIRONMENT, environment));

		// removing it :
		vocService.removeEntry(session,
				VocabularyHelper.VOCABULARY_ENVIRONMENT,
				environment);
		assertTrue(!vocService.entryExists(session,
				VocabularyHelper.VOCABULARY_ENVIRONMENT, environment));
    }

}