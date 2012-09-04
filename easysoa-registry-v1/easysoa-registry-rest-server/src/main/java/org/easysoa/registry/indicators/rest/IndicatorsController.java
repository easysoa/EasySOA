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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.Service;
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
	    DocumentService documentService = Framework.getService(DocumentService.class);
        
        HashMap<String, DocumentModelList> listMap = new HashMap<String, DocumentModelList>();
        listMap.put("Service", session.query(NXQL_SELECT_FROM + "Service" + NXQL_WHERE_NO_PROXY));
        listMap.put(ServiceImplementation.DOCTYPE, session.query(NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE + NXQL_WHERE_NO_PROXY));

        // Count indicators
        
	    HashMap<String, Integer> nbMap = new HashMap<String, Integer>();
        nbMap.put("SoaNode", session.query(NXQL_SELECT_FROM + "SoaNode" + NXQL_WHERE_NO_PROXY).size());
	    nbMap.put("Service", listMap.get("Service").size());
        nbMap.put("SoftwareComponent", session.query(NXQL_SELECT_FROM + "SoftwareComponent" + NXQL_WHERE_NO_PROXY).size());
        nbMap.put("ServiceImplementation", session.query(NXQL_SELECT_FROM + "ServiceImplementation" + NXQL_WHERE_NO_PROXY).size());
        nbMap.put("Deliverable", session.query(NXQL_SELECT_FROM + "Deliverable" + NXQL_WHERE_NO_PROXY).size());
        nbMap.put("DeployedDeliverable", session.query(NXQL_SELECT_FROM + "DeployedDeliverable" + NXQL_WHERE_NO_PROXY).size());
        nbMap.put("Endpoint", session.query(NXQL_SELECT_FROM + "Endpoint" + NXQL_WHERE_NO_PROXY).size());
        nbMap.put("EndpointConsumer", session.query(NXQL_SELECT_FROM + "EndpointConsumer" + NXQL_WHERE_NO_PROXY).size());

        // Count indicators - Service-specific
        int serviceWhithoutImplementationNb = 0;
        int serviceWithImplementationWhithoutEndpointNb = 0;
        for (DocumentModel service : listMap.get("Service")) {
            // finding (all) child implems and then their endpoints
            DocumentModelList serviceImpls = documentService.getChildren(session, service.getRef(), ServiceImplementation.DOCTYPE);
            if (serviceImpls.isEmpty()) {
                serviceWhithoutImplementationNb++;
            } else {
                for (DocumentModel serviceImpl : serviceImpls) {
                    DocumentModelList endpoints = getDocumentService().getChildren(session, serviceImpl.getRef(), Endpoint.DOCTYPE);
                    Serializable isMock = serviceImpl.getPropertyValue(ServiceImplementation.XPATH_ISMOCK);
                    if ((isMock == null || !Boolean.parseBoolean((String) isMock)) && endpoints.isEmpty()) {
                        serviceWithImplementationWhithoutEndpointNb++;
                        break;
                    }
                }
            }
        }
        nbMap.put("serviceWhithoutImplementation", serviceWhithoutImplementationNb); // TODO "main" vs "test" implementation
        nbMap.put("serviceWithImplementationWhithoutEndpoint", serviceWithImplementationWhithoutEndpointNb); // TODO "test", "integration", "staging" ("design", "dev")
        nbMap.put("serviceWhithoutEndpoint", serviceWhithoutImplementationNb + serviceWithImplementationWhithoutEndpointNb);

        // Count indicators - ServiceImplementation-specific
        final int IDEAL_DOCUMENTATION_LINES = 40;
        int undocumentedServiceImpls = 0, documentationLines = 0;
		int maxServiceImplsDocQuality = nbMap.get(ServiceImplementation.DOCTYPE) * IDEAL_DOCUMENTATION_LINES;
        int serviceImplsDocQuality = maxServiceImplsDocQuality;
        Map<Serializable, Boolean> hasMock = new HashMap<Serializable, Boolean>();
        int mockedImplsCount = 0;
        for (DocumentModel serviceImpl : listMap.get(ServiceImplementation.DOCTYPE)) {
        	String documentation = (String) serviceImpl.getPropertyValue(ServiceImplementation.XPATH_DOCUMENTATION);
        	if (documentation != null && !documentation.isEmpty()) {
        		int serviceDocumentationLines = documentation.split("\n").length;
        		documentationLines += serviceDocumentationLines;
        		serviceImplsDocQuality -= Math.max(0, Math.abs(IDEAL_DOCUMENTATION_LINES - serviceDocumentationLines));
        	}
        	else {
        		undocumentedServiceImpls++;
        		serviceImplsDocQuality -= IDEAL_DOCUMENTATION_LINES;
        	}
        	
        	String parentServiceId = null;
        	DocumentModelList implParents = documentService.findAllParents(session, serviceImpl);
        	for (DocumentModel implParent : implParents) {
        		if (Service.DOCTYPE.equals(implParent.getType())) {
        			parentServiceId = (String) implParent.getPropertyValue(Service.XPATH_SOANAME);
        			break;
        		}
        	}
        	if (parentServiceId != null) {
	        	Serializable isMock = serviceImpl.getPropertyValue(ServiceImplementation.XPATH_ISMOCK);
				if (isMock != null && Boolean.parseBoolean((String) isMock)) {
					hasMock.put(parentServiceId, true);
	        	}
				else if (!hasMock.containsKey(parentServiceId)) {
					hasMock.put(parentServiceId, false);
				}
        	}
        }
        for (Boolean isMock : hasMock.values()) {
        	if (isMock) {
        		mockedImplsCount++;
        	}
        }
        nbMap.put("Undocumented service implementation", undocumentedServiceImpls);
        nbMap.put("Lines of documentation per service impl. (average)", (nbMap.get(ServiceImplementation.DOCTYPE) == 0) ? -1 : (documentationLines / nbMap.get(ServiceImplementation.DOCTYPE)));
        nbMap.put("Service impls without mock", hasMock.size() - mockedImplsCount);
        
        // Indicators in %

        HashMap<String, Integer> percentMap = new HashMap<String, Integer>();
        
        percentMap.put("serviceWhithoutImplementation", (nbMap.get("Service") == 0) ? -1 : 100 * serviceWhithoutImplementationNb / nbMap.get("Service"));
        percentMap.put("serviceWhithoutEndpoint", (nbMap.get("Service") == 0) ? -1 :100 * (serviceWhithoutImplementationNb + serviceWithImplementationWhithoutEndpointNb) / nbMap.get("Service"));
        percentMap.put("serviceWithImplementationWhithoutEndpoint", (nbMap.get("Service") - serviceWhithoutImplementationNb == 0) ? -1 :100 * serviceWithImplementationWhithoutEndpointNb / (nbMap.get("Service") - serviceWhithoutImplementationNb));
        
        percentMap.put("Service implementations documentation quality", (maxServiceImplsDocQuality == 0) ? -1 : (100 * serviceImplsDocQuality / maxServiceImplsDocQuality));
        percentMap.put("Non-mocked service impls", (hasMock.size() > 0) ? (100 * mockedImplsCount / hasMock.size()) : -1);

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
