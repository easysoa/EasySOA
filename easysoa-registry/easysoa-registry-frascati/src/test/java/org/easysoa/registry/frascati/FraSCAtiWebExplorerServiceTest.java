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

/**
 * EasySOA: OW2 FraSCAti in Nuxeo
 * Copyright (C) 2011 INRIA, University of Lille 1
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * Contact: frascati@ow2.org
 *
 * Author: Philippe Merle
 *
 * Contributor(s):
 *
 */

package org.easysoa.registry.frascati;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import com.google.inject.Inject;

/**
 * Tests FraSCAti Web Explorer service.
 * This class doesn't works yet, just to keep a trace of the research made to integrate FraSCAti web explorer to Nuxeo
 *
 */

@RunWith(FeaturesRunner.class)
public class FraSCAtiWebExplorerServiceTest {

    static final Log log = LogFactory.getLog(FraSCAtiWebExplorerServiceTest.class);
    
    @Inject NxFraSCAtiService frascatiService;
    
    @Before
    public void setUp() throws Exception {
  	  	//assertNotNull("Cannot get FraSCAti service component", frascatiWebExplorerService);
    }
    
    /**
     * Test the start and the stop of frascati web explorer. This test stop and wait for a manual intervention to continue
     * @throws Exception
     */
    @Test
    @Ignore
    public void waitUntilRead() throws Exception {
		// Starting Frascati web explorer
    	log.info("Test started, just push a key (then enter) in the console window to stop the test !");
		// Just push a key in the console window to stop the test
		System.in.read();
		log.info("Test stopping !");		
    }
    
}
