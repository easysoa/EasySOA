package org.easysoa.records.replay;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
//import javax.ws.rs.Produces;

import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.osoa.sca.annotations.Remotable;

@Remotable
public interface ExchangeReplayService {

	/*@GET
	@Path("/list")
	@Produces("application/json")
	public List<ExchangeRecord> list();*/
	
	@GET
	@Path("/getExchangeRecordlist")
	@Produces("application/json")
	public List<ExchangeRecord> getExchangeRecordlist(@PathParam("storeName") String exchangeRecordStoreName);

	@GET
	@Path("/getExchangeRecordStorelist")
	@Produces("application/json")	
	public List<ExchangeRecordStore> getExchangeRecordStorelist();
	
	/*@GET
	@Path("/list/{service}")
	@Produces("application/json")	
	public ExchangeRecord[] list(@PathParam("service") String service);*/
	
	/*@GET
	@Path("/list")
	@Produces("application/json")
	public List<ExchangeRecordStore> list();*/
	
	@GET
	//@Path("/replay/{exchangeRecordId}")
	@Path("/replay")
	@Produces("application/json")	
	public String replay(@PathParam("exchangeRecordStoreName") String exchangeRecordStoreName, @PathParam("exchangeRecordId") String exchangeRecordId);
	
	@POST
	@Path("/cloneToEnvironment/{anotherEnvironment}")
	@Produces("application/json")	
	public void cloneToEnvironment(@PathParam("anotherEnvironment") String anotherEnvironment);
	
}
