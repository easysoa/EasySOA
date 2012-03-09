package org.easysoa.runtime.api;

import java.io.IOException;
import java.io.InputStream;

/**
 * Describes a deployable artifact, available to deploy on any compatible runtime server.
 * 
 * @author mkalam-alami
 *
 */
public interface Deployable<T> extends DeployableDescriptor<T> {
	
	/**
	 * @return The deployable as an {@link InputStream}.
	 */
	InputStream getInputStream();

	/**
	 * Closes the deployable <code>InputStream</code>.
	 * Should always be called once every time a deployable is fetched.
	 */
	void closeInputStream() throws IOException;
	
	String getFileName();
	
}
