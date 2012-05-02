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
package org.nuxeo.frascati;

import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.nuxeo.frascati.factory.ClassLoaderSingleton;
import org.nuxeo.runtime.bridge.Application;
import org.ow2.frascati.util.reflect.ReflectionHelper;


/**
 * Implementation of the FraSCAtiServiceProviderItf interface 
 * NuxeoFraSCAti allow to instantiate an new FraSCAti instance in Nuxeo
 */
public class NuxeoFraSCAti
implements Application, FraSCAtiServiceProviderItf
{

    Logger log = Logger.getLogger(NuxeoFraSCAti.class.getCanonicalName());

    private ReflectionHelper frascati = new ReflectionHelper(
            ClassLoaderSingleton.classLoader(), "org.ow2.frascati.FraSCAti");

    private FraSCAtiServiceItf frascatiService;
    
    /**
     * Constructor
     * 
     * @throws NuxeoFraSCAtiException
     */
    public NuxeoFraSCAti() throws NuxeoFraSCAtiException
    {

        log.log(Level.INFO, "new FraSCAti instance initialisation");
        frascati.set(frascati.invokeStatic("newFraSCAti"));

        if (frascati.get() != null)
        {
            ReflectionHelper compositeManager  = new ReflectionHelper(
                    ClassLoaderSingleton.classLoader(),
                    "org.ow2.frascati.assembly.factory.api.CompositeManager");
            
            compositeManager.set(frascati.invoke("getCompositeManager"));                        
            
            ReflectionHelper component = new ReflectionHelper(
                    ClassLoaderSingleton.classLoader(),
                    "org.objectweb.fractal.api.Component");

            component.set(compositeManager.invoke("getTopLevelDomainComposite"));
            
            component = getComponent(component,
                    "org.ow2.frascati.FraSCAti/assembly-factory");
           
            frascatiService = (FraSCAtiServiceItf) frascati.invoke("getService",
                    new Class<?>[]{component.getReflectedClass(), 
                    String.class,Class.class},
                    new Object[]{component.get(),"easysoa-frascati-service", 
                    FraSCAtiServiceItf.class});
        }
    }

    public FraSCAtiServiceItf getFraSCAtiService()
    {
        return frascatiService;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.nuxeo.runtime.bridge.Application#getService(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> type)
    {
        if (type.isAssignableFrom(getClass()))
        {
            return (T) this;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nuxeo.runtime.bridge.Application#destroy()
     */
    public void destroy()
    {
        if(frascati == null)
        {
            return;
        }
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name;
        try
        {
            name = new ObjectName("SCA domain:name0=*,*");
            Set<ObjectName> names = mbs.queryNames(name, name);
            for (ObjectName objectName : names)
            {
                mbs.unregisterMBean(objectName);
            }
            mbs.unregisterMBean(new ObjectName(
                    "org.ow2.frascati.jmx:name=FrascatiJmx"));
        } catch (MalformedObjectNameException e)
        {
            // e.printStackTrace();
        } catch (NullPointerException e)
        {
            // e.printStackTrace();
        } catch (MBeanRegistrationException e)
        {
            // e.printStackTrace();
        } catch (InstanceNotFoundException e)
        {
            // e.printStackTrace();
        }
        
        ReflectionHelper compositeManager  = new ReflectionHelper(
                ClassLoaderSingleton.classLoader(),
                "org.ow2.frascati.assembly.factory.api.CompositeManager");
        
        compositeManager.set(frascati.invoke("getCompositeManager"));
        
        ReflectionHelper component = new ReflectionHelper(
                ClassLoaderSingleton.classLoader(),
                "org.objectweb.fractal.api.Component");
        
        component.set(compositeManager.invoke("getTopLevelDomainComposite"));
        
        component =  getComponent(component,
                "org.ow2.frascati.FraSCAti/assembly-factory");
        
        ReflectionHelper lifeCycleController = new ReflectionHelper(
                ClassLoaderSingleton.classLoader(),
                "org.objectweb.fractal.api.control.LifeCycleController");
        
        lifeCycleController.set(component.invoke("getFcInterface",
                new Class<?>[]{String.class},
                new Object[]{"lifecycle-controller"}));
        
        lifeCycleController.invoke("stopFc");
        
        frascati = null;
        frascatiService = null;
    }
    
    private ReflectionHelper getComponent(ReflectionHelper currentComponent,
            String componentPath)
    {
        String[] componentPathElements = componentPath.split("/");
        String lookFor = componentPathElements[0];
        String next = null;
        
        if(componentPathElements.length>1)
        {
            int n = 1;
            StringBuilder nextSB = new StringBuilder();
            for(;n<componentPathElements.length;n++)
            {
                nextSB.append(componentPathElements[n]);
                if(n<componentPathElements.length - 1)
                {
                    nextSB.append("/");
                }
            }
            next = nextSB.toString();
        }        
        ReflectionHelper contentController = new ReflectionHelper(
                ClassLoaderSingleton.classLoader(),
                "org.objectweb.fractal.api.control.ContentController");
        
        contentController.set(currentComponent.invoke("getFcInterface",
                new Class<?>[]{String.class},
                new Object[]{"content-controller"}));        

        ReflectionHelper component = new ReflectionHelper(
                ClassLoaderSingleton.classLoader(),
                "org.objectweb.fractal.api.Component");

        ReflectionHelper nameController = new ReflectionHelper(
                ClassLoaderSingleton.classLoader(),
                "org.objectweb.fractal.api.control.NameController");
        
        Object[] subComponents = (Object[]) 
                contentController.invoke("getFcSubComponents");
        
        if(subComponents == null)
        {
            return null;
        }
        for(Object o : subComponents)
        {
            component.set(o);
            nameController.set(component.invoke("getFcInterface",
                    new Class<?>[]{String.class},
                    new Object[]{"name-controller"}));
            String name = (String) nameController.invoke("getFcName");
            if(lookFor.equals(name))
            {
                if(next == null || next.length() ==0)
                {
                    return component;
                } else 
                {
                    return getComponent(component,next);
                }
            }
        }
        return null;
    }
}
