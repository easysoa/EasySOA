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

package org.easysoa.registry.frascati;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.sca.frascati.AbstractScaImporterBase;
import org.eclipse.stp.sca.BaseReference;
import org.eclipse.stp.sca.BaseService;
import org.eclipse.stp.sca.Binding;
import org.eclipse.stp.sca.WebServiceBinding;

public class WSBindingInfoProvider extends FrascatiBindingInfoProviderBase {

	// Logger
	private static Log log = LogFactory.getLog(WSBindingInfoProvider.class);
	
	/**
	 * 
	 * @param frascatiScaImporter
	 */
	public WSBindingInfoProvider(AbstractScaImporterBase frascatiScaImporter) {
		super(frascatiScaImporter);
	}
	
	@Override
	public boolean isOkFor(Object object) {
		if (object instanceof WebServiceBinding) {
			return true;
		}
		return false;
	}

	@Override
	public String getBindingUrl() {
    	String serviceUrl = null;
    	Binding binding = null;
    	if(frascatiScaImporter.getCurrentBinding() instanceof BaseService){
    		// NB. only difference between Service and ComponentService is Service.promote
    		BaseService componentService = (BaseService) frascatiScaImporter.getCurrentBinding();
    		// TODO do not take only the first one, missing bindings in the case of multiple bindings
    		if(componentService.getBinding() != null && componentService.getBinding().size() > 0){
    			binding = componentService.getBinding().get(0);
    		}
    	} else if(frascatiScaImporter.getCurrentBinding() instanceof BaseReference){
    		// NB. only difference between Reference and ComponentReferenceis Reference.promote
    		BaseReference componentReference = (BaseReference) frascatiScaImporter.getCurrentBinding();
    		if(componentReference.getBinding() != null && componentReference.getBinding().size() > 0){
    			binding = componentReference.getBinding().get(0);
    		}
    	}
    	if (binding != null) {
    		serviceUrl = binding.getUri();
    	}
    	log.debug("binding name : " + binding.getName());    	
    	if(serviceUrl == null){
			// wsdlLocation in case of wsdl binding
			List<String> wsdlLocations = ((WebServiceBinding) binding).getWsdlLocation();
			if (wsdlLocations != null && wsdlLocations.size() != 0) {
				serviceUrl = wsdlLocations.get(0).replace("?wsdl", "");
			}
    	}
    	log.debug("serviceUrl : " + serviceUrl);
    	return serviceUrl;
	}

}
