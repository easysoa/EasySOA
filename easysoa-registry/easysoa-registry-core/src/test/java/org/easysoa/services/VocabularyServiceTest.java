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

package org.easysoa.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.test.EasySOACoreTestFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * Tests vocabulary services
 * @author mkalam-alami, mdutoo
 *
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOACoreTestFeature.class)
@RepositoryConfig(init=EasySOARepositoryInit.class)
public class VocabularyServiceTest extends CoreServiceTestHelperBase {

    static final Log log = LogFactory.getLog(VocabularyServiceTest.class);
    
    @Inject CoreSession session;

    @Inject VocabularyHelper vocService;
    
    @Inject DirectoryService dirService;

    @Before
    public void setUp() throws Exception {
  	  	assertNotNull("Cannot get directory service component", dirService);
    }
    
    @Test
    @Ignore
    public void testVocabulary() throws Exception {
        
        /*
         * FIXME Configuration error
         * org.nuxeo.ecm.directory.DirectoryException: org.nuxeo.ecm.core.api.WrappedException:
         * Exception: org.nuxeo.ecm.directory.DirectoryException. message: dataSource lookup failed
         */
    	
        // Check test environment
  	  	Assume.assumeNotNull(dirService.getDirectories());

		String environment = "test test environment";
		
		// Check vocabulary registration
        assertNotNull("'Environment' vocabulary should be available",
                dirService.getDirectory(VocabularyHelper.VOCABULARY_ENVIRONMENT));
		
		// None yet :
		vocService.removeEntry(session,
				VocabularyHelper.VOCABULARY_ENVIRONMENT,
				environment); // in case previous tests let crap be there
		assertTrue("A not-yet-added entry should not exist", !vocService.entryExists(session,
				VocabularyHelper.VOCABULARY_ENVIRONMENT, environment));

		// Adding one :
		vocService.addEntry(session,
				VocabularyHelper.VOCABULARY_ENVIRONMENT,
				environment, environment);
		assertTrue("An added entry should exist", vocService.entryExists(session,
				VocabularyHelper.VOCABULARY_ENVIRONMENT, environment));

		// Removing it :
		vocService.removeEntry(session,
				VocabularyHelper.VOCABULARY_ENVIRONMENT,
				environment);
		assertTrue("A removed entry should not exist", !vocService.entryExists(session,
				VocabularyHelper.VOCABULARY_ENVIRONMENT, environment));
    }

}