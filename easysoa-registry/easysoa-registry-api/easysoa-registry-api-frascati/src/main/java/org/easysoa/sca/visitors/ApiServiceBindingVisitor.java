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

package org.easysoa.sca.visitors;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.api.EasySOAApiSession;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.properties.ApiUrlProcessor;
import org.easysoa.sca.BindingInfoProvider;
import org.easysoa.sca.IScaImporter;

/**
 * Visitor for WS bindings.
 * Creates new services when <binding.ws> tags are found.
 * @author mkalam-alami
 *
 */
public class ApiServiceBindingVisitor extends ApiScaVisitorBase {
    
	private static Log log = LogFactory.getLog(ApiServiceBindingVisitor.class);	
	
	private EasySOAApiSession api;
	
	/**
	 * 
	 * @param scaImporter
	 */
    public ApiServiceBindingVisitor(IScaImporter scaImporter, EasySOAApiSession api) {
        super(scaImporter);
        this.api = api;
    }

    @Override
    public void visit(BindingInfoProvider bindingInfoProvider) throws Exception {
        String serviceUrl = bindingInfoProvider.getBindingUrl();
        if (serviceUrl == null) {
        	log.warn("serviceUrl is null, returning !");
        	// TODO : throws here an exception to be displayed in the GUI
        	return;
        }
        log.debug("serviceUrl is not null, registering API's and services...");
        //String appliImplUrl = (String) scaImporter.getParentAppliImplModel().getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL);
        String appliImplUrl = scaImporter.getModelProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL);
        String apiUrl = ApiUrlProcessor.computeApiUrl(appliImplUrl, scaImporter.getServiceStackUrl(), serviceUrl);
        // Using default value for API url
	    
	    // enrich or create API
	    Map<String, String> properties = new HashMap<String, String>();
        properties.put(ServiceAPI.PROP_URL, apiUrl);
        properties.put(ServiceAPI.PROP_TITLE, scaImporter.getServiceStackType()); // TODO better, ex. from composite name...
        properties.put(ServiceAPI.PROP_DTIMPORT, scaImporter.getCompositeFile().getName());
        properties.put(ServiceAPI.PROP_PARENTURL, appliImplUrl);	    
	    api.notifyServiceApi(properties);
	    
	    // enrich or create service
        properties.clear();
	    properties.put(Service.PROP_URL, serviceUrl);
	    properties.put(Service.PROP_TITLE, scaImporter.getCurrentArchiName());
	    properties.put(Service.PROP_PARENTURL, apiUrl);
	    properties.put(Service.PROP_ARCHIPATH, scaImporter.toCurrentArchiPath());
	    properties.put(Service.PROP_ARCHILOCALNAME, scaImporter.getCurrentArchiName());
	    properties.put(Service.PROP_DTIMPORT, scaImporter.getCompositeFile().getName()); // TODO also upload and link to it ?	    
	    api.notifyService(properties);
    }
    
    @Override
    public void postCheck() throws Exception {
        // nothing to do
    }

	@Override
	public void setDocumentManager(Object documentManager) {
		// Nothing to do, document manager is not used here
	}

}
