package org.easysoa.tests;

import static org.easysoa.doctypes.AppliImpl.PROP_ENVIRONMENT;
import static org.easysoa.doctypes.AppliImpl.PROP_SERVER;
import static org.easysoa.doctypes.AppliImpl.SCHEMA;
import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.sca.EasySOAFeature;
import org.easysoa.services.VocabularyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.runtime.test.runner.JettyFeature;

import com.google.inject.Inject;

/**
 * (Currently failed) attempt to set up en environment to test vocabulary services
 * @author mkalam-alami
 *
 */
@RunWith(FeaturesRunner.class)
@Deploy({ // all required, else no dirService
    //"org.mortbay.jetty.plus",
    "org.nuxeo.ecm.directory",
    "org.nuxeo.ecm.directory.api",
    "org.nuxeo.ecm.directory.sql",
    //"org.nuxeo.ecm.directory.sql:OSGI-INF/SQLDirectoryFactory.xml",
    //"org.nuxeo.ecm.config",
    "org.easysoa.demo.core:OSGI-INF/nxdirectories-contrib.xml" // required, else no custom vocabularies
})
@Features(EasySOAFeature.class)
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=DefaultRepositoryInit.class)
//@Features(JettyFeature.class)
//@Jetty(config="/home/mdutoo/dev/easysoa/nuxeo-dm-5.3.2-jetty/config/jetty.xml") //,port=9980
public class VocabulariesTest {

    static final Log log = LogFactory.getLog(VocabulariesTest.class);
    
    @Inject DirectoryService dirService;
    @Inject CoreSession session;

    @Before
    public void setUp() throws Exception {
  	  	assertNotNull("Cannot get directory service component", dirService);
    }
    
    @Test
    public void test() throws Exception {
    	log.info("hi!");
  	  	// TODO the vocabulary test !!
    	List<Directory> dirList = dirService.getDirectories();
  	  	assertNotNull(dirList);
  	  	assertEquals(dirList.size(), 3);

		String environment = "test test environment";
		
		// the following fails because of missing container with :
		
		/*
		
DatabaseHelper: Database used for VCS tests: org.nuxeo.ecm.core.storage.sql.DatabaseH2
ERROR [DataSourceHelper] Unknown JNDI Context class: null
ERROR [SQLDirectory] dataSource lookup failed
javax.naming.NoInitialContextException: Need to specify class name in environment or system property, or as an applet parameter, or in an application resource file:  java.naming.factory.initial
	at javax.naming.spi.NamingManager.getInitialContext(NamingManager.java:645)
	at javax.naming.InitialContext.getDefaultInitCtx(InitialContext.java:288)
	at javax.naming.InitialContext.getURLOrDefaultInitCtx(InitialContext.java:325)
	at javax.naming.InitialContext.lookup(InitialContext.java:392)
	at org.nuxeo.runtime.api.DataSourceHelper.getDataSource(DataSourceHelper.java:146)
	at org.nuxeo.ecm.directory.sql.SQLDirectory.getDataSource(SQLDirectory.java:196)
	at org.nuxeo.ecm.directory.sql.SQLDirectory.getConnection(SQLDirectory.java:215)
	at org.nuxeo.ecm.directory.sql.SQLDirectory.buildDialect(SQLDirectory.java:305)
	at org.nuxeo.ecm.directory.sql.SQLDirectory.<init>(SQLDirectory.java:90)
	at org.nuxeo.ecm.directory.sql.SQLDirectoryProxy.getDirectory(SQLDirectoryProxy.java:50)
	at org.nuxeo.ecm.directory.sql.SQLDirectoryProxy.getSession(SQLDirectoryProxy.java:83)
	at org.nuxeo.ecm.directory.DirectoryServiceImpl.open(DirectoryServiceImpl.java:168)
	at org.easysoa.services.VocabularyService.entryExists(VocabularyService.java:45)
	at org.easysoa.tests.VocabulariesTest.test(VocabulariesTest.java:69)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
	at java.lang.reflect.Method.invoke(Method.java:597)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
	at org.nuxeo.runtime.test.runner.FeaturesRunner$InvokeMethod.evaluate(FeaturesRunner.java:253)
	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:28)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:76)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:193)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:52)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:191)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:42)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:184)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:236)
	at org.nuxeo.runtime.test.runner.FeaturesRunner.run(FeaturesRunner.java:211)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:49)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:467)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:683)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:390)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)
		 */
	
		/*
		// none yet :
		assertTrue(!VocabularyService.entryExists(session,
				VocabularyService.VOCABULARY_ENVIRONMENT, environment));
		
		// adding one :
		VocabularyService.addEntry(session,
				VocabularyService.VOCABULARY_ENVIRONMENT,
				environment, environment);
		assertTrue(VocabularyService.entryExists(session,
				VocabularyService.VOCABULARY_ENVIRONMENT, environment));

		// removing it :
		VocabularyService.addEntry(session,
				VocabularyService.VOCABULARY_ENVIRONMENT,
				environment, environment);
		assertTrue(!VocabularyService.entryExists(session,
				VocabularyService.VOCABULARY_ENVIRONMENT, environment));
				*/
    }

}