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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.stp.sca.Binding;

/**
 * Define the shared code between all {@link Binding} processors 
 * (@see org.ow2.frascati.assembly.factory.api.Processor) observers
 * 
 * @param <T>
 *      the object type handled by the targeted processor which have to extend 
 *      {@link EObject} - Here T extends EObject instead of {@link Binding} to 
 *      be able to extend the {@link AbstractProcessorObserver} class
 */
public abstract class AbstractProcessorBindingObserver<T extends EObject> 
extends AbstractProcessorObserver<T>
{   
    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.observer.ProcessorObserverItf#checkDo(
     * java.lang.Object)
     */
    public void checkDo(T t)
    {
        log.info("checkDo ["+t.getClass()+"]");
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.observer.ProcessorObserverItf#checkDone(
     * java.lang.Object)
     */
    public void checkDone(T t)
    {
        log.info("checkDone ["+t.getClass()+"]");
        scaImporter.pushBinding((Binding) t);
    }
}
