package org.easysoa.runtime.maven;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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
public class MavenDeployableDescriptorProvider implements DeployableDescriptorProvider<MavenDeployableDescriptor> {

	List<MavenDeployableDescriptor> descriptors = new ArrayList<MavenDeployableDescriptor>();
	
	Model model;
	
	/**
	 * Parses the specified POM to extract exactly one deployable descriptor,
	 * being the one matching the project's artifact.
	 * @param pomReader
	 */
	protected MavenDeployableDescriptorProvider(Reader pomReader) {
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
			descriptors.add(projectDescriptor);
		} catch (Exception e) {
			// TODO Handle exceptions correctly
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns exactly one descriptor for the deployable matching this project's POM.
	 * All dependencies for this descriptor will be provided.
	 */
	@Override
	public List<MavenDeployableDescriptor> getDeployableDescriptors() {
		return descriptors;
	}
	
	public Model getPomModel() {
		return model;
	}

}
