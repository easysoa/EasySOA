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
