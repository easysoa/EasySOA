/**
 * EasySOA
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

import java.net.URL;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.easysoa.frascati.api.FraSCAtiServiceItf;
//import org.easysoa.frascati.api.RegistryItf;
import org.easysoa.frascati.api.ScaImporterIntermediaryItf;
import org.easysoa.frascati.api.ScaImporterRecipientItf;
//import org.easysoa.frascati.api.intent.ComponentIntentObserverItf;
import org.easysoa.frascati.api.intent.ParserIntentObserverItf;
//import org.easysoa.frascati.api.intent.ProcessingIntentObserverItf;
import org.easysoa.frascati.processor.EasySOAProcessingContext;
import org.eclipse.stp.sca.Composite;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.fractal.api.control.NameController;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.ow2.frascati.assembly.factory.api.ClassLoaderManager;
import org.ow2.frascati.assembly.factory.api.CompositeManager;
import org.ow2.frascati.assembly.factory.api.ManagerException;
import org.ow2.frascati.assembly.factory.api.ProcessingMode;
import org.ow2.frascati.util.AbstractLoggeable;
import org.ow2.frascati.util.FrascatiClassLoader;
import org.ow2.frascati.util.io.IOUtils;

/**
 * Implementation of the {@link FraSCAtiServiceItf}
 *
 * @author Christophe Munilla
 *
 */
@Scope("COMPOSITE")
@Service(interfaces = { FraSCAtiServiceItf.class, ParserIntentObserverItf.class })
public class FraSCAtiService
extends AbstractLoggeable implements FraSCAtiServiceItf, ParserIntentObserverItf
{
//    @Reference(name = "registry")
//    private RegistryItf registry;

    @Reference(name = "composite-manager")
    private CompositeManager compositeManager;

    /**
     * The required CompositeManager
     */
    @Reference(name = "classloader-manager")
    private ClassLoaderManager classLoaderManager;

    @Reference(name = "runtime-sca-importer")
    private ScaImporterIntermediaryItf runtimeSCAImporter;

    private static final Logger logger = Logger.getLogger(FraSCAtiService.class.getCanonicalName());

    private List<String> warningMessages;
    private List<String> errorMessages;
    private Map<String,Composite> compositesMap;

    private int errors;
    private int warnings;

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#getComposite(java.lang.String)
     */
    public Composite getComposite(String compositeName)
            throws FraSCAtiServiceException
    {
        Component component = null;
        String componentName = IOUtils.pathLastPart(compositeName);
        try
        {
            component = getComponent(
                    compositeManager.getTopLevelDomainComposite(),componentName);

        } catch (Exception e)
        {
            logger.log(Level.SEVERE,e.getMessage(),e);
        }
        if(component != null )
        {
            return compositesMap.get(compositeName);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#processContribution(java.
     *      lang.String, int, java.net.URL[])
     */
    public String[] processContribution(String contribution,
            int processingMode, Properties properties, URL... urls) throws FraSCAtiServiceException
    {
        EasySOAProcessingContext processingContext =
            new EasySOAProcessingContext(classLoaderManager.getClassLoader());

        for(String propertyName : properties.stringPropertyNames()) {
            processingContext.setContextualProperty(propertyName, properties.getProperty(propertyName));
        }

        FrascatiClassLoader fcl = (FrascatiClassLoader)processingContext.getClassLoader();
        if(urls != null)
        {
          for(URL url : urls)
          {
              fcl.addUrl(url);
          }
        }
        processingContext.setProcessingMode(resovleProcessingMode(processingMode));

        ClassLoader current = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(fcl);

        Component[] components;
        try
        {
            components = compositeManager.processContribution(contribution,
                    processingContext);

        } catch (ManagerException e)
        {
            logger.log(Level.SEVERE,e.getMessage(),e);
            throw new FraSCAtiServiceException("Enable to process the '"
                    + contribution + "' contribution");

        }catch(Exception e)
        {
            logger.log(Level.SEVERE,e.getMessage(),e);
            throw new FraSCAtiServiceException("Enable to process the '"
                    + contribution + "' composite");
        } finally
        {
            Thread.currentThread().setContextClassLoader(current);
            this.warningMessages = processingContext.getWarningMessages();
            this.errorMessages = processingContext.getErrorMessages();
            this.errors = processingContext.getErrors();
            this.warnings = processingContext.getWarnings();
        }
        if(components != null && components.length>0)
        {
            String[] processed = new String[components.length];
            int currentComponentIndex = 0;
            for(;currentComponentIndex<components.length;currentComponentIndex++)
            {
                try{
                    processed[currentComponentIndex] = ((NameController)components[
                       currentComponentIndex].getFcInterface(
                        "name-controller")).getFcName();

                } catch(NoSuchInterfaceException e)
                {
                    log.log(Level.WARNING,e.getMessage());
                }
            }
            return processed;

        } else
        {
            return new String[0];
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#processContribution(java.
     *      lang.String)
     */
    public String[] processContribution(String contribution)
            throws FraSCAtiServiceException
    {
        return processContribution(contribution, FraSCAtiServiceItf.all, null);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#processComposite(java.lang
     *      .String, int, java.net.URL[])
     */
    public Composite processComposite(String composite, int processingMode, Properties properties,
            URL... urls) throws FraSCAtiServiceException
    {
        FrascatiClassLoader classLoader = new FrascatiClassLoader(
                urls!=null?urls:new URL[0] , classLoaderManager.getClassLoader());
        EasySOAProcessingContext processingContext = new EasySOAProcessingContext(
                classLoader);

        for(String propertyName : properties.stringPropertyNames()) {
            processingContext.setContextualProperty(propertyName, properties.getProperty(propertyName));
        }

        processingContext.setProcessingMode(resovleProcessingMode(processingMode));
        Component component = null;
        try
        {
            component = compositeManager.processComposite(new QName(composite),
                    processingContext);

        } catch (ManagerException e)
        {
            logger.log(Level.SEVERE,e.getMessage(),e);
            throw new FraSCAtiServiceException("Enable to process the '"
                    + composite + "' composite");
        } catch(Exception e)
        {
            logger.log(Level.SEVERE,e.getMessage(),e);
            throw new FraSCAtiServiceException("Enable to process the '"
                    + composite + "' composite");
        }
        finally
        {
            this.warningMessages = processingContext.getWarningMessages();
            this.errorMessages = processingContext.getErrorMessages();
            this.errors = processingContext.getErrors();
            this.warnings = processingContext.getWarnings();
        }
        if(component != null)
        {
            try{

                return getComposite(((NameController)component.getFcInterface(
                    "name-controller")).getFcName());

            } catch(NoSuchInterfaceException e)
            {
                log.log(Level.WARNING,e.getMessage());
            }
        } else if (processingContext.getRootComposite() != null) {
            return processingContext.getRootComposite();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#processComposite(java.lang
     *      .String)
     */
    public Composite processComposite(String composite)
            throws FraSCAtiServiceException
    {
        return processComposite(composite, FraSCAtiServiceItf.all, null);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#state(java.lang.String)
     */
    public String state(String compositeName)
    {
        Component component = null;
        try
        {
            component = getComponent(
                    compositeManager.getTopLevelDomainComposite(),compositeName);

        } catch (Exception e)
        {
            logger.log(Level.SEVERE,e.getMessage(),e);
        }
        if (component != null)
        {
            try
            {
                LifeCycleController lcController = (LifeCycleController) component
                        .getFcInterface("lifecycle-controller");

                return lcController.getFcState();

            } catch (NoSuchInterfaceException e)
            {
                log.log(Level.WARNING,e.getMessage(),e);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#start(java.lang.String)
     */
    public void start(String componentName)
    {
        Component component = null;
        try
        {
            component = getComponent(
                    compositeManager.getTopLevelDomainComposite(),componentName);

        } catch (Exception e)
        {
            logger.log(Level.SEVERE,e.getMessage(),e);
        }
        if (component != null)
        {
            try
            {
                LifeCycleController lcController = (LifeCycleController) component
                        .getFcInterface("lifecycle-controller");
                if (!LifeCycleController.STARTED.equals(lcController
                        .getFcState()))
                {
                    lcController.startFc();
                }
            } catch (NoSuchInterfaceException e)
            {
                logger.log(Level.SEVERE,e.getMessage(),e);

            } catch (IllegalLifeCycleException e)
            {
                logger.log(Level.SEVERE,e.getMessage(),e);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#stop(java.lang.String)
     */
    public void stop(String componentName)
    {
        Component component = null;
        try
        {
            component = getComponent(
                    compositeManager.getTopLevelDomainComposite(),componentName);

        } catch (Exception e)
        {
            logger.log(Level.SEVERE,e.getMessage(),e);
        }
        if (component != null)
        {
            try
            {
                LifeCycleController lcController = (LifeCycleController) component
                        .getFcInterface("lifecycle-controller");
                if (!LifeCycleController.STOPPED.equals(lcController
                        .getFcState()))
                {
                    lcController.stopFc();
                }
            } catch (NoSuchInterfaceException e)
            {
                logger.log(Level.SEVERE,e.getMessage(),e);

            } catch (IllegalLifeCycleException e)
            {
                logger.log(Level.SEVERE,e.getMessage(),e);
            }
        }

    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#remove(java.lang.String)
     */
    public void remove(String compositeName)
            throws FraSCAtiServiceException
    {
        String componentName = IOUtils.pathLastPart(compositeName);
        stop(componentName);
        try
        {
            compositeManager.removeComposite(componentName);

        } catch (ManagerException e)
        {
            logger.log(Level.SEVERE,e.getMessage(),e);
            throw new FraSCAtiServiceException("Enable to remove the '"
            + componentName + "' component");
        }

    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#getService(java.lang.String,
     *      java.lang.String, java.lang.Class)
     */
    public <T> T getService(String componentName,
            String serviceName, Class<T> serviceClass)
            throws FraSCAtiServiceException
    {
        Component component = null;
        try
        {
            component = getComponent(
                    compositeManager.getTopLevelDomainComposite(),componentName);

        } catch (Exception e)
        {
            logger.log(Level.SEVERE,e.getMessage(),e);
        }
        if(component != null)
        {
            try
            {
            	Object serviceFcInterface = component.getFcInterface(serviceName);
            	if (serviceClass.isInstance(serviceFcInterface)) {
            		// else FraSCAti compilation error "Type safety: Unchecked cast from Object to T"
            		// see http://stackoverflow.com/questions/15272855/java-generics-returning-a-list-depending-on-input
            		return serviceClass.cast(serviceFcInterface);
            	}

            } catch (NoSuchInterfaceException e)
            {
                log.log(Level.WARNING,e.getMessage(),e);
            }
        } else
        {
            log.log(Level.WARNING,"Component '" + componentName + "' not found");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#getWarningMessages()
     */
    public List<String> getWarningMessages()
    {
        return warningMessages;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#getErrors()
     */
    public int getErrors()
    {
        return errors;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#getErrorMessages()
     */
    public List<String> getErrorMessages()
    {
        return errorMessages;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#getWarnings()
     */
    public int getWarnings()
    {
        return warnings;
    }

    /**
     *
     * @param mode
     * @return
     */
    private ProcessingMode resovleProcessingMode(int mode)
    {
        switch (mode)
        {
        case FraSCAtiServiceItf.parse:
            return ProcessingMode.parse;
        case FraSCAtiServiceItf.check:
            return ProcessingMode.check;
        case FraSCAtiServiceItf.generate:
            return ProcessingMode.generate;
        case FraSCAtiServiceItf.compile:
            return ProcessingMode.compile;
        case FraSCAtiServiceItf.instantiate:
            return ProcessingMode.instantiate;
        case FraSCAtiServiceItf.complete:
            return ProcessingMode.complete;
        case FraSCAtiServiceItf.start:
            return ProcessingMode.start;
        case FraSCAtiServiceItf.all:
            return ProcessingMode.all;
        default:
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#
     * setScaImporterRecipient(org.easysoa.frascati.api.ScaImporterRecipientItf)
     */
    public void setScaImporterRecipient(ScaImporterRecipientItf recipient)
    {
        this.runtimeSCAImporter.setScaImporterRecipient(recipient);
    }

    /**
     * Return the {@link Component} which path is passed on as a parameter if
     * it exists in the ScaDomain (Top Level Domain Component of FraSCAti)
     *
     * @param componentName
     *          the name of the {@link Component} to return
     * @return
     *          the {@link Component} if it exists, null otherwise
     */
    private Component getComponent(Component currentComponent,
            String componentPath) throws Exception
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
       ContentController contentController = (ContentController) currentComponent.getFcInterface(
               "content-controller");
       Component[] subComponents = contentController.getFcSubComponents();
        if(subComponents == null)
        {
            return null;
        }
        for(Component component : subComponents)
        {
            NameController nameController = (NameController) component.getFcInterface(
                    "name-controller");
            String name = (String) nameController.getFcName();
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

    /**
     * {@inheritDoc}
     *
     * @see org.easysoa.frascati.api.intent.ParserIntentObserverItf#
     * compositeParsed(org.eclipse.stp.sca.Composite)
     */
    public void compositeParsed(Composite composite)
    {
        if(compositesMap == null)
        {
            compositesMap = new HashMap<String,Composite>();
        }
        if(composite != null)
        {
            compositesMap.put(composite.getName(),composite);
        }
    }
}
