package org.easysoa.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.nuxeo.ecm.webengine.model.view.TemplateView;

/**
 * Web root (online documentation)
 * 
 * @author mkalam-alami
 * 
 */
@Path("easysoa")
@Produces(MediaType.TEXT_HTML)
public class EasySOAAppRoot {

	@GET
	public Object doGet() {
		return new TemplateView(this, "index.ftl");
	}

}
