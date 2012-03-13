/**
 * EasySOA - FraSCAti
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
package org.easysoa.frascati.api.observer;

import org.easysoa.frascati.api.ScaImporterIntermediaryItf;
import org.eclipse.emf.ecore.EObject;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.ow2.frascati.util.AbstractLoggeable;

@Scope("COMPOSITE")
@Service(ProcessorObserverItf.class)
public abstract class AbstractProcessorObserver<T extends EObject> 
extends AbstractLoggeable implements ProcessorObserverItf<T>
{
    @Reference(name = "runtime-sca-importer")
    protected ScaImporterIntermediaryItf scaImporter;
}
