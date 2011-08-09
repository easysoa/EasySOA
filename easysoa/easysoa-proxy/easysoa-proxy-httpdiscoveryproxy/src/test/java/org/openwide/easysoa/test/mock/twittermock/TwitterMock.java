package org.openwide.easysoa.test.mock.twittermock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.osoa.sca.annotations.Remotable;

/**
 * A Twitter REST API mock
 * 
 * @author jguillemotte
 *
 */
@Remotable
public interface TwitterMock {

	@GET
	@Path("/1/users/show/{user}")
	public String returnUsersShow(@PathParam("user") String user);	
	
	@GET
	@Path("/1/statuses/friends/{user}")
	public String returnStatusesFriends(@PathParam("user") String user);
	
	@GET
	@Path("/1/statuses/followers/{user}")
	public String returnStatusesFollowers(@PathParam("user") String user);

}
