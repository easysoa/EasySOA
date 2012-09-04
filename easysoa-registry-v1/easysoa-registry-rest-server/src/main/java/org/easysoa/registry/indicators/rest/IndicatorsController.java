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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.Service;
import org.easysoa.registry.types.ServiceImplementation;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.query.sql.NXQL;
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
        nbMap.put("SoaNode", countFromQuery(session, NXQL_SELECT_FROM + "SoaNode" + NXQL_WHERE_NO_PROXY));
	    nbMap.put("Service", listMap.get("Service").size());
        nbMap.put("SoftwareComponent", countFromQuery(session, NXQL_SELECT_FROM + "SoftwareComponent" + NXQL_WHERE_NO_PROXY));
        nbMap.put("ServiceImplementation", countFromQuery(session, NXQL_SELECT_FROM + "ServiceImplementation" + NXQL_WHERE_NO_PROXY));
        nbMap.put("Deliverable", countFromQuery(session, NXQL_SELECT_FROM + "Deliverable" + NXQL_WHERE_NO_PROXY));
        nbMap.put("DeployedDeliverable", countFromQuery(session, NXQL_SELECT_FROM + "DeployedDeliverable" + NXQL_WHERE_NO_PROXY));
        nbMap.put("Endpoint", countFromQuery(session, NXQL_SELECT_FROM + "Endpoint" + NXQL_WHERE_NO_PROXY));
        nbMap.put("EndpointConsumer", countFromQuery(session, NXQL_SELECT_FROM + "EndpointConsumer" + NXQL_WHERE_NO_PROXY));
        nbMap.put("TaggingFolder", countFromQuery(session, NXQL_SELECT_FROM + "TaggingFolder" + NXQL_WHERE_NO_PROXY));

        // Count indicators - Service-specific
        int serviceWhithoutImplementationNb = 0;
        HashSet<String> serviceWhithoutImplementationIdSet = new HashSet<String>(nbMap.get("Service"));
        int serviceWithImplementationWhithoutEndpointNb = 0;
        for (DocumentModel service : listMap.get("Service")) {
            // finding (all) child implems and then their endpoints
            DocumentModelList serviceImpls = getDocumentService().getChildren(session, service.getRef(), ServiceImplementation.DOCTYPE);
            if (serviceImpls.isEmpty()) {
                serviceWhithoutImplementationNb++;
                serviceWhithoutImplementationIdSet.add(service.getId());
            } else {
            	boolean isAServiceWithImplementationWhithoutEndpoint = true;
                for (DocumentModel serviceImplModel : serviceImpls) {
                    DocumentModelList endpoints = getDocumentService().getChildren(session, serviceImplModel.getRef(), Endpoint.DOCTYPE);
                    ServiceImplementation serviceImpl = serviceImplModel.getAdapter(ServiceImplementation.class);
                    if (!serviceImpl.isMock() && endpoints.isEmpty()) {
                    	isAServiceWithImplementationWhithoutEndpoint = false;
                        break;
                    }
                }
                if (isAServiceWithImplementationWhithoutEndpoint) {
                	serviceWithImplementationWhithoutEndpointNb++;
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
        int mockedImplsCount = 0, testedImplsCount = 0, nonMockImplsCount = 0;
        for (DocumentModel serviceImplModel : listMap.get(ServiceImplementation.DOCTYPE)) {
        	ServiceImplementation serviceImpl = serviceImplModel.getAdapter(ServiceImplementation.class);
        	
        	if ("(Placeholder)".equals(serviceImpl.getTitle())) { // TODO Specific field?
        		continue;
        	}
        	
        	String documentation = (String) serviceImpl.getProperty(ServiceImplementation.XPATH_DOCUMENTATION);
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
        	DocumentModelList implParents = documentService.findAllParents(session, serviceImplModel);
        	for (DocumentModel implParent : implParents) {
        		if (Service.DOCTYPE.equals(implParent.getType())) {
        			parentServiceId = (String) implParent.getPropertyValue(Service.XPATH_SOANAME);
        			break;
        		}
        	}
        	if (parentServiceId != null) {
				if (serviceImpl.isMock()) {
					hasMock.put(parentServiceId, true);
	        	}
				else if (!hasMock.containsKey(parentServiceId)) {
					hasMock.put(parentServiceId, false);
				}
        	}
        	
			if (!serviceImpl.isMock()) {
				nonMockImplsCount++;
				if (!serviceImpl.getTests().isEmpty()) {
					testedImplsCount++;
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
        nbMap.put("Service impls without mock", nonMockImplsCount - mockedImplsCount);
        nbMap.put("Service impls without test", nonMockImplsCount - testedImplsCount);
        
        // not in any software component :
        DocumentModelList deliverableProxyInASoftwareComponent = session.query(NXQL_SELECT_FROM + "Deliverable"
                + " WHERE " + "ecm:currentLifeCycleState <> 'deleted' " + "AND ecm:isProxy = 1"
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/SoftwareComponent" + "'");
        listMap.put("deliverable in no software component", session.query(NXQL_SELECT_FROM + "Deliverable" + NXQL_WHERE_NO_PROXY
                + " AND ecm:uuid NOT IN " + getProxiedIdLiteralList(session, deliverableProxyInASoftwareComponent)));
        nbMap.put("deliverable in no software component", listMap.get("deliverable in no software component").size());
        listMap.put("deliverable in no software component' implementations", session.query(NXQL_SELECT_FROM + "ServiceImplementation"
                + " WHERE " + "ecm:currentLifeCycleState <> 'deleted' " + "AND ecm:isProxy = 1"
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/Deliverable" + "'"
                + " AND ecm:parentId NOT IN " + getProxiedIdLiteralList(session, deliverableProxyInASoftwareComponent)));
        nbMap.put("deliverable in no software component' implementations", listMap.get("deliverable in no software component' implementations").size());
        DocumentModelList implementationProxyInADeliverable = session.query(NXQL_SELECT_FROM + "ServiceImplementation"
                + " WHERE " + "ecm:currentLifeCycleState <> 'deleted' " + "AND ecm:isProxy = 1"
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/Deliverable" + "'");
        listMap.put("implementation in no deliverable", session.query(NXQL_SELECT_FROM + "ServiceImplementation" + NXQL_WHERE_NO_PROXY
                + " AND ecm:uuid NOT IN " + getProxiedIdLiteralList(session, implementationProxyInADeliverable)));
        nbMap.put("implementation in no deliverable", listMap.get("implementation in no deliverable").size());
        ArrayList<DocumentModel> implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent = new ArrayList<DocumentModel>(listMap.get("implementation in no deliverable"));
        implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent.addAll(listMap.get("deliverable in no software component' implementations"));
        nbMap.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent", implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent.size());
        DocumentModelList implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceProxy = session.query(NXQL_SELECT_FROM + "ServiceImplementation"
                + " WHERE " + "ecm:currentLifeCycleState <> 'deleted' " + "AND ecm:isProxy = 1"
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/Service" + "'"
                // + " AND ecm:proxyTargetId IN " + getProxiedIdLiteralList(session, implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent)); // TODO soaid for proxies
                + " AND ecm:uuid IN " + getIdLiteralList(getProxyIds(session, implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent, null))); // TODO soaid for proxies
        HashSet<String> implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet = new HashSet<String>(getParentIds(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceProxy));
        nbMap.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentService", implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet.size());
        HashSet<String> serviceInNoSoftwareComponentIdSet = new HashSet<String>(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet);
        serviceInNoSoftwareComponentIdSet.addAll(serviceWhithoutImplementationIdSet);
        nbMap.put("serviceInNoSoftwareComponent", serviceInNoSoftwareComponentIdSet.size());
        
        
        // Indicators in %

        HashMap<String, Integer> percentMap = new HashMap<String, Integer>();
        
        percentMap.put("serviceWhithoutImplementation", (nbMap.get("Service") == 0) ? -1 : 100 * serviceWhithoutImplementationNb / nbMap.get("Service"));
        percentMap.put("serviceWhithoutEndpoint", (nbMap.get("Service") == 0) ? -1 :100 * (serviceWhithoutImplementationNb + serviceWithImplementationWhithoutEndpointNb) / nbMap.get("Service"));
        percentMap.put("serviceWithImplementationWhithoutEndpoint", (nbMap.get("Service") - serviceWhithoutImplementationNb == 0) ? -1 :100 * serviceWithImplementationWhithoutEndpointNb / (nbMap.get("Service") - serviceWhithoutImplementationNb));
        
        percentMap.put("Service implementations documentation quality", (maxServiceImplsDocQuality == 0) ? -1 : (100 * serviceImplsDocQuality / maxServiceImplsDocQuality));
        percentMap.put("Service impls lacking mocks", (nonMockImplsCount > 0) ? (100 * (nonMockImplsCount - mockedImplsCount) / nonMockImplsCount) : -1);
        percentMap.put("Untested service impls", (nonMockImplsCount > 0) ? (100 * (nonMockImplsCount - testedImplsCount) / nonMockImplsCount) : -1);
        
        percentMap.put("deliverable in no software component", 100 * nbMap.get("deliverable in no software component") / nbMap.get("Deliverable"));
        percentMap.put("implementation in no deliverable", 100 * nbMap.get("implementation in no deliverable") / nbMap.get("ServiceImplementation"));
        percentMap.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent", 100 * nbMap.get("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent") / nbMap.get("ServiceImplementation"));
        percentMap.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentService", 100 * nbMap.get("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentService") / nbMap.get("Service"));
        percentMap.put("serviceInNoSoftwareComponent", 100 * nbMap.get("serviceInNoSoftwareComponent") / nbMap.get("Service"));

        // TODO model consistency ex. impl without service
        // TODO for one ex. impl of ONE service => prop to query
        
        return getView("indicators")
                .arg("nbMap", nbMap)
                .arg("percentMap", percentMap);
    }

    private int countFromQuery(CoreSession session, String query) throws ClientException {
		IterableQueryResult queryResult = session.queryAndFetch(query, NXQL.NXQL);
		return (int) queryResult.size();
	}

	private String getIdLiteralList(List<String> ids) throws ClientException {
        return "('" + ids.toString().replaceAll("[\\[\\]]", "").replaceAll(", ", "', '") + "')";
    }

    private String getProxiedIdLiteralList(CoreSession session, List<DocumentModel> proxies) throws ClientException {
        return getIdLiteralList(getProxiedIds(session, proxies));
    }

    private List<String> getProxiedIds(CoreSession session, List<DocumentModel> proxies) throws ClientException {
        ArrayList<String> proxiedIds = new ArrayList<String>();
        for (DocumentModel proxy : proxies) {
            proxiedIds.add(session.getWorkingCopy(proxy.getRef()).getId());
        }
        return proxiedIds;
    }

    private List<String> getProxyIds(CoreSession session, List<DocumentModel> docs, DocumentRef root) throws ClientException {
        ArrayList<String> proxyIds = new ArrayList<String>();
        for (DocumentModel doc : docs) {
            for (DocumentModel proxy : session.getProxies(doc.getRef(), root)) {
                proxyIds.add(proxy.getId());
            }
        }
        return proxyIds;
    }
    
    private List<String> getParentIds(List<DocumentModel> docs) throws ClientException {
        ArrayList<String> parentIds = new ArrayList<String>();
        for (DocumentModel doc : docs) {
            parentIds.add(doc.getParentRef().reference().toString());
        }
        return parentIds;
    }

    public static DocumentService getDocumentService() throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        if (documentService == null) {
            throw new WebException("Unable to get documentService");
        }
        return documentService;
    }

}
