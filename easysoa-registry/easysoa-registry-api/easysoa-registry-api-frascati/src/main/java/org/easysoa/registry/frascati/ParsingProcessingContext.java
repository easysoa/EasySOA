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

package org.easysoa.registry.frascati;

import org.nuxeo.frascati.NuxeoFraSCAtiException;
import org.nuxeo.frascati.api.AbstractProcessingContext;
import org.nuxeo.frascati.api.ProcessingModeProxy;
import org.ow2.frascati.util.reflect.ReflectionHelper;

/**
 * ProcessingContext for parsing-only purpose
 * wrapping FraSCAti's original, runtime-focused one (ProcessingContextImpl,
 * built by frascati.getCompositeManager() i.e. AssemblyFactoryManager)
 * 
 * - Unfound classes produce a harmless warnings, rather than errors and throw a fatal exception
 * - Remembers error and warning messages
 * 
 * NB. can't extend rather than delegate because methods are final
 * @author mdutoo
 *
 */

public class ParsingProcessingContext extends AbstractProcessingContext {
	

	public ParsingProcessingContext(ReflectionHelper delegate) throws NuxeoFraSCAtiException {
		super(delegate);
		setProcessingMode(ProcessingModeProxy.check);
	}
}