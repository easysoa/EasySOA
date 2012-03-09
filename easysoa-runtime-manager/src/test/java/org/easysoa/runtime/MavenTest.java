package org.easysoa.runtime;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.easysoa.runtime.api.DeployableDescriptor;
import org.easysoa.runtime.maven.MavenArtifactDescriptor;
import org.easysoa.runtime.maven.MavenID;
import org.easysoa.runtime.maven.MavenPOMDescriptorProvider;
import org.easysoa.runtime.maven.MavenRepository;
import org.junit.Test;

public class MavenTest {

	private static Logger logger = Logger.getLogger(MavenTest.class);
	
	@Test
	public void testMavenRepository() throws IOException {
		
		MavenRepository repository = new MavenRepository(new URL("http://search.maven.org/remotecontent?filepath="));
		
		MavenID id = new MavenID("org.apache.maven", "maven-archetype-core", "1.0-alpha-3");
		MavenPOMDescriptorProvider pom = repository.fetchPOM(id);
		Assert.assertNotNull(pom);
	
		List<MavenArtifactDescriptor> artifactDescriptorList = pom.getDeployableDescriptors();
		Assert.assertFalse(artifactDescriptorList.isEmpty());
		
		MavenArtifactDescriptor artifactDescriptor = artifactDescriptorList.get(0);
		logger.info("Found artifact: " + artifactDescriptor.toString());
		List<DeployableDescriptor<?>> dependencies = artifactDescriptor.getDependencies();
		Assert.assertEquals(7, dependencies.size());
		
		for (DeployableDescriptor<?> dependency : dependencies) {
			logger.info("  " + dependency.getId().toString());
		}
		
	}
	
}
