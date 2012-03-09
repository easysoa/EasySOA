package org.easysoa.runtime.maven;

import java.io.InputStream;

import org.easysoa.runtime.api.AbstractDeployable;

public class MavenDeployable extends AbstractDeployable<MavenID> {

	public MavenDeployable(MavenID id, InputStream is) {
		super(id, is);
	}
	
	@Override
	public String getFileName() {
		return id.getArtifactId() + "-" + id.getVersion() + ".jar"; // TODO POM projects?
	}
	
}
