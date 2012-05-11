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

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.registry.frascati.FraSCAtiRegistryServiceItf;
import org.easysoa.sca.BindingInfoProvider;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.easysoa.sca.visitors.LocalBindingVisitorFactory;
import org.easysoa.sca.visitors.ScaVisitor;
import org.easysoa.services.DocumentService;
import org.eclipse.stp.sca.Binding;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

/**
 * Sca Importer : Uses Nuxeo API's to register services
 * 
 * @author jguillemotte
 * 
 */
public class FraSCAtiScaImporter extends ApiRuntimeFraSCAtiScaImporter 
{
    
    /**
     * 
     * @param documentManager
     * @param compositeFile
     * @throws Exception
     * @throws ClientException
     */
    public FraSCAtiScaImporter(BindingVisitorFactory bindingVisitorFactory,
            File compositeFile) 
    throws ClientException, Exception
    {
        super(bindingVisitorFactory, compositeFile, 
                Framework.getService(FraSCAtiRegistryServiceItf.class));

        DocumentModel parentAppliImplModel = null;
        CoreSession documentManager = null;
        
        if (bindingVisitorFactory instanceof LocalBindingVisitorFactory)
        {
            documentManager = ((LocalBindingVisitorFactory) bindingVisitorFactory).getDocumentManager();
        }
        if (documentManager != null)
        {
            parentAppliImplModel = Framework.getRuntime().getService(
                 DocumentService.class).getDefaultAppliImpl(documentManager);
            

            super.setAppliImplURL((String) parentAppliImplModel.getProperty(
                    AppliImpl.SCHEMA, AppliImpl.PROP_URL));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.ScaImporterRecipientItf
     * #runtimeServiceBindingVisit()
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