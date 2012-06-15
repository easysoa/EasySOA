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
package org.easysoa.records.replay;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.RecordCollection;
import org.easysoa.records.StoreCollection;
import org.easysoa.template.TemplateFieldSuggestions;
import org.osoa.sca.annotations.Remotable;

@Remotable
public interface ExchangeReplayService {
	
    // TODO : add a method to execute or not the assertion engine when a replay method is called
    
    /**
     * Returns a help text with the available commands
     * @param ui Context UriInfo
     * @return The <code>String</code> help text
     */
    // TODO in case of error, return the user informations
    @GET
    @Path("/")
    //@Consumes("*/*")
    //@Produces({MediaType.TEXT_PLAIN})
    public String returnInformations();
    
	@GET
	@Path("/getExchangeRecordList/{storeName}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public RecordCollection getExchangeRecordlist(@PathParam("storeName") String exchangeRecordStoreName) throws Exception;

	@GET
	@Path("/getExchangeRecord/{storeName}/{exchangeID}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ExchangeRecord getExchangeRecord(@PathParam("storeName") String exchangeRecordStoreName, @PathParam("exchangeID") String exchangeID) throws Exception;
	
	@GET
	@Path("/getExchangeRecordStorelist")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})	
	public StoreCollection getExchangeRecordStorelist() throws Exception;
	
	/**
	 * To replay an exchange record directly without any modifications
	 * @param exchangeRecordStoreName The store where the record is stored
	 * @param exchangeRecordId The ID of the record to replay
	 * @return The response from the replayed exchange request as a JSON string
	 * @throws Exception 
	 */
	@GET
	@Path("/replay/{exchangeRecordStoreName}/{exchangeRecordId}")
	@Produces("application/json")
	public String replay(@PathParam("exchangeRecordStoreName") String exchangeRecordStoreName, @PathParam("exchangeRecordId") String exchangeRecordId) throws Exception;
	
	@POST
	@Path("/cloneToEnvironment/{anotherEnvironment}")
	@Produces("application/json")
	public void cloneToEnvironment(@PathParam("anotherEnvironment") String anotherEnvironment);
	
	@GET
	@Path("/templates/")
	public String getTemplateRecordList();
	
	@GET
	@Path("/templates/getTemplate/{storeName}/{templateName}")
	@Produces("application/json")
	public TemplateFieldSuggestions getTemplateFieldSuggestions(@PathParam("storeName") String storeName,  @PathParam("recordID") String recordID) throws Exception;

	@POST
	@Path("/startReplaySession/{replaySessionName}")
    public String startReplaySession(String replaySessionName) throws Exception;

	@POST
	@Path("/stopReplaySession/")
	public String stopReplaySession() throws Exception;
	
	/*
	@POST
	@Path("/templates/replayWithTemplate/{exchangeStoreName}/{exchangeRecordID}/{templateName}")
	@Produces("application/json")
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	//@Consumes("multipart/form-data")
	@Consumes("application/x-www-form-urlencoded")
	public String replayWithTemplate(MultivaluedMap<String, String> formData, @PathParam("exchangeStoreName") String exchangeStoreName, @PathParam("exchangeRecordID") String exchangeRecordId, @PathParam("templateName") String templateName) throws Exception;	
	*/
}
