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

import org.easysoa.registry.frascati.FraSCAtiRuntimeScaImporterItf;
import org.easysoa.registry.frascati.NxFraSCAtiRegistryService;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.easysoa.sca.visitors.LocalBindingVisitorFactory;
import org.easysoa.services.DocumentService;
import org.eclipse.stp.sca.Composite;
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
public class FraSCAtiScaImporter extends FraSCAtiScaImporterBase implements FraSCAtiRuntimeScaImporterItf {

	private DocumentModel parentAppliImplModel;	
	private CoreSession documentManager;
	
	/**
	 * 
	 * @param documentManager
	 * @param compositeFile
	 * @throws Exception 
	 * @throws ClientException 
	 */
	public FraSCAtiScaImporter(BindingVisitorFactory bindingVisitorFactory, /*CoreSession documentManager,*/ File compositeFile) throws ClientException, Exception{
		super(bindingVisitorFactory, compositeFile, Framework.getService(NxFraSCAtiRegistryService.class));
		if(bindingVisitorFactory instanceof LocalBindingVisitorFactory){
			this.documentManager = ((LocalBindingVisitorFactory)bindingVisitorFactory).getDocumentManager();
		}
		if(documentManager != null){
			parentAppliImplModel = Framework.getRuntime().getService(DocumentService.class).getDefaultAppliImpl(documentManager);
		}
	}
    
    @Override
    public void visitComposite(Composite composite){
    	super.visitComposite(composite);
    }

	@Override
	public String getModelProperty(String arg0, String arg1) throws Exception {
		return (String)(parentAppliImplModel.getProperty(arg0, arg1));
	}

	@Override
	public void setParentAppliImpl(Object appliImplModel) {
		if(appliImplModel instanceof DocumentModel){
			parentAppliImplModel = (DocumentModel) appliImplModel;
		}
	}
    
}
