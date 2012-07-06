package org.easysoa.registry.query.nuxeo.webengine;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.nuxeo.ecm.core.rest.DocumentObject;
import org.nuxeo.ecm.core.search.api.client.querymodel.descriptor.QueryModelDescriptor;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;

@WebObject(type = "Services")
@Produces("text/html;charset=UTF-8")
public class Services extends DefaultObject {
	private static String DEFAULT_ROOT_PATH = "/default-domain";

	@GET
	public Object getAllServices() {
		return findAllServicesInRoot(DEFAULT_ROOT_PATH);
	}

	@GET
	@Path("{rootPath}")
	public Object findAllServicesInRoot(@PathParam("rootPath") String rootPath) {
		QueryModelDescriptor qDesc = new QueryModelDescriptor();
		qDesc.setPattern("SELECT * FROM Service WHERE (ecm:isCheckedInVersion = 0) AND (ecm:path STARTSWITH ?)");
		// TODO

		// return getView("search").arg(
		// "services",
		// findAllServices(DocumentFactory.newDocumentRoot(getContext(),
		// rootPath)));

		return null;
	}

	private Object findAllServices(DocumentObject root) {
		// TODO
		return null;
	}
}
