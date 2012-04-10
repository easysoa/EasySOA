/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.runtime.api;

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
	
}
