package org.easysoa.runtime.maven;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;

import org.easysoa.runtime.api.DeployableProvider;

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
	
	public MavenPOMDescriptorProvider fetchPOM(MavenID id) throws IOException {
		return new MavenPOMDescriptorProvider(openStreamAsReader(getUrl(id, POM_EXT)));
	}
	
	public MavenApplicationPOMDescriptorProvider fetchApplicationPOM(MavenID id) throws IOException {
		return new MavenApplicationPOMDescriptorProvider(openStreamAsReader(getUrl(id, POM_EXT)));
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
	
	private Reader openStreamAsReader(URL url) throws IOException {
		return new InputStreamReader(url.openStream());
	}

}
