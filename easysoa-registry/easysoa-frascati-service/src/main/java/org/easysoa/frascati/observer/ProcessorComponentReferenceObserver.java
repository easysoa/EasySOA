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
package org.easysoa.frascati.observer;

import org.easysoa.frascati.api.observer.AbstractProcessorNotBindingObserver;
import org.eclipse.stp.sca.ComponentReference;

public class ProcessorComponentReferenceObserver
extends AbstractProcessorNotBindingObserver<ComponentReference>
{
    /**
     * {@inheritDoc}
     * @see org.easysoa.frascati.api.observer.AbstractProcessorNotBindingObserver#
     * getName(org.eclipse.emf.ecore.EObject)
     */
    protected String getName(ComponentReference t)
    {
        return t.getName();
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.observer.ProcessorObserverItf#checkDo(java.lang.Object)
     */
    public void checkDo(ComponentReference t)
    {
        String name = getName(t);
        log.info("checkDo [" + t.getClass() + ":" + name + "]");
        scaImporter.pushArchi(name, t);
        scaImporter.defineReferenceDelegate();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.observer.ProcessorObserverItf#checkDone(java.lang.Object)
     */
    public void checkDone(ComponentReference t)
    {
        String name = getName(t);
        log.info("checkDone [" + t.getClass() + ":" + name + "]");
        scaImporter.popArchi(name);
        scaImporter.clearDelegate();
    }
}
