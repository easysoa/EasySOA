package org.easysoa.records.replay;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.ExchangeRecordStoreFactory;
import org.osoa.sca.annotations.Scope;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.util.RequestForwarder;

/**
 * This service allows a user (ex. through a web UI) to choose, load, replay 
 * an exchange and check the response. 
 * 
 * If the user wants to change the entry parameters, he should rather
 * - change them in the client business application and record another exchange
 * - or make some templates out of the exchange
 * 
 * The recorded response is used to provide an idea of how much the actual response is OK :
 * - by doing a diff (on server or client) and displaying it
 * - LATER by running asserts gotten from correlator
 * 
 * @author jguillemotte
 *
 */
// TODO REST JAXRS service, web UI

/**
 * ExchangeReplayService implementation
 * @author jguillemotte
 *
 */
@Scope("composite")
public class ExchangeReplayServiceImpl implements ExchangeReplayService {
	
	// Logger
	private static Logger logger = Logger.getLogger(ExchangeReplayServiceImpl.class.getName());	
	
	// Running environment
	private String environment;

	@Override
	@GET
	@Path("/list")
	@Produces("application/json")
	public List<ExchangeRecord> list() {
		logger.debug("list method called ...");
    	ExchangeRecordStore erfs;
    	List<ExchangeRecord> recordList;
		try {
			erfs = ExchangeRecordStoreFactory.createExchangeRecordStore();
			recordList = erfs.list();
			logger.debug("recordeList size = " + recordList.size());
		} catch (Exception ex) {
			logger.error("An error occurs during the listing of exchanges records", ex);
			recordList = new ArrayList<ExchangeRecord>();
		}
		return recordList;
	}

	@Override
	@Produces("application/json")
	public ExchangeRecord[] list(@PathParam("service") String service) {
		// LATER
		// TODO request to get all the persisted exchange records
		// and next apply a filter to select only those with the corresponding service
		return null;
	}

	@Override
	@Produces("application/json")
	public String replay(@PathParam("exchangeRecordId") String exchangeRecordId) {
		// call remote service using chosen record :
		// see how to share monit.forward(Message) code (extract it in a Util class), see also scaffolder client
		
		// NB. without correlated asserts, test on response are impossible,
		// however diff is possible (on server or client)
		// ex. on server : http://code.google.com/p/java-diff-utils/

		String response;
    	ExchangeRecordStore erfs;
		try {
			erfs = ExchangeRecordStoreFactory.createExchangeRecordStore();
			// get the record
			ExchangeRecord record = erfs.load(exchangeRecordId);
			// Send the request
			RequestForwarder requestForwarder = new RequestForwarder();
			OutMessage outMessage = requestForwarder.send(record.getInMessage());

			logger.debug("Response of orignal exchange : " + record.getOutMessage().getMessageContent().getContent());
			logger.debug("Response of replayed exchange : " + outMessage.getMessageContent().getContent());
			// TODO : compare the returned response with the one contained in the stored Exchange record with an assert template
			response = outMessage.getMessageContent().getContent();
		}
		catch(Exception ex){
			response = "A problem occurs during the replay, see logs for more informations !";
			ex.printStackTrace();
			logger.error("A problem occurs duringt the replay of exchange record  with id " + exchangeRecordId);
		}
		return response; // JSON
	}
	
	@Override
	@Produces("application/json")	
	public void cloneToEnvironment(@PathParam("anotherEnvironment") String anotherEnvironment) {
		// LATER
		// requires to extract service in request & response
	}
	
}
