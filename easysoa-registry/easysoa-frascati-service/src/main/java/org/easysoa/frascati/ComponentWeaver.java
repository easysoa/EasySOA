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
package org.easysoa.frascati;

import java.lang.reflect.Method;
import java.util.logging.Level;

import org.easysoa.frascati.api.ComponentWeaverItf;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.fractal.julia.ComponentInterface;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.ow2.frascati.tinfi.TinfiComponentInterceptor;
import org.ow2.frascati.tinfi.api.IntentHandler;
import org.ow2.frascati.tinfi.api.control.SCABasicIntentController;
import org.ow2.frascati.util.AbstractLoggeable;

/**
 * Weave the component intent with each Component loaded 
 * 
 * @author Christophe Munilla
 *
 */
@Scope("COMPOSITE")
@Service(ComponentWeaverItf.class)
public class ComponentWeaver extends AbstractLoggeable 
implements ComponentWeaverItf
{
    /**
     * The Component Intent is in charge of notice life cycle and call events
     * of the component
     */
    @Reference(name="component-intent")
    private IntentHandler componentIntent;
    
    /**
     * Weave the component intent with the component passed on as a parameter
     * 
     * @param component
     *          the Component object to weave the component intent with
     * @throws Throwable
     *          each potential exception that can be thrown during the weaving
     *          process 
     */
    public void weave(Component component) throws Throwable
    {
        if(component == null)
        {
            return;
        }
        log .log(Level.INFO,"Component added : " + (
                (NameController) component.getFcInterface(
                        "name-controller")).getFcName());
        try
        {
            ContentController contentController = (ContentController) 
                component.getFcInterface("content-controller");
            Component[] subComponents = contentController.getFcSubComponents();
            
            for(Component subComponent : subComponents)
            {
                weave(subComponent);
            }    
        } catch (NoSuchInterfaceException e)
        {
            log.warning(e.getMessage());
        }
        LifeCycleController lcController =
           (LifeCycleController) component.getFcInterface("lifecycle-controller");
        
        TinfiComponentInterceptor<?> tci = null;
        
        try{
            tci = (TinfiComponentInterceptor<?>)(
                (ComponentInterface)lcController).getFcItfImpl();
        } catch(ClassCastException e)
        {
            log.log(Level.WARNING,"Enable to weave the 'component'");
            return;
        }
        Method method = LifeCycleController.class.getMethod("startFc");
        tci.addIntentHandler(componentIntent, method);
        method = LifeCycleController.class.getMethod("stopFc");
        tci.addIntentHandler(componentIntent, method);
        
        boolean started = false;
        if (LifeCycleController.STARTED.equals(lcController.getFcState()))
        {
            lcController.stopFc();
            started = true;
        }
        SCABasicIntentController iController =
                (SCABasicIntentController) component.getFcInterface(
                        SCABasicIntentController.NAME);
        
        iController.addFcIntentHandler(componentIntent);
        if (started)
        {
            lcController.startFc();
        }        
    }

}