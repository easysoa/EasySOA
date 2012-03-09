package org.easysoa.runtime.maven;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.easysoa.runtime.api.DeployableDescriptorProvider;

public abstract class MavenAbstractPOMDescriptorProvider implements DeployableDescriptorProvider<MavenArtifactDescriptor> {

	List<MavenArtifactDescriptor> descriptors = new ArrayList<MavenArtifactDescriptor>();
	
	Model model;
	
	protected MavenAbstractPOMDescriptorProvider(Reader pomReader, boolean includeModules) {
		try {
			MavenXpp3Reader mavenReader = new MavenXpp3Reader();
			this.model = mavenReader.read(pomReader);
			
			// Add POM's project ID & its dependencies
			MavenArtifactDescriptor projectDescriptor = new MavenArtifactDescriptor(new MavenID(
					this.model.getGroupId(),
					this.model.getArtifactId(),
					this.model.getVersion()));
			for (Dependency dependency : this.model.getDependencies()) {
				projectDescriptor.addDependency(new MavenArtifactDescriptor(new MavenID(
						dependency.getGroupId(),
						dependency.getArtifactId(),
						dependency.getVersion())));
			}
			projectDescriptor.setAllDependenciesKnown(true);
			descriptors.add(projectDescriptor);

			// Add modules' IDs
			if (includeModules) {
				/*for (String module : this.model.getModules()) {
					// TODO
				}*/
			}
		} catch (Exception e) {
			// TODO Handle exceptions correctly
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the descriptor for the deployable matching this project's POM.
	 * All dependencies for this descriptor will be provided.
	 */
	@Override
	public List<MavenArtifactDescriptor> getDeployableDescriptors() {
		return descriptors;
	}
	
	public Model getPomModel() {
		return model;
	}

}
