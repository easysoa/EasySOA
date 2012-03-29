package org.easysoa.runtime;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.easysoa.runtime.api.DeployableDescriptor;
import org.easysoa.runtime.copypaste.CopyPasteServer;
import org.easysoa.runtime.maven.MavenDeployable;
import org.easysoa.runtime.maven.MavenDeployableDescriptor;
import org.easysoa.runtime.maven.MavenID;
import org.easysoa.runtime.maven.MavenRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nuxeo.common.utils.FileUtils;

public class MavenTest {

	private static Logger logger = Logger.getLogger(MavenTest.class);

	private static final String ROOT = "target/test-classes/";
	private static final String SERVER_FOLDER = ROOT + "server/";
	
	private static MavenRepository repository;
	
	private static MavenID id;
	
	@BeforeClass
	public static void setUp() throws IOException {
		repository = new MavenRepository(new URL("http://search.maven.org/remotecontent?filepath="));
		id = new MavenID("org.apache.maven", "maven-archetype-core", "1.0-alpha-3");
	}
	
	@Before
	public void clearServer() throws IOException {
		FileUtils.deleteTree(new File(SERVER_FOLDER));
	}
	
	@Test
	public void testFindMavenDependencies() throws IOException {
		// Find artifact descriptor
		MavenDeployableDescriptor artifactDescriptor = repository.fetchDeployableDescriptor(id);
		logger.info("Found artifact: " + artifactDescriptor.toString());

		// Find its dependencies
		List<DeployableDescriptor<?>> dependencies = artifactDescriptor.getDependencies();
		assertEquals(7, dependencies.size());
		for (DeployableDescriptor<?> dependency : dependencies) {
			logger.info(" > " + dependency.getId().toString());
		}
		
	}
	
	@Test
	public void testDeployArtifact() throws IOException {
		// Find deployable
		MavenDeployable artifact = repository.fetchDeployable(id);

		// Set up a server & deploy the artifact
		CopyPasteServer server = new CopyPasteServer(new File(SERVER_FOLDER));
		server.deploy(artifact);
		File targetFile = new File(SERVER_FOLDER + artifact.getFileName());
		assertTrue(targetFile.exists());

		// Deploy its dependencies
		for (DeployableDescriptor<?> dependency : artifact.getDependencies()) {
			server.deploy(repository.fetchDeployable((MavenID) dependency.getId()));
			break; // Let's say one dependency is enough, for performance
		}
		assertEquals(2, server.getDeployablesDirectory().listFiles().length);
		
		// Undeploy
		server.undeploy(artifact);
		assertFalse(targetFile.exists());
	}
	
}
