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

import org.easysoa.frascati.api.intent.ComponentIntentObserverItf;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.fractal.api.type.InterfaceType;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.ow2.frascati.tinfi.api.IntentHandler;
import org.ow2.frascati.tinfi.api.IntentJoinPoint;
import org.ow2.frascati.util.AbstractLoggeable;

@Scope("COMPOSITE")
@Service(IntentHandler.class)
public class ComponentIntent extends AbstractLoggeable implements IntentHandler
{
    @Reference(name = "component-intent-observer")
    private ComponentIntentObserverItf observer;

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.frascati.tinfi.api.IntentHandler
     *      #invoke(org.ow2.frascati.tinfi.api.IntentJoinPoint)
     */
    public Object invoke(IntentJoinPoint ijp) throws Throwable
    {
        Object ret = ijp.proceed();

        String componentName = ((NameController) ijp.getComponent()
                .getFcInterface("name-controller")).getFcName();

        String serviceName = ((InterfaceType) ijp.getInterface().getFcItfType())
                .getFcItfSignature();

        String methodName = ijp.getMethod().getName();

        observer.call(componentName, serviceName, methodName);
        return ret;
    }

}