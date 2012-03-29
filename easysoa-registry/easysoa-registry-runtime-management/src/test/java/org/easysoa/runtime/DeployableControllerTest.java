package org.easysoa.runtime;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.easysoa.runtime.api.event.OnDeployListener;
import org.easysoa.runtime.api.event.OnUndeployListener;
import org.easysoa.runtime.copypaste.CopyPasteServer;
import org.easysoa.runtime.maven.MavenDeployableDescriptor;
import org.easysoa.runtime.maven.MavenID;
import org.easysoa.runtime.maven.MavenRepository;
import org.easysoa.runtime.utils.FileDeployable;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.common.utils.FileUtils;

public class DeployableControllerTest {

	//private static Logger logger = Logger.getLogger(CopyPasteTest.class);
	
	private static final String ROOT = "target/test-classes/";
	private static final String FILE_REPOSITORY_FOLDER = ROOT + "files/";
	private static final String SERVER_FOLDER = ROOT + "server/";
	private static final String DEPLOYABLE_NAME = "hello.jar";
	private static final String DEPLOYABLE_PATH = ROOT + DEPLOYABLE_NAME;

	@Before
	public void clearServer() throws IOException {
		FileUtils.deleteTree(new File(SERVER_FOLDER));
	}
	
	@Test
	public void testDeployableController() throws IOException {
		
	/*	DeployableController deployableController = new DeployableController();

		CopyPasteServer copyPasteServer = new CopyPasteServer(new File(SERVER_FOLDER));
		deployableController.setRuntimeServer(copyPasteServer);
		
		MavenRepository mavenRepository = new MavenRepository(new URL("http://search.maven.org/remotecontent?filepath="));
		deployableController.addDeployableProvider(mavenRepository);

		FileProvider filesProvider = new FileProvider(FILE_REPOSITORY_FOLDER);
		deployableController.addDeployableProvider(filesProvider);
		
		MavenID id = new MavenID("org.apache.maven", "maven-archetype-core", "1.0-alpha-3");
		MavenDeployableDescriptor deployableDescriptor = mavenRepository.fetchDeployableDescriptor();
		deployableController.deployWithDependencies(id);
		
		deployableController.startServer();*/
		
	}
	
}
