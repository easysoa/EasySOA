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

package org.easysoa.registry.documentation.rest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.easysoa.registry.indicators.rest.IndicatorProvider;
import org.easysoa.registry.indicators.rest.SoftwareComponentIndicatorProvider;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.DeployedDeliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.EndpointConsumption;
import org.easysoa.registry.types.Service;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.types.SoftwareComponent;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.types.adapters.SoaNodeAdapter;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.Template;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Indicators
 * 
 * @author mdutoo
 * 
 */
@WebObject(type = "EasySOA")
@Path("easysoa/services")
public class ServiceDocumentationController extends ModuleRoot {

    private static final String SERVICE_LIST_PROPS = "*"; // "ecm:title"
    
    private static Logger logger = Logger.getLogger(ServiceDocumentationController.class);
    
    public ServiceDocumentationController() {
        
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object doGetHTML() throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        
        DocumentModelList services = session.query("SELECT " + SERVICE_LIST_PROPS + " FROM " + Service.DOCTYPE + IndicatorProvider.NXQL_WHERE_NO_PROXY);
        DocumentModelList tags = session.query("SELECT " + "*" + " FROM " + TaggingFolder.DOCTYPE + IndicatorProvider.NXQL_WHERE_NO_PROXY);
        DocumentModelList serviceProxies = session.query("SELECT " + "*" + " FROM " + Service.DOCTYPE + IndicatorProvider.NXQL_WHERE_PROXY
                + IndicatorProvider.NXQL_AND + IndicatorProvider.NXQL_PATH_STARTSWITH + "/default-domain/repository/TaggingFolder" + "'");
        //DocumentModelList serviceProxyIds = session.query("SELECT " + "ecm:uuid, ecm:parentid" + " FROM " + Service.DOCTYPE + IndicatorProvider.NXQL_WHERE_PROXY
        //        + IndicatorProvider.NXQL_AND + IndicatorProvider.NXQL_PATH_STARTSWITH + "/default-domain/repository/TaggingFolder" + "'");
        
        // TODO id to group / aggregate, use... http://stackoverflow.com/questions/5023743/does-guava-have-an-equivalent-to-pythons-reduce-function
        // collection utils :
        // TreeMap(Comparator) http://docs.oracle.com/javase/6/docs/api/java/util/TreeMap.html#TreeMap%28java.util.Comparator%29
        // google collections Maps : uniqueIndex, transformValues (multimaps) http://code.google.com/p/guava-libraries/wiki/CollectionUtilitiesExplained
        // custom code in this spirit http://code.google.com/p/guava-libraries/issues/detail?id=546
        // (NB. guava itself brings nothing more)
        // or more functional stuff :
        // jedi http://jedi.codehaus.org/Examples
        // lambdaj : count, group (& filter, join) http://lambdaj.googlecode.com/svn/trunk/html/apidocs/ch/lambdaj/Lambda.html
        // (java)script in java (ex. Rhino)
        // or more SQL-like stuff ex. josql http://josql.sourceforge.net/
        // (or true olap)

        HashMap<String, HashSet<DocumentModel>> tagId2Services = new HashMap<String, HashSet<DocumentModel>>();
        for (DocumentModel serviceProxyDoc : serviceProxies) {
            String serviceProxyParentId = (String) serviceProxyDoc.getParentRef().reference();
            HashSet<DocumentModel> taggedServices = tagId2Services.get(serviceProxyParentId);
            if (taggedServices == null) {
                taggedServices = new HashSet<DocumentModel>();
                tagId2Services.put(serviceProxyParentId, taggedServices);
            }
            // unwrapping proxy
            taggedServices.add(session.getWorkingCopy(serviceProxyDoc.getRef())); // TODO or by looking in a services map ?
        }
        
        /*HashMap<String, Integer> tagId2ServiceNbs = new HashMap<String, Integer>();
        for (DocumentModel serviceProxyIdDoc : serviceProxyIds) {
            String serviceProxyParentId = (String) serviceProxyIdDoc.getParentRef().reference();
            Integer serviceProxyNb = tagId2ServiceNbs.get(serviceProxyParentId);
            if (serviceProxyNb == null) {
                serviceProxyNb = 0;
            } else {
                serviceProxyNb++;
            }
            tagId2ServiceNbs.put(serviceProxyParentId, serviceProxyNb);
        }*/
        DocumentModelList untaggedServices = session.query("SELECT " + SERVICE_LIST_PROPS + " FROM " + Service.DOCTYPE + IndicatorProvider.NXQL_WHERE_NO_PROXY
                + IndicatorProvider.NXQL_AND + "ecm:uuid in " + SoftwareComponentIndicatorProvider.getProxiedIdLiteralList(session, serviceProxies));
        
        return getView("services")
                .arg("services", services)
                .arg("tags", tags)
                .arg("tagId2Services", tagId2Services)
                //.arg("tagId2ServiceNbs", tagId2ServiceNbs)
                .arg("untaggedServices", untaggedServices);
        // services.get(0).getProperty(schemaName, name)
        // services.get(0).getProperty(xpath)
    }
    
    @GET
    @Path("path/{servicePath:.+}") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetByPathHTML(@PathParam("servicePath") String servicePath) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        
        DocumentModelList services = session.query(IndicatorProvider.NXQL_SELECT_FROM + Service.DOCTYPE
                + IndicatorProvider.NXQL_WHERE_NO_PROXY + IndicatorProvider.NXQL_AND
                + "ecm:path='/" + servicePath + "'");
        
        Template view = getView("servicedoc");
        if (!services.isEmpty()) {
            DocumentModel service = services.get(0);
            service = session.getWorkingCopy(service.getRef()); // unwrapping
            List<DocumentModel> actualImpls = session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_NO_PROXY
                    + IndicatorProvider.NXQL_AND + "ecm:uuid in "
                    + SoftwareComponentIndicatorProvider.getProxiedIdLiteralList(session,
                            session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_PROXY + IndicatorProvider.NXQL_AND
                    + IndicatorProvider.NXQL_PATH_STARTSWITH + "/default-domain/repository/Service" + "'"
                    + IndicatorProvider.NXQL_AND + "ecm:parentId='" + service.getId() + "'"
                    + IndicatorProvider.NXQL_AND + ServiceImplementation.XPATH_ISMOCK + " IS NULL"))); // WARNING use IS NULL instead of !='true'
            List<DocumentModel> mockImpls = session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_NO_PROXY + IndicatorProvider.NXQL_AND + "ecm:uuid in "
                    + SoftwareComponentIndicatorProvider.getProxiedIdLiteralList(session,
                            session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_PROXY + IndicatorProvider.NXQL_AND
                    + IndicatorProvider.NXQL_PATH_STARTSWITH + "/default-domain/repository/Service" + "'"
                    + IndicatorProvider.NXQL_AND + "ecm:parentId='" + service.getId() + "'"
                    + IndicatorProvider.NXQL_AND + ServiceImplementation.XPATH_ISMOCK + "='true'")));
            view = view
                    .arg("service", service)
                    .arg("actualImpls", actualImpls)
                    .arg("mockImpls", mockImpls)
                    .arg("servicee", service.getAdapter(SoaNodeAdapter.class));
        }
        return view; 
    }

    @GET
    @Path("tag/{tagPath:.+}") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetByTagHTML(@PathParam("tagPath") String tagPath) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
 
        DocumentModel tag = session.getWorkingCopy(new PathRef("/" + tagPath)); // unwrapping
        
        DocumentModelList tagServices = session.query(IndicatorProvider.NXQL_SELECT_FROM + Service.DOCTYPE
                + IndicatorProvider.NXQL_WHERE_NO_PROXY + IndicatorProvider.NXQL_AND + "ecm:uuid in "
                + SoftwareComponentIndicatorProvider.getProxiedIdLiteralList(session,
                        session.query(IndicatorProvider.NXQL_SELECT_FROM + Service.DOCTYPE
                /*+ IndicatorProvider.NXQL_WHERE_PROXY + " AND "*/ + IndicatorProvider.NXQL_WHERE
                + IndicatorProvider.NXQL_PATH_STARTSWITH + tag.getPathAsString() + IndicatorProvider.NXQL_QUOTE)));
        
        Template view = getView("tagServices");
        /*if (!services.isEmpty()) {
            DocumentModel service = services.get(0);
            List<DocumentModel> actualImpls = session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_NO_PROXY + IndicatorProvider.NXQL_AND + "ecm:uuid in "
                    + SoftwareComponentIndicatorProvider.getProxiedIdLiteralList(session,
                            session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_PROXY + IndicatorProvider.NXQL_AND
                    + IndicatorProvider.NXQL_PATH_STARTSWITH + "/default-domain/repository/Service" + "'"
                    + IndicatorProvider.NXQL_AND + "ecm:parentId='" + service.getId() + "'"
                    + IndicatorProvider.NXQL_AND + ServiceImplementation.XPATH_ISMOCK + " IS NULL"))); // WARNING use IS NULL instead of !='true'
            List<DocumentModel> mockImpls = session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_NO_PROXY + IndicatorProvider.NXQL_AND + "ecm:uuid in "
                    + SoftwareComponentIndicatorProvider.getProxiedIdLiteralList(session,
                            session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_PROXY + IndicatorProvider.NXQL_AND
                    + IndicatorProvider.NXQL_PATH_STARTSWITH + "/default-domain/repository/Service" + "'"
                    + IndicatorProvider.NXQL_AND + "ecm:parentId='" + service.getId() + "'"
                    + IndicatorProvider.NXQL_AND + ServiceImplementation.XPATH_ISMOCK + "='true'")));
            view = view
                    .arg("service", service)
                    .arg("actualImpls", actualImpls)
                    .arg("mockImpls", mockImpls)
                    .arg("servicee", service.getAdapter(SoaNodeAdapter.class));
        }*/
        return view
                .arg("tag", tag) 
                .arg("tagServices", tagServices); 
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object doGetJSON() throws Exception {
        return null; // TODO
    }
    
}
