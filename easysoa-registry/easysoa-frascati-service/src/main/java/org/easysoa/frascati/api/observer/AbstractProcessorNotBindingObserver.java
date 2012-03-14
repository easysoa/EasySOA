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

/**
 * Define the shared code between all non {@link org.eclipse.stp.sca.Binding} 
 * processors (@see org.ow2.frascati.assembly.factory.api.Processor) observers
 * 
 * @param <T>
 *      the object type handled by the targeted processor which have to extend 
 *      {@link EObject}
 */
public abstract class AbstractProcessorNotBindingObserver<T extends EObject>
extends AbstractProcessorObserver<T>
{
    /**
     * Return the name of the object, which is also an {@ŀink EObject}
     * 
     * @param t
     *            the object of which the name is to find
     * @return 
     *          the name of the object passed on as parameter
     */
    protected abstract String getName(T t);

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.observer.ProcessorObserverItf#checkDo(
     * java.lang.Object)
     */
    public void checkDo(T t)
    {
        String name = getName(t);
        log.info("checkDo [" + t.getClass() + ":" + name + "]");
        scaImporter.pushArchi(name, t);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.observer.ProcessorObserverItf#checkDone(
     * java.lang.Object)
     */
    public void checkDone(T t)
    {
        String name = getName(t);
        log.info("checkDone [" + t.getClass() + ":" + name + "]");
        scaImporter.popArchi(name);
    }
}
