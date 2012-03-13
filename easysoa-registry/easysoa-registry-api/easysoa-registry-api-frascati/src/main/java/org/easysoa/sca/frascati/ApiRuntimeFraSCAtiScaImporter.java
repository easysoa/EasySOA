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
import java.util.List;

import org.easysoa.registry.frascati.FraSCAtiRegistryServiceItf;
//import org.easysoa.registry.frascati.FraSCAtiRuntimeScaImporterItf;
import org.easysoa.sca.BindingInfoProvider;
import org.easysoa.sca.IScaRuntimeImporter;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.easysoa.sca.visitors.ScaVisitor;
import org.eclipse.stp.sca.Binding;

/**
 * Sca Importer (Nuxeo free), uses the Registry API to register services in
 * Nuxeo
 * 
 * @author jguillemotte
 */
public class ApiRuntimeFraSCAtiScaImporter extends FraSCAtiScaImporterBase
        implements IScaRuntimeImporter
{

    /**
     * Default constructor
     * 
     * @throws Exception
     */
    public ApiRuntimeFraSCAtiScaImporter(
            BindingVisitorFactory bindingVisitorFactory, File compositeFile,
            FraSCAtiRegistryServiceItf frascatiService) throws Exception
    {
        super(bindingVisitorFactory, compositeFile, frascatiService);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.ScaImporterRecipientItf
     *      #runtimeServiceBindingVisit()
     */
    public void runtimeServiceBindingVisit()
    {
        Binding binding = (Binding) getBindingStack().pop();
        ScaVisitor visitor = createServiceBindingVisitor();
        List<BindingInfoProvider> bindingInfoProviders = createBindingInfoProviders();
        acceptBindingVisitors(binding, visitor, bindingInfoProviders);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.ScaImporterRecipientItf#runtimeReferenceBindingVisit()
     */
    public void runtimeReferenceBindingVisit()
    {
        Binding binding = (Binding) getBindingStack().pop();
        ScaVisitor visitor = createReferenceBindingVisitor();
        List<BindingInfoProvider> bindingInfoProviders = createBindingInfoProviders();
        acceptBindingVisitors(binding, visitor, bindingInfoProviders);
    }
}
