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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.Service;
import org.easysoa.rest.soapui.SoapUIWSDL;
import org.easysoa.rest.soapui.SoapUIWSDLFactory;
//import org.easysoa.services.DocumentService; // Old document service
import org.easysoa.registry.DocumentService;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.view.TemplateView;
import org.nuxeo.runtime.api.Framework;

@Path("easysoa/soapui")
public class SoapUIConfRest {

    private static final Log log = LogFactory.getLog(SoapUIConfRest.class);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Object doGetSoapUIConf(@Context HttpServletRequest request,
            @PathParam("id") String docId) throws Exception {

        docId = docId.replace(".xml", "");

        // Init
        CoreSession session = SessionFactory.getSession(request);
        DocumentModel requestedDocumentModel = session.getDocument(new IdRef(docId));
        DocumentService docService = Framework.getService(DocumentService.class);

        //DocumentModelList serviceModels = docService.findChildren(session, requestedDocumentModel, Service.DOCTYPE);
        DocumentModelList serviceModels = docService.getChildren(session, requestedDocumentModel.getRef(), Service.DOCTYPE);

        // Set up params (= WSDLs)
        List<SoapUIWSDL> wsdls = new ArrayList<SoapUIWSDL>();
        SoapUIWSDLFactory wsdlFactory = new SoapUIWSDLFactory();
        for (DocumentModel serviceModel : serviceModels) {

            Blob wsdlBlob = (Blob) serviceModel.getPropertyValue("file:content");
            if (wsdlBlob != null) {
                File tmpWsdlFile = File.createTempFile(serviceModel.getTitle().replaceAll("/", "_"), "wsdl");
                wsdlBlob.transferTo(tmpWsdlFile);
                SoapUIWSDL newWSDL = wsdlFactory.create(tmpWsdlFile,
                       (String) serviceModel.getProperty(Service.SCHEMA, Service.PROP_FILEURL));
                wsdls.add(newWSDL);
            }
            else {
                log.info("Could not generate full SoapUI config: a service WSDL is not available");
            }
        }

        // Render
        TemplateView view = new TemplateView(this, "soapuiconf.xml");
        view.arg("wsdls", wsdls);
        return view;
    }

}