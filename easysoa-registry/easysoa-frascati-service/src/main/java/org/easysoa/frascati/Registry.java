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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.easysoa.frascati.api.RegistryItf;
import org.easysoa.frascati.api.intent.ComponentIntentObserverItf;
import org.easysoa.frascati.api.intent.ParserIntentObserverItf;
import org.easysoa.frascati.api.intent.ProcessingIntentObserverItf;
import org.eclipse.stp.sca.Composite;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.NameController;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.ow2.frascati.util.AbstractLoggeable;

/**
 * @author munilla
 *
 */
@Scope("COMPOSITE")
@Service(interfaces = { ComponentIntentObserverItf.class,
        ProcessingIntentObserverItf.class, ParserIntentObserverItf.class,
        RegistryItf.class })
public class Registry extends AbstractLoggeable implements
        ComponentIntentObserverItf, ProcessingIntentObserverItf,
        ParserIntentObserverItf, RegistryItf
{
    
    Stack<QName> processingComposites = null; 
    Stack<CompositeEntry> compositeEntries = null;
    List<String> processed;
    Map<String, CompositeEntry> registryEntries = null;

    /**
     * Constructor
     */
    public Registry()
    {
        processingComposites = new Stack<QName>();
        compositeEntries = new Stack<CompositeEntry>();
        registryEntries = new HashMap<String, CompositeEntry>();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.RegistryItf#getProcessedComponentList()
     */
    public List<String> getProcessedComponentList()
    {
        List<String> result = processed;
        processed = null;
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.intent.ProcessingIntentObserverItf#startProcessing(javax.xml.namespace.QName)
     */
    public void startProcessing(QName qname)
    {
        log.severe("start Processing " + qname);
        processingComposites.push(qname);
        processed = new ArrayList<String>();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.intent.ProcessingIntentObserverItf#stopProcessing(javax.xml.namespace.QName)
     */
    public void stopProcessing(QName qname)
    {
        if (qname == null)
        {
            log.severe("Error : no processing name");

        }else if(processingComposites.size()==0 || !qname.getLocalPart().equals(
                processingComposites.peek().getLocalPart()))
        {
            log.severe("Stop processing of '" + qname + "' composite but '" 
                    + (processingComposites.size()==0?"null":processingComposites.peek().getLocalPart())
                            + "' was expected");
            
            while(processingComposites.size()>0 &&
                    !qname.getLocalPart().equals(processingComposites.peek().getLocalPart()))
            {
                QName processingCompositeError = processingComposites.pop();
                log.severe("Processing '" + processingCompositeError + "' composite error");
                if(registryEntries.remove(processingCompositeError.getLocalPart()) != null)
                {
                    log.severe("'" + processingCompositeError.getLocalPart() + 
                            "' CompositeEntry removed from the Registry");
                }
            }
            
        }else
        {
            log.severe("stop Processing " + qname);            
            String processedComposite = processingComposites.pop().getLocalPart();
            registryEntries.put(processedComposite, compositeEntries.pop());
            processed.add(processedComposite);  
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.intent.ParserIntentObserverItf#compositeParsed(org.eclipse.stp.sca.Composite)
     */
    public void compositeParsed(Composite composite)
    {        
        if (processingComposites.size()>0)
        {
            compositeEntries.push(new CompositeEntry(composite));
            
        } else
        {
            log.severe("no qname registered for '" + composite.getName() + "' composite");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.processor.intent.ProcessorIntentObserver#
     *      addComponent(org.objectweb.fractal.api.Component)
     */
    public void componentAdded(Component component)
    {
        if (compositeEntries.size()>0 && component != null)
        {
            try
            {
                String componentName = ((NameController) component
                        .getFcInterface("name-controller")).getFcName();
                
                compositeEntries.peek().addComponent(componentName, component);

            } catch (NoSuchInterfaceException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.intent.ProcessingIntentObserverItf#componentRemoved(java.lang.String)
     */
    public void componentRemoved(String componentName)
    {
        CompositeEntry toRemove = null;
        
        for(CompositeEntry entry : registryEntries.values())
        {
            if(componentName.equals(entry.rootComponent))
            {
                toRemove = entry;
                break;
            }
        } 
        if(toRemove != null)
        {
            registryEntries.remove(toRemove);
        }else
        {
            log.info("Component '" + componentName + "' to remove not found");
        }
    }

    public void call(String componentName, String serviceName, String methodName)
    {
        log.info("method[" + methodName + "] call by service[" + serviceName
                + "] in component[" + componentName + "]");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.RegistryItf#getComposite(java.lang.String)
     */
    public Composite getComposite(String compositeName)
    {
        CompositeEntry entry = registryEntries.get(compositeName);
        if (entry != null)
        {
            return entry.composite;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.RegistryItf#getComponent(java.lang.String)
     */
    public Component getComponent(String componentName)
    {
        for(CompositeEntry entry : registryEntries.values())
        {
            if(componentName.equals(entry.rootComponent))
            {
                return entry.getComponent(entry.rootComponent);
            }
        } 
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.RegistryItf#getService(java.lang.String,
     * java.lang.String, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(String componentName,
            String serviceName, Class<T> serviceClass)
            throws FraSCAtiServiceException
    {
        Component component = getComponent(componentName);
        if (component != null)
        {
            try
            {
                return (T) component.getFcInterface(serviceName);
            } catch (NoSuchInterfaceException e)
            {
                e.printStackTrace();
                throw new FraSCAtiServiceException("service '" + serviceName
                        + "' does not exist  in " + componentName);
            } catch (ClassCastException e)
            {
                e.printStackTrace();
                throw new FraSCAtiServiceException("service '" + serviceName
                        + "' is not a '" + serviceClass.getCanonicalName()
                        + "' instance");
            }
        }
        return null;
    }

    /**
     * @author munilla
     *
     */
    private class CompositeEntry
    {
        Composite composite;
        String rootComponent;
        Map<String, Component> components;
        String name;

        CompositeEntry(Composite composite)
        {
            this.composite = composite;
            this.name = this.composite.getName();
            components = new HashMap<String, Component>();
        }

        /**
         * @param componentName
         * @return
         */
        public Component getComponent(String componentName)
        {
            return components.get(componentName);
        }

        /**
         * @param component
         */
        public void addComponent(String componentName, Component component)
        {
            components.put(componentName, component);
            if (rootComponent == null)
            {
                rootComponent = componentName;
            }
        }

        /**
         * @param componentName
         */
        public void removeComponent(String componentName)
        {
            components.remove(componentName);
        }
    }
}