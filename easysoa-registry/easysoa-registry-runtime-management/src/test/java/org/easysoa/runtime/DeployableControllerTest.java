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

package org.easysoa.runtime;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.easysoa.runtime.api.DeployableController;
import org.easysoa.runtime.copypaste.CopyPasteServer;
import org.easysoa.runtime.maven.MavenDeployableDescriptor;
import org.easysoa.runtime.maven.MavenID;
import org.easysoa.runtime.maven.MavenRepository;
import org.easysoa.runtime.utils.FileProvider;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.common.utils.FileUtils;

public class DeployableControllerTest {

	//private static Logger logger = Logger.getLogger(CopyPasteTest.class);
	
	private static final String ROOT = "target/test-classes/";
	
	private static final String FILE_REPOSITORY_FOLDER = ROOT + "files/";
	private static final String SERVER_FOLDER = ROOT + "server/";
	
	private static final String FILE_DEPLOYABLE_NAME = "hello.jar";
	private static final MavenID MAVEN_DEPLOYABLE_ID = new MavenID("org.apache.maven", "maven-archetype-core", "1.0-alpha-3");
	
	@Before
	public void clearServer() throws IOException {
		FileUtils.deleteTree(new File(SERVER_FOLDER));
	}
	
	@Test
	public void testDeployableController() throws IOException {

		CopyPasteServer copyPasteServer = new CopyPasteServer(new File(SERVER_FOLDER));
		DeployableController deployableController = new DeployableController(copyPasteServer);
		
		MavenRepository mavenRepository = new MavenRepository(new URL("http://search.maven.org/remotecontent?filepath="));
		deployableController.addDeployableProvider(mavenRepository);

		FileProvider filesProvider = new FileProvider(FILE_REPOSITORY_FOLDER);
		deployableController.addDeployableProvider(filesProvider);
		
		// Deploy various types of deployables
		MavenDeployableDescriptor deployableDescriptor = mavenRepository.fetchDeployableDescriptor(MAVEN_DEPLOYABLE_ID);
		deployableController.deployWithDependencies(deployableDescriptor);
		deployableController.deploy(FILE_DEPLOYABLE_NAME); 
		
		deployableController.startServer();
		
		// Should be 9, but 2 dependencies lack their version number (unsupported right now)
		Assert.assertTrue(new File(SERVER_FOLDER).list().length >= 7);
		
	}
	
}
