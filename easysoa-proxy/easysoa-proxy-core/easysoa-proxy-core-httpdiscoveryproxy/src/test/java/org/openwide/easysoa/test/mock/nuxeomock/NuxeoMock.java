/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
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

package org.openwide.easysoa.test.mock.nuxeomock;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

public interface NuxeoMock {

	/**
	 * Registration service
	 * @return
	 */
	@POST
	@Path("/nuxeo/site/easysoa/notification/{type}")
	public String processNotificationRequest(@PathParam("type") String type);
	
	/**
	 * Automation service
	 * @return
	 */
	@POST
	@Path("/nuxeo/site/automation/Document.Query")
	public String processAutomationRequest(@Context UriInfo ui, @Context Request request, String body);
	
}
