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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.rest.RestNotificationFactory;
import org.easysoa.rest.RestNotificationRequest;
import org.easysoa.rest.RestNotificationFactory.RestDiscoveryService;
import org.easysoa.sca.BindingInfoProvider;
import org.easysoa.sca.IScaImporter;
import org.easysoa.services.ApiUrlProcessor;

/**
 * Visitor for WS bindings.
 * Creates new services when <binding.ws> tags are found.
 * @author mkalam-alami
 *
 */
public class ApiServiceBindingVisitor extends ApiScaVisitorBase {
    
	private static Log log = LogFactory.getLog(ApiServiceBindingVisitor.class);	
	
	/**
	 * 
	 * @param scaImporter
	 */
    public ApiServiceBindingVisitor(IScaImporter scaImporter) {
        super(scaImporter);
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
	    RestNotificationFactory factory = new RestNotificationFactory();
	    
	    // enrich or create API
	    RestNotificationRequest requestServiceApi = factory.createNotification(RestDiscoveryService.SERVICEAPI);
	    requestServiceApi.setProperty(ServiceAPI.PROP_URL, apiUrl);
	    requestServiceApi.setProperty(ServiceAPI.PROP_TITLE, scaImporter.getServiceStackType()); // TODO better, ex. from composite name...
	    requestServiceApi.setProperty(ServiceAPI.PROP_DTIMPORT, scaImporter.getCompositeFile().getName());
	    requestServiceApi.setProperty(ServiceAPI.PROP_PARENTURL, appliImplUrl);	    
	    requestServiceApi.send();
	    
	    // enrich or create service
	    RestNotificationRequest requestService = factory.createNotification(RestDiscoveryService.SERVICE);	    
	    requestService.setProperty(Service.PROP_URL, serviceUrl);
	    requestService.setProperty(Service.PROP_TITLE, scaImporter.getCurrentArchiName());
	    requestService.setProperty(Service.PROP_PARENTURL, apiUrl);
	    requestService.setProperty(Service.PROP_ARCHIPATH, scaImporter.toCurrentArchiPath());
	    requestService.setProperty(Service.PROP_ARCHILOCALNAME, scaImporter.getCurrentArchiName());
	    requestService.setProperty(Service.PROP_DTIMPORT, scaImporter.getCompositeFile().getName()); // TODO also upload and link to it ?	    
	    requestService.send();
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
