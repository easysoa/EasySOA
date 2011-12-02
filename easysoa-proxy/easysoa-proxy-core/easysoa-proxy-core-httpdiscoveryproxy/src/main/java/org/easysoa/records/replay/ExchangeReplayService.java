package org.easysoa.records.replay;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
//import javax.ws.rs.Produces;

import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Remotable;

@Remotable
public interface ExchangeReplayService {

	@GET
	@Path("/list")
	//@Produces("application/json,application/xml")	
	public List<ExchangeRecord> list();

	@GET
	@Path("/list/{service}")
	public ExchangeRecord[] list(String service);
	
	@GET
	@Path("/replay/{exchangeRecordId}")
	public String replay(String exchangeRecordId);
	
	@POST
	@Path("/cloneToEnvironment/{anotherEnvironment}")
	public void cloneToEnvironment(String anotherEnvironment);
	
}
