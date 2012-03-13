/**
 * EasySOA Registry
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

package org.easysoa.sca.frascati;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.api.EasySOAApiSession;
import org.easysoa.registry.frascati.FileUtils;
import org.easysoa.registry.frascati.FraSCAtiRegistryServiceItf;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.eclipse.stp.sca.Binding;
import org.eclipse.stp.sca.Component;
import org.eclipse.stp.sca.ComponentReference;
import org.eclipse.stp.sca.ComponentService;
import org.eclipse.stp.sca.Composite;
import org.eclipse.stp.sca.Reference;
import org.eclipse.stp.sca.Service;
import org.nuxeo.ecm.core.api.ClientException;

public abstract class FraSCAtiScaImporterBase extends AbstractScaImporterBase
{
    private static Log log = LogFactory.getLog(FraSCAtiScaImporterBase.class);
    private EasySOAApiSession api;
    protected FraSCAtiRegistryServiceItf frascatiRegistryService;

    /**
     * Constructor
     * 
     * @param documentManager
     *            Nuxeo document manager
     * @param compositeFile
     *            Composite file to import, can be null if this is used as a runtime
     *            importer
     * @throws FrascatiException
     * @throws ClientException
     */
    public FraSCAtiScaImporterBase(
            BindingVisitorFactory bindingVisitorFactory,
            File compositeFile,
            FraSCAtiRegistryServiceItf frascatiRegistryService)
    {
        super(bindingVisitorFactory, compositeFile);
        this.frascatiRegistryService = frascatiRegistryService;
    }

    @Override 
    public void importSCA() throws Exception
    {
        // If the filename is not set, it is not possible to choose the corresponding
        // import method
        if (compositeFile.getName() == null
                || "".equals(compositeFile.getName()))
        {
            throw new Exception(
                    "Invalid file name (null or empty) : please check that the file name is set");
        }
        String scaFileName = compositeFile.getName();
        if (scaFileName.endsWith(".composite"))
        {
            importSCAComposite();
        } else if (scaFileName.endsWith(".jar") || scaFileName.endsWith(".zip"))
        {
            importSCAZip();
        } else
        {
            throw new Exception(
                    "Unsupported file type : neither a Composite nor an SCA zip or jar");
        }
    }

    /**
     * Import all the composite contained in the zip archive requires all ref'd composites
     * and classes to be there TODO rather replace by old XML one ??
     * 
     * @throws Exception
     */
    public void importSCAZip() throws Exception
    {
        log.debug("Importing SCA Composite contained in ZIP");
        // Initialization
        File scaZipTmpFile =
                File.createTempFile(UUID.randomUUID().toString(), ".jar");
        // Replace this code, record the content of the composite in the new tmp composite
        FileUtils.copyTo(compositeFile, scaZipTmpFile);
        // Read an (Eclipse SOA) SCA composite then visit it
        Set<Composite> scaZipCompositeSet = frascatiRegistryService.readScaZip(
                scaZipTmpFile);
        // Visit each composite
        for (Composite scaZipComposite : scaZipCompositeSet)
        {
            visitComposite(scaZipComposite);
        }
    }

    /**
     * Import a SCA composite
     * 
     * @throws Exception
     */
    public void importSCAComposite() throws Exception
    {
        log.debug("Importing SCA Composite");
        // Initialization
        // TODO works only with physical files, add modifications to work with composite
        // objects
        // Make 2 different constructors : one with file, other with composite and then
        // make tests like the following code
        /*
         * if(this.compositeFile != null){ // Import sca file } else if this.scaComposite
         * != null { // Import sca composite } else { // Throws an error }
         */
        File compositeTmpFile = File.createTempFile(UUID.randomUUID().toString(),
                ".composite");
        FileUtils.copyTo(compositeFile, compositeTmpFile);
        
        // Read an (Eclipse SOA) SCA composite then visit it
        log.debug("frascatiService : " + frascatiRegistryService);
        Composite scaZipComposite = frascatiRegistryService.readComposite(
                compositeTmpFile.toURI().toURL());
        
        log.debug("composite : " + scaZipComposite);
        visitComposite(scaZipComposite);
    }

    /**
     * Visit the composite. For each service, component, reference ... visit the
     * associated bindings
     * 
     * @param scaComposite
     *            the composite to visit
     */
    public void visitComposite(Composite scaComposite)
    {
        log.info("visiting composite");
        log.info("scaComposite : " + scaComposite);
        if (scaComposite == null)
        {
            return;
        }
        log.info("Composite.name =" + scaComposite.getName());
        // Get services
        for (Service service : scaComposite.getService())
        {
            log.debug("Service.name=" + service.getName());
            getArchiNameStack().push(service.getName());
            getBindingStack().push(service);
            for (Binding binding : service.getBinding())
            {
                try
                {
                    acceptBindingVisitors(binding,
                            createServiceBindingVisitor(),
                            createBindingInfoProviders());
                } catch (Exception e)
                {
                    log.warn("A problem occurs during the visit of services", e);
                }
            }
            getBindingStack().pop();
            getArchiNameStack().pop();
        }
        // Get references
        for (Reference reference : scaComposite.getReference())
        {
            log.debug("Reference.name=" + reference.getName());
            getArchiNameStack().push(reference.getName());
            getBindingStack().push(reference);
            for (Binding binding : reference.getBinding())
            {
                try
                {
                    acceptBindingVisitors(binding,
                            createReferenceBindingVisitor(),
                            createBindingInfoProviders());
                } catch (Exception e)
                {
                    log.warn("A problem occurs during the visit of references",
                            e);
                }
            }
            getBindingStack().pop();
            getArchiNameStack().pop();
        }
        // Get components
        for (Component component : scaComposite.getComponent())
        {
            log.debug("Component.name=" + component.getName());
            getArchiNameStack().push(component.getName());
            getBindingStack().push(component);
            // Get component services
            for (ComponentService componentService : component.getService())
            {
                log.debug("ComponentService.name=" + componentService.getName());
                getArchiNameStack().push(componentService.getName());
                getBindingStack().push(componentService);
                for (Binding binding : componentService.getBinding())
                {
                    try
                    {
                        acceptBindingVisitors(binding,
                                createServiceBindingVisitor(),
                                createBindingInfoProviders());
                    } catch (Exception e)
                    {
                        log.warn(
                                "A problem occurs during the visit of component services",
                                e);
                    }
                }
                getBindingStack().pop();
                getArchiNameStack().pop();
            }
            // Get component references
            for (ComponentReference componentReference : component
                    .getReference())
            {
                log.debug("ComponentReference.name="
                        + componentReference.getName());
                getArchiNameStack().push(componentReference.getName());
                getBindingStack().push(componentReference);
                for (Binding binding : componentReference.getBinding())
                {
                    try
                    {
                        acceptBindingVisitors(binding,
                                createReferenceBindingVisitor(),
                                createBindingInfoProviders());
                    } catch (Exception e)
                    {
                        log.warn(
                                "A problem occurs during the visit of component references",
                                e);
                    }
                }
                getArchiNameStack().pop();
                getBindingStack().pop();
            }
            getArchiNameStack().pop();
            getBindingStack().pop();
        }
    }

    protected EasySOAApiSession getApi()
    {
        return api;
    }
}