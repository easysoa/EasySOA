package org.easysoa.registry.query.nuxeo.webengine;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

@WebObject(type = "Query")
@Produces("text/html;charset=UTF-8")
@Path("query")
public class Main extends ModuleRoot {

	@Path("services")
	public Object getServices() {
		return newObject("Services");
	}
}
