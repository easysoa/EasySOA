package org.easysoa.runtime.maven;

import java.io.Reader;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.easysoa.runtime.api.DeployableDescriptorProvider;

/**
 * Handles POM files as {@link DeployableDescriptorProvider}s.
 * Allows to extract all dependencies of a deployable.
 * 
 * @author mkalam-alami
 *
 */
public class MavenPom {

	MavenDeployableDescriptor descriptor;
	
	Model model;
	
	/**
	 * Parses the specified POM to extract the deployable descriptor matching the project's artifact.
	 * @param pomReader
	 */
	protected MavenPom(Reader pomReader) {
		try {
			MavenXpp3Reader mavenReader = new MavenXpp3Reader();
			this.model = mavenReader.read(pomReader);
			
			// Add POM's project ID & its dependencies
			MavenDeployableDescriptor projectDescriptor = new MavenDeployableDescriptor(new MavenID(
					this.model.getGroupId(),
					this.model.getArtifactId(),
					this.model.getVersion()));
			for (Dependency dependency : this.model.getDependencies()) {
				projectDescriptor.addDependency(new MavenDeployableDescriptor(new MavenID(
						dependency.getGroupId(),
						dependency.getArtifactId(),
						dependency.getVersion())));
			}
			projectDescriptor.setAllDependenciesKnown(true);
			descriptor = projectDescriptor;
		} catch (Exception e) {
			// TODO Handle exceptions correctly
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns exactly one descriptor for the deployable matching this project's POM.
	 * All dependencies for this descriptor will be provided.
	 */
	public MavenDeployableDescriptor getDeployableDescriptor() {
		return descriptor;
	}
	
	public Model getPomModel() {
		return model;
	}

}
