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
import java.util.List;

import javax.xml.namespace.QName;

import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.RegistryItf;
import org.easysoa.frascati.api.ScaImporterIntermediaryItf;
import org.easysoa.frascati.api.ScaImporterRecipientItf;
import org.easysoa.frascati.processor.EasySOAProcessingContext;
import org.eclipse.stp.sca.Composite;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.ow2.frascati.assembly.factory.api.ClassLoaderManager;
import org.ow2.frascati.assembly.factory.api.CompositeManager;
import org.ow2.frascati.assembly.factory.api.ManagerException;
import org.ow2.frascati.assembly.factory.api.ProcessingMode;
import org.ow2.frascati.util.AbstractLoggeable;
import org.ow2.frascati.util.FrascatiClassLoader;

/**
 * Implementation of the {@link FraSCAtiServiceItf}
 * 
 * @author Christophe Munilla
 *
 */
@Scope("COMPOSITE")
@Service(FraSCAtiServiceItf.class)
public class FraSCAtiService 
extends AbstractLoggeable implements FraSCAtiServiceItf
{
    @Reference(name = "registry")
    private RegistryItf registry;

    @Reference(name = "composite-manager")
    private CompositeManager compositeManager;
    
    /**
     * The required CompositeManager
     */
    @Reference(name = "classloader-manager") 
    private ClassLoaderManager classLoaderManager;
    
    @Reference(name = "runtime-sca-importer")
    private ScaImporterIntermediaryItf runtimeSCAImporter;

    private List<String> warningMessages;
    private List<String> errorMessages;
    private int errors;
    private int warnings;

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#getComposite(java.lang.String
     *      )
     */
    public Composite getComposite(String compositeName)
            throws FraSCAtiServiceException
    {
        Composite composite = registry.getComposite(compositeName);
        return composite;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#processContribution(java.
     *      lang.String, int, java.net.URL[])
     */
    public String[] processContribution(String contribution,
            int processingMode, URL... urls) throws FraSCAtiServiceException
    {
        EasySOAProcessingContext processingContext = 
            new EasySOAProcessingContext(classLoaderManager.getClassLoader());
        FrascatiClassLoader fcl = 
                (FrascatiClassLoader)processingContext.getClassLoader();
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
        try
        {
            compositeManager.processContribution(contribution,
                    processingContext);

        } catch (ManagerException e)
        {
            e.printStackTrace();
            throw new FraSCAtiServiceException("Enable to process the '"
                    + contribution + "' contribution");
            
        }catch(Exception e)
        {
            e.printStackTrace();
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
        List<String> processed = registry.getProcessedComponentList();
        String[] emptyProcessed = new String[0];
        if(processed != null)
        {
            return processed.toArray(emptyProcessed);
        } else 
        {
            return emptyProcessed;
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
        return processContribution(contribution, FraSCAtiServiceItf.all);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#processComposite(java.lang
     *      .String, int, java.net.URL[])
     */
    public String processComposite(String composite, int processingMode,
            URL... urls) throws FraSCAtiServiceException
    {
        EasySOAProcessingContext processingContext = 
                new EasySOAProcessingContext(classLoaderManager.getClassLoader());
        FrascatiClassLoader fcl = 
                (FrascatiClassLoader)processingContext.getClassLoader();
        if(urls != null)
        {
          for(URL url : urls)
          {
              fcl.addUrl(url);
          }
        }
        processingContext.setProcessingMode(resovleProcessingMode(processingMode));
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try
        {   
            Thread.currentThread().setContextClassLoader(fcl);
            Component component = compositeManager.processComposite(
                    new QName(composite),processingContext);
        } catch (ManagerException e)
        {
            e.printStackTrace();
            throw new FraSCAtiServiceException("Enable to process the '"
                    + composite + "' composite");
        } catch(Exception e)
        {
            e.printStackTrace();
            throw new FraSCAtiServiceException("Enable to process the '"
                    + composite + "' composite");
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(current);
            this.warningMessages = processingContext.getWarningMessages();
            this.errorMessages = processingContext.getErrorMessages();
            this.errors = processingContext.getErrors();
            this.warnings = processingContext.getWarnings();
        }
        return registry.getProcessedComponentList().get(0);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#processComposite(java.lang
     *      .String)
     */
    public String processComposite(String composite)
            throws FraSCAtiServiceException
    {
        return processComposite(composite, FraSCAtiServiceItf.all);
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#state(java.lang.String)
     */
    public String state(String compositeName)
    {
        Component component = registry.getComponent(compositeName);
        if (component != null)
        {
            try
            {
                LifeCycleController lcController = (LifeCycleController) component
                        .getFcInterface("lifecycle-controller");
                
                return lcController.getFcState();
                
            } catch (NoSuchInterfaceException e)
            {
                e.printStackTrace();
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
        Component component = registry.getComponent(componentName);
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
                e.printStackTrace();
            } catch (IllegalLifeCycleException e)
            {
                e.printStackTrace();
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
        Component component = registry.getComponent(componentName);
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
                e.printStackTrace();
            } catch (IllegalLifeCycleException e)
            {
                e.printStackTrace();
            }
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.FraSCAtiServiceItf#remove(java.lang.String)
     */
    public void remove(String componentName)
            throws FraSCAtiServiceException
    {
        stop(componentName);
        try
        {
            compositeManager.removeComposite(componentName);
        } catch (ManagerException e)
        {
            e.printStackTrace();
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
        return registry.getService(componentName, serviceName,
                serviceClass);
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
}
