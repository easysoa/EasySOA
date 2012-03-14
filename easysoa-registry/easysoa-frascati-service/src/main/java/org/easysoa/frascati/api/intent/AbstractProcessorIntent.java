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
package org.easysoa.frascati.api.intent;

import org.easysoa.frascati.api.observer.ProcessorObserverItf;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.ow2.frascati.tinfi.api.IntentHandler;
import org.ow2.frascati.tinfi.api.IntentJoinPoint;
import org.ow2.frascati.util.AbstractLoggeable;

/**
 * Define the shared code between all processor (@see org.ow2.frascati.assembly.factory.api.Processor) 
 * Intents (@see org.ow2.frascati.tinfi.api.IntentHandler)
 * 
 * @param <T>
 *      the object type handled by the targeted processor
 * @param <P>
 *      the processor observer type, which has also to handle the T type of
 *      object 
 */
@Scope("COMPOSITE")
@Service(IntentHandler.class)
public abstract class AbstractProcessorIntent<T, P extends ProcessorObserverItf<T>>
        extends AbstractLoggeable implements IntentHandler
{
    /**
     * the observer to inform about processor checking process
     */
    @Reference(name = "observer")
    protected P observer;
    
    protected T last; 
    protected boolean checked;

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.frascati.tinfi.api.IntentHandler
     *      #invoke(org.ow2.frascati.tinfi.api.IntentJoinPoint)
     */
    public Object invoke(IntentJoinPoint ijp) throws Throwable
    {
        Object ret;
        Object argument = ijp.getArguments()[0];
        checked = false;
        try
        {
            @SuppressWarnings("unchecked")
            T t = (T) argument;
            //avoid check called twice
            if(t != last && !checked)
            {
                observer.checkDo(t);
                last = t;
            }
        } catch (ClassCastException e)
        {
            // do nothing - just proceed
        }
        ret = ijp.proceed();
        
        try
        {
            @SuppressWarnings("unchecked")
            T t = (T) argument;
            //avoid check called twice
            if(t == last && !checked)
            {
                observer.checkDone(t);
                checked = true;
                last = t;
            }
        } catch (ClassCastException e)
        {
            // do nothing - just return
        }
        return ret;
    }

}
