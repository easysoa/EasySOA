package org.easysoa.runtime.utils;

import java.net.URI;

import org.easysoa.runtime.api.AbstractDeployableDescriptor;

/**
 * Use of URIs as deployable descriptors (mainly relevant for Files).
 * 
 * @author mkalam-alami
 *
 */
public class URIDeployableDescriptor extends AbstractDeployableDescriptor<URI> {
	
	public URIDeployableDescriptor(URI id) {
		super(id);
	}

}
