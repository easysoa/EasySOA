/**
 * 
 */
package org.easysoa.records.replay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.RecordCollection;
import org.easysoa.records.StoreCollection;
import org.easysoa.records.assertions.AssertionEngine;
import org.easysoa.records.assertions.AssertionEngineImpl;
import org.easysoa.records.assertions.StringAssertion;
import org.easysoa.records.persistence.filesystem.ProxyExchangeRecordFileStore;

import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.util.RequestForwarder;

/**
 * Contains only the replay code. Other functionalities as assertions engine or template engine are plugged in the replay engine
 * Replay service implementation must use replay engine methods.
 * 
 * @author jguillemotte
 *
 */
public class ReplayEngineImpl implements ReplayEngine {

    // put here methods that can be used specifically by other classes or to be referenced as a java component by Frascati 
    
    // Add code here to organize the work of template engine and assertion engine
    // Hum ... maybe not .. it is possible to generate the template with out to replay
    // On the contrary, it is not possible to execute assertion if the replay has not been replayed 
    
    // TODO : Add a reference on Assertion engine ???
    
    // Logger
    private static Logger logger = Logger.getLogger(ReplayEngineImpl.class.getName());
    
    @Override
    public RecordCollection getExchangeRecordlist(String exchangeRecordStoreName) throws Exception {
        logger.debug("getExchangeRecordlist method called for store : " + exchangeRecordStoreName);
        List<ExchangeRecord> recordList = new ArrayList<ExchangeRecord>();
        try {
            ProxyExchangeRecordFileStore erfs = new ProxyExchangeRecordFileStore();
            recordList = erfs.getExchangeRecordlist(exchangeRecordStoreName);
        } catch (Exception ex) {
            logger.error("An error occurs during the listing of exchanges records", ex);
            throw new Exception("An error occurs during the listing of exchanges records", ex);
        }
        logger.debug("recordedList size = " + recordList.size());
        return new RecordCollection(recordList);
    }    
    
    @Override
    public ExchangeRecord getExchangeRecord(String exchangeRecordStoreName, String exchangeID) throws Exception {
        logger.debug("getExchangeRecord method called for store : " + exchangeRecordStoreName + " and exchangeID : " + exchangeID);
        ExchangeRecord record = null;
        try {
            ProxyExchangeRecordFileStore erfs = new ProxyExchangeRecordFileStore();
            record = erfs.load(exchangeRecordStoreName, exchangeID);
        } 
        catch (Exception ex) {
            logger.error("An error occurs during the list", ex);
            throw new Exception("An error occurs during the loading of exchange record with id " + exchangeID, ex);
        }
        return record;
    }
    
    @Override
    public StoreCollection getExchangeRecordStorelist() throws Exception {
        logger.debug("getExchangeRecordStorelist method called ...");
        List<ExchangeRecordStore> storeList = new ArrayList<ExchangeRecordStore>();;
        try{
            ProxyExchangeRecordFileStore erfs = new ProxyExchangeRecordFileStore();
            storeList = erfs.getExchangeRecordStorelist();
        }
        catch(Exception ex){
            logger.error("An error occurs during the listing of exchanges record stores", ex);
            throw new Exception("An error occurs during the listing of exchanges record stores", ex); 
        }
        return new StoreCollection(storeList);
    }    
    
    @Override
    public Map<String,OutMessage> replay(String exchangeRecordStoreName, String exchangeRecordId) throws Exception{

        // call remote service using chosen record :
        // see how to share monit.forward(Message) code (extract it in a Util class), see also scaffolder client
        // NB. without correlated asserts, test on response are impossible,
        // however diff is possible (on server or client)
        // ex. on server : http://code.google.com/p/java-diff-utils/
        logger.debug("Replaying store : " + exchangeRecordStoreName + ", specific id : " + exchangeRecordId);
        HashMap<String, OutMessage> responseMap = new HashMap<String, OutMessage>();
        try {
            Collection<ExchangeRecord> recordList;
            ProxyExchangeRecordFileStore erfs = new ProxyExchangeRecordFileStore();
            // get the record
            // TODO : if the record is not found, get all the record from the store
            // So need to return a Map of response with record ID as key
            if(exchangeRecordId==null || "".equals(exchangeRecordId)){
                recordList = getExchangeRecordlist(exchangeRecordStoreName).getRecords();
            } else {
                recordList = new ArrayList<ExchangeRecord>();
                recordList.add(erfs.load(exchangeRecordStoreName, exchangeRecordId));
            }
            RequestForwarder requestForwarder;
            OutMessage outMessage = new OutMessage();
            logger.debug("records number to replay : " + recordList.size());
            for(ExchangeRecord record : recordList){
            // Send the request
                requestForwarder = new RequestForwarder();
                outMessage = requestForwarder.send(record.getInMessage());
                logger.debug("Response of original exchange : " + record.getOutMessage().getMessageContent().getContent());
                logger.debug("Response of replayed exchange : " + outMessage.getMessageContent().getContent());
                responseMap.put(record.getExchange().getExchangeID(), outMessage);
                
                // Assertion Engine
                // TODO : make the assertion engine call configurable
                // How to change the type of used assertion ?
                
                // Maybe the solution is to pass the pre-configured assertionEngine as parameter (implementing an 'engine' interface)
                // and then only have to call the 'execute' method...
                // With this solution, we can execute one or several engines in a predefined order
                // problem : execute method and parameters .... not the same params for the differents engines ...
                
                // How to work with fields in fld files
                // Properties by properties => need to specify a property (field in fld files) and to find the corresponding prop in the response ...
                StringAssertion assertion = new StringAssertion("assertion_" + record.getExchange().getExchangeID());
                // Problem with this method when response is big ... treatment is very very long ....
                // Think there is a bug with this method, more of 5 minutes for a long xml string
                //assertion.setMethod(StringAssertionMethod.DISTANCE_LEHVENSTEIN);
                AssertionEngine engine = new AssertionEngineImpl();
                engine.executeAssertion(assertion, record.getOutMessage(), outMessage);
                // End
            }
        }
        catch(Exception ex){
            logger.warn("A problem occurs duringt the replay of exchange record  with id " + exchangeRecordId, ex);
            throw new Exception("A problem occurs during the replay, see logs for more informations !", ex);
        }
        return responseMap;
    }
    
}
