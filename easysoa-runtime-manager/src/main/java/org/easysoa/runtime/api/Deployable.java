package org.easysoa.runtime.api;

import java.io.InputStream;

/**
 * Describes a deployable artifact, available to deploy on any compatible runtime server.
 * 
 * @author mkalam-alami
 *
 */
public interface Deployable<T> extends DeployableDescriptor<T> {
	
	/**
	 * @return The deployable as an {@link InputStream}. Must be closed after use.
	 */
	InputStream getInputStream();
	
	String getFileName();
	
}
