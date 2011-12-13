package org.easysoa.records.replay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.RecordCollection;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.ExchangeRecordStoreManager;
import org.easysoa.records.ExchangeRecordStoreFactory;
import org.easysoa.records.StoreCollection;
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
 * 
 * REST HOWTO
 * 
 * How to return collections in jaxrs / jaxb / cxf (without going to Spring conf) : create a strong-typed collection class ex. ExchangeRecords
 * http://dhruba.name/2008/12/08/rest-service-example-using-cxf-22-jax-rs-10-jaxb-and-spring/#comment-781
 * NB. resteasy provides additional non-standard annotations for that : @Wrapped http://stackoverflow.com/questions/6192389/root-element-name-in-collections-returned-by-resteasy
 * 
 * How to handle a reference to another object not as an object but as its id : XmlJavaTypeAdapter
 * http://docs.oracle.com/javase/6/docs/api/javax/xml/bind/annotation/adapters/XmlJavaTypeAdapter.html
 * 
 * @author jguillemotte
 *
 */
// TODO REST JAXRS service, web UI
@Scope("composite")
public class ExchangeReplayServiceImpl implements ExchangeReplayService {
	
	// Logger
	private static Logger logger = Logger.getLogger(ExchangeReplayServiceImpl.class.getName());	
	
	// Running environment
	private String environment;

	@Override
	@GET
	@Path("/getExchangeRecordlist/{storeName}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//public List<ExchangeRecord> getExchangeRecordlist(@PathParam("storeName") String exchangeRecordStoreName) {
	public RecordCollection getExchangeRecordlist(@PathParam("storeName") String exchangeRecordStoreName) {
		logger.debug("getExchangeRecordlist method called for store : " + exchangeRecordStoreName);
    	ExchangeRecordStoreManager erfs;
    	List<ExchangeRecord> recordList;
		try {
			erfs = ExchangeRecordStoreFactory.createExchangeRecordStore();
			recordList = erfs.getExchangeRecordlist(exchangeRecordStoreName);
		} catch (Exception ex) {
			logger.error("An error occurs during the listing of exchanges records", ex);
			recordList = new ArrayList<ExchangeRecord>();
		}
		logger.debug("recordedList size = " + recordList.size());
		return new RecordCollection(recordList);
	}

	@Override
	@GET
	@Path("/getExchangeRecordlist/{storeName}/{exchangeID}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//public List<ExchangeRecord> getExchangeRecordlist(@PathParam("storeName") String exchangeRecordStoreName) {
	public RecordCollection getExchangeRecord(@PathParam("storeName") String exchangeRecordStoreName, @PathParam("exchangeID") String exchnageID) {
		
	}
	
	
	@Override
	@GET
	@Path("/getExchangeRecordStorelist")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//public List<ExchangeRecordStore> getExchangeRecordStorelist() {
	public StoreCollection getExchangeRecordStorelist() {
		logger.debug("getExchangeRecordStorelist method called ...");
    	ExchangeRecordStoreManager erfs;
    	List<ExchangeRecordStore> storeList;
    	try{
    		erfs = ExchangeRecordStoreFactory.createExchangeRecordStore();
    		storeList = erfs.getExchangeRecordStorelist();
    	}
    	catch(Exception ex){
			logger.error("An error occurs during the listing of exchanges record stores", ex);
			storeList = new ArrayList<ExchangeRecordStore>();    		
    	}
    	return new StoreCollection(storeList);
	}	
	
	@Override
	@Path("/replay")	
	@Produces("application/json")
	public String replay(@PathParam("exchangeRecordStoreName") String exchangeRecordStoreName, @PathParam("exchangeRecordId") String exchangeRecordId) {
		// call remote service using chosen record :
		// see how to share monit.forward(Message) code (extract it in a Util class), see also scaffolder client
		// NB. without correlated asserts, test on response are impossible,
		// however diff is possible (on server or client)
		// ex. on server : http://code.google.com/p/java-diff-utils/
		logger.debug("Replaying store : " + exchangeRecordStoreName + ", specific id : " + exchangeRecordId);
		
    	ExchangeRecordStoreManager erfs;
		StringBuffer responseBuffer = new StringBuffer();    	
		try {
			//List<ExchangeRecord> recordList;
			Collection<ExchangeRecord> recordList;
			erfs = ExchangeRecordStoreFactory.createExchangeRecordStore();
			// get the record
			if(exchangeRecordId==null || "".equals(exchangeRecordId)){
				recordList = getExchangeRecordlist(exchangeRecordStoreName).getRecords();
			} else {
				recordList = new ArrayList<ExchangeRecord>();
				recordList.add(erfs.load(exchangeRecordStoreName, exchangeRecordId));			
			}
			RequestForwarder requestForwarder;
			OutMessage outMessage;
			logger.debug("records number to replay : " + recordList.size());
			for(ExchangeRecord record : recordList){
			// Send the request
				requestForwarder = new RequestForwarder();
				outMessage = requestForwarder.send(record.getInMessage());
	
				logger.debug("Response of original exchange : " + record.getOutMessage().getMessageContent().getContent());
				logger.debug("Response of replayed exchange : " + outMessage.getMessageContent().getContent());
				
				// TODO : compare the returned response with the one contained in the stored Exchange record with an assert template
				// TODO : change the response to return JSON structure
				responseBuffer.append("Replay result for Exchange Record " + record.getExchangeID() + " => ");
				responseBuffer.append(outMessage.getMessageContent().getContent());
				responseBuffer.append("<br/>");
			}
		}
		catch(Exception ex){
			responseBuffer.append("A problem occurs during the replay, see logs for more informations !");
			ex.printStackTrace();
			logger.error("A problem occurs duringt the replay of exchange record  with id " + exchangeRecordId);
		}
		logger.debug("Response : " + responseBuffer.toString());
		return responseBuffer.toString(); // JSON
	}
	
	@Override
	@Produces("application/json")	
	public void cloneToEnvironment(@PathParam("anotherEnvironment") String anotherEnvironment) {
		// LATER
		// requires to extract service in request & response
	}
	
}
