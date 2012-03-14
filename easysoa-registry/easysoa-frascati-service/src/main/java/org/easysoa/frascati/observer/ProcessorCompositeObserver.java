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
import org.eclipse.stp.sca.Composite;

/**
 * Observer for the FraSCAti's Composite processor
 */
public class ProcessorCompositeObserver
extends AbstractProcessorNotBindingObserver<Composite>
{
    /**
     * {@inheritDoc}
     * @see org.easysoa.frascati.api.observer.AbstractProcessorNotBindingObserver#
     * getName(org.eclipse.emf.ecore.EObject)
     */
    protected String getName(Composite t)
    {
        return t.getName();
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.observer.ProcessorObserverItf#checkDo(java.lang.Object)
     */
    public void checkDo(Composite t)
    {
        String name = getName(t);
        log.info("checkDo [" + t.getClass() + ":" + name + "]");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.observer.ProcessorObserverItf#checkDone(java.lang.Object)
     */
    public void checkDone(Composite t)
    {
        String name = getName(t);
        log.info("checkDone [" + t.getClass() + ":" + name + "]");
    }

}
