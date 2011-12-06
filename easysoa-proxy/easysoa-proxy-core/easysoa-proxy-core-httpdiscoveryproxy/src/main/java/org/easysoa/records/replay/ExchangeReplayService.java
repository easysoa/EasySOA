package org.easysoa.records.replay;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
//import javax.ws.rs.Produces;

import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Remotable;

@Remotable
public interface ExchangeReplayService {

	@GET
	@Path("/list")
	@Produces("application/json,application/xml")	
	public List<ExchangeRecord> list();

	@GET
	@Path("/list/{service}")
	@Produces("application/json,application/xml")	
	public ExchangeRecord[] list(@PathParam("service") String service);
	
	@GET
	@Path("/replay/{exchangeRecordId}")
	//@Produces("application/json,application/xml")	
	public String replay(@PathParam("exchangeRecordId") String exchangeRecordId);
	
	@POST
	@Path("/cloneToEnvironment/{anotherEnvironment}")
	@Produces("application/json,application/xml")	
	public void cloneToEnvironment(@PathParam("anotherEnvironment") String anotherEnvironment);
	
}
