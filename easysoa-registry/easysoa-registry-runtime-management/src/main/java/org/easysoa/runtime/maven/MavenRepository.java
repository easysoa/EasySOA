package org.easysoa.runtime.maven;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.DomainInit;
import org.easysoa.runtime.api.DeployableDescriptorProvider;
import org.easysoa.runtime.api.DeployableProvider;

/**
 * Maven repository access.
 * 
 * @author mkalam-alami
 *
 */
public class MavenRepository implements DeployableProvider<MavenDeployable, MavenID>, 
		DeployableDescriptorProvider<MavenDeployableDescriptor, MavenID> {

	private static final String JAR_EXT = "jar";
	
	private static final String POM_EXT = "pom";

	private static final String DEFAULT_REPOSITORY = "http://search.maven.org/remotecontent?filepath=";

    private static Log log = LogFactory.getLog(DomainInit.class);
    
	private URL baseUri;
	
	public MavenRepository() {
		try {
			this.baseUri = new URL(DEFAULT_REPOSITORY);
		} catch (MalformedURLException e) {
			log.error("Default repository URL is invalid", e);
		}
	}
	
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
		return new MavenPom(inputStreamReader).getDeployableDescriptor();
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
