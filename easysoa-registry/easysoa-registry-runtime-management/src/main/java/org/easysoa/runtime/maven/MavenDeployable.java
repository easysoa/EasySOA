package org.easysoa.runtime.maven;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.easysoa.runtime.api.AbstractDeployable;

/**
 * Maven artifact
 * 
 * @author mkalam-alami
 *
 */
public class MavenDeployable extends AbstractDeployable<MavenID> {

	public MavenDeployable(MavenID id, InputStream is, InputStream pomIs) throws IOException {
		super(id, is);
		
		// Fetch dependencies by exploring POM
		MavenDeployableDescriptorProvider mavenPOMDescriptorProvider = new MavenDeployableDescriptorProvider(new InputStreamReader(pomIs));
		List<MavenDeployableDescriptor> deployableDescriptors = mavenPOMDescriptorProvider.getDeployableDescriptors();
		this.dependencies = deployableDescriptors.get(0).getDependencies();
		pomIs.close();
	}
	
	@Override
	public String getFileName() {
		return id.getArtifactId() + "-" + id.getVersion() + ".jar"; // TODO POM projects?
	}
	
}
