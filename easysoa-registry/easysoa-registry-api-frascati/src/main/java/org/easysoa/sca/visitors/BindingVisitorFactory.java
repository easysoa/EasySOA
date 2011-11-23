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

package org.easysoa.sca.visitors;

import org.easysoa.sca.IScaImporter;

/**
 * Allows to create discovery handlers for binding info that has been read.
 * Different BindingVisitorFactory impls will provide different kind of discovery handlers,
 * for instance implemented on top of local Nuxeo APIs or remote EasySOA API.
 * 
 * @author jguillemotte
 *
 */
public interface BindingVisitorFactory {

	/**
	 * Create and return a new ScaVisitor object for References
	 * @return
	 */
	public ScaVisitor createReferenceBindingVisitor(IScaImporter scaImporter);
	
	/**
	 * Create and return a new ScaVisitor object for Services
	 * @return
	 */
	public ScaVisitor createServiceBindingVisitor(IScaImporter scaImporter);
	
}