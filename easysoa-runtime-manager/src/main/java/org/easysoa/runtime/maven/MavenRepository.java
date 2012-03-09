package org.easysoa.runtime.maven;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.List;

import org.easysoa.runtime.api.DeployableProvider;

/**
 * Maven repository access.
 * 
 * @author mkalam-alami
 *
 */
public class MavenRepository implements DeployableProvider<MavenDeployable, MavenID> {

	private static final String JAR_EXT = "jar";
	
	private static final String POM_EXT = "pom";
	
	private URL baseUri;
	
	public MavenRepository(URL url) {
		this.baseUri = url;
	}

	@Override
	public MavenDeployable fetchDeployable(MavenID id) throws IOException {
		return new MavenDeployable(id, getUrl(id, JAR_EXT).openStream(), getUrl(id, POM_EXT).openStream());
	}
	
	public MavenDeployableDescriptor fetchDeployableDescriptor(MavenID id) throws IOException {
		// Use POM as a DeployableDescriptorProvider to find deployable's dependencies
		InputStreamReader inputStreamReader;
		try {
			inputStreamReader = new InputStreamReader(getUrl(id, POM_EXT).openStream());
		}
		catch (IOException e) {
			throw new IOException("Could not download POM for specified artifact", e);
		}
		MavenDeployableDescriptorProvider mavenPOMDescriptorProvider = new MavenDeployableDescriptorProvider(inputStreamReader);
		List<MavenDeployableDescriptor> deployableDescriptors = mavenPOMDescriptorProvider.getDeployableDescriptors();
		return deployableDescriptors.get(0);
	}
	
	private URL getUrl(MavenID id, String extension) {
		try {
			return new URL(baseUri.toString() 
					+ id.getGroupId().replace('.', '/')
					+ "/" + id.getArtifactId()
					+ "/" + id.getVersion()
					+ "/" + id.getArtifactId() + "-" + id.getVersion() + "." + extension);
		} catch (MalformedURLException e) {
			throw new InvalidParameterException("Cannot build URL using specified Maven ID (" + e.getMessage() + ")");
		}
	}

}
