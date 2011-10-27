package org.easysoa.api;

import javax.security.auth.login.LoginException;
/*
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
*/
import org.json.JSONException;

//import com.sun.jersey.api.core.HttpContext;

/**
 * 
 * @author jguillemotte
 *
 */
public interface INotificationRest {

	/*
	@POST
	@Path("/appliimpl")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Object doPostAppliImpl(@Context HttpContext httpContext, @Context HttpServletRequest request) throws JSONException;

	/**
	 * Appli Impl. Documentation
	 * @return
	 * @throws JSONException
	 */
	/*@GET
	@Path("/appliimpl")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Object doGetAppliImpl() throws JSONException;*/

	/*@POST
	@Path("/api")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Object doPostApi(@Context HttpContext httpContext, @Context HttpServletRequest request) throws JSONException;*/

	/**
	 * API Documentation
	 * @return
	 * @throws JSONException
	 */
	/*@GET
	@Path("/api")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Object doGetApi() throws JSONException;*/

	/*@POST
	@Path("/service")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Object doPostService(@Context HttpContext httpContext, @Context HttpServletRequest request) throws JSONException;
	*/

	/**
	 * Service Documentation
	 * @return
	 * @throws JSONException
	 */
	/*@GET
	@Path("/service")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Object doGetService() throws JSONException;

	@POST
	@Path("/{all:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Object doPostInvalid() throws JSONException, LoginException;	
	*/
	
}
