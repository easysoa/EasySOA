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

package org.easysoa.rest;

import java.io.StringWriter;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 * 
 */
@Path("easysoa/properties")
public class PropertiesFileRest {

    /**
     * Lists a list of all services in a properties file
     */

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Object doGetSoapUIConf(@Context HttpServletRequest request, 
            @PathParam("id") String docId) throws Exception {
        
        docId = docId.replace(".properties", "");
        
        // Init
        CoreSession session = SessionFactory.getSession(request);
        DocumentModel requestedDocumentModel = session.getDocument(new IdRef(docId));
        DocumentService docService = Framework.getService(DocumentService.class);
        DocumentModelList serviceModels = docService.findChildren(session, requestedDocumentModel, Service.DOCTYPE);
        DocumentModelList serviceRefsModels = docService.findChildren(session, requestedDocumentModel, ServiceReference.DOCTYPE);
        
        // Gather properties
        Properties properties = new Properties();
        for (DocumentModel serviceModel : serviceModels) {
        	properties.put(serviceModel.getTitle(), (String) serviceModel.getProperty(Service.SCHEMA, Service.PROP_URL));
        }
        for (DocumentModel serviceRefModel : serviceRefsModels) {
        	properties.put(serviceRefModel.getTitle(), (String) serviceRefModel.getProperty(
        			ServiceReference.SCHEMA, ServiceReference.PROP_REFURL));
        }
        
        // Render
        String comment = "Configuration generated for services in " + requestedDocumentModel.getType() + " " + requestedDocumentModel.getTitle();
        StringWriter sw = new StringWriter();
        properties.store(sw, comment);
        return sw.getBuffer().toString();
    }
    
}