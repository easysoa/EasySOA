package org.easysoa.runtime;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
		
	}
	
}
