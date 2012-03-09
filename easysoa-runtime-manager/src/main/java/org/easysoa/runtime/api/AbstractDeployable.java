package org.easysoa.runtime.api;

import java.io.IOException;
import java.io.InputStream;

/**
 * Convenience class to build {@link Deployable} classes,
 * in conjunction with <code>AbstractDeployableDescriptor</code>.
 * 
 * Limitations include:
 * - <code>InputStream</code> handling is basic ;
 * - There is only support for one deployable ID interface.
 * 
 * @author mkalam-alami
 *
 * @param <T> The deployable ID class
 */
public abstract class AbstractDeployable<T> extends AbstractDeployableDescriptor<T> implements Deployable<T> {

	private InputStream is;

	public AbstractDeployable(T id, InputStream is) {
		super(id);
		this.is = is;
	}
	
	/**
	 * Any data read through this <code>InputStream</code> can only be read once.
	 * Calling <code>getInputStream</code> multiple times will always return the same instance.
	 */
	@Override
	public InputStream getInputStream() {
		return is;
	}

	@Override
	public void closeInputStream() throws IOException {
		is.close();
	}
	
}
