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

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.api.DirectoryService;
import com.google.inject.Inject;

/**
 * Tests vocabulary services
 * @author mkalam-alami, mdutoo
 *
 */
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
    public void testVocabulary() throws Exception {
    	List<Directory> dirList = dirService.getDirectories();
    	
    	// Check test environment
  	  	Assume.assumeNotNull(dirList);

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