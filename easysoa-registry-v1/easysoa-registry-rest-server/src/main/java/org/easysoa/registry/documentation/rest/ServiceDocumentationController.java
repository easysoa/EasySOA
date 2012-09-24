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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.Template;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

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
        
        return getView("services")
                .arg("services", services);
        // services.get(0).getProperty(schemaName, name)
        // services.get(0).getProperty(xpath)
    }
    
    @GET
    @Path("{ecmPath:.+}") // TODO encoding
    @Produces(MediaType.TEXT_HTML)
    public Object doGetHTML(@PathParam("ecmPath") String ecmPath) throws Exception {
        CoreSession session = SessionFactory.getSession(request);
        
        DocumentModelList services = session.query(IndicatorProvider.NXQL_SELECT_FROM + Service.DOCTYPE
                /*+ IndicatorProvider.NXQL_WHERE_NO_PROXY + " AND "*/ + IndicatorProvider.NXQL_WHERE
                + "ecm:path='/" + ecmPath + "'");
        
        Template view = getView("servicedoc");
        if (!services.isEmpty()) {
            DocumentModel service = services.get(0);
            List<DocumentModel> actualImpls = session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_NO_PROXY
                    + IndicatorProvider.NXQL_AND + "ecm:uuid in "
                    + SoftwareComponentIndicatorProvider.getProxiedIdLiteralList(session,
                            session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_PROXY + IndicatorProvider.NXQL_AND
                    + "ecm:path STARTSWITH '" + "/default-domain/repository/Service" + "'"
                    + IndicatorProvider.NXQL_AND + "ecm:parentId='" + service.getId() + "'"
                    + IndicatorProvider.NXQL_AND + ServiceImplementation.XPATH_ISMOCK + " IS NULL"))); // WARNING use IS NULL instead of !='true'
            List<DocumentModel> mockImpls = session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_NO_PROXY + IndicatorProvider.NXQL_AND + "ecm:uuid in "
                    + SoftwareComponentIndicatorProvider.getProxiedIdLiteralList(session,
                            session.query(IndicatorProvider.NXQL_SELECT_FROM + ServiceImplementation.DOCTYPE
                    + IndicatorProvider.NXQL_WHERE_PROXY + IndicatorProvider.NXQL_AND
                    + "ecm:path STARTSWITH '" + "/default-domain/repository/Service" + "'"
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
    @Produces(MediaType.APPLICATION_JSON)
    public Object doGetJSON() throws Exception {
        return null; // TODO
    }
    
}
