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
import org.easysoa.registry.frascati.FraSCAtiServiceItf;
import org.easysoa.sca.visitors.BindingVisitorFactory;

/**
 * Sca Importer (Nuxeo free), uses the Registry API to register services in Nuxeo
 * @author jguillemotte
 *
 */
public class ApiFraSCAtiScaImporter extends FraSCAtiScaImporterBase implements FraSCAtiRuntimeScaImporterItf {

	/**
	 * Default constructor
	 * @throws Exception
	 */

	public ApiFraSCAtiScaImporter(BindingVisitorFactory bindingVisitorFactory, File compositeFile,
	        FraSCAtiServiceItf frascatiService) throws Exception {
		super(bindingVisitorFactory, compositeFile, frascatiService);
	}

	@Override
	public String getModelProperty(String arg0, String arg1) throws Exception {
		return null;
	}

	@Override
	public void setParentAppliImpl(Object appliImplModel) {
		// Nothing to do here
	}
	
}
