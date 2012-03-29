package org.easysoa.runtime.maven;

import org.easysoa.runtime.api.AbstractDeployableDescriptor;

/**
 * Maven artifact descriptor, stores basic project information
 * and the artifact's dependencies.
 * 
 * @author mkalam-alami
 *
 */
public class MavenDeployableDescriptor extends AbstractDeployableDescriptor<MavenID> {
	
	public MavenDeployableDescriptor(MavenID id) {
		super(id);
	}
	
}
