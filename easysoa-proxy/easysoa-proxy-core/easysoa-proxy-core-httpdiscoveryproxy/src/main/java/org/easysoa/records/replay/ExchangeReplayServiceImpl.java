package org.easysoa.records.replay;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.ExchangeRecordStoreFactory;
import org.osoa.sca.annotations.Scope;

import com.openwide.easysoa.monitoring.Message;
import com.openwide.easysoa.proxy.HttpResponseHandler;
import com.openwide.easysoa.proxy.HttpRetryHandler;
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
	
	private String environment;

	@Override
	@GET
	@Path("/list")
	@Produces("application/json,application/xml") 
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
	public ExchangeRecord[] list(@PathParam("service") String service) {
		// LATER
		return null;
	}

	@Override	
	public String replay(@PathParam("exchangeRecordId") String exchangeRecordId) {
		// call remote service using chosen record :
		// see how to share monit.forward(Message) code (extract it in a Util class), see also scaffolder client
		
		// NB. without correlated asserts, test on response are impossible,
		// however diff is possible (on server or client)
		// ex. on server : http://code.google.com/p/java-diff-utils/
		
		// Get the corresponding recorded exchange.
		// Get the inMessage
		// make a new request with inMessage datas
		// Send the request
		// Get the response and compare it to the outMessage. 

    	ExchangeRecordStore erfs;
		try {
			erfs = ExchangeRecordStoreFactory.createExchangeRecordStore();
			// get the record
			ExchangeRecord record = erfs.load(exchangeRecordId);
			// Send the request
			RequestForwarder requestForwarder = new RequestForwarder();
			requestForwarder.send(record);
			
			// compare the returned response with the one contained in the stored Exchange record
			
			
			
			
			////// Send message
			/*
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.setHttpRequestRetryHandler(new HttpRetryHandler());
			// set the connection timeout
			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, this.forwardHttpConnexionTimeoutMs);
			HttpConnectionParams.setSoTimeout(httpParams, this.forwardHttpSocketTimeoutMs);
			
			// URL
			StringBuffer requestUrlBuffer = new StringBuffer();
			requestUrlBuffer.append(request.getRequestURL().toString());
		    if(request.getQueryString() != null){
		    	requestUrlBuffer.append("?");
		    	requestUrlBuffer.append(request.getQueryString());
		    }
		    String requestUrlString = requestUrlBuffer.toString();
	    	HttpEntity httpEntity = new StringEntity(message.getBody());
			HttpUriRequest httpUriRequest;
			// TODO later use a pattern to create them (builder found in a map method -> builder...)
			if("GET".equalsIgnoreCase(request.getMethod())){
		    	httpUriRequest = new HttpGet(requestUrlString);
		    } else if("PUT".equalsIgnoreCase(request.getMethod())){
		    	HttpPut httpPut = new HttpPut(requestUrlString);
		    	httpPut.setEntity(httpEntity);
		    	httpUriRequest = httpPut;
		    } else if("DELETE".equalsIgnoreCase(request.getMethod())){
		    	httpUriRequest = new HttpDelete(requestUrlString);
		    } else if("OPTIONS".equalsIgnoreCase(request.getMethod())){
		    	httpUriRequest = new HttpOptions(requestUrlString);
		    } else if("HEAD".equalsIgnoreCase(request.getMethod())){
		    	httpUriRequest = new HttpOptions(requestUrlString);
		    } else if("TRACE".equalsIgnoreCase(request.getMethod())){
		    	httpUriRequest = new HttpOptions(requestUrlString);
		    } else { // POST
		    	HttpPost httpPost = new HttpPost(requestUrlString);
		    	httpPost.setEntity(httpEntity);
		    	httpUriRequest = httpPost;
		    }
	    	setHeaders(request, httpUriRequest);
	    	ResponseHandler<String> responseHandler = new HttpResponseHandler();
	    	String clientResponse = httpClient.execute(httpUriRequest, responseHandler);
	    	logger.debug("clientResponse : " + clientResponse);
	    	*/
	    	///// Send message end
			
		}
		catch(Exception ex){
			logger.error("A problem occurs duringt the replay of exchange record  with id " + exchangeRecordId);
		}
		
		return null; // JSON
	}
	
	@Override	
	public void cloneToEnvironment(@PathParam("anotherEnvironment") String anotherEnvironment) {
		// LATER
		// requires to extract service in request & response
	}
	
}
