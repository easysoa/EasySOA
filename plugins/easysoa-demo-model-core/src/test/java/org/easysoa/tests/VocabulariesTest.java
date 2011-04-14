package org.easysoa.tests;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.nuxeo.ecm.directory.DirectoryServiceImpl;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.directory.sql.SQLDirectoryFactory;
import org.nuxeo.runtime.test.NXRuntimeTestCase;

/**
 * (Currently failed) attempt to set up en environment to test vocabulary services
 * @author mkalam-alami
 *
 */
public class VocabulariesTest extends NXRuntimeTestCase {

    static final Logger log = Logger.getLogger(VocabulariesTest.class);
    
    static DirectoryServiceImpl dirService;
    static SQLDirectoryFactory dirFactory;

    @Override
    public void setUp() throws Exception {
    	
        super.setUp();
        
    	deployBundle("org.nuxeo.ecm.directory");
    	deployBundle("org.nuxeo.ecm.directory.api");
    	deployBundle("org.nuxeo.ecm.directory.sql");
    	
        Logger.getRootLogger().setLevel(Level.INFO);
        
    	dirService = (DirectoryServiceImpl) runtime.getComponent(DirectoryService.NAME);
  	  	assertNotNull("Cannot get directory service component", dirService);
    	
    }
    
    @Override
    public void tearDown() throws Exception {
        Logger.getRootLogger().setLevel(Level.ERROR);
    	super.tearDown();
    }
    
    @Test
    public void test() {
    	log.info("hi!");
    }

}