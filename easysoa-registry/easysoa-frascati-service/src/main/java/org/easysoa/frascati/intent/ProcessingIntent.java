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
package org.easysoa.frascati.intent;

import java.util.logging.Level;

import javax.xml.namespace.QName;

import org.easysoa.frascati.api.ComponentWeaverItf;
import org.easysoa.frascati.api.intent.ProcessingIntentObserverItf;
import org.objectweb.fractal.api.Component;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.ow2.frascati.tinfi.api.IntentHandler;
import org.ow2.frascati.tinfi.api.IntentJoinPoint;
import org.ow2.frascati.util.AbstractLoggeable;

@Scope("COMPOSITE")
@Service(IntentHandler.class)
public class ProcessingIntent extends AbstractLoggeable implements
        IntentHandler
{
    @Reference(name = "component-weaver")
    private ComponentWeaverItf componentWeaver;

    @Reference(name = "processing-intent-observer")
    private ProcessingIntentObserverItf observer;

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.frascati.tinfi.api.IntentHandler
     *      #invoke(org.ow2.frascati.tinfi.api.IntentJoinPoint)
     */
    public Object invoke(IntentJoinPoint ijp) throws Throwable
    {
        Component component = null;
        QName qname = null;
        boolean process = false;
        
        String methodName = ijp.getMethod().getName();
        Object argument = ijp.getArguments()[0];

        if ("processComposite".equals(methodName))
        {
            qname = (QName) argument;
            observer.startProcessing(qname);
            process = true;
        }
        Object ret = ijp.proceed();
        if (process)
        {
            component = (Component) ret;
            componentWeaver.weave(component);
            observer.componentAdded(component);
            observer.stopProcessing(qname);

        } else if ("removeComposite".equals(methodName))
        {
            String name = (String) argument;
            log.log(Level.INFO, "Composite removed : " + name);
            observer.componentRemoved(name);
        } else
        {
            log.info("call " + methodName);
        }
        return ret;
    }

}