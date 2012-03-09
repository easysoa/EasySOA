package org.easysoa.runtime.maven;

/**
 * Maven deployable (= artifact) identifier
 * 
 * @author mkalam-alami
 *
 */
public class MavenID {
	
	private String groupId;
	
	private String artifactId;
	
	private String version;

	public MavenID(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}
	
	@Override
	public String toString() {
		return groupId + ":" + artifactId + ":" + version;
	}

}
