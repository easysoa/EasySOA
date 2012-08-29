/**
 * EasySOA Registry
 * Copyright 2012 Open Wide
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

package org.easysoa.registry.indicators.rest;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.ServiceImplementation;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

/**
 * Indicators
 * 
 * @author mdutoo
 * 
 */
@WebObject(type = "EasySOA")
@Produces(MediaType.TEXT_HTML)
@Path("easysoa")
public class IndicatorsController extends ModuleRoot {

    private static final String NXQL_SELECT_FROM = "SELECT * FROM ";
	private static final String NXQL_CRITERIA_NO_PROXY = "ecm:currentLifeCycleState <> 'deleted' " +
                "AND ecm:isCheckedInVersion = 0 AND ecm:isProxy = 0";
    private static final String NXQL_WHERE_NO_PROXY = " WHERE " + NXQL_CRITERIA_NO_PROXY;

    @GET
    public Object doGet() throws Exception {
	    CoreSession session = SessionFactory.getSession(request);
        
        HashMap<String, DocumentModelList> listMap = new HashMap<String, DocumentModelList>();
        listMap.put("Service", session.query(NXQL_SELECT_FROM + "Service" + NXQL_WHERE_NO_PROXY));
	    
	    HashMap<String, Integer> nbMap = new HashMap<String, Integer>();
        nbMap.put("SoaNode", session.query(NXQL_SELECT_FROM + "SoaNode" + NXQL_WHERE_NO_PROXY).size());
	    nbMap.put("Service", listMap.get("Service").size());
        nbMap.put("SoftwareComponent", session.query(NXQL_SELECT_FROM + "SoftwareComponent" + NXQL_WHERE_NO_PROXY).size());
        nbMap.put("ServiceImplementation", session.query(NXQL_SELECT_FROM + "ServiceImplementation" + NXQL_WHERE_NO_PROXY).size());
        nbMap.put("OperationImplementation", session.query(NXQL_SELECT_FROM + "OperationImplementation" + NXQL_WHERE_NO_PROXY).size());
        nbMap.put("Deliverable", session.query(NXQL_SELECT_FROM + "Deliverable" + NXQL_WHERE_NO_PROXY).size());
        nbMap.put("DeployedDeliverable", session.query(NXQL_SELECT_FROM + "DeployedDeliverable" + NXQL_WHERE_NO_PROXY).size());
        nbMap.put("Endpoint", session.query(NXQL_SELECT_FROM + "Endpoint" + NXQL_WHERE_NO_PROXY).size());
        nbMap.put("EndpointConsumer", session.query(NXQL_SELECT_FROM + "EndpointConsumer" + NXQL_WHERE_NO_PROXY).size());

        int serviceWhithoutImplementationNb = 0;
        int serviceWithImplementationWhithoutEndpointNb = 0;
        for (DocumentModel service : listMap.get("Service")) {
            service = session.getWorkingCopy(service.getRef()); // making sure it's not a proxy
            // finding (all) child implems and then their endpoints
            List<DocumentModel> serviceImpls = getDocumentService().getChildren(session, service.getRef(), ServiceImplementation.DOCTYPE);
            if (serviceImpls.isEmpty()) {
                serviceWhithoutImplementationNb++;
            } else {
                for (DocumentModel serviceImpl : serviceImpls) {
                    serviceImpl = session.getWorkingCopy(serviceImpl.getRef()); // making sure it's not a proxy
                    List<DocumentModel> endpoints = getDocumentService().getChildren(session, serviceImpl.getRef(), Endpoint.DOCTYPE);
                    if (endpoints.isEmpty()) {
                        serviceWithImplementationWhithoutEndpointNb++;
                    }
                }
            }
        }
        nbMap.put("serviceWhithoutImplementation", serviceWhithoutImplementationNb); // TODO "main" vs "test" implementation
        nbMap.put("serviceWithImplementationWhithoutEndpoint", serviceWithImplementationWhithoutEndpointNb); // TODO "test", "integration", "staging" ("design", "dev")
        nbMap.put("serviceWhithoutEndpoint", serviceWhithoutImplementationNb + serviceWithImplementationWhithoutEndpointNb);
        

        HashMap<String, Integer> percentMap = new HashMap<String, Integer>();
        
        percentMap.put("serviceWhithoutImplementation", 100 * serviceWhithoutImplementationNb / nbMap.get("Service"));
        percentMap.put("serviceWhithoutEndpoint", 100 * (serviceWhithoutImplementationNb + serviceWithImplementationWhithoutEndpointNb) / nbMap.get("Service"));
        percentMap.put("serviceWithImplementationWhithoutEndpoint", 100 * serviceWithImplementationWhithoutEndpointNb / (nbMap.get("Service") - serviceWhithoutImplementationNb));

        // TODO model consistency ex. impl without service
        // TODO for one ex. impl of ONE service => prop to query
        
        return getView("indicators")
                .arg("nbMap", nbMap)
                .arg("percentMap", percentMap);
    }

    public static DocumentService getDocumentService() throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        if (documentService == null) {
            throw new WebException("Unable to get documentService");
        }
        return documentService;
    }

}
